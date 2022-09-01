package br.com.americanas.estagiotech.libraryapi.api.dtos;

import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Pagination<T> {

    private int currentPage;
    private int perPage;
    private long total;
    private List<T> items;

    public <R> Pagination<R> map(final Function<T, R> mapper) {
        final List<R> aNewList = this.items.stream().map(mapper).toList();

        return new Pagination<>(this.currentPage, this.perPage, this.total, aNewList);
    }
}
