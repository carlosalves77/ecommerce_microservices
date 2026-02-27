package com.carldev.order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

    private UUID addressId;

    @Column(name = "street_line1")
    private String streetLine1;

    @Column(name = "street_line2")
    private String streetLine2;

    private String city;

    private String state;

    private String country;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order orderAddress;

}
