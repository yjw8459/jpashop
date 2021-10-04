package jpabook.jpashop.exception;

import java.util.logging.Logger;

public class TestException extends RuntimeException{
    public TestException() {
    }

    public TestException(String message) {
        super(message);
    }

    public TestException(String message, Throwable cause) {

        super(message, cause);
    }

    public TestException(Throwable cause) {
        super(cause);
    }
}
