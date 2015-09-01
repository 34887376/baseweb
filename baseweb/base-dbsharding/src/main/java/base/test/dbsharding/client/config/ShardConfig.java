package base.test.dbsharding.client.config;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 维护逻辑表名到TableConfig的映射关系的容器，从定义规则的配置文件中初始化
 *
 */
public class ShardConfig implements ApplicationContextAware {

    private ApplicationContext springContext;

    /**
     * 逻辑表名与TableConfig的映射关系
     */
     private Map<String, TableConfig> tableConfig;

    /**
     * 获取逻辑表名与TableConfig的映射关系
     *
     * @return 不能修改的映射关系,Collections.UnmodifiableMap<String, TableConfig>
     */
    public Map<String, TableConfig> getTableConfig() {
        if(CollectionUtils.isEmpty(tableConfig)) {
              tableConfig = Collections.emptyMap();
        }
        return Collections.unmodifiableMap(tableConfig);
    }

    /**
     * setter for the field:tableConfig
     * @param tableConfig
     */
    public void setTableConfig(Map<String, TableConfig> tableConfig) {
        this.tableConfig = tableConfig;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }
}
