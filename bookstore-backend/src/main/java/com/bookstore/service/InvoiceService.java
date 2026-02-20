package com.bookstore.service;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    public byte[] generateInvoice(Order order) {

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Bookstore Invoice"));
            document.add(new Paragraph("----------------------------"));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Customer Email: " + order.getUserEmail()));
            document.add(new Paragraph("Order Date: " + order.getOrderDate()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Items:"));

            for (OrderItem item : order.getOrderItems()) {
                document.add(new Paragraph(
                        "Book ID: " + item.getBookId() +
                        " | Quantity: " + item.getQuantity() +
                        " | Price: " + item.getPrice()
                ));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Amount: " + order.getTotalAmount()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for your purchase!"));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating invoice");
        }
    }
}
