package br.com.americanas.estagiotech.libraryapi.e2e.api.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.americanas.estagiotech.libraryapi.api.dtos.book.BookResponse;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.CreateBookRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.UpdateBookRequest;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;

@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
public class BookControllerE2ETest {

    private static String BOOK_API_URL = "/api/books";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void givenAValidBook_whenCallsCreateBook_thenReturnBook() throws Exception {
        Assertions.assertEquals(0, bookRepository.count());

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
    }

    @Test
    void givenAValidBook_whenCallsUpdateBook_thenReturnBook() throws Exception {
        Assertions.assertEquals(0, bookRepository.count());

        givenABook("C", "9780132350884", "R", 2, "P");

        var expectedId = 1L;
        var expectedTitle = "Clean Code";
        var expectedIsbn = "9780132350884";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var createBookRequest = UpdateBookRequest.builder()
                .title(expectedTitle)
                .author(expectedAuthor)
                .edition(expectedEdition)
                .publisher(expectedPublisher).build();

        String json = mapper.writeValueAsString(createBookRequest);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API_URL + "/" + expectedId)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

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

    private BookResponse givenABook(String title, String isbn, String author, Integer edition, String publisher)
            throws Exception {
        var createBookRequest = CreateBookRequest.builder()
                .title(title)
                .isbn(isbn)
                .author(author)
                .edition(edition)
                .publisher(publisher).build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(json);

        final var result = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        return mapper.readValue(result, BookResponse.class);
    }
}
