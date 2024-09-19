package it.saimao.tmktaikeyboard.activities;

import static it.saimao.tmktaikeyboard.utils.Constants.APP_LANGUAGE;
import static it.saimao.tmktaikeyboard.utils.PrefManager.getApplicationLanguage;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import java.util.List;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.ActivityEnableKeyboardBinding;
import it.saimao.tmktaikeyboard.databinding.DialogAppLanguagesBinding;
import it.saimao.tmktaikeyboard.maoconverter.PopupConverterService;
import it.saimao.tmktaikeyboard.utils.PrefManager;
import it.saimao.tmktaikeyboard.utils.Utils;

public class EnableKeyboardActivity extends AppCompatActivity {

    private ActivityEnableKeyboardBinding binding;
    private ContentObserver observer;
    private AlertDialog chooseKeyboardDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initLanguage(this);
        initConverterService();
        SplashScreen.installSplashScreen(this);
        binding = ActivityEnableKeyboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListener();
    }

    private void initConverterService() {
        if (PrefManager.isEnablePopupConverter(getApplicationContext())) {
            startService(new Intent(this, PopupConverterService.class));
        }

    }




    @Override
    protected void onStart() {
        super.onStart();
        initUi();
    }

    private void initUi() {
        boolean isKeyboardEnabled = isKeyboardEnabled(this);
        boolean isKeyboardSelected = isKeyboardInUse(this);
        if (isKeyboardEnabled && isKeyboardSelected) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            if (!isKeyboardEnabled) {
                binding.cvEnableKeyboard.setVisibility(View.VISIBLE);
                binding.cvChooseKeyboard.setVisibility(View.GONE);
            } else {
                binding.cvEnableKeyboard.setVisibility(View.GONE);
                binding.cvChooseKeyboard.setVisibility(View.VISIBLE);
                Handler handler = new Handler(Looper.getMainLooper());
                registerKeyboardChangeObserver(this, handler);
            }
        }


        var appLanguage = getApplicationLanguage(getApplicationContext(), APP_LANGUAGE);
        binding.tvChangeAppLanguage.setText(appLanguage);


    }

    public void registerKeyboardChangeObserver(Context context, Handler handler) {
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                boolean isCustomKeyboardSelected = isKeyboardInUse(context);
                // Handle the change
                if (isCustomKeyboardSelected) {
                    initUi();
                }
            }
        };

        context.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.DEFAULT_INPUT_METHOD),
                false,
                observer
        );
    }

    public static boolean isKeyboardInUse(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        String currentInputMethodId = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.DEFAULT_INPUT_METHOD
        );

        List<InputMethodInfo> enabledInputMethods = imm.getEnabledInputMethodList();

        for (InputMethodInfo inputMethod : enabledInputMethods) {
            if (inputMethod.getId().equals(currentInputMethodId) &&
                    inputMethod.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKeyboardEnabled(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> enabledInputMethods = imm.getEnabledInputMethodList();

        for (InputMethodInfo inputMethod : enabledInputMethods) {
            if (inputMethod.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void initListener() {

        binding.cvEnableKeyboard.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
        });
        binding.cvChooseKeyboard.setOnClickListener(v -> {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showInputMethodPicker();
        });
        binding.tvChangeAppLanguage.setOnClickListener(v -> {
            changeAppLanguageDialog();
        });
    }


    private void changeAppLanguageDialog() {
        var dialogBinding = DialogAppLanguagesBinding.inflate(getLayoutInflater());
        var appLanguages = List.of("en", "shn", "my");
        // Preselect the app language
        var appLanguage = PrefManager.getStringValue(getApplicationContext(), APP_LANGUAGE);
        ((RadioButton) dialogBinding.rgAppLanguages.getChildAt(appLanguages.indexOf(appLanguage))).setChecked(true);

        if (chooseKeyboardDialog == null) {

            var builder = new AlertDialog.Builder(this);
            chooseKeyboardDialog = builder.setTitle("Choose App Language")
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

                        Intent refresh = new Intent(this, EnableKeyboardActivity.class);
                        startActivity(refresh);
                        finish();

                    }).create();
        }
        chooseKeyboardDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (observer != null)
            getContentResolver().unregisterContentObserver(observer);
    }
}