package base.test.dbsharding.client.config;

import java.util.HashMap;
import java.util.Map;

import base.test.dbsharding.client.rule.function.Function;
import base.test.dbsharding.client.rule.function.Hash;


/**
 * Function的注册器，用于持有使用无参数构造器生成的Function对象
 *
 */
public class FunctionRegistry {

    public static final String HASH_FUNC_NAME = "hash";

    private static final Map<String, Function> funcMap = new HashMap<String, Function>(2);
    static {
        funcMap.put(HASH_FUNC_NAME, new Hash());
    }

    public static Function getFunction(String funcName) {
        return funcMap.get(funcName);
    }
}
