package it.saimao.emojikeyboard.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

import it.saimao.emojikeyboard.adapter.EmojiPagerAdapter;
import it.saimao.maokeyboard.MaoKeyboardService;
import it.saimao.maokeyboard.R;
import it.saimao.utils.Utils;

public class EmojiKeyboardView extends View implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private LinearLayout layout;
    private int backgroundResourceId;

    private EmojiPagerAdapter emojiPagerAdapter;
    private MaoKeyboardService emojiKeyboardService;

    public EmojiKeyboardView(Context context) {
        super(context);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {

//        Utils.setEmojiKeyboard(true);

        emojiKeyboardService = (MaoKeyboardService) context;
        backgroundResourceId = Utils.getThemeBackgroundResource(emojiKeyboardService);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout = (LinearLayout) inflater.inflate(R.layout.keyboard_main, null);
        layout.setBackgroundResource(backgroundResourceId);

        viewPager = (ViewPager) layout.findViewById(R.id.emojiKeyboard);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) layout.findViewById(R.id.emojiCategorytab);
        pagerSlidingTabStrip.setTextColor(getResources().getColor(R.color.key_white));
        pagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.key_secondary));
        pagerSlidingTabStrip.setIndicatorHeight(5);

        emojiPagerAdapter = new EmojiPagerAdapter(context, viewPager, height);

        viewPager.setAdapter(emojiPagerAdapter);

        setupDeleteButton();
        setupReturnButton();
        setupEnterButton();
        setupSpacebarButton();

        pagerSlidingTabStrip.setViewPager(viewPager);

//        viewPager.setCurrentItem(0);

        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
    }

    private void setupSpacebarButton() {
        ImageButton spaceBtn = layout.findViewById(R.id.spaceBarButton);
        spaceBtn.setBackgroundResource(backgroundResourceId);
        spaceBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiKeyboardService.sendDownAndUpKeyEvent(KeyEvent.KEYCODE_SPACE, 0);
            }
        });
    }

    private void setupEnterButton() {
        ImageButton enterBtn = layout.findViewById(R.id.enterButton);
        enterBtn.setBackgroundResource(backgroundResourceId);
        enterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiKeyboardService.sendDownAndUpKeyEvent(KeyEvent.KEYCODE_ENTER, 0);
            }
        });
    }

    private void setupReturnButton() {
        ImageButton switchToKeyboardBtn = layout.findViewById(R.id.switchToKeyboardButton);
        switchToKeyboardBtn.setBackgroundResource(backgroundResourceId);
        switchToKeyboardBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiKeyboardService.goBackToPreviousKeyboard();
            }
        });
    }

    public View getView() {
        return layout;
    }

    public void notifyDataSetChanged() {
        emojiPagerAdapter.notifyDataSetChanged();
        viewPager.refreshDrawableState();
    }

    private void setupDeleteButton() {

        ImageButton delete = layout.findViewById(R.id.deleteButton);
        delete.setBackgroundResource(backgroundResourceId);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiKeyboardService.sendDownAndUpKeyEvent(KeyEvent.KEYCODE_DEL, 0);
            }
        });
    }


    private int width;
    private int height;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        Log.d("emojiKeyboardView", width + " : " + height);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d("sharedPreferenceChange", "function called on change of shared preferences with key " + key);
        if (key.equals("icon_set")) {
            emojiPagerAdapter = new EmojiPagerAdapter(getContext(), viewPager, height);
            viewPager.setAdapter(emojiPagerAdapter);
            this.invalidate();
        }
    }
}
