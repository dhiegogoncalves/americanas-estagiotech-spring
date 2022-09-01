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

import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;
import br.com.americanas.estagiotech.libraryapi.services.BookService;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

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

        Mockito.when(bookRepository.findById(Mockito.eq(expectedId)))
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

        var bookResult = book.clone();
        bookResult.setId(1L);

        Mockito.when(bookRepository.existsByIsbn(Mockito.eq(expectedIsbn))).thenReturn(false);
        Mockito.when(bookRepository.save(Mockito.eq(book))).thenReturn(bookResult);

        var bookCreated = bookService.create(book);

        Assertions.assertEquals(expectedId, bookCreated.getId());
        Assertions.assertEquals(expectedTitle, bookCreated.getTitle());
        Assertions.assertEquals(expectedIsbn, bookCreated.getIsbn());
        Assertions.assertEquals(expectedAuthor, bookCreated.getAuthor());
        Assertions.assertEquals(expectedEdition, bookCreated.getEdition());
        Assertions.assertEquals(expectedPublisher, bookCreated.getPublisher());
        Assertions.assertEquals(bookResult.getCreatedAt(), bookCreated.getCreatedAt());
        Assertions.assertEquals(bookResult.getUpdatedAt(), bookCreated.getUpdatedAt());
    }

}
