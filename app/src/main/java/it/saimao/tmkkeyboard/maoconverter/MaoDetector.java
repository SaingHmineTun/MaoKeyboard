package it.saimao.tmkkeyboard.maoconverter;

import android.content.Context;

import it.saimao.tmkkeyboard.zawgyidetector.ZawgyiDetector;

public class MaoDetector {


    private static ZawgyiDetector detector;
    private static String shanCharacters = "\u1075\u1076\u1077\u1078\u1079\u107A\u107B\u107C\u107D\u107E\u107F\u1080\u1081";

    public static boolean isLeikTaiMao(String input) {
        int confirm = 0;
        for (int i = 0; i < input.length(); i++) {
            int character = input.charAt(i);
            if (character >= 6480 && character < 6520) {
                confirm++;
            }
            if (confirm > 5) return true;
        }
        return confirm > 0;
    }

    public static boolean isShanZawgyi(String text) {
        for (char ch : text.toCharArray()) {
            if (ch >= 0xAA00 && ch <= 0xAA5F) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBurmeseZawgyi(Context context, String text) {
        if (detector == null) detector = new ZawgyiDetector(context);
        double score = detector.getZawgyiProbability(text);
        return score > .8;
    }

    public static boolean isShanUnicode(String text) {
        for (char ch : text.toCharArray()) {
            if ((ch >= 0x1075 && ch <= 0x108A) || ch == 0x1022) {
                return true;
            }
        }
        return false;
    }
}



