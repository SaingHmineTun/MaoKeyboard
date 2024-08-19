package it.saimao.tulukeyboard.utils;

import static it.saimao.tulukeyboard.utils.Constants.ENABLE_KEY_SOUND;
import static it.saimao.tulukeyboard.utils.Constants.ENABLE_KEY_VIBRATION;
import static it.saimao.tulukeyboard.utils.Constants.KEYBOARD_THEME;

import android.content.Context;

public class PrefManager {
    private static final String NAME = "TMK Keybobard";

    public static boolean isEnabledKeyVibration(Context context) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY_VIBRATION, false);
    }

    public static void setEnabledKeyVibration(Context context, boolean value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_KEY_VIBRATION, value);
        editor.apply();
    }

    public static boolean isEnabledKeySound(Context context) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY_SOUND, false);
    }

    public static void setEnabledKeySound(Context context, boolean value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_KEY_SOUND, value);
        editor.apply();
    }

    public static int getKeyboardTheme(Context context) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEYBOARD_THEME, 0);
    }

    public static void setKeyboardTheme(Context context, int value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putInt(KEYBOARD_THEME, value);
        editor.apply();
    }

    public static void saveStringValue(Context context, String key, String value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringValue(Context context, String key) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "en");
    }

}
