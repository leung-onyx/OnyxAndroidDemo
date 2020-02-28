package com.onyx.android.sdk.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.webkit.WebView;

import com.onyx.android.sdk.api.device.epd.EPDMode;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdatePolicy;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.utils.ReflectUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joy on 2016/5/10.
 */
public class IMX6Device extends BaseDevice {

    private static final String TAG = IMX6Device.class.getSimpleName();

    private static IMX6Device sInstance = null;

    private static int sPolicyAutomatic = 0;
    private static int sPolicyGUIntervally = 0;

    private static int sModeDW = 0;
    private static int sModeGU = 0;
    private static int sModeGC = 0;
    private static int sModeAnimation = 0;
    private static int sModeAnimationQuality = 0;
    private static int sModeGC4 = 0;
    private static int sModeRegal = 0;
    private static int sModeRegalD = 0;

    private static final int sSchemeSNAPSHOT = 0;
    private static final int sSchemeQUEUE = 1;
    private static final int sSchemeQUEUE_AND_MERGE = 2;


    /**
     * static int View.getWindowRotation()
     */
    private static Method sMethodGetWindowRotation = null;
    /**
     * static void View.setWindowRotation(int rotation, boolean alwaysSendConfiguration, int animFlags)
     */
    private static Method sMethodSetWindowRotation = null;
    /**
     * View.setUpdatePolicy(int updatePolicy, int guInterval)
     */
    private static Method sMethodSetUpdatePolicy = null;
    /**
     * View.refreshScreen(int updateMode)
     */
    private static Method sMethodRefreshScreen = null;
    private static Method sMethodRefreshScreenRegion = null;
    private static Method sMethodScreenshot = null;
    private static Method sMethodSupportRegal = null;
    private static Method sMethodEnableRegal = null;

    private static Method sMethodMoveTo = null;
    private static Method sMethodMoveToView = null;
    private static Method sMethodSetStrokeColor = null;
    private static Method sMethodSetStrokeStyle = null;
    private static Method sMethodSetStrokeWidth = null;
    private static Method sMethodSetPainterStyle = null;
    private static Method sMethodLineTo = null;
    private static Method sMethodLineToView = null;
    private static Method sMethodQuadTo = null;
    private static Method sMethodQuadToView = null;
    private static Method sMethodGetTouchWidth = null;
    private static Method sMethodGetTouchHeight = null;
    private static Method sMethodGetMaxTouchPressure = null;
    private static Method sMethodGetEpdWidth = null;
    private static Method sMethodGetEpdHeight = null;
    private static Method sMethodMapToView = null;
    private static Method sMethodMapToEpd = null;
    private static Method sMethodMapFromRawTouchPoint = null;
    private static Method sMethodMapToRawTouchPoint = null;
    private static Method sMethodEnablePost = null;
    private static Method sMethodResetEpdPost = null;
    private static Method sMethodSetScreenHandWritingPenState = null;
    private static Method sMethodSetScreenHandWritingRegionLimit = null;
    private static Method sMethodSetScreenHandWritingRegionExclude = null;
    private static Method sMethodApplyGammaCorrection = null;
    private static Method sMethodStartStroke = null;
    private static Method sMethodAddStrokePoint = null;
    private static Method sMethodFinishStroke = null;

    private static Method sMethodEnableA2;
    private static Method sMethodDisableA2;
    private static Method sMethodWebViewSetCssInjectEnabled;

    private static Method sMethodSetQRShowConfig;
    private static Method sMethodSetInfoShowConfig;

    private static Method sMethodGetRemovableSDCardDirectory;

    /**
     * View.postInvalidate(int updateMode)
     */
    private static Method sMethodPostInvalidate = null;
    /**
     * View.invalidate(int updateMode)
     */
    private static Method sMethodInvalidate = null;
    private static Method sMethodInvalidateRect = null;
    private static Method sMethodInSystemFastMode = null;


    /**
     * View.getDefaultUpdateMode()
     */
    private static Method sMethodGetViewDefaultUpdateMode = null;

    /**
     * View.resetUpdateMode()
     */
    private static Method sMethodResetViewUpdateMode = null;

    /**
     * View.setDefaultUpdateMode(int updateMode)
     */
    private static Method sMethodSetViewDefaultUpdateMode = null;

    /**
     * View.getGlobalUpdateMode()
     */
    private static Method sMethodGetSystemDefaultUpdateMode = null;

    /**
     * View.setGlobalUpdateMode(int updateMode)
     */
    private static Method sMethodSetSystemDefaultUpdateMode = null;

    /**
     * View.setFirstDrawUpdateMode(int updateMode)
     */
    private static Method sMethodSetFirstDrawUpdateMode = null;

    /**
     * View.setWaveformAndScheme(int mode, int scheme)
     */
    private static Method sMethodSetSystemUpdateModeAndScheme = null;

    private static Method sMethodApplyApplicationFastMode = null;

