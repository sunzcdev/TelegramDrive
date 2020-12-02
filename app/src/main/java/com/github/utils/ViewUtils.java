package com.github.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;


public class ViewUtils {
    private static Toast mToast;

    public static void toast(Context context, String msg) {
        new Handler(context.getMainLooper()).post(() -> {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            mToast.show();
        });
    }
}
