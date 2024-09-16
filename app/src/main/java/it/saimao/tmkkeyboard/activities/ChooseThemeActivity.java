package it.saimao.tmkkeyboard.activities;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.adapters.Theme;
import it.saimao.tmkkeyboard.adapters.ThemeAdapter;
import it.saimao.tmkkeyboard.databinding.ActivityChooseThemeBinding;
import it.saimao.tmkkeyboard.utils.PrefManager;
import it.saimao.tmkkeyboard.utils.Utils;

public class ChooseThemeActivity extends AppCompatActivity {

    private ActivityChooseThemeBinding binding;
    private ThemeAdapter themeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseThemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
    }

    private void initUi() {
        themes = List.of(
                new Theme(getString(R.string.dark), R.drawable.theme_dark),
                new Theme(getString(R.string.green), R.drawable.theme_material_green),
                new Theme(getString(R.string.blue), R.drawable.theme_sky_blue),
                new Theme(getString(R.string.cyan), R.drawable.theme_cyan),
                new Theme(getString(R.string.gold), R.drawable.theme_red_danger),
                new Theme(getString(R.string.pink), R.drawable.theme_lovely_pink),
                new Theme(getString(R.string.violet), R.drawable.theme_violet),
                new Theme(getString(R.string.scarlet), R.drawable.theme_scarlet),
                new Theme(getString(R.string.dracula), R.drawable.theme_dracula),
                new Theme(getString(R.string.tmk), R.drawable.theme_mlh)
        );
        themeAdapter = new ThemeAdapter(theme -> {
            var selected = themes.indexOf(theme);
            PrefManager.setKeyboardTheme(this, selected);
            refreshThemes();
            Utils.setThemeChanged(true);
        });
        binding.rvThemes.setAdapter(themeAdapter);
        binding.rvThemes.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        refreshThemes();

    }


    private List<Theme> themes;

    private void refreshThemes() {
        ArrayList<Theme> themes = new ArrayList<>(this.themes);
        int selected = PrefManager.getKeyboardTheme(this);
        Theme selectedTheme = themes.get(selected);
        Theme newTheme = new Theme(selectedTheme);
        newTheme.setSelected(true);
        themes.set(selected, newTheme);
        themeAdapter.setThemes(themes);
    }

}