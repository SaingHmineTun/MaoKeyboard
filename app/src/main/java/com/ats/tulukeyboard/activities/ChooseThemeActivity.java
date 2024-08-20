package com.ats.tulukeyboard.activities;


import static com.ats.tulukeyboard.utils.Constants.THEME_LIST;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ats.tulukeyboard.databinding.ActivityChooseThemeBinding;

import java.util.ArrayList;

import com.ats.tulukeyboard.adapters.Theme;
import com.ats.tulukeyboard.adapters.ThemeAdapter;
import com.ats.tulukeyboard.utils.PrefManager;
import com.ats.tulukeyboard.utils.Utils;

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
        themeAdapter = new ThemeAdapter(theme -> {
            var selected = THEME_LIST.indexOf(theme);
            PrefManager.setKeyboardTheme(this, selected);
            refreshThemes();
            Utils.setThemeChanged(true);
        });
        binding.rvThemes.setAdapter(themeAdapter);
        binding.rvThemes.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        refreshThemes();

    }

    private void refreshThemes() {
        ArrayList<Theme> themes = new ArrayList<>(THEME_LIST);
        int selected = PrefManager.getKeyboardTheme(this);
        Theme selectedTheme = themes.get(selected);
        Theme newTheme = new Theme(selectedTheme);
        newTheme.setSelected(true);
        themes.set(selected, newTheme);
        themeAdapter.setThemes(themes);
    }

}