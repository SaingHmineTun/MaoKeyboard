package it.saimao.tmkkeyboard.maoconverter;

public class ShanZawgyiConverter {
    public static String uni2zg(String input) {

        return input
                // For asai and esai
                .replaceAll("([\\u1000-\\u1021\\u1075-\\u1081\\u1022\\u108f\\u1029\\u106e\\u106f\\u1086\\u1090\\u1091\\u1092\\u1097])((?:\\u1039[\\u1000-\\u1021])?)((?:[\\u103b-\\u103e\\u1087]*)?)\\u1031", "\u1031$1$2$3")
                .replaceAll("([\\u1000-\\u1021\\u1075-\\u1081\\u1022\\u108f\\u1029\\u106e\\u106f\\u1086\\u1090\\u1091\\u1092\\u1097])((?:\\u1039[\\u1000-\\u1021])?)((?:[\\u103b-\\u103e\\u1087]*)?)\\u1084", "\uaa2c$1$2$3")

                /*
                သ + ြ    ႁႂ်ႈပိၼ်ႇပဵၼ် ြ  + သ
                တႃႇတႆးၼႆႉ မၼ်းတေမီး တြႃး ၽြႃး ၵွၺ်း
                 */
                .replaceAll("([\\u1000-\\u1021\\u1075-\\u1081\\u1022\\u108f\\u1029\\u106e\\u106f\\u1086\\u1090\\u1091\\u1092\\u1097])(\\u103c)", "$2$1")

                // Shan consonants omit Burma ones
                .replaceAll("\\u1075", "\uaa00")
                .replaceAll("\\u1076", "\uaa01")
                .replaceAll("\\u1078", "\uaa05")
                .replaceAll("\\u107a", "\uaa09")
                .replaceAll("\\u107c", "\uaa13")
                .replaceAll("\\u107d", "\uaa15")
                .replaceAll("\\u107e", "\uaa18")
                .replaceAll("\\u1081", "\uaa1f")
                .replaceAll("\\u1022", "\uaa21")

                // Vowels and others
                .replaceAll("\\u1087", "\uaa32")
                .replaceAll("\\u1088", "\uaa33")
                .replaceAll("\\u1089", "\uaa35")
                .replaceAll("\\u108a", "\uaa36")
                .replaceAll("\\u1084", "\uaa2c")
                .replaceAll("\\u1035", "\uaa31")
                .replaceAll("\\u1085", "\uaa30")
                .replaceAll("\\u1062", "\uaa24")
                .replaceAll("\\u1083", "\uaa23")
                .replaceAll("\\u1082", "\uaa2e")
                .replaceAll("\\u1086", "\uaa2f")
                // Shan Numbers
                .replaceAll("\\u1091", "\uaa3c")
                .replaceAll("\\u1092", "\uaa3d")
                .replaceAll("\\u1093", "\uaa3e")
                .replaceAll("\\u1094", "\uaa3f")
                .replaceAll("\\u1095", "\uaa40")
                .replaceAll("\\u1096", "\uaa41")
                .replaceAll("\\u1097", "\uaa42")
                .replaceAll("\\u1098", "\uaa43")
                .replaceAll("\\u1099", "\uaa44")
                .replaceAll("\\u1090", "\uaa3b")
                // Pali characters for Shan unicode
                .replaceAll("\\uaa61", "\uaa07")
                .replaceAll("\\u107b", "\uaa11")
                .replaceAll("\\ua9e3", "\uaa0e")
                .replaceAll("\\ua9e3", "\uaa0e")
                .replaceAll("\\u107f", "\uaa16")
                .replaceAll("\\u1077", "\uaa02")
                .replaceAll("\\uaa6a", "\uaa12")
                .replaceAll("\\ua9e0", "\uaa03")
                .replaceAll("\\uaa6e", "\uaa20")

                // For others

                .replaceAll("\\u103a", "\u1039")
                .replaceAll("\\u103b", "\u103a")
                // တြ ၊ သြ ၊ ၽြ ၊
                .replaceAll("\\u103c([\\u1010\\u1011\\u101e\\uaa05\\uaa15\\uaa17])", "\u107e$1")
                // ၵြႃး ၊ မြႃး ၊ ၶြႃး ၊ ပြႃး ၊ မြႃး ၊
                .replaceAll("\\u103c([\\uaa00\\uaa01\\uaa02\\uaa03\\u1015\\u1019\\u1004])", "\u103b$1")
                .replaceAll("\\u103d", "\u103c")
                .replaceAll("\\u103e", "\u103d")

                .trim();
    }

