package com.axelcrm.service;

import com.axelcrm.dto.ClientAttachmentResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.ClientAttachment;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientAttachmentRepository;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ClientAttachmentService {

    private final ClientAttachmentRepository clientAttachmentRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public List<ClientAttachmentResponse> findByClient(UUID organizationId, UUID clientId) {
        return clientAttachmentRepository.findByOrganization_IdAndClient_IdAndDeletedAtIsNullOrderByCreatedAtDesc(organizationId, clientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClientAttachment findForDownload(UUID organizationId, UUID attachmentId) {
        return clientAttachmentRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(attachmentId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientAttachment", "id", attachmentId));
    }

    @Transactional
    public ClientAttachmentResponse create(UUID organizationId, UUID clientId, MultipartFile file, UUID userId) throws IOException {
        Client client = clientRepository.findByIdAndOrganization_Id(clientId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        ClientAttachment attachment = new ClientAttachment();
        attachment.setClient(client);
        attachment.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed_file");
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFileData(file.getBytes());

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            attachment.setUser(user);
        }

        attachment = clientAttachmentRepository.save(attachment);
        return toResponse(attachment);
    }

    @Transactional
    public void delete(UUID organizationId, UUID attachmentId) {
        ClientAttachment attachment = clientAttachmentRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(attachmentId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientAttachment", "id", attachmentId));

        attachment.setDeletedAt(java.time.LocalDateTime.now());
        clientAttachmentRepository.save(attachment);
    }

    private ClientAttachmentResponse toResponse(ClientAttachment attachment) {
        return new ClientAttachmentResponse(
                attachment.getId(),
                attachment.getClient().getId(),
                attachment.getUser() != null ? attachment.getUser().getId() : null,
                attachment.getUser() != null ? attachment.getUser().getName() : "Sistema",
                attachment.getFileName(),
                attachment.getFileType(),
                attachment.getFileSize(),
                attachment.getCreatedAt()
        );
    }
}
