package com.ydd.zhichat.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;

public class ContextUtil {
    /**
     * Get activity instance from desired context.
     */
    @Nullable
    public static Activity findActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper)
            return findActivity(((ContextWrapper) context).getBaseContext());
        return null;
    }
}
