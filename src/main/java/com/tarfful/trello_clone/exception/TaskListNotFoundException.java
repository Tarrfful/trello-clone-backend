package com.tarfful.trello_clone.exception;

import com.tarfful.trello_clone.dto.TaskListResponse;

public class TaskListNotFoundException extends RuntimeException{
    public TaskListNotFoundException(String message){
        super(message);
    }
}
