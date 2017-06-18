package cn.edu.nuc.recommendme.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nuc.recommendme.R;
import cn.edu.nuc.recommendme.tables.SeasonalOne;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class MySeasonalListAdapter extends BaseAdapter {

    private List<SeasonalOne> lists = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public MySeasonalListAdapter(){}

    public MySeasonalListAdapter(List<SeasonalOne> lists, Context context){
        this.lists = lists;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public SeasonalOne getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.seasonal_list, null);
        SeasonalOne seasonalOne = getItem(position);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_seasonal_title);
        TextView tv_des = (TextView) view.findViewById(R.id.tv_seasonal_describe);
        ImageView iv_image = (ImageView) view.findViewById(R.id.im_seasonal);

        tv_title.setText(seasonalOne.getTitle());
        tv_des.setText(seasonalOne.getDescribe());
        iv_image.setImageBitmap(seasonalOne.getBitmap());

        return view;
    }
}
