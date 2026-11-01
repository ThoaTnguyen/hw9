package patterns.proxy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SongTest {
    @Test
    void storesMetadata() {
        Song song = new Song(1, "Blinding Lights", "The Weeknd", "After Hours", 200);

        assertEquals(1, song.getId());
        assertEquals("Blinding Lights", song.getTitle());
        assertEquals("The Weeknd", song.getArtist());
        assertEquals("After Hours", song.getAlbum());
        assertEquals(200, song.getDuration());
        assertEquals("[1] Blinding Lights by The Weeknd | After Hours | 200s", song.toString());
    }

    @Test
    void equalityUsesEveryField() {
        Song song = new Song(1, "Peaches", "Justin Bieber", "Justice", 198);
        Song same = new Song(1, "Peaches", "Justin Bieber", "Justice", 198);
        Song otherArtist = new Song(1, "Peaches", "Jack Black", "Justice", 198);

        assertEquals(song, same);
        assertEquals(song.hashCode(), same.hashCode());
        assertNotEquals(song, otherArtist);
    }

    @Test
    void rejectsIncompleteMetadata() {
        assertThrows(IllegalArgumentException.class,
                () -> new Song(0, "Peaches", "Justin Bieber", "Justice", 198));
        assertThrows(IllegalArgumentException.class,
                () -> new Song(1, " ", "Justin Bieber", "Justice", 198));
        assertThrows(IllegalArgumentException.class,
                () -> new Song(1, "Peaches", null, "Justice", 198));
        assertThrows(IllegalArgumentException.class,
                () -> new Song(1, "Peaches", "Justin Bieber", "", 198));
        assertThrows(IllegalArgumentException.class,
                () -> new Song(1, "Peaches", "Justin Bieber", "Justice", 0));
    }
}
