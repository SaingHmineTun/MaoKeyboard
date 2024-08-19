package it.saimao.tulukeyboard.activities;

import static it.saimao.tulukeyboard.utils.Constants.APP_LANGUAGE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import it.saimao.tulukeyboard.R;
import it.saimao.tulukeyboard.databinding.ActivityMainBinding;
import it.saimao.tulukeyboard.databinding.DialogAppLanguagesBinding;
import it.saimao.tulukeyboard.utils.PrefManager;
import it.saimao.tulukeyboard.utils.Utils;

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
        binding.cvEnableKeyVibration.setChecked(PrefManager.isEnabledKeyVibration(this));
        binding.cvEnableKeySound.setChecked(PrefManager.isEnabledKeySound(this));
    }

    private void initListeners() {

        binding.cvChangeAppLanguage.setOnClickListener(v -> {
            changeAppLanguageDialog();
        });

        binding.cvEnableKeyVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeyVibration(getApplicationContext(), isChecked);
            Utils.setUpdateSharedPreference(true);
        });

        binding.cvEnableKeySound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeySound(getApplicationContext(), isChecked);
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

    private void changeAppLanguageDialog() {
        var dialogBinding = DialogAppLanguagesBinding.inflate(getLayoutInflater());
        var appLanguages = List.of("en", "tulu");
        // Preselect the app language
        var appLanguage = PrefManager.getStringValue(getApplicationContext(), APP_LANGUAGE);
        ((RadioButton) dialogBinding.rgAppLanguages.getChildAt(appLanguages.indexOf(appLanguage))).setChecked(true);

        var builder = new AlertDialog.Builder(this);
        var dialog = builder.setTitle("Choose App Language")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Save", (dialog1, which) -> {
                    int checkedId = dialogBinding.rgAppLanguages.getCheckedRadioButtonId();
                    String locale;
                    if (checkedId == R.id.rb_shan) locale = "shn";
                    else locale = "en";
                    PrefManager.saveStringValue(getApplicationContext(), APP_LANGUAGE, locale);
                    Utils.setLocale(MainActivity.this, locale);
                    dialog1.cancel();
                    recreate();
                }).create();
        dialog.show();
    }


}