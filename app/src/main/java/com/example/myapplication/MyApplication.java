package com.example.myapplication;

import android.app.Application;
import android.app.DownloadManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.security.SecureRandom;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();
    private RequestQueue mRecuestQueue;
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance =  this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRecuestQueue(){
        if (mRecuestQueue == null){
            mRecuestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRecuestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRecuestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if (mRecuestQueue != null){
            mRecuestQueue.cancelAll(tag);
        }
    }
}
