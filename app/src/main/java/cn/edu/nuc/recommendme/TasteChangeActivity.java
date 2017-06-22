package cn.edu.nuc.recommendme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.edu.nuc.recommendme.tables.User;

public class TasteChangeActivity extends AppCompatActivity  implements SeekBar.OnSeekBarChangeListener {

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

    private Button bt_cancle;
    private Button bt_makesure;

    private float myAcid ;
    private float mySweet ;
    private float myBitter ;
    private float mySpicy ;
    private float mySalty ;



    private String name;
    private String ObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taste_change);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        myAcid = intent.getFloatExtra("acid", 2);
        mySweet = intent.getFloatExtra("sweet", 2);
        myBitter = intent.getFloatExtra("bitter", 2);
        mySpicy = intent.getFloatExtra("spicy", 2);
        mySalty = intent.getFloatExtra("salty", 2);

        Log.w("Taste","get:"+myAcid+mySweet+myBitter+mySpicy+mySalty);

        tv_acid = (TextView) findViewById(R.id.tv_acid_change);
        tv_sweet = (TextView) findViewById(R.id.tv_sweet_change);
        tv_bitter = (TextView) findViewById(R.id.tv_bitter_change);
        tv_spicy = (TextView) findViewById(R.id.tv_spicy_change);
        tv_salty = (TextView) findViewById(R.id.tv_salty_change);

        bar_acid = (SeekBar) findViewById(R.id.bar_acid_change);
        bar_sweet = (SeekBar) findViewById(R.id.bar_sweet_change);
        bar_bitter = (SeekBar) findViewById(R.id.bar_bitter_change);
        bar_spicy = (SeekBar) findViewById(R.id.bar_spicy_change);
        bar_salty = (SeekBar) findViewById(R.id.bar_salty_change);

        bar_acid.setProgress((int)myAcid);
        bar_sweet.setProgress((int)mySweet);
        bar_bitter.setProgress((int)myBitter);
        bar_spicy.setProgress((int)mySpicy);
        bar_salty.setProgress((int)mySalty);

        tv_acid.setText("酸："+(int)myAcid);
        tv_sweet.setText("甜："+(int)mySweet);
        tv_bitter.setText("苦："+(int)myBitter);
        tv_spicy.setText("辣："+(int)mySpicy);
        tv_salty.setText("咸："+(int)mySalty);

        bt_cancle = (Button) findViewById(R.id.bt_cancel_change);
        bt_makesure = (Button) findViewById(R.id.bt_make_sure_change);

        bar_acid.setOnSeekBarChangeListener(this);
        bar_sweet.setOnSeekBarChangeListener(this);
        bar_bitter.setOnSeekBarChangeListener(this);
        bar_spicy.setOnSeekBarChangeListener(this);
        bar_salty.setOnSeekBarChangeListener(this);

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_makesure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final User user = new User();
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("UserName", name);
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null){
                            ObjectId = list.get(0).getObjectId();
                            user.setAcid(myAcid);
                            user.setSweet(mySweet);
                            user.setBitter(myBitter);
                            user.setSpicy(mySpicy);
                            user.setSalty(mySalty);
                            user.update(ObjectId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null){
                                        Toast.makeText(TasteChangeActivity.this, "口味更新成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Log.w("Taste", "更新失败："+e.getErrorCode());
                                    }
                                }
                            });
                        }else {
                            Log.w("Taste", "查询失败："+e.getErrorCode());
                        }

                    }
                });

            }
        });


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.bar_acid_change:
                tv_acid.setText("酸："+String.valueOf(seekBar.getProgress()));
                myAcid = Float.valueOf(String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_sweet_change:
                tv_sweet.setText("甜："+String.valueOf(seekBar.getProgress()));
                mySweet = Float.valueOf(String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_bitter_change:
                tv_bitter.setText("苦："+String.valueOf(seekBar.getProgress()));
                myBitter = Float.valueOf(String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_spicy_change:
                tv_spicy.setText("辣："+String.valueOf(seekBar.getProgress()));
                mySpicy = Float.valueOf(String.valueOf(seekBar.getProgress()));
                break;
            case R.id.bar_salty_change:
                tv_salty.setText("咸："+String.valueOf(seekBar.getProgress()));
                mySalty = Float.valueOf(String.valueOf(seekBar.getProgress()));
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
