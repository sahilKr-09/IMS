

package com.ims.controller;
import com.ims.entity.Order;
import com.ims.entity.Product;
import com.ims.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(inventoryService.createProduct(product));
    }

    @GetMapping("/ping")
    public String checkServerSatus() {
        log.info("checkServerSatus");
        return "Server is running";
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getProductById(id));
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return ResponseEntity.ok(inventoryService.updateProduct(id, updatedProduct));
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- Order APIs ---

    @PostMapping("/order")
    public ResponseEntity<Order> placeOrder(@RequestParam Long productId,
                                            @RequestParam Integer quantity,
                                            @RequestParam String emailAddress) {
        return ResponseEntity.ok(inventoryService.placeOrder(productId, quantity, emailAddress));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getOrderById(id));
    }
}