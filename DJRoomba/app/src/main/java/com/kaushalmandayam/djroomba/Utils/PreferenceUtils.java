package com.kaushalmandayam.djroomba.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.kaushalmandayam.djroomba.DjRoombaApplication;
import com.kaushalmandayam.djroomba.net.DjRoombaApi;

/**
 * Created by Kaushal on 6/4/2017.
 */

public class PreferenceUtils
{
    private static final String PREFS_ID = "DJROOMBA_PREFERENCES";
    private static final String PREF_USER_LOGGEDIN_STATUS = "PREF_USER_LOGGEDIN_STATUS";
    private static final String PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN";
    private static final String PREF_REFRESH_TOKEN = "PREF_REFRESH_TOKEN";

    public static SharedPreferences getSharedPreferences()
    {
        return DjRoombaApplication.getContext().getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE);
    }

    public static void setUserLoggedInStatus(boolean status)
    {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREF_USER_LOGGEDIN_STATUS, status);
        editor.apply();
    }

    public static boolean getUserLoggedInStatus()
    {
        return getSharedPreferences().getBoolean(PREF_USER_LOGGEDIN_STATUS, false);
    }

    public static void setAccessCode(String accessToken)
    {
        Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public static String getAccessCode()
    {
        return getSharedPreferences().getString(PREF_ACCESS_TOKEN, null);
    }

    public static void setRefreshToken(String refreshToken)
    {
        Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public static String getRefreshToken()
    {
        return getSharedPreferences().getString(PREF_REFRESH_TOKEN, null);
    }
}
