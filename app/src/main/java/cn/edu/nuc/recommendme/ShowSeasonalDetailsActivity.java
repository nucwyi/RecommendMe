package cn.edu.nuc.recommendme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ShowSeasonalDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar ;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seasonal_details);

        toolbar = (Toolbar) findViewById(R.id.seasonal_details_toolBar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        imageView = (ImageView) findViewById(R.id.seasonal_details_image);
        textView = (TextView) findViewById(R.id.seasonal_details_describe);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String name = intent.getStringExtra("title");
        String des = intent.getStringExtra("describe");
        int id = intent.getIntExtra("id", 0);

        collapsingToolbarLayout.setTitle(name);
        textView.setText(des);
        if (id == 0){
            //将byte数组转换成bitmap对象
            byte [] bis=intent.getByteArrayExtra("bitmap");
            Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
            imageView.setImageBitmap(bitmap);
        }else {
            imageView.setImageResource(id);
        }








    }



}
