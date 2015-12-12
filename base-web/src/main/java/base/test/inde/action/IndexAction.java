/**
 * 
 */
package base.test.inde.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ms.domain.promotion.bo.PromotionDetailInfoBO;
import com.ms.domian.action.promotion.vo.PromotionDetailInfoVO;
import com.ms.service.promotion.face.IPromotionService;

import base.test.base.action.BaseAction;


/**
 * @author zhoushanjie
 * 
 */
public class IndexAction extends BaseAction {

    private static final long serialVersionUID = 5448158832007961012L;
    
	private Logger logger = Logger.getLogger(this.getClass());
    
    private IPromotionService iPromotionService;

    public String showIndex() {
        try{
        	List<PromotionDetailInfoBO> promotionInfoList = iPromotionService.queryLastNumPromotion(8);
	    	Map<String, Object> parmKeyValue = new HashMap<String, Object>();
	    	parmKeyValue.put("promotionInfoList",promotionInfoList);
	    	putParamToVm(parmKeyValue);
        }catch(Exception e){
        	logger.error("IndexAction.showIndex方法查询过程中发生异常！！！", e);
        }
        return SUCCESS;
    }
    
    private PromotionDetailInfoVO queryPromotion(List<Long> promotionIdList){
    	return null;
    }

	public void setiPromotionService(IPromotionService iPromotionService) {
		this.iPromotionService = iPromotionService;
	}



}
