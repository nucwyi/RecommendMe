package cn.edu.nuc.recommendme.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.edu.nuc.recommendme.MainActivity;
import cn.edu.nuc.recommendme.R;
import cn.edu.nuc.recommendme.RecommendView;
import cn.edu.nuc.recommendme.tables.Food;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class Recommend extends Fragment{

    private static final String TAG = "Recommend";

    private Button recommend;
    private RecommendView recommendView;


    private float userTasteCacul ;
    private Float[] UserTaste = new Float[5];
    private List<String> FoodNames = new ArrayList<>();
    private List<Float> FoodAcid = new ArrayList<>();
    private List<Float> FoodSweet= new ArrayList<>();
    private List<Float> FoodBitter= new ArrayList<>();
    private List<Float> FoodSpicy= new ArrayList<>();
    private List<Float> FoodSalty= new ArrayList<>();
    private List<String> BestFoods = new ArrayList<>();
    private Map<String, Float> name_score_map = new HashMap<>();

    private boolean unLikeAcid = false;
    private boolean unLikeSweet = false;
    private boolean unLikeBitter = false;
    private boolean unLikeSpicy = false;
    private boolean unLikeSalty = false;

    private int listSize = 0;

    private boolean isQuerying = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        recommend = (Button) view.findViewById(R.id.bt_recommend_me);

        userTasteCacul = (Float) getArguments().get("taste");
        Log.w(TAG, "MainActivity传来的taste："+userTasteCacul);
        UserTaste[0] = (Float) getArguments().get("acid");
        UserTaste[1] = (Float) getArguments().get("sweet");
        UserTaste[2] = (Float) getArguments().get("bitter");
        UserTaste[3] = (Float) getArguments().get("spicy");
        UserTaste[4] = (Float) getArguments().get("salty");

        //对各个值进行判断
        if (UserTaste[0] <= 2){
            unLikeAcid = true;
        }
        if (UserTaste[1] <= 2){
            unLikeSweet = true;
        }
        if (UserTaste[2] <= 2){
            unLikeBitter = true;
        }
        if (UserTaste[3] <= 2){
            unLikeSpicy = true;
        }
        if (UserTaste[4] <= 2){
            unLikeSalty = true;
        }

        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "onclick");

                if (isQuerying) {
                    Toast.makeText(getContext(), "计算完成。。。请再次点击", Toast.LENGTH_SHORT).show();
                    queryFood();
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("掐指一算，推荐您：");
                    if (BestFoods.get(1) != null){
                        builder.setMessage(BestFoods.get(0)+"\n"+BestFoods.get(1));
                    }else {
                        builder.setMessage(BestFoods.get(0));
                    }

                    builder.setCancelable(false);

                    if (BestFoods.size() > 2){
                        builder.setNegativeButton("哎哟，不错哦", null);
                        builder.setPositiveButton("换一批", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                                builder2.setTitle("掐指又一算，推荐您：");
                                if (BestFoods.get(3) != null){
                                    builder2.setMessage(BestFoods.get(2)+"\n"+BestFoods.get(3));
                                }else {
                                    builder2.setMessage(BestFoods.get(2));
                                }
                                builder2.setNegativeButton("都不喜欢",null);
                                builder2.setPositiveButton("甚合我意",null);
                                builder2.show();
                            }
                        });
                    } else {
                        builder.setNegativeButton("唉，都不喜欢", null);
                        builder.setPositiveButton("甚合我意",null);
                    }
                    builder.show();
                }
            }
        });
        return view;
    }

    private void queryFood(){
        Log.w(TAG, "query....");
        isQuerying = true;
        BmobQuery<Food> query = new BmobQuery<>();
        query.addWhereNotEqualTo("FoodName", "空");
        query.findObjects(new FindListener<Food>() {
            @Override
            public void done(List<Food> list, BmobException e) {
                if (e == null){
                    Log.w(TAG, "querySuccess");
                    FoodNames.clear();FoodAcid.clear();FoodSweet.clear();
                    FoodBitter.clear();FoodSpicy.clear();FoodSalty.clear();
                    BestFoods.clear();

                    for (Food food : list){

                        if (unLikeAcid && food.getAcid() > 2.6){
                            continue;
                        }else if (unLikeSweet && food.getSweet() > 3){
                            continue;
                        }else if (unLikeBitter && food.getBitter() >2){
                            continue;
                        }else if (unLikeSpicy && food.getSpicy() > 2){
                            continue;
                        }else if (unLikeSalty && food.getSalty() > 3){
                            continue;
                        }
                        FoodNames.add(food.getFoodName());
                        FoodAcid.add(food.getAcid());
                        FoodSweet.add(food.getSweet());
                        FoodBitter.add(food.getBitter());
                        FoodSpicy.add(food.getSpicy());
                        FoodSalty.add(food.getSalty());
                        listSize++;

                    }
                    //计算相似度
                    Log.w(TAG,"开始相似度计算");
                    Log.w(TAG, "列表大小："+listSize);
                    for (int t = 0; t < listSize; t++){

                        FoodNames.get(t);
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

                        //关联两个list
                        name_score_map.put(FoodNames.get(t), similar);
                        Log.w(TAG, "菜名、相似度："+FoodNames.get(t)+","+similar);

                    }
                    //排序
                    List<Map.Entry<String, Float>> infoIds =
                            new ArrayList<Map.Entry<String, Float>>(name_score_map.entrySet());

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

}
