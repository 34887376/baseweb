package base.test.dbsharding.client.spring;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import base.test.dbsharding.client.RouteFactor;
import base.test.dbsharding.client.datasource.MultipleDataSourcesService;
import base.test.dbsharding.client.exception.RouteException;
import base.test.dbsharding.client.exception.UnknownDataAccessException;
import base.test.dbsharding.client.router.Router;
import base.test.dbsharding.client.support.RouteContextConstants;
import base.test.dbsharding.client.support.RouteResult;
import base.test.dbsharding.client.util.RouteContext;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * Spring的SqlMapClientTemplate类的扩展实现，支持数据分片场景的多源访问<br/>
 * 当前实现支持如下两种模式：<br/>
 * 1.数据表多库多表模式:配置了MultiDataSourceService和Router后自动处于此模式<br/>
 * 2.普通的单库单表模式:跟直接使用SqlMapClientTemplate时一样，不要配置dataSourceService或router<br/>
 * <p/>
 * Note:多库多表模式只支持单库级别的事务，使用方式跟单库单表模式一致<br/>
 * 
 * Nowadays com.jd.sharding.client.DbShardSqlMapClientTemplate is encouraged as simple circumstances
 * 
 * @see org.springframework.transaction.support.TransactionTemplate
 * @see org.springframework.transaction.PlatformTransactionManager
 */
@Deprecated
public class ShardSqlMapClientTemplate extends SqlMapClientTemplate {

    private MultipleDataSourcesService multiDataSourcesService;

    private Router<RouteFactor> router;

