package it.saimao.tmkkeyboard.maokeyboard;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputConnection;

import it.saimao.tmkkeyboard.utils.PrefManager;

public class ShanKeyboard extends MaoKeyboard {
    private static boolean swapConsonant = false;
    private static boolean swapMedial = false;
    private static final int MY_E = 4145;
    private static final int SH_E = 4228;
    private static final int ASAT = 4154;
    private static final int TEMP = 8203;
    private Context context;

    public ShanKeyboard(Context context, int xmlLayoutResId, String id) {
        super(context, xmlLayoutResId, id);
        this.context = context;
    }

    public ShanKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    public String handleShanInputText(int primaryCode, InputConnection ic) {
        CharSequence charBeforeCursor = ic.getTextBeforeCursor(1, 0);
        if ((primaryCode == MY_E || primaryCode == SH_E) && PrefManager.isEnabledHandWriting(context)) {
            char[] temp = {(char) TEMP, (char) primaryCode}; // ZWSP added
            String outText = String.valueOf(temp);
            swapConsonant = false;
            swapMedial = false;
            return outText;
        }
        // if getTextBeforeCursor return null, issues on version 1.1
        if (charBeforeCursor == null) {
            charBeforeCursor = "";
        }
        int charCodeBeforeCursor;
        if (charBeforeCursor.length() > 0) {
            charCodeBeforeCursor = charBeforeCursor.charAt(0);
        } else {
            return String.valueOf((char) primaryCode);
        }

        // Reorder ေႂ (ဢေသႂ်ႇ ၵွႆႈ)
        if (charCodeBeforeCursor == ASAT && primaryCode == 0x1082) {
            char[] temp = {(char) 0x1082, (char) ASAT};
            ic.deleteSurroundingText(1, 0);
            return String.valueOf(temp);
        }

        // Reorder  ႆၢ (ၵႆႇၶိုၼ်း ဢႃပွတ်း)
        if (charCodeBeforeCursor == 0x1086 && primaryCode == 0x1062) {
            char[] temp = {(char) 0x1062, (char) 0x1086};
            ic.deleteSurroundingText(1, 0);
            return String.valueOf(temp);
        }

        // Reorder ိူ (တိုတ်းသွင် တၢင်ႇလၢႆ)
        if (charCodeBeforeCursor == 0x1030 && primaryCode == 0x102D) {
            char[] temp = {(char) 0x102D, (char) 0x1030};
            ic.deleteSurroundingText(1, 0);
            return String.valueOf(temp);
        }

        // Reorder ို (တိုတ်းၼိုင်ႈ တၢင်ႇလၢႆ)
        if (charCodeBeforeCursor == 0x102F && primaryCode == 0x102D) {
            char[] temp = {(char) 0x102D, (char) 0x102F};
            ic.deleteSurroundingText(1, 0);
            return String.valueOf(temp);
        }
        if (PrefManager.isEnabledHandWriting(context)) return handleShanTyping(primaryCode, ic);
        return String.valueOf((char) primaryCode);
    }

    private String handleShanTyping(int primaryCode, InputConnection ic) {
        CharSequence charBeforeCursor = ic.getTextBeforeCursor(1, 0);
        if (charBeforeCursor == null) {
            charBeforeCursor = "";
        }

        if (isOthers(primaryCode)) {
            swapConsonant = false;
            swapMedial = false;
            return String.valueOf((char) primaryCode);
        }

        int charCodeBeforeCursor;
        if (charBeforeCursor.length() > 0) {
            charCodeBeforeCursor = charBeforeCursor.charAt(0);
        } else {
            swapConsonant = false;
            swapMedial = false;
            return String.valueOf((char) primaryCode);
        }

        if (charCodeBeforeCursor == MY_E || charCodeBeforeCursor == SH_E) {
            if (isConsonant(primaryCode)) {
                if (!swapConsonant) {
                    swapConsonant = true;
                    return reorderConsonantsWithZWSP(primaryCode, charCodeBeforeCursor, ic);
                } else {
                    swapConsonant = false;
                    swapMedial = false;
                    return String.valueOf((char) primaryCode);
                }
            }
            if (primaryCode == ASAT) {// ////CAUTION!!!!
                if (swapConsonant) {
                    swapConsonant = false;
                    return reorderConsonantsWithZWSP(primaryCode, charCodeBeforeCursor, ic);
                }
            }
            if (isMedial(primaryCode)) {// ////CAUTION!!!!
                if (!swapMedial && swapConsonant) {
                    swapMedial = true;
                    return reorderMedian(primaryCode, charCodeBeforeCursor, ic);
                }
            }
        }
        return String.valueOf((char) primaryCode);
    }

