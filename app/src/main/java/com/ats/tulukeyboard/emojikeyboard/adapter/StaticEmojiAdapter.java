package com.ats.tulukeyboard.emojikeyboard.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import com.ats.tulukeyboard.maokeyboard.MaoKeyboardService;

public class StaticEmojiAdapter extends BaseEmojiAdapter {

    public StaticEmojiAdapter(Context context, String[] emojiTextsAsStrings, ArrayList<Integer> iconIds) {
        super((MaoKeyboardService) context);
        this.emojiTexts = new ArrayList<>(Arrays.asList(emojiTextsAsStrings));
        this.iconIds = iconIds;
    }
}