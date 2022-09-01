package br.com.americanas.estagiotech.libraryapi.api.dtos.loan;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateLoanRequest {

    @NotBlank(message = "'customer' é obrigatório")
    private String customer;

    @NotBlank(message = "'customer_email' é obrigatório")
    @JsonProperty("customer_email")
    private String customerEmail;

    @NotBlank(message = "'book_isbn' é obrigatório")
    @JsonProperty("book_isbn")
    private String bookIsbn;
}
