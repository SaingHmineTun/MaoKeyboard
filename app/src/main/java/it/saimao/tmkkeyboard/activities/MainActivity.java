package it.saimao.tmkkeyboard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import it.saimao.tmkkeyboard.databinding.ActivityMainBinding;
import it.saimao.tmkkeyboard.maoconverter.MaoConverterService;
import it.saimao.tmkkeyboard.utils.PrefManager;
import it.saimao.tmkkeyboard.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding.cvEnablePopupConverter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledPopupConverter(getApplicationContext(), isChecked);
            if (isChecked) {
                startService(new Intent(this, MaoConverterService.class));
            } else {
                stopService(new Intent(this, MaoConverterService.class));
            }
        });

        binding.cvEnableKeyVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("TMK Group", "Enable Key Vibration");
            PrefManager.setEnabledKeyVibration(getApplicationContext(), isChecked);
            Utils.setUpdateSharedPreference(true);
        });

        binding.cvEnableKeySound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeySound(getApplicationContext(), isChecked);
            Utils.setUpdateSharedPreference(true);
        });
        binding.cbEnableHandwriting.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledHandWriting(getApplicationContext(), isChecked);
            Utils.setUpdateSharedPreference(true);
        });

        binding.cvChooseTheme.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ChooseThemeActivity.class));
        });
        binding.cvAbout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        });
        binding.cvTestKeyboard.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TestKeyboardActivity.class));
        });
    }
}