package base.test.dbsharding.client.rule;

/**
 */
public interface RouteRule<T> {

    /**
     * 获取库的索引数组下标
     *
     * @param routeFactor，路由条件
     * @return  库的索引数组下标
     */
    int getDbIndex(T routeFactor);

    /**
     * 获取表的索引数组下标
     *
     * @param routeFactor，路由条件
     * @return  表的索引数组下标
     */
    int getTableIndex(T routeFactor);
}
