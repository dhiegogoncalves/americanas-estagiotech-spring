package br.com.americanas.estagiotech.libraryapi.unit.api.controllers;

import org.junit.jupiter.api.Assertions;
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

    @Test
    void givenAValidBook_whenCallsCreateBook_thenReturnBook() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

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
}
