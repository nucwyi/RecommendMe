package cn.edu.nuc.recommendme;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.edu.nuc.recommendme.tables.User;

public class Register2Activity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private TextView tv_acid;
    private TextView tv_sweet;
    private TextView tv_bitter;
    private TextView tv_spicy;
    private TextView tv_salty;

    private SeekBar bar_acid;
    private SeekBar bar_sweet;
    private SeekBar bar_bitter;
    private SeekBar bar_spicy;
    private SeekBar bar_salty;

    private Button register;

    Float myAcid = null;
    Float mySweet = null;
    Float myBitter = null;
    Float mySpicy = null;
    Float mySalty = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        init();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAcid = (float) bar_acid.getProgress();
                mySweet = (float) bar_sweet.getProgress();
                myBitter = (float) bar_bitter.getProgress();
                mySpicy = (float) bar_spicy.getProgress();
                mySalty = (float) bar_salty.getProgress();

                final Intent intent = getIntent();
                final String name = intent.getStringExtra("name");
                String password = intent.getStringExtra("password");

                User user = new User();
                user.setUserName(name);
                user.setPassword(password);
                user.setAcid(myAcid);
                user.setSweet(mySweet);
                user.setBitter(myBitter);
                user.setSpicy(mySpicy);
                user.setSalty(mySalty);

                user.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            Log.w("Register2", "注册成功");
                            Intent intent1 = new Intent(Register2Activity.this, MainActivity.class);
                            intent1.putExtra("name", name);
                            intent1.putExtra("acid", myAcid);
                            intent1.putExtra("sweet", mySweet);
                            intent1.putExtra("bitter", myBitter);
                            intent1.putExtra("spicy", mySpicy);
                            intent1.putExtra("salty", mySalty);
                            startActivity(intent1);
                            Toast.makeText(Register2Activity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Log.w("Register2", ""+e.getErrorCode());
                        }
                    }
                });
            }
        });
    }

    private void init(){
        tv_acid = (TextView) findViewById(R.id.tv_acid);
        tv_sweet = (TextView) findViewById(R.id.tv_sweet);
        tv_bitter = (TextView) findViewById(R.id.tv_bitter);
        tv_spicy = (TextView) findViewById(R.id.tv_spicy);
        tv_salty = (TextView) findViewById(R.id.tv_salty);

        bar_acid = (SeekBar) findViewById(R.id.bar_acid);
        bar_sweet = (SeekBar) findViewById(R.id.bar_sweet);
        bar_bitter = (SeekBar) findViewById(R.id.bar_bitter);
        bar_spicy = (SeekBar) findViewById(R.id.bar_spicy);
        bar_salty = (SeekBar) findViewById(R.id.bar_salty);

        register = (Button) findViewById(R.id.bt_user_register);

        bar_acid.setOnSeekBarChangeListener(this);
        bar_sweet.setOnSeekBarChangeListener(this);
        bar_bitter.setOnSeekBarChangeListener(this);
        bar_spicy.setOnSeekBarChangeListener(this);
        bar_salty.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.bar_acid:
                tv_acid.setText("酸："+String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_sweet:
                tv_sweet.setText("甜："+String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_bitter:
                tv_bitter.setText("苦："+String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_spicy:
                tv_spicy.setText("辣："+String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_salty:
                tv_salty.setText("咸："+String.valueOf(seekBar.getProgress()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
