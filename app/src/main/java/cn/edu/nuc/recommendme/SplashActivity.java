package cn.edu.nuc.recommendme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import cn.edu.nuc.recommendme.Utils.Contants;
import cn.edu.nuc.recommendme.Utils.SharedpreferencesUtils;

public class SplashActivity extends Activity {

    private RelativeLayout mSplash_bg;
    private void init() {
        mSplash_bg = (RelativeLayout) findViewById(R.id.activity_splish);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        init();

        initData();

    }

    /**
     * 数据处理
     * */
    private void initData(){
        //旋转
        //创建旋转动画对象
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        //设置旋转动画的持续时间
        rotateAnimation.setDuration(1000);
        //设置动画结束后的状态
        rotateAnimation.setFillAfter(true);


        //缩放
        //创建缩放动画对象
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f );
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        //透明
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        //组合
        //创建动画插值器
        AnimationSet animationSet = new AnimationSet(true);
        //将上面山种动画添加到动画差值器中
        //animationSet.addAnimation(rotateAnimation);
        //animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        //启动动画
        mSplash_bg.startAnimation(animationSet);

        animationSet.setAnimationListener(animationListener);
    }

    /**
     * 创建动画监听器
     * */
    private Animation.AnimationListener animationListener =
            new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //根据是否为第一次安装进入，进行不同的跳转
                    //将动画结束后的状态保存到SD卡中
                    //使用Sharedpreferences类保存状态，判断是否是第一次，并获取状态
                    boolean isFirstEnter = SharedpreferencesUtils.getBoolean(SplashActivity.this,
                            Contants.IS_FIRSR_ENTER, true);
                    //根据isFirstEnter标识判断，进入那个页面
                    if (isFirstEnter){
                        //动画结束后,跳转到Guide界面
                        Intent intent = new Intent(SplashActivity.this, GuildActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    //移除当前界面
                    finish();


                }

                //动画执行过程中调用
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            };




}
