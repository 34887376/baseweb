package base.test.dbsharding.client.exception;

import org.springframework.dao.DataAccessException;

/**
 * 路由过程中产生的异常
 *
 */
public class RouteException extends DataAccessException {

	private static final long serialVersionUID = -7201101900545005878L;

	public RouteException(String message) {
        super(message);
    }

    public RouteException(String message, Throwable cause) {
        super(message, cause);
    }
}
