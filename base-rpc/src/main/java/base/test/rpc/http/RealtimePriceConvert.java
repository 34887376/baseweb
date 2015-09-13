package base.test.rpc.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import base.test.rpc.http.RealtimePriceRequest.RealtimePriceConstant;
import base.test.rpc.http.RealtimePriceResponse.PriceResponse;
import base.test.rpc.http.RealtimePriceResult.RealtimePrice;


public class RealtimePriceConvert {

    private static Logger logger = Logger.getLogger(RealtimePriceConvert.class);

    public static RealtimePriceRequest convertToRealtimePriceRequest(RealtimePriceParam param) {
        RealtimePriceRequest request = new RealtimePriceRequest();
        request.setSkuIds(param.getSkuIds());
        request.setArea(param.getArea());
        request.setOrigin(param.getOrigin());
        request.setType(param.getType());
        request.setCallback(param.getCallback());
        request.setSource(param.getSource());
        return request;
    }

    public static RealtimePriceResult convertToRealtimePriceResult(RealtimePriceResponse response) {
        RealtimePriceResult result = new RealtimePriceResult();
        if (CollectionUtils.isEmpty(response.getPriceResponseList())) {
            return result;
        }
        List<RealtimePrice> realtimePrices = new ArrayList<RealtimePrice>();
        for (PriceResponse priceResponse : response.getPriceResponseList()) {
            // 价格为-1，表示是下柜商品
            if (priceResponse.getP() < 0) {
                continue;
            }
            RealtimePrice price = new RealtimePrice();
            String skuIdStr = priceResponse.getId();
            price.setSkuId(convertToSkuId(skuIdStr));
            price.setMarketPrice(priceResponse.getM());
            price.setJdPrice(priceResponse.getP());
            realtimePrices.add(price);
        }

        result.setRealtimePrices(realtimePrices);
        return result;
    }

    private static Long convertToSkuId(String skuIdStr) {
        if (StringUtils.isBlank(skuIdStr)) {
            return null;
        }
        try {
            if (skuIdStr.contains(RealtimePriceConstant.SKU_PREFIX)) {
                skuIdStr = skuIdStr.replaceFirst(RealtimePriceConstant.SKU_PREFIX, "");
            }
            return Long.valueOf(skuIdStr);
        } catch (Exception e) {
            logger.error("convertToSkuId实时价格转换skuId出错!", e);
        }
        return null;
    }

}
