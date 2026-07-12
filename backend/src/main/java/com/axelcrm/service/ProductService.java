package com.axelcrm.service;

import com.axelcrm.dto.ProductRequest;
import com.axelcrm.dto.ProductResponse;
import com.axelcrm.entity.Product;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> findAll(UUID organizationId, Pageable pageable) {
        return productRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public ProductResponse findById(UUID organizationId, UUID id) {
        return productRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional
    public ProductResponse create(UUID organizationId, ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setCategory(request.category());
        product.setUnitPrice(request.unitPrice() != null ? request.unitPrice() : java.math.BigDecimal.ZERO);
        product.setCostPrice(request.costPrice());
        product.setUnit(request.unit());
        product.setActive(request.isActive() == null || request.isActive());
        product.setNotes(request.notes());

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(UUID organizationId, UUID id, ProductRequest request) {
        Product product = productRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setSku(request.sku());
        product.setCategory(request.category());
        product.setUnitPrice(request.unitPrice() != null ? request.unitPrice() : java.math.BigDecimal.ZERO);
        product.setCostPrice(request.costPrice());
        product.setUnit(request.unit());
        product.setActive(request.isActive() == null || request.isActive());
        product.setNotes(request.notes());

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Product product = productRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getCategory(),
                product.getUnitPrice(),
                product.getCostPrice(),
                product.getUnit(),
                product.isActive(),
                product.getNotes(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
