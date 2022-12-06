package com.example.yidianClock;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class DisplayMetricsHolder {

    private static @Nullable
    DisplayMetrics sWindowDisplayMetrics;
    private static @Nullable DisplayMetrics sScreenDisplayMetrics;

    /**
     * @deprecated Use {@link #setScreenDisplayMetrics(DisplayMetrics)} instead. See comment above as
     * to why this is not correct to use.
     */
    public static void setWindowDisplayMetrics(DisplayMetrics displayMetrics) {
        sWindowDisplayMetrics = displayMetrics;
    }

    public static void initDisplayMetricsIfNotInitialized(Context context) {
        if (DisplayMetricsHolder.getScreenDisplayMetrics() != null) {
            return;
        }
        initDisplayMetrics(context);
    }

    public static void initDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        DisplayMetricsHolder.setWindowDisplayMetrics(displayMetrics);

        DisplayMetrics screenDisplayMetrics = new DisplayMetrics();
        screenDisplayMetrics.setTo(displayMetrics);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) throw new AssertionError();
        Display display = wm.getDefaultDisplay();

        display.getRealMetrics(screenDisplayMetrics);
        DisplayMetricsHolder.setScreenDisplayMetrics(screenDisplayMetrics);
    }

    /**
     * @deprecated Use {@link #getScreenDisplayMetrics()} instead. See comment above as to why this
     * is not correct to use.
     */
    @Deprecated
    public static DisplayMetrics getWindowDisplayMetrics() {
        return sWindowDisplayMetrics;
    }

    public static void setScreenDisplayMetrics(DisplayMetrics screenDisplayMetrics) {
        sScreenDisplayMetrics = screenDisplayMetrics;
    }

    public static DisplayMetrics getScreenDisplayMetrics() {
        return sScreenDisplayMetrics;
    }
}
