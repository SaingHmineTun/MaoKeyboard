package it.saimao.tmkkeyboard.utils;

import java.util.List;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.adapters.Theme;

public class Constants {
    public static final String ENABLE_KEY_VIBRATION = "key_vibration";
    public static final String ENABLE_KEY_SOUND = "key_sound";
    public static final String ENABLE_HAND_WRITING = "hand_writing";
    public static final String ENABLE_POPUP_CONVERTER = "popup_converter";
    public static final String KEYBOARD_THEME = "keyboard_theme";
    public static final List<Theme> THEME_LIST = List.of(
            new Theme("Dracular", R.drawable.theme_dracula, false),
            new Theme("Material Green", R.drawable.theme_material_green, false),
            new Theme("Sky Blue", R.drawable.theme_sky_blue, false),
            new Theme("Cyan", R.drawable.theme_cyan, false),
            new Theme("Red Danger", R.drawable.theme_red_danger, false),
            new Theme("Lovely Pink", R.drawable.theme_lovely_pink, false));
}
