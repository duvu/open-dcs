package com.vd5.dcs.exception;

/**
 * @author beou on 7/17/18 15:07
 */
public class EventDataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6463793268166871416L;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public EventDataNotFoundException(String message) {
        super(message);
    }
}
