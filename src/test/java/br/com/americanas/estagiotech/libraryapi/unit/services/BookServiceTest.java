package br.com.americanas.estagiotech.libraryapi.unit.services;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(bookRepository);
    }

    @Test
    public void givenAValidBookId_whenCallsGetById_thenReturnBook() {
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

        var book = bookService.getById(expectedId);

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
    public void givenAnInvalidBookId_whenCallsGetById_thenReturnEmptyOptional() {
        var id = 1L;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        var book = bookService.getById(id);

        Assertions.assertFalse(book.isPresent());
    }

    @Test
    public void givenAValidBook_whenCallsCreateBook_thenReturnBook() {
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

        var bookCreated = bookService.create(book);

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
    public void givenAnExistingBookIsbn_whenCallsCreateBook_thenReturnBusinessException() {
        var expectedErrorMessage = "Isbn já foi cadastrado";

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");

        Mockito.when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);

        var exception = Assertions.assertThrows(BusinessException.class, () -> bookService.create(book));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());

        Mockito.verify(bookRepository, Mockito.times(0)).save(book);
    }

    @Test
    public void givenAValidBook_whenCallsUpdateBook_thenReturnBook() {
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
    public void givenAValidBook_whenCallsDeleteBook_thenDontReturnException() {
        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");
        book.setId(1L);

        Mockito.when(loanRepository.existsByBookAndActiveTrue(book)).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> bookService.delete(book));

        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    public void givenAValidBookWithActiveLoan_whenCallsDeleteBook_thenReturnException() {
        var expectedErrorMessage = "Não é possivel deletar um livro com empréstimo ativo";

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");
        book.setId(1L);

        Mockito.when(loanRepository.existsByBookAndActiveTrue(book)).thenReturn(true);

        var exception = Assertions.assertThrows(BusinessException.class, () -> bookService.delete(book));

        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());

        Mockito.verify(bookRepository, Mockito.times(0)).delete(book);
    }
}
