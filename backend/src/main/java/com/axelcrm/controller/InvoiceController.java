package com.axelcrm.controller;

import com.axelcrm.dto.InvoiceRequest;
import com.axelcrm.dto.InvoiceResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Endpoints for managing client invoices and billing")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Operation(summary = "List all invoices")
    public ResponseEntity<Page<InvoiceResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(invoiceService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an invoice by ID")
    public ResponseEntity<InvoiceResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(invoiceService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(invoiceService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing invoice")
    public ResponseEntity<InvoiceResponse> update(@PathVariable UUID id, @Valid @RequestBody InvoiceRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(invoiceService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an invoice")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        invoiceService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Generate PDF for an invoice")
    public ResponseEntity<byte[]> getPdf(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        byte[] pdfBytes = invoiceService.generateInvoicePdf(organizationId, id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"fatura-" + id + ".pdf\"")
                .body(pdfBytes);
    }

    @GetMapping("/report/pdf")
    @Operation(summary = "Generate consolidative billing report PDF for all active invoices")
    public ResponseEntity<byte[]> getReportPdf() {
        UUID organizationId = TenantContext.getOrganizationId();
        byte[] pdfBytes = invoiceService.generateInvoicesReportPdf(organizationId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"relatorio-faturamento.pdf\"")
                .body(pdfBytes);
    }
}
