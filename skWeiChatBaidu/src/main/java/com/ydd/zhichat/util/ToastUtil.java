package com.ydd.zhichat.util;

import android.content.Context;
import android.widget.Toast;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;

/**
 *
 */
public class ToastUtil {

    private static Toast longToast;
    private static Toast toast;
    private static Context context = MyApplication.getInstance().getApplicationContext();

    public static void showErrorNet(Context context) {
        showToast(context, R.string.net_exception);
    }

    public static void showToast(Context context, int res) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, context.getString(res), Toast.LENGTH_SHORT).show();
    }

    public static void showToast(final String message) {
        MyApplication.applicationHandler.post(new Runnable() {
            @Override
            public void run() {
                if (longToast != null) {
                    longToast.cancel();
                }
                if (toast == null) {
                    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(message);
                }
                toast.show();
            }
        });
    }

    public static void showErrorData(Context context) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, context.getString(R.string.data_exception), Toast.LENGTH_SHORT).show();
    }


    public static void showNetError(Context context) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, context.getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context, int res) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, context.getString(res), Toast.LENGTH_LONG).show();
    }

    public static void showUnkownError(Context ctx, Throwable t) {
        String message = "null";
        if (t != null) {
            message = t.getMessage();
        }
        showToast(ctx, ctx.getString(R.string.tip_unkown_error_place_holder, message));
    }
}