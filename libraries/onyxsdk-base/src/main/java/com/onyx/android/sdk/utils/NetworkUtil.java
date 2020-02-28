package com.onyx.android.sdk.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.onyx.android.sdk.device.Device;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by suicheng on 2017/5/6.
 */

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();
    private static final String MAC_ADDRESS_KEY = "mac_address";
    private static final String MAC_UNKNOWN = "unknown";

    public static final String[] INVALID_MAC_ADDRESS = {"00:00:00:00:00:00", "20:00:00:00:00:00"};
    private static String macCache;

    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return isWiFiConnected(info);
    }

    public static boolean isWiFiConnected(NetworkInfo info) {
        return (info != null && info.isConnected());
    }

    public static void toggleWiFi(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean enable = wm.isWifiEnabled();
        wm.setWifiEnabled(!enable);
    }

    public static void enableWiFi(Context context, boolean enabled) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(enabled);
    }

    public static boolean isConnectingOrConnected(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static String getMacAddressFromCacheFile(Context context){
        return Device.currentDevice().readSystemConfig(context, MAC_ADDRESS_KEY);
    }

    private static boolean isMacAddressInInvalidList(String val) {
        for (String address : INVALID_MAC_ADDRESS) {
            if (address.equals(val)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStringValidMacAddress(String val) {
        if (StringUtils.isNullOrEmpty(val)) {
            return false;
        }
        String macAddressRegex = "([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}";
        boolean valid = val.matches(macAddressRegex);
        return valid && !isMacAddressInInvalidList(val);
    }

    public static boolean isWifiEnabled(final Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return (wifiManager != null && wifiManager.isWifiEnabled());
    }

    public static String getMacAddressFromSystem(Context context) {
        String macAddress = null;
        for(int i = 0; i < 5; ++i) {
            macAddress = getMacAddressFromSystemImpl(context);
            if (StringUtils.isNotBlank(macAddress)) {
                return macAddress.toLowerCase();
            }
        }
        Log.e(TAG, "No mac address acquired");
        return macAddress;
    }

    private static String getMacAddressFromSystemImpl(Context context) {
        boolean restoreWifiStatus = false;
        if (!isWifiEnabled(context)) {
            enableWiFi(context, true);
            TestUtils.sleep(600);
            restoreWifiStatus = true;
        }
        String macAddress;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            macAddress = getMacAddressFromNetworkInterface();
        } else {
            macAddress = getMacAddressFromWifiManager(context);
        }
        if (restoreWifiStatus) {
            enableWiFi(context, false);
        }
        return macAddress;
    }

    private static String getMacAddressFromNetworkInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                for (byte b : macBytes) {
                    sb.append(String.format("%02X:", b & 0xFF));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString().toLowerCase();
            }
        } catch (Exception e) {
            if (Debug.getDebug()) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static String getMacAddressFromWifiManager(final Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo().getMacAddress();
        }
        return "";
    }

    private static void saveMacAddressToCache(Context context, String macAddress) {
        Device.currentDevice().saveSystemConfig(context, MAC_ADDRESS_KEY, macAddress);
    }

    @Nullable
    public static String getMacAddress(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            return null;
        }
        String result = getMacAddressFromCacheFile(context);
        if (TextUtils.isEmpty(result) || !(isStringValidMacAddress(result))) {
            result = getMacAddressFromSystem(context);
        }
        if (!TextUtils.isEmpty(result)) {
            saveMacAddressToCache(context, result);
        }
        return result;
    }

    @NonNull
    public static String getMacAddressWithoutSymbol(Context context) {
        String macAddress = getMacAddress(context);
        if (TextUtils.isEmpty(macAddress)) {
            return MAC_UNKNOWN;
         }
        return macAddress.replaceAll(":", "");
    }

    public static String getCurrentWifiSSID(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            return wifiManager.getConnectionInfo().getSSID();
        }
        return "";
    }

    public static boolean enableWifiOpenAndDetect(Context context) {
        if (!NetworkUtil.isWiFiConnected(context)) {
            Device.currentDevice().enableWifiDetect(context);
            NetworkUtil.enableWiFi(context, true);
            return true;
        }
        return false;
    }

    public static void clearCookies(Context context) {
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncManager =
                CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @NonNull
    public static String getMacCache() {
        if (TextUtils.isEmpty(macCache)) {
            return MAC_UNKNOWN;
        }
        return macCache;
    }

    @NonNull
    public static String getMacCacheWithoutSymbol() {
        return getMacCache().replaceAll(":", "");
    }

    public static void initMacCache(Context context) {
        NetworkUtil.macCache = getMacAddress(context.getApplicationContext());
    }
}
