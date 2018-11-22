package com.plivo.contactbook;

/**
 * Created by shondad on 21/11/18.
 */
public class ApiException extends Exception {

    ApiException(String exceptionString)
    {
        super(exceptionString);
    }
}
