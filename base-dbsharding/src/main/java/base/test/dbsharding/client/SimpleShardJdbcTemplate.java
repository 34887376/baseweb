package base.test.dbsharding.client;

import static base.test.dbsharding.client.annotation.Constants.DEFAULT_SHARDING_FIELD;
import static base.test.dbsharding.client.annotation.Constants.DEFAULT_TABLE_NAME_FIELD;
import static base.test.dbsharding.client.util.BeanPropertyAccessUtil.getPropertyValue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import base.test.dbsharding.client.annotation.Sharding;
import base.test.dbsharding.client.datasource.MultipleDataSourcesService;
import base.test.dbsharding.client.exception.RouteException;
import base.test.dbsharding.client.exception.UnknownDataAccessException;
import base.test.dbsharding.client.router.Router;
import base.test.dbsharding.client.support.RouteContextConstants;
import base.test.dbsharding.client.support.RouteResult;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;

/**
 * 支持简单的数据分片场景的多源访问<br/>
 * 当前实现需要依赖如下两个对象的支持：dataSourcesService,router<br/>
 *
 * <p/>
 * Note:多库多表模式只支持单库级别的事务，使用方式跟单库单表模式一致<br/>
 *
 * 更多方便的功能查看扩展自spring的SqlMapClientTemplate的实现ShardSqlMapClientTemplate
 * @see com.jd.sharding.client.spring.ShardSqlMapClientTemplate
 * @see org.springframework.orm.ibatis.SqlMapClientTemplate
 * @see org.springframework.transaction.support.TransactionTemplate
 * @see org.springframework.transaction.PlatformTransactionManager
 *
 */
public class SimpleShardJdbcTemplate extends JdbcAccessor implements InitializingBean{

    private SqlMapClient sqlMapClient;

    private MultipleDataSourcesService dataSourcesService;

    private Router<RouteFactor> router;

    private boolean lazyLoadingAvailable = true;

    private String logicalTabName;

