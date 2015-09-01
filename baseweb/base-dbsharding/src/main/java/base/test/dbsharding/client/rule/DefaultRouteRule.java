package base.test.dbsharding.client.rule;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import base.test.dbsharding.client.config.FunctionRegistry;
import base.test.dbsharding.client.rule.function.Function;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
public class DefaultRouteRule implements RouteRule<Object>, InitializingBean {

    /**
     * 设置表路由函数集
     *
     * @param tableRouteFuncs，表的路由函数集
     */
    public void setTableRouteFuncs(List<Object> tableRouteFuncs) {
        this.tableRouteFuncs = getFuncs(tableRouteFuncs);
    }

    /**
     * 表的路由函数集
     */
    private List<Function> tableRouteFuncs;


    public List<Function> getDbRouteFuncs() {
        return Collections.unmodifiableList(dbRouteFuncs);
    }

    /**
     * 设置数据库的路由函数集
     *
     * @param dbRouteFuncs，路由函数集
     */
    public void setDbRouteFuncs(List<Object> dbRouteFuncs) {
        this.dbRouteFuncs = getFuncs(dbRouteFuncs);
    }

    /**
     * 库的路由函数集
     */
    private List<Function> dbRouteFuncs;

    public List<Function> getTableRouteFuncs() {
        return Collections.unmodifiableList(tableRouteFuncs);
    }

    /**
     * 设置需要对路由key进行计算的函数列表
     *
     * @param funcList 函数列表
     */
    public List<Function> getFuncs(List<Object> funcList) {
        if (!CollectionUtils.isEmpty(funcList)) {
            List<Function> funcs = new LinkedList<Function>();
            for (Object obj : funcList) {
                if (obj instanceof Function) {
                    funcs.add((Function) obj);
                } else if (obj instanceof String) {
                    Function func = FunctionRegistry.getFunction(FunctionRegistry.HASH_FUNC_NAME);
                    if (func == null) {
                        throw new IllegalArgumentException("Unregistered function!name:" + obj);
                    }
                    funcs.add(func);
                }
                throw new IllegalArgumentException("Illegal function parameter!");
            }
            return funcs;
        }
        return Collections.emptyList();
    }

    public int getDbIndex(Object routeFactor) {
        Long dbIndex = funcChain(routeFactor, dbRouteFuncs);
        return dbIndex == null ? 0:Integer.parseInt(String.valueOf(dbIndex));
    }

    public int getTableIndex(Object routeFactor) {
        Long tableIndex = funcChain(routeFactor, tableRouteFuncs);
        return tableIndex == null ? 0:Integer.parseInt(String.valueOf(tableIndex));
    }

    protected Long funcChain(final Object routeFactor, final List<Function> funcs) {
        Long result = null;
        for(int i=0;i<funcs.size();i++) {
            Function func = funcs.get(i);
            if(i==0) {
                result = func.process(routeFactor);
            } else {
                result = func.process(result);
            }
        }
        return result;
    }

    public void afterPropertiesSet() throws Exception {
        if(CollectionUtils.isEmpty(dbRouteFuncs)) {
            throw new IllegalStateException("dbRouteFuncs cann't be null or empty!");
        }
        if(CollectionUtils.isEmpty(tableRouteFuncs)) {
            throw new IllegalArgumentException("tableRouteFuncs cann't be null or empty!");
        }
    }
}
