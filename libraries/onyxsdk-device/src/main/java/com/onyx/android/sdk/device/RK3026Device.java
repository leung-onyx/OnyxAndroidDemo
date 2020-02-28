/**
 *
 */
package com.onyx.android.sdk.device;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.onyx.android.sdk.device.BuildConfig;
import com.onyx.android.sdk.api.device.epd.EPDMode;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.ReflectUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author jim
 */
public class RK3026Device extends BaseDevice {

    private final static String TAG = "RK3026Device";

    private static RK3026Device sInstance;

    private final static int DEFAULT_VIEW_MODE = 1;
    private final static String DEFAULT_EPD_MODE = "1"; // which standing for A2 according to RK3026 specification

    private final static String SYSTEM_PROPERTIES_QUILIFIED_NAME = "android.os.SystemProperties";

    private static Class<Enum> sEinkModeEnumClass = null;

    private static Method sMethodViewRequestEpdMode = null;
    private static Method sMethodViewRequestEpdModeForce = null;
    private static int sViewNull = DEFAULT_VIEW_MODE;
    private static int sViewFull = DEFAULT_VIEW_MODE;
    private static int sViewA2 = DEFAULT_VIEW_MODE;
    private static int sViewAuto = DEFAULT_VIEW_MODE;
    private static int sViewPart = DEFAULT_VIEW_MODE;
    private static int sViewRegla = DEFAULT_VIEW_MODE;

    private static final int INDEX_EPD_NULL = 0;
    private static final int INDEX_EPD_AUTO = 1;
    private static final int INDEX_EPD_FULL = 2;
    private static final int INDEX_EPD_A2 = 3;
    private static final int INDEX_EPD_PART = 4;
    private static final int INDEX_EPD_REGLA = 16;

    private static final String NAME_EPD_NULL = "EPD_NULL";
    private static final String NAME_EPD_AUTO = "EPD_AUTO";
    private static final String NAME_EPD_FULL = "EPD_FULL";
    private static final String NAME_EPD_A2 = "EPD_A2";
    private static final String NAME_EPD_PART = "EPD_PART";
    private static final String NAME_EPD_REGLA = "EPD_REGLA";

    private Context mContext = null;

    private EPDMode mCurrentMode = EPDMode.AUTO;
    private UpdateMode mUpdateMode = UpdateMode.GU;

    private static Method sMethodIsTouchable;
    private static Method sMethodGetTouchType;
    private static Method sMethodHasWifi;
    private static Method sMethodHasAudio;
    private static Method sMethodHasFrontLight;
    private static Method sMethodHasBluetooth;

    private static Method sMethodOpenFrontLight;
    private static Method sMethodCloseFrontLight;
    private static Method sMethodGetFrontLightValue;
    private static Method sMethodSetFrontLightValue;
    private static Method sMethodGetFrontLightConfigValue;
    private static Method sMethodSetFrontLightConfigValue;
    private static Method sMethodGetFrontLightValueList;
    private static Method sMethodReadSystemConfig;
    private static Method sMethodSaveSystemConfig;

    private static Method sMethodHasNaturalBrightness;
    private static Method sMethodGetWarmLightConfigValue;
    private static Method sMethodGetColdLightConfigValue;
    private static Method sMethodSetWarmLightDeviceValue;
    private static Method sMethodSetColdLightDeviceValue;
    private static Method sMethodSetWarmLightValue;
    private static Method sMethodSetColdLightValue;
    private static Method sMethodIncreaseBrightness;
    private static Method sMethodDecreaseBrightness;

    private static Method sMethodStopBootAnimation;
    private static Method sMethodLed;

    private static Method sMethodEnableA2;
    private static Method sMethodDisableA2;
    private static Method sMethodSystemIntegrityCheck;
    private static Method sMethodSupportRegal;
    private static Method sMethodHoldDisplay;
    private static Method sMethodEnableRegal;

    private static final String UNKNOWN = "unknown";
    private static final String DEVICE_ID = "ro.deviceid";

    private RK3026Device() {

    }

