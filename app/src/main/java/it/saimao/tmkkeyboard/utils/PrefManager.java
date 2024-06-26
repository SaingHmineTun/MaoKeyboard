package it.saimao.tmkkeyboard.utils;

import static it.saimao.tmkkeyboard.utils.Constants.ENABLE_HAND_WRITING;
import static it.saimao.tmkkeyboard.utils.Constants.ENABLE_KEY_SOUND;
import static it.saimao.tmkkeyboard.utils.Constants.ENABLE_KEY_VIBRATION;
import static it.saimao.tmkkeyboard.utils.Constants.ENABLE_POPUP_CONVERTER;
import static it.saimao.tmkkeyboard.utils.Constants.KEYBOARD_THEME;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String NAME = "TMK Keybobard";

    public static boolean isEnabledKeyVibration(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY_VIBRATION, false);
    }

    public static void setEnabledKeyVibration(Context context, boolean value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_KEY_VIBRATION, value);
        editor.apply();
    }

    public static boolean isEnabledKeySound(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY_SOUND, false);
    }

    public static void setEnabledKeySound(Context context, boolean value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_KEY_SOUND, value);
        editor.apply();
    }

    public static boolean isEnabledHandWriting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_HAND_WRITING, false);
    }

    public static void setEnabledHandWriting(Context context, boolean value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_HAND_WRITING, value);
        editor.apply();
    }

    public static boolean isEnablePopupConverter(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_POPUP_CONVERTER, false);
    }

    public static void setEnabledPopupConverter(Context context, boolean value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putBoolean(ENABLE_POPUP_CONVERTER, value);
        editor.apply();
    }

    public static int getKeyboardTheme(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(KEYBOARD_THEME, 0);
    }

    public static void setKeyboardTheme(Context context, int value) {
        var sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        var editor = sp.edit();
        editor.putInt(KEYBOARD_THEME, value);
        editor.apply();
    }

}
