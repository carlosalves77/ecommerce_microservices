package com.carldev.payment_service.entity;

import com.carldev.payment_service.utils.PaymentStatus;
import com.carldev.payment_service.utils.PaymentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_table")
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column(nullable  = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Long orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable  = false)
    PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    PaymentType paymentType;

    @OneToMany(
            mappedBy = "payment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<PaymentItem> items = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public void addItem(PaymentItem item) {
        items.add(item);
        item.setPayment(this);
    }
}
