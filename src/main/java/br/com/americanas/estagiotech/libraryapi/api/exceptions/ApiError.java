package br.com.americanas.estagiotech.libraryapi.api.exceptions;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@Getter
public class ApiError {

    private HttpStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss.SSSZ")
    private Instant date = Instant.now();

    private String message;

    private List<String> errors;

    public ApiError(HttpStatus status, List<String> errors) {
        super();
        this.status = status;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public ApiError(HttpStatus status) {
        super();
        this.status = status;
    }
}
