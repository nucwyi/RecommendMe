package cn.edu.nuc.recommendme.Utils;

/**
 * Created by Administrator on 2017/6/18 0018.
 */

public class ViewpagerUtil {

    //定义图片
    private int iconResId;
    //文本
    private String des;

    public ViewpagerUtil(int iconResId, String des){
        super();
        this.iconResId = iconResId;
        this.des = des;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
