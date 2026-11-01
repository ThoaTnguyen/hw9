package patterns.flyweight;

/**
 * One character in a document. The symbol is unique to this character.
 * The style is a shared flyweight.
 */
public final class TextCharacter {
    private final char symbol;
    private final CharacterProperties properties;

    public TextCharacter(char symbol, CharacterProperties properties) {
        if (symbol == '|' || symbol == '\n' || symbol == '\r' || symbol == '\t') {
            throw new IllegalArgumentException("character cannot be '|', a tab, or a newline");
        }
        if (properties == null) {
            throw new IllegalArgumentException("character properties are required");
        }
        this.symbol = symbol;
        this.properties = properties;
    }

    public char getSymbol() {
        return symbol;
    }

    public CharacterProperties getProperties() {
        return properties;
    }
}
