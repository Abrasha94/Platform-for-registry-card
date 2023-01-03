package com.modsen.cardissuer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

    @ExceptionHandler(value =
            {BalanceNotFoundException.class, CardNotFoundException.class, CompanyNotFoundException.class,
                    RoleNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ErrorMessage resourceNotFoundException(RuntimeException e, WebRequest request) {

        return new ErrorMessage(HttpStatus.NO_CONTENT.value(),
                new Date(), e.getMessage(), request.getDescription(false));
    }
}
