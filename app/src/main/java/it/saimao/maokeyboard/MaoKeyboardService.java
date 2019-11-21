package it.saimao.maokeyboard;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.SystemClock;
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
import android.widget.Toast;

import it.saimao.emojikeyboard.view.EmojiKeyboardView;
import it.saimao.maoconverter.MaoConverterService;
import it.saimao.maoconverter.MaoZgUniConverter;
import it.saimao.utils.Utils;
import it.saimao.zawgyidetector.ZawgyiDetector;

public class MaoKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private MaoKeyboard eng1Keyboard, eng2Keyboard, bm1Keyboard, bm2Keyboard, tai1Keyboard, tai2Keyboard, currentKeyboard, numberKeyboard, engSymbolKeyboard, previousKeyboard, bm1DtKeyboard, tai1DtKeyboard, burmaSymbolKeyboard, taiSymbolKeyboard, engNumbersKeyboard;
    private EmojiKeyboardView emojiKeyboardView;
    private boolean caps = false, keyVibrate, keySound, handwritingStyle, deleteDone;
    private SoundPool sp;
    private Vibrator vibrator;
    private int sound_standard;
    private PopupWindow popwd1;
    private Keyboard popkb1;
    private KeyboardView popkbv1;
    private ZawgyiDetector detector;
    private boolean inputConsonant, emojiOn;
    private int asai = 4145, esai = 4228;
    private InputConnection inputConnection;


    @Override
    public void onCreate() {
        super.onCreate();
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sound_standard = sp.load(getApplicationContext(), R.raw.sound1, 1);
        detector = new ZawgyiDetector(this);
        if (Utils.isEnable(this, "enablePopupConverter")) {
            if (!Utils.isMyServiceRunning(this, MaoConverterService.class)) {
                startService(new Intent(this, MaoConverterService.class));
            }
        }
    }

    @Override
    public void onDestroy() {
        if (Utils.isMyServiceRunning(this, MaoConverterService.class)) {
            stopService(new Intent(this, MaoConverterService.class));
        }
        super.onDestroy();
    }


    public static Context staticApplicationContext;
    public InputMethodManager previousInputMethodManager;
    public IBinder iBinder;

    @Override
    public View onCreateInputView() {

        if (emojiOn) {
            // Context
            staticApplicationContext = getApplicationContext();
            // Input Method Manager
            previousInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            // iBinder
            iBinder = this.getWindow().getWindow().getAttributes().token;
            emojiKeyboardView = (EmojiKeyboardView) getLayoutInflater().inflate(R.layout.emoji_keyboard_layout, null);
            emojiOn = false;
            View emojiView = emojiKeyboardView.getView();
            return emojiView;
        }
        switch (Utils.getKeyboardTheme(this)) {
            case 1:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.dark_theme, null);
                break;
            case 2:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.green_theme, null);
                break;
            case 3:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.blue_theme, null);
                break;
            case 4:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.skyblue_theme, null);
                break;
            case 5:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.red_theme, null);
                break;
            case 6:
                keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.pink_theme, null);
                break;
        }
        eng1Keyboard = new MaoKeyboard(this, R.xml.english1, "eng1");
        eng2Keyboard = new MaoKeyboard(this, R.xml.english2, "eng2");
//        if (Utils.isDoubleTapOn(this)) {
//            bm1Keyboard = new MaoKeyboard(this, R.xml.burma1_doubletap, "bm1");
//            tai1Keyboard = new MaoKeyboard(this, R.xml.tai1_doubletap, "tai1");
//        } else {
        bm1Keyboard = new MaoKeyboard(this, R.xml.burma1, "bm1");
        tai1Keyboard = new MaoKeyboard(this, R.xml.tai1, "tai1");
        engNumbersKeyboard = new MaoKeyboard(this, R.xml.eng_numbers);
