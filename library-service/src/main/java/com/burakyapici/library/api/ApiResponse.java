package com.burakyapici.library.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final int statusCode;
    private final String statusName;
    private final Instant timestamp;
    private final T data;

    @Builder
    private ApiResponse(boolean success, String message, int statusCode, String statusName, T data) {
        this.success = success;
        this.message = message;
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.timestamp = Instant.now();
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Process completed successfully");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .statusCode(HttpStatus.OK.value())
            .statusName(HttpStatus.OK.name())
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .statusCode(status.value())
            .statusName(status.name())
            .data(data)
            .build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> okResponse(T data) {
        return ResponseEntity.ok(success(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> okResponse(T data, String message) {
        return ResponseEntity.ok(success(data, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> createdResponse(T data, String message, UUID id) {
        ApiResponse<T> response = success(data, message, HttpStatus.CREATED);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();

        return ResponseEntity
            .created(location)
            .body(response);
    }

    public static <T, ID> ResponseEntity<ApiResponse<T>> createdResponse(T data, String message, Function<T, ID> idExtractor) {
        ApiResponse<T> response = success(data, message, HttpStatus.CREATED);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(idExtractor.apply(data))
            .toUri();

        return ResponseEntity
            .created(location)
            .body(response);
    }

    public static ResponseEntity<ApiResponse<Void>> noContentResponse(String message) {
        return ResponseEntity.ok(success(null, message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> customResponse(T data, String message, HttpStatus status) {
        return ResponseEntity
            .status(status)
            .body(success(data, message, status));
    }
}