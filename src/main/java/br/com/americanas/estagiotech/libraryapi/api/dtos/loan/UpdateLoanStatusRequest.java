package br.com.americanas.estagiotech.libraryapi.api.dtos.loan;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanStatusRequest {

    @NotNull(message = "'active' é obrigatório")
    private Boolean active;
}
