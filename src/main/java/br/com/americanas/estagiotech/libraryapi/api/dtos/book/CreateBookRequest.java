package br.com.americanas.estagiotech.libraryapi.api.dtos.book;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import br.com.americanas.estagiotech.libraryapi.models.Book;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateBookRequest {

    @NotBlank(message = "'title' é obrigatório")
    private String title;

    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "'isbn' deve ter 10 ou 13 digitos")
    private String isbn;

    @NotBlank(message = "'author' é obrigatório")
    private String author;

    @NotNull(message = "'edition' é obrigatório")
    @Min(value = 1, message = "'edition' deve ser maior ou igual a 1")
    private Integer edition;

    @NotBlank(message = "'publisher' é obrigatório")
    private String publisher;

    public Book ToModel() {
        return Book.create(this.title, this.isbn, this.author, this.edition, this.publisher);
    }
}
