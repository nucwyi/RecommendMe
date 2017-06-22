package cn.edu.nuc.recommendme.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREAT_USER = "create table UserInfo ("+
            "userName text primary key,"+
            "password text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_USER);
        Log.w("Login", "数据库创建成功，。。");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
