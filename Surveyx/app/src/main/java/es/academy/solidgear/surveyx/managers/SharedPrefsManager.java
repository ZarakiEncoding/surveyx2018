package es.academy.solidgear.surveyx.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SharedPrefsManager {

    private static SharedPrefsManager instance = null;

    private SharedPreferences sharedPreferences;

    private SharedPrefsManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean getBoolean(String key) {
        return this.sharedPreferences.getBoolean(key, false);
    }

    public void putBoolean(String key, boolean value) {
        final SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void remove(String key) {
        final SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }


}
