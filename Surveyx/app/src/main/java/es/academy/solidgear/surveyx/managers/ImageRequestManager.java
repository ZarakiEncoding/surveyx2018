package es.academy.solidgear.surveyx.managers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class ImageRequestManager {
    private static ImageRequestManager instance = null;
    private static Activity mActivity;

    public static ImageRequestManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new ImageRequestManager(activity);
        }
        return instance;
    }

    public ImageRequestManager(Activity activity) {
        mActivity = activity;
    }

    public ImageLoader getImageLoader() {
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        ImageLoader imageLoader = new ImageLoader(requestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
        return imageLoader;
    }
}
