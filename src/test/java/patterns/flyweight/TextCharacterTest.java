package patterns.flyweight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextCharacterTest {
    @Test
    void getSymbolReturnsTheCharacter() {
        TextCharacter character = character();

        assertEquals('H', character.getSymbol());
    }

    @Test
    void getPropertiesReturnsTheSharedStyle() {
        CharacterProperties properties = new CharacterPropertiesFactory().get("Arial", 12, "Red");
        TextCharacter character = new TextCharacter('H', properties);

        assertSame(properties, character.getProperties());
    }

    @Test
    void constructorRejectsASeparatorOrMissingStyle() {
        CharacterProperties properties = new CharacterPropertiesFactory().get("Arial", 12, "Red");

        assertThrows(IllegalArgumentException.class, () -> new TextCharacter('|', properties));
        assertThrows(IllegalArgumentException.class, () -> new TextCharacter('\n', properties));
        assertThrows(IllegalArgumentException.class, () -> new TextCharacter('H', null));
    }

    private static TextCharacter character() {
        return new TextCharacter('H', new CharacterPropertiesFactory().get("Arial", 12, "Red"));
    }
}
