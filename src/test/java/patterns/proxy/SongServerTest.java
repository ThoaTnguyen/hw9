package patterns.proxy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SongServerTest {
    @Test
    void eachSearchWaitsAndReturnsMetadata() {
        SongServer server = new SongServer(ProxyDriver.sampleLibrary());

        long idStart = System.nanoTime();
        Song byId = server.searchById(1);
        assertTrue(millisSince(idStart) >= 900);
        assertEquals("Blinding Lights", byId.getTitle());
        assertEquals("The Weeknd", byId.getArtist());
        assertEquals("After Hours", byId.getAlbum());
        assertEquals(200, byId.getDuration());

        long titleStart = System.nanoTime();
        List<Song> byTitle = server.searchByTitle("Peaches");
        assertTrue(millisSince(titleStart) >= 900);
        assertEquals(List.of("Justin Bieber", "Jack Black"),
                byTitle.stream().map(Song::getArtist).toList());

        long albumStart = System.nanoTime();
        List<Song> byAlbum = server.searchByAlbum("After Hours");
        assertTrue(millisSince(albumStart) >= 900);
        assertEquals(List.of(1, 2), byAlbum.stream().map(Song::getId).toList());

        long missingStart = System.nanoTime();
        assertNull(server.searchById(99));
        assertTrue(millisSince(missingStart) >= 900);
    }

    @Test
    void rejectsABadLibrary() {
        assertThrows(IllegalArgumentException.class, () -> new SongServer(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SongServer(null));
        Song song = new Song(1, "Peaches", "Justin Bieber", "Justice", 198);
        assertThrows(IllegalArgumentException.class, () -> new SongServer(List.of(song, song)));
    }

    private static long millisSince(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000L;
    }
}
