package patterns;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @Test
    void mainPrintsBothDemos() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }

        String output = buffer.toString();
        assertTrue(output.contains("Flyweight: Text Editor"));
        assertTrue(output.contains("HelloWorldCS5800"));
        assertTrue(output.contains("Property variations: 4"));
        assertTrue(output.contains("Previous style still cached: true"));
        assertTrue(output.contains("Proxy: Music Streaming"));
        assertTrue(output.contains("searchById(1) again -> cache"));
        assertTrue(output.contains("Server calls: 4"));
    }
}
