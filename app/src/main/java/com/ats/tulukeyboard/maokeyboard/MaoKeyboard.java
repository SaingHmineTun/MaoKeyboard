package com.ats.tulukeyboard.maokeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;

public class MaoKeyboard extends Keyboard {

    private String id;

    public String getId() {
        return id;
    }

    public MaoKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public MaoKeyboard(Context context, int xmlLayoutResId, String id) {
        super(context, xmlLayoutResId);
        this.id = id;
    }

    public MaoKeyboard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
    }

    public MaoKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public MaoKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);

    }
}