    private boolean isOthers(int primaryCode) {
        return switch (primaryCode) {
            // ၵၢႆႇၶိုၼ်း ၊ ယၵ်း ၊ ယၵ်းၸမ်ႈ ၊ ၸမ်ႈတႂ်ႈ ၊ ၸမ်ႈၶိုၼ်ႈ ၊ ၸမ်ႈတႂ်ႈမၢၼ်ႊ ၊ ၸမ်ႈၼႃႈ ၊ ဢႃပွတ်း ၊ ဢႃယၢဝ်း ၊ ၵွႆႈ
            case 0x1086, 0x1087, 0x1088, 0x1089, 0x108A, 0x1037, 0x1038, 0x1062, 0x1083, 0x1082 ->
                    true;
            default -> false;
        };
    }

    private boolean isMedial(int primaryCode) {
        // medial Ya, Ra (ႁွပ်ႇ၊ လဵပ်ႈ)
        return primaryCode == 0x103B || primaryCode == 0x103C;
    }

    private String reorderConsonantsWithZWSP(int primaryCode, int charcodeBeforeCursor, InputConnection ic) {
        ic.deleteSurroundingText(2, 0);
        char[] reorderChars = {(char) primaryCode, (char) charcodeBeforeCursor};
        return String.valueOf(reorderChars);
    }

    private String reorderMedian(int primaryCode, int charcodeBeforeCursor, InputConnection ic) {
        ic.deleteSurroundingText(1, 0);
        char[] reorderChars = {(char) primaryCode, (char) charcodeBeforeCursor};
        return String.valueOf(reorderChars);
    }

    public void handleShanDelete(InputConnection ic) {
        if (PrefManager.isEnabledHandWriting(context)) {
            handleSingleDelete(ic);
        } else {
            MaoKeyboardService.deleteHandle(ic);
        }
    }

    private void handleSingleDelete(InputConnection ic) {
        CharSequence getTextBeforeChar = ic.getTextBeforeCursor(1, 0);
        // if getTextBeforeCursor return null, issues on version 1.1
        if (getTextBeforeChar == null) {
            getTextBeforeChar = "";
        }

        int firstChar;
        int secPrev;
        if (getTextBeforeChar.length() > 0) {
            firstChar = getTextBeforeChar.charAt(0);
            if (firstChar == MY_E || firstChar == SH_E) {
                // Need to initialize FLAG
                swapConsonant = false;
                swapMedial = false;
                getTextBeforeChar = ic.getTextBeforeCursor(2, 0);
                secPrev = getTextBeforeChar.charAt(0);
                if (isMedial(secPrev)) {
                    swapConsonant = true;
                    swapMedial = false;
                    deleteCharBeforeMedian(firstChar, ic);
                } else if (isConsonant(secPrev)) {
                    swapMedial = false;
                    swapConsonant = false;
                    deleteCharBeforeConsonant(firstChar, ic);
                } else if (secPrev == TEMP) {
                    deleteCharWithZWSP(ic);
                } else {
                    ic.deleteSurroundingText(1, 0);
                }

            } else {
                getTextBeforeChar = ic.getTextBeforeCursor(2, 0);
                secPrev = (int) getTextBeforeChar.charAt(0);
                CharSequence getThirdText = ic.getTextBeforeCursor(3, 0);
                int thirdChar = 0;
                if (getThirdText != null && getThirdText.length() == 3)
                    thirdChar = getThirdText.charAt(0);

                if (secPrev == MY_E || secPrev == SH_E)
                    swapConsonant = thirdChar != TEMP;
                // ic.deleteSurroundingText(1, 0);
                MaoKeyboardService.deleteHandle(ic);
            }
        } else {
            ic.deleteSurroundingText(1, 0);
        }
    }

    private void deleteCharBeforeMedian(int firstChar, InputConnection ic) {

        ic.deleteSurroundingText(2, 0);
        ic.commitText(String.valueOf((char) firstChar), 1);
    }

    private void deleteCharWithZWSP(InputConnection ic) {
        ic.deleteSurroundingText(2, 0);
    }

    private void deleteCharBeforeConsonant(int firstChar, InputConnection ic) {
        ic.deleteSurroundingText(2, 0);
        ic.commitText(String.valueOf(new char[]{(char) 8203, (char) firstChar}), 1);
    }


    private boolean isConsonant(int primaryCode) {
        return MaoKeyboardService.isShanConsonant(primaryCode);
    }

    public String shanVowel1() {
        char[] outText = {(char) 0x1082, (char) 4154};
        return String.valueOf(outText);
    }

    public void handleShanMoneySym(InputConnection ic) {
        char[] temp = {4117, 0x103B, 4227, 4152};
        ic.commitText(String.valueOf(temp), 1);
    }

}
