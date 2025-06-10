package com.ims.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("product")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @JsonProperty("quantity")
    @Column(nullable = false)
    private Integer quantity;

    @JsonProperty("total_price")
    @Column(nullable = false)
    private Double totalPrice;

    @JsonProperty("emailAddress")
    @Column(nullable = false)
    private  String emailAddress;

    @JsonProperty("timestamp")
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
