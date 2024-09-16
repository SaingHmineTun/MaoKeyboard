package it.saimao.tmktaikeyboard.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.adapters.LanguageAdapter;
import it.saimao.tmktaikeyboard.databinding.ActivityChooseLanguageBinding;
import it.saimao.tmktaikeyboard.utils.PrefManager;

public class ChooseLanguageActivity extends AppCompatActivity {

    private ActivityChooseLanguageBinding binding;
    private LanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
    }

    private void initUi() {
        languageAdapter = new LanguageAdapter(this, (key, isChecked) -> {
            PrefManager.setEnabledLanguage(ChooseLanguageActivity.this, key, isChecked);
        });
        binding.rvLanguages.setAdapter(languageAdapter);
        binding.rvLanguages.setLayoutManager(new GridLayoutManager(this, 2));
    }
}