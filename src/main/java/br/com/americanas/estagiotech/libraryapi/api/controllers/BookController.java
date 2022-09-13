package br.com.americanas.estagiotech.libraryapi.api.controllers;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.americanas.estagiotech.libraryapi.api.dtos.Pagination;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.BookListRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.BookResponse;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.CreateBookRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.book.UpdateBookRequest;
import br.com.americanas.estagiotech.libraryapi.api.dtos.loan.LoanResponse;
import br.com.americanas.estagiotech.libraryapi.api.exceptions.NotFoundException;
import br.com.americanas.estagiotech.libraryapi.services.BookService;
import br.com.americanas.estagiotech.libraryapi.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/api/books")
@RestController
@RequiredArgsConstructor
@Tag(name = "Books")
public class BookController {

    private static final String BOOK_MESSAGE_NOT_FOUND = "Livro não foi encontrado";
    private final BookService bookService;
    private final LoanService loanService;

    @GetMapping
    @Operation(summary = "Find books by params")
    public Pagination<?> findAll(
            BookListRequest bookListRequest,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "title") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") final String direction) {
        var book = bookListRequest.toModel();
        var pageRequest = PageRequest.of(page, perPage, Sort.by(Direction.fromString(direction), sort));
        var pageResult = bookService.findAll(book, pageRequest);

        return Pagination.toDto(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(BookResponse::toDto).toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Get book details by id")
    public BookResponse getById(@PathVariable Long id) {
        var book = bookService.getById(id).orElseThrow(() -> new NotFoundException(BOOK_MESSAGE_NOT_FOUND));

        return BookResponse.toDto(book);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a book")
    public BookResponse create(@RequestBody @Valid CreateBookRequest createBookRequest) {
        var book = createBookRequest.toModel();
        var bookCreated = bookService.create(book);

        return BookResponse.toDto(bookCreated);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}")
    @Operation(summary = "Update a book")
    public BookResponse update(@PathVariable Long id, @RequestBody @Valid UpdateBookRequest updateBookRequest) {
        var book = bookService.getById(id).orElseThrow(() -> new NotFoundException(BOOK_MESSAGE_NOT_FOUND));

        book.update(
                updateBookRequest.getTitle(),
                updateBookRequest.getAuthor(),
                updateBookRequest.getEdition(),
                updateBookRequest.getPublisher());

        var bookUpdated = bookService.update(book);

        return BookResponse.toDto(bookUpdated);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book by id")
    public void delete(@PathVariable Long id) {
        var book = bookService.getById(id).orElseThrow(() -> new NotFoundException(BOOK_MESSAGE_NOT_FOUND));
        bookService.delete(book);
    }

    @GetMapping("{id}/loans")
    @Operation(summary = "Find loans by book id")
    public Pagination<LoanResponse> loansByBookId(
            @PathVariable Long id,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "id") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "desc") final String direction) {
        var book = bookService.getById(id).orElseThrow(() -> new NotFoundException(BOOK_MESSAGE_NOT_FOUND));
        var pageRequest = PageRequest.of(page, perPage, Sort.by(Direction.fromString(direction), sort));
        var pageResult = loanService.findAllByBook(book, pageRequest);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(LoanResponse::toDto).toList());
    }

}
