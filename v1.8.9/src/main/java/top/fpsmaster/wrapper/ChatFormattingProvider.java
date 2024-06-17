package top.fpsmaster.wrapper;


import java.util.*;
import java.util.regex.Pattern;

public enum ChatFormattingProvider {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    OBFUSCATED('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    public static final char PREFIX_CODE = '§';
    private static final Map<Character, ChatFormattingProvider> FORMATTING_BY_CHAR = new HashMap<>();
    private static final Map<String, ChatFormattingProvider> FORMATTING_BY_NAME = new HashMap<>();
    private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-OR]");
    private final char code;
    private final boolean isFormat;
    private final String toString;

    ChatFormattingProvider(char code) {
        this(code, false);
    }

    ChatFormattingProvider(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = "§" + code;
    }

    public char getChar() {
        return this.code;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public String toString() {
        return this.toString;
    }

    public static String stripFormatting(String input) {
        return input == null ? null : STRIP_FORMATTING_PATTERN.matcher(input).replaceAll("");
    }

    public static ChatFormattingProvider getByChar(char code) {
        return FORMATTING_BY_CHAR.get(code);
    }

    public static ChatFormattingProvider getByName(String name) {
        return name == null ? null : FORMATTING_BY_NAME.get(name.toLowerCase());
    }

    public static Collection<String> getNames(boolean getColors, boolean getFormats) {
        List<String> result = new ArrayList<>();
        ChatFormattingProvider[] arr$ = values();
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ChatFormattingProvider format = arr$[i$];
            if ((!format.isColor() || getColors) && (!format.isFormat() || getFormats)) {
                result.add(format.getName());
            }
        }

        return result;
    }

    static {
        ChatFormattingProvider[] arr$ = values();
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ChatFormattingProvider format = arr$[i$];
            FORMATTING_BY_CHAR.put(format.getChar(), format);
            FORMATTING_BY_NAME.put(format.getName(), format);
        }

    }
}
