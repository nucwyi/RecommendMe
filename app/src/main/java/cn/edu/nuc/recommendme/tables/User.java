package cn.edu.nuc.recommendme.tables;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class User extends BmobObject {

    private String UserName;
    private String Password;
    private Float acid;
    private Float sweet;
    private Float bitter;
    private Float spicy;
    private Float salty;

    private BmobFile Icon;

    public BmobFile getIcon() {
        return Icon;
    }

    public void setIcon(BmobFile icon) {
        Icon = icon;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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