    /**
     * 查询单条记录
     * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForObject(String, Object)
     * @param statementName，name of mapping statement
     * @param parameterObj,the parameter object
     *
     * @return  单条记录Object
     * @throws DataAccessException
     */
    public Object queryForObject(final String statementName, final Object parameterObj) throws DataAccessException {

        SqlMapClientCallback callback = new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.queryForObject(statementName, parameterObj);
            }
        };
        return executeWithObjectResult(getRoutedSingleDataSource(statementName, parameterObj), callback);
    }

    /**
     * 查询记录列表
     * @see com.ibatis.sqlmap.client.SqlMapExecutor#queryForList(String, Object)
     * @param statementName，name of mapping statement
     * @param parameterObj,the parameter object
     *
     * @return 记录列表List
     * @throws DataAccessException
     */
    public List queryForList(final String statementName, final Object parameterObj) throws DataAccessException {
        SqlMapClientCallback callback = new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.queryForList(statementName, parameterObj);
            }
        };
        return (List) executeWithObjectResult(getRoutedSingleDataSource(statementName, parameterObj), callback);
    }

    /**
     * 插入单条记录
     *
     * @see com.ibatis.sqlmap.client.SqlMapExecutor#insert(String, Object)
     * @param statementName,name of mapping statement
     * @param parameterObj,the parameter object
     *
     * @return
     * @throws DataAccessException
     */
    public Object insert(final String statementName, final Object parameterObj) throws DataAccessException {
        SqlMapClientCallback callback = new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.insert(statementName, parameterObj);
            }
        };
        return executeWithObjectResult(getRoutedSingleDataSource(statementName, parameterObj), callback);
    }

    /**
     * 修改单条记录
     *
     * @see com.ibatis.sqlmap.client.SqlMapExecutor#update(String, Object)
     * @param statementName,name of mapping statement
     * @param parameterObject,the parameter object
     *
     * @return count of rows affected
     * @throws DataAccessException
     */
    public int update(final String statementName, final Object parameterObject) throws DataAccessException {
        SqlMapClientCallback callback = new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.update(statementName, parameterObject);
            }
        };
        Integer rowsAffected = (Integer)executeWithObjectResult(getRoutedSingleDataSource(statementName, parameterObject), callback);
        return rowsAffected == null ? 0 : rowsAffected;
    }

    /**
     * 删除单条记录
     *
     * @see com.ibatis.sqlmap.client.SqlMapExecutor#delete(String, Object)
     * @param statementName,name of mapping statement
     * @param parameterObject,the parameter object
     *
     * @return count of rows affected
     * @throws DataAccessException
     */
    public int delete(final String statementName, final Object parameterObject) throws DataAccessException {
        SqlMapClientCallback callback = new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.delete(statementName, parameterObject);
            }
        };
        Integer rowsAffected = (Integer)executeWithObjectResult(getRoutedSingleDataSource(statementName, parameterObject), callback);
        return rowsAffected == null ? 0 : rowsAffected;
    }

    /**
     * 根据条件和规则路由后获取的单数据源<br/>
     *
     * @param routeFactor,路由条件
     * @return DataSource,路由选取的数据源
     * @throws RouteException 路由时未找到数据源或找到>1个数据源抛出此异常
     */
    @Deprecated
    protected DataSource getRoutedSingleDataSource(RouteFactor routeFactor)throws RouteException {
        List<DataSource> dsList = getRoutedDataSources(routeFactor);
        if (CollectionUtils.isEmpty(dsList)) {
            throw new RouteException("Cann't Find Routed DataSources!");
        } else if(dsList.size() > 1) {
            throw new RouteException("Multiple DataSources were selected by the Router!");
        }
        return dsList.get(0);
    }

    /**
     * 根据条件和规则路由后获取的单数据源<br/>
     *
     * @param statementName,name of mapping statement
     * @param parameterObj,the parameter object
     *
     * @return DataSource,路由选取的数据源
     * @throws RouteException 路由时未找到数据源或找到>1个数据源抛出此异常
     */
    protected DataSource getRoutedSingleDataSource(final String statementName, final Object parameterObj) throws RouteException {
        RouteFactor routeFactor = loadRouteFactor(statementName, parameterObj);
        RouteResult result = getRouter().doRoute(routeFactor);
        if(!CollectionUtils.isEmpty(result.getResourceIdentities())) {
            List<DataSource> dataSources = new LinkedList<DataSource>();
            for(String identity : result.getResourceIdentities()) {
                DataSource ds = getDataSourcesService().getDataSources().get(identity);
                if(ds != null) {
                    dataSources.add(ds);
                }
            }
            if (dataSources.size() > 1) {
                throw new RouteException("Multiple DataSources were selected by the Router!");
            } else if(dataSources.size() == 1) {
                return dataSources.get(0);
            }
        }
        throw new RouteException("can not find routed datasource by your parameter object!");
    }

    /**
     * 从传入的参数中装载路由信息<br/>
     * 需要获取的路由信息包括:<br/>
     * 1. 需要切分的表的名称(逻辑表名,在sql-mapping文件中使用的占位符也需要保持一致)<br/>
     * 2. 计算路由信息所需的变量(即分片的属性名称)<br/>
     * 如果传入的参数是Map,直接从其分别读取默认Key的值(Constants.DEFAULT_TABLE_NAME_FIELD,Constants.DEFAULT_SHARDING_FIELD)<br/>
     * 如果是其它对象类型,读取对应的属性值(可以通过Sharding这个Annotation指定,未指定就使用默认的)<br/>
     *
     * @param parameterObj,SQL-MAP执行时传给Ibatis的参数:若为map,直接从里面读取需要切分的表
     * @return
     * @throws RouteException
     */
    protected RouteFactor loadRouteFactor(String statementName, Object parameterObj) throws RouteException {
        try {
            String logicalTableName = this.logicalTabName;
            Object routeField = null;
            if (parameterObj instanceof Map) {
                Map parametersMap = (Map) parameterObj;
                if(StringUtils.isBlank(logicalTableName)) {
                    logicalTableName = (String) parametersMap.get(DEFAULT_TABLE_NAME_FIELD);
                }
                routeField = parametersMap.get(DEFAULT_SHARDING_FIELD);
            } else if (parameterObj != null) {
                if (parameterObj.getClass().isAnnotationPresent(Sharding.class)) {
                    Sharding sharding = parameterObj.getClass().getAnnotation(Sharding.class);
                    if(StringUtils.isBlank(logicalTableName)) {
                        if (StringUtils.isNotBlank(sharding.tabName())) {
                            logicalTableName = sharding.tabName();
                        } else {
                            logicalTableName = (String) getPropertyValue(sharding.tabNameField(), parameterObj);
                        }
                    }
                    routeField = getPropertyValue(sharding.shardField(), parameterObj);
                } else {
                    if(StringUtils.isBlank(logicalTableName)) {
                        logicalTableName = (String) getPropertyValue(DEFAULT_TABLE_NAME_FIELD, parameterObj);
                    }
                    routeField = getPropertyValue(DEFAULT_SHARDING_FIELD, parameterObj);
                }
            }
            RouteFactor routeFactor = new RouteFactor(logicalTableName);
            routeFactor.addRouteField(RouteContextConstants.ROUTE_SHARD_FIELD_KEY, routeField);
            routeFactor.setAction(statementName);
            routeFactor.setParameter(parameterObj);
            return routeFactor;
        } catch (Throwable e) {
            throw new RouteException("load route factor error,please check your parameterObject!", e);
        }
    }

    protected Object executeWithObjectResult(final DataSource dataSource, final SqlMapClientCallback callback) throws DataAccessException{
        Assert.notNull(dataSource, "None DataSource is specified!");
        Assert.notNull(callback, "None SqlMapClientCallback is specified!");
        Assert.notNull(getSqlMapClient(), "None SqlMapClient is specified!");
        SqlMapSession session = getSqlMapClient().openSession();
        try {
            Connection springCon = null;
            //boolean isTransactionAware = (dataSource instanceof TransactionAwareDataSourceProxy);
            try {//Get Jdbc Connection
                springCon = dataSource.getConnection();
                session.setUserConnection(springCon);
            } catch (SQLException e) {
                throw new CannotGetJdbcConnectionException("Could not Get Jdbc Connection!", e);
            }

            try {
                return callback.doInSqlMapClient(session);
            } catch (SQLException e) {
                throw new SQLErrorCodeSQLExceptionTranslator().translate("SqlMapClient operation",
                        null, e);
            } catch (Throwable t) {
                throw new UnknownDataAccessException("Unknown exception occurs on processing data access!", t);
            } finally {//Close the Connection
                try {
                    if (springCon != null) {
                    	springCon.close();
                    }
                } catch (Throwable et) {
                    //TODO log
                }
            }

        } finally {//Close the SqlMapSession
            session.close();
        }
    }

    /**
     * 根据路由参数信息获取对应的数据源列表
     *
     * @param routeFactor, 路由参数
     * @return  List<DataSource>路由匹配的数据源列表
     */
    protected List<DataSource> getRoutedDataSources(final RouteFactor routeFactor) {
        List<DataSource> dataSources = new LinkedList<DataSource>();
        RouteResult routeResult = getRouter().doRoute(routeFactor);
        if(routeResult != null && !CollectionUtils.isEmpty(routeResult.getResourceIdentities())) {
            for(String identity : routeResult.getResourceIdentities()) {
                DataSource ds = getDataSourcesService().getDataSources().get(identity);
                if(ds != null) {
                    dataSources.add(ds);
                }
            }
        }
        return dataSources;
    }

    /**
     * 获取路由后的实际的表名
     *
     * @param routeFactor,路由参数集合
     * @return  路由后的实际的表名
     */
    public String getPhysicalTableName(final RouteFactor routeFactor) {
        RouteResult routeResult = getRouter().doRoute(routeFactor);
        if(routeResult != null) {
            return routeResult.getPhysicalTableName();
        }
        return null;
    }

    @Override
    public DataSource getDataSource() {
        DataSource ds = super.getDataSource();
        return ds == null ? sqlMapClient.getDataSource() : ds;
    }

    @Override
    public void afterPropertiesSet() {
        if (getSqlMapClient() == null) {
            throw new IllegalArgumentException("Property 'sqlMapClient' is required");
        }
        if (getSqlMapClient() instanceof ExtendedSqlMapClient) {
            // Check whether iBATIS lazy loading is available, that is,
            // whether a DataSource was specified on the SqlMapClient itself.
            this.lazyLoadingAvailable = (((ExtendedSqlMapClient) getSqlMapClient()).getDelegate().getTxManager() != null);
        }
        if(getDataSourcesService() == null || getRouter() == null) {
            throw new IllegalArgumentException("dataSourcesService and router are both required!");
        }
        super.afterPropertiesSet();
    }

    public SqlMapClient getSqlMapClient() {
        return sqlMapClient;
    }

    public void setSqlMapClient(SqlMapClient sqlMapClient) {
        this.sqlMapClient = sqlMapClient;
    }

    public MultipleDataSourcesService getDataSourcesService() {
        return dataSourcesService;
    }

    public void setDataSourcesService(MultipleDataSourcesService dataSourcesService) {
        this.dataSourcesService = dataSourcesService;
    }

    public Router<RouteFactor> getRouter() {
        return router;
    }

    public void setRouter(Router<RouteFactor> router) {
        this.router = router;
    }

    public void setLogicalTabName(String logicalTabName) {
        this.logicalTabName = logicalTabName;
    }
}
