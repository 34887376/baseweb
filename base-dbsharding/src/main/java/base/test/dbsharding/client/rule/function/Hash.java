package base.test.dbsharding.client.rule.function;

import base.test.dbsharding.client.util.MurmurHash;


/**
 * Hash函数，使用MurmurHash算法对字符串计算hash code
 *
 */
public class Hash extends Function<String> {

    /**
     * 使用MurmurHash算法求字符串类型key的hash code
     *
     * @param key, 字符串类型的key
     * @return 字符串类型的key对应的hash code
     */
    @Override
    public long eval(String key) {
        return MurmurHash.hash(key);
    }

    @Override
    public String parseInput(Object obj) {
        if(obj instanceof String) {
            return (String)obj;
        } else if(obj != null) {
            return String.valueOf(obj);
        }
        throw new IllegalArgumentException("[Function#Hash]Input cann't be transfered to String!");
    }
}
