package it.saimao.tmkkeyboard.maoconverter;
/*
Version 2.2
 */
public class TaiNueaSyllableBreaker {

    public static String syllable_break(String input) {
        String result = input.replaceAll("([\\u0028\\u005b\\u007b\\u003c])", "$1\u0020");
        result = result.replaceAll("([\\u104b\\u104a\\u002e\\u002c\\u0029\\u005d\\u007d\\u003e\\u003f\\u0021\\u003a\\u003b\\u0022\\u0027\\u002a\\u005e])", "\u0020$1");

        String[] arrs;
        boolean con;
        int count = 0;
        do {
            count++;
            result = tm3wordbreaks(result);
            result = checkWordbreak(result);
            result = tm2wordbreaks(result);
            result = checkWordbreak(result);
            if (count == 100) {
                break;
            }
            arrs = result.split("\\u0020");
            con = continueConverting(arrs);
        } while (con);
        result = result.replaceAll("(\\u0020)([\\u104b\\u104a\\u002e\\u002c\\u0029\\u005d\\u007d\\u003e\\u003f\\u0021\\u003a\\u003b\\u0022\\u0027\\u002a\\u005e])", "$2");
        result = result.replaceAll("([\\u0028\\u005b\\u007b\\u003c])(\\u0020)", "$1");
        result = result.concat("\u0020");
        return result;
    }

    private static boolean continueConverting(String[] tests) {
        for (String str : tests) {
            if ((int) str.charAt(0) >= 6480 && (int) str.charAt(0) <= 6509) {
                if (str.length() == 4) {
                    int consonant_count = 0, vowel_count = 0;
                    for (char c : str.toCharArray()) {
                        int character = (int) c;
                        // Check consonants ka to na
                        // In one word, no more than 3 consonants should exist
                        if (character >= 6480 && character <= 6498) {
                            consonant_count++;
                        }
                        // In one word, no more than 2 vowels should exist
                        if (character >= 6499 && character <= 6508) {
                            vowel_count ++;
                        }
                    }
                    if (consonant_count >= 3 || vowel_count >= 2) {
                        return true;
                    }
                }
                if (str.length() > 4) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String checkWordbreak(String input) {
        String output = input;
        output = output.replaceAll("(\\u0020)([\\u02ca\\u1974\\u02c7\\u1971\\u02cb\\u1972\\u00a8\\u1970\\u02d9\\u1973])", "$2");
        output = output.replaceAll("(\\u0020)([\\u1963\\u1964\\u1965\\u1966\\u1969\\u1967\\u1968\\u196a\\u196b\\u196c])", "$2");
        output = output.replaceAll("(\\u0020)([\\u1950-\\u1962\\u196d])([\\u02ca\\u1974\\u02c7\\u1971\\u02cb\\u1972\\u00a8\\u1970\\u02d9\\u1973])", "$2$3");
        output = output.replaceAll("(\\u0020)([\\u1950-\\u1962\\u196d])([\\u1950-\\u1962\\u196d])([\\u1963\\u1964\\u1965\\u1966\\u1969\\u1967\\u1968\\u196a\\u196b])", "$2$3$4");
        output = output.replaceAll("(\\u0020|\\u000a)([\\u1950-\\u1962\\u196d])(\\u0020|\\u000a)", "$2$3");
        output = output.replaceAll("(\\u0020)([\\u1950-\\u1962\\u196d])([\\u1950-\\u1962\\u196d])([\\u1950-\\u1962\\u196d])", "$2\u0020$3$4");
        output = output.replaceAll("(\\u0020)([\\u1950-\\u1962\\u196d])([\\u1950-\\u1962\\u196d])([\\u1963\\u1964\\u1965\\u1966\\u1969\\u1967\\u1968\\u196a\\u196b\\u196c])(\\u0020)", "$2$1$3$4");
        output = output.replaceAll("\\u0020+", "\u0020");
        return output;
    }

    private static String tm3wordbreaks(String input) {
        String output = input;
        output = output.replaceAll("([\\u1950-\\u1962\\u196d])([\\u1963\\u1964\\u1965\\u1966\\u1969\\u1967\\u1968\\u196a\\u196b])([\\u1950\\u1952\\u196d\\u1956\\u1962\\u1959\\u195b\\u195d])", "$1$2$3\u0020");
        return output;
    }

    private static String tm2wordbreaks(String input_text) {

        String output_text = input_text;
        output_text = output_text.replaceAll("null", "");
        //                                                                                       ᥴ                    ᥱ                      ᥲ                     ᥰ                      ᥳ
        output_text = output_text.replaceAll("([\\u02ca\\u1974\\u02c7\\u1971\\u02cb\\u1972\\u00a8\\u1970\\u02d9\\u1973])", "$1\u0020");
        output_text = output_text.replaceAll("(\\u196c)([^\\u02ca\\u1974\\u02c7\\u1971\\u02cb\\u1972\\u00a8\\u1970\\u02d9\\u1973])", "$1\u0020$2");
        output_text = output_text.replaceAll("([\\u1950-\\u1962\\u196d])([\\u1963\\u1964\\u1965\\u1966\\u1969\\u1967\\u1968\\u196a\\u196b])([^\\u02ca\\u1974\\u02c7\\u1971\\u02cb\\u1972\\u00a8\\u1970\\u02d9\\u1973])", "$1$2\u0020$3");
        output_text = output_text.replaceAll("([\\u1950-\\u1962\\u196d])([\\u1950\\u1952\\u196d\\u1956\\u1959\\u195b\\u195d\\u1962])", "$1$2\u0020");
        return output_text;
    }

}
