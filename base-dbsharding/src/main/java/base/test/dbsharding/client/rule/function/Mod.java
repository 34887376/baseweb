package base.test.dbsharding.client.rule.function;

/**
 *  对象的求余运算的函数，只支持Integer,Long类型的正整数以及对应的String
 *
 */
public class Mod extends Function<Long> {

    /**
     * 求余因子，只能为正整数
     */
    private int factor;

    /**
     * 求余运算的构造器，需要传入正整数作为求余因子
     *
     * @param factor 求余因子
     */
    public Mod(int factor) {
        if(factor <= 0) {
            throw new IllegalArgumentException("factor must be positive(>0)!");
        }
        this.factor = factor;
    }

    @Override
    public long eval(Long longValue) {
        if(longValue == null) {
            throw new IllegalArgumentException("[Function#Mod]longValue cann't be null!");
        }
        return longValue % factor;
    }

    @Override
    Long parseInput(Object obj) {
        if(obj instanceof Integer) {
            return Long.parseLong(String.valueOf(obj));
        } else if(obj instanceof Long) {
            return (Long)obj;
        } else if(obj instanceof String) {
            return Long.parseLong((String)obj);
        }
        throw new IllegalArgumentException("[Function#Mod]Invalid input Object!");
    }
}
