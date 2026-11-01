package patterns.flyweight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A document is a sequence of characters that can be edited, saved, and loaded.
 * Characters with the same font, size, and color share one property object.
 * Saving writes each character as {@code symbol|font|size|color}.
 */
public final class Document {
    private final String name;
    private final CharacterPropertiesFactory factory;
    private final List<TextCharacter> characters = new ArrayList<>();

    public Document(String name, CharacterPropertiesFactory factory) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("document name is required");
        }
        if (factory == null) {
            throw new IllegalArgumentException("character properties factory is required");
        }
        this.name = name;
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public int length() {
        return characters.size();
    }

    public String getText() {
        StringBuilder text = new StringBuilder(characters.size());
        for (TextCharacter character : characters) {
            text.append(character.getSymbol());
        }
        return text.toString();
    }

    public List<TextCharacter> getCharacters() {
        return List.copyOf(characters);
    }

    public int variationCount() {
        Set<CharacterProperties> styles = new HashSet<>();
        for (TextCharacter character : characters) {
            styles.add(character.getProperties());
        }
        return styles.size();
    }

    /**
     * @return true when every matching style in this document is the same object
     */
    public boolean sharesPropertyObjects() {
        for (int i = 0; i < characters.size(); i++) {
            for (int j = i + 1; j < characters.size(); j++) {
                CharacterProperties left = characters.get(i).getProperties();
                CharacterProperties right = characters.get(j).getProperties();
                if (left.equals(right) && left != right) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addCharacter(char symbol, String font, int size, String color) {
        characters.add(createCharacter(symbol, font, size, color));
    }

    public void insertCharacter(int index, char symbol, String font, int size, String color) {
        if (index < 0 || index > characters.size()) {
            throw new IndexOutOfBoundsException("character index: " + index);
        }
        characters.add(index, createCharacter(symbol, font, size, color));
    }

    public void setSymbol(int index, char symbol) {
        TextCharacter current = characterAt(index);
        characters.set(index, new TextCharacter(symbol, current.getProperties()));
    }

    public void setProperties(int index, String font, int size, String color) {
        TextCharacter current = characterAt(index);
        CharacterProperties properties = factory.get(font, size, color);
        characters.set(index, new TextCharacter(current.getSymbol(), properties));
    }

    /**
     * Removes a character from the document. The style object stays in the factory.
     */
    public TextCharacter removeCharacter(int index) {
        characterAt(index);
        return characters.remove(index);
    }

    public void save(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file is required");
        }
        File parent = file.getParentFile();
        if (parent != null) {
            Files.createDirectories(parent.toPath());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            for (TextCharacter character : characters) {
                CharacterProperties properties = character.getProperties();
                writer.write(character.getSymbol());
                writer.write('|');
                writer.write(properties.getFont());
                writer.write('|');
                writer.write(Integer.toString(properties.getSize()));
                writer.write('|');
                writer.write(properties.getColor());
                writer.newLine();
            }
        }
    }

    public static Document load(File file, String name, CharacterPropertiesFactory factory) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file is required");
        }
        Document document = new Document(name, factory);
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4 || parts[0].length() != 1) {
                    throw new IOException("invalid character record at line " + lineNumber);
                }
                int size;
                try {
                    size = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ex) {
                    throw new IOException("invalid size at line " + lineNumber, ex);
                }
                try {
                    document.addCharacter(parts[0].charAt(0), parts[1], size, parts[3]);
                } catch (IllegalArgumentException ex) {
                    throw new IOException(
                            "invalid character record at line " + lineNumber + ": " + ex.getMessage(), ex);
                }
            }
        }
        return document;
    }

    private TextCharacter createCharacter(char symbol, String font, int size, String color) {
        return new TextCharacter(symbol, factory.get(font, size, color));
    }

    private TextCharacter characterAt(int index) {
        if (index < 0 || index >= characters.size()) {
            throw new IndexOutOfBoundsException("character index: " + index);
        }
        return characters.get(index);
    }
}
