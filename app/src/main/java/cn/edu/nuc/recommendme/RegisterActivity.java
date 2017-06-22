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

    private TextView tv_check_user_name;

    String myUserName ;
    String myPassword = null;

    private Boolean is_user_name_ok = false;
    private Boolean is_user_password_ok = false;
    private Button next ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "b8028060cef024de7bf49fe6be101955");
        setContentView(R.layout.activity_register);

        //初始化控件
        ed_user_name = (EditText) findViewById(R.id.edit_user_name);
        ed_user_password = (EditText) findViewById(R.id.edit_user_password);
        ed_check_password = (EditText) findViewById(R.id.edit_check_user_password);
        tv_check_user_name = (TextView) findViewById(R.id.tv_check_user_name);
        next = (Button) findViewById(R.id.bt_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查用户名是否可用
                myUserName = ed_user_name.getText().toString();
                myPassword = ed_user_password.getText().toString();
                if (myUserName.equals("")||myPassword.equals("")){
                    Toast.makeText(RegisterActivity.this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(myPassword.length()<6){
                    Toast.makeText(RegisterActivity.this, "密码至少6位数", Toast.LENGTH_SHORT).show();
                    return;
                }
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("UserName", myUserName);
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null){
                            if (list.size() != 0){
                                is_user_name_ok = false;
                                Toast.makeText(RegisterActivity.this, "用户名已存在！", Toast.LENGTH_SHORT).show();
                            }else {
                                is_user_name_ok = true;

                                //检查密码是否相同
                                String first_pass = ed_user_password.getText().toString();
                                String second_pass = ed_check_password.getText().toString();
                                if (first_pass.equals(second_pass)){
                                    is_user_password_ok = true;
                                    Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
                                    intent.putExtra("name", myUserName);
                                    intent.putExtra("password", first_pass);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    is_user_password_ok = false;
                                    Toast.makeText(RegisterActivity.this, "两次输入密码不相同", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }else {
                            Log.w("Register",e.getErrorCode()+"");
                        }
                    }
                });


            }
        });


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
