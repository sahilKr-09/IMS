package com.ims.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("name")
    @Column(nullable = false)
    private String name;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("price")
    @Column(nullable = false)
    private Double price;

    @JsonProperty("type")
    private String type;

}
