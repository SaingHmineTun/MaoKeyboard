package it.saimao.tmktaikeyboard.emojikeyboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.saimao.tmktaikeyboard.emojikeyboard.sqlite.RecentEntry;
import it.saimao.tmktaikeyboard.maokeyboard.MaoKeyboardService;

public class RecentEmojiAdapter extends BaseEmojiAdapter {

    private List<RecentEntry> frequentlyUsedEmojiList;

    public RecentEmojiAdapter(Context context) {
        super((MaoKeyboardService) context);
        refreshAdapterFromSource();
    }

    public void refreshAdapterFromSource() {

        frequentlyUsedEmojiList = dataSource.getAllEntriesInDescendingOrderOfCount();
        setupEmojiDataFromList(frequentlyUsedEmojiList);
        notifyDataSetChanged();
    }

    private void setupEmojiDataFromList(List<RecentEntry> recentEntries) {
        emojiTexts = new ArrayList<>();
        iconIds = new ArrayList<>();
        for (RecentEntry i : recentEntries) {
            try {
                iconIds.add(Integer.valueOf(i.getIcon()));
                emojiTexts.add(i.getText());
            } catch (NumberFormatException e) {
                Log.d("Kham", "Cannot convert " + i.getIcon() + " to Number");
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View imageView = super.getView(position, convertView, parent);
        imageView.setOnClickListener(v -> {
            emojiKeyboardService.sendText(emojiTexts.get(position));
            dataSource.incrementExistingEntryCountByOne(iconIds.get(position) + "");
        });

        imageView.setOnLongClickListener(view -> {
            dataSource.deleteEntryWithId(frequentlyUsedEmojiList.get(position).getId());
            frequentlyUsedEmojiList = null;
            refreshAdapterFromSource();
            return true;
        });

        return imageView;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dataSource.close();
    }
}