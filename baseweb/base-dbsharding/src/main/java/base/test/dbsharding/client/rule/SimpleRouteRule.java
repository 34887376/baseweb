package base.test.dbsharding.client.rule;

import base.test.dbsharding.client.util.MurmurHash;



/**
 * 库和表的路由规则，可以拿到具体的库和表的索引下标
 *
 */
public class SimpleRouteRule implements RouteRule<Object> {

   
    private int tablesCount;

    private int tablesCountPerDb;

    public SimpleRouteRule(int dbsCount, int tablesCount) {
        if(dbsCount <= 0 || tablesCount <= 0) {
            throw new IllegalArgumentException("dbsCount and tablesCount must be both positive!");
        }
        //this.dbsCount = dbsCount;
        this.tablesCount = tablesCount;
        this.tablesCountPerDb = (tablesCount/dbsCount);
        if(this.tablesCountPerDb == 0) {
        	throw new IllegalArgumentException("the expression 'tablesCount/dbsCount'==0!");
        }
    }


    public int getDbIndex(Object routeFactor) {
        //int index = 0;
        if(routeFactor instanceof Integer) {
            return Math.abs((Integer)routeFactor) % tablesCount / tablesCountPerDb;
        } else if(routeFactor instanceof Long) {
            return Integer.parseInt(String.valueOf(Math.abs((Long) routeFactor) % tablesCount / tablesCountPerDb));
        } else if(routeFactor instanceof String) {
            long value = MurmurHash.hash((String)routeFactor);
            return getDbIndex(Math.abs(value));
        }
        throw new IllegalArgumentException("Unsupported RouteFactor parameter type!");
    }

    public int getTableIndex(Object routeFactor) {
        if(routeFactor instanceof Integer) {
            return Math.abs((Integer)routeFactor) % tablesCount;
        } else if(routeFactor instanceof Long) {
            return Integer.parseInt(String.valueOf(Math.abs((Long) routeFactor) % tablesCount));
        } else if(routeFactor instanceof String) {
            String routeFactorStr = (String)routeFactor;
            long value = MurmurHash.hash(routeFactorStr);
            return getTableIndex(Math.abs(value));
        }
        throw new IllegalArgumentException("Unsupported RouteFactor parameter type!");
    }
}
