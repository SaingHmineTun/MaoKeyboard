package it.saimao.tulukeyboard.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

import it.saimao.tulukeyboard.R;
import it.saimao.tulukeyboard.maokeyboard.MaoKeyboard;

public class Utils {
    private static boolean themeChange;
    private static boolean emojiKeyboard;
    private static boolean updateSharedPreference;
    private static MaoKeyboard keyboardBeforeChangeToEmoji;

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
            case 1 -> R.drawable.dark_theme_keybackground;
            case 2 -> R.drawable.green_theme_keybackground;
            case 3 -> R.drawable.blue_theme_keybackground;
            case 4 -> R.drawable.skyblue_theme_keybackground;
            case 5 -> R.drawable.red_theme_keybackground;
            case 6 -> R.drawable.pink_theme_keybackground;
            case 7 -> R.drawable.key_background_violet;
            case 8 -> R.drawable.key_background_scarlet;
            case 9 -> R.drawable.key_background_dracula;
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

}
