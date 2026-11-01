package patterns.flyweight;

import java.util.Objects;

/**
 * Shared style for a character: font, size, and color.
 * The character itself is stored separately and is not part of this object.
 */
public final class CharacterProperties {
    private final String font;
    private final int size;
    private final String color;

    CharacterProperties(String font, int size, String color) {
        this.font = font;
        this.size = size;
        this.color = color;
    }

    public String getFont() {
        return font;
    }

    public int getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String describe() {
        return font + ", " + size + ", " + color;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharacterProperties other)) {
            return false;
        }
        return size == other.size && font.equals(other.font) && color.equals(other.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, size, color);
    }

    @Override
    public String toString() {
        return describe();
    }
}
