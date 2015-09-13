package base.test.dbsharding.client.router;


import java.util.Map;

import static base.test.dbsharding.client.annotation.Constants.DEFAULT_SHARDING_FIELD;
import static base.test.dbsharding.client.annotation.Constants.DEFAULT_TABLE_NAME_FIELD;
import static base.test.dbsharding.client.support.RouteContextConstants.ROUTE_SHARD_FIELD_KEY;
import org.apache.commons.lang.StringUtils;

import base.test.dbsharding.client.RouteFactor;
import base.test.dbsharding.client.config.ShardConfig;
import base.test.dbsharding.client.config.TableConfig;
import base.test.dbsharding.client.exception.RouteException;
import base.test.dbsharding.client.support.RouteResult;
import base.test.dbsharding.client.util.BeanPropertyAccessUtil;


/**
 * 默认的Router实现，其实现了数据水平切分后的路由方式
 *
 */
public class SimpleRouter implements Router<RouteFactor>{

    public void setShardConfig(ShardConfig shardConfig) {
        this.shardConfig = shardConfig;
    }

    private ShardConfig shardConfig;

    public RouteResult doRoute(RouteFactor routeCondition) throws RouteException {
        if (routeCondition == null) {
            throw new IllegalArgumentException("routeCondition or its logicalTableName cann't be null or empty!");
        }
        RouteResult result = new RouteResult();
        try {
            String logicalTableName = routeCondition.getLogicalTableName();
            TableConfig tableConfig = getTableConfig(logicalTableName);
            if (tableConfig != null) {
                Object routeField = getRouteField(tableConfig, routeCondition);
                if (routeField != null) {
                    int dbIndex = tableConfig.getRouteRule().getDbIndex(routeField);
                    result.addDbIdentities(tableConfig.getDbIndices()[dbIndex]);
                    int tableIndex = tableConfig.getRouteRule().getTableIndex(routeField);
                    result.setPhysicalTableName(tableConfig.getTableIndices()[tableIndex]);
                    String tableNameField = tableConfig.getTabNameField();
                    if(StringUtils.isNotBlank(tableNameField)) {
                        rewriteTableName(routeCondition.getParameter(), tableNameField, result.getPhysicalTableName());
                    }
                }
            }
        } catch (Throwable e) {
            throw new RouteException("Route error!", e);
        }

        return result;
    }

    /**
     * 获取路由字段的值<br/>
     * 1.路由字段可以在外部解析后通过RouteFactor传入;<br/>
     * 2.可以通过TableConfig的routeField进行配置,这里通过反射从Map或Object中获取；<br/>
     * 如果这两种方式都传了的话，以RouteFactor里面的值优先级更高.<br/>
     *
     * @param tableConfig,表的路由配置信息
     * @param routeFactor,由外部传入的路由参数信息
     * @return 路由字段的值
     * @throws RouteException 解析过程中出现异常
     */
    private Object getRouteField(final TableConfig tableConfig, final RouteFactor routeFactor) throws RouteException {
        Object routeField = routeFactor.getRouteField(ROUTE_SHARD_FIELD_KEY);
        if (routeField == null) {
            String fieldName = tableConfig.getRouteField();
            Object para = routeFactor.getParameter();
            if(StringUtils.isNotBlank(fieldName)) {
                if(para instanceof Map) {
                    Map<Object, Object> paramMap = (Map)para;
                    Object routeObj = paramMap.get(fieldName);
                    if(routeObj != null) {
                        routeField = routeObj;
                    } else if(paramMap.size() == 1) {
                        for(Map.Entry<Object, Object> entry : paramMap.entrySet()) {
                            routeObj = entry.getValue();
                        }
                        try {
                            routeField = BeanPropertyAccessUtil.getPropertyValue(fieldName, routeObj);
                        } catch (Exception e) {
                            throw new RouteException("getRouteField error!", e);
                        }
                    }
                } else if(para != null) {
                    try {
                        routeField = BeanPropertyAccessUtil.getPropertyValue(fieldName, para);
                    } catch (Exception e) {
                        throw new RouteException("getRouteField error!", e);
                    }
                }
            }
        }
        return routeField == null ? routeFactor.getParameter() : routeField;
    }

    /**
     * 获取路由表配置信息<br/>
     * 1.如果指定了逻辑表名，直接通过映射关系找到配置的表路由规则；<br/>
     * 2.如果未指定逻辑表名并且只配置了一张表的规则，就使用这张表对应的路由规则.<br/>
     *
     * @param logicalTableName,外部解析后传入的逻辑表名
     * @return 表的路由配置信息TableConfig
     */
    private TableConfig getTableConfig(String logicalTableName) {
        TableConfig tableConfig = null;
        Map<String, TableConfig> tableConfigMap = shardConfig.getTableConfig();
        if(StringUtils.isBlank(logicalTableName)) {
            if (tableConfigMap.size() == 1) {
                for (Map.Entry<String, TableConfig> entry : tableConfigMap.entrySet()) {
                    tableConfig = entry.getValue();
                }
            }
        } else {
            tableConfig = tableConfigMap.get(logicalTableName);
        }
        return tableConfig;
    }

    /**
     * 通过反射将物理的表名回写到传入的parameter对象对应的属性中
     *
     * @param parameterObj,the parameter object
     * @param tableNameField,parameter name of the table name passed to ibatis engine
     * @param physicalTableName,the true name after the logical table is routed
     */
    private void rewriteTableName(final Object parameterObj,final String tableNameField, final String physicalTableName) throws RouteException {
        try {
            if (parameterObj instanceof Map) {
                ((Map) parameterObj).put(tableNameField, physicalTableName);
            } else if (parameterObj != null) {
            	BeanPropertyAccessUtil.setPropertyValue(tableNameField, physicalTableName, parameterObj);
            }
        } catch (Exception e) {
            throw new RouteException("rewrite table name error!", e);
        }
    }

}
