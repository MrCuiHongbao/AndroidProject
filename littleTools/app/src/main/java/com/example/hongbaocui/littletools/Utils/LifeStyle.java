package com.example.hongbaocui.littletools.Utils;

import android.app.Application;
import android.content.Context;

/**
 * Author : ddz
 * Creation time   : 2016/12/2 10:08
 * Fix time   :  2016/12/2 10:08
 */

public class LifeStyle extends Application {

    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = getApplicationContext();
        }
//        LifeStyleAppContext.init(this);
    }




}
