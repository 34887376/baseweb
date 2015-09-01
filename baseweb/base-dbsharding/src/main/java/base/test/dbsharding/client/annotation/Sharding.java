package base.test.dbsharding.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  用于标注分库分表context信息的annotation类型<br/>
 *  只能使用在ElementType.TYPE元素上.<br/>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Sharding {

    String tabName() default Constants.DEFAULT_TABLE_NAME;

    String tabNameField() default Constants.DEFAULT_TABLE_NAME_FIELD;

    String shardField() default Constants.DEFAULT_SHARDING_FIELD;
}
