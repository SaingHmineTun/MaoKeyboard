package it.saimao.tmktaikeyboard.activities;

import static it.saimao.tmktaikeyboard.utils.Constants.APP_LANGUAGE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.ActivityMainBinding;
import it.saimao.tmktaikeyboard.databinding.DialogAppLanguagesBinding;
import it.saimao.tmktaikeyboard.maoconverter.PopupConverterService;
import it.saimao.tmktaikeyboard.utils.PermissionUtils;
import it.saimao.tmktaikeyboard.utils.PrefManager;
import it.saimao.tmktaikeyboard.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Utils.initLanguage(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PrefManager.isEnabledLanguage(this, "mm_MM") || PrefManager.isEnabledLanguage(this, "shn_MM")) {
            binding.cbEnablePopupConverter.setVisibility(View.VISIBLE);
            binding.cbEnableHandwriting.setVisibility(View.VISIBLE);
            binding.cvEnableConverters.setVisibility(View.VISIBLE);
        } else {
            binding.cbEnablePopupConverter.setVisibility(View.GONE);
            binding.cbEnableHandwriting.setVisibility(View.GONE);
            binding.cvEnableConverters.setVisibility(View.GONE);
        }
    }

    private void initUi() {
        binding.cbEnableKeyPreview.setChecked(PrefManager.isEnabledKeyPreview(this));
        binding.cbEnableKeyVibration.setChecked(PrefManager.isEnabledKeyVibration(this));
        binding.cbEnableKeySound.setChecked(PrefManager.isEnabledKeySound(this));
        binding.cbEnableHandwriting.setChecked(PrefManager.isEnabledHandWriting(this));
        binding.cbEnablePopupConverter.setChecked(PrefManager.isEnablePopupConverter(this));
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

        binding.cbEnablePopupConverter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (PermissionUtils.isOverlayPermissionEnabled(this)) {
                PrefManager.setEnabledPopupConverter(getApplicationContext(), isChecked);
                if (isChecked) {
                    startService(new Intent(this, PopupConverterService.class));
                } else {
                    stopService(new Intent(this, PopupConverterService.class));
                }
            } else {
                showRequestPermissionDialog();
                binding.cbEnablePopupConverter.setChecked(false);
            }
        });

        binding.cbEnableKeyVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeyVibration(getApplicationContext(), isChecked);
        });

        binding.cbEnableKeySound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeySound(getApplicationContext(), isChecked);
        });

        binding.cbEnableHandwriting.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledHandWriting(getApplicationContext(), isChecked);
        });

        binding.cbEnableKeyPreview.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PrefManager.setEnabledKeyPreview(getApplicationContext(), isChecked);
        });

        binding.cvChooseTheme.setOnClickListener(v -> {
            startActivity(new Intent(this, ChooseThemeActivity.class));
        });

        binding.cvChooseLanguage.setOnClickListener(view -> {
            startActivity(new Intent(this, ChooseLanguageActivity.class));
        });

        binding.cvEnableConverters.setOnClickListener(view -> {
            startActivity(new Intent(this, EnableConvertersActivity.class));
        });

        binding.cvAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivity.class));
        });
        binding.cvTestKeyboard.setOnClickListener(v -> {
            startActivity(new Intent(this, TestKeyboardActivity.class));
        });
    }

    private AlertDialog chooseLanguageDialog;

    private void changeAppLanguageDialog() {
        var dialogBinding = DialogAppLanguagesBinding.inflate(getLayoutInflater());
        var appLanguages = List.of("en", "shn", "my");
        // Preselect the app language
        var appLanguage = PrefManager.getStringValue(getApplicationContext(), APP_LANGUAGE);
        ((RadioButton) dialogBinding.rgAppLanguages.getChildAt(appLanguages.indexOf(appLanguage))).setChecked(true);

        if (chooseLanguageDialog == null) {
            var builder = new AlertDialog.Builder(this);
            chooseLanguageDialog = builder.setTitle("Choose App Language")
                    .setView(dialogBinding.getRoot())
                    .setPositiveButton("Save", (dialog1, which) -> {
                        int checkedId = dialogBinding.rgAppLanguages.getCheckedRadioButtonId();
                        String locale;
                        if (checkedId == R.id.rb_shan) locale = "shn";
                        else if (checkedId == R.id.rb_burma) locale = "my";
                        else locale = "en";
                        Utils.setAppLocale(this, locale);
                        PrefManager.saveStringValue(this, APP_LANGUAGE, locale);
                        dialog1.cancel();

                        Intent refresh = new Intent(this, MainActivity.class);
                        startActivity(refresh);
                        finish();

                    }).create();
        }
        chooseLanguageDialog.show();
    }


}