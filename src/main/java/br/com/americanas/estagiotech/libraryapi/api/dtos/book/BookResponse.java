package br.com.americanas.estagiotech.libraryapi.api.dtos.book;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.americanas.estagiotech.libraryapi.models.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    private String isbn;
    private String author;
    private Integer edition;
    private String publisher;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss.SSSZ")
    private Instant createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss.SSSZ")
    private Instant updatedAt;

    public static BookResponse toDto(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAuthor(),
                book.getEdition(),
                book.getPublisher(),
                book.getCreatedAt(),
                book.getUpdatedAt());
    }
}
