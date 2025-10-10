package it.saimao.tmktaikeyboard.emojikeyboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.databinding.KeyboardEmojiBinding;
import it.saimao.tmktaikeyboard.emojikeyboard.adapter.EmojiPagerAdapter;
import it.saimao.tmktaikeyboard.maokeyboard.MaoKeyboardService;
import it.saimao.tmktaikeyboard.utils.PrefManager;
import it.saimao.tmktaikeyboard.utils.Utils;


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

    private void adjustKeyboardViewFor15() {

        // Apply insets fix here
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int bottomPadding = Math.max(imeInsets.bottom, sysInsets.bottom);
            view.setPadding(0, 0, 0, bottomPadding);

            return insets;
        });

        ViewCompat.requestApplyInsets(binding.getRoot());
    }
    private void initialize(Context context) {
        emojiKeyboardService = (MaoKeyboardService) context;
        backgroundResourceId = Utils.getThemeBackgroundResource(emojiKeyboardService);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = KeyboardEmojiBinding.inflate(inflater);

        int theme = PrefManager.getKeyboardTheme(getContext());
        int borderColor = getBorderColor();
        if (theme == 9) binding.ivBackground.setImageResource(R.drawable.bg_custom_default);
        else binding.emojiLayout.setBackgroundColor(borderColor);
        
        // Apply background resource to the entire emoji keyboard view
        binding.emojiLayout.setBackgroundResource(backgroundResourceId);

        // Apply background resource to all buttons
        binding.switchToKeyboardButton.setBackgroundResource(backgroundResourceId);
        binding.deleteButton.setBackgroundResource(backgroundResourceId);
        binding.spaceBarButton.setBackgroundResource(backgroundResourceId);
        binding.enterButton.setBackgroundResource(backgroundResourceId);

        // View Pager
        if (theme == 8) {

            binding.bottomBar.setBackgroundColor(getResources().getColor(R.color.black));
            binding.viewPager.setBackgroundColor(getResources().getColor(R.color.black));
        } else if (theme == 4 || theme == 5 || theme == 6 || theme == 7) {

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

        binding.divider.setBackgroundColor(getBorderPressedColor());
        setupDeleteButton();
        setupReturnButton();
        setupEnterButton();
        setupSpaceBarButton();
        adjustKeyboardViewFor15();
    }

    private int getBorderPressedColor() {
        return getResources().getColor(R.color.white);
    }

    private int getBorderColor() {
        int theme = PrefManager.getKeyboardTheme(getContext());
        switch (theme) {
            case 0, 8 -> {
                return getResources().getColor(R.color.key_dark);
            }
            case 1 -> {
                return getResources().getColor(R.color.key_success);
            }
            case 2 -> {
                return getResources().getColor(R.color.key_primary);
            }
            case 3 -> {
                return getResources().getColor(R.color.key_info);
            }
            case 4 -> {
                return getResources().getColor(R.color.key_dark);
            }
            case 5 -> {
                return getResources().getColor(R.color.key_pink);
            }
            case 6 -> {
                return getResources().getColor(R.color.violet_normal);
            }
            case 7 -> {
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
        View rootView = binding.getRoot();
        // Apply background to the root view as well
        rootView.setBackgroundResource(Utils.getThemeBackgroundResource(emojiKeyboardService));
        return rootView;
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
