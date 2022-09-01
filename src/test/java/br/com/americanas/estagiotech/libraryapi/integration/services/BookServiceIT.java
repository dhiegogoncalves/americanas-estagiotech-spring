package br.com.americanas.estagiotech.libraryapi.integration.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;
import br.com.americanas.estagiotech.libraryapi.services.BookService;

@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class BookServiceIT {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Test
    public void givenAValidBookId_whenCallsGetById_thenReturnBook() {
        Assertions.assertEquals(0, bookRepository.count());

        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        givenABook(expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);

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
    public void givenAValidBook_whenCallsCreateBook_thenReturnBook() {
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

    private Book givenABook(String expectedTitle, String expectedIsbn, String expectedAuthor, int expectedEdition,
            String expectedPublisher) {
        var book = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);

        return bookService.create(book);
    }

}
