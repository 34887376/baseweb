/**
 * 
 */
package com.ms.dao.base.dao;

/**
 * 
 * 数据库重试操作
 * 
 * @author zhoushanjie
 * 
 */
public abstract class RetryExcuter {

    /**
     * 重试方法,写入的返回
     * 
     * @return
     */
    public long retry() {
        long result = -1;
        for (int i = 0; i < 3; i++) {
            result = excuteMethod();
            if (result > 0) {
                break;
            }
            if (i == 3) {
                return -1;
            }
        }
        return result;
    }

    public abstract long excuteMethod();

}
