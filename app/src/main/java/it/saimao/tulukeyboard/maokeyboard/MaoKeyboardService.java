package it.saimao.tulukeyboard.maokeyboard;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import it.saimao.tulukeyboard.R;
import it.saimao.tulukeyboard.databinding.PopupkbBinding;
import it.saimao.tulukeyboard.emojikeyboard.view.EmojiKeyboardView;
import it.saimao.tulukeyboard.utils.PrefManager;
import it.saimao.tulukeyboard.utils.Utils;

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

    private void initKeyboardView() {

        switch (PrefManager.getKeyboardTheme(this)) {
            case 1:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_dark, null);
                break;
            case 2:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_green, null);
                break;
            case 3:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_blue, null);
                break;
            case 4:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_sky_blue, null);
                break;
            case 5:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_red, null);
                break;
            case 6:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_pink, null);
                break;
            case 7:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_violet, null);
                break;
            case 8:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_scarlet_red, null);
                break;
            case 9:
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
        initKeyboardView();
        if (Utils.getKeyboardBeforeChangeToEmoji() == null) {
            keyboardView.setKeyboard(getEng1Keyboard());
            currentKeyboard = getEng1Keyboard();
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

    // Change dark_theme depend on Input Type
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        keyVibrate = PrefManager.isEnabledKeyVibration(getApplicationContext());
        keySound = PrefManager.isEnabledKeySound(getApplicationContext());
        if (Utils.isThemeChanged()) {
            setInputView(onCreateInputView());
            emojiKeyboardView = null;
            Utils.setThemeChanged(false);
        }
        if (keyboardView == null) initKeyboardView();
        if (!isLanguageKeyboard()) {
            if (previousKeyboard == null) {
                MaoKeyboard keyboard = getEng1Keyboard();
                changeKeyboard(keyboard);
            } else {
                changeKeyboard(previousKeyboard);
            }
        }

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
            case Keyboard.KEYCODE_DELETE:
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
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case 2301:
                var popupBinding = PopupkbBinding.inflate(LayoutInflater.from(getApplicationContext()));
                View container = popupBinding.getRoot();

                Keyboard popkb1 = new Keyboard(getApplicationContext(), R.xml.popup);
                popwd1 = new PopupWindow(getApplicationContext());
                popwd1.setBackgroundDrawable(null);
                popwd1.setContentView(container);

                KeyboardView popkbv1 = popupBinding.popupkb;
                popkbv1.setKeyboard(popkb1);
                popkbv1.setPopupParent(keyboardView);
                popkbv1.setOnKeyboardActionListener(this);

                popwd1.setOutsideTouchable(true);
                popwd1.setWidth(keyboardView.getWidth());
                popwd1.setHeight(keyboardView.getHeight());
                popwd1.showAtLocation(keyboardView, 17, 0, 0);
                break;
            case 2300:
                popwd1.dismiss();
                break;
            case -130:
                Utils.setKeyboardBeforeChangeToEmoji(currentKeyboard);
                previousKeyboard = currentKeyboard;
                emojiOn = true;
                Utils.setEmojiKeyboard(true);
                setInputView(onCreateInputView());
                break;
            case -101:
                if (currentKeyboard == getEng1Keyboard() || currentKeyboard == getEng2Keyboard()) {
                    changeKeyboard(getTuluKeyboard());
                } else if (currentKeyboard == getTuluKeyboard() || currentKeyboard == getShiftedTuluKeyboard()) {
                    changeKeyboard(getEng1Keyboard());
                }
                break;
            case -123:
                previousKeyboard = currentKeyboard;
                changeKeyboard(getEngSymbolKeyboard());
                break;
            case -321:
                if (previousKeyboard == null) {
                    changeKeyboard(getEng1Keyboard());
                } else {
                    changeKeyboard(previousKeyboard);
                }
                break;
            case -412:
                changeKeyboard(getEng2Keyboard());
                shifted = true;
                break;
            case -421:
                changeKeyboard(getEng1Keyboard());
                shifted = false;
                break;
            case -882:
                changeKeyboard(getShiftedTuluKeyboard());
                shifted = true;
                break;
            case -881:
                changeKeyboard(getTuluKeyboard());
                shifted = false;
                break;
            case -555:
                shifted = true;
                break;
            case -501:
                changeKeyboard(getEngNumbersKeyboard());
                break;
            case -521:
                changeKeyboard(getEngSymbolKeyboard());
                break;
            default:
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
                if (shifted) {
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
        ic.commitText(charSequence, 0);
        if (shifted) {
            if (currentKeyboard == getShiftedTuluKeyboard()) {
                changeKeyboard(getTuluKeyboard());
                shifted = false;
            }
        }
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
        if (Utils.isEmojiKeyboard()) {
            emojiOn = false;
            setInputView(onCreateInputView());
            Utils.setEmojiKeyboard(false);
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
            changeKeyboard(getKeyboardFromId(previousKeyboard.getId()));
        }
    }


    public static boolean isEndOfText(InputConnection ic) {
        CharSequence charAfterCursor = ic.getTextAfterCursor(1, 0);
        if (charAfterCursor == null)
            return true;
        return charAfterCursor.length() <= 0;
    }

}
