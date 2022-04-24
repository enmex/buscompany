package net.thumbtack.school.buscompany.exception;

public class BusCompanyException extends Exception{
    private final ErrorCode errorCode;

    public BusCompanyException(final ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
