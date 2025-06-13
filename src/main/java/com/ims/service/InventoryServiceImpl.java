
package com.ims.service;

import com.ims.dto.EmailMessage;
import com.ims.dto.OrderItemRequest;
import com.ims.entity.Order;
import com.ims.entity.OrderItem;
import com.ims.entity.Product;
import com.ims.repository.OrderItemRepository;
import com.ims.repository.OrderRepository;
import com.ims.repository.ProductRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final JmsTemplate jmsTemplate;

    public InventoryServiceImpl(ProductRepository productRepository,
                                OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository,
                                JmsTemplate jmsTemplate) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
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
    public Order placeOrder(List<OrderItemRequest> items, String emailAddress) {
        double totalOrderPrice = 0.0;

        // Step 1: Validate stock for all items before doing any update
        for (OrderItemRequest itemReq : items) {
            Product product = getProductById(itemReq.getProductId());
            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        // Step 2: Create order but DO NOT save yet
        Order order = new Order();
        order.setEmailAddress(emailAddress);
        order.setTimestamp(LocalDateTime.now());

        StringBuilder mailBody = new StringBuilder();
        mailBody.append("Your order has been placed successfully!\n\nItems:\n");

        List<OrderItem> orderItems = new ArrayList<>();

        // Step 3: Process items, update stock, and build order items
        for (OrderItemRequest itemReq : items) {
            Product product = getProductById(itemReq.getProductId());

            // Deduct stock
            product.setQuantity(product.getQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            // Calculate price
            double itemTotalPrice = product.getPrice() * itemReq.getQuantity();
            totalOrderPrice += itemTotalPrice;

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // link, but not saving yet
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(itemTotalPrice);

            orderItems.add(orderItem);

            mailBody.append("- ").append(product.getName())
                    .append(" | Qty: ").append(itemReq.getQuantity())
                    .append(" | ₹").append(itemTotalPrice)
                    .append("\n");
        }

        // Step 4: Set total price and save order
        order.setTotalPrice(totalOrderPrice);
        Order savedOrder = orderRepository.save(order);

        // Step 5: Save all order items with the saved order reference
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder); // ensure correct linkage
            orderItemRepository.save(orderItem);
        }

        mailBody.append("\nTotal Price: ₹").append(totalOrderPrice);

        // Step 6: Prepare and send email via ActiveMQ
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(emailAddress);
        emailMessage.setSubject("Order Confirmation - #" + savedOrder.getId());
        emailMessage.setBody(mailBody.toString());

        jmsTemplate.convertAndSend("email-queue", emailMessage);

        return savedOrder;
    }


    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}