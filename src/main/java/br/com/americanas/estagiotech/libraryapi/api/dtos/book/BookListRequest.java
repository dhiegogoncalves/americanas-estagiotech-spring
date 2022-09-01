package br.com.americanas.estagiotech.libraryapi.api.dtos.book;

import br.com.americanas.estagiotech.libraryapi.models.Book;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookListRequest {
    private String title;
    private String isbn;
    private String author;
    private Integer edition;
    private String publisher;

    public Book ToModel() {
        return Book.filter(this.title, this.isbn, this.author, this.edition, this.publisher);
    }
}
