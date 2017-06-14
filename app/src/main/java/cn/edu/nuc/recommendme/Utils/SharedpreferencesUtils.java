package cn.edu.nuc.recommendme.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/6/4 0004.
 */

public class SharedpreferencesUtils {

    private static SharedPreferences sp;

    /**
     * 保存信息
     * */
    // ctrl + alt + f 抽取成员变量
    // ctrl + alt + v 新建，赋值
    public static void saveBoolean(Context context, String key, boolean value){
        //获取
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 获取信息
     * */
    public static boolean getBoolean(Context context, String key, boolean defualtValue){
        if (sp == null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defualtValue);
    }
}
