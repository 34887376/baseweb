package base.test.dbsharding.client.exception;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 *  未知的JDBC数据访问层异常
 *
 */
public class UnknownDataAccessException extends UncategorizedDataAccessException {

	private static final long serialVersionUID = 1853822440588137975L;

	/**
     * Constructor for UncategorizedDataAccessException.
     *
     * @param msg   the detail message
     * @param cause the exception thrown by underlying data access API
     */
    public UnknownDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
