package br.com.americanas.estagiotech.libraryapi.api.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Pagination<T> {

    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("per_page")
    private int perPage;
    private long total;
    private List<T> items;

    public static Pagination<?> toDto(int currentPage, int perPage, long total, List<?> items) {
        return new Pagination<>(currentPage, perPage, total, items);
    }
}