//        }
        bm2Keyboard = new MaoKeyboard(this, R.xml.burma2, "bm2");
        tai2Keyboard = new MaoKeyboard(this, R.xml.tai2, "tai2");
        numberKeyboard = new MaoKeyboard(this, R.xml.number);
        engSymbolKeyboard = new MaoKeyboard(this, R.xml.eng_symbol);
        burmaSymbolKeyboard = new MaoKeyboard(this, R.xml.burma_symbol);
        taiSymbolKeyboard = new MaoKeyboard(this, R.xml.tai_symbol);

        if (Utils.getKeyboardBeforeChangeToEmoji() == null) {
            keyboardView.setKeyboard(eng1Keyboard);
            currentKeyboard = eng1Keyboard;
        } else {
            keyboardView.setKeyboard(Utils.getKeyboardBeforeChangeToEmoji());
            currentKeyboard = getKeyboardFromId(Utils.getKeyboardBeforeChangeToEmoji().getId());
            Utils.setKeyboardBeforeChangeToEmoji(null);
        }
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    private MaoKeyboard getKeyboardFromId(String id) {
        if (id.equals("eng1") || id.equals("eng2")) {
            return eng1Keyboard;
        } else if (id.equals("bm1") || id.equals("bm2")) {
            return bm1Keyboard;
        } else if (id.equals("tai1") || id.equals("tai2")) {
            return tai1Keyboard;
        } else {
            return eng1Keyboard;
        }
    }

    // Change dark_theme depend on Input Type
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        inputConnection = getCurrentInputConnection();
        keyVibrate = Utils.isEnable(this, "enableVibrate");
        keySound = Utils.isEnable(this, "enableKeySound");
        if (Utils.isThemeChanged()) {
            setInputView(onCreateInputView());
            Utils.setThemeChanged(false);
        }

