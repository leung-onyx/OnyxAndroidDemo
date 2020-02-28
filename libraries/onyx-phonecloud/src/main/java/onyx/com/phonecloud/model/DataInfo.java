package onyx.com.phonecloud.model;

/**
 * Created by TonyXie on 2020-02-28
 */
public class DataInfo {
    private int type;
    private String data;

    public DataInfo(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
