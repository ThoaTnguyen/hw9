package patterns.flyweight;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds HelloWorldCS5800 with four character styles, saves it, and loads it back.
 * An extra edit shows that a new style is added without dropping the old one.
 */
public final class FlyweightDriver {
    static final String TEXT = "HelloWorldCS5800";

    private FlyweightDriver() {
    }

    public static void main(String[] args) {
        run().forEach(System.out::println);
    }

    public static List<String> run() {
        return run(new File("documents", "HelloWorldCS5800.txt"));
    }

    public static List<String> run(File file) {
        try {
            return writeDemo(file);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static List<String> writeDemo(File file) throws IOException {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document document = new Document(TEXT, factory);
        Style[] styles = {
                new Style("Arial", 12, "Red"),
                new Style("Calibri", 14, "Blue"),
                new Style("Verdana", 16, "Black"),
                new Style("Arial", 14, "Black")
        };
        for (int i = 0; i < TEXT.length(); i++) {
            Style style = styles[i / 4];
            document.addCharacter(TEXT.charAt(i), style.font, style.size, style.color);
        }

        List<String> lines = new ArrayList<>();
        lines.add("Document: " + document.getName());
        appendCharacters(lines, document);
        lines.add("Characters: " + document.length());
        lines.add("Property variations: " + document.variationCount());
        lines.add("Unique property objects: " + factory.size());
        lines.add("Same properties share one object: " + document.sharesPropertyObjects());

        document.save(file);
        lines.add("Saved: " + file.getPath());

        Document loaded = Document.load(file, document.getName(), factory);
        lines.add("Loaded text: " + loaded.getText());
        lines.add("Loaded matches saved characters: " + sameCharacters(document, loaded));
        lines.add("Loaded properties are the same objects: " + samePropertyInstances(document, loaded));
        lines.add("Unique property objects after load: " + factory.size());

        CharacterProperties previous = document.getCharacters().get(15).getProperties();
        document.setProperties(15, "Arial", 14, "Blue");
        lines.add("Edited character 15 to Arial, 14, Blue");
        lines.add("Text after edit: " + document.getText());
        lines.add("Unique property objects after edit: " + factory.size());
        lines.add("Previous style still cached: " + (factory.get("Arial", 14, "Black") == previous));

        File editedFile = editedCopy(file);
        document.save(editedFile);
        Document reloaded = Document.load(editedFile, document.getName(), factory);
        lines.add("Saved edit: " + editedFile.getPath());
        lines.add("Reloaded text: " + reloaded.getText());
        lines.add("Reloaded last style: " + reloaded.getCharacters().get(15).getProperties().describe());
        lines.add("Reloaded last style is the shared object: "
                + (reloaded.getCharacters().get(15).getProperties() == factory.get("Arial", 14, "Blue")));
        return lines;
    }

    private static void appendCharacters(List<String> lines, Document document) {
        for (TextCharacter character : document.getCharacters()) {
            lines.add(character.getSymbol() + " -> " + character.getProperties().describe());
        }
    }

    private static boolean sameCharacters(Document left, Document right) {
        List<TextCharacter> a = left.getCharacters();
        List<TextCharacter> b = right.getCharacters();
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getSymbol() != b.get(i).getSymbol()
                    || !a.get(i).getProperties().equals(b.get(i).getProperties())) {
                return false;
            }
        }
        return true;
    }

    private static boolean samePropertyInstances(Document left, Document right) {
        List<TextCharacter> a = left.getCharacters();
        List<TextCharacter> b = right.getCharacters();
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getProperties() != b.get(i).getProperties()) {
                return false;
            }
        }
        return true;
    }

    private static File editedCopy(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        String editedName = (dot == -1 ? name : name.substring(0, dot))
                + "-edited"
                + (dot == -1 ? "" : name.substring(dot));
        File parent = file.getParentFile();
        return parent == null ? new File(editedName) : new File(parent, editedName);
    }

    private static final class Style {
        private final String font;
        private final int size;
        private final String color;

        private Style(String font, int size, String color) {
            this.font = font;
            this.size = size;
            this.color = color;
        }
    }
}
