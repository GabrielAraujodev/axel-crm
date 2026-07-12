package com.axelcrm.controller;

import com.axelcrm.dto.ProductRequest;
import com.axelcrm.dto.ProductResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for managing product and service catalog")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List all products")
    public ResponseEntity<Page<ProductResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(productService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(productService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(productService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(productService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a product")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        productService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
