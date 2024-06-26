package it.saimao.tmkkeyboard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import it.saimao.tmkkeyboard.databinding.ActivityMainBinding;
import it.saimao.tmkkeyboard.maoconverter.MaoConverterService;
import it.saimao.tmkkeyboard.utils.PrefManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
        initListeners();
    }

    private void initUi() {
        binding.cvEnablePopupConverter.setChecked(PrefManager.isEnablePopupConverter(this));
        binding.cvEnableKeyVibration.setChecked(PrefManager.isEnabledKeyVibration(this));
        binding.cvEnableKeySound.setChecked(PrefManager.isEnabledKeySound(this));
        binding.cbEnableHandwriting.setChecked(PrefManager.isEnabledHandWriting(this));
    }

    private void initListeners() {
        binding.cvEnableKeyboard.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
        });
        binding.cvChooseKeyboard.setOnClickListener(v -> {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showInputMethodPicker();
        });
        binding.cvEnablePopupConverter.setOnClickListener(v -> {
            var isChecked = !PrefManager.isEnablePopupConverter(this);
            PrefManager.setEnabledPopupConverter(this, isChecked);
            if (isChecked) {
                startService(new Intent(this, MaoConverterService.class));
            } else {
                stopService(new Intent(this, MaoConverterService.class));
            }
        });

        binding.cvEnableKeyVibration.setOnClickListener(v -> {
            var isChecked = !PrefManager.isEnabledKeyVibration(this);
            PrefManager.setEnabledKeyVibration(this, isChecked);
        });

        binding.cvEnableKeySound.setOnClickListener(v -> {
            var isChecked = !PrefManager.isEnabledKeySound(this);
            PrefManager.setEnabledKeySound(this, isChecked);
        });

        binding.cvChooseTheme.setOnClickListener(v -> {
            startActivity(new Intent(this, ChooseThemeActivity.class));
        });
        binding.cvAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });
    }
}