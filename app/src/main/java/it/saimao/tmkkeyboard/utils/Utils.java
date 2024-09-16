package it.saimao.tmkkeyboard.utils;

import android.app.ActivityManager;
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

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.maokeyboard.MaoKeyboard;

public class Utils {
    private static boolean stopCopyDialog;
    private static boolean themeChange;
    private static boolean emojiKeyboard;
    private static boolean doubleTapOn, changingDoubleTap, updateSharedPreference;
    private static MaoKeyboard keyboardBeforeChangeToEmoji;
    private static final int[] codesToBeReordered = {4155, 4156, 4157, 4158};

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

    public static boolean isStopCopyDialog() {
        return stopCopyDialog;
    }

    public static void setStopCopyDialog(boolean stopCopyDialog) {
        Utils.stopCopyDialog = stopCopyDialog;
    }

    public static void setEnabledConvertFromFb(Context context, String name, boolean bool) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MaoSharedPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, bool);
        editor.apply();
    }

    public static void setThemeChanged(boolean bool) {
        themeChange = bool;
    }

    public static boolean isThemeChanged() {
        return themeChange;
    }

    public static boolean isEnabledConvertFromFb(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MaoSharedPreference", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(name, false);
    }

    public static boolean isMyanmarConsonant(int code) {

        return (code >= 4096 && code <= 4130) || (code >= 4213 && code <= 4225 || code == 43617 || code == 43491 || code == 43626 || code == 43488 || code == 43630);
    }

    public static boolean isEnable(Context context, String name) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(name, false);
    }

    public static boolean isMyServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static int getKeyboardTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = sharedPreferences.getString("chooseTheme", "1");
        return Integer.parseInt(theme);
    }

    public static int getThemeBackgroundResource(Context context) {
        return switch (PrefManager.getKeyboardTheme(context)) {
            case 0 -> R.drawable.dark_theme_keybackground;
            case 1 -> R.drawable.green_theme_keybackground;
            case 2 -> R.drawable.blue_theme_keybackground;
            case 3 -> R.drawable.skyblue_theme_keybackground;
            case 4 -> R.drawable.key_background_wood;
            case 5 -> R.drawable.key_background_pink;
            case 6 -> R.drawable.key_background_violet;
            case 7 -> R.drawable.key_background_scarlet;
            case 8 -> R.drawable.key_background_dracula;
            default -> R.drawable.key_background_mlh;
        };
    }

    public static boolean isChangingDoubleTap() {
        return changingDoubleTap;
    }

    public static void setChangingDoubleTap(boolean changingDoubleTap) {
        Utils.changingDoubleTap = changingDoubleTap;
    }

    public static boolean isDoubleTapOn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("enableDoubleTap", false);
    }

    public static boolean isCodeToBeReordered(int code) {
        for (int codeToBeReordered : codesToBeReordered) {
            if (codeToBeReordered == code) {
                return true;
            }
        }
        return false;
    }


    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static ArrayList<Integer> initArrayList(int... ints) {
        ArrayList<Integer> list = new ArrayList<Integer>();
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
