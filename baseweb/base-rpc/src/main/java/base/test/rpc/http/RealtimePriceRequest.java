package base.test.rpc.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

public class RealtimePriceRequest {

    // 商品对应的skuid串, 多个skuid用逗号隔开,不为空
    private List<Long> skuIds = new ArrayList<Long>();

    // 浏览该商品用户的地址信息串, 用下划线将多级地址ID从高到低连接,可为空
    private String area;

    // 手机APP调用不可为空，必须为2，主站可不传
    private Integer origin;

    // 类型，默认为1 ，可不传
    private Integer type;

    // 回调方法名，可不传
    private String callback;

    // 应用调用来源
    private String source;

    @Override
    public String toString() {
        if (CollectionUtils.isEmpty(getSkuIds())) {
            throw new NullPointerException("商品id不能为空！");
        }
        StringBuffer result = new StringBuffer("?skuIds=");

        // 商品信息
        result.append(getSkuIds().get(0));
        if (getSkuIds().size() > 1) {
            for (int i = 1; i < getSkuIds().size(); i++) {
                result.append(RealtimePriceConstant.SKU_SPLIT).append(getSkuIds().get(i));
            }
        }

        // 地址信息
        if (StringUtils.isNotBlank(getArea())) {
            result.append(RealtimePriceConstant.SPLIT).append("area=");
            result.append(getArea());
        }

        // origin
        if (getOrigin() != null) {
            result.append(RealtimePriceConstant.SPLIT).append("origin=");
            result.append(getOrigin());
        }

        // type
        if (getType() != null) {
            result.append(RealtimePriceConstant.SPLIT).append("type=");
            result.append(getType());
        }

        // 回调方法
        if (StringUtils.isNotBlank(getCallback())) {
            result.append(RealtimePriceConstant.SPLIT).append("callback=");
            result.append(getCallback());
        }
        // 调用来源
        if (StringUtils.isNotBlank(getCallback())) {
            result.append(RealtimePriceConstant.SPLIT).append("source=");
            result.append(getCallback());
        }
        return result.toString();
    }

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

    public static class RealtimePriceConstant {

        // url分割符
        public static final String SPLIT = "&";

        // skuId前缀
        public static final String SKU_PREFIX = "J_";

        // skuId分割符
        public static final String SKU_SPLIT = ",";

    }

}
