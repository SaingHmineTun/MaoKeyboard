package it.saimao.tmkkeyboard.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.maokeyboard.MaoKeyboard;

public class Utils {
    private static boolean stopCopyDialog;
    private static boolean themeChange;
    private static boolean emojiKeyboard;
    private static boolean doubleTapOn, changingDoubleTap, updateSharedPreference;
    private static MaoKeyboard keyboardBeforeChangeToEmoji;
    private static final int[] codesToBeReordered = {4155, 4156, 4157, 4158};

    public static boolean isUpdateSharedPreference() {
        return updateSharedPreference;
    }

    public static void setUpdateSharedPreference(boolean updateSharedPreference) {
        Utils.updateSharedPreference = updateSharedPreference;
    }

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
            case 4 -> R.drawable.red_theme_keybackground;
            case 5 -> R.drawable.pink_theme_keybackground;
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

}