    /**
     * View.resetWaveformAndScheme()
     */
    private static Method sMethodClearSystemUpdateModeAndScheme = null;
    private static Method sMethodEnableScreenUpdate = null;
    private static Method sMethodSetDisplayScheme = null;
    private static Method sMethodWaitForUpdateFinished = null;


    private static Method sMethodOpenFrontLight;
    private static Method sMethodCloseFrontLight;
    private static Method sMethodGetFrontLightValue;
    private static Method sMethodSetFrontLightValue;
    private static Method sMethodSetNaturalLightValue;
    private static Method sMethodGetFrontLightConfigValue;
    private static Method sMethodSetFrontLightConfigValue;
    private static Method sMethodLed;
    private static Method sMethodSetLedColor;
    private static Method sMethodSetVCom;
    private static Method sMethodGetVCom;
    private static Method sMethodSetWaveform;
    private static Method sMethodReadSystemConfig;
    private static Method sMethodSaveSystemConfig;
    private static Method sMethodUpdateMetadataDB;
    private static Method sMethodGotoSleep;

    private static Method sMethodUseBigPen;
    private static Method sMethodStopTpd;
    private static Method sMethodStartTpd;
    private static Method sMethodEnableTpd;
    private static Method sMethodHasWifi;
    private static Method sMethodApplyGcOnce;

    private static Method sMethodGetWarmLightConfigValue;
    private static Method sMethodGetColdLightConfigValue;

    private static final String SET_APP_CTP_DISABLE_REGION = "set_app_ctp_disable_region";
    private static final String APP_RESET_CTP_DISABLE_REGION = "app_reset_ctp_disable_region";
    private static final String CHECK_CTP_STATUS = "check_ctp_status";
    private static final String CTP_STATUS = "ctp_status";
    private static final String STATUS = "status";

    private static final String ARGS_REGION_TO_DISABLE = "args_region_to_disable";
    private static final String ARGS_EXCLUDE_REGION = "args_exclude_region";

    private IMX6Device() {
    }

    public int getWindowRotation() {
        if (sMethodGetWindowRotation != null) {
            try {
                return (Integer) sMethodGetWindowRotation.invoke(null);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, e);
            } catch (IllegalAccessException e) {
                Log.w(TAG, e);
            } catch (InvocationTargetException e) {
                Log.w(TAG, e);
            }
        }

