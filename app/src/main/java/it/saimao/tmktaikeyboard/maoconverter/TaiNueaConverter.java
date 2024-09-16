package it.saimao.tmktaikeyboard.maoconverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 2.1
 */
public class TaiNueaConverter {

    public static String shn2tdd(String input) {

        return input
                .replaceAll("(\\u1075\\u103b)", "\u1953")
                .replaceAll("\\u1075", "\u1950")
                .replaceAll("(\\u1076\\u103b)", "\u1960")
                .replaceAll("\\u1076", "\u1951")

                .replaceAll("\\u1004", "\u1952")
                .replaceAll("\\u1078", "\u1953")
                .replaceAll("(\\u101e\\u103b)", "\u1961")
                .replaceAll("(\\u101e\\u103c\\u1083\\u1087)", "\u1954\u1963\u1971\u1958\u1963\u1971")
                .replaceAll("\\u101e", "\u1954")
                .replaceAll("\\u107a", "\u196d")
                .replaceAll("\\u1010\\u103c\\u1083\\u1038", "\u1956\u1963\u1970\u1958\u1963\u1970")
                .replaceAll("\\u1010", "\u1956")
                .replaceAll("\\u1011", "\u1957")
                .replaceAll("\\u107c", "\u1962")
                .replaceAll("\\u1015", "\u1959")
                .replaceAll("\\u107d\\u103c\\u1083\\u1038", "\u195a\u1963\u1970\u1958\u1963\u1970")
                .replaceAll("\\u107d", "\u195a")
                .replaceAll("\\u107e", "\u195c")
                .replaceAll("\\u1019", "\u195b")
                .replaceAll("\\u101a", "\u1955")
//                "{  \"from\": \"\\u101b\",  \"to\": \"\\u1951\" }," +
                .replaceAll("\\u101c", "\u1958")
                .replaceAll("\u101d", "\u195d")
                .replaceAll("\u1081", "\u195e")
                .replaceAll("\u1022", "\u195f")

                // End Tai Consonants
                .replaceAll("(\\u1031\\u1083)", "\u1969\u1974")
                .replaceAll("([\\u1031\\u1035])", "\u1965\u1974")
                .replaceAll("([\\u1084\\u1085])", "\u1966\u1974")

                .replaceAll("(\\u102d\\u102f)", "\u196a")
                .replaceAll("(\\u102d\\u1030)", "\u196b")

                .replaceAll("\\u102f", "\u1967")
//                "{  \"from\": \"\\u103b\",  \"to\": \"\\u1966\" }," +
                .replaceAll("\\u1030", "\u1968\u1974")
                .replaceAll("\\u103d", "\u1969")
                .replaceAll("\\u103d", "\u1969")
                // kai khin
                .replaceAll("\\u1086", "\u196d\u1974")
                .replaceAll("\\u102e", "\u1964\u1974")
                .replaceAll("\\u102d", "\u1964")
                .replaceAll("(\\u1082\\u103a)", "\u196c\u1974")
                .replaceAll("\\u103a", "\u1974")
                .replaceAll("\\u1087", "\u1971")
                .replaceAll("\\u1088", "\u1972")
                .replaceAll("\\u1038", "\u1970")
                .replaceAll("([\\u1089\\u1037])", "\u1973")
                .replaceAll("\\u1082", "")
                // set temp for yak khunt
                .replaceAll("\\u108a", "\u5000")
                .replaceAll("\\u1083", "\u1963\u1974")
                .replaceAll("\\u1062", "\u1963")
                .replaceAll("(\\u1974)([\\u1950-\\u195f\\u196d\\u1962])(\\u1974)", "$2$3")
                .replaceAll("(\\u1974)([\\u1971\\u1972\\u1970\\u1973])", "$2")
                // Fixed 1
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])(\\u1968)([\\u1971\\u1972\\u1970\\u1973\\u1974\\u5000])", "$1\u1967$3")
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u196b\\u196a])(\\u195d)", "$1$2")

                .replaceAll("(\\u1974)(\\u5000)", "");
    }

    private static String beforeConvert(String input) {
        String output = input;
        output = output.replaceAll("\\u0020", "\u1500");
        return output;
    }

    private static String afterConvert(String input) {
        String output = input;
        output = output.replaceAll("\\u0020", "");
        output = output.replaceAll("\\u1500", "\u0020");
        return output;
    }

    public static String tdd2shn(String input) {
        String output = input;
        // Convert spacing to another unused codes
        output = beforeConvert(output);
        // Word break Tai Nuea text
        output = TaiNueaSyllableBreaker.syllable_break(output);
        // Convert the word breaking Tai Nuea text
        output = tdd2shn_raw(output);
        // Convert the unused code to spacing back
        output = afterConvert(output);
        // Additional fix for Fra, Tra, Sra etc...
        output = additionalFix(output);
        return output;
    }

    private static String additionalFix(String input) {
        String output = input;
        // Phra, Tra, Sra
        output = output.replaceAll("(\\u107d\\u1083\\u1038)(\\u0020)?(\\u101c\\u1083\\u1038)", "\u107d\u103c\u1083\u1038");
        output = output.replaceAll("(\\u1010\\u1083\\u1038)(\\u0020)?(\\u101c\\u1083\\u1038)", "\u1010\u103c\u1083\u1038");
        output = output.replaceAll("(\\u101e\\u1083\\u1087)(\\u0020)?(\\u101c\\u1083\\u1087)", "\u101e\u103c\u1083\u1087");
        return output;
    }


    private static String tdd2shn_raw(String input) {
        return input
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u196b\\u196a])([\\u1971\\u1972\\u1970\\u1973\\u1974\\u0020])", "$1$2\u195d$3")
                // For Yak Khint
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u1969\\u1965\\u1966\\u1964\\u196a\\u196b\\u1963\\u1967\\u1968])?([\\u1950-\\u195f\\u196d\\u1962])([\\u0020\\u000a\\u104a\\u104b])", "$1$2$3\u103a\u108a$4")
                .replaceAll("([\\u1968\\u1967\\u196c\\u1963\\u1965\\u1966\\u1964\\u1969])([\\u0020\\u000a\\u104a\\u104b])", "$1\u108a$2")
                // kai khin
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u1963\\u1969])?(\\u196d)", "$1$2\u1086")
                .replaceAll("(\\u1086)(\\u103a)(\\u108a)", "$1$3")
                // arr pot & arr yao
                .replaceAll("(\\u1963)([\\u1970-\\u1974\\u00a8\\u02c7\\u02cb\\u02d9\\u02ca\\u108a])", "\u1083$2")
                .replaceAll("\\u1963", "\u1062")
                // Hoi (or) asai aryao
                .replaceAll("(\\u1969)([\\u1970-\\u1974\\u00a8\\u02c7\\u02cb\\u02d9\\u02ca\\u108a])", "\u1031\u1083$2")
                // ဢေ
                .replaceAll("(\\u1965)([\\u1970-\\u1974\\u00a8\\u02c7\\u02cb\\u02d9\\u02ca\\u108a])", "\u1031$2")
                // ဢဵ
                .replaceAll("\\u1965", "\u1035")
                // ဢႄ
                .replaceAll("(\\u1966)([\\u1970-\\u1974\\u00a8\\u02c7\\u02cb\\u02d9\\u02ca\\u108a])", "\u1084$2")
                // ဢႅ
                .replaceAll("\\u1966", "\u1085")
                // ဢီ
                .replaceAll("(\\u1964)([\\u1970-\\u1974\\u00a8\\u02c7\\u02cb\\u02d9\\u02ca\\u108a])", "\u102e$2")
                // ဢိ
                .replaceAll("\\u1964", "\u102d")
                // တူၼ်းသဵင် 5 တူၼ်း

                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u1971\\u02c7])", "$1\u103a\u1087")
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u1972\\u02cb])", "$1\u103a\u1088")
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u1970\\u00a8])", "$1\u103a\u1038")
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u1973\\u02d9])", "$1\u103a\u1089")
                .replaceAll("([\\u1950-\\u195f\\u196d\\u1962])([\\u1974\\u02ca])", "$1\u103a\u5000")

