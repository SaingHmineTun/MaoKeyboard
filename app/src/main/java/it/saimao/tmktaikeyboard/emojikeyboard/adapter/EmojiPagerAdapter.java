package it.saimao.tmktaikeyboard.emojikeyboard.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import it.saimao.tmktaikeyboard.emojikeyboard.constants.EmojiIcons;
import it.saimao.tmktaikeyboard.emojikeyboard.constants.EmojiTexts;
import it.saimao.tmktaikeyboard.emojikeyboard.constants.Twitter_EmojiIcons;
import it.saimao.tmktaikeyboard.emojikeyboard.view.KeyboardSinglePageView;

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
    private View recent, people, things, nature, transport, others;

    public EmojiPagerAdapter(Context context, ViewPager pager, int keyboardHeight) {
        super();

        this.context = context;
        this.pager = pager;
        this.keyboardHeight = keyboardHeight;
        this.pageViews = new View[6];
        this.icons = getPreferredIconSets();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        View pageView = getPageView(position);
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
                    if (recent == null) {
                        recentEmojiAdapter = new RecentEmojiAdapter(context);
                        recent = new KeyboardSinglePageView(context, recentEmojiAdapter).getView();
                    }
                    pageView = recent;
                }
                case 1 -> {
                    if (people == null)
                        people = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.peopleEmojiTexts, icons.getPeopleIconIds())).getView();
                    pageView = people;

                }
                case 2 -> {
                    if (things == null)
                        things = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.thingsEmojiTexts, icons.getThingsIconIds())).getView();
                    pageView = things;
                }
                case 3 -> {
                    if (nature == null)
                        nature = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.natureEmojiTexts, icons.getNatureIconIds())).getView();
                    pageView = nature;
                }
                case 4 -> {
                    if (transport == null)
                        transport = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.transEmojiTexts, icons.getTransIconIds())).getView();
                    pageView = transport;
                }
                case 5 -> {
                    if (others == null)
                        others = new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, EmojiTexts.otherEmojiTexts, icons.getOtherIconIds())).getView();
                    pageView = others;
                }
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

    private EmojiIcons getPreferredIconSets() {
        return new Twitter_EmojiIcons();
    }
}
