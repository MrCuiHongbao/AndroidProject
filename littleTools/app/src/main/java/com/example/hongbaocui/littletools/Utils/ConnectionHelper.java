package com.example.hongbaocui.littletools.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//import static com.example.hongbaocui.littletools.Utils.*;


/**
 * Created by @dazhao on 2016/8/18 14:33.
 */
public class ConnectionHelper {

    public static int position = 0;
    public static final int NETWROSE = 101;
    public static final int NETRIGHT = 201;

    public static int getNetInfo() {

        try {
            ConnectivityManager connectMgr = (ConnectivityManager) LifeStyle.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
//        NetworkInfo wifinetworkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        NetworkInfo mMobileNetworkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if (info == null) {
//            position = NETWROSE;
//        } else if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI && wifinetworkInfo.isAvailable()) {
//            position = NETRIGHT;
//        } else if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE && mMobileNetworkInfo.isAvailable()) {
//            position = NETRIGHT;
//        }
            if (info!=null&&info.isConnected()) {
                if (info.getDetailedState()== NetworkInfo.DetailedState.CONNECTED){
                    position = NETRIGHT;
                }
            }
        } catch (Exception e) {
            position = NETWROSE;
        }
        position = NETWROSE;
        return position;
    }
}

