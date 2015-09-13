package base.test.dbsharding.client.rule.function;

/**
 * 用于路由计算的函数
 */
public abstract class Function<T> {

    /**
     * 根据输入计算返回输出
     *
     * @param t 输入
     * @return 计算后的结果
     */
    abstract long eval(T t);

    /**
     * 转换输入参数的类型
     *
     * @param obj,输入的都统一为Object
     * @return eval方法所需的类型
     */
    abstract T parseInput(Object obj);

    /**
     * 执行函数
     *
     * @param input,输入参数
     * @return  函数计算的结果
     */
    public long process(Object input) {
        return eval(parseInput(input));
    }
}
