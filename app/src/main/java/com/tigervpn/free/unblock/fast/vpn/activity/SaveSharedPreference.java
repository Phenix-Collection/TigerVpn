package com.tigervpn.free.unblock.fast.vpn.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SaveSharedPreference {
    static final int PREF_TEXT_SIZE = 32;
    static final String THEME_TYPE = "DAY";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setThemeType(Context ctx, String themetype) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(THEME_TYPE, themetype);
        editor.commit();
    }

    public static void setPrefTextSize(Context ctx, int textsize) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt("TEXT SIZE", textsize);
        editor.commit();
    }

    public static int getPrefTextSize(Context ctx) {
        return getSharedPreferences(ctx).getInt("TEXT SIZE", PREF_TEXT_SIZE);
    }

    public static String getThemeType(Context ctx) {
        return getSharedPreferences(ctx).getString(THEME_TYPE, "");
    }
}

