package patterns.proxy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SongServiceProxyTest {
    @Test
    void repeatedSearchesHitTheCache() {
        FakeServer server = new FakeServer(ProxyDriver.sampleLibrary());
        SongServiceProxy proxy = new SongServiceProxy(server);

        Song first = proxy.searchById(3);
        Song second = proxy.searchById(3);
        assertSame(first, second);
        assertEquals("Levitating", second.getTitle());
        assertEquals(1, server.idCalls);

        List<Song> peaches = proxy.searchByTitle("Peaches");
        List<Song> peachesAgain = proxy.searchByTitle("Peaches");
        assertEquals(2, peaches.size());
        assertEquals(peaches, peachesAgain);
        assertSame(peaches.get(0), peachesAgain.get(0));
        assertEquals(1, server.titleCalls);
        assertThrows(UnsupportedOperationException.class, () -> peaches.add(peaches.get(0)));

        List<Song> album = proxy.searchByAlbum("Future Nostalgia");
        assertEquals(List.of(3, 4), album.stream().map(Song::getId).toList());
        proxy.searchByAlbum("Future Nostalgia");
        assertEquals(1, server.albumCalls);

        assertSame(first, proxy.searchById(3));
        assertEquals(1, server.idCalls);
        assertSame(album.get(1), proxy.searchById(4));
        assertEquals(1, server.idCalls);

        assertNull(proxy.searchById(99));
        assertNull(proxy.searchById(99));
        assertEquals(2, server.idCalls);
        assertEquals(6, proxy.getCacheHits());
        assertEquals(4, proxy.getCacheMisses());
    }

    @Test
    void emptyResultsAreCached() {
        FakeServer server = new FakeServer(ProxyDriver.sampleLibrary());
        SongServiceProxy proxy = new SongServiceProxy(server);

        assertEquals(List.of(), proxy.searchByTitle("Nope"));
        assertEquals(List.of(), proxy.searchByAlbum("Nope"));
        assertEquals(List.of(), proxy.searchByTitle("Nope"));
        assertEquals(1, server.titleCalls);
        assertEquals(1, server.albumCalls);
    }

    @Test
    void rejectsBadArguments() {
        assertThrows(IllegalArgumentException.class, () -> new SongServiceProxy(null));
        SongServiceProxy proxy = new SongServiceProxy(new FakeServer(ProxyDriver.sampleLibrary()));
        assertThrows(IllegalArgumentException.class, () -> proxy.searchById(null));
        assertThrows(IllegalArgumentException.class, () -> proxy.searchByTitle(" "));
        assertThrows(IllegalArgumentException.class, () -> proxy.searchByAlbum(null));
    }

    private static final class FakeServer implements SongService {
        private final List<Song> songs;
        private int idCalls;
        private int titleCalls;
        private int albumCalls;

        private FakeServer(List<Song> songs) {
            this.songs = songs;
        }

        @Override
        public Song searchById(Integer songID) {
            idCalls++;
            for (Song song : songs) {
                if (song.getId().equals(songID)) {
                    return song;
                }
            }
            return null;
        }

        @Override
        public List<Song> searchByTitle(String title) {
            titleCalls++;
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
            albumCalls++;
            List<Song> matches = new ArrayList<>();
            for (Song song : songs) {
                if (song.getAlbum().equals(album)) {
                    matches.add(song);
                }
            }
            return matches;
        }
    }
}
