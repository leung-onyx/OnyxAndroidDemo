/**
 * 
 */
package com.onyx.android.sdk.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.GObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author dxw
 *
 */
public class RawResourceUtil {
    static final String DRAWABLE_RESOURCE_TYPE = "drawable";
    static final String STRING_RESOURCE_TYPE = "string";

    static public int getDrawableIdByName(Context context, final String resourceName) {
        return getResourceIdByName(context, DRAWABLE_RESOURCE_TYPE, resourceName);
    }

    static public int getStringIdByName(Context context,final String resourceName){
        return getResourceIdByName(context, STRING_RESOURCE_TYPE, resourceName);
    }

    @Nullable
    static public String getStringByResourceName(Context context, final String resourceName) {
        int resId = getStringIdByName(context, resourceName);
        if (resId <= 0) {
            return null;
        }
        return context.getString(resId);
    }

    @Nullable
    static public Drawable getDrawableByResourceName(Context context, final String resourceName) {
        int resId = getDrawableIdByName(context, resourceName);
        if (resId <= 0) {
            return null;
        }
        return context.getResources().getDrawable(resId);
    }

    static public int getResourceIdByName(Context context, final String resourceType, final String resourceName) {
        if (StringUtils.isNotBlank(resourceName)) {
            String packageName = context.getPackageName();
            return context.getResources().getIdentifier(resourceName, resourceType, packageName);
        }
        return 0;
    }
   
    public static String contentOfRawResource(Context context, int rawResourceId) {
        BufferedReader breader = null;
        InputStream is = null;
        try {
             is = context.getResources().openRawResource(rawResourceId);
             breader = new BufferedReader(new InputStreamReader(is));
             StringBuilder total = new StringBuilder();
             String line = null;
             while ((line = breader.readLine()) != null) {
                 total.append(line);
             }
             return total.toString();
        }
        catch (Exception e) {
            if (rawResourceId > 0) {
                e.printStackTrace();
            }
        }
         finally {
            closeQuietly(breader);
            closeQuietly(is);
        }
        return null;
    }
    
    public static Map<String, List<Integer>> integerMapFromRawResource(Context context, int rawResourceId) {
        String content = contentOfRawResource(context, rawResourceId);
        return JSON.parseObject(content, new TypeReference<Map<String, List<Integer>>>(){});
    }

    public static GObject objectFromRawResource(Context context, int rawResourceId) {
        String content = contentOfRawResource(context, rawResourceId);
        try {
            Map<String, Object> map = JSON.parseObject(content);
            if (map == null) {
                return null;
            }
            GObject object = new GObject();
            for(Map.Entry<String, Object> entry : map.entrySet()) {
                object.putObject(entry.getKey(), entry.getValue());
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    static public void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String contentFromRawResource(Context context, String name) {
        String content = "";
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            content = contentOfRawResource(context, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
