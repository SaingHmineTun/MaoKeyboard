package com.ats.tulukeyboard.emojikeyboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.ats.tulukeyboard.R;
import com.ats.tulukeyboard.databinding.KeyboardEmojiBinding;
import com.ats.tulukeyboard.emojikeyboard.adapter.EmojiPagerAdapter;
import com.ats.tulukeyboard.maokeyboard.MaoKeyboardService;
import com.ats.tulukeyboard.utils.PrefManager;
import com.ats.tulukeyboard.utils.Utils;

public class EmojiKeyboardView extends View {
    private int backgroundResourceId;
    private KeyboardEmojiBinding binding;
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
        emojiKeyboardService = (MaoKeyboardService) context;
        backgroundResourceId = Utils.getThemeBackgroundResource(emojiKeyboardService);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        binding = KeyboardEmojiBinding.inflate(inflater);

        int theme = PrefManager.getKeyboardTheme(getContext());
        int borderColor = getBorderColor();
        if (theme == 0) binding.ivBackground.setImageResource(R.drawable.bg_tulu);
        else binding.emojiLayout.setBackgroundColor(borderColor);

        // View Pager
        if (theme == 9) {

            binding.bottomBar.setBackgroundColor(getResources().getColor(R.color.black));
            binding.viewPager.setBackgroundColor(getResources().getColor(R.color.black));
        } else if (theme == 6 || theme == 7 || theme == 8) {

            binding.bottomBar.setBackgroundColor(getResources().getColor(R.color.white));
            binding.viewPager.setBackgroundColor(getResources().getColor(R.color.white));
        } else {

            binding.bottomBar.setBackgroundColor(borderColor);
            binding.viewPager.setBackgroundColor(borderColor);
        }
        emojiPagerAdapter = new EmojiPagerAdapter(context, binding.viewPager, height);
        binding.viewPager.setAdapter(emojiPagerAdapter);


        // Top bar
        binding.pagerSlidingTab.setTextColor(getResources().getColor(R.color.key_white));
        binding.pagerSlidingTab.setIndicatorColor(getBorderPressedColor());
        binding.pagerSlidingTab.setIndicatorHeight(5);
        binding.pagerSlidingTab.setViewPager(binding.viewPager);
        binding.pagerSlidingTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) emojiPagerAdapter.refreshRecentAdapter();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Bottom bar
        binding.divider.setBackgroundColor(getBorderPressedColor());
        setupDeleteButton();
        setupReturnButton();
        setupEnterButton();
        setupSpaceBarButton();
    }

    private int getBorderPressedColor() {
        return getResources().getColor(R.color.white);
    }

    private int getBorderColor() {
        int theme = PrefManager.getKeyboardTheme(getContext());
        switch (theme) {
            case 1, 9 -> {
                return getResources().getColor(R.color.key_dark);
            }
            case 2 -> {
                return getResources().getColor(R.color.key_success);
            }
            case 3 -> {
                return getResources().getColor(R.color.key_primary);
            }
            case 4 -> {
                return getResources().getColor(R.color.key_info);
            }
            case 5 -> {
                return getResources().getColor(R.color.key_danger);
            }
            case 6 -> {
                return getResources().getColor(R.color.key_pink);
            }
            case 7 -> {
                return getResources().getColor(R.color.violet_normal);
            }
            case 8 -> {
                return getResources().getColor(R.color.scarlet_pressed);
            }
            default -> {
                return getResources().getColor(android.R.color.transparent);
            }
        }
    }


    private void setupSpaceBarButton() {

        binding.spaceBarButton.setBackgroundResource(backgroundResourceId);
        binding.spaceBarButton.setOnClickListener(v -> emojiKeyboardService.sendDownAndUpKeyEvent(KeyEvent.KEYCODE_SPACE, 0));
    }

    private void setupEnterButton() {
        binding.enterButton.setBackgroundResource(backgroundResourceId);
        binding.enterButton.setOnClickListener(v -> emojiKeyboardService.sendDownAndUpKeyEvent(KeyEvent.KEYCODE_ENTER, 0));
    }

    private void setupReturnButton() {
        binding.switchToKeyboardButton.setBackgroundResource(backgroundResourceId);
        binding.switchToKeyboardButton.setOnClickListener(view -> emojiKeyboardService.goBackToPreviousKeyboard());
    }

    public View getView() {
        return binding.getRoot();
    }

    private void setupDeleteButton() {

        binding.deleteButton.setBackgroundResource(backgroundResourceId);
        binding.deleteButton.setOnClickListener(v -> emojiKeyboardService.sendDownAndUpKeyEvent(KeyEvent.KEYCODE_DEL, 0));
    }


    private int width;
    private int height;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

    }
}
