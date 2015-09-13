package base.test.rpc.http;

import java.util.ArrayList;
import java.util.List;

public class RealtimePriceParam {

    // 商品对应的skuid
    private List<Long> skuIds = new ArrayList<Long>();

    // 浏览该商品用户的地址信息
    private String area;

    // 手机APP调用不可为空，必须为2
    private Integer origin;

    // 类型
    private Integer type;

    // 回调方法名
    private String callback;

    // 应用调用来源
    private String source;

    public List<Long> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<Long> skuIds) {
        this.skuIds = skuIds;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
