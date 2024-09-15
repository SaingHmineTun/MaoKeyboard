package it.saimao.tmkkeyboard.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.adapters.LanguageAdapter;
import it.saimao.tmkkeyboard.databinding.ActivityChooseLanguageBinding;
import it.saimao.tmkkeyboard.utils.PrefManager;

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