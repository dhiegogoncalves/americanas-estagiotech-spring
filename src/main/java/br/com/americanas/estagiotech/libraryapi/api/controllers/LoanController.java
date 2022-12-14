package br.com.americanas.estagiotech.libraryapi.api.controllers;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.americanas.estagiotech.libraryapi.api.dtos.Pagination;
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.CreateLoanRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.LoanListRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.LoanResponse;
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.UpdateLoanRequest;
import br.com.americanas.estagiotech.libraryapi.api.exceptions.NotFoundException;
import br.com.americanas.estagiotech.libraryapi.models.Loan;
import br.com.americanas.estagiotech.libraryapi.services.BookService;
import br.com.americanas.estagiotech.libraryapi.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/api/loans")
@RestController
@RequiredArgsConstructor
@Tag(name = "Loans")
public class LoanController {

    private static final String LOAN_MESSAGE_NOT_FOUND = "Empréstimo não foi encontrado";
    private final LoanService loanService;
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Find loans by params")
    public Pagination<?> findAll(
            LoanListRequest loanListRequest,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "customer") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction) {
        var loan = loanListRequest.toModel();
        var pageRequest = PageRequest.of(page, perPage, Sort.by(Direction.fromString(direction), sort));
        var pageResult = loanService.findAll(loan, pageRequest);

        return Pagination.toDto(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(LoanResponse::toDto).toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Get loan details by id")
    public LoanResponse getById(@PathVariable Long id) {
        var loan = loanService.getById(id).orElseThrow(() -> new NotFoundException(LOAN_MESSAGE_NOT_FOUND));
        return LoanResponse.toDto(loan);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a loan")
    public LoanResponse create(@RequestBody @Valid CreateLoanRequest createLoanRequest) {
        var book = bookService.getBookByIsbn(createLoanRequest.getBookIsbn())
                .orElseThrow(() -> new NotFoundException("Livro não foi encontrado"));
        var loan = Loan.create(
                createLoanRequest.getCustomer(),
                createLoanRequest.getCustomerEmail(),
                book);
        var loanCreated = loanService.create(loan);

        return LoanResponse.toDto(loanCreated);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a loan")
    public LoanResponse update(@PathVariable Long id, @RequestBody @Valid UpdateLoanRequest updateLoanRequest) {
        var loan = loanService.getById(id).orElseThrow(() -> new NotFoundException(LOAN_MESSAGE_NOT_FOUND));
        loan.update(updateLoanRequest.getCustomer(), updateLoanRequest.getCustomerEmail());
        var loanUpdated = loanService.update(loan);

        return LoanResponse.toDto(loanUpdated);
    }

    @PostMapping("{id}/finalize")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "finalize loan by id")
    public void finalizeLoan(@PathVariable Long id) {
        var loan = loanService.getById(id).orElseThrow(() -> new NotFoundException(LOAN_MESSAGE_NOT_FOUND));
        loan.returnBook();
        loanService.update(loan);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a loan by id")
    public void delete(@PathVariable Long id) {
        var loan = loanService.getById(id).orElseThrow(() -> new NotFoundException(LOAN_MESSAGE_NOT_FOUND));
        loanService.delete(loan);
    }
}
