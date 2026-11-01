package patterns.flyweight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CharacterPropertiesTest {
    @Test
    void getFontReturnsTheFont() {
        assertEquals("Calibri", style().getFont());
    }

    @Test
    void getSizeReturnsTheSize() {
        assertEquals(14, style().getSize());
    }

    @Test
    void getColorReturnsTheColor() {
        assertEquals("Blue", style().getColor());
    }

    @Test
    void describeJoinsFontSizeAndColor() {
        assertEquals("Calibri, 14, Blue", style().describe());
    }

    @Test
    void toStringMatchesDescribe() {
        CharacterProperties properties = style();

        assertEquals(properties.describe(), properties.toString());
    }

    @Test
    void equalsComparesFontSizeAndColor() {
        CharacterProperties properties = style();
        CharacterProperties same = new CharacterPropertiesFactory().get("Calibri", 14, "Blue");
        CharacterProperties differentSize = new CharacterPropertiesFactory().get("Calibri", 16, "Blue");

        assertEquals(properties, same);
        assertEquals(properties, properties);
        assertNotEquals(properties, differentSize);
        assertNotEquals(properties, "Calibri, 14, Blue");
    }

    @Test
    void hashCodeMatchesAnEqualStyle() {
        CharacterProperties properties = style();
        CharacterProperties same = new CharacterPropertiesFactory().get("Calibri", 14, "Blue");

        assertEquals(properties.hashCode(), same.hashCode());
    }

    private static CharacterProperties style() {
        return new CharacterPropertiesFactory().get("Calibri", 14, "Blue");
    }
}
