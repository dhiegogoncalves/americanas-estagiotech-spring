package br.com.americanas.estagiotech.libraryapi.integration.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.americanas.estagiotech.libraryapi.api.exceptions.BusinessException;
import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.models.Loan;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;
import br.com.americanas.estagiotech.libraryapi.repositories.LoanRepository;
import br.com.americanas.estagiotech.libraryapi.services.BookService;
import br.com.americanas.estagiotech.libraryapi.services.LoanService;

@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class BookServiceIT {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @Test
    public void givenValidBook_whenCallsFindAll_thenReturnBooks() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var expectedPage = 0;
        var expectedPerPage = 10;
        var expectedTotal = 1;

        var book = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);

        bookService.create(book);

        Assertions.assertEquals(1, bookRepository.count());

        var bookFilter = Book.filter("Code", null, null, null, null);

        var pageRequest = PageRequest.of(
                expectedPage, expectedPerPage,
                Sort.by(Direction.fromString("asc"), "title"));

        // when
        var pageResult = bookService.findAll(bookFilter, pageRequest);

        // then
        Assertions.assertEquals(expectedPage, pageResult.getNumber());
        Assertions.assertEquals(expectedPerPage, pageResult.getSize());
        Assertions.assertEquals(expectedTotal, pageResult.getTotalElements());
        Assertions.assertEquals(expectedId, pageResult.getContent().get(0).getId());
        Assertions.assertEquals(expectedTitle, pageResult.getContent().get(0).getTitle());
        Assertions.assertEquals(expectedIsbn, pageResult.getContent().get(0).getIsbn());
        Assertions.assertEquals(expectedAuthor, pageResult.getContent().get(0).getAuthor());
        Assertions.assertEquals(expectedEdition, pageResult.getContent().get(0).getEdition());
        Assertions.assertEquals(expectedPublisher, pageResult.getContent().get(0).getPublisher());
        Assertions.assertNotNull(pageResult.getContent().get(0).getCreatedAt());
        Assertions.assertNotNull(pageResult.getContent().get(0).getUpdatedAt());
    }

    @Test
    public void givenValidBookId_whenCallsGetById_thenReturnBook() {
        Assertions.assertEquals(0, bookRepository.count());

        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var book = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);

        bookService.create(book);

        // when
        var bookFound = bookService.getById(expectedId);

        // then
        Assertions.assertEquals(expectedId, bookFound.get().getId());
        Assertions.assertEquals(expectedTitle, bookFound.get().getTitle());
        Assertions.assertEquals(expectedIsbn, bookFound.get().getIsbn());
        Assertions.assertEquals(expectedAuthor, bookFound.get().getAuthor());
        Assertions.assertEquals(expectedEdition, bookFound.get().getEdition());
        Assertions.assertEquals(expectedPublisher, bookFound.get().getPublisher());
        Assertions.assertNotNull(bookFound.get().getCreatedAt());
        Assertions.assertNotNull(bookFound.get().getUpdatedAt());
    }

    @Test
    public void givenInvalidId_whenCallsGetById_thenReturnEmptyOptional() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        // when
        var bookFound = bookService.getById(1L);

        // then
        Assertions.assertTrue(bookFound.isEmpty());
    }

    @Test
    public void givenValidBook_whenCallsCreateBook_thenReturnBook() {
        Assertions.assertEquals(0, bookRepository.count());

        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var book = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);

        // when
        var bookCreated = bookService.create(book);

        // then
        Assertions.assertEquals(expectedId, bookCreated.getId());
        Assertions.assertEquals(expectedTitle, bookCreated.getTitle());
        Assertions.assertEquals(expectedIsbn, bookCreated.getIsbn());
        Assertions.assertEquals(expectedAuthor, bookCreated.getAuthor());
        Assertions.assertEquals(expectedEdition, bookCreated.getEdition());
        Assertions.assertEquals(expectedPublisher, bookCreated.getPublisher());
        Assertions.assertNotNull(bookCreated.getCreatedAt());
        Assertions.assertNotNull(bookCreated.getUpdatedAt());
    }

    @Test
    public void givenValidBook_whenCallsCreateBook_thenReturnBusinessException() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var expectedErrorMessage = "Isbn já foi cadastrado";

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");

        bookService.create(book);

        Assertions.assertEquals(1, bookRepository.count());

        // when
        var exception = Assertions.assertThrows(
                BusinessException.class,
                () -> bookService.create(book));

        // then
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void givenValidBook_whenCallsUpdateBook_thenReturnBook() {
        Assertions.assertEquals(0, bookRepository.count());

        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var book = Book.create(
                "Clean",
                expectedIsbn,
                "Robert",
                2,
                "P");

        bookService.create(book);

        Assertions.assertEquals(1, bookRepository.count());

        var bookFound = bookService.getById(expectedId).get();

        bookFound.update(expectedTitle, expectedAuthor, expectedEdition, expectedPublisher);

        // when
        var bookUpdated = bookService.update(bookFound);

        // then
        Assertions.assertEquals(expectedId, bookUpdated.getId());
        Assertions.assertEquals(expectedTitle, bookUpdated.getTitle());
        Assertions.assertEquals(expectedIsbn, bookUpdated.getIsbn());
        Assertions.assertEquals(expectedAuthor, bookUpdated.getAuthor());
        Assertions.assertEquals(expectedEdition, bookUpdated.getEdition());
        Assertions.assertEquals(expectedPublisher, bookUpdated.getPublisher());
        Assertions.assertNotNull(bookUpdated.getCreatedAt());
        Assertions.assertNotNull(bookUpdated.getUpdatedAt());
    }

    @Test
    public void givenValidBook_whenCallsDeleteBook_thenDontReturnException() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");

        bookService.create(book);

        Assertions.assertEquals(1, bookRepository.count());

        var bookFound = bookService.getById(1L).get();

        // when
        Assertions.assertDoesNotThrow(() -> bookService.delete(bookFound));

        // then
        Assertions.assertEquals(0, bookRepository.count());
    }

    @Test
    public void givenValidBookWithActiveLoan_whenCallsDeleteBook_thenReturnBusinessException() {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

        var expectedErrorMessage = "Não é possivel deletar um livro com empréstimo ativo";

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");

        var bookCreated = bookService.create(book);

        var loan = Loan.create(
                "João Silva",
                "joao.silva@email.com",
                bookCreated);

        loanService.create(loan);

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        var bookFound = bookService.getById(1L).get();

        // when
        var exception = Assertions.assertThrows(
                BusinessException.class,
                () -> bookService.delete(bookFound));

        // then
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());
    }

}
