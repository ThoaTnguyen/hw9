package patterns.proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches a small song library through the proxy.
 * The first call for each query waits on the server. The repeat is served from the cache.
 */
public final class ProxyDriver {
    private ProxyDriver() {
    }

    public static void main(String[] args) {
        run().forEach(System.out::println);
    }

    public static List<Song> sampleLibrary() {
        return List.of(
                new Song(1, "Blinding Lights", "The Weeknd", "After Hours", 200),
                new Song(2, "Save Your Tears", "The Weeknd", "After Hours", 215),
                new Song(3, "Levitating", "Dua Lipa", "Future Nostalgia", 203),
                new Song(4, "Don't Start Now", "Dua Lipa", "Future Nostalgia", 183),
                new Song(5, "As It Was", "Harry Styles", "Harry's House", 167),
                new Song(6, "Watermelon Sugar", "Harry Styles", "Fine Line", 174),
                new Song(7, "Peaches", "Justin Bieber", "Justice", 198),
                new Song(8, "Peaches", "Jack Black", "The Super Mario Bros. Movie", 178)
        );
    }

    public static List<String> run() {
        List<Song> library = sampleLibrary();
        SongServiceProxy proxy = new SongServiceProxy(new SongServer(library));
        List<String> lines = new ArrayList<>();
        lines.add("Library (" + library.size() + " songs):");
        for (Song song : library) {
            lines.add("  " + song);
        }
        for (Lookup lookup : lookups(proxy)) {
            String source = lookup.cacheHit ? "cache" : "server";
            lines.add(lookup.label + " -> " + source + " " + lookup.elapsedMillis + " ms -> " + lookup.result);
        }
        lines.add("Cache hits: " + proxy.getCacheHits());
        lines.add("Server calls: " + proxy.getCacheMisses());
        return lines;
    }

    public static List<Lookup> lookups(SongServiceProxy proxy) {
        List<Lookup> lookups = new ArrayList<>();
        lookups.add(time(proxy, "searchById(1)", () -> formatSong(proxy.searchById(1))));
        lookups.add(time(proxy, "searchById(1) again", () -> formatSong(proxy.searchById(1))));
        lookups.add(time(proxy, "searchByTitle(\"Peaches\")", () -> formatSongs(proxy.searchByTitle("Peaches"))));
        lookups.add(time(proxy, "searchByTitle(\"Peaches\") again",
                () -> formatSongs(proxy.searchByTitle("Peaches"))));
        lookups.add(time(proxy, "searchByAlbum(\"After Hours\")",
                () -> formatSongs(proxy.searchByAlbum("After Hours"))));
        lookups.add(time(proxy, "searchByAlbum(\"After Hours\") again",
                () -> formatSongs(proxy.searchByAlbum("After Hours"))));
        lookups.add(time(proxy, "searchById(2) after album search", () -> formatSong(proxy.searchById(2))));
        lookups.add(time(proxy, "searchById(99)", () -> formatSong(proxy.searchById(99))));
        lookups.add(time(proxy, "searchById(99) again", () -> formatSong(proxy.searchById(99))));
        return lookups;
    }

    private static Lookup time(SongServiceProxy proxy, String label, Search search) {
        int hits = proxy.getCacheHits();
        int misses = proxy.getCacheMisses();
        long start = System.nanoTime();
        String result = search.run();
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;
        boolean cacheHit = proxy.getCacheHits() == hits + 1;
        boolean serverCall = proxy.getCacheMisses() == misses + 1;
        if (cacheHit == serverCall) {
            throw new IllegalStateException("search was not recorded for " + label);
        }
        return new Lookup(label, cacheHit, elapsedMillis, result);
    }

    private static String formatSong(Song song) {
        return song == null ? "not found" : song.toString();
    }

    private static String formatSongs(List<Song> songs) {
        if (songs.isEmpty()) {
            return "no matches";
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < songs.size(); i++) {
            if (i > 0) {
                text.append("; ");
            }
            text.append(songs.get(i));
        }
        return text.toString();
    }

    @FunctionalInterface
    private interface Search {
        String run();
    }

    public static final class Lookup {
        private final String label;
        private final boolean cacheHit;
        private final long elapsedMillis;
        private final String result;

        private Lookup(String label, boolean cacheHit, long elapsedMillis, String result) {
            this.label = label;
            this.cacheHit = cacheHit;
            this.elapsedMillis = elapsedMillis;
            this.result = result;
        }

        public String getLabel() {
            return label;
        }

        public boolean isCacheHit() {
            return cacheHit;
        }

        public long getElapsedMillis() {
            return elapsedMillis;
        }

        public String getResult() {
            return result;
        }
    }
}
