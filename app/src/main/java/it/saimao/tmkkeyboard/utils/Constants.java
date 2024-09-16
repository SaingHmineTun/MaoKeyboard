package it.saimao.tmkkeyboard.utils;

import java.util.List;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.adapters.Theme;

public class Constants {

    public static final String[] LANGUAGES = {"en_GB", "mm_MM", "shn_MM", "taile_MM", "th_TH", "khamti_MM", "tham_TH", "tai_lue_TH", "tai_dam_VN", "tai_IN"};


    public static final String SHARED_PREFERENCE_NAME = "tmk_keyboard";
    public static final String ENABLE_KEY_VIBRATION = "key_vibration";
    public static final String ENABLE_KEY_SOUND = "key_sound";
    public static final String ENABLE_KEY_PREVIEW = "key_preview";
    public static final String ENABLE_HAND_WRITING = "hand_writing";
    public static final String ENABLE_POPUP_CONVERTER = "popup_converter";
    public static final String KEYBOARD_THEME = "keyboard_theme";
    public static final String APP_LANGUAGE = "app_language";
    public static final String FONT_CONVERTER = "font_converter";
    public static final String TAILE_CONVERTER = "taile_converter";
    public static final String SHAN_TRANSLIT = "shan_translit";
    public static final List<Theme> THEME_LIST = List.of(
            new Theme("Dark", R.drawable.theme_dark),
            new Theme("Green", R.drawable.theme_material_green),
            new Theme("Sky Blue", R.drawable.theme_sky_blue),
            new Theme("Cyan", R.drawable.theme_cyan),
            new Theme("Wooden", R.drawable.theme_red_danger),
            new Theme("Pink", R.drawable.theme_lovely_pink),
            new Theme("Violet", R.drawable.theme_violet),
            new Theme("Scarlet", R.drawable.theme_scarlet),
            new Theme("Dracula", R.drawable.theme_dracula),
            new Theme("MLH", R.drawable.theme_mlh)
    );
}
