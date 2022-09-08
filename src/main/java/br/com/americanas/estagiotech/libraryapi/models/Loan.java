package br.com.americanas.estagiotech.libraryapi.models;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customer;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Loan(String customer, String customerEmail, String bookIsbn) {
        this.customer = customer;
        this.customerEmail = customerEmail;
        this.book = bookIsbn != null ? Book.filter(bookIsbn) : null;
    }

    private Loan(
            String customer,
            String customerEmail,
            Book book,
            LocalDate loanDate,
            Boolean active,
            Instant createdAt,
            Instant updatedAt) {
        this.customer = Objects.requireNonNull(customer, "'customer' não pode ser nulo");
        this.customerEmail = Objects.requireNonNull(customerEmail, "'customer_email' não pode ser nulo");
        this.book = Objects.requireNonNull(book, "'book' não pode ser nulo");
        this.loanDate = Objects.requireNonNull(loanDate, "'loan_date' não pode ser nulo");
        this.active = Objects.requireNonNull(active, "'active' não pode ser nulo");
        this.createdAt = Objects.requireNonNull(createdAt, "'created_at' não pode ser nulo");
        this.updatedAt = Objects.requireNonNull(updatedAt, "'updated_at' não pode ser nulo");
    }

    public static Loan create(String customer, String customerEmail, Book book) {
        var now = Instant.now();
        return new Loan(
                customer,
                customerEmail,
                book,
                LocalDate.now(),
                true,
                now,
                now);
    }

    public void update(String customer, String customerEmail) {
        this.customer = Objects.requireNonNull(customer, "'customer' não pode ser nulo");
        this.customerEmail = Objects.requireNonNull(customerEmail, "'customer_email' não pode ser nulo");
        this.updatedAt = Instant.now();
    }

    public void updateStatus(Boolean active) {
        this.active = Objects.requireNonNull(active, "'active' não pode ser nulo");
        this.updatedAt = Instant.now();
    }

    public static Loan filter(String customer, String customerEmail, String bookIsbn) {
        return new Loan(customer, customerEmail, bookIsbn);
    }

}
