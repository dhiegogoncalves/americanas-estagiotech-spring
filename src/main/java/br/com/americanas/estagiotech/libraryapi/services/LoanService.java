package br.com.americanas.estagiotech.libraryapi.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.americanas.estagiotech.libraryapi.api.exceptions.BusinessException;
import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.models.Loan;
import br.com.americanas.estagiotech.libraryapi.repositories.LoanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanService {

    @Value("${application.loanDays}")
    private Integer loanDays;

    private final LoanRepository loanRepository;

    public Page<Loan> findAll(Loan loan, Pageable pageable) {
        var bookExample = Example.of(loan, ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return loanRepository.findAll(bookExample, pageable);
    }

    public Page<Loan> findAllByBook(Book book, PageRequest pageRequest) {
        return loanRepository.findAllByBook(book, pageRequest);
    }

    public Optional<Loan> getById(Long id) {
        return loanRepository.findById(id);
    }

    public Loan create(String customer, String customerEmail, Book book) {
        if (loanRepository.existsByBookAndActiveTrue(book)) {
            throw new BusinessException("Livro já foi emprestado");
        }

        var loan = Loan.create(customer, customerEmail, book);
        return loanRepository.save(loan);
    }

    public void updateStatus(Loan loan, Boolean active) {
        loan.updateStatus(active);
        loanRepository.save(loan);
    }

    public void delete(Loan loan) {
        if (loan.getActive()) {
            throw new BusinessException("Não é possivel deletar um empréstimo ativo");
        }
        loanRepository.delete(loan);
    }

    public List<Loan> getAllLateLoans() {
        var daysAgo = LocalDate.now().minusDays(loanDays);
        return loanRepository.findByLoanDateLessThanEqualAndActiveTrue(daysAgo);
    }

}
