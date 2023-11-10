package com.softwill.alpha.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class YourPreference {
    private static YourPreference yourPreference;
    private static SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "PRODUCT_APP";

    public static YourPreference getInstance(Context context) {
        if (yourPreference == null) {
            yourPreference = new YourPreference(context);
        }
        return yourPreference;
    }

    public YourPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("YourCustomNamedPreference", Context.MODE_PRIVATE);
    }

    public static void  saveData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(key, value);
        prefsEditor.commit();

    }

    public static void saveData(String key, int value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(key, String.valueOf(value));
        prefsEditor.commit();
    }

    public static void  saveData(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(key, String.valueOf(value));
        prefsEditor.commit();
    }

    public static void  saveData(String key, double value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(key, String.valueOf(value));
        prefsEditor.commit();
    }

    ///my code
   /* public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(key, json);
    }*/

    public static void set(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public String getData(String key) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getString(key, null);
        }
        return "";
    }
    public static void clearPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("YourCustomNamedPreference", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        editor.apply();


    }

}