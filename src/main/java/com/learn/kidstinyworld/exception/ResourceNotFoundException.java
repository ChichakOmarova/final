package com.learn.kidstinyworld.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// HTTP Status: 404 NOT FOUND qaytarır
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s tapılmadı: %s = '%s'", resourceName, fieldName, fieldValue));
    }
}