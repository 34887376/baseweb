package base.test.rpc.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;

import base.test.http.util.httpclient.IHttpClient;
import base.test.http.util.mode.ResModle;


public class HttpClientTestImpl {

    Logger logger = Logger.getLogger(this.getClass());

    public RealtimePriceResult getRealtimePrice(RealtimePriceParam param) {
        RealtimePriceResult result = new RealtimePriceResult();
        try {
            // 拼装url参数
            RealtimePriceRequest request = RealtimePriceConvert.convertToRealtimePriceRequest(param);
            StringBuffer url = new StringBuffer(HttpRpcPropertiesHolder.getRealtimePriceUrl());
            // StringBuffer url = new StringBuffer("http://pm.3.cn/prices/mgets");
            url.append(request.toString());

            // 执行
            IHttpClient client = HttpRpcFactory.createRealtimePriceClient(false);
            ResModle res = client.sendGet(url.toString());

            // 转换结果
            RealtimePriceResponse resonse = new RealtimePriceResponse();
            if (res != null && StringUtils.isNotBlank(res.getBody()) && (res.getBody().indexOf("error") == -1)) {
                resonse.convertResponseStrToList(res.getBody());
            }

            result = RealtimePriceConvert.convertToRealtimePriceResult(resonse);
        } catch (JsonParseException e) {
            logger.error("SkuInfoRpcImpl.getRealtimePrice获取实时价格json反序列化格式发生异常！！！", e);
        } catch (Exception e) {
            logger.error("SkuInfoRpcImpl.getRealtimePrice获取实时价格异常", e);
        }
        return result;
    }
}
