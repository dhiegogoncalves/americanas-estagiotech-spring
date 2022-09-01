package br.com.americanas.estagiotech.libraryapi.api.dtos.loan;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.americanas.estagiotech.libraryapi.api.dtos.book.BookResponse;
import br.com.americanas.estagiotech.libraryapi.models.Loan;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanResponse {

    private Long id;

    private String customer;

    @JsonProperty("customer_email")
    private String customerEmail;

    private BookResponse book;

    @JsonProperty("loan_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate loanDate;

    private Boolean active;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss.SSSZ")
    private Instant createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss.SSSZ")
    private Instant updatedAt;

    public static LoanResponse toDto(Loan loan) {
        var book = BookResponse.toDto(loan.getBook());

        return LoanResponse.builder()
                .id(loan.getId())
                .customer(loan.getCustomer())
                .customerEmail(loan.getCustomerEmail())
                .book(book)
                .loanDate(loan.getLoanDate())
                .active(loan.getActive())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .build();

    }
}
