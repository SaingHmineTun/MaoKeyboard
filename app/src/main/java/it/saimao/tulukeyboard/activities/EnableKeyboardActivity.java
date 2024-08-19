package it.saimao.tulukeyboard.activities;

import static it.saimao.tulukeyboard.utils.Constants.APP_LANGUAGE;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import java.util.List;

import it.saimao.tulukeyboard.databinding.ActivityEnableKeyboardBinding;
import it.saimao.tulukeyboard.utils.PrefManager;
import it.saimao.tulukeyboard.utils.Utils;

public class EnableKeyboardActivity extends AppCompatActivity {

    private ActivityEnableKeyboardBinding binding;
    private ContentObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocale();
        SplashScreen.installSplashScreen(this);
        binding = ActivityEnableKeyboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListener();
    }


    private void initLocale() {
        var appLanguage = PrefManager.getStringValue(getApplicationContext(), APP_LANGUAGE);
        Utils.setLocale(this, appLanguage);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (observer != null)
            getContentResolver().unregisterContentObserver(observer);
    }
}