    public static String zg2uni(String input) {
        return input
                // ြတ ၊ ြၽ - တြ ၊ ၽြ
                .replaceAll("\\u103c", "\u103d")
                .replaceAll("([\\u107e\\u103b])([\\u1000-\\u1022\\uaa00-\\uaa21])", "$2\u103c")
                .replaceAll("\\u103a", "\u103b")
                .replaceAll("\\u1039", "\u103a")
                .replaceAll("([\\u1094\\u1095])", "\u1037")
                .replaceAll("\\u1031\\u1047", "\u1031\u101b")
                .replaceAll("\\u1040(\\u102e|\\u102f|\\u102d\\u102f|\\u1030|\\u1036|\\u103d|\\u103e)", "\u101d$1")
                .replaceAll("(\\u1031)([\\u1000-\\u1022\\uaa00-\\uaa21])(\\u103b)", "$2$3$1")
                .replaceAll("(\\u1031)([\\u1000-\\u1022\\uaa00-\\uaa21])", "$2$1")
                .replaceAll("(\\uaa2c)([\\u1000-\\u1022\\uaa00-\\uaa21])", "$2$1")
                // ( ိ ၊ ီ ) + ြ   = ြ  + ( ိ ၊ ီ )
                .replaceAll("([\\u102d\\u102e])(\\u103b)", "$2$1")
                .replaceAll("([\\u102f\\u1030])([\\u102d\\u102e])", "$2$1")
                .replaceAll("\\u102d+", "\u102d")
                .replaceAll("\\u103a+", "\u103a")
                .replaceAll("\\u102e+", "\u102e")
                .replaceAll("\\u102f\\u102d", "\u102d\u102f")


                // ၵြႃး ၊ မြႃး ၊ ၶြႃး ၊ ပြႃး ၊ မြႃး ၊
//                .replaceAll("\\u103c([\\uaa00\\uaa01\\uaa02\\uaa03\\u1015\\u1019\\u1004])", "\u103b$1")

                .replaceAll("\\uaa00", "\u1075")
                .replaceAll("\\uaa01", "\u1076")
                .replaceAll("\\uaa05", "\u1078")
                .replaceAll("\\uaa09", "\u107a")
                .replaceAll("\\uaa13", "\u107c")
                .replaceAll("\\uaa15", "\u107d")
                .replaceAll("\\uaa18", "\u107e")
                .replaceAll("\\uaa1f", "\u1081")
                .replaceAll("\\uaa21", "\u1022")
                .replaceAll("\\uaa32", "\u1087")
                .replaceAll("\\uaa33", "\u1088")
                .replaceAll("\\uaa35", "\u1089")
                .replaceAll("\\uaa36", "\u108a")
                .replaceAll("\\uaa2c", "\u1084")
                .replaceAll("\\uaa31", "\u1035")
                .replaceAll("\\uaa30", "\u1085")
                .replaceAll("\\uaa24", "\u1062")
                .replaceAll("\\uaa23", "\u1083")
                .replaceAll("\\uaa2e", "\u1082")
                .replaceAll("\\uaa2f", "\u1086")
                .replaceAll("\\uaa3c", "\u1091")
                .replaceAll("\\uaa3d", "\u1092")
                .replaceAll("\\uaa3e", "\u1093")
                .replaceAll("\\uaa3f", "\u1094")
                .replaceAll("\\uaa40", "\u1095")
                .replaceAll("\\uaa41", "\u1096")
                .replaceAll("\\uaa42", "\u1097")
                .replaceAll("\\uaa43", "\u1098")
                .replaceAll("\\uaa44", "\u1099")
                .replaceAll("\\uaa3b", "\u1090")
                .replaceAll("\\uaa07", "\uaa61")
                .replaceAll("\\uaa11", "\u107b")
                .replaceAll("\\uaa0e", "\ua9e3")
                .replaceAll("\\uaa02", "\u1077")
                .replaceAll("\\uaa12", "\uaa6a")
                .replaceAll("\\uaa03", "\ua9e0")
                .replaceAll("\\uaa20", "\uaa6e")
                .trim();
    }

}