    /**
     * Execute the given data access action on a SqlMapExecutor.
     * 
     * @param action
     *            callback object that specifies the data access action
     * @return a result object returned by the action, or <code>null</code>
     * @throws DataAccessException
     *             in case of SQL Maps errors
     */
    @Override
    public Object execute(SqlMapClientCallback action) throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> dataSources = getRoutedDataSource(null, null);
            if (!CollectionUtils.isEmpty(dataSources)) {
                if (dataSources.size() > 1) {
                    throw new RouteException("Multiple DataSources were selected by the Router!");
                }
                return this.executeWithObjectResult(dataSources.get(0), action);
            }
        }
        return super.execute(action);
    }

    /**
     * Execute the given data access action on a SqlMapExecutor,
     * expecting a List result.
     * 
     * @param action
     *            callback object that specifies the data access action
     * @return the List result
     * @throws DataAccessException
     *             in case of SQL Maps errors
     */
    @Override
    public List executeWithListResult(SqlMapClientCallback action) throws DataAccessException {
        return (List) this.execute(action);
    }

    /**
     * Execute the given data access action on a SqlMapExecutor,
     * expecting a Map result.
     * 
     * @param action
     *            callback object that specifies the data access action
     * @return the Map result
     * @throws DataAccessException
     *             in case of SQL Maps errors
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map executeWithMapResult(SqlMapClientCallback action) throws DataAccessException {
        return (Map) this.execute(action);
    }

    @Override
    public Object queryForObject(final String statementName, final Object parameterObject, final Object resultObject) throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> routedDataSources = getRoutedDataSource(statementName, parameterObject);
            if (!CollectionUtils.isEmpty(routedDataSources)) {
                if (routedDataSources.size() == 1) {
                    SqlMapClientCallback callback;
                    if (resultObject == null) {
                        callback = new SqlMapClientCallback() {

                            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                                return executor.queryForObject(statementName, parameterObject);
                            }
                        };
                    } else {
                        callback = new SqlMapClientCallback() {

                            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                                return executor.queryForObject(statementName, parameterObject, resultObject);
                            }
                        };
                    }
                    return executeWithObjectResult(routedDataSources.get(0), callback);
                } else if (routedDataSources.size() > 1) {
                    throw new RouteException("Multiple DataSources were selected by the Router!");
                }
            }
        }
        if (resultObject == null) {
            return super.queryForObject(statementName, parameterObject);
        } else {
            return super.queryForObject(statementName, parameterObject, resultObject);
        }
    }

    @Override
    public Object queryForObject(String statementName) throws DataAccessException {
        return this.queryForObject(statementName, null);
    }

    @Override
    public Object queryForObject(String statementName, Object parameterObject) throws DataAccessException {
        return this.queryForObject(statementName, parameterObject, null);
    }

    @Override
    public List queryForList(String statementName) throws DataAccessException {
        return this.queryForList(statementName, null, null, null);
    }

    @Override
    public List queryForList(String statementName, Object parameterObject) throws DataAccessException {
        return this.queryForList(statementName, parameterObject, null, null);
    }

    @Override
    public List queryForList(String statementName, int skipResults, int maxResults) throws DataAccessException {
        return this.queryForList(statementName, null, Integer.valueOf(skipResults), Integer.valueOf(maxResults));
    }

    @Override
    public List queryForList(String statementName, Object parameterObject, int skipResults, int maxResults) throws DataAccessException {
        return this.queryForList(statementName, parameterObject, Integer.valueOf(skipResults), Integer.valueOf(maxResults));
    }

    protected List queryForList(final String statementName, final Object parameterObject, final Integer skipResults, final Integer maxResults)
            throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> dataSources = getRoutedDataSource(statementName, parameterObject);
            if (!CollectionUtils.isEmpty(dataSources)) {
                if (dataSources.size() > 1) {
                    throw new RouteException("Multiple Datasources were selected by the Router!");
                }
                SqlMapClientCallback callback = null;
                if (skipResults == null || maxResults == null) {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.queryForList(statementName, parameterObject);
                        }
                    };
                } else {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.queryForList(statementName, parameterObject, skipResults, maxResults);
                        }
                    };
                }
                return (List) this.executeWithObjectResult(dataSources.get(0), callback);
            }
        }
        if (skipResults == null || maxResults == null) {
            return super.queryForList(statementName, parameterObject);
        } else {
            return super.queryForList(statementName, parameterObject, skipResults, maxResults);
        }
    }

    @Override
    public void queryWithRowHandler(String statementName, RowHandler rowHandler) throws DataAccessException {
        this.queryWithRowHandler(statementName, null, rowHandler);
    }

    @Override
    public void queryWithRowHandler(final String statementName, final Object parameterObject, final RowHandler rowHandler) throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> dataSources = getRoutedDataSource(statementName, parameterObject);
            if (!CollectionUtils.isEmpty(dataSources)) {
                if (dataSources.size() > 1) {
                    throw new RouteException("Multiple Datasources were selected by the Router!");
                }
                SqlMapClientCallback callback = null;
                if (parameterObject == null) {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            executor.queryWithRowHandler(statementName, rowHandler);
                            return null;
                        }
                    };
                } else {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            executor.queryWithRowHandler(statementName, parameterObject, rowHandler);
                            return null;
                        }
                    };
                }
                this.executeWithObjectResult(dataSources.get(0), callback);
                return;
            }
        }
        if (parameterObject == null) {
            super.queryWithRowHandler(statementName, rowHandler);
        } else {
            super.queryWithRowHandler(statementName, parameterObject, rowHandler);
        }
    }

    @Override
    public Map queryForMap(String statementName, Object parameterObject, String keyProperty) throws DataAccessException {
        return this.queryForMap(statementName, parameterObject, keyProperty, null);
    }

    @Override
    public Map queryForMap(final String statementName, final Object parameterObject, final String keyProperty, final String valueProperty)
            throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> dataSources = getRoutedDataSource(statementName, parameterObject);
            if (!CollectionUtils.isEmpty(dataSources)) {
                if (dataSources.size() > 1) {
                    throw new RouteException("Multiple DataSources were selected by the Router!");
                }
                SqlMapClientCallback callback = null;
                if (valueProperty == null) {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.queryForMap(statementName, parameterObject, keyProperty);
                        }
                    };
                } else {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.queryForMap(statementName, parameterObject, keyProperty, valueProperty);
                        }
                    };
                }
                return (Map) executeWithObjectResult(dataSources.get(0), callback);
            }
        }
        if (valueProperty == null) {
            return super.queryForMap(statementName, parameterObject, keyProperty);
        } else {
            return super.queryForMap(statementName, parameterObject, keyProperty, valueProperty);
        }
    }

    @Override
    public Object insert(String statementName) throws DataAccessException {
        return this.insert(statementName, null);
    }

    @Override
    public Object insert(final String statementName, final Object parameterObject) throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> dataSources = getRoutedDataSource(statementName, parameterObject);
            if (CollectionUtils.isEmpty(dataSources)) {
                if (dataSources.size() > 1) {
                    throw new RouteException("Multiple DataSources were selected by the Router!");
                }
                SqlMapClientCallback callback = null;
                if (parameterObject == null) {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.insert(statementName);
                        }
                    };
                } else {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.insert(statementName, parameterObject);
                        }
                    };
                }
                return executeWithObjectResult(dataSources.get(0), callback);
            }
        }
        if (parameterObject == null) {
            return super.insert(statementName);
        } else {
            return super.insert(statementName, parameterObject);
        }
    }

    @Override
    public int update(String statementName) throws DataAccessException {
        return this.update(statementName, null);
    }

    @Override
    public int update(final String statementName, final Object parameterObject) throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> dataSources = getRoutedDataSource(statementName, parameterObject);
            if (CollectionUtils.isEmpty(dataSources)) {
                if (dataSources.size() > 1) {
                    throw new RouteException("Multiple DataSources were selected by the Router!");
                }
                SqlMapClientCallback callback = null;
                if (parameterObject == null) {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.update(statementName);
                        }
                    };
                } else {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.update(statementName, parameterObject);
                        }
                    };
                }
                Integer updateCount = (Integer) executeWithObjectResult(dataSources.get(0), callback);
                return updateCount == null ? 0 : updateCount;
            }
        }
        if (parameterObject == null) {
            return super.update(statementName);
        } else {
            return super.update(statementName, parameterObject);
        }
    }

    @Override
    public void update(String statementName, Object parameterObject, int requiredRowsAffected) throws DataAccessException {
        int actualRowsAffected = this.update(statementName, parameterObject);
        if (actualRowsAffected != requiredRowsAffected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(statementName, requiredRowsAffected, actualRowsAffected);
        }
    }

    @Override
    public int delete(String statementName) throws DataAccessException {
        return this.delete(statementName, null);
    }

    @Override
    public int delete(final String statementName, final Object parameterObject) throws DataAccessException {
        if (isShardingBehaviorEnable()) {
            List<DataSource> dataSources = getRoutedDataSource(statementName, parameterObject);
            if (CollectionUtils.isEmpty(dataSources)) {
                if (dataSources.size() > 1) {
                    throw new RouteException("Multiple DataSources were selected by the Router!");
                }
                SqlMapClientCallback callback = null;
                if (parameterObject == null) {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.delete(statementName);
                        }
                    };
                } else {
                    callback = new SqlMapClientCallback() {

                        public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                            return executor.delete(statementName, parameterObject);
                        }
                    };
                }
                Integer deleteCount = (Integer) executeWithObjectResult(dataSources.get(0), callback);
                return deleteCount == null ? 0 : deleteCount;
            }
        }
        if (parameterObject == null) {
            return super.delete(statementName);
        } else {
            return super.delete(statementName, parameterObject);
        }
    }

    @Override
    public void delete(String statementName, Object parameterObject, int requiredRowsAffected) throws DataAccessException {
        int actualRowsAffected = this.delete(statementName, parameterObject);
        if (actualRowsAffected != requiredRowsAffected) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(statementName, requiredRowsAffected, actualRowsAffected);
        }
    }

    protected Object executeWithObjectResult(final DataSource dataSource, final SqlMapClientCallback callback) {
        Assert.notNull(dataSource, "None DataSource is specified!");
        Assert.notNull(callback, "None SqlMapClientCallback is specified!");
        Assert.notNull(getSqlMapClient(), "None SqlMapClient is specified!");
        SqlMapSession session = getSqlMapClient().openSession();
        try {
            Connection springCon = null;
            try {// Get Jdbc Connection
                springCon = dataSource.getConnection();
                session.setUserConnection(springCon);
            } catch (SQLException e) {
                throw new CannotGetJdbcConnectionException("Could not Get Jdbc Connection!", e);
            }

            try {
                return callback.doInSqlMapClient(session);
            } catch (SQLException e) {
                throw new SQLErrorCodeSQLExceptionTranslator().translate("SqlMapClient operation", null, e);
            } catch (Throwable t) {
                throw new UnknownDataAccessException("Unknown exception occurs on processing data access!", t);
            } finally {// Close the Connection
                try {
                    if (springCon != null) {
                        // session.close();
                        springCon.close();
                    }
                } catch (Throwable et) {
                    throw new RuntimeException(et);
                }
            }

        } finally {// Close the SqlMapSession
            session.close();
        }
    }

    protected List<DataSource> getRoutedDataSource(String statementName, Object parameterObj) throws DataAccessException {
        List<DataSource> dataSources = new LinkedList<DataSource>();
        if (!isShardingBehaviorEnable()) {
            return dataSources;
        }
        RouteFactor routeFactor = (RouteFactor) RouteContext.get(RouteContextConstants.ROUTE_FACTOR_KEY);
        if (routeFactor != null) {
            try {
                RouteResult result = getRouter().doRoute(routeFactor);
                List<String> identities = result.getResourceIdentities();
                if (!CollectionUtils.isEmpty(identities)) {
                    for (String identity : identities) {
                        dataSources.add(getMultiDataSourcesService().getDataSources().get(identity));
                    }
                }
            } catch (RouteException e) {
                throw new DataAccessResourceFailureException("Route Error!routeFactor:" + routeFactor);
            }
        }
        return dataSources;
    }

    private boolean isShardingBehaviorEnable() {
        return getMultiDataSourcesService() != null && getRouter() != null;
    }

    public Router<RouteFactor> getRouter() {
        return router;
    }

    public void setRouter(Router<RouteFactor> router) {
        this.router = router;
    }

    public MultipleDataSourcesService getMultiDataSourcesService() {
        return multiDataSourcesService;
    }

    public void setMultiDataSourcesService(MultipleDataSourcesService multiDataSourcesService) {
        this.multiDataSourcesService = multiDataSourcesService;
    }
}
