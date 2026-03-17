package com.subscriptio.application;

import com.subscriptio.api.dto.response.InvoiceResponse;
import com.subscriptio.api.dto.response.PageResponse;
import com.subscriptio.domain.model.*;
import com.subscriptio.domain.repository.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final TenantService tenantService;

    public InvoiceService(InvoiceRepository invoiceRepo, TenantService tenantService) {
        this.invoiceRepo = invoiceRepo;
        this.tenantService = tenantService;
    }

    @Transactional
    public Invoice generateInvoice(Subscription subscription) {
        Plan plan = subscription.getPlan();
        BigDecimal amount = plan.getPrice();
        Instant periodStart = subscription.getCurrentPeriodEnd();
        Instant periodEnd = "yearly".equals(plan.getBillingInterval())
                ? periodStart.plus(365, ChronoUnit.DAYS)
                : periodStart.plus(30, ChronoUnit.DAYS);

        String invoiceNumber = generateInvoiceNumber(subscription.getTenant());

        Invoice invoice = new Invoice(
                subscription.getTenant(),
                subscription.getCustomer(),
                subscription,
                invoiceNumber,
                amount,
                amount,
                periodStart.plus(14, ChronoUnit.DAYS),
                periodStart,
                periodEnd
        );
        invoice.setCurrency(plan.getCurrency());

        String description = plan.getName() + " Plan - " +
                LocalDate.ofInstant(periodStart, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("MMM yyyy"));
        InvoiceLineItem lineItem = new InvoiceLineItem(description, 1, amount, amount);
        invoice.addLineItem(lineItem);
        invoice.markOpen();

        return invoiceRepo.save(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getByExternalId(UUID externalId) {
        return invoiceRepo.findByExternalId(externalId)
                .map(InvoiceResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + externalId));
    }

    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> listByTenant(UUID tenantExternalId, Pageable pageable) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        return PageResponse.from(invoiceRepo.findByTenantId(tenant.getId(), pageable), InvoiceResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> listByCustomer(Long customerId, Pageable pageable) {
        return PageResponse.from(invoiceRepo.findByCustomerId(customerId, pageable), InvoiceResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> listByTenantAndStatus(UUID tenantExternalId,
                                                                InvoiceStatus status, Pageable pageable) {
        Tenant tenant = tenantService.findEntityByExternalId(tenantExternalId);
        return PageResponse.from(invoiceRepo.findByTenantIdAndStatus(tenant.getId(), status, pageable),
                InvoiceResponse::from);
    }

    private String generateInvoiceNumber(Tenant tenant) {
        long count = invoiceRepo.countByTenantId(tenant.getId()) + 1;
        String year = String.valueOf(LocalDate.now().getYear());
        return "INV-" + year + "-" + String.format("%04d", count);
    }
}