//                "{\"from\":\"(?!([\\u1971\\u02c7\\u1972\\u02cb\\u1970\\u00a8\\u1973\\u02d9\\u1974\\u02ca]))(\\u0020)\", \"to\": \"\\u108a$2\"}," +
                .replaceAll("([\\u1971\\u02c7])", "\u1087")
                .replaceAll("([\\u1972\\u02cb])", "\u1088")
                .replaceAll("([\\u1970\\u00a8])", "\u1038")
                .replaceAll("([\\u1973\\u02d9])", "\u1089")
                .replaceAll("([\\u1974\\u02ca])", "\u5000")
                // end toon sein
                // tai lone consonants
                .replaceAll("\\u1950", "\u1075")
                .replaceAll("\\u1951", "\u1076")
                .replaceAll("\\u1952", "\u1004")
                .replaceAll("\\u1953", "\u1078")
                .replaceAll("\\u1954", "\u101e")
                // Sha & Cha
                .replaceAll("\\u1961", "\u101e\u103b")
                .replaceAll("\\u1960", "\u1076\u103b")
                .replaceAll("\\u196d", "\u107a")
                .replaceAll("\\u1956", "\u1010")
                .replaceAll("\\u1957", "\u1011")
                .replaceAll("\\u1962", "\u107c")
                .replaceAll("\\u1959", "\u1015")
                .replaceAll("\\u195a", "\u107d")
                .replaceAll("\\u195c", "\u107e")
                .replaceAll("\\u195b", "\u1019")
                .replaceAll("\\u1955", "\u101a")
                .replaceAll("\\u1958", "\u101c")
                .replaceAll("\\u195d", "\u101d")
                .replaceAll("\\u195e", "\u1081")
                .replaceAll("\\u195f", "\u1022")

                // End Tai consonants
                .replaceAll("\\u1967", "\u102f")
                .replaceAll("\\u1968", "\u1030")
                .replaceAll("\\u1969", "\u103d")
                // Kang King
                .replaceAll("\\u196c", "\u1082\u103a")
                .replaceAll("\\u196a", "\u102d\u102f")
                .replaceAll("\\u196b", "\u102d\u1030")
                // For yak khin
                .replaceAll("([\\u1087\\u1088\\u1038\\u1089\\u1037\\u108a\\u5000])(\\u108a)", "$1")
