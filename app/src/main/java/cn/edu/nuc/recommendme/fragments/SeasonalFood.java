package cn.edu.nuc.recommendme.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nuc.recommendme.R;
import cn.edu.nuc.recommendme.ShowSeasonalDetailsActivity;
import cn.edu.nuc.recommendme.Utils.MySeasonalListAdapter;
import cn.edu.nuc.recommendme.tables.SeasonalOne;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class SeasonalFood extends Fragment {

    private ListView mListView;
    private MySeasonalListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seasonal_food, container, false);

        mListView = (ListView) view.findViewById(R.id.list_seasonal);
        final List<SeasonalOne> list = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            SeasonalOne seasonalOne =new SeasonalOne();
            seasonalOne.setTitle("夏至来了" + i+1);
            seasonalOne.setDescribe("夏天吃西瓜。。。。。很舒服");
            seasonalOne.setImageId(R.drawable.xigua);
            list.add(seasonalOne);
        }

        adapter = new MySeasonalListAdapter(list, getContext());
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SeasonalOne seasonalOne = list.get(position);
                Intent intent = new Intent(getContext(), ShowSeasonalDetailsActivity.class);
                intent.putExtra("title", seasonalOne.getTitle());
                intent.putExtra("describe", seasonalOne.getDescribe());
                intent.putExtra("image", seasonalOne.getImageId());
                startActivity(intent);
            }
        });

        return view;
    }
}
