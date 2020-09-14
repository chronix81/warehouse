package pl.mb.warehouse.adapters.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.mb.warehouse.domain.DomainException;
import pl.mb.warehouse.domain.IllegalArgumentDomainException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorProducer {


    @ExceptionHandler(IllegalArgumentDomainException.class)
    public ResponseEntity<ErrorDto> badRequest(IllegalArgumentDomainException e, HttpServletRequest req) {
        String message = e.getLocalizedMessage();
        return new ResponseEntity<>(new ErrorDto(HttpStatus.BAD_REQUEST.toString(), message),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DomainException.class)
    protected ResponseEntity<ErrorDto> internalServerError(DomainException ex, HttpServletRequest req) {
        String message = ex.getLocalizedMessage();
        return new ResponseEntity<>(new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(), message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}