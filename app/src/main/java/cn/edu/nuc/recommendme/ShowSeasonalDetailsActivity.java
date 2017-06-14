package cn.edu.nuc.recommendme;

import android.content.Intent;
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String des = intent.getStringExtra("describe");
        int imageId = intent.getIntExtra("image", R.drawable.splash_bg);

        collapsingToolbarLayout.setTitle(name);
        Glide.with(this).load(imageId).into(imageView);
        String details = generateDetails(des);
        textView.setText(details);


    }

    private String generateDetails(String des){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 100; i++){
            builder.append(des);
        }
        return builder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
