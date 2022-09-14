package br.com.americanas.estagiotech.libraryapi.integration.repositories;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;

@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryIT {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void should_find_all_books() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var expectedPage = 0;
        var expectedSize = 10;
        var expectedTotal = 2;

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");

        var book2 = Book.create(
                "The Clean Coder",
                "9780137081073",
                "Robert C. Martin",
                1,
                "Pearson");

        bookRepository.saveAll(List.of(book, book2));

        Assertions.assertEquals(2, bookRepository.count());

        var filter = Book.filter(
                null,
                null,
                "rob",
                null,
                null);

        var pageRequest = PageRequest.of(
                expectedPage,
                expectedSize,
                Sort.by(Direction.fromString("ASC"), "author"));

        var bookExample = Example.of(filter, ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        // when
        var pageResult = bookRepository.findAll(bookExample, pageRequest);

        // then
        Assertions.assertEquals(expectedPage, pageResult.getNumber());
        Assertions.assertEquals(expectedSize, pageResult.getSize());
        Assertions.assertEquals(expectedTotal, pageResult.getTotalElements());
    }

    @Test
    public void should_find_book() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");
        var bookCreated = bookRepository.save(book);

        Assertions.assertEquals(1, bookRepository.count());

        // when
        var bookFound = bookRepository.findById(bookCreated.getId());

        // then
        Assertions.assertTrue(bookFound.isPresent());

        Assertions.assertEquals(bookCreated.getId(), bookCreated.getId());
        Assertions.assertEquals(bookCreated.getTitle(), bookFound.get().getTitle());
        Assertions.assertEquals(bookCreated.getIsbn(), bookFound.get().getIsbn());
        Assertions.assertEquals(bookCreated.getAuthor(), bookFound.get().getAuthor());
        Assertions.assertEquals(bookCreated.getEdition(), bookFound.get().getEdition());
        Assertions.assertEquals(bookCreated.getPublisher(), bookFound.get().getPublisher());
        Assertions.assertEquals(bookCreated.getCreatedAt(), bookFound.get().getCreatedAt());
        Assertions.assertEquals(bookCreated.getUpdatedAt(), bookFound.get().getUpdatedAt());
    }

    @Test
    public void should_verify_exists_book_by_isbn() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");
        var bookCreated = bookRepository.save(book);

        Assertions.assertEquals(1, bookRepository.count());

        // when
        var result = bookRepository.existsByIsbn(bookCreated.getIsbn());

        // then
        Assertions.assertTrue(result);
    }

    @Test
    public void should_create_book() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");

        // when
        var bookCreated = bookRepository.save(book);

        // then
        Assertions.assertEquals(1, bookRepository.count());

        Assertions.assertNotNull(bookCreated.getId());
        Assertions.assertEquals(book.getTitle(), bookCreated.getTitle());
        Assertions.assertEquals(book.getIsbn(), bookCreated.getIsbn());
        Assertions.assertEquals(book.getAuthor(), bookCreated.getAuthor());
        Assertions.assertEquals(book.getEdition(), bookCreated.getEdition());
        Assertions.assertEquals(book.getPublisher(), bookCreated.getPublisher());
        Assertions.assertNotNull(bookCreated.getCreatedAt());
        Assertions.assertNotNull(bookCreated.getUpdatedAt());
    }

    @Test
    public void should_update_book() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var book = Book.create(
                "Test",
                "9780132350884",
                "Test",
                2,
                "Test");
        var bookCreated = bookRepository.save(book);

        var bookCreatedUpdatedAt = bookCreated.getUpdatedAt();

        bookCreated.update(
                "Clean Code",
                "Robert C. Martin",
                1, "Pearson");

        // when
        var bookUpdated = bookRepository.save(bookCreated);

        // then
        Assertions.assertEquals(1, bookRepository.count());

        Assertions.assertEquals(bookCreated.getId(), bookUpdated.getId());
        Assertions.assertEquals(bookCreated.getTitle(), bookUpdated.getTitle());
        Assertions.assertEquals(bookCreated.getIsbn(), bookUpdated.getIsbn());
        Assertions.assertEquals(bookCreated.getAuthor(), bookUpdated.getAuthor());
        Assertions.assertEquals(bookCreated.getEdition(), bookUpdated.getEdition());
        Assertions.assertEquals(bookCreated.getPublisher(), bookUpdated.getPublisher());
        Assertions.assertEquals(bookCreated.getCreatedAt(), bookUpdated.getCreatedAt());
        Assertions.assertTrue(bookCreatedUpdatedAt.isBefore(bookUpdated.getUpdatedAt()));
    }

    @Test
    public void should_delete_book() {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var book = Book.create(
                "Clean Code",
                "9780132350884",
                "Robert C. Martin",
                1,
                "Pearson");
        var bookCreated = bookRepository.save(book);

        Assertions.assertEquals(1, bookRepository.count());

        // when
        bookRepository.delete(bookCreated);

        // then
        Assertions.assertEquals(0, bookRepository.count());
    }
}
