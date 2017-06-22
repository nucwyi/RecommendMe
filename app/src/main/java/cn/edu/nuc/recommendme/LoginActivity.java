package cn.edu.nuc.recommendme;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.edu.nuc.recommendme.Utils.Contants;
import cn.edu.nuc.recommendme.Utils.MyDatabaseHelper;
import cn.edu.nuc.recommendme.Utils.SharedpreferencesUtils;
import cn.edu.nuc.recommendme.tables.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText password;
    private TextView froget_password;
    private TextView register;
    private Button login;

    private boolean isLogined = false;
    private String getBaseName;
    private String getBasePassword;

    private List<String> nameList = new ArrayList<>();

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "b8028060cef024de7bf49fe6be101955");
        setContentView(R.layout.activity_login);

        dbHelper = new MyDatabaseHelper(this, "UserInfo.db", null, 1);

        userName = (EditText) findViewById(R.id.ed_LoginActivity_user_name);
        password = (EditText) findViewById(R.id.ed_LoginActivity_password);
        froget_password = (TextView) findViewById(R.id.tv_forget);
        register = (TextView) findViewById(R.id.tv_register);
        login = (Button) findViewById(R.id.bt_LoginActivity_login);


        froget_password.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);

        boolean isFirstLogin = SharedpreferencesUtils.getBoolean(this, "isFirstLogin", false);
        if (isFirstLogin){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("UserInfo", null, null, null, null, null, null);
            if (cursor.moveToFirst()){
                do {
                    getBaseName = cursor.getString(cursor.getColumnIndex("userName"));
                    getBasePassword = cursor.getString(cursor.getColumnIndex("password"));
                    nameList.add(getBaseName);
                    Log.w("Login", "表内姓名："+getBaseName);
                } while (cursor.moveToNext());
            }
            cursor.close();
            userName.setText(getBaseName);
            password.setText(getBasePassword);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_forget:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("抱歉");
                builder.setMessage("该功能暂未开放，如有需要请联系开发人员。");
                builder.setCancelable(true).show();
                break;
            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
            case R.id.bt_LoginActivity_login:
                final String inputName = userName.getText().toString();
                final String inputPassword = password.getText().toString();
                if (inputName.equals("") || inputPassword.equals("")){
                    Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                }else {
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereExists("UserName");
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e == null){
                                for (User user : list){
                                    if (inputName.equals(user.getUserName())){
                                        if (inputPassword.equals(user.getPassword())){
                                            getUserInfo(user);
                                            //保存用户名和密码到本地数据库
                                            finish();
                                            return;
                                        }else {
                                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                                Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
             break;
        }
    }

    private void getUserInfo(User user){
        String getName = user.getUserName();
        Float getAcid = user.getAcid();
        Float getSweet = user.getSweet();
        Float getBitter = user.getBitter();
        Float getSpicy = user.getSpicy();
        Float getSalty = user.getSalty();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("name", getName);
        intent.putExtra("acid", getAcid);
        intent.putExtra("sweet", getSweet);
        intent.putExtra("bitter", getBitter);
        intent.putExtra("spicy", getSpicy);
        intent.putExtra("salty", getSalty);

        for (int i = 0; i < nameList.size(); i ++){
            if (getName.equals(nameList.get(i))){
                startActivity(intent);
                SharedpreferencesUtils.saveBoolean(this, "isFirstLogin", true);
                return;
            }
        }
        Log.w("Login", "准备添加数据");
        dbHelper.getWritableDatabase();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //填入数据
        values.put("userName", getName);
        values.put("password", user.getPassword());
        db.insert("UserInfo", null, values);

        Log.w("Login", "添加数据成功");

        startActivity(intent);
        SharedpreferencesUtils.saveBoolean(this, "isFirstLogin", true);
    }


}

