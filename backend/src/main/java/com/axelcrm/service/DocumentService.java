package com.axelcrm.service;

import com.axelcrm.dto.DocumentRequest;
import com.axelcrm.dto.DocumentResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Contract;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.Document;
import com.axelcrm.entity.Project;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ContractRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.DocumentRepository;
import com.axelcrm.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ClientRepository clientRepository;
    private final DealRepository dealRepository;
    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;

    public Page<DocumentResponse> findAll(UUID organizationId, Pageable pageable) {
        return documentRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public DocumentResponse findById(UUID organizationId, UUID id) {
        return documentRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
    }

    @Transactional
    public DocumentResponse create(UUID organizationId, DocumentRequest request) {
        Document document = new Document();
        applyRequest(document, organizationId, request);
        document = documentRepository.save(document);
        return toResponse(document);
    }

    @Transactional
    public DocumentResponse update(UUID organizationId, UUID id, DocumentRequest request) {
        Document document = documentRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        applyRequest(document, organizationId, request);
        document = documentRepository.save(document);
        return toResponse(document);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Document document = documentRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        document.setDeletedAt(LocalDateTime.now());
        documentRepository.save(document);
    }

    private void applyRequest(Document document, UUID organizationId, DocumentRequest request) {
        document.setName(request.name());
        document.setDescription(request.description());
        document.setCategory(request.category());
        document.setTags(request.tags());
        document.setFileName(request.fileName());
        document.setFileType(request.fileType());
        document.setFileSize(request.fileSize());
        document.setFileUrl(request.fileUrl());
        document.setDocumentDate(request.documentDate());
        document.setExpiryDate(request.expiryDate());
        document.setArchived(request.archived());

        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(
                    request.clientId(), organizationId).orElse(null);
            document.setClient(client);
        } else {
            document.setClient(null);
        }

        if (request.dealId() != null) {
            Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(
                    request.dealId(), organizationId).orElse(null);
            document.setDeal(deal);
        } else {
            document.setDeal(null);
        }

        if (request.contractId() != null) {
            Contract contract = contractRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(
                    request.contractId(), organizationId).orElse(null);
            document.setContract(contract);
        } else {
            document.setContract(null);
        }

        if (request.projectId() != null) {
            Project project = projectRepository.findByIdAndOrganization_Id(
                    request.projectId(), organizationId).orElse(null);
            document.setProject(project);
        } else {
            document.setProject(null);
        }
    }

    private DocumentResponse toResponse(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getName(),
                doc.getDescription(),
                doc.getCategory(),
                doc.getTags(),
                doc.getFileName(),
                doc.getFileType(),
                doc.getFileSize(),
                doc.getFileUrl(),
                doc.getClient() != null ? doc.getClient().getId() : null,
                doc.getClient() != null ? doc.getClient().getName() : null,
                doc.getDeal() != null ? doc.getDeal().getId() : null,
                doc.getDeal() != null ? doc.getDeal().getTitle() : null,
                doc.getContract() != null ? doc.getContract().getId() : null,
                doc.getContract() != null ? doc.getContract().getTitle() : null,
                doc.getProject() != null ? doc.getProject().getId() : null,
                doc.getProject() != null ? doc.getProject().getName() : null,
                doc.getDocumentDate(),
                doc.getExpiryDate(),
                doc.isArchived(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }
}
