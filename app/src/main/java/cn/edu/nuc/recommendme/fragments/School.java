package cn.edu.nuc.recommendme.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.edu.nuc.recommendme.R;
import cn.edu.nuc.recommendme.Utils.ToggleButton;
import cn.edu.nuc.recommendme.Utils.ViewpagerUtil;
import cn.edu.nuc.recommendme.tables.East;
import cn.edu.nuc.recommendme.tables.Food;
import cn.edu.nuc.recommendme.tables.West;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class School extends Fragment implements View.OnClickListener{

    private static final String TAG = "School";
    private float userTasteCacul ;
    private Float[] UserTaste = new Float[5];
    private List<String> FoodNames = new ArrayList<>();
    private List<Float> FoodAcid = new ArrayList<>();
    private List<Float> FoodSweet= new ArrayList<>();
    private List<Float> FoodBitter= new ArrayList<>();
    private List<Float> FoodSpicy= new ArrayList<>();
    private List<Float> FoodSalty= new ArrayList<>();
    private List<String> BestFoods = new ArrayList<>();
    private List<String> FoodDes = new ArrayList<>();
    private boolean isWest = true;
    private Map<String, String> name_des_map = new HashMap<>();
    private Map<String, Float> name_score_map = new HashMap<>();
    private Map<String , Float> name_des_score_map = new HashMap<>();


    private boolean isQuerying = true;

    private TextView mTextView;
    private LinearLayout mDotLayout;
    private ViewPager mViewPager;
    private ToggleButton mToggleButton;
    private Button random;
    private Button byYourself;
    private ArrayList<ViewpagerUtil> list = new ArrayList<>();


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //让viewpager选中下一页
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school, container, false);

        userTasteCacul = (Float) getArguments().get("taste");
        Log.w(TAG, "MainActivity传来的taste："+userTasteCacul);
        UserTaste[0] = (Float) getArguments().get("acid");
        UserTaste[1] = (Float) getArguments().get("sweet");
        UserTaste[2] = (Float) getArguments().get("bitter");
        UserTaste[3] = (Float) getArguments().get("spicy");
        UserTaste[4] = (Float) getArguments().get("salty");

        mViewPager = (ViewPager) view.findViewById(R.id.school_viewPager);
        mDotLayout = (LinearLayout) view.findViewById(R.id.school_dot_layout);
        mTextView = (TextView) view.findViewById(R.id.school_tv_des);
        mToggleButton = (ToggleButton) view.findViewById(R.id.school_toggleButton);
        random = (Button) view.findViewById(R.id.school_random);
        byYourself = (Button) view.findViewById(R.id.school_by_yourself);

        random.setOnClickListener(this);
        byYourself.setOnClickListener(this);

        //为toggleButton设置图片
        Log.w(TAG, "设置图片");
        mToggleButton.setToggleBackgroundResource(R.drawable.switch_background);
        mToggleButton.setSlideImage(R.drawable.slide_image);
        //设置监听器
        mToggleButton.setOnToggleStateChangeListener(new ToggleButton.OnToggleStateChangeListener() {
            @Override
            public void OnToggleStateChange(ToggleButton.ToggleState mState) {
                if (mState == ToggleButton.ToggleState.west){
                    Toast.makeText(getContext(), "西区", Toast.LENGTH_SHORT).show();
                    isWest = true;
                }else {
                    Toast.makeText(getContext(), "东区", Toast.LENGTH_SHORT).show();
                    isWest = false;
                }
            }
        });

        //准备数据
        list.add(new ViewpagerUtil(R.drawable.guide_2, "文瀛一食堂"));
        list.add(new ViewpagerUtil(R.drawable.navigation_bg, "文瀛四食堂"));
        list.add(new ViewpagerUtil(R.drawable.guide_2, "文韬食堂"));
        list.add(new ViewpagerUtil(R.drawable.navigation_bg, "北区新食堂"));

        //初始化所有点
        initDotLayout();

        mViewPager.setAdapter(new MyAdapter());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            //当pager页数发生改变时调用
            @Override
            public void onPageSelected(int position) {
                updateTextAndDot();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateTextAndDot();

        //设置viewpager默认选中的页数，设置最大值
        mViewPager.setCurrentItem(list.size()*10000);
        //发送延时消息
        handler.sendEmptyMessageDelayed(0,3000);

        return view;
    }

    private void initDotLayout(){
        //遍历集合，获取数据，根据具体数据创建具体点
        for (int i = 0; i < list.size(); i++){
            View DotView = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            if (i > 0){
                params.leftMargin = 10;
            }
            //设置宽高参数
            DotView.setLayoutParams(params);
            mDotLayout.addView(DotView);
        }
    }


    private void updateTextAndDot(){
        //获取当前页数
        int currentItem = mViewPager.getCurrentItem()%list.size();

        //设置相对应的文本
        mTextView.setText(list.get(currentItem).getDes());

        //如果当前currentItem和点的位置相同，将点设置为白色，反之黑色
        for (int i = 0; i < mDotLayout.getChildCount(); i++){
            View ChildView = mDotLayout.getChildAt(i);
            ChildView.setBackgroundResource(
                    currentItem == i ? R.drawable.dot_focus : R.drawable.dot_unfocus);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.school_by_yourself:
                if (isWest){
                    //根据口味相似度推荐，查west表
                    if (isQuerying) {
                        Toast.makeText(getContext(), "计算完成。。。请再次点击", Toast.LENGTH_SHORT).show();
                        query_west_similar();
                    } else {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("掐指一算，推荐您：");
                        builder.setMessage(BestFoods.get(0));
                        builder.setCancelable(false);
                        builder.setNegativeButton("不错，朕喜欢", null);
                        builder.setPositiveButton("不行，下一个", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.setMessage(BestFoods.get(1));
                                builder.setPositiveButton("没了。。",null);
                            }
                        });
                        builder.show();
                    }
                }else {
                    //根据口味相似度推荐，查east表

                }
                break;
            case R.id.school_random:
                if (isWest){
                    //随机推荐，查west表
                }else {
                    //随机推荐，查east表
                }
                break;
            default:
                break;
        }
    }


    //按口味相似度查west表
    private void query_west_similar(){
        Log.w(TAG, "query_west....");
        isQuerying = true;
        BmobQuery<West> query = new BmobQuery<>();
        query.addWhereNotEqualTo("FoodName", "空");
        query.findObjects(new FindListener<West>() {
            @Override
            public void done(List<West> list, BmobException e) {
                if (e == null){
                    Log.w(TAG, "querySuccess");
                    FoodNames.clear();FoodAcid.clear();FoodSweet.clear();
                    FoodBitter.clear();FoodSpicy.clear();FoodSalty.clear();
                    BestFoods.clear();FoodDes.clear();

                    for (West food : list){

                        FoodNames.add(food.getFoodName());
                        FoodAcid.add(food.getAcid());
                        FoodSweet.add(food.getSweet());
                        FoodBitter.add(food.getBitter());
                        FoodSpicy.add(food.getSpicy());
                        FoodSalty.add(food.getSalty());
                        FoodDes.add(food.getDes());
                    }
                    //计算相似度
                    Log.w(TAG,"开始相似度计算");
                    Log.w(TAG, "列表大小："+list.size());
                    for (int t = 0; t < list.size(); t++){

                        Float fenzi = (UserTaste[0]*FoodAcid.get(t) +
                                UserTaste[1]*FoodSweet.get(t) +
                                UserTaste[2]*FoodBitter.get(t) +
                                UserTaste[3]*FoodSpicy.get(t) +
                                UserTaste[4]*FoodSalty.get(t));

                        Float fenmu2 = (float) Math.sqrt(FoodAcid.get(t)*FoodAcid.get(t) +
                                FoodSweet.get(t)*FoodSweet.get(t) +
                                FoodBitter.get(t)*FoodBitter.get(t) +
                                FoodSpicy.get(t)*FoodSpicy.get(t) +
                                FoodSalty.get(t)*FoodSalty.get(t));

                        Float similar = fenzi/(userTasteCacul * fenmu2) * 100;

                        //关联list

                        String name_des = FoodNames.get(t) + "," +FoodDes.get(t);
                        name_des_score_map.put(name_des, similar);
                        Log.w(TAG, "菜名、相似度："+FoodNames.get(t)+","+similar);

                    }
                    //排序
                    List<Map.Entry<String, Float>> infoIds =
                            new ArrayList<Map.Entry<String, Float>>(name_des_score_map.entrySet());

                    Collections.sort(infoIds, new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (int)(o2.getValue() - o1.getValue());
                        }
                    });

                    //排序后输出
                    Log.w(TAG,"排序后输出");
                    for (int i = 0; i < infoIds.size(); i++) {
                        String id = infoIds.get(i).getKey();
                        BestFoods.add(id);
                        Log.w(TAG, " "+BestFoods.get(i));
                    }
                    isQuerying = false;

                }else {
                    Log.w(TAG, "查询失败"+e.getErrorCode());
                }
            }
        });

    }


    public class MyAdapter extends PagerAdapter{

        //返回最大页数
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //判断instantiateItem方法返回的object是否和view一致
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        //类似BaseAdapter里的getView（），主要用于加载view视图，并且给viewpager填充数据
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(getContext(), R.layout.fragment_school_viewpager_list, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.school_viewPager_list);
            imageView.setImageResource(list.get(position % list.size()).getIconResId());

            container.addView(view);
            return view;
        }



        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