//        if (Utils.isChangingDoubleTap()) {
//            setInputView(onCreateInputView());
//            Utils.setChangingDoubleTap(false);
//        }

        handwritingStyle = Utils.isEnable(this, "enableHandwritingStyle");
        if (!languageKeyboard()) {
            if (previousKeyboard == null) {
                changeKeyboard(eng1Keyboard);
            } else {
                changeKeyboard(previousKeyboard);
            }
        }

        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_PHONE:
                try {
                    keyboardView.setKeyboard(numberKeyboard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case InputType.TYPE_CLASS_TEXT:
                try {
                    changeKeyboard(currentKeyboard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    changeKeyboard(currentKeyboard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private boolean languageKeyboard() {
        if (currentKeyboard == eng1Keyboard || currentKeyboard == eng2Keyboard || currentKeyboard == bm1Keyboard || currentKeyboard == bm2Keyboard || currentKeyboard == tai2Keyboard || currentKeyboard == tai1Keyboard) {
            return true;
        }
        return false;
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

//        Log.d("tet", primaryCode + " : " + keyCodes.length);

        int charCodeBeforeCursor = 0, charCodeBeforeCursor2 = 0;

        InputConnection ic = getCurrentInputConnection();

        // emoji
        if ((primaryCode >= 128000) && (primaryCode <= 128567)) {
            ic.commitText(new String(Character.toChars(primaryCode)), 1);
            return;
        }

        CharSequence charBeforeCursor = ic.getTextBeforeCursor(1, 0);
        CharSequence charBeforeCursor2 = ic.getTextBeforeCursor(2, 0);
        if (charBeforeCursor.length() > 0) {
            charCodeBeforeCursor = Integer.valueOf(charBeforeCursor.charAt(0));
        }

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                CharSequence selectedText = ic.getSelectedText(0);
                if (TextUtils.isEmpty(selectedText)) {
//                    if (handwritingStyle) {
//                        // ‌ေ ကို ၂ ချက်ဖျက်တာကို ဖြေရှင်းထားတယ်
//                        if (charCodeBeforeCursor == charCodeBeforeCursor2) {
//                            ic.deleteSurroundingText(1, 0);
//                        } else if (charCodeBeforeCursor == asai || charCodeBeforeCursor == esai) {
//                            if (!deleteDone) {
//                                // တွေ ငွေ ရွှေ စစ်ထားတယ်
//                                CharSequence ch = ic.getTextBeforeCursor(2, 0);
//                                if (ch.length() > 1) {
//                                    int helpingChar = (int) ch.charAt(0);
////                                    Log.d("deletet", "Helping Character: " + (char)helpingChar);
//                                    //                          ျ                                   ွ                                   ှ                                   ြ
//                                    if (helpingChar == 4155 || helpingChar == 4157 || helpingChar == 4158 || helpingChar == 4156) {
//
//                                    } else if (helpingChar == 4151) {
//                                    } else if (Utils.isMyanmarConsonant(helpingChar)) {
//                                        // input Consonant ကို false ထားပြီး double tap မှာ တွေဈေ စစ်ထားတာကို ဖြုတ်မယ်
//                                        inputConsonant = true;
//                                        deleteDone = false;
//                                    }
//                                    else {
//                                        inputConsonant = false;
//                                        deleteDone = true;
//                                    }
//                                } else {
//                                    deleteDone = true;
//                                }
//                                ic.deleteSurroundingText(2, 0);
//                                ic.commitText(String.valueOf((char) charCodeBeforeCursor), 0);
//                                // inputConsonant false ဆိုရင် ကေ မှာ က ကို ဖျက်ပြီး က ပြန်ရေးရင် အဆင်ပြေတယ်
//                            }
//                            // end delete done
//                            else {
//                                ic.deleteSurroundingText(1, 0);
//                                deleteDone = false;
//                            }
//                            // charCodeBeforeCursor = ‌ေ စစ်ထားတာ ပြီးဆုံး
//                        } else {
//                            ic.deleteSurroundingText(1, 0);
//                            // solved  ‌ေ၁ ကို ဖြေရှင်းထားသည်
//                            // issue    ပေတ မှာ တ ကို ဖျက်ရင် ပ ကို ဖျက်မယ့် ပုံစံပျောက် ‌ေ ကိုပဲ အရင် ဖျက်သွား new issue
////                            deleteDone = true;
//                        }
//                    }
//                    else {
                    // for Emotion delete
                    if ((charBeforeCursor == null) || (charBeforeCursor.length() <= 0)) {
                        return;// fixed on issue of version 1.2, cause=(getText is null)
                        // solution=(if getText is null, return)
                    }
                    if (Character.isLowSurrogate(charBeforeCursor.charAt(0))
                            || Character.isHighSurrogate(charBeforeCursor.charAt(0))) {
                        ic.deleteSurroundingText(2, 0);
                    } else if (Utils.isMyanmarConsonant(charCodeBeforeCursor)) {
                        inputConsonant = true;
                        ic.deleteSurroundingText(1, 0);
                    } else {
                        ic.deleteSurroundingText(1, 0);
                    }
//                    }
                } else {
                    ic.commitText("", 1);
                }
                break;
            case Keyboard.KEYCODE_DONE:
//                if (onKeyLongPress(Keyboard.KEYCODE_DONE, null)) {
//                    Log.d("deletet", "ENTER KEY LONG PRESS!");
//                } else {
//                Log.d("deletet", onKeyLongPress(Keyboard.KEYCODE_DONE, null) + "");
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
//                }
                break;
            case 2301:
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View container = inflater.inflate(R.layout.popupkb, null);
                popkb1 = new Keyboard(getApplicationContext(), R.xml.popup);
                popwd1 = new PopupWindow(getApplicationContext());
                popwd1.setBackgroundDrawable(null);
                popwd1.setContentView(container);
                popkbv1 = container.findViewById(R.id.popupkb);
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
                if (currentKeyboard == eng1Keyboard || currentKeyboard == eng2Keyboard) {
                    changeKeyboard(bm1Keyboard);
                } else if (currentKeyboard == bm1Keyboard || currentKeyboard == bm2Keyboard) {
                    changeKeyboard(tai1Keyboard);
                } else if (currentKeyboard == tai1Keyboard || currentKeyboard == tai2Keyboard) {
                    changeKeyboard(eng1Keyboard);
                }
                break;
            case -123:
                previousKeyboard = currentKeyboard;
//                if (previousKeyboard == eng1Keyboard || previousKeyboard == eng2Keyboard) {
                changeKeyboard(engSymbolKeyboard);
//                } else if (previousKeyboard == bm1Keyboard || previousKeyboard == bm2Keyboar {
//                    changeKeyboard(burmaSymbolKeyboard);
//                } else if (previousKeyboard == tai1Keyboard || previousKeyboard == tai2Keyboard) {
//                    changeKeyboard(taiSymbolKeyboard);
//                }
                break;
            case -321:
                if (previousKeyboard == null) {
                    changeKeyboard(eng1Keyboard);
                } else {
                    if (previousKeyboard == taiSymbolKeyboard) {
                        changeKeyboard(tai1Keyboard);
                    } else if (previousKeyboard == burmaSymbolKeyboard) {
                        changeKeyboard(bm1Keyboard);
                    } else {
                        changeKeyboard(previousKeyboard);
                    }
                }
                break;
            case -112:
                changeKeyboard(bm2Keyboard);
                caps = true;
                break;
            case -121:
                changeKeyboard(bm1Keyboard);
                caps = false;
                break;
            case -212:
                changeKeyboard(tai2Keyboard);
                caps = true;
                break;
            case -221:
                changeKeyboard(tai1Keyboard);
                caps = false;
                break;
            case -412:
                changeKeyboard(eng2Keyboard);
                caps = true;
                break;
            case -421:
                changeKeyboard(eng1Keyboard);
                caps = false;
                break;
            case -501:
                changeKeyboard(engNumbersKeyboard);
                break;
            case -521:
//                changeKeyboard(previousKeyboard);
                if (previousKeyboard == bm1Keyboard || previousKeyboard == bm2Keyboard) {
                    changeKeyboard(burmaSymbolKeyboard);
                } else if (previousKeyboard == tai1Keyboard || previousKeyboard == tai2Keyboard) {
                    changeKeyboard(taiSymbolKeyboard);
                } else if (previousKeyboard == eng1Keyboard || previousKeyboard == eng2Keyboard) {
                    changeKeyboard(engSymbolKeyboard);
                }
                break;
            default:
                char code = (char) primaryCode;
                // သုံ ကို သုံ ပြောင်းမယ်
                //                              ု                                                   ံ                               ု
                if (primaryCode == 4143 && charCodeBeforeCursor == 4150 || primaryCode == 4141 && charCodeBeforeCursor == 4143 || primaryCode == 4141 && charCodeBeforeCursor == 4144 || primaryCode == 4156 && charCodeBeforeCursor == 4157 || primaryCode == 4155 && charCodeBeforeCursor == 4157) {
                    ic.deleteSurroundingText(1, 0);
                    char[] reorderChars = {(char) primaryCode, (char) charCodeBeforeCursor};
                    ic.commitText(String.valueOf(reorderChars), 1);
                    return;
                }

                // ခ ွ ျ ကို ခ ျ ွ ပြောင်းမယ်
//                if (primaryCode == 4155 && charCodeBeforeCursor == 4157) {
//                    ic.deleteSurroundingText(1, 0);
//                    char[] reorderChars = {(char)primaryCode, (char) charCodeBeforeCursor};
//                    ic.commitText(String.valueOf(reorderChars), 1);
//                    return;
//                }

//                Log.d("deletet", charBeforeCursor.length() + "");

                if (handwritingStyle) {
                    if (primaryCode == asai || primaryCode == esai) {
                        deleteDone = true;
                    }
//                    Log.d("deletet", charBeforeCursor.toString());
                    if (charCodeBeforeCursor == asai || charCodeBeforeCursor == esai) {
                        if (Utils.isMyanmarConsonant(primaryCode)) {
                            deleteDone = false;
//                            Log.d("deletet",  "Char code before cursor 2 : " + charBeforeCursor2.charAt(0) + " : " + charBeforeCursor2.length());
                            if (!inputConsonant) {
                                // အေပါ မှာ ပါ ကို ဖျက်ပြီး ဗျည်းရေးရင် reorder မလုပ်ဖို့ စစ်ထား
                                // မနေ ရေးချင်တာ မေန ပဲ ရေးလို့ရ
//                                if (Utils.isMyanmarConsonant(charBeforeCursor2.charAt(0))) {
//                                    ic.commitText(String.valueOf(code), 1);
//                                    inputConsonant = false;
//                                } else {
                                ic.deleteSurroundingText(1, 0);
                                char[] reorderChars = {code, (char) charCodeBeforeCursor};
                                ic.commitText(String.valueOf(reorderChars), 1);
                                inputConsonant = true;
//                                }
                            } else {
                                ic.commitText(String.valueOf(code), 1);
                            }
                            //                                  ျ                                   ြ                                   ွ                                   ှ
                        } else if (primaryCode == 4155 || primaryCode == 4156 || primaryCode == 4157 || primaryCode == 4158) {
                            ic.deleteSurroundingText(1, 0);
                            char[] reorderChars = {code, (char) charCodeBeforeCursor};
                            ic.commitText(String.valueOf(reorderChars), 1);
                            // stopReorder true ဆိုရင် ကျေ မှာ ကျ ကို ဖျက်ပြီး က ပြန်ရေးရင် ပြဿနာ
                            // false ဆိုရင်ကျတော့ ချွေတာကို ရိုက်မရတော့ပြန်ဘူး
//                            stopReorder = true;
                            // Double tap solution
                        } else if (primaryCode == 4143 || primaryCode == 4144) {
                            ic.commitText(String.valueOf(code), 1);
                        } else {
                            ic.commitText(String.valueOf(code), 1);
                            // false ဆိုရင် သေသေကို ရေးလို့မရတော့
                            inputConsonant = false;
                        }
                        // not ‌ေ and ​ႄ method
                    } else {
                        ic.commitText(String.valueOf(code), 1);
                        inputConsonant = false;
                    }
                    // End handwriting style
                } else {
                    ic.commitText(String.valueOf(code), 1);
                }
                if (caps) {
                    if (currentKeyboard == bm2Keyboard) {
                        changeKeyboard(bm1Keyboard);
                    } else if (currentKeyboard == tai2Keyboard) {
                        changeKeyboard(tai1Keyboard);
                    } else if (currentKeyboard == eng2Keyboard) {
                        changeKeyboard(eng1Keyboard);
                    }
                    caps = false;
                    keyboardView.invalidateAllKeys();
                }
        }
    }


    // Play vibration when click
    private void playVibrate() {
        if (keyVibrate) {
            vibrator.vibrate(30);
//            vibrator.vibrate(VibrationEffect.createOneShot(30, 1));
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
            if (currentKeyboard == tai2Keyboard) {
                changeKeyboard(tai1Keyboard);
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
            sp.play(sound_standard, 1, 1, 0, 0, 1);
        }
    }

    @Override
    public void onWindowShown() {
        if (Utils.isEmojiKeyboard()) {
            emojiOn = false;
            setInputView(onCreateInputView());
            Utils.setEmojiKeyboard(false);
        }
        // Auto-Capitalization
//        if (inputConnection.getTextBeforeCursor(2, 0).length() <= 0) {
//            changeKeyboard(eng2Keyboard);
//            caps = true;
//        }

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
        ic.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_DOWN,
                        keyEventCode,
                        1,
                        flags
                )
        );
    }

    public void sendUpKeyEvent(int keyEventCode, int flags) {
        InputConnection ic = getCurrentInputConnection();
        ic.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_UP,
                        keyEventCode,
                        1,
                        flags
                )
        );
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
            changeKeyboard(eng1Keyboard);
        } else {
            changeKeyboard(getKeyboardFromId(previousKeyboard.getId()));
        }
    }

    public boolean isZawgyi(String text) {
        double score = detector.getZawgyiProbability(text);
        boolean isZawgyi = score > .8;

        return isZawgyi;
    }
}
