package com.ecommerce.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> methodArgumentNotValid(MethodArgumentNotValidException e){
        Map<String,String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName=((FieldError)error).getField();
            String fieldValue=((FieldError)error).getDefaultMessage();
            response.put(fieldName,fieldValue);
        });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> resourceNotFoundException(ResourceNotFoundException e){
        String message=e.getMessage();
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<String> myAPIException(APIException e){
        String message=e.getMessage();
        return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullPointerException(NullPointerException e){
        String message=e.getMessage();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
}
