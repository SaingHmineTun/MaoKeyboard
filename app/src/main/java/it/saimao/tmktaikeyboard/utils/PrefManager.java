package it.saimao.tmktaikeyboard.utils;

import static it.saimao.tmktaikeyboard.utils.Constants.ENABLE_HAND_WRITING;
import static it.saimao.tmktaikeyboard.utils.Constants.ENABLE_KEY_PREVIEW;
import static it.saimao.tmktaikeyboard.utils.Constants.ENABLE_KEY_SOUND;
import static it.saimao.tmktaikeyboard.utils.Constants.ENABLE_KEY_VIBRATION;
import static it.saimao.tmktaikeyboard.utils.Constants.ENABLE_POPUP_CONVERTER;
import static it.saimao.tmktaikeyboard.utils.Constants.KEYBOARD_THEME;
import static it.saimao.tmktaikeyboard.utils.Constants.SHARED_PREFERENCE_NAME;

import android.content.Context;

public class PrefManager {


    public static boolean isEnabledKeyVibration(Context context) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY_VIBRATION, false);
    }

    public static void setEnabledKeyVibration(Context context, boolean value) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_KEY_VIBRATION, value);
        editor.apply();
    }

    public static boolean isEnabledKeyPreview(Context context) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY_PREVIEW, false);
    }

    public static void setEnabledKeyPreview(Context context, boolean value) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_KEY_PREVIEW, value);
        editor.apply();
    }

    public static boolean isEnabledKeySound(Context context) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY_SOUND, false);
    }

    public static void setEnabledKeySound(Context context, boolean value) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_KEY_SOUND, value);
        editor.apply();
    }

    public static boolean isEnabledHandWriting(Context context) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_HAND_WRITING, false);
    }

    public static void setEnabledHandWriting(Context context, boolean value) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_HAND_WRITING, value);
        editor.apply();
    }

    public static boolean isEnablePopupConverter(Context context) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_POPUP_CONVERTER, false);
    }

    public static void setEnabledPopupConverter(Context context, boolean value) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_POPUP_CONVERTER, value);
        editor.apply();
    }

    public static int getKeyboardTheme(Context context) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        // Prevent getting -1 as index
        var index = sp.getInt(KEYBOARD_THEME, 0);
        return Math.max(index, 0);
    }

    public static void setKeyboardTheme(Context context, int value) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putInt(KEYBOARD_THEME, value);
        editor.apply();
    }

    public static void saveStringValue(Context context, String key, String value) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringValue(Context context, String key) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "en");
    }

    public static boolean isEnabledLanguage(Context context, String key) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (key.equals("en_GB")) return sp.getBoolean(key, true);
        return sp.getBoolean(key, false);
    }

    public static void setEnabledLanguage(Context context, String key, boolean isChecked) {
        var sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        if (!key.equals("en_GB")) {
            editor.putBoolean(key, isChecked);
        }
        editor.apply();
    }

}
