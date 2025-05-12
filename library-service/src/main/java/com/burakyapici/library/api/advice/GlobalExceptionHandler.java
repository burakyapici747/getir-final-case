package com.burakyapici.library.api.advice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException ex) {
        ProblemDetail pd = createProblemDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            "ENTITY_NOT_FOUND",
            "Entity Not Found"
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(pd);
    }

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ProblemDetail> handleDataConflictException(DataConflictException ex) {
        ProblemDetail pd = createProblemDetail(
            HttpStatus.CONFLICT,
            ex.getMessage(),
            "DATA_CONFLICT",
            "Data Conflict"
        );
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(pd);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenAccessException(ForbiddenAccessException ex) {
        ProblemDetail pd = createProblemDetail(
            HttpStatus.FORBIDDEN,
            ex.getMessage(),
            "FORBIDDEN_ACCESS",
            "Forbidden Access"
        );
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(pd);
    }

    @ExceptionHandler(UnauthorizedResourceAccessException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedResourceAccessException(UnauthorizedResourceAccessException ex) {
        ProblemDetail pd = createProblemDetail(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage(),
            "UNAUTHORIZED_ACCESS",
            "Unauthorized Access"
        );
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(pd);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ProblemDetail> handleUnprocessableEntityException(UnprocessableEntityException ex) {
        ProblemDetail pd = createProblemDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.getMessage(),
            "UNPROCESSABLE_ENTITY",
            "Unprocessable Entity"
        );
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex) {
        List<Map<String, String>> invalidParams = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String field = violation.getPropertyPath().toString();
                    String reason = violation.getMessage();
                    return Map.of(
                        "name", field,
                        "reason", reason
                    );
                })
                .collect(Collectors.toList());

        ProblemDetail pd = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "The request contains invalid parameters",
                "validation-error",
                "Validation Failed"
        );
        pd.setProperty("invalid-params", invalidParams);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> invalidParams = ex.getBindingResult()
            .getFieldErrors().stream()
            .map(err -> Map.of(
                "name", err.getField(),
                "reason", Objects.requireNonNull(err.getDefaultMessage())
            ))
            .collect(Collectors.toList());

        ProblemDetail pd = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "The request contains invalid parameters",
            "validation-error",
            "Validation Failed"
        );
        pd.setProperty("invalidParams", invalidParams);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(pd);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ProblemDetail> handleAllUncaught(Exception ex) {
//        ProblemDetail pd = createProblemDetail(
//            HttpStatus.INTERNAL_SERVER_ERROR,
//            "An unexpected error occurred",
//            "internal-server-error",
//            "Internal Server Error"
//        );
//        return ResponseEntity
//            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
//            .body(pd);
//    }

    private ProblemDetail createProblemDetail(
        HttpStatus status,
        String detail,
        String errorCode,
        String title
    ) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setInstance(ServletUriComponentsBuilder
            .fromCurrentRequest()
            .build()
            .toUri()
        );
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("errorCode", errorCode);
        return pd;
    }
}
