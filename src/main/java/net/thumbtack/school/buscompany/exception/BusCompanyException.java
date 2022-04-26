package net.thumbtack.school.buscompany.exception;

public class BusCompanyException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String field;

    public BusCompanyException(final ErrorCode errorCode, String field){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.field = field;
    }

    public BusCompanyException(final ErrorCode errorCode){
        this(errorCode, "");
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }

    public String getMessage(){
        return errorCode.getMessage();
    }

    public String getField() {
        return field;
    }
}
