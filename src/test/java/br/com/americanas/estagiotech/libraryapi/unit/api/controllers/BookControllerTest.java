package br.com.americanas.estagiotech.libraryapi.unit.api.controllers;

import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.americanas.estagiotech.libraryapi.api.controllers.BookController;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.CreateBookRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.UpdateBookRequest;
import br.com.americanas.estagiotech.libraryapi.api.exceptions.BusinessException;
import br.com.americanas.estagiotech.libraryapi.models.Book;
import br.com.americanas.estagiotech.libraryapi.services.BookService;
import br.com.americanas.estagiotech.libraryapi.services.LoanService;

@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
public class BookControllerTest {

    private static String BOOK_API_URL = "/api/books";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(bookService, loanService);

    }

    @Test
    public void givenValidParams_whenCallsFindAll_thenReturnBooks() throws Exception {
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
        var expectedItems = 1;

        var bookResult = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);
        bookResult.setId(expectedId);

        var pageRequest = PageRequest.of(
                expectedPage, expectedPerPage,
                Sort.by(Direction.fromString("asc"), "title"));

        var page = new PageImpl<Book>(List.of(bookResult), pageRequest, expectedTotal);

        BDDMockito.given(bookService.findAll(Mockito.any(), Mockito.any())).willReturn(page);

        // when
        var request = MockMvcRequestBuilders.get(BOOK_API_URL)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", "title")
                .queryParam("dir", "asc")
                .queryParam("title", "code");

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("current_page").value(expectedPage))
                .andExpect(MockMvcResultMatchers.jsonPath("per_page").value(expectedPerPage))
                .andExpect(MockMvcResultMatchers.jsonPath("total").value(expectedTotal))
                .andExpect(MockMvcResultMatchers.jsonPath("items", Matchers.hasSize(expectedItems)))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].updated_at").isNotEmpty());

        Mockito.verify(bookService, Mockito.times(1))
                .findAll(bookCaptor.capture(), Mockito.any());

        var book = bookCaptor.getValue();

        Assertions.assertEquals("code", book.getTitle());
        Assertions.assertNull(book.getIsbn());
        Assertions.assertNull(book.getAuthor());
        Assertions.assertNull(book.getEdition());
        Assertions.assertNull(book.getPublisher());
    }

    @Test
    public void givenValidId_whenCallsGetById_thenReturnBook() throws Exception {
        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var bookFound = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);
        bookFound.setId(expectedId);

        BDDMockito.given(bookService.getById(expectedId)).willReturn(Optional.of(bookFound));

        // when
        var request = MockMvcRequestBuilders.get(BOOK_API_URL + "/1");

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("updated_at").isNotEmpty());
    }

    @Test
    public void givenValidBook_whenCallsCreateBook_thenReturnBook() throws Exception {
        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var createBookRequest = CreateBookRequest.builder()
                .title(expectedTitle)
                .isbn(expectedIsbn)
                .author(expectedAuthor)
                .edition(expectedEdition)
                .publisher(expectedPublisher).build();

        var savedBook = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);
        savedBook.setId(expectedId);

        BDDMockito.given(bookService.create(Mockito.any())).willReturn(savedBook);

        var json = mapper.writeValueAsString(createBookRequest);

        // when
        var request = MockMvcRequestBuilders.post(BOOK_API_URL)
                .contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("updated_at").isNotEmpty());

        Mockito.verify(bookService, Mockito.times(1)).create(bookCaptor.capture());

        var book = bookCaptor.getValue();

        Assertions.assertEquals(expectedTitle, book.getTitle());
        Assertions.assertEquals(expectedIsbn, book.getIsbn());
        Assertions.assertEquals(expectedAuthor, book.getAuthor());
        Assertions.assertEquals(expectedEdition, book.getEdition());
        Assertions.assertEquals(expectedPublisher, book.getPublisher());
    }

    @Test
    public void givenExistingBookIsbn_whenCallsCreateBook_thenReturnStatus400() throws Exception {
        // given
        var createBookRequest = CreateBookRequest.builder()
                .title("Clean Code")
                .isbn("9780132350884")
                .author("Robert C. Martin")
                .edition(1)
                .publisher("Pearson").build();

        BDDMockito.given(bookService.create(Mockito.any())).willThrow(new BusinessException("Isbn já foi cadastrado"));

        var json = mapper.writeValueAsString(createBookRequest);

        // when
        var request = MockMvcRequestBuilders.post(BOOK_API_URL)
                .contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Isbn já foi cadastrado"));
    }

    @Test
    public void givenValidBook_whenCallsUpdateBook_thenReturnBook() throws Exception {
        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var updateBookRequest = UpdateBookRequest.builder()
                .title(expectedTitle)
                .author(expectedAuthor)
                .edition(expectedEdition)
                .publisher(expectedPublisher).build();

        var bookFound = Book.create(
                "Clean",
                expectedIsbn,
                "Robert",
                2,
                "P");
        bookFound.setId(expectedId);

        var updatedBook = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);
        updatedBook.setId(expectedId);

        BDDMockito.given(bookService.getById(expectedId)).willReturn(Optional.of(bookFound));
        BDDMockito.given(bookService.update(Mockito.any())).willReturn(updatedBook);

        var json = mapper.writeValueAsString(updateBookRequest);

        // when
        var request = MockMvcRequestBuilders.put(BOOK_API_URL + "/" + expectedId)
                .contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("updated_at").isNotEmpty());

        Mockito.verify(bookService, Mockito.times(1)).update(bookCaptor.capture());

        var book = bookCaptor.getValue();

        Assertions.assertEquals(expectedTitle, book.getTitle());
        Assertions.assertEquals(expectedIsbn, book.getIsbn());
        Assertions.assertEquals(expectedAuthor, book.getAuthor());
        Assertions.assertEquals(expectedEdition, book.getEdition());
        Assertions.assertEquals(expectedPublisher, book.getPublisher());
    }

    @Test
    public void givenValidId_whenCallsDeleteBook_thenReturnStatus204() throws Exception {
        // given
        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var bookFound = Book.create(
                expectedTitle,
                expectedIsbn,
                expectedAuthor,
                expectedEdition,
                expectedPublisher);
        bookFound.setId(expectedId);

        BDDMockito.given(bookService.getById(expectedId)).willReturn(Optional.of(bookFound));

        // when
        var request = MockMvcRequestBuilders.delete(BOOK_API_URL + "/" + expectedId);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(bookService, Mockito.times(1)).delete(bookCaptor.capture());

        var book = bookCaptor.getValue();

        Assertions.assertEquals(expectedTitle, book.getTitle());
        Assertions.assertEquals(expectedIsbn, book.getIsbn());
        Assertions.assertEquals(expectedAuthor, book.getAuthor());
        Assertions.assertEquals(expectedEdition, book.getEdition());
        Assertions.assertEquals(expectedPublisher, book.getPublisher());
    }
}
