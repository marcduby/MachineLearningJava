package com.doobs.mlbeta.util;

/**
 * generic excep[tion class for this project; will make exception handlign easier
 *
 */
public class MlException extends Exception {

    /**
     * default constructor
     *
     */
    public MlException() {
        super();
    }

    /**
     * construcor with text message
     *
     * @param message
     */
    public MlException(String message) {
        super(message);
    }
}