//                .replaceAll("([\\u1075-\\u1081\\u1004\\u101e\\u1010\\u1011\\u1015\\u1019\\u101a\\u101b\\u101c\\u101d\\u1022])(\\u1035)(\\u108a)\", \"to\": \"$1\\u103a$3\")
//                .replaceAll("(\\u1085)(\\u108a)\", \"to\": \"\\u1084$2\")

                // Fix 1
                .replaceAll("([\\u1075-\\u1081\\u1004\\u101e\\u1010\\u1011\\u1015\\u1019\\u101a\\u101b\\u101c\\u101d\\u1022])(\\u102f)([\\u1087\\u1088\\u1038\\u1089\\u1037\\u108a\\u5000])", "$1\u1030$3")
                .replaceAll("\\u5000", "")
                .replaceAll("\\u200b", "");
    }

    public static String replace_with_rule(String rule, String output) {

        try {
            JSONArray rule_array = new JSONArray(rule);
            int max_loop = rule_array.length();

            //because of JDK 7 bugs in Android
            output = output.replace("null", "\uFFFF\uFFFF");

            for (int i = 0; i < max_loop; i++) {

                JSONObject obj = rule_array.getJSONObject(i);
                String from = obj.getString("from");
                String to = obj.getString("to");

                output = output.replaceAll(from, to);
                output = output.replace("null", "");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        output = output.replace("\uFFFF\uFFFF", "null");
        return output;

    }

}
