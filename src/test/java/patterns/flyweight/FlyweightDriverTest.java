package patterns.flyweight;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlyweightDriverTest {
    @Test
    void savesHelloWorldWithFourStylesAndKeepsTheOldStyleAfterEdit(@TempDir Path dir) throws IOException {
        File file = dir.resolve("nested").resolve("HelloWorldCS5800.txt").toFile();

        List<String> lines = FlyweightDriver.run(file);

        assertTrue(lines.contains("Document: HelloWorldCS5800"));
        assertTrue(lines.contains("H -> Arial, 12, Red"));
        assertTrue(lines.contains("o -> Calibri, 14, Blue"));
        assertTrue(lines.contains("l -> Verdana, 16, Black"));
        assertTrue(lines.contains("5 -> Arial, 14, Black"));
        assertTrue(lines.contains("Characters: 16"));
        assertTrue(lines.contains("Property variations: 4"));
        assertTrue(lines.contains("Unique property objects: 4"));
        assertTrue(lines.contains("Same properties share one object: true"));
        assertTrue(lines.contains("Loaded text: HelloWorldCS5800"));
        assertTrue(lines.contains("Loaded matches saved characters: true"));
        assertTrue(lines.contains("Loaded properties are the same objects: true"));
        assertTrue(lines.contains("Unique property objects after load: 4"));
        assertTrue(lines.contains("Edited character 15 to Arial, 14, Blue"));
        assertTrue(lines.contains("Text after edit: HelloWorldCS5800"));
        assertTrue(lines.contains("Unique property objects after edit: 5"));
        assertTrue(lines.contains("Previous style still cached: true"));
        assertTrue(lines.contains("Reloaded text: HelloWorldCS5800"));
        assertTrue(lines.contains("Reloaded last style: Arial, 14, Blue"));
        assertTrue(lines.contains("Reloaded last style is the shared object: true"));

        assertEquals("H|Arial|12|Red", Files.readAllLines(file.toPath()).get(0));
        assertEquals("o|Calibri|14|Blue", Files.readAllLines(file.toPath()).get(4));
        assertEquals("l|Verdana|16|Black", Files.readAllLines(file.toPath()).get(8));
        assertEquals("5|Arial|14|Black", Files.readAllLines(file.toPath()).get(12));
        assertEquals("0|Arial|14|Black", Files.readAllLines(file.toPath()).get(15));

        File edited = dir.resolve("nested").resolve("HelloWorldCS5800-edited.txt").toFile();
        List<String> editedLines = Files.readAllLines(edited.toPath());
        assertEquals(16, editedLines.size());
        assertEquals("0|Arial|14|Black", editedLines.get(14));
        assertEquals("0|Arial|14|Blue", editedLines.get(15));
    }

    @Test
    void runSavesTheDefaultDocument() {
        List<String> lines = FlyweightDriver.run();

        assertTrue(lines.contains("Saved: documents/HelloWorldCS5800.txt"));
        assertTrue(new File("documents", "HelloWorldCS5800.txt").isFile());
    }

    @Test
    void mainPrintsTheDocument() {
        String output = captureMainOutput();

        assertTrue(output.contains("Document: HelloWorldCS5800"));
        assertTrue(output.contains("Property variations: 4"));
    }

    private static String captureMainOutput() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            FlyweightDriver.main(new String[0]);
            return buffer.toString();
        } finally {
            System.setOut(original);
        }
    }
}
