package br.com.americanas.estagiotech.libraryapi.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.models.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Page<Loan> findAllByBook(Book book, PageRequest pageRequest);

    boolean existsByBookAndActiveTrue(Book book);

    List<Loan> findByLoanDateLessThanEqualAndActiveTrue(LocalDate daysAgo);
}
