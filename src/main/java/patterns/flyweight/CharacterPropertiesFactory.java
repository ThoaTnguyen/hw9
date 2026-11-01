package patterns.flyweight;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Creates and reuses character property objects.
 * Each font, size, and color combination is created once and then shared.
 * Property objects are never removed from the pool.
 */
public final class CharacterPropertiesFactory {
    public static final Set<String> FONTS = Set.of("Arial", "Calibri", "Verdana");
    public static final Set<String> COLORS = Set.of("Red", "Blue", "Black");
    public static final Set<Integer> SIZES = Set.of(12, 14, 16);

    private final Map<Key, CharacterProperties> pool = new HashMap<>();

    public CharacterProperties get(String font, int size, String color) {
        String checkedFont = requireFont(font);
        int checkedSize = requireSize(size);
        String checkedColor = requireColor(color);
        Key key = new Key(checkedFont, checkedSize, checkedColor);
        CharacterProperties existing = pool.get(key);
        if (existing != null) {
            return existing;
        }
        CharacterProperties created = new CharacterProperties(checkedFont, checkedSize, checkedColor);
        pool.put(key, created);
        return created;
    }

    public int size() {
        return pool.size();
    }

    private static String requireFont(String font) {
        if (font == null || font.isBlank()) {
            throw new IllegalArgumentException("font is required");
        }
        if (!FONTS.contains(font)) {
            throw new IllegalArgumentException("font must be Arial, Calibri, or Verdana");
        }
        return font;
    }

    private static int requireSize(int size) {
        if (!SIZES.contains(size)) {
            throw new IllegalArgumentException("size must be 12, 14, or 16");
        }
        return size;
    }

    private static String requireColor(String color) {
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("color is required");
        }
        if (!COLORS.contains(color)) {
            throw new IllegalArgumentException("color must be Red, Blue, or Black");
        }
        return color;
    }

    private static final class Key {
        private final String font;
        private final int size;
        private final String color;

        private Key(String font, int size, String color) {
            this.font = font;
            this.size = size;
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Key other)) {
                return false;
            }
            return size == other.size && font.equals(other.font) && color.equals(other.color);
        }

        @Override
        public int hashCode() {
            int result = font.hashCode();
            result = 31 * result + size;
            result = 31 * result + color.hashCode();
            return result;
        }
    }
}