        return Surface.ROTATION_0;
    }

    public boolean setWindowRotation(int rotation) {
        if (sMethodSetWindowRotation != null) {
            try {
                final int Surface_FLAGS_ORIENTATION_ANIMATION_DISABLE = 0x000000001;
                sMethodSetWindowRotation.invoke(null, rotation, true, Surface_FLAGS_ORIENTATION_ANIMATION_DISABLE);
                return true;
            } catch (IllegalArgumentException e) {
                Log.w(TAG, e);
            } catch (IllegalAccessException e) {
                Log.w(TAG, e);
            } catch (InvocationTargetException e) {
                Log.w(TAG, e);
            }
        }

        return false;
    }

    public boolean setUpdatePolicy(View view, UpdatePolicy policy, int guInterval) {
        int dst_mode_value = getPolicyValue(policy);

        try {
            assert (sMethodSetUpdatePolicy != null);
            sMethodSetUpdatePolicy.invoke(view, dst_mode_value, guInterval);
            return true;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        return false;
    }

    @Override
    public File getStorageRootDirectory() {
        return new File("/mnt");
    }

    @Override
    public File getExternalStorageDirectory() {
        // TODO or Environment.getExternalStorageDirectory()?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Environment.getExternalStorageDirectory();
        }
        return new File("/mnt/sdcard");
    }

    @Override
    public File getRemovableSDCardDirectory() {
        // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash,
        // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
        //final String SDCARD_MOUNTED_FOLDER = "extsd";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File file = (File) ReflectUtil.invokeMethodSafely(sMethodGetRemovableSDCardDirectory, null);
            if (file != null) {
                return file;
            }
        }
        return new File("/mnt/extsd");
    }

    @Override
    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }

    @Override
    public EPDMode getEpdMode() {
        return EPDMode.AUTO;
    }

    @Override
    public boolean setEpdMode(Context context, EPDMode mode) {
        setSystemUpdateModeAndScheme(getEpdMode(mode), UpdateScheme.QUEUE_AND_MERGE, Integer.MAX_VALUE);
        return false;
    }

    @Override
    public void invalidate(View view, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodInvalidate != null);
            sMethodInvalidate.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    @Override
    public void invalidate(View view, int left, int top, int right, int bottom, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodInvalidateRect != null);
            sMethodInvalidateRect.invoke(view, left, top, right, bottom, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    @Override
    public void postInvalidate(View view, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodPostInvalidate != null);
            Log.d(TAG, "dst mode: " + dst_mode_value);
            sMethodPostInvalidate.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    @Override
    public void refreshScreen(View view, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreen != null);
            sMethodRefreshScreen.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    public void refreshScreenRegion(View view, int left, int top, int width, int height, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreenRegion != null);
            sMethodRefreshScreenRegion.invoke(view, left, top, width, height, dst_mode_value);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void screenshot(View view, int rotation, final String path) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodScreenshot, view, rotation, path);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStrokeColor(int color) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeColor, null, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStrokeStyle(int style) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeStyle, null, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetPainterStyle, null, antiAlias, strokeStyle, joinStyle, capStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStrokeWidth(float width) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeWidth, null, width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveTo(float x, float y, float width) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMoveTo, null, x, y, width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveTo(View view, float x, float y, float width) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMoveToView, null, view, x, y, width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean supportDFB() {
        return (sMethodLineTo != null);
    }

    public boolean supportRegal() {
        if (sMethodSupportRegal == null) {
            return false;
        }
        Boolean value = (Boolean)ReflectUtil.invokeMethodSafely(sMethodSupportRegal, null);
        if (value == null) {
            return false;
        }
        return value.booleanValue();
    }

    @Override
    public void enableRegal(boolean enable) {
        ReflectUtil.invokeMethodSafely(sMethodEnableRegal, null, enable);
    }

    public void lineTo(float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodLineTo, null, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lineTo(View view, float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodLineToView, null, view, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quadTo(float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodQuadTo, null, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quadTo(View view, float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodQuadToView, null, view, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getTouchWidth() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetTouchWidth, null);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public float getTouchHeight() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetTouchHeight, null);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public float getMaxTouchPressure() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetMaxTouchPressure, null);
            return value.floatValue();
        } catch (Exception e) {
        }
        return 1024.0f;
    }

    public float getEpdWidth() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetEpdWidth, null);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public float getEpdHeight() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetEpdHeight, null);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void mapToView(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapToView, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mapToEpd(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapToEpd, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapFromRawTouchPoint(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapFromRawTouchPoint, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapToRawTouchPoint(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapToRawTouchPoint, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodStartStroke, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseWidth;
    }

    public float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodAddStrokePoint, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseWidth;
    }

    public float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodFinishStroke, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseWidth;
    }

    public void enterScribbleMode(View view) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveScribbleMode(View view) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, view, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enablePost(View view, int enable) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, view, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetEpdPost() {
        try {
            ReflectUtil.invokeMethodSafely(sMethodResetEpdPost, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean supportScreenHandWriting() {
        return (sMethodSetScreenHandWritingPenState != null);
    }

    public void setScreenHandWritingPenState(View view, int penState) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingPenState, view, penState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScreenHandWritingRegionLimit(View view) {
        if (view == null) {
            return;
        }
        setScreenHandWritingRegionLimit(view, 0, 0, view.getRight(), view.getBottom());
    }

    public void setScreenHandWritingRegionLimit(View view, int left, int top, int right, int bottom) {
        setScreenHandWritingRegionLimit(view, new int[] { left, top, right, bottom });
    }

    @Override
    public void setScreenHandWritingRegionLimit(View view, int[] array) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingRegionLimit, view, view, array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setScreenHandWritingRegionLimit(View view, Rect[] regions) {
        int array[] = new int[regions.length * 4];
        for (int i = 0; i < regions.length; i++) {
            Rect region = regions[i];

            int left = Math.min(region.left, region.right);
            int top = Math.min(region.top, region.bottom);
            int right = Math.max(region.left, region.right);
            int bottom = Math.max(region.top, region.bottom);

            array[4 * i] = left;
            array[4 * i + 1] = top;
            array[4 * i + 2] = right;
            array[4 * i + 3] = bottom;
        }
        setScreenHandWritingRegionLimit(view, array);
    }

    @Override
    public void setScreenHandWritingRegionExclude(View view, int[] array) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingRegionExclude, view, view, array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setScreenHandWritingRegionExclude(View view, Rect[] regions) {
        int array[] = new int[regions.length * 4];
        for (int i = 0; i < regions.length; i++) {
            Rect region = regions[i];

            int left = Math.min(region.left, region.right);
            int top = Math.min(region.top, region.bottom);
            int right = Math.max(region.left, region.right);
            int bottom = Math.max(region.top, region.bottom);

            array[4 * i] = left;
            array[4 * i + 1] = top;
            array[4 * i + 2] = right;
            array[4 * i + 3] = bottom;
        }
        setScreenHandWritingRegionExclude(view, array);
    }

    public void applyGammaCorrection(boolean apply, int value) {
        ReflectUtil.invokeMethodSafely(sMethodApplyGammaCorrection, null, apply, value);
    }

    @Override
    public boolean enableScreenUpdate(View view, boolean enable) {
        try {
            sMethodEnableScreenUpdate.invoke(view, enable);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public boolean setDisplayScheme(int scheme) {
        ReflectUtil.invokeMethodSafely(sMethodSetDisplayScheme, null, scheme);
        return true;
    }

    public void waitForUpdateFinished() {
        ReflectUtil.invokeMethodSafely(sMethodWaitForUpdateFinished, null);
    }

    public static IMX6Device createDevice() {
        if (sInstance == null) {
            sInstance = new IMX6Device();

            Class<View> cls = View.class;

            // signature of "public static int getWindowRotation()"
            sMethodGetWindowRotation = ReflectUtil.getMethodSafely(cls, "getWindowRotation");
            // signature of "public static void setWindowRotation(int rotation, boolean alwaysSendConfiguration, int animFlags)"
            sMethodSetWindowRotation = ReflectUtil.getMethodSafely(cls, "setWindowRotation", int.class, boolean.class, int.class);
            sMethodInSystemFastMode = ReflectUtil.getMethodSafely(cls, "inSystemFastMode");

            int value_policy_automic = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_ONYX_AUTO_MASK");
            int value_policy_gu_intervally = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_ONYX_GC_MASK");
            int value_mode_regional = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_AUTO_MODE_REGIONAL");
            int value_mode_nowait = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAIT_MODE_NOWAIT");
            int value_mode_wait = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAIT_MODE_WAIT");
            int value_mode_waveform_du = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_DU");
            int value_mode_waveform_animation = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_ANIM");
            int value_mode_waveform_gc4 = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_GC4");
            int value_mode_waveform_gc16 = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_GC16");
            int value_mode_waveform_regal = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_REAGL");
            int value_mode_waveform_regalD = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_REAGL_MODE_REAGLD");
            int value_mode_update_partial = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_UPDATE_MODE_PARTIAL");
            int value_mode_update_full = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_UPDATE_MODE_FULL");

            sPolicyAutomatic = value_policy_automic;
            sPolicyGUIntervally = value_policy_gu_intervally;

            sModeDW = value_mode_regional | value_mode_nowait | value_mode_waveform_du | value_mode_update_partial;
            sModeGU = value_mode_regional | value_mode_nowait | value_mode_waveform_gc16 | value_mode_update_partial;
            sModeGC = value_mode_regional | value_mode_wait | value_mode_waveform_gc16 | value_mode_update_full;
            sModeAnimation = value_mode_regional | value_mode_nowait | value_mode_waveform_animation | value_mode_update_partial;
            sModeAnimationQuality = ReflectUtil.getStaticIntFieldSafely(cls, "UI_A2_QUALITY_MODE");
            sModeGC4 = value_mode_regional | value_mode_nowait | value_mode_waveform_gc4 | value_mode_update_partial;
            sModeRegal = value_mode_regional | value_mode_nowait | value_mode_waveform_regal | value_mode_update_partial;
            sModeRegalD = value_mode_regional | value_mode_nowait | value_mode_waveform_regalD | value_mode_waveform_regal | value_mode_update_partial;

            Class<?> deviceControllerClass = ReflectUtil.classForName("android.hardware.DeviceController");

            // new added methods, separating for compatibility
            sMethodOpenFrontLight = ReflectUtil.getMethodSafely(deviceControllerClass, "openFrontLight", Context.class);
            sMethodCloseFrontLight = ReflectUtil.getMethodSafely(deviceControllerClass, "closeFrontLight", Context.class);
            sMethodGetFrontLightValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getFrontLightValue", Context.class);
            sMethodSetFrontLightValue = ReflectUtil.getMethodSafely(deviceControllerClass, "setFrontLightValue", Context.class, int.class);
            sMethodSetNaturalLightValue = ReflectUtil.getMethodSafely(deviceControllerClass, "setNaturalLightValue", Context.class, int.class);
            sMethodGetFrontLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getFrontLightConfigValue", Context.class);
            sMethodSetFrontLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "setFrontLightConfigValue", Context.class, int.class);
            sMethodUseBigPen = ReflectUtil.getMethodSafely(deviceControllerClass, "useBigPen", boolean.class);
            sMethodStopTpd = ReflectUtil.getMethodSafely(deviceControllerClass, "stopTpd");
            sMethodStartTpd = ReflectUtil.getMethodSafely(deviceControllerClass, "startTpd");
            sMethodGotoSleep = ReflectUtil.getMethodSafely(deviceControllerClass, "gotoSleep", Context.class, long.class);

            sMethodLed = ReflectUtil.getMethodSafely(deviceControllerClass, "led", boolean.class);
            sMethodSetLedColor = ReflectUtil.getMethodSafely(deviceControllerClass, "setLedColor", String.class, int.class);


            sMethodEnableTpd = ReflectUtil.getMethodSafely(cls, "enableOnyxTpd", int.class);
            sMethodHasWifi = ReflectUtil.getMethodSafely(deviceControllerClass, "hasWifi");
            // signature of "public void setUpdatePolicy(int updatePolicy, int guInterval)"
            sMethodSetUpdatePolicy = ReflectUtil.getMethodSafely(cls, "setUpdatePolicy", int.class, int.class);
            // signature of "public void postInvalidate(int updateMode)"
            sMethodPostInvalidate = ReflectUtil.getMethodSafely(cls, "postInvalidate", int.class);
            // signature of "public void refreshScreen(int updateMode)"
            sMethodRefreshScreen = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class);
            sMethodRefreshScreenRegion = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class, int.class, int.class, int.class, int.class);
            sMethodScreenshot = ReflectUtil.getMethodSafely(cls, "screenshot", int.class, String.class);

            sMethodSetStrokeColor = ReflectUtil.getMethodSafely(cls, "setStrokeColor", int.class);
            sMethodSetStrokeStyle = ReflectUtil.getMethodSafely(cls, "setStrokeStyle", int.class);
            sMethodSetStrokeWidth = ReflectUtil.getMethodSafely(cls, "setStrokeWidth", float.class);
            sMethodSetPainterStyle = ReflectUtil.getMethodSafely(cls, "setPainterStyle", boolean.class, Paint.Style.class, Paint.Join.class, Paint.Cap.class);
            sMethodSupportRegal = ReflectUtil.getMethodSafely(cls, "supportRegal");
            sMethodEnableRegal = ReflectUtil.getMethodSafely(cls, "enableRegal", boolean.class);
            sMethodMoveTo = ReflectUtil.getMethodSafely(cls, "moveTo", float.class, float.class, float.class);
            sMethodLineTo = ReflectUtil.getMethodSafely(cls, "lineTo", float.class, float.class, int.class);
            sMethodQuadTo = ReflectUtil.getMethodSafely(cls, "quadTo", float.class, float.class, int.class);
            sMethodMoveToView = ReflectUtil.getMethodSafely(cls, "moveTo", View.class, float.class, float.class, float.class);
            sMethodLineToView = ReflectUtil.getMethodSafely(cls, "lineTo", View.class, float.class, float.class, int.class);
            sMethodQuadToView = ReflectUtil.getMethodSafely(cls, "quadTo", View.class, float.class, float.class, int.class);
            sMethodGetTouchWidth = ReflectUtil.getMethodSafely(cls, "getTouchWidth");
            sMethodGetTouchHeight = ReflectUtil.getMethodSafely(cls, "getTouchHeight");
            sMethodGetMaxTouchPressure = ReflectUtil.getMethodSafely(cls, "getMaxTouchPressure");
            sMethodGetEpdWidth = ReflectUtil.getMethodSafely(cls, "getEpdWidth");
            sMethodGetEpdHeight = ReflectUtil.getMethodSafely(cls, "getEpdHeight");
            sMethodMapToView = ReflectUtil.getMethodSafely(cls, "mapToView", View.class, float[].class, float[].class);
            sMethodMapToEpd = ReflectUtil.getMethodSafely(cls, "mapToEpd", View.class, float[].class, float[].class);
            sMethodMapFromRawTouchPoint = ReflectUtil.getMethodSafely(cls, "mapFromRawTouchPoint", View.class, float[].class, float[].class);
            sMethodMapToRawTouchPoint = ReflectUtil.getMethodSafely(cls, "mapToRawTouchPoint", View.class, float[].class, float[].class);
            sMethodEnablePost = ReflectUtil.getMethodSafely(cls, "enablePost", int.class);
            sMethodResetEpdPost = ReflectUtil.getMethodSafely(cls, "resetEpdPost");
            sMethodSetScreenHandWritingPenState = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingPenState", int.class);
            sMethodSetScreenHandWritingRegionLimit = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingRegionLimit", View.class, int[].class);
            sMethodSetScreenHandWritingRegionExclude = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingRegionExclude", View.class, int[].class);
            sMethodApplyGammaCorrection = ReflectUtil.getMethodSafely(cls, "applyGammaCorrection", boolean.class, int.class);

            sMethodStartStroke = ReflectUtil.getMethodSafely(cls, "startStroke", float.class, float.class, float.class, float.class, float.class, float.class);
            sMethodAddStrokePoint = ReflectUtil.getMethodSafely(cls, "addStrokePoint", float.class, float.class, float.class, float.class, float.class, float.class);
            sMethodFinishStroke = ReflectUtil.getMethodSafely(cls, "finishStroke", float.class, float.class, float.class, float.class, float.class, float.class);

            // signature of "public void invalidate(int updateMode)"
            sMethodInvalidate = ReflectUtil.getMethodSafely(cls, "invalidate", int.class);
            sMethodInvalidateRect = ReflectUtil.getMethodSafely(cls, "invalidate", int.class, int.class, int.class, int.class, int.class);
            // signature of "public void invalidate(int updateMode)"
            sMethodSetViewDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "setDefaultUpdateMode", int.class);
            // signature of "public void invalidate(int updateMode)"
            sMethodGetViewDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "getDefaultUpdateMode");
            sMethodResetViewUpdateMode = ReflectUtil.getMethodSafely(cls, "resetUpdateMode");

            // signature of "public void invalidate(int updateMode)"
            sMethodGetSystemDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "getGlobalUpdateMode");
            // signature of "public void invalidate(int updateMode)"
            sMethodSetSystemDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "setGlobalUpdateMode", int.class);
            // signature of "public void setFirstDrawUpdateMode(int updateMode)"
            sMethodSetFirstDrawUpdateMode = ReflectUtil.getMethodSafely(cls, "setFirstDrawUpdateMode", int.class);
            // signature of "public void setWaveformAndScheme(int mode, int scheme)"
            sMethodSetSystemUpdateModeAndScheme = ReflectUtil.getMethodSafely(cls, "setWaveformAndScheme", int.class, int.class, int.class);
            // signature of "public void resetWaveformAndScheme()"
            sMethodClearSystemUpdateModeAndScheme = ReflectUtil.getMethodSafely(cls, "resetWaveformAndScheme");
            sMethodApplyApplicationFastMode = ReflectUtil.getMethodSafely(cls, "applyApplicationFastMode", String.class, boolean.class, boolean.class);
            sMethodEnableScreenUpdate = ReflectUtil.getMethodSafely(cls, "enableScreenUpdate", boolean.class);
            sMethodSetDisplayScheme = ReflectUtil.getMethodSafely(cls, "setDisplayScheme", int.class);
            sMethodWaitForUpdateFinished = ReflectUtil.getMethodSafely(cls, "waitForUpdateFinished");

            sMethodSetVCom = ReflectUtil.getMethodSafely(deviceControllerClass, "setVCom", Context.class, int.class, String.class);
            sMethodGetVCom = ReflectUtil.getMethodSafely(deviceControllerClass, "getVCom", String.class);
            sMethodSetWaveform = ReflectUtil.getMethodSafely(deviceControllerClass, "updateWaveform", String.class, String.class);
            sMethodReadSystemConfig = ReflectUtil.getMethodSafely(deviceControllerClass, "readSystemConfig", String.class);
            sMethodSaveSystemConfig = ReflectUtil.getMethodSafely(deviceControllerClass, "saveSystemConfig", String.class, String.class);
            sMethodUpdateMetadataDB = ReflectUtil.getMethodSafely(deviceControllerClass, "updateMetadataDB", String.class, String.class);

            sMethodGetWarmLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getWarmLightConfigValue", Context.class);
            sMethodGetColdLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getColdLightConfigValue", Context.class);

            // signature of "public void enableA2()"
            sMethodEnableA2 = ReflectUtil.getMethodSafely(cls, "enableA2");
            // signature of "public void disableA2()"
            sMethodDisableA2 = ReflectUtil.getMethodSafely(cls, "disableA2");

            sMethodWebViewSetCssInjectEnabled = ReflectUtil.getMethodSafely(WebView.class, "setCssInjectEnabled", boolean.class);

            sMethodSetQRShowConfig = ReflectUtil.getMethodSafely(cls,"setQRShowConfig",int.class,int.class,int.class);
            sMethodSetInfoShowConfig = ReflectUtil.getMethodSafely(cls,"setInfoShowConfig",int.class,int.class,int.class);

            sMethodGetRemovableSDCardDirectory = ReflectUtil.getMethodSafely(Environment.class,"getRemovableSDCardDirectory");
            sMethodApplyGcOnce = ReflectUtil.getMethodSafely(cls, "applyGCOnce");
            Log.d(TAG, "init device EINK_ONYX_GC_MASK.");
            return sInstance;
        }
        return sInstance;
    }

    public void useBigPen(boolean use) {
        invokeDeviceControllerMethod(null, sMethodUseBigPen, use);
    }

    public void stopTpd() {
        invokeDeviceControllerMethod(null, sMethodStopTpd);
    }

    public void startTpd() {
        invokeDeviceControllerMethod(null, sMethodStartTpd);
    }

    public void enableTpd(boolean enable) {
        ReflectUtil.invokeMethodSafely(sMethodEnableTpd, null, enable ? 1 : 0);
    }

    @Override
    public UpdateMode getViewDefaultUpdateMode(View view) {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetViewDefaultUpdateMode, view);
        if (res == null) {
            return UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    public void resetViewUpdateMode(View view) {
        ReflectUtil.invokeMethodSafely(sMethodResetViewUpdateMode, view);
    }

    @Override
    public boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetViewDefaultUpdateMode, view, getUpdateMode(mode));
        return res != null;
    }

    @Override
    public UpdateMode getSystemDefaultUpdateMode() {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetSystemDefaultUpdateMode, null);
        if (res == null) {
            return UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    @Override
    public boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemDefaultUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    public boolean setFirstDrawUpdateMode(UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetFirstDrawUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    @Override
    public boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemUpdateModeAndScheme, null, getUpdateMode(mode), getUpdateScheme(scheme), count);
        return res != null;
    }

    @Override
    public boolean clearSystemUpdateModeAndScheme() {
        Object res = ReflectUtil.invokeMethodSafely(sMethodClearSystemUpdateModeAndScheme, null);
        return res != null;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodApplyApplicationFastMode, null, application, enable, clear);
        return res != null;
    }

    @Override
    public int getFrontLightBrightnessMinimum(Context context) {
        return 0;
    }

    @Override
    public int getFrontLightBrightnessMaximum(Context context) {
        return 255;
    }

    @Override
    public boolean openFrontLight(Context context) {
        Boolean succ = (Boolean) this.invokeDeviceControllerMethod(context, sMethodOpenFrontLight, context);
        if (succ == null) {
            return false;
        }
        return succ.booleanValue();
    }

    @Override
    public boolean closeFrontLight(Context context) {
        Boolean succ = (Boolean) this.invokeDeviceControllerMethod(context, sMethodCloseFrontLight, context);
        if (succ == null) {
            return false;
        }

        return succ.booleanValue();
    }

    @Override
    public int getFrontLightDeviceValue(Context context) {
        Integer value = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetFrontLightValue, context);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    @Override
    public boolean setFrontLightDeviceValue(Context context, int value) {
        Object res = this.invokeDeviceControllerMethod(context, sMethodSetFrontLightValue, context, Integer.valueOf(value));
        return res != null;
    }

    @Override
    public boolean setNaturalLightConfigValue(Context context, int value) {
        Object res = this.invokeDeviceControllerMethod(context, sMethodSetNaturalLightValue, context, Integer.valueOf(value));
        return res != null;
    }

    @Override
    public int getFrontLightConfigValue(Context context) {
        Integer res = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetFrontLightConfigValue, context);
        return res.intValue();
    }

    @Override
    public boolean setFrontLightConfigValue(Context context, int value) {
        this.invokeDeviceControllerMethod(context, sMethodSetFrontLightConfigValue, context, Integer.valueOf(value));
        return true;
    }

    @Override
    public List<Integer> getFrontLightValueList(Context context) {
        Integer intValues[] = {0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120, 128, 136, 144, 152, 160};
        return Arrays.asList(intValues);
    }

    @Override
    public void led(Context context, boolean on) {
        this.invokeDeviceControllerMethod(context, sMethodLed, on);
    }

    public boolean setLedColor(final String color, final int on) {
        invokeDeviceControllerMethod(null, sMethodSetLedColor, color, on);
        return true;
    }

    @Override
    public void setVCom(Context context, int value, String path) {
        this.invokeDeviceControllerMethod(context, sMethodSetVCom, context, value, path);
        //return res != null;
    }

    @Override
    public int getVCom(Context context, String path) {
        Integer value = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetVCom, path);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    @Override
    public void updateWaveform(Context context, String path, String target) {
        this.invokeDeviceControllerMethod(context, sMethodSetWaveform, path, target);
        //return res != null;
    }

    @Override
    public String readSystemConfig(Context context, String key) {
        Object result = this.invokeDeviceControllerMethod(context, sMethodReadSystemConfig, key);
        if (result == null || result.equals("")) {
            return "";
        }
        return result.toString();
    }

    @Override
    public boolean saveSystemConfig(Context context, String key, String value) {
        Object saveSuccess = this.invokeDeviceControllerMethod(context, sMethodSaveSystemConfig, key, value);
        return saveSuccess != null && (boolean) saveSuccess;
    }

    @Override
    public void updateMetadataDB(Context context, String path, String target) {
        this.invokeDeviceControllerMethod(context, sMethodUpdateMetadataDB, path, target);
    }

    /**
     * helper method to do trivial argument and exception check, return null if failed
     *
     * @param context
     * @param method
     * @return
     */
    private Object invokeDeviceControllerMethod(Context context, Method method, Object... args) {
        if (method == null) {
            return null;
        }

        return ReflectUtil.invokeMethodSafely(method, null, args);
    }

    UpdateMode getEpdMode(EPDMode mode) {
        switch (mode) {
            case FULL:
                return UpdateMode.GC;
            case AUTO:
            case TEXT:
            case AUTO_PART:
                return UpdateMode.GU;
            default:
                return UpdateMode.DU;
        }
    }

    int getUpdateMode(UpdateMode mode) {
        // default use GC update mode
        int dst_mode = sModeGC;
        switch (mode) {
            case GU_FAST:
            case DU:
                dst_mode = sModeDW;
                break;
            case GU:
                dst_mode = sModeGU;
                break;
            case GC:
                dst_mode = sModeGC;
                break;
            case ANIMATION:
                dst_mode = sModeAnimation;
                break;
            case ANIMATION_QUALITY:
                dst_mode = sModeAnimationQuality;
                break;
            case GC4:
                dst_mode = sModeGC4;
                break;
            case REGAL:
                dst_mode = sModeRegal != 0 ? sModeRegal : sModeGU;
                break;
            case REGAL_D:
                dst_mode = sModeRegalD != 0  ? sModeRegalD : sModeGU;
                break;
            default:
                assert (false);
                break;
        }
        return dst_mode;
    }

    private int getUpdateScheme(UpdateScheme scheme) {
        int dst_scheme = sSchemeQUEUE;
        switch (scheme) {
            case SNAPSHOT:
                dst_scheme = sSchemeSNAPSHOT;
                break;
            case QUEUE:
                dst_scheme = sSchemeQUEUE;
                break;
            case QUEUE_AND_MERGE:
                dst_scheme = sSchemeQUEUE_AND_MERGE;
                break;
            default:
                assert (false);
                break;
        }
        return dst_scheme;

    }

    private UpdateMode updateModeFromValue(int value) {
        if (value == sModeDW) {
            return UpdateMode.DU;
        } else if (value == sModeGU) {
            return UpdateMode.GU;
        } else if (value == sModeGC) {
            return UpdateMode.GC;
        }
        return UpdateMode.GC;
    }

    private static int getPolicyValue(UpdatePolicy policy) {
        int dst_value = sModeGU;
        switch (policy) {
            case Automatic:
                dst_value |= sPolicyAutomatic;
                break;
            case GUIntervally:
                dst_value |= sPolicyGUIntervally;
                break;
            default:
                assert (false);
                break;
        }

        return dst_value;
    }

    @Override
    public void disableA2ForSpecificView(View view) {
        ReflectUtil.invokeMethodSafely(sMethodDisableA2, view);
    }

    @Override
    public void enableA2ForSpecificView(View view) {
        ReflectUtil.invokeMethodSafely(sMethodEnableA2, view);
    }

    @Override
    public void setWebViewContrastOptimize(WebView view, boolean enabled) {
        ReflectUtil.invokeMethodSafely(sMethodWebViewSetCssInjectEnabled, view, enabled);
    }

    @Override
    public void setQRShowConfig(int orientation, int startX, int startY) {
        ReflectUtil.invokeMethodSafely(sMethodSetQRShowConfig, null, orientation, startX, startY);
    }

    @Override
    public void setInfoShowConfig(int orientation, int startX, int startY) {
        ReflectUtil.invokeMethodSafely(sMethodSetInfoShowConfig, null, orientation, startX, startY);
    }

    public void gotoSleep(final Context context) {
        long value = System.currentTimeMillis();
        ReflectUtil.invokeMethodSafely(sMethodGotoSleep, context, value);
    }

    @Override
    public boolean hasWifi(Context context)
    {
        Boolean has = (Boolean)this.invokeDeviceControllerMethod(context,  sMethodHasWifi);
        if (has == null) {
            return false;
        }

        return has.booleanValue();
    }

    @Override
    public boolean inSystemFastMode() {
        Boolean value = (Boolean)ReflectUtil.invokeMethodSafely(sMethodInSystemFastMode, null);
        if (value == null) {
            return false;
        }
        return value.booleanValue();
    }

    @Override
    public void applyGCOnce() {
        ReflectUtil.invokeMethodSafely(sMethodApplyGcOnce, null);
    }

    /**
     * TODO:imx6 device should register broadcast receiver to detect ctp status,don't rely this direct return value.
     * e.g:
     * BroadcastReceiver mCTPReceiver = new BroadcastReceiver() {
     *         @Override
     *         public void onReceive(Context context, Intent intent) {
     *             String action = intent.getAction();
     *             if(action.equals(CTP_STATUS)){
     *                  boolean result = intent.getBooleanExtra(STATUS, false);
     *             }
     *         }
     *     };
     */
    @Override
    public boolean isCTPDisableRegion(Context context) {
        Intent intent = new Intent(APP_RESET_CTP_DISABLE_REGION);
        context.sendBroadcast(intent);
        return false;
    }

    @Override
    public void appResetCTPDisableRegion(Context context) {
        Intent intent = new Intent(APP_RESET_CTP_DISABLE_REGION);
        context.sendBroadcast(intent);
    }

    @Override
    public void setAppCTPDisableRegion(Context context, int[] disableRegionArray, @Nullable int[] excludeRegionArray) {
        Intent intent = new Intent(SET_APP_CTP_DISABLE_REGION);
        intent.putExtra(ARGS_REGION_TO_DISABLE, disableRegionArray);
        intent.putExtra(ARGS_EXCLUDE_REGION, excludeRegionArray);
        context.sendBroadcast(intent);
    }

    @Override
    public void setAppCTPDisableRegion(Context context, Rect[] disableRegions, @Nullable Rect[] excludeRegions) {
        setAppCTPDisableRegion(context, convertRectArrayToIntArray(disableRegions), convertRectArrayToIntArray(excludeRegions));
    }

    @Override
    public int getColdLightConfigValue(Context context) {
        Integer res = (Integer)this.invokeDeviceControllerMethod(context, sMethodGetColdLightConfigValue, context);
        return res.intValue();
    }

    @Override
    public int getWarmLightConfigValue(Context context) {
        Integer res = (Integer)this.invokeDeviceControllerMethod(context, sMethodGetWarmLightConfigValue, context);
        return res.intValue();
    }
}
