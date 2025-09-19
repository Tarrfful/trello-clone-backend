package com.tarfful.trello_clone.exception;

import com.tarfful.trello_clone.dto.TaskResponse;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException(String message){
        super(message);
    }
}
