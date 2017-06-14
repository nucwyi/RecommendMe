package cn.edu.nuc.recommendme;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.edu.nuc.recommendme.tables.User;

public class RegisterActivity extends Activity {

    //加载控件
    private EditText ed_user_name;
    private EditText ed_user_password;
    private EditText ed_check_password;
    private EditText ed_acid;
    private EditText ed_sweet;
    private EditText ed_bitter;
    private EditText ed_spicy;
    private EditText ed_salty;
    private TextView tv_check_user_name;
    private Button bt_register;

    String myUserName ;
    String myPassword = null;
    Float myAcid = null;
    Float mySweet = null;
    Float myBitter = null;
    Float mySpicy = null;
    Float mySalty = null;
    private Boolean is_user_name_ok = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "b8028060cef024de7bf49fe6be101955");
        setContentView(R.layout.activity_register);

        //初始化控件
        ed_user_name = (EditText) findViewById(R.id.edit_user_name);
        ed_user_password = (EditText) findViewById(R.id.edit_user_password);
        ed_check_password = (EditText) findViewById(R.id.edit_check_user_password);
        ed_acid = (EditText) findViewById(R.id.ed_acid);
        ed_sweet = (EditText) findViewById(R.id.ed_sweet);
        ed_bitter = (EditText) findViewById(R.id.ed_bitter);
        ed_spicy = (EditText) findViewById(R.id.ed_spicy);
        ed_salty = (EditText) findViewById(R.id.ed_salty);
        tv_check_user_name = (TextView) findViewById(R.id.tv_check_user_name);
        bt_register = (Button) findViewById(R.id.bt_user_register);

        //对输入的用户名进行判断，是否可用
        /*myUserName = ed_user_name.getText().toString();
        checkUserName();

        if (is_user_name_ok){
            tv_check_user_name.setText("用户名可用");
            tv_check_user_name.setTextColor(Color.GREEN);
        }else {
            tv_check_user_name.setText("用户名不可用");
            tv_check_user_name.setTextColor(Color.RED);
        }*/



        //点击注册按钮，处理注册事件
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myUserName = ed_user_name.getText().toString();
                myPassword = ed_user_password.getText().toString();
                myAcid = Float.valueOf(ed_acid.getText().toString());
                mySweet = Float.valueOf(ed_sweet.getText().toString());
                myBitter = Float.valueOf(ed_bitter.getText().toString());
                mySpicy = Float.valueOf(ed_spicy.getText().toString());
                mySalty = Float.valueOf(ed_salty.getText().toString());

                User user = new User();
                user.setUserName(myUserName);
                user.setPassword(myPassword);
                user.setAcid(myAcid);
                user.setSweet(mySweet);
                user.setBitter(myBitter);
                user.setSpicy(mySpicy);
                user.setSalty(mySalty);

                user.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            Log.w("Bmob", "注册成功");
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("name", myUserName);
                            intent.putExtra("acid", myAcid);
                            intent.putExtra("sweet", mySweet);
                            intent.putExtra("bitter", myBitter);
                            intent.putExtra("spicy", mySpicy);
                            intent.putExtra("salty", mySalty);
                            startActivity(intent);

                            finish();
                        }else {
                            Log.w("Bmob", "注册失败");
                        }
                    }
                });
            }
        });
    }



    /**
     * 检查用户名是否可用
     * */
    private void checkUserName(){
        if (myUserName.equals("")){
            is_user_name_ok = false;
        }else {
            BmobQuery<User> query = new BmobQuery<User>();
            query.addWhereExists("UserName");
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        Log.w("Bmob", "检查用户名查询成功");
                        for (User user : list){
                            if (myUserName.equals(user.getUserName())){
                                is_user_name_ok = false;
                                return ;
                            }
                        }
                        is_user_name_ok = true;
                    }else {
                        Log.w("Bmob", "检查用户名查询失败");
                    }
                }
            });
        }

    }
}
