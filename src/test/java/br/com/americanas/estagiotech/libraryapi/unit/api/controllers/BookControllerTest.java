package br.com.americanas.estagiotech.libraryapi.unit.api.controllers;

import java.util.Optional;

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
    public void givenValidBook_whenCallsCreateBook_thenReturnBookCreated() throws Exception {
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

        var request = MockMvcRequestBuilders.post(BOOK_API_URL)
                .contentType(MediaType.APPLICATION_JSON).content(json);

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

        var createBookRequest = CreateBookRequest.builder()
                .title("Clean Code")
                .isbn("9780132350884")
                .author("Robert C. Martin")
                .edition(1)
                .publisher("Pearson").build();

        BDDMockito.given(bookService.create(Mockito.any())).willThrow(new BusinessException("Isbn já foi cadastrado"));

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL)
                .contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Isbn já foi cadastrado"));
    }

    @Test
    public void givenValidBook_whenCallsUpdateBook_thenReturnBookUpdated() throws Exception {
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

        var request = MockMvcRequestBuilders.put(BOOK_API_URL + "/" + expectedId)
                .contentType(MediaType.APPLICATION_JSON).content(json);

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
}
