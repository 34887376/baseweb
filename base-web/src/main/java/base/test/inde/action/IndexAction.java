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

import com.ms.domian.action.promotion.vo.PromotionInfoVO;

import base.test.base.action.BaseAction;


/**
 * @author zhoushanjie
 * 
 */
public class IndexAction extends BaseAction {

    private static final long serialVersionUID = 5448158832007961012L;

    public String showIndex() {
        
        return SUCCESS;
    }
    
    private PromotionInfoVO queryPromotion(List<Long> promotionIdList){
    	return null;
    	
    }



}
