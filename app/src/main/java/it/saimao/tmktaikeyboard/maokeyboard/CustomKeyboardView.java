package it.saimao.tmktaikeyboard.maokeyboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import java.io.InputStream;

import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.utils.PrefManager;

public class CustomKeyboardView extends MaoKeyboardView {

    // Add a static bitmap cache
    private static Bitmap cachedBackgroundBitmap = null;
    private static String cachedBackgroundUri = null;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set a custom background for the Custom theme if one has been selected by the user
     */
    public void setCustomBackground() {
        String backgroundImageUri = PrefManager.getCustomBackgroundUri(getContext());

        // Check if we have a cached bitmap for this URI
        if (backgroundImageUri != null && !backgroundImageUri.isEmpty() &&
                backgroundImageUri.equals(cachedBackgroundUri) && cachedBackgroundBitmap != null &&
                !cachedBackgroundBitmap.isRecycled()) {
            // Use cached bitmap
            Drawable drawable = new BitmapDrawable(getContext().getResources(), cachedBackgroundBitmap);
            setBackground(drawable);
            return;
        }

        if (backgroundImageUri != null && !backgroundImageUri.isEmpty()) {
            try {
                // Try to load the custom background
                Uri uri = Uri.parse(backgroundImageUri);
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Cache the bitmap for future use
                if (cachedBackgroundBitmap != null && !cachedBackgroundBitmap.isRecycled()) {
                    cachedBackgroundBitmap.recycle();
                }
                cachedBackgroundBitmap = bitmap;
                cachedBackgroundUri = backgroundImageUri;

                Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                setBackground(drawable);
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                // Fallback to default background
                setBackgroundResource(R.drawable.bg_custom_default);
                // Clear cache on error
                if (cachedBackgroundBitmap != null && !cachedBackgroundBitmap.isRecycled()) {
                    cachedBackgroundBitmap.recycle();
                }
                cachedBackgroundBitmap = null;
                cachedBackgroundUri = null;
                e.printStackTrace();
            }
        } else {
            // Use default background
            setBackgroundResource(R.drawable.bg_custom_default);
            // Clear cache
            if (cachedBackgroundBitmap != null && !cachedBackgroundBitmap.isRecycled()) {
                cachedBackgroundBitmap.recycle();
            }
            cachedBackgroundBitmap = null;
            cachedBackgroundUri = null;
        }
    }

    /**
     * Clear the background bitmap cache
     */
    public static void clearBackgroundCache() {
        if (cachedBackgroundBitmap != null && !cachedBackgroundBitmap.isRecycled()) {
            cachedBackgroundBitmap.recycle();
        }
        cachedBackgroundBitmap = null;
        cachedBackgroundUri = null;
    }
}