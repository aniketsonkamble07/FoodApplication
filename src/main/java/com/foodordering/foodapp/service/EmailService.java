package com.foodordering.foodapp.service;

import com.foodordering.foodapp.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(String to, Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Order Confirmation - " + order.getOrderNumber());
        message.setText(buildEmailBody(order));
        mailSender.send(message);
    }

    private String buildEmailBody(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Thank you for your order!\n");
        sb.append("Order Number: ").append(order.getOrderNumber()).append("\n");
        sb.append("Order Date: ").append(order.getOrderDate()).append("\n");
        sb.append("Delivery Address: ").append(order.getDeliveryAddress()).append("\n");
        sb.append("Order Status: ").append(order.getStatus()).append("\n");
        sb.append("Items:\n");

        order.getItems().forEach(item -> {
            sb.append("- ").append(item.getFoodItem().getName())
                    .append(" x").append(item.getQuantity())
                    .append(" @ ").append(item.getFoodItem().getPrice())
                    .append("\n");
        });

        sb.append("Total Amount: ").append(order.getTotalAmount()).append("\n\n");
        sb.append("We will notify you once your order is processed.\n");
        return sb.toString();
    }
}
