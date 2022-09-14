package br.com.americanas.estagiotech.libraryapi.unit.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

import br.com.americanas.estagiotech.libraryapi.api.exceptions.BusinessException;
import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;
import br.com.americanas.estagiotech.libraryapi.repositories.LoanRepository;
import br.com.americanas.estagiotech.libraryapi.services.BookService;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(bookRepository);
    }

    @Test
    public void givenValidParams_whenCallsFindAll_thenReturnBooks() {
        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var expectedPage = 0;
        var expectedPerPage = 10;
        var expectedTotal = 1;

        var bookResult = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);
        bookResult.setId(expectedId);

        var bookFilter = Book.filter(
                expectedTitle,
                null,
                null,
                null,
                null);

        var bookExample = Example.of(bookFilter, ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        var pageRequest = PageRequest.of(
                expectedPage, expectedPerPage,
                Sort.by(Direction.fromString("asc"), "title"));

        var page = new PageImpl<Book>(List.of(bookResult), pageRequest, expectedTotal);

        Mockito.when(bookRepository.findAll(bookExample, pageRequest))
                .thenReturn(page);

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
    public void givenValidId_whenCallsGetById_thenReturnBook() {
        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var now = Instant.now();
        var bookResult = Book.builder()
                .id(expectedId)
                .title(expectedTitle)
                .isbn(expectedIsbn)
                .author(expectedAuthor)
                .edition(expectedEdition)
                .publisher(expectedPublisher)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Mockito.when(bookRepository.findById(expectedId))
                .thenReturn(Optional.of(bookResult));

        // when
        var book = bookService.getById(expectedId);

        // then
        Assertions.assertEquals(expectedId, book.get().getId());
        Assertions.assertEquals(expectedTitle, book.get().getTitle());
        Assertions.assertEquals(expectedIsbn, book.get().getIsbn());
        Assertions.assertEquals(expectedAuthor, book.get().getAuthor());
        Assertions.assertEquals(expectedEdition, book.get().getEdition());
        Assertions.assertEquals(expectedPublisher, book.get().getPublisher());
        Assertions.assertNotNull(book.get().getCreatedAt());
        Assertions.assertNotNull(book.get().getUpdatedAt());
    }

    @Test
    public void givenInvalidId_whenCallsGetById_thenReturnEmptyOptional() {
        // given
        var id = 1L;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // when
        var book = bookService.getById(id);

        // then
        Assertions.assertFalse(book.isPresent());
    }

    @Test
    public void givenValidBook_whenCallsCreateBook_thenReturnBookCreated() {
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

        var bookResult = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);
        bookResult.setId(expectedId);

        Mockito.when(bookRepository.existsByIsbn(expectedIsbn)).thenReturn(false);
        Mockito.when(bookRepository.save(book)).thenReturn(bookResult);

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
    public void givenExistingBookIsbn_whenCallsCreateBook_thenReturnBusinessException() {
        // given
        var expectedErrorMessage = "Isbn já foi cadastrado";

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");

        Mockito.when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);

        // when
        var exception = Assertions.assertThrows(BusinessException.class, () -> bookService.create(book));

        // then
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());

        Mockito.verify(bookRepository, Mockito.times(0)).save(book);
    }

    @Test
    public void givenValidBook_whenCallsUpdateBook_thenReturnBookUpdated() {
        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var book = Book.create(
                "C",
                expectedIsbn,
                "R",
                2,
                "P");
        book.setId(expectedId);

        var bookResult = Book.create(
                "C",
                expectedIsbn,
                "R",
                2,
                "P");
        bookResult.setId(expectedId);

        bookResult.update(expectedTitle, expectedAuthor, expectedEdition, expectedPublisher);

        Mockito.when(bookRepository.save(book)).thenReturn(bookResult);

        // then
        var bookUpdated = bookService.update(book);

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
        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");
        book.setId(1L);

        Mockito.when(loanRepository.existsByBookAndActiveTrue(book)).thenReturn(false);

        // when
        Assertions.assertDoesNotThrow(() -> bookService.delete(book));

        // then
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    public void givenValidBookWithActiveLoan_whenCallsDeleteBook_thenReturnBusinessException() {
        // given
        var expectedErrorMessage = "Não é possivel deletar um livro com empréstimo ativo";

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");
        book.setId(1L);

        Mockito.when(loanRepository.existsByBookAndActiveTrue(book)).thenReturn(true);

        // when
        var exception = Assertions.assertThrows(BusinessException.class, () -> bookService.delete(book));

        // then
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());

        Mockito.verify(bookRepository, Mockito.times(0)).delete(book);
    }
}
