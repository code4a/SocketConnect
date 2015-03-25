package com.sk.socketconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences tool
 *
 * @author liuzian
 */
public class PersistTool {
    private final String PREFERENCES_NAME = "socket_preferences";

    /**
     * wimo 1.0 is on
     */
    public static final String TASK_ID = "TASK";
    /**
     * wifi auto connect on
     */
    public static final String POINT_TIME = "points";
    /**
     * SharedPreferences
     */
    private SharedPreferences prefer = null;
    /**
     * PersistTool
     */
    private static PersistTool mPersistTool;

    /**
     * get PersistTool
     *
     * @param context
     *            Context
     * @return PersistTool
     */
    public static PersistTool getInstance(Context context) {
        if (mPersistTool == null) {
            mPersistTool = new PersistTool(context);
        }
        return mPersistTool;
    }

    /**
     * PersistTool
     *
     * @param context
     *            Context
     */
    private PersistTool(Context context) {
        if (prefer == null) {
            prefer = context.getSharedPreferences(PREFERENCES_NAME,
                    Context.MODE_PRIVATE);
        }
    }

    /**
     * set int
     *
     * @param name
     *            key
     * @param value
     *            value
     */
    public void saveInt(String name, int value) {
        SharedPreferences.Editor editor = prefer.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    /**
     * get int value
     *
     * @param name
     *            key
     * @param defaultValue
     *            defaultValue
     * @return int value
     */
    public int getInt(String name, int defaultValue) {
        return prefer.getInt(name, defaultValue);
    }

    /**
     * set float
     *
     * @param name
     *            key
     * @param value
     *            float value
     */
    public void saveFloat(String name, float value) {
        SharedPreferences.Editor editor = prefer.edit();
        editor.putFloat(name, value);
        editor.commit();
    }

    /**
     * get float value
     *
     * @param name
     *            key
     * @param defaultValue
     *            defaultValue
     * @return float value
     */
    public float getFloat(String name, float defaultValue) {
        return prefer.getFloat(name, defaultValue);
    }

    /**
     * set string value
     *
     * @param name
     *            key
     * @param value
     *            string value
     */
    public void saveString(String name, String value) {
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString(name, value);
        editor.commit();
    }

    /**
     * get string value
     *
     * @param name
     *            key
     * @param defaultValue
     *            string defaultValue
     * @return string value
     */
    public String getString(String name, String defaultValue) {
        return prefer.getString(name, defaultValue);
    }

}
