package patterns.proxy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Real song service. Each search waits 1 second, imitating a slow server.
 * The proxy is what the application should call.
 */
public final class SongServer implements SongService {
    private final List<Song> songs;

    public SongServer(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            throw new IllegalArgumentException("song library is required");
        }
        Map<Integer, Song> indexed = new LinkedHashMap<>();
        for (Song song : songs) {
            if (song == null) {
                throw new IllegalArgumentException("song is required");
            }
            if (indexed.containsKey(song.getId())) {
                throw new IllegalArgumentException("duplicate song id: " + song.getId());
            }
            indexed.put(song.getId(), song);
        }
        this.songs = List.copyOf(indexed.values());
    }

    @Override
    public Song searchById(Integer songID) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        if (songID == null) {
            throw new IllegalArgumentException("song id is required");
        }
        for (Song song : songs) {
            if (song.getId().equals(songID)) {
                return song;
            }
        }
        return null;
    }

    @Override
    public List<Song> searchByTitle(String title) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        List<Song> matches = new ArrayList<>();
        for (Song song : songs) {
            if (song.getTitle().equals(title)) {
                matches.add(song);
            }
        }
        return matches;
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        if (album == null || album.isBlank()) {
            throw new IllegalArgumentException("album is required");
        }
        List<Song> matches = new ArrayList<>();
        for (Song song : songs) {
            if (song.getAlbum().equals(album)) {
                matches.add(song);
            }
        }
        return matches;
    }
}
