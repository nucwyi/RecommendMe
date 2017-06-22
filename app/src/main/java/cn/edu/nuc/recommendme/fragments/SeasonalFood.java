package cn.edu.nuc.recommendme.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.edu.nuc.recommendme.R;
import cn.edu.nuc.recommendme.ShowSeasonalDetailsActivity;
import cn.edu.nuc.recommendme.Utils.MySeasonalListAdapter;
import cn.edu.nuc.recommendme.tables.SeasonalOne;
import cn.edu.nuc.recommendme.tables.TableSeasonalFood;

import static android.R.id.icon_frame;
import static android.R.id.list;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class SeasonalFood extends Fragment {
    private static final String TAG = "SeasonalFood";

    private ListView mListView;
    private MySeasonalListAdapter adapter;
    private SwipeRefreshLayout mFreshVIew;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private ImageView mFirstImage;
    //private ImageView testImageView;
    private int ListSize = 0;
    private int bitmapSize = 0;

    private String[]titles = new String[15];
    private String[]describes = new String[15];
    private String[]objectId = new String[15];
    private String[]imageUrl = new String[15];
    private Bitmap[]bitmaps = new Bitmap[15];

    private URL url;

    private List<SeasonalOne> seasonalOnes = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seasonal_food, container, false);

        mFreshVIew = (SwipeRefreshLayout) view.findViewById(R.id.seasonal_refresh_view);
        mListView = (ListView) view.findViewById(R.id.list_seasonal);
        mToolbar = (Toolbar) view.findViewById(R.id.seasonalFood_toolBar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.seasonalFood_collapsing_toolbar);
        mFirstImage = (ImageView) view.findViewById(R.id.seasonalFood_image);

        //刷新控件设置
        mFreshVIew.setColorSchemeResources(R.color.colorPrimary);

        mFirstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowSeasonalDetailsActivity.class);
                intent.putExtra("title", "夏至");
                intent.putExtra("describe", describes[1]);
                intent.putExtra("id", R.drawable.seasonal_food_image);
                startActivity(intent);
            }
        });

        //第一次加载
        refreshSeasonalFoodList();
        mFirstImage.setImageResource(R.drawable.seasonal_food_image);
        mCollapsingToolbarLayout.setTitle("吃什么");
        mFreshVIew.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ListSize = 0;
                bitmapSize = 0;
                seasonalOnes.clear();
                refreshSeasonalFoodList();
                Toast.makeText(getContext(), "若图片未加载，请稍后刷新", Toast.LENGTH_SHORT).show();

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SeasonalOne seasonalOne = seasonalOnes.get(position);
                Intent intent = new Intent(getContext(), ShowSeasonalDetailsActivity.class);
                intent.putExtra("title", seasonalOne.getTitle());
                intent.putExtra("describe", seasonalOne.getDescribe());
                //将bitmap转换为byte数组，才能传递
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                seasonalOne.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte [] bitmapByte =baos.toByteArray();
                intent.putExtra("bitmap", bitmapByte);

                startActivity(intent);
            }
        });

        return view;
    }

    private void refreshSeasonalFoodList(){
        BmobQuery<TableSeasonalFood> query = new BmobQuery<>();
        query.addWhereNotEqualTo("SeasonalName", "空");
        query.findObjects(new FindListener<TableSeasonalFood>() {
            @Override
            public void done(List<TableSeasonalFood> list, BmobException e) {
                if (e == null) {
                    Log.w(TAG, "查表成功");
                    for (TableSeasonalFood tabFood : list) {
                        titles[ListSize] = tabFood.getTitle();
                        describes[ListSize] = tabFood.getDescribe();
                        objectId[ListSize] = tabFood.getObjectId();
                        BmobFile file = tabFood.getImage();
                        imageUrl[ListSize] = file.getFileUrl();

                        Log.w(TAG, titles[ListSize]);
                        Log.w(TAG, describes[ListSize]);
                        Log.w(TAG, objectId[ListSize]);
                        Log.w(TAG, imageUrl[ListSize]);
                        ListSize++;

                        File image = new File(getContext().getExternalCacheDir(), tabFood.getObjectId()+".PNG");
                        String path = getContext().getExternalCacheDir()+"/"+tabFood.getObjectId()+".PNG";
                        Log.w(TAG, path);
                        if (image.exists()){
                            Log.w(TAG, "exists"+ ListSize);
                            Bitmap exitsBitmap = convertToBitmap(path, 250, 200);

                            bitmaps[ListSize-1] = exitsBitmap;

                        }else {
                            try{
                                image.createNewFile();
                                url = new URL(imageUrl[ListSize-1]);
                            }catch (IOException e2){
                                e2.printStackTrace();
                            }
                            //开始下载
                            downloadPic(url, image);
                        }
                    }
                }else {
                    Log.w(TAG, "查询时令错误："+e.getErrorCode());
                }

                for (int i = 0; i < ListSize; i++){
                    Log.w(TAG, "开始加载数据");
                    SeasonalOne seasonalOne =new SeasonalOne();
                    seasonalOne.setTitle(titles[i]);
                    seasonalOne.setDescribe(describes[i]);
                    seasonalOne.setBitmap(bitmaps[i]);
                    seasonalOnes.add(seasonalOne);
                }
                adapter = new MySeasonalListAdapter(seasonalOnes, getContext());
                mListView.setAdapter(adapter);
                mFreshVIew.setRefreshing(false);
            }
        });
    }

    private void downloadPic(final URL url, final File image){
        Log.w(TAG, "readyDownload");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try{
                    Bitmap bitmap1 = null;
                    URL myUrl;
                    myUrl = url;
                    HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    bitmap1 = BitmapFactory.decodeStream(in);
                    in.close();
                    Log.w(TAG, "下载成功");
                    //保存图片
                    FileOutputStream fout = new FileOutputStream(image);
                    bitmap1.compress(Bitmap.CompressFormat.PNG, 100, fout);
                    fout.flush();
                    fout.close();
                    Log.w(TAG, "保存成功");
                    //将其存放在数组内
                    bitmaps[bitmapSize] = bitmap1;
                    bitmapSize ++;
                    
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

        }).start();

    }


    public Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int)scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

}
