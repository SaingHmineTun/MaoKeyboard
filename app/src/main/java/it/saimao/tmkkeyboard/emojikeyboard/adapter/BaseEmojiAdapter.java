package it.saimao.tmkkeyboard.emojikeyboard.adapter;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import it.saimao.tmkkeyboard.emojikeyboard.sqlite.EmojiDataSource;
import it.saimao.tmkkeyboard.maokeyboard.MaoKeyboardService;
import it.saimao.tmkkeyboard.utils.Utils;

public abstract class BaseEmojiAdapter extends BaseAdapter {

    protected MaoKeyboardService emojiKeyboardService;
    protected ArrayList<String> emojiTexts;
    protected ArrayList<Integer> iconIds;

    protected EmojiDataSource dataSource;

    public BaseEmojiAdapter(MaoKeyboardService emojiKeyboardService) {
        this.emojiKeyboardService = emojiKeyboardService;
        dataSource = new EmojiDataSource(emojiKeyboardService);
        dataSource.openInReadWriteMode();
    }

    @Override
    public int getCount() {
        if (emojiTexts == null) return 0;
        return emojiTexts.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(emojiKeyboardService);
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, emojiKeyboardService.getResources().getDisplayMetrics());
            imageView.setPadding(scale, (int) (scale * 1.2), scale, (int) (scale * 1.2));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(iconIds.get(position));
        imageView.setBackgroundResource(Utils.getThemeBackgroundResource(emojiKeyboardService));

        imageView.setOnClickListener(v -> {
            emojiKeyboardService.sendText(emojiTexts.get(position));
            dataSource.upsertEntry(emojiTexts.get(position), iconIds.get(position) + "");
        });

        return imageView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
