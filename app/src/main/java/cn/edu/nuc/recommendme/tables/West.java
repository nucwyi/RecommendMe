package cn.edu.nuc.recommendme.tables;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/6/18 0018.
 */

public class West extends BmobObject {

    private String FoodName;
    private Float acid;
    private Float sweet;
    private Float bitter;
    private Float spicy;
    private Float salty;
    private String des;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public Float getAcid() {
        return acid;
    }

    public void setAcid(Float acid) {
        this.acid = acid;
    }

    public Float getSweet() {
        return sweet;
    }

    public void setSweet(Float sweet) {
        this.sweet = sweet;
    }

    public Float getBitter() {
        return bitter;
    }

    public void setBitter(Float bitter) {
        this.bitter = bitter;
    }

    public Float getSpicy() {
        return spicy;
    }

    public void setSpicy(Float spicy) {
        this.spicy = spicy;
    }

    public Float getSalty() {
        return salty;
    }

    public void setSalty(Float salty) {
        this.salty = salty;
    }
}
