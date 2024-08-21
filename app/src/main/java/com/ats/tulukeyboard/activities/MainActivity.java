package com.ats.tulukeyboard.activities;

import static com.ats.tulukeyboard.utils.Constants.APP_LANGUAGE;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ats.tulukeyboard.R;
import com.ats.tulukeyboard.databinding.ActivityMainBinding;
import com.ats.tulukeyboard.databinding.DialogAppLanguagesBinding;
import com.ats.tulukeyboard.databinding.DialogTestKeyboardBinding;
import com.ats.tulukeyboard.utils.PrefManager;
import com.ats.tulukeyboard.utils.Utils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AlertDialog testLanguageDialog;
    private DialogTestKeyboardBinding dialogBinding;

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
        binding.cvEnableKeyPreview.setChecked(PrefManager.isEnabledKeyPreview(this));
    }

    private void initListeners() {

        binding.cvChangeAppLanguage.setOnClickListener(v -> {
            changeAppLanguageDialog();
        });

        binding.cvEnableKeyVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeyVibration(getApplicationContext(), isChecked);
        });

        binding.cvEnableKeySound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeySound(getApplicationContext(), isChecked);
        });

        binding.cvEnableKeyPreview.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            PrefManager.setEnabledKeyPreview(getApplicationContext(), isChecked);
        }));

        binding.cvChooseTheme.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ChooseThemeActivity.class));
        });
        binding.cvAbout.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        });
        binding.cvTestKeyboard.setOnClickListener(v -> {
            showTestDialog();
        });
    }

    private void showTestDialog() {

        if (testLanguageDialog == null) {
            dialogBinding = DialogTestKeyboardBinding.inflate(getLayoutInflater());
            testLanguageDialog = new AlertDialog.Builder(this).setView(dialogBinding.getRoot()).create();
            dialogBinding.btClose.setOnClickListener(v -> testLanguageDialog.cancel());
        }
        dialogBinding.editText.requestFocus();
        testLanguageDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        testLanguageDialog.show();
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