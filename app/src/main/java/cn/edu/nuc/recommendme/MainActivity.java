package cn.edu.nuc.recommendme;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;


import cn.edu.nuc.recommendme.fragments.Recommend;
import cn.edu.nuc.recommendme.fragments.School;
import cn.edu.nuc.recommendme.fragments.SeasonalFood;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView login_user_name;
    private TextView login_user_taste;

    private Recommend recommend;
    private SeasonalFood seasonalFood;
    private School school;

    private String name;
    private Float taste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nv_view);

        //初始化header内的用户名和口味系数控件
        View header = navigationView.getHeaderView(0);
        login_user_name = (TextView) header.findViewById(R.id.user_login_name);
        login_user_taste = (TextView) header.findViewById(R.id.user_login_taste);


        //加载标题栏左边的人形图标（home图标）
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.user_edit);
        }

        //第一次进入界面，默认加载第0个fragment
        setTableSelection(0);

        //获取从LoginActivity传过来的用户数据
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Float acid = intent.getFloatExtra("acid", 0);
        Float sweet = intent.getFloatExtra("sweet", 0);
        Float bitter = intent.getFloatExtra("bitter", 0);
        Float spicy = intent.getFloatExtra("spicy", 0);
        Float salty = intent.getFloatExtra("salty", 0);

        double taste = Math.sqrt(acid*acid +
                sweet*sweet + bitter*bitter +
                spicy*spicy + salty*salty);

        BigDecimal bigDecimal = new BigDecimal(taste);
        double newTaste = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        login_user_name.setText(name);
        login_user_taste.setText("口味系数："+newTaste);


        //底部三个按钮监听事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.it_recommend:
                        setTableSelection(0);
                        break;
                    case R.id.it_food_list:
                        setTableSelection(1);
                        break;
                    case R.id.it_school:
                        setTableSelection(2);
                        break;
                }
                return false;
            }
        });



    }

    //根据底部按钮选择加载内容
    private void setTableSelection(int index){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index){
            case 0:
                if(recommend == null){
                    recommend = new Recommend();
                    transaction.add(R.id.content, recommend);
                }else {
                    transaction.show(recommend);
                }
                menuItem = bottomNavigationView.getMenu().getItem(index);
                menuItem.setChecked(true);
                break;
            case 1:
                if (seasonalFood == null){
                    seasonalFood = new SeasonalFood();
                    transaction.add(R.id.content, seasonalFood);
                }else {
                    transaction.show(seasonalFood);
                }
                menuItem = bottomNavigationView.getMenu().getItem(index);
                menuItem.setChecked(true);
                break;
            case 2:
                if (school == null){
                    school = new School();
                    transaction.add(R.id.content, school);
                }else {
                    transaction.show(school);
                }
                menuItem = bottomNavigationView.getMenu().getItem(index);
                menuItem.setChecked(true);
                break;
        }
        transaction.commit();
    }


    //隐藏fragment
    private void hideFragments(FragmentTransaction transaction) {
        if (recommend != null) {
            transaction.hide(recommend);
        }
        if (seasonalFood != null) {
            transaction.hide(seasonalFood);
        }
        if (school != null) {
            transaction.hide(school);
        }
    }

    //为toolbar添加点击菜单
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.it_add:
                Toast.makeText(MainActivity.this, "添加喜爱的食物", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                break;
            default:
                break;
        }
        return true;
    }


}
