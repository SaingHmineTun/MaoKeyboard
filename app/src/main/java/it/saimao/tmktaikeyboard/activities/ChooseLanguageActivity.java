package it.saimao.tmktaikeyboard.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.adapters.LanguageAdapter;
import it.saimao.tmktaikeyboard.databinding.ActivityChooseLanguageBinding;
import it.saimao.tmktaikeyboard.utils.PrefManager;
import it.saimao.tmktaikeyboard.utils.Utils;

public class ChooseLanguageActivity extends AppCompatActivity {

    private ActivityChooseLanguageBinding binding;
    private LanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Utils.initLanguage(this);
        binding = ActivityChooseLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
    }

    private void initUi() {
        languageAdapter = new LanguageAdapter(this, (key, isChecked) -> {
            PrefManager.setEnabledLanguage(ChooseLanguageActivity.this, key, isChecked);
            // Refresh the adapter to update UI
            languageAdapter.refresh();
        });
        binding.rvLanguages.setAdapter(languageAdapter);
        binding.rvLanguages.setLayoutManager(new GridLayoutManager(this, 2));
    }
}