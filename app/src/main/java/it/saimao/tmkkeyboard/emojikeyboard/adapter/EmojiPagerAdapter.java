package it.saimao.tmkkeyboard.emojikeyboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import it.saimao.tmkkeyboard.emojikeyboard.constants.EmojiIcons;
import it.saimao.tmkkeyboard.emojikeyboard.constants.EmojiTexts;
import it.saimao.tmkkeyboard.emojikeyboard.constants.Emojione_EmojiIcons;
import it.saimao.tmkkeyboard.emojikeyboard.view.KeyboardSinglePageView;

public class EmojiPagerAdapter extends PagerAdapter {

    private final String[] TITLES = {
            "recent",
            "people",
            "things",
            "nature",
            "places",
            "symbols"};

    private final ViewPager pager;
    private final int keyboardHeight;
    private final View[] pageViews;
    private final EmojiIcons icons;
    private final Context context;
    private RecentEmojiAdapter recentEmojiAdapter;

    public EmojiPagerAdapter(Context context, ViewPager pager, int keyboardHeight) {
        super();

        this.context = context;
        this.pager = pager;
        this.keyboardHeight = keyboardHeight;
        this.pageViews = new View[6];
        this.icons = getPreferedIconSet();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {

        var pageView = getPageView(position);
        pager.addView(pageView, position, keyboardHeight);
        return pageView;
    }

    public void refreshRecentAdapter() {
        recentEmojiAdapter.refreshAdapterFromSource();
    }


    private View getPageView(int position) {
        var pageView = pageViews[position];
        if (pageView == null) {
            switch (position) {
                case 0 -> {
                    recentEmojiAdapter = new RecentEmojiAdapter(context);
                    pageView = new KeyboardSinglePageView(context, recentEmojiAdapter).getView();
                }
                case 1 ->
                        pageView = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.peopleEmojiTexts, icons.getPeopleIconIds())).getView();
                case 2 ->
                        pageView = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.thingsEmojiTexts, icons.getThingsIconIds())).getView();
                case 3 ->
                        pageView = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.natureEmojiTexts, icons.getNatureIconIds())).getView();
                case 4 ->
                        pageView = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.transEmojiTexts, icons.getTransIconIds())).getView();
                case 5 ->
                        pageView = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.otherEmojiTexts, icons.getOtherIconIds())).getView();
            }
        }
        return pageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        pager.removeView(getPageView(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private EmojiIcons getPreferedIconSet() {
        return new Emojione_EmojiIcons();
    }
}
