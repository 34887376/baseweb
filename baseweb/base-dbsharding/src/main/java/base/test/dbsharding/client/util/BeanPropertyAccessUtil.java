package base.test.dbsharding.client.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * 
 */
public class BeanPropertyAccessUtil {

    /**
     * 
     * @param propertyName
     *            ,name of the property
     * @param propertyValue
     *            ,new value of the property
     * @param object
     *            ,the object which contains the property
     */
    public static void setPropertyValue(final String propertyName, final Object propertyValue, final Object object) throws InvocationTargetException,
            IllegalAccessException {
        if (StringUtils.isBlank(propertyName) || object == null) {
            throw new IllegalArgumentException("'propertyName' cann't be null or empty,'object' cann't be null!");
        }
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), propertyName);
        if (descriptor != null && descriptor.getWriteMethod() != null) {
            descriptor.getWriteMethod().invoke(object, propertyValue);
        }
    }

    /**
     * 
     * @param propertyName
     *            ,name of the property
     * @param object
     *            ,the object which contains the property
     * @return the value object of the property
     */
    public static Object getPropertyValue(final String propertyName, final Object object) throws InvocationTargetException, IllegalAccessException {
        if (StringUtils.isBlank(propertyName) || object == null) {
            throw new IllegalArgumentException("'propertyName' cann't be null or empty,'object' cann't be null!");
        }
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), propertyName);
        if (descriptor != null && descriptor.getReadMethod() != null) {
            return descriptor.getReadMethod().invoke(object);
        }
        return null;
    }

}
