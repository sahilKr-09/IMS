package com.ims.service;

import com.ims.dto.OrderItemRequest;
import com.ims.entity.Order;
import com.ims.entity.Product;

import java.util.List;

public interface InventoryService {

    Product createProduct(Product product);

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product updateProduct(Long id, Product updatedProduct);

    void deleteProduct(Long id);

    Order placeOrder(List<OrderItemRequest> items, String emailAddress);

    Order getOrderById(Long id);
}
