package base.test.dbsharding.client.router;

import base.test.dbsharding.client.exception.RouteException;
import base.test.dbsharding.client.support.RouteResult;


/**
 * 数据源的路由器接口，负责根据条件选出
 *
 */
public interface Router<T> {

    /**
     * 执行具体的数据源的路由
     *
     * @param routeCondition,路由规则所需的参数信息
     * @return 具体的路由方式
     * @throws RouteException，路由处理出错
     */
    RouteResult doRoute(T routeCondition) throws RouteException;


}
