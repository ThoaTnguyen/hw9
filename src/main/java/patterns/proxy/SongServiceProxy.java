package patterns.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cache in front of a {@link SongService}.
 * The first lookup for an id, title, or album calls the real service.
 * Later lookups for that same key are answered from memory.
 * Songs returned by a title or album search are also stored by id.
 */
public final class SongServiceProxy implements SongService {
    private final SongService service;
    private final Map<Integer, Song> songsById = new HashMap<>();
    private final Set<Integer> missingIds = new HashSet<>();
    private final Map<String, List<Song>> songsByTitle = new HashMap<>();
    private final Map<String, List<Song>> songsByAlbum = new HashMap<>();
    private int cacheHits;
    private int cacheMisses;

    public SongServiceProxy(SongService service) {
        if (service == null) {
            throw new IllegalArgumentException("song service is required");
        }
        this.service = service;
    }

    public int getCacheHits() {
        return cacheHits;
    }

    public int getCacheMisses() {
        return cacheMisses;
    }

    @Override
    public Song searchById(Integer songID) {
        if (songID == null) {
            throw new IllegalArgumentException("song id is required");
        }
        if (songsById.containsKey(songID)) {
            cacheHits++;
            return songsById.get(songID);
        }
        if (missingIds.contains(songID)) {
            cacheHits++;
            return null;
        }
        cacheMisses++;
        Song song = service.searchById(songID);
        if (song == null) {
            missingIds.add(songID);
            return null;
        }
        songsById.put(song.getId(), song);
        return song;
    }

    @Override
    public List<Song> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        List<Song> cached = songsByTitle.get(title);
        if (cached != null) {
            cacheHits++;
            return List.copyOf(cached);
        }
        cacheMisses++;
        List<Song> found = copySongs(service.searchByTitle(title));
        songsByTitle.put(title, found);
        cacheSongsById(found);
        return List.copyOf(found);
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        if (album == null || album.isBlank()) {
            throw new IllegalArgumentException("album is required");
        }
        List<Song> cached = songsByAlbum.get(album);
        if (cached != null) {
            cacheHits++;
            return List.copyOf(cached);
        }
        cacheMisses++;
        List<Song> found = copySongs(service.searchByAlbum(album));
        songsByAlbum.put(album, found);
        cacheSongsById(found);
        return List.copyOf(found);
    }

    private void cacheSongsById(List<Song> found) {
        for (Song song : found) {
            songsById.put(song.getId(), song);
            missingIds.remove(song.getId());
        }
    }

    private static List<Song> copySongs(List<Song> found) {
        if (found == null) {
            throw new IllegalStateException("song service returned null");
        }
        return List.copyOf(found);
    }
}
