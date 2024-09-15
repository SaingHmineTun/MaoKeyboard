package it.saimao.tmkkeyboard.activities;

import static it.saimao.tmkkeyboard.utils.Constants.APP_LANGUAGE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.databinding.ActivityMainBinding;
import it.saimao.tmkkeyboard.databinding.DialogAppLanguagesBinding;
import it.saimao.tmkkeyboard.maoconverter.PopupConverterService;
import it.saimao.tmkkeyboard.utils.PermissionUtils;
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

    private void showRequestPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder
                .setTitle("Enable draw-over permission dialog")
                .setMessage("Please choose TMK Keyboard in the list and allow the permission")
                .setPositiveButton("OK", (dialog1, which) -> {
                    PermissionUtils.requestOverlayPermission(this);
                    dialog1.cancel();
                }).setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    private void initListeners() {

        binding.cvChangeAppLanguage.setOnClickListener(v -> {
            changeAppLanguageDialog();
        });

        binding.cvEnablePopupConverter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (PermissionUtils.isOverlayPermissionEnabled(this)) {
                PrefManager.setEnabledPopupConverter(getApplicationContext(), isChecked);
                if (isChecked) {
                    startService(new Intent(this, PopupConverterService.class));
                } else {
                    stopService(new Intent(this, PopupConverterService.class));
                }
            } else {
                showRequestPermissionDialog();
                binding.cvEnablePopupConverter.setChecked(false);
            }
        });

        binding.cvEnableKeyVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeyVibration(getApplicationContext(), isChecked);
        });

        binding.cvEnableKeySound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeySound(getApplicationContext(), isChecked);
        });

        binding.cbEnableHandwriting.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledHandWriting(getApplicationContext(), isChecked);
        });

        binding.cbEnableKeyPreview.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeyPreview(getApplicationContext(), isChecked);
        });

        binding.cvChooseTheme.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ChooseThemeActivity.class));
        });

        binding.cvChooseLanguage.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ChooseLanguageActivity.class));
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
        var appLanguages = List.of("en", "shn");
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