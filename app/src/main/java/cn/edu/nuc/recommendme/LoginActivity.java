package cn.edu.nuc.recommendme;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "b8028060cef024de7bf49fe6be101955");
        setContentView(R.layout.activity_login);

        userName = (EditText) findViewById(R.id.ed_LoginActivity_user_name);
        password = (EditText) findViewById(R.id.ed_LoginActivity_password);
        froget_password = (TextView) findViewById(R.id.tv_forget);
        register = (TextView) findViewById(R.id.tv_register);
        login = (Button) findViewById(R.id.bt_LoginActivity_login);


        froget_password.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);

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
                if (inputName.equals("") || inputPassword.equals(" ")){
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

        startActivity(intent);
    }
}

