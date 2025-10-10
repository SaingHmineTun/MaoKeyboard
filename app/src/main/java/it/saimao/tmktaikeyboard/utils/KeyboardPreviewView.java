package it.saimao.tmktaikeyboard.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.maokeyboard.MaoKeyboard;

public class KeyboardPreviewView extends View {

    private MaoKeyboard keyboard;
    private int themeIndex = 0;
    private Context context;

    public KeyboardPreviewView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public KeyboardPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public KeyboardPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        // Initialization if needed
    }

    public void setThemeIndex(int themeIndex) {
        this.themeIndex = themeIndex;
        invalidate();
    }

    public void setKeyboard(MaoKeyboard keyboard) {
        this.keyboard = keyboard;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (keyboard == null) return;

        List<Keyboard.Key> keys = keyboard.getKeys();
        for (Keyboard.Key key : keys) {
            // Adjust for preview size (scale down)
            float scaleX = (float) getWidth() / keyboard.getMinWidth();
            float scaleY = (float) getHeight() / keyboard.getHeight();

            Rect scaledRect = new Rect((int) (key.x * scaleX), (int) (key.y * scaleY), (int) ((key.x + key.width) * scaleX), (int) ((key.y + key.height) * scaleY));

            // For TMK theme (index 9), draw transparent keys
            if (themeIndex == 9) {
                // Draw only key labels/icons without background for TMK theme
                Paint textPaint = new Paint();
                textPaint.setAntiAlias(true);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(dpToPx(10));
                textPaint.setColor(Color.WHITE);

                if (key.icon != null) {
                    // Draw icon
                    Drawable icon = key.icon.mutate();
                    int iconLeft = scaledRect.left + dpToPx(6);
                    int iconTop = scaledRect.top + dpToPx(6);
                    int iconRight = scaledRect.right - dpToPx(6);
                    int iconBottom = scaledRect.bottom - dpToPx(6);

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.setTint(Color.WHITE);
                    icon.draw(canvas);
                } else if (key.label != null) {
                    // Draw text
                    String label = key.label.toString();
                    float x = scaledRect.left + scaledRect.width() / 2f;
                    float y = scaledRect.top + scaledRect.height() / 2f - (textPaint.descent() + textPaint.ascent()) / 2;
                    canvas.drawText(label, x, y, textPaint);
                }
            } else {
                // Draw keys with actual theme backgrounds
                Drawable keyBackground = getKeyBackground();
                if (keyBackground != null) {
                    keyBackground.setBounds(scaledRect);
                    keyBackground.draw(canvas);
                } else {
                    // Fallback to solid colors if no drawable is available
                    Paint backgroundPaint = new Paint();
                    backgroundPaint.setAntiAlias(true);
                    backgroundPaint.setColor(getKeyColor());
                    canvas.drawRoundRect(new android.graphics.RectF(scaledRect), dpToPx(4), dpToPx(4), backgroundPaint);
                }

                // Draw key label or icon
                Paint textPaint = new Paint();
                textPaint.setAntiAlias(true);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTextSize(dpToPx(10));
                textPaint.setColor(Color.WHITE);

                if (key.icon != null) {
                    // Draw icon
                    Drawable icon = key.icon.mutate();
                    int iconLeft = scaledRect.left + dpToPx(6);
                    int iconTop = scaledRect.top + dpToPx(6);
                    int iconRight = scaledRect.right - dpToPx(6);
                    int iconBottom = scaledRect.bottom - dpToPx(6);

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.setTint(Color.WHITE);
                    icon.draw(canvas);
                } else if (key.label != null) {
                    // Draw text
                    String label = key.label.toString();
                    float x = scaledRect.left + scaledRect.width() / 2f;
                    float y = scaledRect.top + scaledRect.height() / 2f - (textPaint.descent() + textPaint.ascent()) / 2;
                    canvas.drawText(label, x, y, textPaint);
                }
            }
        }
    }

    private Drawable getKeyBackground() {
        // Return the appropriate key background drawable based on theme
        return switch (themeIndex) {
            case 0 -> context.getDrawable(R.drawable.enhanced_dark_theme_keybackground); // Dark theme
            case 1 -> context.getDrawable(R.drawable.enhanced_green_theme_keybackground); // Green theme
            case 2 -> context.getDrawable(R.drawable.enhanced_blue_theme_keybackground); // Blue theme
            case 3 -> context.getDrawable(R.drawable.enhanced_sunset_theme_keybackground); // Sunset theme
            case 4 -> context.getDrawable(R.drawable.enhanced_gold_theme_keybackground); // Gold theme
            case 5 -> context.getDrawable(R.drawable.enhanced_pink_theme_keybackground); // Pink theme
            case 6 -> context.getDrawable(R.drawable.enhanced_violet_theme_keybackground); // Violet theme
            case 7 -> context.getDrawable(R.drawable.enhanced_scarlet_theme_keybackground); // Scarlet theme
            case 8 -> context.getDrawable(R.drawable.enhanced_neon_theme_keybackground); // Neon theme
            default -> null;
        };
    }

    private int getKeyColor() {
        // Define background colors based on theme (fallback)
        return switch (themeIndex) {
            case 0 -> // Dark theme
                    Color.parseColor("#3a3a3a");
            case 1 -> // Green theme
                    Color.parseColor("#2e8b57");
            case 2 -> // Blue theme
                    Color.parseColor("#3F51B5");
            case 3 -> // Sky Blue theme
                    Color.parseColor("#039be5");
            case 4 -> // Gold theme
                    Color.parseColor("#d2ac47");
            case 5 -> // Pink theme
                    Color.parseColor("#ec407a");
            case 6 -> // Violet theme
                    Color.parseColor("#7e22ce");
            case 7 -> // Scarlet theme
                    Color.parseColor("#d3425b");
            case 8 -> // Neon theme
                    Color.parseColor("#3b3a41");
            case 9 -> // TMK theme (transparent)
                    Color.parseColor("#00000000"); // Fully transparent
            default -> Color.parseColor("#E0E0E0");
        };
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}