package patterns.proxy;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProxyDriverTest {
    @Test
    void driverShowsServerDelayThenCacheHits() {
        List<String> lines = ProxyDriver.run();

        assertEquals(8, ProxyDriver.sampleLibrary().size());
        assertTrue(lines.get(0).contains("Library (8 songs)"));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Blinding Lights")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("searchById(1) -> server")
                && line.contains("Blinding Lights")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("searchById(1) again -> cache")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("searchByTitle(\"Peaches\") -> server")
                && line.contains("Justin Bieber")
                && line.contains("Jack Black")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("searchByAlbum(\"After Hours\") again -> cache")
                && line.contains("Save Your Tears")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("searchById(2) after album search -> cache")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("searchById(99) -> server")
                && line.contains("not found")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("searchById(99) again -> cache")));
        assertTrue(lines.contains("Cache hits: 5"));
        assertTrue(lines.contains("Server calls: 4"));

        SongServiceProxy proxy = new SongServiceProxy(new SongServer(ProxyDriver.sampleLibrary()));
        List<ProxyDriver.Lookup> lookups = ProxyDriver.lookups(proxy);
        assertEquals(9, lookups.size());
        assertEquals("searchById(1)", lookups.get(0).getLabel());
        assertFalse(lookups.get(0).isCacheHit());
        assertTrue(lookups.get(0).getElapsedMillis() >= 900);
        assertTrue(lookups.get(1).isCacheHit());
        assertTrue(lookups.get(1).getElapsedMillis() < 500);
        assertFalse(lookups.get(2).isCacheHit());
        assertTrue(lookups.get(3).isCacheHit());
        assertTrue(lookups.get(3).getResult().contains("Jack Black"));
        assertFalse(lookups.get(4).isCacheHit());
        assertTrue(lookups.get(5).isCacheHit());
        assertTrue(lookups.get(6).isCacheHit());
        assertTrue(lookups.get(6).getResult().contains("Save Your Tears"));
        assertFalse(lookups.get(7).isCacheHit());
        assertEquals("not found", lookups.get(7).getResult());
        assertTrue(lookups.get(8).isCacheHit());
        assertTrue(lookups.get(8).getElapsedMillis() < 500);
        assertEquals(5, proxy.getCacheHits());
        assertEquals(4, proxy.getCacheMisses());
    }

    @Test
    void mainPrintsTheLibraryAndCacheHits() {
        String output = captureMainOutput();

        assertTrue(output.contains("Library (8 songs)"));
        assertTrue(output.contains("Cache hits: 5"));
        assertTrue(output.contains("Server calls: 4"));
    }

    private static String captureMainOutput() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            ProxyDriver.main(new String[0]);
            return buffer.toString();
        } finally {
            System.setOut(original);
        }
    }
}
