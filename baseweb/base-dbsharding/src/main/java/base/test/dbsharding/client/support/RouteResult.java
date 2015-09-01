package base.test.dbsharding.client.support;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由结果的表示
 *
 */
public class RouteResult {

    /**
     * 经路由处理后满足条件的数据源索引列表
     */
    private List<String> resourceIdentities = new ArrayList<String>(2);

    public String getPhysicalTableName() {
        return physicalTableName;
    }

    public void setPhysicalTableName(String physicalTableName) {
        this.physicalTableName = physicalTableName;
    }

    /**
     * 实际访问的表的名称
     */
    private String physicalTableName;

    /**
     * 获取满足路由规则的数据源索引列表
     * @return List<String>,数据源索引列表
     */
    public List<String> getResourceIdentities() {
        return resourceIdentities;
    }

    public void addDbIdentities(List<String> resourceIdentities) {
        if(resourceIdentities != null) {
            this.resourceIdentities.addAll(resourceIdentities);
        }
    }

    public void addDbIdentities(String identity) {
        if(StringUtils.isNotBlank(identity)) {
            this.resourceIdentities.add(identity);
        }
    }

}
