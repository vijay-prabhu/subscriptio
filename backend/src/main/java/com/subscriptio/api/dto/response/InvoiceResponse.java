package com.subscriptio.api.dto.response;

import com.subscriptio.domain.model.Invoice;
import com.subscriptio.domain.model.InvoiceLineItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        String invoiceNumber,
        String status,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        String currency,
        Instant dueDate,
        Instant paidAt,
        Instant periodStart,
        Instant periodEnd,
        String pdfUrl,
        List<LineItemResponse> lineItems,
        Instant createdAt
) {
    public record LineItemResponse(
            String description,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal amount
    ) {
        public static LineItemResponse from(InvoiceLineItem item) {
            return new LineItemResponse(item.getDescription(), item.getQuantity(),
                    item.getUnitPrice(), item.getAmount());
        }
    }

    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getExternalId(),
                invoice.getInvoiceNumber(),
                invoice.getStatus().name(),
                invoice.getSubtotal(),
                invoice.getTax(),
                invoice.getTotal(),
                invoice.getCurrency(),
                invoice.getDueDate(),
                invoice.getPaidAt(),
                invoice.getPeriodStart(),
                invoice.getPeriodEnd(),
                invoice.getPdfUrl(),
                invoice.getLineItems().stream().map(LineItemResponse::from).toList(),
                invoice.getCreatedAt()
        );
    }
}
