package br.com.americanas.estagiotech.libraryapi.e2e.api.controllers;

import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.americanas.estagiotech.libraryapi.api.dtos.book.CreateBookRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.UpdateBookRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.CreateLoanRequest;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;
import br.com.americanas.estagiotech.libraryapi.repositories.LoanRepository;

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

    @Autowired
    private LoanRepository loanRepository;

    @Test
    public void givenValidParams_whenCallsFindAll_thenReturnBooks() throws Exception {
        // given
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

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        createBookRequest = CreateBookRequest.builder()
                .title("The Clean Coder")
                .isbn("9780137081073")
                .author("Robert C. Martin")
                .edition(1)
                .publisher("Pearson").build();

        json = mapper.writeValueAsString(createBookRequest);

        request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(2, bookRepository.count());

        var expectedPage = 0;
        var expectedPerPage = 10;
        var expectedTotal = 2;
        var expectedItems = 2;

        // when
        request = MockMvcRequestBuilders.get(BOOK_API_URL)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", "title")
                .queryParam("dir", "asc")
                .queryParam("title", "Clean")
                .queryParam("author", "Rob")
                .queryParam("publisher", "P");

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
    }

    @Test
    public void givenValidId_whenCallsGetById_thenReturnBook() throws Exception {
        // given
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

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        // when
        request = MockMvcRequestBuilders.get(BOOK_API_URL + "/" + expectedId);

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
    public void givenInvalidId_whenCallsGetById_thenReturnStatus404() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

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

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        // when
        request = MockMvcRequestBuilders.get(BOOK_API_URL + "/" + 2L);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Livro não foi encontrado"));
    }

    @Test
    public void givenValidBook_whenCallsCreateBook_thenReturnBook() throws Exception {
        // given
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

        // when
        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

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
    }

    @Test
    public void givenValidExistingBook_whenCallsCreateBook_thenReturnStatus400() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("C")
                .isbn("9780132350884")
                .author("R")
                .edition(2)
                .publisher("P").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        // when
        request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Isbn já foi cadastrado"));
    }

    @Test
    public void givenInvalidBook_whenCallsCreateBook_thenReturnStatus400() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("")
                .isbn("")
                .author("")
                .edition(0)
                .publisher("").build();

        var json = mapper.writeValueAsString(createBookRequest);

        // when
        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(5)));
    }

    @Test
    public void givenValidBook_whenCallsUpdateBook_thenReturnBook() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("C")
                .isbn("9780132350884")
                .author("R")
                .edition(2)
                .publisher("P").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

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

        json = mapper.writeValueAsString(updateBookRequest);

        // when
        request = MockMvcRequestBuilders.put(BOOK_API_URL + "/" + expectedId).contentType(MediaType.APPLICATION_JSON)
                .content(json);

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
    public void givenInvalidBook_whenCallsUpdateBook_thenReturnStatus400() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("Clean Code")
                .isbn("9780132350884")
                .author("Robert C. Martin")
                .edition(1)
                .publisher("Pearson").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        var updateBookRequest = UpdateBookRequest.builder()
                .title("")
                .author("")
                .edition(0)
                .publisher("").build();

        json = mapper.writeValueAsString(updateBookRequest);

        // when
        request = MockMvcRequestBuilders.put(BOOK_API_URL + "/" + 1L).contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(4)));
    }

    @Test
    public void givenInvalidIdAndValidBook_whenCallsUpdateBook_thenReturn404() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("C")
                .isbn("9780132350884")
                .author("R")
                .edition(2)
                .publisher("P").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        var expectedTitle = "Clean Code";
        var expectedAuthor = "Robert C. Martin";
        var expectedEdition = 1;
        var expectedPublisher = "Pearson";

        var updateBookRequest = UpdateBookRequest.builder()
                .title(expectedTitle)
                .author(expectedAuthor)
                .edition(expectedEdition)
                .publisher(expectedPublisher).build();

        json = mapper.writeValueAsString(updateBookRequest);

        // when
        request = MockMvcRequestBuilders.put(BOOK_API_URL + "/" + 2L).contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Livro não foi encontrado"));
    }

    @Test
    public void givenValidId_whenCallsDeleteBook_thenReturnStatus204() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("C")
                .isbn("9780132350884")
                .author("R")
                .edition(2)
                .publisher("P").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        // when
        request = MockMvcRequestBuilders.delete(BOOK_API_URL + "/" + 1L);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertEquals(0, bookRepository.count());
    }

    @Test
    public void givenInvalidId_whenCallsDeleteBook_thenReturnStatus404() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("C")
                .isbn("9780132350884")
                .author("R")
                .edition(2)
                .publisher("P").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        // when
        request = MockMvcRequestBuilders.delete(BOOK_API_URL + "/" + 2L);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Livro não foi encontrado"));

    }

    @Test
    public void givenValidIdAndLoanActive_whenCallsDeleteBook_thenReturnStatus400() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("Clean Code")
                .isbn("9780132350884")
                .author("Robert C. Martin")
                .edition(1)
                .publisher("Pearson").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        var createLoanRequest = CreateLoanRequest.builder()
                .customer("Dhiego Silva")
                .customerEmail("dhiego@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post("/api/loans").contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        // when
        request = MockMvcRequestBuilders.delete(BOOK_API_URL + "/" + 1L);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Não é possivel deletar um livro com empréstimo ativo"));

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());
    }

    @Test
    public void givenValidParams_whenCallsLoansByBookId_thenReturnLoans() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("Clean Code")
                .isbn("9780132350884")
                .author("Robert C. Martin")
                .edition(1)
                .publisher("Pearson").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        var createLoanRequest = CreateLoanRequest.builder()
                .customer("Dhiego Silva")
                .customerEmail("dhiego@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post("/api/loans").contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        var expectedPage = 0;
        var expectedPerPage = 10;
        var expectedTotal = 1;
        var expectedItems = 1;

        // when
        request = MockMvcRequestBuilders.get(BOOK_API_URL + "/" + 1L + "/loans")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", "id")
                .queryParam("dir", "desc");

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("current_page", Matchers.equalTo(expectedPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("per_page", Matchers.equalTo(expectedPerPage)))
                .andExpect(MockMvcResultMatchers.jsonPath("total", Matchers.equalTo(expectedTotal)))
                .andExpect(MockMvcResultMatchers.jsonPath("items", Matchers.hasSize(expectedItems)));
    }

    @Test
    public void givenInvalidParams_whenCallsLoansByBookId_thenReturnStatus404() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());

        var createBookRequest = CreateBookRequest.builder()
                .title("Clean Code")
                .isbn("9780132350884")
                .author("Robert C. Martin")
                .edition(1)
                .publisher("Pearson").build();

        var json = mapper.writeValueAsString(createBookRequest);

        var request = MockMvcRequestBuilders.post(BOOK_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());

        // when
        request = MockMvcRequestBuilders.get(BOOK_API_URL + "/" + 2L + "/loans")
                .queryParam("page", String.valueOf(0))
                .queryParam("perPage", String.valueOf(10))
                .queryParam("sort", "id")
                .queryParam("dir", "desc");

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Livro não foi encontrado"));
    }
}
