package net.thumbtack.school.buscompany.exception;

// REVU ИМХО лучше checked. Но на Ваше усмотрение
public class BusCompanyException extends RuntimeException{
    private final ErrorCode errorCode;
    // REVU а не отправить ли field в ErrorCode ?
    // чтобы в коде не разбрасывать строки ?
    private final String field;

    public BusCompanyException(final ErrorCode errorCode, String field){
        // REVU зачем ? Есть же ErrorCode
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
