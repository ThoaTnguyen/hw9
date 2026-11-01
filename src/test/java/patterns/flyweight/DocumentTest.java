package patterns.flyweight;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentTest {
    @Test
    void getNameReturnsTheDocumentName() {
        Document document = new Document("notes", new CharacterPropertiesFactory());

        assertEquals("notes", document.getName());
    }

    @Test
    void charactersWithTheSameStyleShareOneFlyweight() {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document first = new Document("notes", factory);
        Document second = new Document("other", factory);

        first.addCharacter('A', "Arial", 12, "Red");
        first.addCharacter('B', "Arial", 12, "Red");
        second.addCharacter('C', "Arial", 12, "Red");
        first.addCharacter('D', "Calibri", 14, "Blue");

        assertEquals("ABD", first.getText());
        assertSame(first.getCharacters().get(0).getProperties(), first.getCharacters().get(1).getProperties());
        assertSame(first.getCharacters().get(0).getProperties(), second.getCharacters().get(0).getProperties());
        assertNotSame(first.getCharacters().get(0).getProperties(), first.getCharacters().get(2).getProperties());
        assertEquals(2, factory.size());
        assertEquals(2, first.variationCount());
        assertTrue(first.sharesPropertyObjects());
    }

    @Test
    void editingACharacterDoesNotDeleteTheOldStyle() {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document document = new Document("notes", factory);
        document.addCharacter('A', "Arial", 12, "Red");
        document.addCharacter('B', "Arial", 12, "Red");
        CharacterProperties original = document.getCharacters().get(0).getProperties();

        document.setSymbol(0, 'Z');
        assertEquals("ZB", document.getText());
        assertSame(original, document.getCharacters().get(0).getProperties());

        document.setProperties(0, "Verdana", 16, "Black");
        assertEquals(2, factory.size());
        assertSame(original, factory.get("Arial", 12, "Red"));
        assertSame(original, document.getCharacters().get(1).getProperties());
        assertEquals("Verdana, 16, Black", document.getCharacters().get(0).getProperties().describe());

        TextCharacter removed = document.removeCharacter(1);
        assertEquals('B', removed.getSymbol());
        assertSame(original, removed.getProperties());
        assertEquals("Z", document.getText());
        assertEquals(2, factory.size());
        assertSame(original, factory.get("Arial", 12, "Red"));
    }

    @Test
    void insertAndBounds() {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document document = new Document("notes", factory);
        document.addCharacter('B', "Arial", 12, "Black");
        document.insertCharacter(0, 'A', "Arial", 12, "Black");
        document.insertCharacter(2, 'C', "Arial", 14, "Blue");

        assertEquals("ABC", document.getText());
        assertThrows(IndexOutOfBoundsException.class, () -> document.setSymbol(3, 'D'));
        assertThrows(IndexOutOfBoundsException.class,
                () -> document.insertCharacter(4, 'D', "Arial", 12, "Black"));
        assertThrows(IllegalArgumentException.class, () -> document.addCharacter('|', "Arial", 12, "Black"));
    }

    @Test
    void saveAndLoadRoundTripSharesFlyweights(@TempDir Path dir) throws IOException {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document document = new Document("notes", factory);
        document.addCharacter('H', "Arial", 12, "Red");
        document.addCharacter('i', "Arial", 12, "Red");
        document.addCharacter('!', "Calibri", 16, "Blue");
        File file = dir.resolve("notes.txt").toFile();

        document.save(file);
        Document loaded = Document.load(file, "notes", factory);

        assertEquals("Hi!", loaded.getText());
        assertEquals(List.of("H|Arial|12|Red", "i|Arial|12|Red", "!|Calibri|16|Blue"),
                Files.readAllLines(file.toPath()));
        assertSame(document.getCharacters().get(0).getProperties(), loaded.getCharacters().get(0).getProperties());
        assertSame(loaded.getCharacters().get(0).getProperties(), loaded.getCharacters().get(1).getProperties());
        assertEquals(2, factory.size());
        assertTrue(loaded.sharesPropertyObjects());
    }

    @Test
    void loadIntoANewFactoryStillSharesStylesInsideTheDocument(@TempDir Path dir) throws IOException {
        CharacterPropertiesFactory original = new CharacterPropertiesFactory();
        Document document = new Document("notes", original);
        document.addCharacter('A', "Verdana", 14, "Black");
        document.addCharacter('B', "Verdana", 14, "Black");
        File file = dir.resolve("notes.txt").toFile();
        document.save(file);

        CharacterPropertiesFactory fresh = new CharacterPropertiesFactory();
        Document loaded = Document.load(file, "notes", fresh);

        assertSame(loaded.getCharacters().get(0).getProperties(), loaded.getCharacters().get(1).getProperties());
        assertNotSame(document.getCharacters().get(0).getProperties(), loaded.getCharacters().get(0).getProperties());
        assertEquals(1, fresh.size());
    }

    @Test
    void loadRejectsACorruptFile(@TempDir Path dir) throws IOException {
        File file = dir.resolve("bad.txt").toFile();
        Files.writeString(file.toPath(), "AB|Arial|12|Red\n");
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();

        IOException ex = assertThrows(IOException.class, () -> Document.load(file, "notes", factory));
        assertTrue(ex.getMessage().contains("line 1"));
    }

    @Test
    void rejectsMissingNameFactoryAndFile() {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        assertThrows(IllegalArgumentException.class, () -> new Document("  ", factory));
        assertThrows(IllegalArgumentException.class, () -> new Document("notes", null));
        Document document = new Document("notes", factory);
        assertThrows(IllegalArgumentException.class, () -> document.save(null));
        assertThrows(IllegalArgumentException.class, () -> Document.load(null, "notes", factory));
    }

    @Test
    void returnedCharacterListCannotChangeTheDocument() {
        Document document = new Document("notes", new CharacterPropertiesFactory());
        document.addCharacter('A', "Arial", 12, "Red");

        assertThrows(UnsupportedOperationException.class, () -> document.getCharacters().clear());
        assertEquals(1, document.length());
        assertFalse(document.getText().isEmpty());
    }
}
