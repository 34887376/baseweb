package base.test.dbsharding.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 路由条件的参数设置
 *
 */
public class RouteFactor {

    /**
     * the name of the logical table which need sharding or spliting
     */
    private String logicalTableName;

    /**
     * statement name
     */
    private String action;

    /**
     * parameter object of the statement
     */
    private Object parameter;

    private Map<String, Object> routeFields = null;

    /**
     * 使用逻辑表名构造路由参数集
     *
     * @param logicalTableName，逻辑表名
     */
    public RouteFactor(String logicalTableName) {
        this.logicalTableName = logicalTableName;
    }

    public RouteFactor(String statementName, Object parameterObj) {
        setAction(statementName);
        setParameter(parameterObj);
    }

    /**
     *  get the name of the logical table which need sharding or spliting
     * @return  the name of the logical table which need sharding or spliting
     */
    public String getLogicalTableName() {
        return logicalTableName;
    }

    /**
     * add route parameter field
     *
     * @param routeFieldKey,
     * @param routeFieldValue
     */
    public RouteFactor addRouteField(String routeFieldKey, Object routeFieldValue) {
        if(routeFields == null) {
            routeFields = new HashMap<String, Object>(1);
        }
        routeFields.put(routeFieldKey, routeFieldValue);
        return this;
    }

    public Object getRouteField(String routeFieldKey) {
        if(routeFields != null) {
            return routeFields.get(routeFieldKey);
        }
        return null;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
