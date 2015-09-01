package base.test.rpc.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import base.test.base.util.JsonUtil;


public class RealtimePriceResponse {

    private List<PriceResponse> priceResponseList = new ArrayList<PriceResponse>();

    public void convertResponseStrToList(String json) throws Exception {
        if (StringUtils.isNotBlank(json)) {
            this.priceResponseList = JsonUtil.readJson(json, List.class, PriceResponse.class);
        }
    }

    public List<PriceResponse> getPriceResponseList() {
        return priceResponseList;
    }

    public static class PriceResponse {

        // 商品id
        private String id;

        // 商品市场价
        private Double m;

        // 京东价
        private Double p;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Double getM() {
            return m;
        }

        public void setM(Double m) {
            this.m = m;
        }

        public Double getP() {
            return p;
        }

        public void setP(Double p) {
            this.p = p;
        }

    }

    // public static void main(String[] args) {
    // RealtimePriceResponse realtimePriceResponse = new RealtimePriceResponse();
    // realtimePriceResponse.setJson("skuids input error\n");
    // }

}
