package it.saimao.tmkkeyboard.maokeyboard;

import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import it.saimao.tmkkeyboard.R;
import it.saimao.tmkkeyboard.databinding.PopupkbBinding;
import it.saimao.tmkkeyboard.emojikeyboard.view.EmojiKeyboardView;
import it.saimao.tmkkeyboard.maoconverter.MaoZgUniConverter;
import it.saimao.tmkkeyboard.maoconverter.PopupConverterService;
import it.saimao.tmkkeyboard.utils.PrefManager;
import it.saimao.tmkkeyboard.utils.Utils;
import it.saimao.tmkkeyboard.zawgyidetector.ZawgyiDetector;

public class MaoKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;

    private MaoKeyboard eng1Keyboard;
    private MaoKeyboard eng2Keyboard;
    private MaoKeyboard bm1Keyboard;
    private MaoKeyboard bm2Keyboard;
    private MaoKeyboard tai1Keyboard;
    private MaoKeyboard tai2Keyboard;
    private MaoKeyboard currentKeyboard;
    private MaoKeyboard numberKeyboard;
    private MaoKeyboard engSymbolKeyboard;
    private MaoKeyboard burmaSymbolKeyboard;
    private MaoKeyboard taiSymbolKeyboard;
    private MaoKeyboard engNumbersKeyboard;
    private MaoKeyboard previousKeyboard;
    private boolean caps = false;
    private boolean keyVibrate;
    private boolean keySound;
    private boolean handwritingStyle;
    private SoundPool sp;
    private Vibrator vibrator;
    private int sound_standard;
    private PopupWindow popwd1;
    private ZawgyiDetector detector;
    private boolean inputConsonant, emojiOn;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Utils.isEnable(this, "enablePopupConverter")) {
            if (!Utils.isMyServiceRunning(this, PopupConverterService.class)) {
                startService(new Intent(this, PopupConverterService.class));
            }
        }
    }

    public ZawgyiDetector getDetector() {
        if (detector == null) detector = new ZawgyiDetector(this);
        return detector;
    }

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

    @Override
    public void onDestroy() {
        if (Utils.isMyServiceRunning(this, PopupConverterService.class)) {
            stopService(new Intent(this, PopupConverterService.class));
        }
        super.onDestroy();
    }


    public Context staticApplicationContext;
    public InputMethodManager previousInputMethodManager;

    private void initKeyboardView() {

        switch (PrefManager.getKeyboardTheme(this)) {
            case 1:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_green, null);
                break;
            case 2:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_blue, null);
                break;
            case 3:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_sky_blue, null);
                break;
            case 4:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_red, null);
                break;
            case 5:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_pink, null);
                break;
            case 6:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_violet, null);
                break;
            case 7:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_scarlet_red, null);
                break;
            case 8:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_dracula, null);
                break;
            case 9:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_light, null);
                break;
            default:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.theme_dark, null);
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

    public MaoKeyboard getTaiSymbolKeyboard() {
        if (taiSymbolKeyboard == null) taiSymbolKeyboard = new MaoKeyboard(this, R.xml.tai_symbol);
        return taiSymbolKeyboard;
    }

    public MaoKeyboard getBurmaSymbolKeyboard() {

        if (burmaSymbolKeyboard == null)
            burmaSymbolKeyboard = new MaoKeyboard(this, R.xml.burma_symbol);
        return burmaSymbolKeyboard;
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

    public MaoKeyboard getTai2Keyboard() {
        if (tai2Keyboard == null) tai2Keyboard = new MaoKeyboard(this, R.xml.tai2, "tai2");
        return tai2Keyboard;
    }

    public MaoKeyboard getTai1Keyboard() {
        if (tai1Keyboard == null) tai1Keyboard = new MaoKeyboard(this, R.xml.tai1, "tai1");
        return tai1Keyboard;
    }

    public MaoKeyboard getBm2Keyboard() {
        if (bm2Keyboard == null) bm2Keyboard = new MaoKeyboard(this, R.xml.burma2, "bm2");
        return bm2Keyboard;
    }

    public MaoKeyboard getBm1Keyboard() {
        if (bm1Keyboard == null) bm1Keyboard = new MaoKeyboard(this, R.xml.burma1, "bm1");
        return bm1Keyboard;
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
            case "bm1", "bm2" -> getBm1Keyboard();
            case "tai1", "tai2" -> getTai1Keyboard();
            default -> getEng1Keyboard();
        };
    }

    // Change dark_theme depend on Input Type
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        keyVibrate = PrefManager.isEnabledKeyVibration(getApplicationContext());
        keySound = PrefManager.isEnabledKeySound(getApplicationContext());
        handwritingStyle = PrefManager.isEnabledHandWriting(getApplicationContext());
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
        return currentKeyboard == getEng1Keyboard() || currentKeyboard == getEng2Keyboard() || currentKeyboard == getBm1Keyboard() || currentKeyboard == getBm2Keyboard() || currentKeyboard == getTai1Keyboard() || currentKeyboard == getTai2Keyboard();
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
        if ((primaryCode >= 128000) && (primaryCode <= 128567)) {
            ic.commitText(new String(Character.toChars(primaryCode)), 1);
            return;
        }

        int charCodeBeforeCursor = 0;
        CharSequence charBeforeCursor = ic.getTextBeforeCursor(1, 0);
        if (charBeforeCursor != null && charBeforeCursor.length() > 0) {
            charCodeBeforeCursor = charBeforeCursor.charAt(0);
        }

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                CharSequence selectedText = ic.getSelectedText(0);
                if (TextUtils.isEmpty(selectedText)) {
                    if ((charBeforeCursor == null) || (charBeforeCursor.length() <= 0)) {
                        return;// fixed on issue of version 1.2, cause=(getText is null)
                    }
                    if (Character.isLowSurrogate(charBeforeCursor.charAt(0)) || Character.isHighSurrogate(charBeforeCursor.charAt(0))) {
                        ic.deleteSurroundingText(2, 0);
                    } else if (Utils.isMyanmarConsonant(charCodeBeforeCursor)) {
                        inputConsonant = true;
                        ic.deleteSurroundingText(1, 0);
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
                Log.d("TMK Group", "2301");
                var popupBinding = PopupkbBinding.inflate(LayoutInflater.from(getApplicationContext()));
                View container = popupBinding.getRoot();
                Keyboard popkb1 = new Keyboard(getApplicationContext(), R.xml.popup);
                popwd1 = new PopupWindow(getApplicationContext());
                popwd1.setBackgroundDrawable(null);
                popwd1.setContentView(container);
                KeyboardView popkbv1 = container.findViewById(R.id.popupkb);
                popkbv1.setKeyboard(popkb1);
                popkbv1.setPopupParent(keyboardView);
                popkbv1.setOnKeyboardActionListener(this);
                popwd1.setOutsideTouchable(false);
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
            case -140:
                ic.performContextMenuAction(android.R.id.selectAll);
                CharSequence charSequence = ic.getSelectedText(0);
                String convertedText, selectedText2;
                if (!TextUtils.isEmpty(charSequence)) {
                    selectedText2 = charSequence.toString();
                    if (isZawgyi(selectedText2)) {
                        convertedText = MaoZgUniConverter.zg2uni(selectedText2);
                    } else {
                        convertedText = MaoZgUniConverter.uni2zg(selectedText2);
                    }
                    ic.commitText(convertedText, 1);
                }
                break;
            case -101:
                if (currentKeyboard == getEng1Keyboard() || currentKeyboard == getEng2Keyboard()) {
                    changeKeyboard(getBm1Keyboard());
                } else if (currentKeyboard == getBm1Keyboard() || currentKeyboard == getBm2Keyboard()) {
                    changeKeyboard(getTai1Keyboard());
                } else if (currentKeyboard == getTai1Keyboard() || currentKeyboard == getTai2Keyboard()) {
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
                    if (previousKeyboard == getTaiSymbolKeyboard()) {
                        changeKeyboard(getTai1Keyboard());
                    } else if (previousKeyboard == getBurmaSymbolKeyboard()) {
                        changeKeyboard(getBm1Keyboard());
                    } else {
                        changeKeyboard(previousKeyboard);
                    }
                }
                break;
            case -112:
                changeKeyboard(getBm2Keyboard());
                caps = true;
                break;
            case -121:
                changeKeyboard(getBm1Keyboard());
                caps = false;
                break;
            case -212:
                changeKeyboard(getTai2Keyboard());
                caps = true;
                break;
            case -221:
                changeKeyboard(getTai1Keyboard());
                caps = false;
                break;
            case -412:
                changeKeyboard(getEng2Keyboard());
                caps = true;
                break;
            case -421:
                changeKeyboard(getEng1Keyboard());
                caps = false;
                break;
            case -501:
                changeKeyboard(getEngNumbersKeyboard());
                break;
            case -521:
                if (previousKeyboard == getBm1Keyboard() || previousKeyboard == getBm2Keyboard()) {
                    changeKeyboard(getBurmaSymbolKeyboard());
                } else if (previousKeyboard == getTai1Keyboard() || previousKeyboard == getTai2Keyboard()) {
                    changeKeyboard(getTaiSymbolKeyboard());
                } else if (previousKeyboard == getEng1Keyboard() || previousKeyboard == getEng2Keyboard()) {
                    changeKeyboard(getEngSymbolKeyboard());
                }
                break;
            default:
                char code = (char) primaryCode;
                // သုံ ကို သုံ ပြောင်းမယ်
                if (primaryCode == 4143 && charCodeBeforeCursor == 4150 || primaryCode == 4141 && charCodeBeforeCursor == 4143 || primaryCode == 4141 && charCodeBeforeCursor == 4144 || primaryCode == 4156 && charCodeBeforeCursor == 4157 || primaryCode == 4155 && charCodeBeforeCursor == 4157) {
                    ic.deleteSurroundingText(1, 0);
                    char[] reorderChars = {(char) primaryCode, (char) charCodeBeforeCursor};
                    ic.commitText(String.valueOf(reorderChars), 1);
                    return;
                }

                if (handwritingStyle) {
                    int esai = 4228;
                    int asai = 4145;
                    if (charCodeBeforeCursor == asai || charCodeBeforeCursor == esai) {
                        if (Utils.isMyanmarConsonant(primaryCode)) {
                            if (!inputConsonant) {
                                ic.deleteSurroundingText(1, 0);
                                char[] reorderChars = {code, (char) charCodeBeforeCursor};
                                ic.commitText(String.valueOf(reorderChars), 1);
                                inputConsonant = true;
                            } else {
                                ic.commitText(String.valueOf(code), 1);
                            }
                        } else if (primaryCode == 4155 || primaryCode == 4156 || primaryCode == 4157 || primaryCode == 4158) {
                            ic.deleteSurroundingText(1, 0);
                            char[] reorderChars = {code, (char) charCodeBeforeCursor};
                            ic.commitText(String.valueOf(reorderChars), 1);
                        } else if (primaryCode == 4143 || primaryCode == 4144) {
                            ic.commitText(String.valueOf(code), 1);
                        } else {
                            ic.commitText(String.valueOf(code), 1);
                            inputConsonant = false;
                        }
                    } else {
                        ic.commitText(String.valueOf(code), 1);
                        inputConsonant = false;
                    }
                    // End handwriting style
                } else {
                    ic.commitText(String.valueOf(code), 1);
                }
                if (caps) {
                    if (currentKeyboard == getBm2Keyboard()) {
                        changeKeyboard(getBm1Keyboard());
                    } else if (currentKeyboard == getTai2Keyboard()) {
                        changeKeyboard(getTai1Keyboard());
                    } else if (currentKeyboard == getEng2Keyboard()) {
                        changeKeyboard(getEng1Keyboard());
                    }
                    caps = false;
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
        if (caps) {
            if (currentKeyboard == getTai2Keyboard()) {
                changeKeyboard(getTai1Keyboard());
                caps = false;
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

    public boolean isZawgyi(String text) {
        double score = getDetector().getZawgyiProbability(text);

        return score > .8;
    }
}
