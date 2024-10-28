package com.ats.tulukeyboard.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ats.tulukeyboard.R;
import com.ats.tulukeyboard.maokeyboard.MaoKeyboard;

public class Utils {
    private static boolean themeChange;
    private static boolean emojiKeyboard;
    private static MaoKeyboard keyboardBeforeChangeToEmoji;


    public static MaoKeyboard getKeyboardBeforeChangeToEmoji() {
        return keyboardBeforeChangeToEmoji;
    }

    public static void setKeyboardBeforeChangeToEmoji(MaoKeyboard keyboardBeforeChangeToEmoji) {
        Utils.keyboardBeforeChangeToEmoji = keyboardBeforeChangeToEmoji;
    }

    public static boolean isEmojiKeyboard() {
        return emojiKeyboard;
    }

    public static void setEmojiKeyboard(boolean emojiKeyboard) {
        Utils.emojiKeyboard = emojiKeyboard;
    }

    public static void setThemeChanged(boolean bool) {
        themeChange = bool;
    }

    public static boolean isThemeChanged() {
        return themeChange;
    }


    public static boolean isEnable(Context context, String name) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(name, false);
    }

    public static int getKeyboardTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPreferences.getString("chooseTheme", "1");
        return Integer.parseInt(theme);
    }

    public static int getThemeBackgroundResource(Context context) {
        return switch (PrefManager.getKeyboardTheme(context)) {
            case 3 -> R.drawable.dark_theme_keybackground;
            case 4 -> R.drawable.green_theme_keybackground;
            case 5 -> R.drawable.blue_theme_keybackground;
            case 6 -> R.drawable.skyblue_theme_keybackground;
            case 7 -> R.drawable.red_theme_keybackground;
            case 8 -> R.drawable.key_background_pink;
            case 9 -> R.drawable.key_background_violet;
            case 10 -> R.drawable.key_background_scarlet;
            case 11 -> R.drawable.key_background_dracula;
            // 0, 1, 2
            default -> R.drawable.key_background_tulu;
        };
    }

    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static ArrayList<Integer> initArrayList(int... ints) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i : ints) {
            list.add(i);
        }
        return list;
    }

    public static void justify(final TextView textView) {

        final AtomicBoolean isJustify = new AtomicBoolean(false);

        final String textString = textView.getText().toString();

        final TextPaint textPaint = textView.getPaint();

        final SpannableStringBuilder builder = new SpannableStringBuilder();

        textView.post(() -> {

            if (!isJustify.get()) {

                final int lineCount = textView.getLineCount();
                final int textViewWidth = textView.getWidth();

                for (int i = 0; i < lineCount; i++) {

                    int lineStart = textView.getLayout().getLineStart(i);
                    int lineEnd = textView.getLayout().getLineEnd(i);

                    String lineString = textString.substring(lineStart, lineEnd);

                    if (i == lineCount - 1) {
                        builder.append(new SpannableString(lineString));
                        break;
                    }

                    String trimSpaceText = lineString.trim();
                    String removeSpaceText = lineString.replaceAll(" ", "");

                    float removeSpaceWidth = textPaint.measureText(removeSpaceText);
                    float spaceCount = trimSpaceText.length() - removeSpaceText.length();

                    float eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount;

                    SpannableString spannableString = new SpannableString(lineString);
                    for (int j = 0; j < trimSpaceText.length(); j++) {
                        char c = trimSpaceText.charAt(j);
                        if (c == ' ') {
                            Drawable drawable = new ColorDrawable(0x00ffffff);
                            drawable.setBounds(0, 0, (int) eachSpaceWidth, 0);
                            ImageSpan span = new ImageSpan(drawable);
                            spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    builder.append(spannableString);
                }

                textView.setText(builder);
                isJustify.set(true);
            }
        });
    }

}
