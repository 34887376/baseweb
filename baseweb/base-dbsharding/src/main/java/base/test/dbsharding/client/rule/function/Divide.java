package base.test.dbsharding.client.rule.function;

/**
 *  对象的整除运算，只支持Integer,Long和数字类型的String<br/>
 *  其它类型的输入会抛异常，@see java.lang.IllegalArgumentException
 *
 * @author:huangzongwang
 * @since: 1.0.0
 */
public class Divide extends Function<Long> {

    /**
     * 除数
     */
    private int dividNumber;

    /**
     * 除法运算的默认构造器，传入除数
     *
     * @param dividNumber，整除运算的除数
     */
    public Divide(int dividNumber) {
        if(dividNumber <= 0) {
            throw new IllegalArgumentException("positive(>0) dividNumber field is required!");
        }
        this.dividNumber = dividNumber;
    }

    @Override
    long eval(Long dividedNumber) {
        return dividedNumber / dividedNumber;
    }

    @Override
    Long parseInput(Object obj) {
        if(obj instanceof Long) {
            return (Long)obj;
        } else if(obj instanceof Integer){
            return Long.parseLong(String.valueOf(obj));
        } else if(obj instanceof String) {
            return Long.parseLong((String)obj);
        }
        throw new IllegalArgumentException("[Function#Divide:Illegal input parameter type!]");
    }
}
