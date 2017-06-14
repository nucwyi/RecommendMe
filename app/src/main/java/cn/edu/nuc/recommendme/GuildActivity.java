package cn.edu.nuc.recommendme;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nuc.recommendme.Utils.Contants;
import cn.edu.nuc.recommendme.Utils.SharedpreferencesUtils;

public class GuildActivity extends Activity implements View.OnClickListener {

    //存放图片的数组
    private int[] mImageIds = new int[]{R.drawable.guide_2,
            R.drawable.guide_2,
            R.drawable.guide_2};

    //定义一个集合，用于存放获取到的图片资源
    private List<ImageView> list;
    private ViewPager mVIewPager;
    private Button mLogin;
    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除 标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guild);

        initData();
        initView();

    }

    private void initData() {
        list = new ArrayList<ImageView>();

        for (int i = 0; i < mImageIds.length; i++) {
            ImageView imageView = new ImageView(GuildActivity.this);
            imageView.setBackgroundResource(mImageIds[i]);
            list.add(imageView);
        }
    }

    private void initView() {
        //获取控件
        mVIewPager = (ViewPager) findViewById(R.id.vp_guide_viewPager);
        mLogin = (Button) findViewById(R.id.btn_login);
        mRegister = (Button) findViewById(R.id.bt_register);

        //给viewpager填充内容，即设置适配器
        mVIewPager.setAdapter(new MyPagerAdapter());

        //设置页面滑动监听,判断何时显示跳转按钮
        mVIewPager.setOnPageChangeListener(mOnPageChangeListener);


    }

    /**
     * viewpager界面滑动监听
     */
    private ViewPager.OnPageChangeListener mOnPageChangeListener =
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }


                //当界面切换完成时调用
                @Override
                public void onPageSelected(int position) {
                    //判断：当前界面是否为最后一个界面. 是：显示按钮，反之不显示
                    if (position == mImageIds.length - 1) {
                        mLogin.setVisibility(View.VISIBLE);
                        mRegister.setVisibility(View.VISIBLE);
                        //设置按钮点击事件
                        mLogin.setOnClickListener(GuildActivity.this);
                        mRegister.setOnClickListener(GuildActivity.this);
                    } else {
                        //隐藏按钮
                        mLogin.setVisibility(View.INVISIBLE);
                        mLogin.setOnClickListener(null);
                        mRegister.setVisibility(View.INVISIBLE);
                        mRegister.setOnClickListener(null);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:
                //保存是否第一次安装进入APP状态
                SharedpreferencesUtils.saveBoolean(this, Contants.IS_FIRSR_ENTER, false);
                startActivity(new Intent(GuildActivity.this, LoginActivity.class));
                finish();
                break;

            case R.id.bt_register:
                //保存是否第一次安装进入APP状态
                SharedpreferencesUtils.saveBoolean(this, Contants.IS_FIRSR_ENTER, false);
                startActivity(new Intent(GuildActivity.this, RegisterActivity.class));
                finish();
                break;
        }

    }



    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //根据条目的位置，获取相对应的imageview
            ImageView imageView = list.get(position);
            //将获取到的imageview添加到viewpager中
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
