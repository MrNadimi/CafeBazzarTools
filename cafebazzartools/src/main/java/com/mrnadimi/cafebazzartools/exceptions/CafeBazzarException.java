package com.mrnadimi.cafebazzartools.exceptions;

public  class CafeBazzarException extends Exception{
    public CafeBazzarException() {
    }

    public CafeBazzarException(String message) {
        super(message);
    }

    public CafeBazzarException(String message, Throwable cause) {
        super(message, cause);
    }

    public CafeBazzarException(Throwable cause) {
        super(cause);
    }

}
