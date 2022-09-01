package br.com.americanas.estagiotech.libraryapi.models;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Integer edition;

    @Column(nullable = false)
    private String publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private Book(String title, String isbn, String author, Integer edition, String publisher) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.edition = edition;
        this.publisher = publisher;
    }

    private Book(String title, String isbn, String author, Integer edition, String publisher, Instant createdAt,
            Instant updatedAt) {
        this.title = Objects.requireNonNull(title, "'title' não pode ser nulo");
        this.isbn = Objects.requireNonNull(isbn, "'isbn' não pode ser nulo");
        this.author = Objects.requireNonNull(author, "'author' não pode ser nulo");
        this.edition = Objects.requireNonNull(edition, "'edition' não pode ser nulo");
        this.publisher = Objects.requireNonNull(publisher, "'publisher' não pode ser nulo");
        this.createdAt = Objects.requireNonNull(createdAt, "'created_at' não pode ser nulo");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updated_at' não pode ser nulo");
    }

    public static Book create(String title, String isbn, String author, Integer edition, String publisher) {
        var now = Instant.now();
        return new Book(title, isbn, author, edition, publisher, now, now);
    }

    public void update(String title, String author, Integer edition, String publisher) {
        this.title = Objects.requireNonNull(title, "'title' não pode ser nulo");
        this.author = Objects.requireNonNull(author, "'author' não pode ser nulo");
        this.edition = Objects.requireNonNull(edition, "'edition' não pode ser nulo");
        this.publisher = Objects.requireNonNull(publisher, "'publisher' não pode ser nulo");
        this.updatedAt = Instant.now();
    }

    public static Book filter(String title, String isbn, String author, Integer edition, String publisher) {
        return new Book(title, isbn, author, edition, publisher);
    }

    public static Book filter(String isbn) {
        return new Book(null, isbn, null, null, null);
    }

    public Book clone() {
        return new Book(
                this.title,
                this.isbn,
                this.author,
                this.edition,
                this.publisher,
                this.createdAt,
                this.updatedAt);
    }

}
