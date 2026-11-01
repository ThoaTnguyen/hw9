package patterns.proxy;

import java.util.Objects;

/**
 * Metadata for one song. The streaming app does not play audio.
 */
public final class Song {
    private final Integer id;
    private final String title;
    private final String artist;
    private final String album;
    private final int duration;

    public Song(Integer id, String title, String artist, String album, int duration) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("song id must be positive");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (artist == null || artist.isBlank()) {
            throw new IllegalArgumentException("artist is required");
        }
        if (album == null || album.isBlank()) {
            throw new IllegalArgumentException("album is required");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Song other)) {
            return false;
        }
        return duration == other.duration
                && id.equals(other.id)
                && title.equals(other.title)
                && artist.equals(other.artist)
                && album.equals(other.album);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, album, duration);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " by " + artist + " | " + album + " | " + duration + "s";
    }
}
