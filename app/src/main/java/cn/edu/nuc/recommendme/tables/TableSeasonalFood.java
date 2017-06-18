package cn.edu.nuc.recommendme.tables;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class TableSeasonalFood extends BmobObject {

    private String Title;
    private String Describe;
    private BmobFile image;
    private String SeasonalName;

    public String getSeasonalName() {
        return SeasonalName;
    }

    public void setSeasonalName(String seasonalName) {
        SeasonalName = seasonalName;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescribe() {
        return Describe;
    }

    public void setDescribe(String describe) {
        Describe = describe;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }
}
