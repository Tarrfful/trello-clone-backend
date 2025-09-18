package com.tarfful.trello_clone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message){
        super(message);
    }
}
