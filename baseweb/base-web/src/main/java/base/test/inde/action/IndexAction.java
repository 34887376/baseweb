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

import base.test.base.action.BaseAction;


/**
 * @author zhoushanjie
 * 
 */
public class IndexAction extends BaseAction {

    private static final long serialVersionUID = 5448158832007961012L;

    public String showIndex() {
        String pin = getPin();

        List<String> subjectVoList = new ArrayList<String>();

        // 如果用户没有选中主题,则随机选择一个主题
        String selectedSubject = subjectVoList.get(0);


        List<String> topicList = new ArrayList<String>();
        List<String> topicAbstractList = new ArrayList<String>();
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("selectedSubject", selectedSubject);
        paraMap.put("subjectList", subjectVoList);
        paraMap.put("topicList", topicAbstractList);
        putParamToVm(paraMap);
        return SUCCESS;
    }



}
