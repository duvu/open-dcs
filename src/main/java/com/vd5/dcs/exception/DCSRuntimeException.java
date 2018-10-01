package com.vd5.dcs.exception;

/**
 * @author beou on 8/27/17 04:30
 * @version 1.0
 */
public class DCSRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -5443247147486639343L;

    public DCSRuntimeException(Exception ex) {
        super(ex);
    }

    public DCSRuntimeException(ClassNotFoundException cnfe) {
        super(cnfe);
    }
}
