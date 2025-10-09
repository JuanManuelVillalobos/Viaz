package com.viaz.dashboard.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class TokenRevokedException extends RuntimeException {
    public TokenRevokedException(String message) {
        super(message);
    }
}
