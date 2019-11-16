/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.onyx.calculator;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Provides vertical scrolling for the input/result EditText.
 */
class CalculatorDisplay extends ViewSwitcher {

    public static final int SCROLL_UP = 0;
    public static final int SCROLL_DOWN = 1;
    public static final int SCROLL_NONE = 2;

    @IntDef({SCROLL_UP, SCROLL_DOWN, SCROLL_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Scroll{}




    private static final String ATTR_MAX_DIGITS = "maxDigits";
    private static final int DEFAULT_MAX_DIGITS = 10;

    // only these chars are accepted from keyboard
    private static final char[] ACCEPTED_CHARS =
        "0123456789.+-*/\u2212\u00d7\u00f7()!%^".toCharArray();

    private static final int ANIM_DURATION = 500;

//    enum Scroll { UP, DOWN, NONE }

    TranslateAnimation inAnimUp;
    TranslateAnimation outAnimUp;
    TranslateAnimation inAnimDown;
    TranslateAnimation outAnimDown;

    private int mMaxDigits = DEFAULT_MAX_DIGITS;

    public CalculatorDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMaxDigits = attrs.getAttributeIntValue(null, ATTR_MAX_DIGITS, DEFAULT_MAX_DIGITS);


    }

    public int getMaxDigits() {
        return mMaxDigits;
    }

    protected void setLogic(Logic logic) {
        NumberKeyListener calculatorKeyListener =
            new NumberKeyListener() {
                public int getInputType() {
                    return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                }

                @Override
                protected char[] getAcceptedChars() {
                    return ACCEPTED_CHARS;
                }

                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    /* the EditText should still accept letters (eg. 'sin')
                       coming from the on-screen touch buttons, so don't filter anything.
                    */
                    return null;
                }
            };

        Editable.Factory factory = new CalculatorEditable.Factory(logic);
        for (int i = 0; i < 2; ++i) {
            EditText text = (EditText) getChildAt(i);
            if (Build.VERSION.SDK_INT >= 16){
                text.setBackground(null);
            } else {
                text.setBackgroundDrawable(null);
            }
            text.setEditableFactory(factory);
            text.setKeyListener(calculatorKeyListener);
            text.setSingleLine();
        }
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        getChildAt(0).setOnKeyListener(l);
        getChildAt(1).setOnKeyListener(l);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        inAnimUp = new TranslateAnimation(0, 0, h, 0);
        inAnimUp.setDuration(ANIM_DURATION);
        outAnimUp = new TranslateAnimation(0, 0, 0, -h);
        outAnimUp.setDuration(ANIM_DURATION);

        inAnimDown = new TranslateAnimation(0, 0, -h, 0);
        inAnimDown.setDuration(ANIM_DURATION);
        outAnimDown = new TranslateAnimation(0, 0, 0, h);
        outAnimDown.setDuration(ANIM_DURATION);
    }

    void insert(String delta) {
        EditText editor = (EditText) getCurrentView();
        int cursor = editor.getSelectionStart();
        editor.getText().insert(cursor, delta);
    }

    void delBackspace(){
        String text = getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        text = text.substring(0,text.length()-1);
        setText(text,SCROLL_NONE);
    }

    EditText getEditText() {
        return (EditText) getCurrentView();
    }

    Editable getText() {
        EditText text = (EditText) getCurrentView();
        return text.getText();
    }

    void setText(CharSequence text, @Scroll int dir) {
        if (getText().length() == 0) {
            dir = SCROLL_NONE;
        }

        // if (dir == Scroll.UP) {
        //     setInAnimation(inAnimUp);
        //     setOutAnimation(outAnimUp);
        // } else if (dir == Scroll.DOWN) {
        //     setInAnimation(inAnimDown);
        //     setOutAnimation(outAnimDown);
        // } else { // Scroll.NONE
        //     setInAnimation(null);
        //     setOutAnimation(null);
        // }

        EditText editText = (EditText) getNextView();
        editText.setText(text);
        //Calculator.log("selection to " + text.length() + "; " + text);
        editText.setSelection(text.length());
        showNext();
    }

    int getSelectionStart() {
        EditText text = (EditText) getCurrentView();
        return text.getSelectionStart();
    }

    @Override
    protected void onFocusChanged(boolean gain, int direction, Rect prev) {
        super.onFocusChanged(gain,direction,prev);
        //Calculator.log("focus " + gain + "; " + direction + "; " + prev);
        if (!gain) {
            requestFocus();
        }
    }
}