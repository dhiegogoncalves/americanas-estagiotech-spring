package br.com.americanas.estagiotech.libraryapi.api.controllers;

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
import br.com.americanas.estagiotech.libraryapi.api.exceptions.NotFoundException;
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

    private final LoanService loanService;
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Find loans by params")
    public Pagination<LoanResponse> find(
            LoanListRequest loanListRequest,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "customer") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction) {
        var loan = loanListRequest.toModel();
        var pageRequest = PageRequest.of(page, perPage, Sort.by(Direction.fromString(direction), sort));
        var pageResult = loanService.findAll(loan, pageRequest);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(LoanResponse::toDto).toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a loan")
    public LoanResponse create(@RequestBody CreateLoanRequest createLoanRequest) {
        var book = bookService.getBookByIsbn(createLoanRequest.getBookIsbn())
                .orElseThrow(() -> new NotFoundException("Livro não foi encontrado"));
        var loanCreated = loanService.create(
                createLoanRequest.getCustomer(),
                createLoanRequest.getCustomerEmail(),
                book);

        return LoanResponse.toDto(loanCreated);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}/status/{active}")
    @Operation(summary = "Update loan status by id")
    public void returnBook(@PathVariable Long id, @PathVariable Boolean active) {
        var loan = loanService.getById(id).orElseThrow(() -> new NotFoundException("Empréstimo não foi encontrado"));
        loanService.updateStatus(loan, active);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a loan by id")
    public void delete(@PathVariable Long id) {
        var loan = loanService.getById(id).orElseThrow(() -> new NotFoundException("Empréstimo não foi encontrado"));
        loanService.delete(loan);
    }
}
