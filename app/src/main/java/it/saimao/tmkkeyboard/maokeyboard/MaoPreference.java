package it.saimao.tmkkeyboard.maokeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.activities.AboutActivity;
import it.saimao.tmkkeyboard.maoconverter.MaoConverterService;
import it.saimao.tmkkeyboard.utils.Utils;


public class MaoPreference extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference enableKeyboardPref = findPreference("enableKeyboard");
        enableKeyboardPref.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            return true;
        });

        Preference chooseKeyboardPref = findPreference("chooseKeyboard");
        chooseKeyboardPref.setOnPreferenceClickListener(preference -> {
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showInputMethodPicker();
            return true;
        });

        CheckBoxPreference enablePopupPref = (CheckBoxPreference) findPreference("enablePopupConverter");
        enablePopupPref.setOnPreferenceChangeListener((preference, o) -> {
            boolean popupEnabled = Boolean.valueOf(String.valueOf(o));
            if (popupEnabled) {
                getActivity().startService(new Intent(getActivity(), MaoConverterService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), MaoConverterService.class));
            }
            return true;
        });

        ListPreference chooseThemePref = (ListPreference) findPreference("chooseTheme");
        chooseThemePref.setOnPreferenceChangeListener((preference, o) -> {
            if (Integer.parseInt(o.toString()) != Utils.getKeyboardTheme(getActivity()))
                Utils.setThemeChanged(true);
            return true;
        });

        Preference aboutPref = findPreference("about");
        aboutPref.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), AboutActivity.class));
            return true;
        });
    }
}
