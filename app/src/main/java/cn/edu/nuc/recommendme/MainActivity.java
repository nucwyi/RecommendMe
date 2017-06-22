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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

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

    private Float acid ;
    private Float sweet ;
    private Float bitter ;
    private Float spicy ;
    private Float salty ;

    private final static long TWICE_CLICK_INTERVAL=1000;// 两次按返回键的最大间隔

    private long firstClickTime=0; // 记录第一次按键的时间，如没有按键，则为0


    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - firstClickTime) > TWICE_CLICK_INTERVAL){
            firstClickTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }else {
            finish();
            System.exit(0);
        }
    }

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
        //判断头像文件是否存在
        File outputImage = new File(getExternalCacheDir(), "output_image.ipg");
        if (outputImage.exists()){
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24){
                uri = FileProvider.getUriForFile(MainActivity.this,
                        "cn.edu.nuc.recommendme.fileprovider", outputImage);
            }else {
                uri = Uri.fromFile(outputImage);
            }

            try{
                Bitmap bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(uri));
                login_user_head_image.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }


        }else {
            login_user_head_image.setImageResource(R.drawable.splash_bg);
        }




        //加载标题栏左边的人形图标（home图标）
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.user_edit);
        }

        //获取从LoginActivity或register传过来的用户数据
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
         acid = intent.getFloatExtra("acid", 0);
         sweet = intent.getFloatExtra("sweet", 0);
         bitter = intent.getFloatExtra("bitter", 0);
         spicy = intent.getFloatExtra("spicy", 0);
         salty = intent.getFloatExtra("salty", 0);

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
                        Intent TasteChangeIntent = new Intent(MainActivity.this, TasteChangeActivity.class);
                        TasteChangeIntent.putExtra("name", name);
                        TasteChangeIntent.putExtra("acid", acid);
                        TasteChangeIntent.putExtra("sweet", sweet);
                        TasteChangeIntent.putExtra("bitter", bitter);
                        TasteChangeIntent.putExtra("spicy", spicy);
                        TasteChangeIntent.putExtra("salty", salty);
                        startActivity(TasteChangeIntent);

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

    }




    //调用摄像头拍照
    private void takePhoto()
    {
        //判断头像文件是否存在
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
                    User user = new User();
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("UserName", name);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e == null){
                                for (User user1 : list){
                                    objectId = user1.getObjectId();
                                    Log.w(TAG, "获取id成功"+objectId);
                                }
                                User user2 = new User();
                                user2.setIcon(icon);
                                user2.update(objectId, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            Toast.makeText(MainActivity.this, "信息更新成功", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Log.w(TAG, "失败"+e.getErrorCode());
                                        }
                                    }
                                });
                            }else {
                                Log.w(TAG, "获取id失败"+e.getErrorCode());
                            }
                        }
                    });



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
                    bundle.putFloat("acid", acid);
                    bundle.putFloat("sweet", sweet);
                    bundle.putFloat("bitter", bitter);
                    bundle.putFloat("spicy", spicy);
                    bundle.putFloat("salty", salty);
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
                    Bundle bundle = new Bundle();
                    bundle.putFloat("taste", UserTaste);
                    bundle.putFloat("acid", acid);
                    bundle.putFloat("sweet", sweet);
                    bundle.putFloat("bitter", bitter);
                    bundle.putFloat("spicy", spicy);
                    bundle.putFloat("salty", salty);
                    school.setArguments(bundle);
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()){
            case R.id.bt_cancel_change:
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_make_sure_change:
                Toast.makeText(this, "修改", Toast.LENGTH_SHORT).show();
        }
    }
}
