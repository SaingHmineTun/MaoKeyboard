package it.saimao.tulukeyboard.activities;

import static it.saimao.tulukeyboard.utils.Constants.THEME_LIST;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import it.saimao.tulukeyboard.R;
import it.saimao.tulukeyboard.adapters.Theme;
import it.saimao.tulukeyboard.adapters.ThemeAdapter;
import it.saimao.tulukeyboard.databinding.ActivityChooseThemeBinding;
import it.saimao.tulukeyboard.utils.PrefManager;
import it.saimao.tulukeyboard.utils.Utils;

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
        Log.d("TMK Group", "" + (selectedTheme == newTheme));
        newTheme.setSelected(true);
        themes.set(selected, newTheme);
        themeAdapter.setThemes(themes);
    }

}