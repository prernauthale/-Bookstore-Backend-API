package com.bookstore.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bookstore.entity.PaymentStatus;
import com.bookstore.entity.OrderStatus;
import com.bookstore.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bookstore.dto.OrderItemRequest;
import com.bookstore.dto.OrderRequest;
import com.bookstore.dto.OrderResponse;
import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    // ================= PLACE ORDER =================
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody OrderRequest orderRequest,
            Authentication authentication) {

        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        String userEmail = authentication.getName();

        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PLACED);

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest item : orderRequest.getItems()) {

            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Book not found with id: " + item.getBookId()));

            if (book.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for book: " + book.getTitle());
            }

            // Reduce stock
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(book.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(book.getPrice());
            orderItem.setOrder(order);

            totalAmount += book.getPrice() * item.getQuantity();
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        return ResponseEntity.ok(
                new OrderResponse(savedOrder.getId(), "Order placed successfully")
        );
    }

    // ================= GET MY ORDERS =================
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {

        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        String userEmail = authentication.getName();

        List<Order> orders = orderRepository.findByUserEmail(userEmail);

        return ResponseEntity.ok(orders);
    }

    // ================= UPDATE ORDER STATUS (ADMIN) =================
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return orderRepository.findById(id)
                .map(order -> {
                    order.setOrderStatus(status);
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));
    }

    // ================= UPDATE PAYMENT STATUS (ADMIN) =================
    @PutMapping("/{id}/payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus paymentStatus) {

        return orderRepository.findById(id)
                .map(order -> {
                    order.setPaymentStatus(paymentStatus);
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));
    }

    // ================= DOWNLOAD INVOICE =================
 // ================= DOWNLOAD INVOICE (SECURED) =================
    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));

        String loggedInUserEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Allow only order owner OR admin
        if (!order.getUserEmail().equals(loggedInUserEmail) && !isAdmin) {
            throw new RuntimeException("Access Denied: You cannot download this invoice");
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            document.open();

            document.add(new Paragraph("===== BOOKSTORE INVOICE ====="));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Customer: " + order.getUserEmail()));
            document.add(new Paragraph("Date: " + order.getOrderDate()));
            document.add(new Paragraph("Payment Status: " + order.getPaymentStatus()));
            document.add(new Paragraph("Order Status: " + order.getOrderStatus()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Items:"));
            document.add(new Paragraph("---------------------------------------"));

            for (OrderItem item : order.getOrderItems()) {
                document.add(new Paragraph(
                        "Book ID: " + item.getBookId() +
                        " | Quantity: " + item.getQuantity() +
                        " | Price: " + item.getPrice()
                ));
            }

            document.add(new Paragraph("---------------------------------------"));
            document.add(new Paragraph("Total Amount: $" + order.getTotalAmount()));

            document.close();

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=invoice_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error generating invoice");
        }
    }
 // ================= CANCEL ORDER (CUSTOMER) =================
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));

        // Only order owner can cancel
        if (!order.getUserEmail().equals(authentication.getName())) {
            throw new RuntimeException("Access denied");
        }

        // Cannot cancel if already shipped or delivered
        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
            order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel shipped/delivered order");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        return ResponseEntity.ok(orderRepository.save(order));
    }
}
