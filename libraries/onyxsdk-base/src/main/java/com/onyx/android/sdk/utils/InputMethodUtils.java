package com.onyx.android.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

/**
 * Created by solskjaer49 on 14/9/30 16:30.
 */
public class InputMethodUtils {
    private static final int SOFT_KEYBOARD_HEIGHT = 70;

    public static void showForcedInputKeyboard(Context context, View focusView) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);
    }

    public static void showForcedInputKeyboardForFragment(Context context, View focusView) {
        if (focusView.requestFocus()){
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void hideInputKeyboard(Context context) {
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null && view.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void toggleStatus(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isShow(Context context, View focusView) {
        Object obj = context.getSystemService(Context.INPUT_METHOD_SERVICE);
        System.out.println(obj);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean bool = imm.isActive(focusView);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();
        for (InputMethodInfo imi : mInputMethodProperties) {
            if (imi.getId().equals(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD))) {
                //imi contains the information about the keyboard you are using
                break;
            }
        }
        return bool;
    }

    /**
     * @param rootView any view.getRootView in activity
     **/
    public static boolean isShow(View rootView) {
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > SOFT_KEYBOARD_HEIGHT * dm.density;
    }

    public static void alwaysHideSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}

