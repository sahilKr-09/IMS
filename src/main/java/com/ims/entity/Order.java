package com.ims.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("order_items")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;  // List of items in the order

    @JsonProperty("emailAddress")
    @Column(nullable = false)
    private String emailAddress;

    @JsonProperty("total_price")
    @Column(nullable = false)
    private Double totalPrice;  // Total price for the order

    @JsonProperty("timestamp")
    @Column(nullable = false)
    private LocalDateTime timestamp;

}
