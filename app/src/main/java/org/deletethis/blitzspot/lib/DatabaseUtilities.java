package org.deletethis.blitzspot.lib;

import java.text.Normalizer;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class DatabaseUtilities {
    private static final String EMPTY = "";

    private static void convertRemainingAccentCharacters(final StringBuilder decomposed) {
        for (int i = 0; i < decomposed.length(); i++) {
            if (decomposed.charAt(i) == '\u0141') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'L');
            } else if (decomposed.charAt(i) == '\u0142') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'l');
            }
        }
    }
    private final static Pattern DIACRITICAL = Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); //$NON-NLS-1$

    private static String stripDiacritics(String input) {
        final StringBuilder sb = new StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD));
        convertRemainingAccentCharacters(sb);
        // Note that this doesn't correctly remove ligatures...

        return DIACRITICAL.matcher(sb).replaceAll(EMPTY);
    }

    private static boolean hasLastChar(CharSequence cs, char c) {
        return (cs.length() > 0 && cs.charAt(cs.length()-1) == c);
    }



    public static String normalize(String input) {
        input = stripDiacritics(input);

        // locale was chosen quite randomly, but I think it's better when it's fixed
        input = input.toLowerCase(Locale.ENGLISH);
        StringBuilder sb = new StringBuilder(input.length());
        for(int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);
            if(Character.isLetterOrDigit(ch)) {
                if(!hasLastChar(sb, ch)) {
                    sb.append(ch);
                }
            } else if(Character.isSpaceChar(ch) || Character.isWhitespace(ch)) {
                if(!hasLastChar(sb, ' ') && sb.length() > 0) {
                    sb.append(' ');
                }
            }
        }
        while(hasLastChar(sb, ' ')) {
            sb.setLength(sb.length()-1);
        }
        return sb.toString();
    }

    private static long timestamp(Date date) {
        return date.getTime();
    }

    public static long timestamp() {
        return timestamp(new Date());
    }

}
