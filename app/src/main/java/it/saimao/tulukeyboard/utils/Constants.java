package it.saimao.tulukeyboard.utils;

import java.util.List;

import it.saimao.tulukeyboard.R;
import it.saimao.tulukeyboard.adapters.Theme;

public class Constants {
    public static final String ENABLE_KEY_VIBRATION = "key_vibration";
    public static final String ENABLE_KEY_SOUND = "key_sound";
    public static final String ENABLE_HAND_WRITING = "hand_writing";
    public static final String ENABLE_POPUP_CONVERTER = "popup_converter";
    public static final String KEYBOARD_THEME = "keyboard_theme";
    public static final String APP_LANGUAGE = "app_language";
    public static final List<Theme> THEME_LIST = List.of(
            new Theme("Tulu", R.drawable.theme_tulu),
            new Theme("Dark", R.drawable.theme_dark),
            new Theme("Material Green", R.drawable.theme_material_green),
            new Theme("Sky Blue", R.drawable.theme_sky_blue),
            new Theme("Cyan", R.drawable.theme_cyan),
            new Theme("Red Danger", R.drawable.theme_red_danger),
            new Theme("Lovely Pink", R.drawable.theme_lovely_pink),
            new Theme("Violet", R.drawable.theme_violet),
            new Theme("Scarlet", R.drawable.theme_scarlet),
            new Theme("Dracula", R.drawable.theme_dracula)
    );
}
