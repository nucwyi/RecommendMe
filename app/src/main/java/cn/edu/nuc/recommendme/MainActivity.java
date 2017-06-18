package cn.edu.nuc.recommendme;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.edu.nuc.recommendme.fragments.Recommend;
import cn.edu.nuc.recommendme.fragments.School;
import cn.edu.nuc.recommendme.fragments.SeasonalFood;
import cn.edu.nuc.recommendme.tables.Food;
import cn.edu.nuc.recommendme.tables.TableSeasonalFood;
import cn.edu.nuc.recommendme.tables.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{

    public static final int TAKE_PHOTO = 1;
    public static final int CROP_SMALL_PICTURE = 2;
    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView login_user_name;
    private TextView login_user_taste;
    private CircleImageView login_user_head_image;

    private Button my_taste_image;
    private Button change_taste;
    private Button setting;
    private Button zhuxiao;
    private Button fankui;

    private Uri imageUri;

    private Recommend recommend;
    private SeasonalFood seasonalFood;
    private School school;

    private Float UserTaste;

    private String objectId ;
    private String name;
    private Float taste;
    private String iconStringUrl;
    private URL iconUrl;
    private URL imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "b8028060cef024de7bf49fe6be101955");
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
        login_user_head_image = (CircleImageView) header.findViewById(R.id.user_login_head_image);


        //加载标题栏左边的人形图标（home图标）
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.user_edit);
        }

        //获取从LoginActivity或register传过来的用户数据
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Float acid = intent.getFloatExtra("acid", 0);
        final Float sweet = intent.getFloatExtra("sweet", 0);
        Float bitter = intent.getFloatExtra("bitter", 0);
        Float spicy = intent.getFloatExtra("spicy", 0);
        Float salty = intent.getFloatExtra("salty", 0);

        UserTaste =(float) Math.sqrt(acid*acid +
                sweet*sweet + bitter*bitter +
                spicy*spicy + salty*salty);

        BigDecimal bigDecimal = new BigDecimal(UserTaste);
        double newTaste = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        login_user_name.setText(name);
        login_user_taste.setText("口味系数："+newTaste);

        //第一次进入界面，默认加载第0个fragment
        setTableSelection(0);

        //设置头像点击监听事件
        login_user_head_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("设置头像");
                builder.setItems(new CharSequence[]{"拍照", "从相册选择","下载"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                break;
                            case 2:
                                //goDownload(iconUrl);
                                break;
                        }
                    }
                });
                builder.setCancelable(true).show();
            }
        });

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


        //侧滑菜单：更改口味
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.it_my_taste:
                        Toast.makeText(MainActivity.this, "开发中。。。", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.it_change_taste:

                        break;
                    case R.id.it_setting:
                        break;
                    case R.id.it_zhuxiao:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("注销");
                        builder.setMessage("确定要注销吗？");
                        builder.setNegativeButton("取消", null);
                        builder.setPositiveButton("注销", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent zhuxiaoIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(zhuxiaoIntent);
                                finish();
                            }
                        });
                        builder.show();
                        break;
                    case R.id.it_recall:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                        builder2.setTitle("反馈");
                        builder2.setMessage("请加QQ：\n903240917\n感谢您的反馈");
                        builder2.setNegativeButton("取消", null);
                        builder2.setPositiveButton("确定", null);
                        builder2.show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        /*my_taste_image = (Button) findViewById(R.id.it_my_taste);
        change_taste = (Button) findViewById(R.id.it_change_taste);
        setting = (Button) findViewById(R.id.it_setting);
        zhuxiao = (Button) findViewById(R.id.it_zhuxiao);
        fankui = (Button) findViewById(R.id.it_recall);

        my_taste_image.setOnClickListener(this);
        change_taste.setOnClickListener(this);
        setting.setOnClickListener(this);
        zhuxiao.setOnClickListener(this);
        fankui.setOnClickListener(this);*/

    }



    //调用摄像头拍照
    private void takePhoto()
    {

        File outputImage = new File(getExternalCacheDir(), "output_image.ipg");
        try{
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "cn.edu.nuc.recommendme.fileprovider", outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(imageUri));
                        login_user_head_image.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    uploadPic();

                }else {
                    Toast.makeText(this, "拍照失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case CROP_SMALL_PICTURE:
                if (data != null){
                    break;
                }
            default:
                break;
        }
    }


    private void uploadPic() {

        final String imagePath = getExternalCacheDir()+ "/output_image.ipg";
        final BmobFile icon = new BmobFile(new File(imagePath));

        icon.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    Log.w(TAG, "上传成功。。。。。。。。。。。。。。。");
                    iconStringUrl = icon.getFileUrl();
                    try{
                        iconUrl = new URL(iconStringUrl);
                    } catch (IOException e1){
                        e1.printStackTrace();
                    }


                }else {
                    Log.w(TAG, "上传失败。。。。。。。。。。。。。"+e.getErrorCode());
                }
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
                    Bundle bundle = new Bundle();
                    bundle.putFloat("taste", UserTaste);
                    recommend.setArguments(bundle);
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