    public static RK3026Device createDevice() {
        if (sInstance == null) {
            sInstance = new RK3026Device();

            try {
                Class<View> class_view = View.class;

                sEinkModeEnumClass = (Class<Enum>) Class.forName("android.view.View$EINK_MODE");

                sMethodViewRequestEpdMode = class_view.getMethod("requestEpdMode", sEinkModeEnumClass);
                sMethodViewRequestEpdModeForce = class_view.getMethod("requestEpdMode", sEinkModeEnumClass, boolean.class);
                Object[] einkModeConstants = sEinkModeEnumClass.getEnumConstants();
                Class<?> sub = einkModeConstants[0].getClass();
                Method mth = sub.getDeclaredMethod("getValue");
                sViewNull = (Integer) mth.invoke(einkModeConstants[INDEX_EPD_NULL]);
                sViewAuto = (Integer) mth.invoke(einkModeConstants[INDEX_EPD_AUTO]);
                sViewFull = (Integer) mth.invoke(einkModeConstants[INDEX_EPD_FULL]);
                sViewA2 = (Integer) mth.invoke(einkModeConstants[INDEX_EPD_A2]);
                sViewPart = (Integer) mth.invoke(einkModeConstants[INDEX_EPD_PART]);
                if (einkModeConstants.length > INDEX_EPD_REGLA) {
                    sViewRegla = (Integer) mth.invoke(einkModeConstants[INDEX_EPD_REGLA]);
                } else {
                    sViewRegla = sViewPart;
                }
                sMethodSupportRegal = ReflectUtil.getMethodSafely(class_view, "supportRegal");
                sMethodHoldDisplay = ReflectUtil.getMethodSafely(class_view, "holdDisplay", boolean.class, int.class, int.class);
                sMethodEnableRegal = ReflectUtil.getMethodSafely(class_view, "enableRegal", boolean.class);

                Class class_device_controller = Class.forName("android.hardware.DeviceController");

                sMethodIsTouchable = ReflectUtil.getMethodSafely(class_device_controller, "isTouchable");
                sMethodGetTouchType = ReflectUtil.getMethodSafely(class_device_controller, "getTouchType");
                sMethodHasWifi = ReflectUtil.getMethodSafely(class_device_controller, "hasWifi");
                sMethodHasAudio = ReflectUtil.getMethodSafely(class_device_controller, "hasAudio");
                sMethodHasFrontLight = ReflectUtil.getMethodSafely(class_device_controller, "hasFrontLight");
                sMethodHasNaturalBrightness = ReflectUtil.getMethodSafely(class_device_controller, "hasNaturalLight");
                sMethodHasBluetooth = ReflectUtil.getMethodSafely(class_device_controller, "hasBluetooth");

                // new added methods, separating for compatibility
                sMethodOpenFrontLight = ReflectUtil.getMethodSafely(class_device_controller, "openFrontLight", Context.class);
                sMethodCloseFrontLight = ReflectUtil.getMethodSafely(class_device_controller, "closeFrontLight", Context.class);
                sMethodGetFrontLightValue = ReflectUtil.getMethodSafely(class_device_controller, "getFrontLightValue", Context.class);
                sMethodSetFrontLightValue = ReflectUtil.getMethodSafely(class_device_controller, "setFrontLightValue", Context.class, int.class);
                sMethodGetFrontLightConfigValue = ReflectUtil.getMethodSafely(class_device_controller, "getFrontLightConfigValue", Context.class);
                sMethodSetFrontLightConfigValue = ReflectUtil.getMethodSafely(class_device_controller, "setFrontLightConfigValue", Context.class, int.class);
                sMethodGetFrontLightValueList = ReflectUtil.getMethodSafely(class_device_controller, "getFrontLightValues", Context.class);

                sMethodGetWarmLightConfigValue = ReflectUtil.getMethodSafely(class_device_controller, "getWarmLightConfigValue", Context.class);
                sMethodGetColdLightConfigValue = ReflectUtil.getMethodSafely(class_device_controller, "getColdLightConfigValue", Context.class);

                sMethodSetWarmLightDeviceValue = ReflectUtil.getMethodSafely(class_device_controller, "setWarmLightConfigValue", Context.class, int.class);
                sMethodSetColdLightDeviceValue = ReflectUtil.getMethodSafely(class_device_controller, "setColdLightConfigValue", Context.class, int.class);
                sMethodSetWarmLightValue = ReflectUtil.getMethodSafely(class_device_controller, "setWarmLightValue", Context.class, int.class);
                sMethodSetColdLightValue = ReflectUtil.getMethodSafely(class_device_controller, "setColdLightValue", Context.class, int.class);
                sMethodIncreaseBrightness = ReflectUtil.getMethodSafely(class_device_controller, "increaseBrightness", Context.class);
                sMethodDecreaseBrightness = ReflectUtil.getMethodSafely(class_device_controller, "decreaseBrightness", Context.class);

                sMethodReadSystemConfig = ReflectUtil.getMethodSafely(class_device_controller, "readSystemConfig", String.class);
                sMethodSaveSystemConfig = ReflectUtil.getMethodSafely(class_device_controller, "saveSystemConfig", String.class, String.class);
                sMethodSystemIntegrityCheck = ReflectUtil.getMethodSafely(class_device_controller, "systemIntegrityCheck");

                sMethodStopBootAnimation = ReflectUtil.getMethodSafely(class_view, "requestStopBootAnimation");

                sMethodLed = ReflectUtil.getMethodSafely(class_device_controller, "led", boolean.class);

                // signature of "public void enableA2()"
                sMethodEnableA2 = ReflectUtil.getMethodSafely(class_view, "enableA2");
                // signature of "public void disableA2()"
                sMethodDisableA2 = ReflectUtil.getMethodSafely(class_view, "disableA2");


            } catch (ClassNotFoundException e) {
                Log.w(TAG, e);
            } catch (SecurityException e) {
                Log.w(TAG, e);
            } catch (NoSuchMethodException e) {
                Log.w(TAG, e);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, e);
            } catch (IllegalAccessException e) {
                Log.w(TAG, e);
            } catch (InvocationTargetException e) {
                Log.w(TAG, e);
            }
        }
        return sInstance;
    }

    @Override
    public File getStorageRootDirectory() {
        return new File("/mnt");
    }

    @Override
    public File getExternalStorageDirectory() {
        return new File("/mnt/sdcard");
    }

    @Override
    public File getRemovableSDCardDirectory() {
        return new File("/mnt/external_sd");
    }

    @Override
    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }


    @Override
    public PowerManager.WakeLock newWakeLock(Context context, String tag) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, tag);
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
        return (Boolean) this.invokeDeviceControllerMethod(context, sMethodSaveSystemConfig, key, value);
    }

    @Override
    public EPDMode getEpdMode() {
        return mCurrentMode;
    }

    @Override
    public boolean setEpdMode(Context context, EPDMode mode) {
        return false;
    }

    private Object getValueOfEinkModeEnum(String enumName) {
        return Enum.valueOf(sEinkModeEnumClass, enumName);
    }

    private Object getEinkMode(EPDMode mode) {
        String einkModeString = NAME_EPD_NULL;
        switch (mode) {
            case FULL:
                einkModeString = NAME_EPD_FULL;
                break;
            case AUTO:
                einkModeString = NAME_EPD_PART;
                break;
            case TEXT:
                einkModeString = NAME_EPD_PART;
                break;
            case AUTO_PART:
                einkModeString = NAME_EPD_PART;
                break;
            case AUTO_BLACK_WHITE:
                einkModeString = NAME_EPD_A2;
                break;
            case AUTO_A2:
                einkModeString = NAME_EPD_A2;
                break;
            case EPD_REGLA:
                einkModeString = NAME_EPD_REGLA;
                break;
            default:
                assert (false);
                break;
        }
        return getValueOfEinkModeEnum(einkModeString);
    }

    private Object getEinkModeFromUpdateMode(UpdateMode mode) {
        String einkModeString = NAME_EPD_NULL;
        switch (mode) {
            case GU:
                einkModeString = NAME_EPD_PART;
                break;
            case GU_FAST:
                einkModeString = NAME_EPD_PART;
                break;
            case GC:
                einkModeString = NAME_EPD_FULL;
                break;
            case DU:
                einkModeString = NAME_EPD_A2;
                break;
            case REGAL:
                einkModeString = NAME_EPD_REGLA;
                break;
            default:
                assert (false);
                break;
        }
        return getValueOfEinkModeEnum(einkModeString);
    }

    @Override
    public boolean setEpdMode(View view, EPDMode mode) {
        Object einkMode = getEinkMode(mode);
        try {
            sMethodViewRequestEpdMode.invoke(view, einkMode);
            return true;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "exception", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "exception", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "exception", e);
        }
        return false;
    }

    @Override
    public UpdateMode getViewDefaultUpdateMode(View view) {
        return mUpdateMode;
    }

    @Override
    public boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
        Object einkMode = getEinkModeFromUpdateMode(mode);
        if (ReflectUtil.invokeMethodSafely(sMethodViewRequestEpdMode, view, einkMode) != null) {
            mUpdateMode = mode;
            return true;
        }
        return false;
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
    public Integer[] getWarmLightValues(Context context) {
        List<Integer> list = getFrontLightValueList(context);
        return list.toArray(new Integer[list.size()]);
    }

    @Override
    public Integer[] getColdLightValues(Context context) {
        List<Integer> list = getFrontLightValueList(context);
        return list.toArray(new Integer[list.size()]);
    }

    @Override
    public int getWarmLightConfigValue(Context context) {
        Object object = this.invokeDeviceControllerMethod(context, sMethodGetWarmLightConfigValue, context);
        if (object != null) {
            return (Integer) object;
        }
        return 0;
    }

    @Override
    public int getColdLightConfigValue(Context context) {
        Object object = this.invokeDeviceControllerMethod(context, sMethodGetColdLightConfigValue, context);
        if (object != null) {
            return (Integer) object;
        }
        return 0;
    }

    @Override
    public boolean setWarmLightDeviceValue(Context context, int value) {
        this.invokeDeviceControllerMethod(context, sMethodSetWarmLightDeviceValue, context, Integer.valueOf(value));
        this.invokeDeviceControllerMethod(context, sMethodSetWarmLightValue, context, Integer.valueOf(value));
        return true;
    }

    @Override
    public boolean setColdLightDeviceValue(Context context, int value) {
        this.invokeDeviceControllerMethod(context, sMethodSetColdLightDeviceValue, context,  Integer.valueOf(value));
        this.invokeDeviceControllerMethod(context, sMethodSetColdLightValue, context,  Integer.valueOf(value));
        return true;
    }

    @Override
    public boolean increaseBrightness(Context context, int colorTemp) {
        if (hasFLBrightness(context)) {
            Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodIncreaseBrightness, context);
            return result != null;
        } else if (hasCTMBrightness(context)) {
            return adjustBrightness(context, colorTemp, getFrontLightValueList(context), true);
        }
        return false;
    }

    @Override
    public boolean decreaseBrightness(Context context, int colorTemp) {
        if (hasFLBrightness(context)) {
            Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodDecreaseBrightness, context);
            return result != null;
        } else if (hasCTMBrightness(context)) {
            return adjustBrightness(context, colorTemp, getFrontLightValueList(context), false);
        }
        return false;
    }

    @Override
    public List<Integer> getFrontLightValueList(Context context) {
        List<Integer> values = (List<Integer>) this.invokeDeviceControllerMethod(context,
                sMethodGetFrontLightValueList, context);
        return values;
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

    @Override
    public UpdateMode getSystemDefaultUpdateMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void invalidate(View view, UpdateMode mode) {
        Object einkMode = getEinkModeFromUpdateMode(mode);
        try {
            sMethodViewRequestEpdMode.invoke(view, einkMode);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "exception", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "exception", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "exception", e);
        }
        view.invalidate();
    }

    @Override
    public void postInvalidate(View view, UpdateMode mode) {
        Object einkMode = getEinkModeFromUpdateMode(mode);
        try {
            sMethodViewRequestEpdMode.invoke(view, einkMode);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "exception", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "exception", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "exception", e);
        }
        view.postInvalidate();
    }

    @Override
    public String getEncryptedDeviceID() {
        Class<?> classSystemProperties = null;
        try {
            classSystemProperties = Class.forName(SYSTEM_PROPERTIES_QUILIFIED_NAME);
            Log.i(TAG, "Class: " + SYSTEM_PROPERTIES_QUILIFIED_NAME + " found!");
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "Class: " + SYSTEM_PROPERTIES_QUILIFIED_NAME + " not found!");
            return null;
        }
        Method methodGet = null;
        String methodName = "get";
        try {
            methodGet = classSystemProperties.getMethod(methodName, String.class, String.class);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Method: " + methodName + " not found!");
            return null;
        }
        String result = null;
        try {
            result = (String) methodGet.invoke(null, DEVICE_ID, UNKNOWN);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.w(TAG, "invoke " + SYSTEM_PROPERTIES_QUILIFIED_NAME + "." + methodName + " exception, illegal argument!");
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.w(TAG, "invoke " + SYSTEM_PROPERTIES_QUILIFIED_NAME + "." + methodName + " exception, illegal access!");
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.w(TAG, "invoke " + SYSTEM_PROPERTIES_QUILIFIED_NAME + "." + methodName + " exception, invocation target exception!");
            return null;
        }
        return result;
    }

    @Override
    public void stopBootAnimation() {
        this.invokeDeviceControllerMethod(null, sMethodStopBootAnimation, (Object)null);
    }

    @Override
    public void led(Context context, boolean on) {
        this.invokeDeviceControllerMethod(context, sMethodLed, on);
    }

    public boolean supportRegal() {
        Object object = ReflectUtil.invokeMethodSafely(sMethodSupportRegal, null);
        if (object != null) {
            return (Boolean) object;
        }
        return false;
    }

    public void holdDisplay(boolean hold,  UpdateMode updateMode, int ignoreFrame) {
        ReflectUtil.invokeMethodSafely(sMethodHoldDisplay, null, hold, sViewRegla, ignoreFrame);
    }

    @Override
    public int getVCom(Context context, String path) {
        String value = DeviceFileUtils.readContentOfFile(new File(path));
        if (TextUtils.isEmpty(value)) {
            return Integer.MIN_VALUE;
        }
        return Integer.parseInt(value);
    }

    @Override
    public void setVCom(Context context, int value, String path) {
        DeviceFileUtils.saveContentToFile(String.valueOf(value), new File(path));
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
    public boolean isLegalSystem(final Context context) {
        if (BuildConfig.DEBUG) {
            return true;
        }
        return (Boolean)this.invokeDeviceControllerMethod(context, sMethodSystemIntegrityCheck);
    }

    @Override
    public boolean hasWifi(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasWifi);
        return result == null ? false : result;
    }

    @Override
    public boolean hasAudio(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasAudio);
        return result == null ? false : result;
    }

    @Override
    public boolean hasFLBrightness(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasFrontLight);
        return result == null ? false : result;
    }

    @Override
    public boolean hasCTMBrightness(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasNaturalBrightness);
        return result == null ? false : result;
    }

    @Override
    public boolean hasBluetooth(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasBluetooth);
        return result == null ? false : result;
    }

    @Override
    public boolean isTouchable(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodIsTouchable);
        return result == null ? false : result;
    }

    @Override
    public void enableRegal(boolean enable) {
        ReflectUtil.invokeMethodSafely(sMethodEnableRegal, null, enable);
    }

    @Override
    public int getFrontLightTypeCTMWarm() {
        return 2;
    }

    @Override
    public int getFrontLightTypeCTMCold() {
        return 3;
    }

    private boolean adjustBrightness(Context context, final int colorTemp, final List<Integer> steps, final boolean progressive) {
        if (steps.size() == 0) {
            return false;
        }

        int value = 0;
        if (colorTemp == 3) {
            value = getColdLightConfigValue(context);
        } else if (colorTemp == 2) {
            value = getWarmLightConfigValue(context);
        }

        Integer[] lightSteps = steps.toArray(new Integer[steps.size()]);
        int stepPoor = Math.abs(value - lightSteps[0]);
        int result = 0;
        for (int i = 0; i < lightSteps.length; i++) {
            int index =  Math.abs(value - lightSteps[i]);
            if (stepPoor >= index) {
                stepPoor = index;
                result = i;
            }
        }
        result = progressive ? ++result : --result;

        if(result >= 0 && result < lightSteps.length) {
            int currentBrightness = lightSteps[result];
            if (colorTemp == getFrontLightTypeCTMCold()) {
                setColdLightDeviceValue(context, currentBrightness);
            } else if (colorTemp == getFrontLightTypeCTMWarm()) {
                setWarmLightDeviceValue(context, currentBrightness);
            }
        }
        return true;
    }
}
