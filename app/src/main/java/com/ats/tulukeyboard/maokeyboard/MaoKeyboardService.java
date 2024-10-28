package com.ats.tulukeyboard.maokeyboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.ats.tulukeyboard.R;
import com.ats.tulukeyboard.emojikeyboard.view.EmojiKeyboardView;
import com.ats.tulukeyboard.utils.PrefManager;
import com.ats.tulukeyboard.utils.Utils;

public class MaoKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private MaoKeyboardView keyboardView;
    private MaoKeyboard eng1Keyboard;
    private MaoKeyboard eng2Keyboard;
    private MaoKeyboard tuluKeyboard;
    private MaoKeyboard tuluShiftedKeyboard;
    private MaoKeyboard currentKeyboard;
    private MaoKeyboard numberKeyboard;
    private MaoKeyboard engSymbolKeyboard;
    private MaoKeyboard engNumbersKeyboard;
    private MaoKeyboard previousKeyboard;
    private boolean shifted = false;
    private boolean keyVibrate;
    private boolean keySound;
    private SoundPool sp;
    private Vibrator vibrator;
    private int sound_standard;
    private PopupWindow popwd1;
    private boolean emojiOn;

    public SoundPool getSoundPool() {
        if (sp == null) {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            sound_standard = sp.load(getApplicationContext(), R.raw.sound1, 1);
        }
        return sp;
    }

    public Vibrator getVibrator() {
        if (vibrator == null) vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        return vibrator;
    }


    public Context staticApplicationContext;
    public InputMethodManager previousInputMethodManager;

    private void initKeyboardTheme() {

        switch (PrefManager.getKeyboardTheme(this)) {
            case 0:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_tulu, null);
                break;
            case 1:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_korre, null);
                break;
            case 2:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_india, null);
                break;
            case 3:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_dark, null);
                break;
            case 4:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_green, null);
                break;
            case 5:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_blue, null);
                break;
            case 6:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_sky_blue, null);
                break;
            case 7:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_red, null);
                break;
            case 8:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_pink, null);
                break;
            case 9:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_violet, null);
                break;
            case 10:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_scarlet_red, null);
                break;
            case 11:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_dracula, null);
                break;
            default:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_tulu, null);
        }
    }

    private EmojiKeyboardView emojiKeyboardView;

    private EmojiKeyboardView getEmojiKeyboardView() {
        if (emojiKeyboardView == null) {
            emojiKeyboardView = (EmojiKeyboardView) getLayoutInflater().inflate(R.layout.emoji_keyboard_layout, null);
        }
        return emojiKeyboardView;
    }

    @Override
    public View onCreateInputView() {

        if (emojiOn) {
            // Context
            staticApplicationContext = getApplicationContext();
            // Input Method Manager
            previousInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            emojiOn = false;
            return getEmojiKeyboardView().getView();
        }
        initKeyboardTheme();
        if (Utils.getKeyboardBeforeChangeToEmoji() == null) {
            keyboardView.setKeyboard(getTuluKeyboard());
            currentKeyboard = getTuluKeyboard();
        } else {
            keyboardView.setKeyboard(Utils.getKeyboardBeforeChangeToEmoji());
            currentKeyboard = getKeyboardFromId(Utils.getKeyboardBeforeChangeToEmoji().getId());
            Utils.setKeyboardBeforeChangeToEmoji(null);
        }
        keyboardView.setOnKeyboardActionListener(this);

        return keyboardView;
    }

    public MaoKeyboard getEngSymbolKeyboard() {
        if (engSymbolKeyboard == null) engSymbolKeyboard = new MaoKeyboard(this, R.xml.eng_symbol);
        return engSymbolKeyboard;
    }

    public MaoKeyboard getNumberKeyboard() {
        if (numberKeyboard == null) numberKeyboard = new MaoKeyboard(this, R.xml.number);
        return numberKeyboard;
    }

    public MaoKeyboard getEngNumbersKeyboard() {
        if (engNumbersKeyboard == null)
            engNumbersKeyboard = new MaoKeyboard(this, R.xml.eng_numbers);
        return engNumbersKeyboard;
    }

    public MaoKeyboard getTuluKeyboard() {
        if (tuluShiftedKeyboard == null)
            tuluShiftedKeyboard = new MaoKeyboard(this, R.xml.tulu, "tl1");
        return tuluShiftedKeyboard;
    }

    public MaoKeyboard getShiftedTuluKeyboard() {
        if (tuluKeyboard == null) tuluKeyboard = new MaoKeyboard(this, R.xml.tulu_shifted, "tl2");
        return tuluKeyboard;
    }

    private MaoKeyboard getEng2Keyboard() {
        if (eng2Keyboard == null) eng2Keyboard = new MaoKeyboard(this, R.xml.english2, "eng2");
        return eng2Keyboard;
    }

    private MaoKeyboard getEng1Keyboard() {
        if (eng1Keyboard == null) eng1Keyboard = new MaoKeyboard(this, R.xml.english1, "eng1");
        return eng1Keyboard;
    }


    private MaoKeyboard getKeyboardFromId(String id) {
        return switch (id) {
            case "tl1", "tl2" -> getTuluKeyboard();
            default -> getEng1Keyboard();
        };
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);

        if (keyboardView == null) initKeyboardTheme();

        if ((attribute.inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_PHONE) {
            try {
                keyboardView.setKeyboard(getNumberKeyboard());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                changeKeyboard(currentKeyboard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isLanguageKeyboard() {
        return currentKeyboard == getEng1Keyboard() || currentKeyboard == getEng2Keyboard() || currentKeyboard == getTuluKeyboard() || currentKeyboard == getShiftedTuluKeyboard();
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
    }


    @Override
    public void onPress(int i) {
        playVibrate();
        playClick();
    }

    @Override
    public void onRelease(int i) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {


        InputConnection ic = getCurrentInputConnection();

        // emoji
        if ((primaryCode >= 128000 && primaryCode <= 128567) || primaryCode == 92619) {
            ic.commitText(new String(Character.toChars(primaryCode)), 1);
            return;
        }

        CharSequence charBeforeCursor = ic.getTextBeforeCursor(1, 0);

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE -> {
                CharSequence selectedText = ic.getSelectedText(0);
                if (TextUtils.isEmpty(selectedText)) {
                    if ((charBeforeCursor == null) || (charBeforeCursor.length() <= 0)) {
                        return;// fixed on issue of version 1.2, cause=(getText is null)
                    }
                    if (Character.isLowSurrogate(charBeforeCursor.charAt(0)) || Character.isHighSurrogate(charBeforeCursor.charAt(0))) {
                        ic.deleteSurroundingText(2, 0);
                    } else {
                        ic.deleteSurroundingText(1, 0);
                    }
                } else {
                    ic.commitText("", 1);
                }
            }
            case Keyboard.KEYCODE_DONE -> {
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            }
            case -130 -> { // Switch to emoji keyboard
                Utils.setKeyboardBeforeChangeToEmoji(currentKeyboard);
                previousKeyboard = currentKeyboard;
                emojiOn = true;
                Utils.setEmojiKeyboard(true);
                setInputView(onCreateInputView());
                resetCapsAndShift();
            }
            case -101 -> { // Switch language
                if (currentKeyboard == getEng1Keyboard() || currentKeyboard == getEng2Keyboard()) {
                    changeKeyboard(getTuluKeyboard());
                } else if (currentKeyboard == getTuluKeyboard() || currentKeyboard == getShiftedTuluKeyboard()) {
                    changeKeyboard(getEng1Keyboard());
                }
                resetCapsAndShift();
            }
            case -123 -> { // Switch to eng symbol keyboard
                previousKeyboard = currentKeyboard;
                changeKeyboard(getEngSymbolKeyboard());
                resetCapsAndShift();
            }
            case -321 -> { // Switch from eng symbol to normal keyboard
                if (previousKeyboard == null) {
                    changeKeyboard(getEng1Keyboard());
                } else {
                    changeKeyboard(previousKeyboard);
                }
                resetCapsAndShift();
            }
            case -412 -> { // Switch to eng shifted keyboard
                changeKeyboard(getEng2Keyboard());
                checkToggleCapsLock();
                shifted = true;
            }
            case -421 -> { // Switch to eng normal keyboard
                checkToggleCapsLock();
                if (capsLock) {
                    capsLock = false;
                    caps = true;
                    return;
                }
                changeKeyboard(getEng1Keyboard());
                resetCapsAndShift();
            }
            case -882 -> { // Switch to tulu shifted keyboard
                changeKeyboard(getShiftedTuluKeyboard());
                checkToggleCapsLock();
                shifted = true;
            }
            case -881 -> { // Switch to tulu normal keyboard
                checkToggleCapsLock();
                if (capsLock) {
                    capsLock = false;
                    caps = true;
                    return;
                }
                changeKeyboard(getTuluKeyboard());
                resetCapsAndShift();
            }
            case -521 -> { // Switch from eng number to eng symbol
                changeKeyboard(getEngSymbolKeyboard());
                resetCapsAndShift();
            }
            case -557 -> {
                changeKeyboard(getEngNumbersKeyboard());
                resetCapsAndShift();
            }

            default -> {
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
                if (shifted && !caps) {
                    if (currentKeyboard == getShiftedTuluKeyboard()) {
                        changeKeyboard(getTuluKeyboard());
                    } else if (currentKeyboard == getEng2Keyboard()) {
                        changeKeyboard(getEng1Keyboard());
                    }
                    shifted = false;
                    keyboardView.invalidateAllKeys();
                }
            }
        }
    }


    // Play vibration when click
    private void playVibrate() {
        if (keyVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getVibrator().vibrate(VibrationEffect.createOneShot(30, 1));
            } else {
                getVibrator().vibrate(30);
            }
        }
    }

    private void changeKeyboard(MaoKeyboard keyboard) {
        keyboardView.setKeyboard(keyboard);
        currentKeyboard = keyboard;
    }

    @Override
    public void onText(CharSequence charSequence) {
        InputConnection ic = getCurrentInputConnection();
        ic.commitText(charSequence, 1);
    }


    @Override
    public void swipeLeft() {

    }


    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {
        requestHideSelf(0);
    }

    @Override
    public void swipeUp() {

    }

    private void playClick() {
        if (keySound) {
            getSoundPool().play(sound_standard, 1, 1, 0, 0, 1);
        }
    }

    @Override
    public void onWindowShown() {
        keyVibrate = PrefManager.isEnabledKeyVibration(getApplicationContext());
        keySound = PrefManager.isEnabledKeySound(getApplicationContext());
        if (Utils.isEmojiKeyboard()) {
            emojiOn = false;
            setInputView(onCreateInputView());
            Utils.setEmojiKeyboard(false);
        }
        if (Utils.isThemeChanged()) {
            setInputView(onCreateInputView());
            emojiKeyboardView = null;
            Utils.setThemeChanged(false);
        }
        keyboardView.setPreviewEnabled(PrefManager.isEnabledKeyPreview(getApplicationContext()));
        if (!isLanguageKeyboard()) {
            if (previousKeyboard == null) {
                MaoKeyboard keyboard = getTuluKeyboard();
                changeKeyboard(keyboard);
            } else {
                changeKeyboard(previousKeyboard);
            }
        }

        super.onWindowShown();
    }

    public void sendText(String text) {
        playClick();
        playVibrate();
        InputConnection ic = getCurrentInputConnection();
        ic.commitText(text, 1);
    }

    public void sendDownKeyEvent(int keyEventCode, int flags) {
        InputConnection ic = getCurrentInputConnection();
        ic.sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, keyEventCode, 1, flags));
    }

    public void sendUpKeyEvent(int keyEventCode, int flags) {
        InputConnection ic = getCurrentInputConnection();
        ic.sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyEventCode, 1, flags));
    }


    public void sendDownAndUpKeyEvent(int keyEventCode, int flags) {
        playVibrate();
        playClick();
        sendDownKeyEvent(keyEventCode, flags);
        sendUpKeyEvent(keyEventCode, flags);
    }

    public void goBackToPreviousKeyboard() {
        playVibrate();
        playClick();
        setInputView(onCreateInputView());
        if (previousKeyboard == null) {
            changeKeyboard(getEng1Keyboard());
        } else {
            changeKeyboard(previousKeyboard);
        }
    }

    private long lastShiftTime;
    private boolean capsLock, caps;

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (lastShiftTime + 800 > now) {
            capsLock = true;
            lastShiftTime = 0;
        } else {
            lastShiftTime = now;
        }
    }

    private void resetCapsAndShift() {
        caps = false;
        lastShiftTime = 0;
        shifted = false;
    }


}
