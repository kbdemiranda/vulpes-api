package io.github.vulpes.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class VulpesException extends RuntimeException {

    public static final Integer OK = 200;
    public static final Integer CREATED = 201;
    public static final Integer NO_CONTENT = 204;
    public static final Integer BAD_REQUEST = 400;
    public static final Integer UNAUTHORIZED = 401;
    public static final Integer FORBIDDEN = 403;
    public static final Integer NOT_FOUND = 404;
    public static final Integer METHOD_NOT_ALLOWED = 405;
    public static final Integer CONFLICT = 409;
    public static final Integer UNPROCESSABLE_ENTITY = 422;
    public static final Integer INTERNAL_SERVER_ERROR = 500;
    public static final Integer NOT_IMPLEMENTED = 501;
    public static final Integer BAD_GATEWAY = 502;
    public static final Integer SERVICE_UNAVAILABLE = 503;
    public static final Integer GATEWAY_TIMEOUT = 504;


    private final Integer code;

    public VulpesException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public VulpesException(Integer code, String message, Object... formartParams) {
        super(String.format(message, formartParams));
        this.code = code;
    }

    public VulpesException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public VulpesException(Integer code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public static VulpesException notFound(String message, Object... formartParams) {
        return new VulpesException(NOT_FOUND, "Not found: " + message, formartParams);
    }

}
