
package com.ims.service;

import com.ims.dto.EmailMessage;
import com.ims.entity.Product;
import com.ims.entity.Order;
import com.ims.repository.ProductRepository;
import com.ims.repository.OrderRepository;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final JmsTemplate jmsTemplate;

    public InventoryServiceImpl(ProductRepository productRepository,
                                OrderRepository orderRepository,
                                JmsTemplate jmsTemplate) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.jmsTemplate = jmsTemplate;

    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setType(updatedProduct.getType());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Order placeOrder(Long productId, Integer quantity, String emailAddress) {
        Product product = getProductById(productId);

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setTotalPrice(quantity * product.getPrice());
        order.setTimestamp(LocalDateTime.now());
        order.setEmailAddress(emailAddress);

        Order savedOrder = orderRepository.save(order);

        // Prepare email message
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(emailAddress);
        emailMessage.setSubject("Order Confirmation - #" + savedOrder.getId());
        emailMessage.setBody("Thank you for your order of " + quantity + " unit(s) of " + product.getName()
                + ".\nTotal Price: ₹" + savedOrder.getTotalPrice());

        // Send message to ActiveMQ queue
        jmsTemplate.convertAndSend("email-queue", emailMessage);

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}