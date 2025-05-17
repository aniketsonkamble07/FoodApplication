package com.foodordering.foodapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private Order order;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @Builder.Default
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Size(max = 100)
    private String transactionId;

    @Version
    private Integer version;

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, CASH_ON_DELIVERY
    }

    public enum Status {
        PENDING, COMPLETED, FAILED, REFUNDED
    }
}