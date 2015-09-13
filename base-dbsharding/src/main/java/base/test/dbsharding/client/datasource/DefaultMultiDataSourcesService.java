package base.test.dbsharding.client.datasource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class DefaultMultiDataSourcesService implements MultipleDataSourcesService, ApplicationContextAware, InitializingBean{

    private ApplicationContext springContext;

    private Map<String, DataSource> dataSourceMap;

    public void setDataSourcePool(Map<String, Object> dataSourcePool) {
        this.dataSourcePoolConfig = dataSourcePool;
    }

    private Map<String, Object> dataSourcePoolConfig;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    public Map<String, DataSource> getDataSources() {
        if(CollectionUtils.isEmpty(dataSourceMap)) {
             dataSourceMap = Collections.emptyMap();
        }
        return Collections.unmodifiableMap(dataSourceMap);
    }

    public void afterPropertiesSet() throws Exception {
        if(CollectionUtils.isEmpty(dataSourcePoolConfig)) {
            throw new IllegalArgumentException("dataSourcePool cann't be null or empty!");
        }
        dataSourceMap = new HashMap<String, DataSource>(dataSourcePoolConfig.size());
        for(Map.Entry<String, Object> entity : dataSourcePoolConfig.entrySet()) {
            Object value = entity.getValue();
            if(value instanceof DataSource) {
                dataSourceMap.put(entity.getKey(), (DataSource)value);
            } else if(value instanceof String) {
                Object valueBean = springContext.getBean((String)value);
                if(valueBean instanceof DataSource) {
                    dataSourceMap.put(entity.getKey(), (DataSource)valueBean);
                } else {
                    throw new IllegalArgumentException("illegal value argument,key:" + entity.getKey() +
                            ",a reference or bean name of type javax.sql.DataSource is required!");
                }
            } else {
                throw new IllegalArgumentException("illegal value argument,key:" + entity.getKey() +
                            ",a reference or bean name of type javax.sql.DataSource is required!");
            }
        }
    }
}
