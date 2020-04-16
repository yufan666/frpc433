package com.activity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 日志管理类
 */
public class LogUtils {
    public static final boolean DEBUG = true;

    public static void v(String tag, String message) {
        if(DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void DebugLog(String message){
        if(DEBUG){
            Log.d("liyongfu",message);
        }
    }

    public static void d(String tag, String message) {
        if(DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if(DEBUG) {
            Log.i(tag, message);
        }
    }


    public static void e(String tag, String message, Exception e) {
        if(DEBUG) {
            Log.e(tag, message, e);
        }
    }

    public static void ShowToast(Context context, String text) {
        if (context != null)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public static void ShowToast(Context context, String text, int duration) {
        if (context != null)
            Toast.makeText(context, text, duration).show();
    }

    public static void ShowToast(Context context, int resid, int duration) {
        if (context != null)
            Toast.makeText(context, context.getString(resid), duration).show();

    }
    public static void ShowToast(Context context, int resid) {
        if (context != null)
            Toast.makeText(context, context.getString(resid), Toast.LENGTH_SHORT).show();

    }


    public static void e(String string, String string2) {
        if (DEBUG)
            Log.e(string, string2);
    }

    public static void w(String string, String string2) {
        if (DEBUG)
            Log.w(string, string2);
    }

    public static void PST(Exception e) {
        if (DEBUG) {
            e.printStackTrace();
        }
    }

    public static void PST(OutOfMemoryError e) {
        if (DEBUG) {
            e.printStackTrace();
        }
    }
}
