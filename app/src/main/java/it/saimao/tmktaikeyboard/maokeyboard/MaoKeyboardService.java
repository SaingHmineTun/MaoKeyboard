package it.saimao.tmktaikeyboard.maokeyboard;

import static it.saimao.tmktaikeyboard.utils.Constants.LANGUAGES;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import it.saimao.shan_language_tools.converters.ShanZawgyiConverter;
import it.saimao.shan_language_tools.converters.TaiNueaConverter;
import it.saimao.tmktaikeyboard.R;
import it.saimao.tmktaikeyboard.emojikeyboard.view.EmojiKeyboardView;
import it.saimao.tmktaikeyboard.maoconverter.MaoDetector;
import it.saimao.tmktaikeyboard.maoconverter.PopupConverterService;
import it.saimao.tmktaikeyboard.maoconverter.Rabbit;
import it.saimao.tmktaikeyboard.utils.PrefManager;
import it.saimao.tmktaikeyboard.utils.Utils;

public class MaoKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private MaoKeyboardView keyboardView;

    private MaoKeyboard eng1Keyboard;
    private MaoKeyboard eng2Keyboard;
    private MaoKeyboard bm1Keyboard;
    private MaoKeyboard bm2Keyboard;
    private MaoKeyboard tai1Keyboard;
    private MaoKeyboard tai2Keyboard;
    private MaoKeyboard taile1Keyboard;
    private MaoKeyboard taile2Keyboard;
    private MaoKeyboard thai1Keyboard;
    private MaoKeyboard thai2Keyboard;
    private MaoKeyboard khamti1Keyboard, khamti2Keyboard;
    private MaoKeyboard tham1Keyboard, tham2Keyboard;
    private MaoKeyboard currentKeyboard;
    private MaoKeyboard numberKeyboard;
    private MaoKeyboard engSymbolKeyboard;
    private MaoKeyboard burmaSymbolKeyboard;
    private MaoKeyboard taiSymbolKeyboard;
    private MaoKeyboard engNumbersKeyboard;
    private MaoKeyboard previousKeyboard;

    private boolean shifted = false;
    private boolean keyVibrate;
    private boolean keySound;
    private SoundPool sp;
    private Vibrator vibrator;
    private int sound_standard;

    private static String shanConsonants;
    private static String mWordSeparators;


    @Override
    public void onCreate() {
        super.onCreate();

        shanConsonants = getResources().getString(R.string.shan_consonants);
        mWordSeparators = getResources().getString(R.string.word_separators);
        if (Utils.isEnable(this, "enablePopupConverter")) {
            if (!Utils.isMyServiceRunning(this, PopupConverterService.class)) {
                startService(new Intent(this, PopupConverterService.class));
            }
        }
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


    public InputMethodManager previousInputMethodManager;

    private void initKeyboardView() {
        switch (PrefManager.getKeyboardTheme(this)) {
            case 1:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_green, null);
                break;
            case 2:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_blue, null);
                break;
            case 3:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_sky_blue, null);
                break;
            case 4:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_wood, null);
                break;
            case 5:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_pink, null);
                break;
            case 6:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_violet, null);
                break;
            case 7:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_scarlet_red, null);
                break;
            case 8:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_dracula, null);
                break;
            case 9:
                // Usethe custom MLH keyboard view
                MlhKeyboardView mlhKeyboardView = (MlhKeyboardView) getLayoutInflater().inflate(R.layout.theme_mlh, null);
                mlhKeyboardView.setCustomBackground();
                keyboardView = mlhKeyboardView;
                break;
            default:
                keyboardView = (MaoKeyboardView) getLayoutInflater().inflate(R.layout.theme_dark, null);
        }
        if (keyboardView != null) {
            keyboardView.post(() -> {
                keyboardView.requestLayout();
                keyboardView.invalidate();
            });
        }
    }

    private EmojiKeyboardView emojiKeyboardView;

    private EmojiKeyboardView getEmojiKeyboardView() {
        previousInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        // Always create a new instance to ensure theme is properly applied
        emojiKeyboardView = (EmojiKeyboardView) getLayoutInflater().inflate(R.layout.emoji_keyboard_layout, null);
        return emojiKeyboardView;
    }


    @Override
    public View onCreateInputView() {
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

        //Special handling for MLH theme
        if (PrefManager.getKeyboardTheme(this) == 9) {
            // Refresh the custom background
            if (keyboardView instanceof MlhKeyboardView) {
                ((MlhKeyboardView) keyboardView).setCustomBackground();
            }
        } else if (keyboardView != null) {
            // Apply background resource for all other themes
            keyboardView.setBackgroundResource(Utils.getThemeBackgroundResource(this));
        }

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
        if (tai2Keyboard == null) tai2Keyboard = new ShanKeyboard(this, R.xml.tai2, "tai2");
        return tai2Keyboard;
    }

    public MaoKeyboard getTai1Keyboard() {
        if (tai1Keyboard == null) tai1Keyboard = new ShanKeyboard(this, R.xml.tai1, "tai1");
        return tai1Keyboard;
    }

    public MaoKeyboard getKhamti1Keyboard() {
        if (khamti1Keyboard == null)
            khamti1Keyboard = new MaoKeyboard(this, R.xml.tai_khamti_qwerty, "khamti1");
        return khamti1Keyboard;
    }

    public MaoKeyboard getKhamti2Keyboard() {
        if (khamti2Keyboard == null)
            khamti2Keyboard = new MaoKeyboard(this, R.xml.tai_khamti_shifted, "khamti2");
        return khamti2Keyboard;
    }

    public MaoKeyboard getThai1Keyboard() {
        if (thai1Keyboard == null) thai1Keyboard = new MaoKeyboard(this, R.xml.th_qwerty, "thai1");
        return thai1Keyboard;
    }

    public MaoKeyboard getThai2Keyboard() {
        if (thai2Keyboard == null)
            thai2Keyboard = new MaoKeyboard(this, R.xml.th_shifted_qwerty, "thai2");
        return thai2Keyboard;
    }

    public MaoKeyboard getTham1Keyboard() {
        if (tham1Keyboard == null)
            tham1Keyboard = new MaoKeyboard(this, R.xml.tai_tham_qwerty, "tham1");
        return tham1Keyboard;
    }

    private MaoKeyboard getTham2Keyboard() {
        if (tham2Keyboard == null)
            tham2Keyboard = new MaoKeyboard(this, R.xml.tai_tham_shifted_qwerty, "tham2");
        return tham2Keyboard;
    }

    public MaoKeyboard getBm2Keyboard() {
        if (bm2Keyboard == null) bm2Keyboard = new BamarKeyboard(this, R.xml.burma2, "bm2");
        return bm2Keyboard;
    }

    public MaoKeyboard getBm1Keyboard() {
        if (bm1Keyboard == null) bm1Keyboard = new BamarKeyboard(this, R.xml.burma1, "bm1");
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

    private MaoKeyboard getTaile1Keyboard() {
        if (taile1Keyboard == null)
            taile1Keyboard = new MaoKeyboard(this, R.xml.taile_normal, "taile1");
        return taile1Keyboard;
    }

    public MaoKeyboard getTaile2Keyboard() {
        if (taile2Keyboard == null)
            taile2Keyboard = new MaoKeyboard(this, R.xml.taile_shift, "taile2");
        return taile2Keyboard;
    }

    private MaoKeyboard taiLue1Keyboard, taiLue2Keyboard;

    private MaoKeyboard getTaiLue1Keyboard() {
        if (taiLue1Keyboard == null)
            taiLue1Keyboard = new MaoKeyboard(this, R.xml.tai_lue_qwerty, "tailue1");
        return taiLue1Keyboard;
    }

    private MaoKeyboard getTaiLue2Keyboard() {
        if (taiLue2Keyboard == null)
            taiLue2Keyboard = new MaoKeyboard(this, R.xml.tai_lue_shifted, "tailue2");
        return taiLue2Keyboard;
    }

    private MaoKeyboard taiDam1Keyboard, taiDam2Keyboard;

    private MaoKeyboard getTaiDam1Keyboard() {
        if (taiDam1Keyboard == null)
            taiDam1Keyboard = new MaoKeyboard(this, R.xml.tai_dam_qwerty, "taidam1");
        return taiDam1Keyboard;
    }

    private MaoKeyboard getTaiDam2Keyboard() {
        if (taiDam2Keyboard == null)
            taiDam2Keyboard = new MaoKeyboard(this, R.xml.tai_dam_shifted, "taidam2");
        return taiDam2Keyboard;
    }

    private MaoKeyboard ahom1Keyboard, ahom2Keyboard;

    public MaoKeyboard getAhom1Keyboard() {
        if (ahom1Keyboard == null)
            ahom1Keyboard = new MaoKeyboard(this, R.xml.tai_ahom_normal, "ahom1");
        return ahom1Keyboard;
    }

    public MaoKeyboard getAhom2Keyboard() {
        if (ahom2Keyboard == null)
            ahom2Keyboard = new MaoKeyboard(this, R.xml.tai_ahom_shifted, "ahom2");
        return ahom2Keyboard;
    }

    private MaoKeyboard getKeyboardFromId(String id) {
        return switch (id) {
            case "eng1", "eng2" -> getEng1Keyboard();
            case "bm1", "bm2" -> getBm1Keyboard();
            case "tai1", "tai2" -> getTai1Keyboard();
            case "taile1", "taile2" -> getTaile1Keyboard();
            case "thai1", "thai2" -> getThai1Keyboard();
            case "khamti1", "khamti2" -> getKhamti1Keyboard();
            case "tham1", "tham2" -> getTham1Keyboard();
            case "tailue1", "tailue2" -> getTaiLue1Keyboard();
            case "taidam1", "taidam2" -> getTaiDam1Keyboard();
            case "ahom1", "ahom2" -> getAhom1Keyboard();
            default -> getEng1Keyboard();
        };
    }


    private boolean isLanguageKeyboard() {
        return
                currentKeyboard == getEng1Keyboard() || currentKeyboard == getEng2Keyboard() ||
                        currentKeyboard == getBm1Keyboard() || currentKeyboard == getBm2Keyboard() ||
                        currentKeyboard == getTai1Keyboard() || currentKeyboard == getTai2Keyboard() ||
                        currentKeyboard == getTaile1Keyboard() || currentKeyboard == getTaile2Keyboard() ||
                        currentKeyboard == getThai1Keyboard() || currentKeyboard == getThai2Keyboard() ||
                        currentKeyboard == getKhamti1Keyboard() || currentKeyboard == getKhamti2Keyboard() ||
                        currentKeyboard == getTham1Keyboard() || currentKeyboard == getTham2Keyboard() ||
                        currentKeyboard == getTaiLue1Keyboard() || currentKeyboard == getTaiLue2Keyboard() ||
                        currentKeyboard == getTaiDam1Keyboard() || currentKeyboard == getTaiDam2Keyboard() ||
                        currentKeyboard == getAhom1Keyboard() || currentKeyboard == getAhom2Keyboard()
                ;
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

    public void convertTaimao() {
        InputConnection ic = getCurrentInputConnection();
        ic.performContextMenuAction(android.R.id.selectAll);
        CharSequence charSequence = ic.getSelectedText(0);
        String convertedText, selectedText2;
        if (!TextUtils.isEmpty(charSequence)) {
            selectedText2 = charSequence.toString();

            if (MaoDetector.isLeikTaiMao(selectedText2)) {
                convertedText = TaiNueaConverter.tdd2shn(selectedText2);
            } else {
                convertedText = TaiNueaConverter.shn2tdd(selectedText2);
            }
            ic.commitText(convertedText, 1);
        }
    }

    public void convertZawgyi() {
        InputConnection ic = getCurrentInputConnection();
        ic.performContextMenuAction(android.R.id.selectAll);
        CharSequence charSequence = ic.getSelectedText(0);
        String convertedText, selectedText2;
        if (!TextUtils.isEmpty(charSequence)) {
            selectedText2 = charSequence.toString();
            if (MaoDetector.isShanLanguage(selectedText2)) {
                // FORSHAN CONVERTER
                if (MaoDetector.isShanZawgyi(selectedText2)) {
                    convertedText = ShanZawgyiConverter.zg2uni(selectedText2);
                } else {
                    convertedText = ShanZawgyiConverter.uni2zg(selectedText2);
                }
            } else {
// FORBURMESE CONVERTER
                if (MaoDetector.isBurmeseZawgyi(getApplicationContext(), selectedText2)) {
                    convertedText = Rabbit.zg2uni(selectedText2);
                } else {
                    convertedText = Rabbit.uni2zg(selectedText2);
                }
            }

            ic.commitText(convertedText, 1);
        }
    }

    public boolean isMyanmarKeyboard() {
        return currentKeyboard == bm1Keyboard || currentKeyboard == bm2Keyboard;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
// emoji
        if ((primaryCode >= 128000) && (primaryCode <= 128567)) {
            ic.commitText(new String(Character.toChars(primaryCode)), 1);
            return;
        }

        // tai ahom
        if ((primaryCode >= 71424) && (primaryCode <= 71487)) {
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

                    if (currentKeyboard == getTai1Keyboard() || currentKeyboard == getTai2Keyboard()) {
                        ((ShanKeyboard) currentKeyboard).handleShanDelete(ic);
                    } else if (currentKeyboard == getBm1Keyboard() || currentKeyboard == getBm2Keyboard()) {
                        ((BamarKeyboard) currentKeyboard).handleMyanmarDelete(ic);
                    } else {
                        deleteHandle(ic);
                    }
                } else {
                    ic.commitText("", 1);
                }
                break;
            case Keyboard.KEYCODE_DONE:
                handleImeAction();
                break;
            case -130: // switch toemoji keyboard
                Utils.setKeyboardBeforeChangeToEmoji(currentKeyboard);
                Utils.setEmojiKeyboard(true);
                previousKeyboard = currentKeyboard;
                View emojiView = getEmojiKeyboardView().getView();

                if (emojiView != null) {
                    emojiView.post(() -> {
                        emojiView.requestLayout();
                        emojiView.invalidate();
                    });
                }

                setInputView(emojiView);
                resetCapsAndShift();
                break;

            case -1001:
                break;
            case -101: // switch language
                changeLanguages();
                resetCapsAndShift();
                break;
            case -123: // switch to eng symbol
                previousKeyboard = currentKeyboard;
                changeKeyboard(getEngSymbolKeyboard());
                resetCapsAndShift();
                break;
            case -321: // switch from engsymbol to normal keyboard
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
                resetCapsAndShift();
                break;
            case -1:
                if (shifted) {
// Unshift
                    checkToggleCapsLock();
                    if (capsLock) {
                        capsLock = false;
                        caps = true;
                        return;
                    }
                    unShiftKeyboard();
                    resetCapsAndShift();
                } else {
// Shift
                    checkToggleCapsLock();
                    shiftKeyboard();
                }
                break;
            case -151: // shift : taile1to taile2
                changeKeyboard(getTaile2Keyboard());
                checkToggleCapsLock();
                shifted = true;
                break;
            case -152: // un-shift : taile2 to taile1
                checkToggleCapsLock();
                if (capsLock) {
                    capsLock = false;
                    caps = true;
                    return;
                }
                changeKeyboard(getTaile1Keyboard());
                resetCapsAndShift();
                break;
            case -112: // shift : burma1to burma2
                changeKeyboard(getBm2Keyboard());
                checkToggleCapsLock();
                shifted = true;
                break;
            case -121: // un-shift :burma2to burma1
                checkToggleCapsLock();
                if (capsLock) {
                    capsLock = false;
                    caps = true;
                    return;
                }
                changeKeyboard(getBm1Keyboard());
                resetCapsAndShift();
                break;
            case -212: // shift :tai1to tai2
                 changeKeyboard(getTai2Keyboard());
                checkToggleCapsLock();
                shifted = true;
                break;
            case -221: // un-shift : tai2 totai1
                checkToggleCapsLock();
                if (capsLock) {
                    capsLock = false;
                    caps = true;
                    return;
                }
                changeKeyboard(getTai1Keyboard());
                resetCapsAndShift();
                break;
            case -412:// shift : eng1 to eng2
                changeKeyboard(getEng2Keyboard());
                checkToggleCapsLock();
                shifted = true;
                break;
            case -421:// un-shift : eng2 to eng1
                checkToggleCapsLock();
                if (capsLock) {
                    capsLock = false;
                    caps = true;
                    return;
                }
                changeKeyboard(getEng1Keyboard());
                resetCapsAndShift();
                break;
            case -501: //switch toeng number keyboard
                changeKeyboard(getEngNumbersKeyboard());
                resetCapsAndShift();
                break;
            case -521: // switchto corresponding symbol keyboard
                if (previousKeyboard == getBm1Keyboard() || previousKeyboard == getBm2Keyboard()) {
                    changeKeyboard(getBurmaSymbolKeyboard());
                } else if (previousKeyboard == getTai1Keyboard() || previousKeyboard == getTai2Keyboard()) {
                    changeKeyboard(getTaiSymbolKeyboard());
                } else if (previousKeyboard == getEng1Keyboard() || previousKeyboard == getEng2Keyboard()) {
                    changeKeyboard(getEngSymbolKeyboard());
                }
                resetCapsAndShift();
                break;
            default:
                char code = (char) primaryCode;
                String cText = String.valueOf(code);
                if (currentKeyboard == getTai1Keyboard() || currentKeyboard == getTai2Keyboard()) {
                    cText = ((ShanKeyboard) currentKeyboard).handleShanInputText(primaryCode, ic);
                } else if (currentKeyboard == getBm1Keyboard() || currentKeyboard == getBm2Keyboard()) {
                    cText = ((BamarKeyboard) currentKeyboard).handelMyanmarInputText(primaryCode, ic);

                }
                ic.commitText(cText, 1);
                if (shifted && !caps) {
                    unShiftKeyboard();
                }
        }
    }

    private void shiftKeyboard() {
        if (currentKeyboard == getBm1Keyboard()) {
            changeKeyboard(getBm2Keyboard());
        } else if (currentKeyboard == getTai1Keyboard()) {
            changeKeyboard(getTai2Keyboard());
        } else if (currentKeyboard == getEng1Keyboard()) {
            changeKeyboard(getEng2Keyboard());
        } else if (currentKeyboard == getTaile1Keyboard()) {
            changeKeyboard(getTaile2Keyboard());
        } else if (currentKeyboard == getThai1Keyboard()) {
            changeKeyboard(getThai2Keyboard());
        } else if (currentKeyboard == getKhamti1Keyboard()) {
            changeKeyboard(getKhamti2Keyboard());
        } else if (currentKeyboard == getTham1Keyboard()) {
            changeKeyboard(getTham2Keyboard());
        } else if (currentKeyboard == getTaiLue1Keyboard()) {
            changeKeyboard(getTaiLue2Keyboard());
        } else if (currentKeyboard == getTaiDam1Keyboard()) {
            changeKeyboard(getTaiDam2Keyboard());
        } else if (currentKeyboard == getAhom1Keyboard()) {
            changeKeyboard(getAhom2Keyboard());
        }
        shifted = true;
        keyboardView.invalidateAllKeys();
    }

    private void unShiftKeyboard() {
        if (currentKeyboard == getBm2Keyboard()) {
            changeKeyboard(getBm1Keyboard());
        } else if (currentKeyboard == getTai2Keyboard()) {
            changeKeyboard(getTai1Keyboard());
        } else if (currentKeyboard == getEng2Keyboard()) {
            changeKeyboard(getEng1Keyboard());
        } else if (currentKeyboard == getTaile2Keyboard()) {
            changeKeyboard(getTaile1Keyboard());
        } else if (currentKeyboard == getThai2Keyboard()) {
            changeKeyboard(getThai1Keyboard());
        } else if (currentKeyboard == getKhamti2Keyboard()) {
            changeKeyboard(getKhamti1Keyboard());
        } else if (currentKeyboard == getTham2Keyboard()) {
            changeKeyboard(getTham1Keyboard());
        } else if (currentKeyboard == getTaiLue2Keyboard()) {
            changeKeyboard(getTaiLue1Keyboard());
        } else if (currentKeyboard == getTaiDam2Keyboard()) {
            changeKeyboard(getTaiDam1Keyboard());
        } else if (currentKeyboard == getAhom2Keyboard()) {
            changeKeyboard(getAhom1Keyboard());
        }
        shifted = false;
        keyboardView.invalidateAllKeys();
    }


    private MaoKeyboard getKeyboardFromKey(String key) {
        if (key.equals(LANGUAGES[0])) return getEng1Keyboard();
        else if (key.equals(LANGUAGES[1])) return getBm1Keyboard();
        else if (key.equals(LANGUAGES[2])) return getTai1Keyboard();
        else if (key.equals(LANGUAGES[3])) return getTaile1Keyboard();
        else if (key.equals(LANGUAGES[4])) return getThai1Keyboard();
        else if (key.equals(LANGUAGES[5])) return getKhamti1Keyboard();
        else if (key.equals(LANGUAGES[6])) return getTham1Keyboard();
        else if (key.equals(LANGUAGES[7])) return getTaiLue1Keyboard();
        else if (key.equals(LANGUAGES[8])) return getTaiDam1Keyboard();
        else return getAhom1Keyboard();
    }


    private void switchKeyboard(int index) {
        boolean enabled = false;
        for (int i = index; i < LANGUAGES.length; i++) {
            if (PrefManager.isEnabledLanguage(this, LANGUAGES[i])) {
                changeKeyboard(getKeyboardFromKey(LANGUAGES[i]));
                enabled = true;
                break;
            }
        }
        if (!enabled && currentKeyboard != getEng1Keyboard() && currentKeyboard != getEng2Keyboard())
            changeKeyboard(getEng1Keyboard());
    }


    private void changeLanguages() {

        if (currentKeyboard == getEng1Keyboard() || currentKeyboard == getEng2Keyboard()) {
            switchKeyboard(1);
        } else if (currentKeyboard == getBm1Keyboard() || currentKeyboard == getBm2Keyboard()) {
            switchKeyboard(2);
        } else if (currentKeyboard == getTai1Keyboard() || currentKeyboard == getTai2Keyboard()) {
            switchKeyboard(3);
        } else if (currentKeyboard == getTaile1Keyboard() || currentKeyboard == getTaile2Keyboard()) {
            switchKeyboard(4);
        } else if (currentKeyboard == getThai1Keyboard() || currentKeyboard == getThai2Keyboard()) {
            switchKeyboard(5);
        } else if (currentKeyboard == getKhamti1Keyboard() || currentKeyboard == getKhamti2Keyboard()) {
            switchKeyboard(6);
        } else if (currentKeyboard == getTham1Keyboard() || currentKeyboard == getTham2Keyboard()) {
            switchKeyboard(7);
        } else if (currentKeyboard == getTaiLue1Keyboard() || currentKeyboard == getTaiLue2Keyboard()) {
            switchKeyboard(8);
        } else if (currentKeyboard == getTaiDam1Keyboard() || currentKeyboard == getTaiDam2Keyboard()) {
            switchKeyboard(9);
        } else if (currentKeyboard == getAhom1Keyboard() || currentKeyboard == getAhom2Keyboard()) {
            switchKeyboard(10);
        }
    }


    //Play vibration when click
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
        if (shifted && !caps) {
            unShiftKeyboard();
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


    // Change dark_theme dependon Input Type
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

        // Handle different input types
        int inputType = attribute.inputType;
        int variation = inputType & InputType.TYPE_MASK_VARIATION;

        // Check for phone input
        if ((inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_PHONE) {
            try {
                keyboardView.setKeyboard(getNumberKeyboard());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Check for number input (including decimal numbers)
        else if ((inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_NUMBER) {
            try {
                keyboardView.setKeyboard(getNumberKeyboard());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Check for text inputvariations
        else if ((inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_TEXT) {
            // Email input
            if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ||
                    variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS) {
                try {
                    // UseEnglish keyboard with symbols for email input
                    if (currentKeyboard != getEng1Keyboard() && currentKeyboard != getEng2Keyboard()) {
                        changeKeyboard(getEng1Keyboard());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Password input
            else if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                    variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) {
                try {
                    // Use English keyboard for password input
                    if (currentKeyboard != getEng1Keyboard() && currentKeyboard != getEng2Keyboard()) {
                        changeKeyboard(getEng1Keyboard());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Normaltext input
            else {
                try {
                    changeKeyboard(currentKeyboard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                changeKeyboard(currentKeyboard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Update actionkeybased onimeOptions
        updateActionKey(attribute);
    }

    /**
     * Update the action key based on imeOptions
     */
    private void updateActionKey(EditorInfo attribute) {
        // Get the action from imeOptions
        int action = attribute.imeOptions & EditorInfo.IME_MASK_ACTION;

        // Find the enter key in the current keyboard and update its label/icon
        if (keyboardView != null && keyboardView.getKeyboard() != null) {
            List<Keyboard.Key> keys = keyboardView.getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                // Check if this is the enter key (usually code -4)
                if (key.codes != null && key.codes.length > 0 && key.codes[0] == -4) {
                    // Update the key based on the action
                    switch (action) {
                        case EditorInfo.IME_ACTION_SEARCH:
                            key.label = "Search";
                            key.icon = null;
                            break;
                        case EditorInfo.IME_ACTION_SEND:
                            key.label = "Send";
                            key.icon = null;
                            break;
                        case EditorInfo.IME_ACTION_NEXT:
                            key.label = "Next";
                            key.icon = null;
                            break;
                        case EditorInfo.IME_ACTION_DONE:
                            key.label = "Done";
                            key.icon = null;
                            break;
                        case EditorInfo.IME_ACTION_GO:
                            key.label = "Go";
                            key.icon = null;
                            break;
                        default:
                            // Use default enter key
                            key.label = null;
                            try {
                                key.icon = getResources().getDrawable(R.drawable.key_icon_enter_key_white);
                            } catch (Exception e) {
                                // Fallback if drawable cannot be loaded
                                key.label = "Enter";
                                key.icon = null;
                            }
                            break;
                    }
                    keyboardView.invalidateAllKeys();
                    break;
                }
            }
        }
    }

    @Override
    public void onWindowShown() {
        keyVibrate = PrefManager.isEnabledKeyVibration(getApplicationContext());
        keySound = PrefManager.isEnabledKeySound(getApplicationContext());
        if (keySound) {
            getSoundPool();
        }
        if (Utils.isEmojiKeyboard()) {
            setInputView(onCreateInputView());
            Utils.setEmojiKeyboard(false);
        }
        if (Utils.isThemeChanged()) {
            setInputView(onCreateInputView());
            emojiKeyboardView = null;
            Utils.setThemeChanged(false);
        }
        if (keyboardView == null) initKeyboardView();
        if (keyboardView != null) {
            keyboardView.post(() -> {
                keyboardView.requestLayout();
                keyboardView.invalidate();
            });
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

    /**
     * HandleIMEaction based on the current input field's imeOptions
     */
    private void handleImeAction() {
        InputConnection ic = getCurrentInputConnection();
        EditorInfo editorInfo = getCurrentInputEditorInfo();

        if (ic == null) return;

        if (editorInfo != null) {
            int action = editorInfo.imeOptions & EditorInfo.IME_MASK_ACTION;
            switch (action) {
                case EditorInfo.IME_ACTION_SEARCH:
                    // Send search action
                    ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                    break;
                case EditorInfo.IME_ACTION_SEND:
// Send send action
                    ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
                    break;
                case EditorInfo.IME_ACTION_NEXT:
                    // Move to next field
                    ic.performEditorAction(EditorInfo.IME_ACTION_NEXT);
                    break;
                case EditorInfo.IME_ACTION_DONE:
                    // Close keyboard
                    requestHideSelf(0);
                    break;
                case EditorInfo.IME_ACTION_GO:
                    // Send go action
                    ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                    break;
                default:
                    // Default behavior - send enter key event
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                    break;
            }
        } else {
            // Fallback to default behavior
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
        }
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


    public static void deleteHandle(InputConnection ic) {
        CharSequence charBeforeCursor = ic.getTextBeforeCursor(1, 0);
        int charCodeBeforeCursor = 0;
        if (charBeforeCursor != null && charBeforeCursor.length() > 0) {
            charCodeBeforeCursor = charBeforeCursor.charAt(0);
        }

        if ((charBeforeCursor == null) || (charBeforeCursor.length() <= 0)) {
            return;// fixed on issue of version 1.2, cause=(getText is null)
        }


        // for Emotion & Ahom delete
        if (Character.isLowSurrogate(charBeforeCursor.charAt(0))
                || Character.isHighSurrogate(charBeforeCursor.charAt(0))) {
            ic.deleteSurroundingText(2, 0);
        } else if (Utils.isMyanmarConsonant(charCodeBeforeCursor)) {
            ic.deleteSurroundingText(1, 0);
        } else {
            ic.deleteSurroundingText(1, 0);
        }
    }

    private static String getWordSeparators() {
        return mWordSeparators;
    }

    public static boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    public static boolean isEndOfText(InputConnection ic) {
        CharSequence charAfterCursor = ic.getTextAfterCursor(1, 0);
        if (charAfterCursor == null)
            return true;
        return charAfterCursor.length() <= 0;
    }


    public static boolean isShanConsonant(int code) {
        String separators = shanConsonants;
        return separators.contains(String.valueOf((char) code));
    }

    private long lastShiftTime;
    private boolean capsLock, caps;

    private void checkToggleCapsLock() {
        long now = System.currentTimeMillis();
        if (lastShiftTime + 500 > now) {
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