package patterns.flyweight;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CharacterPropertiesFactoryTest {
    @Test
    void reusesOneObjectForTheSameStyle() {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();

        CharacterProperties first = factory.get("Arial", 12, "Red");
        CharacterProperties second = factory.get("Arial", 12, "Red");

        assertSame(first, second);
        assertEquals(1, factory.size());
        assertEquals("Arial, 12, Red", first.describe());
    }

    @Test
    void differentStylesAreDifferentObjects() {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();

        CharacterProperties arial = factory.get("Arial", 12, "Red");
        CharacterProperties calibri = factory.get("Calibri", 14, "Blue");
        CharacterProperties arialAgain = factory.get("Arial", 12, "Red");

        assertNotSame(arial, calibri);
        assertSame(arial, arialAgain);
        assertEquals(2, factory.size());
    }

    @Test
    void separateFactoriesDoNotShareObjects() {
        CharacterPropertiesFactory first = new CharacterPropertiesFactory();
        CharacterPropertiesFactory second = new CharacterPropertiesFactory();

        assertNotSame(first.get("Verdana", 16, "Black"), second.get("Verdana", 16, "Black"));
    }

    @Test
    void rejectsUnknownProperties() {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.get(null, 12, "Red"));
        assertThrows(IllegalArgumentException.class, () -> factory.get("arial", 12, "Red"));
        assertThrows(IllegalArgumentException.class, () -> factory.get("Arial", 13, "Red"));
        assertThrows(IllegalArgumentException.class, () -> factory.get("Arial", 12, "Green"));
        assertEquals(0, factory.size());
    }

    @Test
    void hasNoWayToDeleteProperties() {
        for (Method method : CharacterPropertiesFactory.class.getDeclaredMethods()) {
            String name = method.getName().toLowerCase();
            assertFalse(name.contains("remove") || name.contains("delete") || name.contains("clear"), name);
        }
    }
}
