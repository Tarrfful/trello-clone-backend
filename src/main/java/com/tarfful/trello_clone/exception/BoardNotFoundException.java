package com.tarfful.trello_clone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(String message){
        super(message);
    }
}
