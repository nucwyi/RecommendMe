package cn.edu.nuc.recommendme.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

    private Float[] UserTaste = {1f,3f,1f,3f,2f};
    private List<String> FoodNames = new ArrayList<>();
    private List<Float> FoodAcid = new ArrayList<>();
    private List<Float> FoodSweet= new ArrayList<>();
    private List<Float> FoodBitter= new ArrayList<>();
    private List<Float> FoodSpicy= new ArrayList<>();
    private List<Float> FoodSalty= new ArrayList<>();
    private List<String> BestFoods = new ArrayList<>();
    private float userTasteCacul ;

    private Map<String, Float> name_score_map = new HashMap<>();

    private boolean isQuerying = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        recommend = (Button) view.findViewById(R.id.bt_recommend_me);

        userTasteCacul = (Float) getArguments().get("taste");
        Log.w(TAG, "MainActivity穿过了的taste："+userTasteCacul);

        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "onclick");

                if (isQuerying) {
                    Toast.makeText(getContext(), "计算中。。。", Toast.LENGTH_SHORT).show();
                    queryFood();
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("掐指一算，推荐您：");
                    builder.setMessage(BestFoods.get(0)+"\n"+BestFoods.get(1));
                    builder.setCancelable(false);
                    builder.setNegativeButton("哎，都不喜欢", null);
                    builder.setPositiveButton("嗯，不错，朕知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
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

                    for (Food food : list){

                        FoodNames.add(food.getFoodName());
                        FoodAcid.add(food.getAcid());
                        FoodSweet.add(food.getSweet());
                        FoodBitter.add(food.getBitter());
                        FoodSpicy.add(food.getSpicy());
                        FoodSalty.add(food.getSalty());
                    }
                    //计算相似度
                    Log.w(TAG,"开始相似度计算");
                    Log.w(TAG, "列表大小："+list.size());
                    for (int t = 0; t < list.size(); t++){

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
                            //return (o1.getKey()).toString().compareTo(o2.getKey());
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
