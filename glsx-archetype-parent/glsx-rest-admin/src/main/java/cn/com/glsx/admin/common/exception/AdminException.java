package cn.com.glsx.admin.common.exception;

public class AdminException extends RuntimeException {

    public AdminException() {
    }

    public AdminException(String message) {
        super(message);
    }

    public AdminException(Throwable cause) {
        super(cause);
    }

    public AdminException(String message, Throwable cause) {
        super(message, cause);
    }

}
