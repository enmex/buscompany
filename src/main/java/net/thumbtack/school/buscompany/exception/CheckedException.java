package net.thumbtack.school.buscompany.exception;

public class CheckedException extends RuntimeException{
    private final ErrorCode errorCode;

    public CheckedException(final ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }

    public String getMessage(){
        return errorCode.getMessage();
    }

    public String getField() {
        return errorCode.getField();
    }
}
