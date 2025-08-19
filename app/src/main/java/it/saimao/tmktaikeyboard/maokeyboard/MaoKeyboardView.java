/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.saimao.tmktaikeyboard.maokeyboard;

import static it.saimao.tmktaikeyboard.utils.Constants.FONT_CONVERTER;
import static it.saimao.tmktaikeyboard.utils.Constants.TAILE_CONVERTER;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import it.saimao.tmktaikeyboard.utils.PrefManager;

public class MaoKeyboardView extends KeyboardView {

    private static final int KEYCODE_OPTIONS = -100;
    private final Context context;


    public MaoKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MaoKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        // Apply insets fix here
        ViewCompat.setOnApplyWindowInsetsListener(this, (view, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int bottomPadding = Math.max(imeInsets.bottom, sysInsets.bottom);
            view.setPadding(0, 0, 0, bottomPadding);

            return insets;
        });
        // Request a layout pass to apply the insets.
        ViewCompat.requestApplyInsets(this);
    }

    @Override
    protected boolean onLongPress(Keyboard.Key key) {
        MaoKeyboardService service = (MaoKeyboardService) getOnKeyboardActionListener();
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else if (key.codes[0] == -4 && PrefManager.isEnabledLanguage(context, FONT_CONVERTER)) {
            ((MaoKeyboardService) getOnKeyboardActionListener()).convertZawgyi();
            return true;
        } else if (key.codes[0] == -123 && PrefManager.isEnabledLanguage(context, TAILE_CONVERTER)) {
            ((MaoKeyboardService) getOnKeyboardActionListener()).convertTaimao();
            return true;
        } else if (key.codes[0] == 32) {
            InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showInputMethodPicker();
            return true;
        } else if (service.isMyanmarKeyboard()) {
            if (key.codes[0] == 0x1000 || key.codes[0] == 0x1001 || key.codes[0] == 0x1002 ||
                    key.codes[0] == 0x1005 || key.codes[0] == 0x1006 || key.codes[0] == 0x1007 || key.codes[0] == 0x100F ||
                    key.codes[0] == 0x1010 || key.codes[0] == 0x1011 || key.codes[0] == 0x1012 || key.codes[0] == 0x1013 ||
                    key.codes[0] == 0x1014 || key.codes[0] == 0x1015 || key.codes[0] == 0x1017 || key.codes[0] == 0x1018 ||
                    key.codes[0] == 0x1019 || key.codes[0] == 0x101A || key.codes[0] == 0x101C || key.codes[0] == 0x100D ||
                    key.codes[0] == 0x100B
            ) {
                service.onText(convertToViramaCharacter((char) key.codes[0]));
            } else if (key.codes[0] == 0x1004) {
                service.onText((char) key.codes[0] + "\u103A\u1039");
            }
            return super.onLongPress(key);
        } else if (key.codes[0] == -101) {
            ((MaoKeyboardService) getOnKeyboardActionListener()).hideWindow();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("it.saimao.tmktaikeyboard", "it.saimao.tmktaikeyboard.activities.MainActivity"));
            context.startActivity(intent);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }

    private String convertToViramaCharacter(char character) {
        return "\u1039" + character;
    }

}
