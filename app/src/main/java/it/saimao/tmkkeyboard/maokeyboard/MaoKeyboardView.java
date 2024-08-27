/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.saimao.tmkkeyboard.maokeyboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

public class MaoKeyboardView extends KeyboardView {

    private static final int KEYCODE_OPTIONS = -100;
    private final Context context;


    public MaoKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MaoKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected boolean onLongPress(Keyboard.Key key) {
//        Log.d("Kham", "" + key.codes[0]);
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else if (key.codes[0] == -4) {
            ((MaoKeyboardService) getOnKeyboardActionListener()).convertZawgyi();
            return true;
        } else if (key.codes[0] == -123) {
            ((MaoKeyboardService) getOnKeyboardActionListener()).convertTaimao();
            return true;
        } else if (key.codes[0] == -101) {
            ((MaoKeyboardService) getOnKeyboardActionListener()).hideWindow();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("it.saimao.tmkkeyboard", "it.saimao.tmkkeyboard.activities.MainActivity"));
            context.startActivity(intent);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }

//    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
//        final MaoKeyboard keyboard = (MaoKeyboard) getKeyboard();
//        //keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
//        invalidateAllKeys();
//    }

//    @Override
//    protected boolean onLongPress(Keyboard.Key popupKey) {
//        int popupKeyboardId = popupKey.popupResId;
//
//
//        if (popupKeyboardId != 0) {
//
//            mPopupKeyboard = new PopupWindow();
//
//
//            mPopupKeyboard.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
//            mPopupKeyboard.setOutsideTouchable(true);
//            mPopupKeyboard.setFocusable(false);
//
//            mPopupKeyboard.setTouchInterceptor(new OnTouchListener() {
//
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                        mPopupKeyboard.dismiss();
//                        return true;
//                    }
//
//                    return false;
//                }
//            });
//
//
//            if (mMiniKeyboardContainer == null) {
//                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
//                        Context.LAYOUT_INFLATER_SERVICE);
//                mMiniKeyboardContainer = inflater.inflate(R.layout.flat_black_key_popup_layout, null);
//                mMiniKeyboard = (MaoKeyboardView) mMiniKeyboardContainer.findViewById(android.R.id.keyboardView);
//
//                if (mWindowOffset == null) {
//                    mWindowOffset = new int[2];
//                    getLocationInWindow(mWindowOffset);
//                }
//
//
//                mMiniKeyboard.setOnKeyboardActionListener(new OnKeyboardActionListener() {
//                    public void onKey(int primaryCode, int[] keyCodes) {
//
////                        char code = (char) primaryCode;
////                        db.getInstance(getContext()).insertData_words(String.valueOf(code));
//                        mPopupKeyboard.dismiss();
//                        mPopupKeyboard = null;
//                        mMiniKeyboardVisible = false;
//                        getOnKeyboardActionListener().onKey(primaryCode, keyCodes);
//
//                    }
//
//                    public void onText(CharSequence text) {
//                        mPopupKeyboard.dismiss();
//                        mPopupKeyboard = null;
//                    }
//
//                    public void swipeLeft() {
//                    }
//
//                    public void swipeRight() {
//                    }
//
//                    public void swipeUp() {
//                    }
//
//                    public void swipeDown() {
//                    }
//
//                    public void onPress(int primaryCode) {
//
//                    }
//
//                    public void onRelease(int primaryCode) {
//
//                    }
//                });
//
//            } else {
//
//                mMiniKeyboard = (MaoKeyboardView) mMiniKeyboardContainer.findViewById(android.R.id.keyboardView);
//            }
//
//            Keyboard keyboard;
//            if (popupKey.popupCharacters != null) {
//                keyboard = new Keyboard(getContext(), R.xml.keyboard_popup_template,
//                        popupKey.popupCharacters, -1, getPaddingLeft() + getPaddingRight());
//            } else {
//                keyboard = new Keyboard(getContext(), R.xml.keyboard_popup_template);
//            }
//            mMiniKeyboard.setKeyboard(keyboard);
//            mMiniKeyboard.setPopupParent(this);
//            mMiniKeyboardContainer.measure(
//                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
//                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
//
//
//            int popupX = popupKey.x + mWindowOffset[0];
//            popupX -= mMiniKeyboard.getPaddingLeft();
//            int popupY = popupKey.y + mWindowOffset[1];
//            popupY += getPaddingTop();
//            popupY -= mMiniKeyboard.getMeasuredHeight();
//            popupY -= mMiniKeyboard.getPaddingBottom();
//
//            popupX = popupX + popupKey.width - mMiniKeyboardContainer.getMeasuredWidth();
//            popupY = popupY - mMiniKeyboardContainer.getMeasuredHeight();
//            final int x = popupX + mMiniKeyboardContainer.getPaddingRight() + mWindowOffset[0];
//            final int y = popupY + mMiniKeyboardContainer.getPaddingBottom() + mWindowOffset[1] + 70;
//            mMiniKeyboard.setPopupOffset(x < 0 ? 0 : x, y);
//            mMiniKeyboard.setShifted(isShifted());
//
//            mPopupKeyboard.setContentView(mMiniKeyboardContainer);
//
//            mPopupKeyboard.setWidth(mMiniKeyboardContainer.getMeasuredWidth());
//            mPopupKeyboard.setHeight(mMiniKeyboardContainer.getMeasuredHeight());
//            mPopupKeyboard.showAtLocation(this, Gravity.NO_GRAVITY, x, y);
//
////
////            // Inject down event on the key to mini keyboard.
////            long eventTime = SystemClock.uptimeMillis();
////            mMiniKeyboardPopupTime = eventTime;
////            MotionEvent downEvent = generateMiniKeyboardMotionEvent(MotionEvent.ACTION_DOWN, popupKey.x
////                    + popupKey.width / 2, popupKey.y + popupKey.height / 2, eventTime);
////            mMiniKeyboard.onTouchEvent(downEvent);
////            downEvent.recycle();
//
////            View container = (View) mPopupKeyboard.getContentView().getParent();
////            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
////            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
////            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
////            p.dimAmount = 0.3f;
////            wm.updateViewLayout(container, p);
//
//
//            invalidateAllKeys();
//
//            return true;
//        }
//
//        return false;
//    }


//    @Override
//    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        Paint paint = new Paint();
//        paint.setTextAlign(Paint.Align.CENTER);
//        paint.setTextSize(28);
//        paint.setColor(Color.LTGRAY);
//
//        List<Keyboard.Key> keys = getKeyboard().getKeys();
//        for(Keyboard.Key key: keys) {
//            if(key.label != null) {
//                if (key.label.equals("1")) {
//                    canvas.drawText("1", key.x + (key.width - 25), key.y + 40, paint);
//                } else if (key.label.equals("w")) {
//                    canvas.drawText("2", key.x + (key.width - 25), key.y + 40, paint);
//                } else if (key.label.equals("e")) {
//                    canvas.drawText("3", key.x + (key.width - 25), key.y + 40, paint);
//                } else if (key.label.equals("r")) {
//                    canvas.drawText("4", key.x + (key.width - 25), key.y + 40, paint);
//                } else if (key.label.equals("t")) {
//                    canvas.drawText("5", key.x + (key.width - 25), key.y + 40, paint);
//                }
//            }
//
//        }
//    }


}
