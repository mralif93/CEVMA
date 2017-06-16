package com.example.alip.evcma.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.alip.evcma.Fragment1;

/**
 * Created by Alip on 4/8/2017.
 */

public class AppController extends Application {

    private static AppController mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;

    public AppController (Context context) {
        mContext = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized AppController getInstance (Context context) {
        if(mInstance == null) {
            mInstance = new AppController(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue () {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return requestQueue;
    }

    public <T>void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }

}
