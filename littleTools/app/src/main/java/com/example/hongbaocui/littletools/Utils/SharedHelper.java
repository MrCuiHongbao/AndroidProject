package com.example.hongbaocui.littletools.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedHelper {
    private Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }
    /**
     * 定义一个保存数据的方法
     * @param value
     * @param key
     */
    public void save(String value,String key) {
        SharedPreferences sp = mContext.getSharedPreferences("my_sp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    /**
     * 定义一个读取SP文件的方法
     * @return
     */
    public String read(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("my_sp", Context.MODE_PRIVATE);
        return  sp.getString(key, "");
    }
}
