package com.ims.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestWrapper {
    private List<OrderItemRequest> items;
    private String emailAddress;
}
