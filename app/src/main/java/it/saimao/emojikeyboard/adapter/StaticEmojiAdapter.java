package it.saimao.emojikeyboard.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import it.saimao.maokeyboard.MaoKeyboardService;

public class StaticEmojiAdapter extends BaseEmojiAdapter {

    public StaticEmojiAdapter(Context context, String[] emojiTextsAsStrings, ArrayList<Integer> iconIds) {
        super((MaoKeyboardService) context);
        this.emojiTexts = new ArrayList<String>(Arrays.asList(emojiTextsAsStrings));
        this.iconIds = iconIds;
    }
}