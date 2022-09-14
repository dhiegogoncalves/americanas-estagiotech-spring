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
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.CreateLoanRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.UpdateLoanRequest;
import br.com.americanas.estagiotech.libraryapi.repositories.BookRepository;
import br.com.americanas.estagiotech.libraryapi.repositories.LoanRepository;

@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
public class LoanControllerE2ETest {

    private static String LOAN_API_URL = "/api/loans";
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
    public void givenValidParams_whenCallsFindAll_thenReturnLoans() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

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

        var expectedCustomer = "Joao Silva";
        var expectedCustomerEmail = "joao.silva@email.com";
        var expectedActive = "true";

        var createLoanRequest = CreateLoanRequest.builder()
                .customer(expectedCustomer)
                .customerEmail(expectedCustomerEmail)
                .bookIsbn(expectedIsbn).build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        createLoanRequest = CreateLoanRequest.builder()
                .customer("Paulo Silva")
                .customerEmail("paulo.silva@email.com")
                .bookIsbn("9780137081073").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(2, bookRepository.count());
        Assertions.assertEquals(2, loanRepository.count());

        var expectedPage = 0;
        var expectedPerPage = 10;
        var expectedTotal = 2;
        var expectedItems = 2;

        // when
        request = MockMvcRequestBuilders.get(LOAN_API_URL)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", "customer")
                .queryParam("dir", "asc")
                .queryParam("customer_email", "silva");

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("current_page").value(expectedPage))
                .andExpect(MockMvcResultMatchers.jsonPath("per_page").value(expectedPerPage))
                .andExpect(MockMvcResultMatchers.jsonPath("total").value(expectedTotal))
                .andExpect(MockMvcResultMatchers.jsonPath("items", Matchers.hasSize(expectedItems)))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].customer").value(expectedCustomer))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].customer_email").value(expectedCustomerEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].loan_date").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].active").value(expectedActive))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].updated_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("items[0].book.updated_at").isNotEmpty());
    }

    @Test
    public void givenValidId_whenCallsGetById_thenReturnLoan() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

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

        var expectedCustomer = "Joao Silva";
        var expectedCustomerEmail = "joao.silva@email.com";
        var expectedActive = "true";

        var createLoanRequest = CreateLoanRequest.builder()
                .customer(expectedCustomer)
                .customerEmail(expectedCustomerEmail)
                .bookIsbn(expectedIsbn).build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        // when
        request = MockMvcRequestBuilders.get(LOAN_API_URL + "/" + expectedId);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("customer").value(expectedCustomer))
                .andExpect(MockMvcResultMatchers.jsonPath("customer_email").value(expectedCustomerEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("loan_date").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("active").value(expectedActive))
                .andExpect(MockMvcResultMatchers.jsonPath("created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("updated_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("book.id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("book.title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("book.isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("book.author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("book.edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("book.publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("book.created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("book.updated_at").isNotEmpty());
    }

    @Test
    public void givenInvalidId_whenCallsGetById_thenReturnStatus404() throws Exception {
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
                .customer("João Silva")
                .customerEmail("joao.silva@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        // when
        request = MockMvcRequestBuilders.get(LOAN_API_URL + "/" + 2L);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Empréstimo não foi encontrado"));
    }

    @Test
    public void givenValidLoan_whenCallsCreateLoan_thenReturnLoan() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

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

        var expectedCustomer = "João Silva";
        var expectedCustomerEmail = "joao.silva@email.com";
        var expectedActive = "true";

        var createLoanRequest = CreateLoanRequest.builder()
                .customer(expectedCustomer)
                .customerEmail(expectedCustomerEmail)
                .bookIsbn(expectedIsbn).build();

        json = mapper.writeValueAsString(createLoanRequest);

        // when
        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("customer").value(expectedCustomer))
                .andExpect(MockMvcResultMatchers.jsonPath("customer_email").value(expectedCustomerEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("loan_date").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("active").value(expectedActive))
                .andExpect(MockMvcResultMatchers.jsonPath("created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("updated_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("book.id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("book.title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("book.isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("book.author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("book.edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("book.publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("book.created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("book.updated_at").isNotEmpty());
    }

    @Test
    public void givenInvalidLoan_whenCallsCreateLoan_thenReturnStatus400() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

        var createLoanRequest = CreateLoanRequest.builder()
                .customer("")
                .customerEmail("")
                .bookIsbn("").build();

        var json = mapper.writeValueAsString(createLoanRequest);

        // when
        var request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    public void givenValidLoan_whenCallsCreateLoan_thenReturnStatus404() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

        var createLoanRequest = CreateLoanRequest.builder()
                .customer("João Silva")
                .customerEmail("joao.silva@email.com")
                .bookIsbn("9780132350884").build();

        var json = mapper.writeValueAsString(createLoanRequest);

        // when
        var request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Livro não foi encontrado"));
    }

    @Test
    public void givenValidLoanAndExistingBook_whenCallsCreateLoan_thenReturnStatus400() throws Exception {
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
                .customer("João Silva")
                .customerEmail("joao.silva@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        createLoanRequest = CreateLoanRequest.builder()
                .customer("Paulo Silva")
                .customerEmail("paulo.silva@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        // when
        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Livro já foi emprestado"));
    }

    @Test
    public void givenValidLoan_whenCallsUpdateLoan_thenReturnLoan() throws Exception {
        // given
        Assertions.assertEquals(0, bookRepository.count());
        Assertions.assertEquals(0, loanRepository.count());

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

        var createLoanRequest = CreateLoanRequest.builder()
                .customer("João")
                .customerEmail("joao@email.com")
                .bookIsbn(expectedIsbn).build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        var expectedCustomer = "João Silva";
        var expectedCustomerEmail = "joao.silva@email.com";
        var expectedActive = "true";

        var updateLoanRequest = UpdateLoanRequest.builder()
                .customer(expectedCustomer)
                .customerEmail(expectedCustomerEmail).build();

        json = mapper.writeValueAsString(updateLoanRequest);

        // when
        request = MockMvcRequestBuilders.put(LOAN_API_URL + "/" + expectedId).contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("customer").value(expectedCustomer))
                .andExpect(MockMvcResultMatchers.jsonPath("customer_email").value(expectedCustomerEmail))
                .andExpect(MockMvcResultMatchers.jsonPath("loan_date").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("active").value(expectedActive))
                .andExpect(MockMvcResultMatchers.jsonPath("created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("updated_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("book.id").value(expectedId))
                .andExpect(MockMvcResultMatchers.jsonPath("book.title").value(expectedTitle))
                .andExpect(MockMvcResultMatchers.jsonPath("book.isbn").value(expectedIsbn))
                .andExpect(MockMvcResultMatchers.jsonPath("book.author").value(expectedAuthor))
                .andExpect(MockMvcResultMatchers.jsonPath("book.edition").value(expectedEdition))
                .andExpect(MockMvcResultMatchers.jsonPath("book.publisher").value(expectedPublisher))
                .andExpect(MockMvcResultMatchers.jsonPath("book.created_at").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("book.updated_at").isNotEmpty());
    }

    @Test
    public void givenInvalidLoan_whenCallsUpdateLoan_thenReturnStatus400() throws Exception {
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
                .customer("João")
                .customerEmail("joao@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        var updateLoanRequest = UpdateLoanRequest.builder()
                .customer("")
                .customerEmail("").build();

        json = mapper.writeValueAsString(updateLoanRequest);

        // when
        request = MockMvcRequestBuilders.put(LOAN_API_URL + "/1").contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(2)));
    }

    @Test
    public void givenInvalidIdAndValidLoan_whenCallsUpdateLoan_thenReturn404() throws Exception {
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
                .customer("João")
                .customerEmail("joao@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        var updateLoanRequest = UpdateLoanRequest.builder()
                .customer("João Paulo")
                .customerEmail("joao.silva@email.com").build();

        json = mapper.writeValueAsString(updateLoanRequest);

        // when
        request = MockMvcRequestBuilders.put(LOAN_API_URL + "/2").contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Empréstimo não foi encontrado"));
    }

    @Test
    public void givenValidId_whenCallsUpdateFinalizeLoan_thenReturnStatus200() throws Exception {
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
                .customer("João")
                .customerEmail("joao@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        // when
        request = MockMvcRequestBuilders.post(LOAN_API_URL + "/1/finalize");

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenInvalidId_whenCallsFinalizeLoan_thenReturnStatus404() throws Exception {
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
                .customer("João")
                .customerEmail("joao@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        // when
        request = MockMvcRequestBuilders.post(LOAN_API_URL + "/2/finalize");

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Empréstimo não foi encontrado"));
    }

    @Test
    public void givenValidId_whenCallsDeleteLoan_thenReturnStatus204() throws Exception {
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
                .customer("João Paulo")
                .customerEmail("joao.silva@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        request = MockMvcRequestBuilders.post(LOAN_API_URL + "/1/finalize").content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());

        // when
        request = MockMvcRequestBuilders.delete(LOAN_API_URL + "/1").content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenValidIdAndLoanActive_whenCallsDeleteLoan_thenReturnStatus400() throws Exception {
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
                .customer("João Paulo")
                .customerEmail("joao.silva@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        // when
        request = MockMvcRequestBuilders.delete(LOAN_API_URL + "/1").content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Não é possivel deletar um empréstimo ativo"));
    }

    @Test
    public void givenInvalidId_whenCallsDeleteLoan_thenReturnStatus404() throws Exception {
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
                .customer("João Paulo")
                .customerEmail("joao.silva@email.com")
                .bookIsbn("9780132350884").build();

        json = mapper.writeValueAsString(createLoanRequest);

        request = MockMvcRequestBuilders.post(LOAN_API_URL).contentType(MediaType.APPLICATION_JSON).content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, bookRepository.count());
        Assertions.assertEquals(1, loanRepository.count());

        request = MockMvcRequestBuilders.post(LOAN_API_URL + "/1/finalize").content(json);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());

        // when
        request = MockMvcRequestBuilders.delete(LOAN_API_URL + "/" + 2L).content(json);

        // then
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Empréstimo não foi encontrado"));
    }

}
