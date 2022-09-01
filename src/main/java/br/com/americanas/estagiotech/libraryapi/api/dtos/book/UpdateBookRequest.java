package br.com.americanas.estagiotech.libraryapi.api.dtos.book;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateBookRequest {

    @NotBlank(message = "'title' é obrigatório")
    private String title;

    @NotBlank(message = "'author' é obrigatório")
    private String author;

    @NotNull(message = "'edition' é obrigatório")
    @Min(value = 1, message = "'edition' deve ser maior ou igual a 1")
    private Integer edition;

    @NotBlank(message = "'publisher' é obrigatório")
    private String publisher;
}
