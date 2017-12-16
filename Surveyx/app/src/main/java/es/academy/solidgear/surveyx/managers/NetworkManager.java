package es.academy.solidgear.surveyx.managers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkManager {

    private static NetworkManager instance = null;

    private RequestQueue mRequestQueue;

    private  NetworkManager(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public void makeRequest(Request request) {
        mRequestQueue.add(request);
    }

    public void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
