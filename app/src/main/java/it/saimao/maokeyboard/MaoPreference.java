package it.saimao.maokeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import it.saimao.maoconverter.MaoConverterService;
import it.saimao.utils.Utils;


public class MaoPreference extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference enableKeyboardPref = findPreference("enableKeyboard");
        enableKeyboardPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                return true;
            }
        });

        Preference chooseKeyboardPref = findPreference("chooseKeyboard");
        chooseKeyboardPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.showInputMethodPicker();
                return true;
            }
        });

        CheckBoxPreference enablePopupPref = (CheckBoxPreference) findPreference("enablePopupConverter");
        enablePopupPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean popupEnabled = Boolean.valueOf(String.valueOf(o));
                if (popupEnabled) {
                    getActivity().startService(new Intent(getActivity(), MaoConverterService.class));
                } else {
                    getActivity().stopService(new Intent(getActivity(), MaoConverterService.class));
                }
                return true;
            }
        });

        ListPreference chooseThemePref = (ListPreference) findPreference("chooseTheme");
        chooseThemePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

//                Toast.makeText(getActivity(), "Current Keyboard: " + Utils.getKeyboardTheme(getActivity()) + " : " + "Choose Keyboard: " + o.toString(), Toast.LENGTH_LONG).show();
                if (Integer.valueOf(o.toString()) != Utils.getKeyboardTheme(getActivity()))
                    Utils.setThemeChanged(true);
                return true;
            }
        });

//        CheckBoxPreference enableDoubleTap = (CheckBoxPreference) findPreference("enableDoubleTap");
//        enableDoubleTap.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                Utils.setChangingDoubleTap(true);
//                return true;
//            }
//        });

        Preference aboutPref = findPreference("about");
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                startActivity(new Intent(getActivity(), About.class));

                return true;
            }
        });
    }
}
