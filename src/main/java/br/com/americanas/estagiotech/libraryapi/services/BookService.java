package br.com.americanas.estagiotech.libraryapi.services;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.americanas.estagiotech.libraryapi.api.exceptions.BusinessException;
import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;
import br.com.americanas.estagiotech.libraryapi.repositories.LoanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public Page<Book> findAll(Book book, Pageable pageable) {
        var bookExample = Example.of(book, ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return bookRepository.findAll(bookExample, pageable);
    }

    public Optional<Book> getById(Long id) {
        return bookRepository.findById(id);
    }

    public Book create(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já foi cadastrado");
        }

        return bookRepository.save(book);
    }

    public Book update(Book book) {
        return bookRepository.save(book);
    }

    public void delete(Book book) {
        if (loanRepository.existsByBookAndActiveTrue(book)) {
            throw new BusinessException("Não é possivel deletar um livro com empréstimo ativo");
        }

        bookRepository.delete(book);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

}
