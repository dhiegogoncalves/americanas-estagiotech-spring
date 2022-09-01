package br.com.americanas.estagiotech.libraryapi.api.dtos.loan;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.americanas.estagiotech.libraryapi.models.Loan;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanListRequest {
    private String customer;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("book_isbn")
    private String bookIsbn;

    public Loan toModel() {
        return Loan.filter(customer, customerEmail, bookIsbn);
    }

}
