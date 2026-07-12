package com.axelcrm.service;

import com.axelcrm.dto.ClientNoteRequest;
import com.axelcrm.dto.ClientNoteResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.ClientNote;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientNoteRepository;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ClientNoteService {

    private final ClientNoteRepository clientNoteRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public List<ClientNoteResponse> findByClient(UUID organizationId, UUID clientId) {
        return clientNoteRepository.findByOrganization_IdAndClient_IdAndDeletedAtIsNullOrderByCreatedAtDesc(organizationId, clientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClientNoteResponse create(UUID organizationId, UUID clientId, ClientNoteRequest request, UUID userId) {
        Client client = clientRepository.findByIdAndOrganization_Id(clientId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        ClientNote note = new ClientNote();
        note.setClient(client);
        note.setContent(request.content());

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            note.setUser(user);
        }

        note = clientNoteRepository.save(note);
        return toResponse(note);
    }

    @Transactional
    public void delete(UUID organizationId, UUID noteId) {
        ClientNote note = clientNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientNote", "id", noteId));

        if (!note.getOrganization().getId().equals(organizationId)) {
            throw new ResourceNotFoundException("ClientNote", "id", noteId);
        }

        note.setDeletedAt(java.time.LocalDateTime.now());
        clientNoteRepository.save(note);
    }

    private ClientNoteResponse toResponse(ClientNote note) {
        return new ClientNoteResponse(
                note.getId(),
                note.getClient().getId(),
                note.getUser() != null ? note.getUser().getId() : null,
                note.getUser() != null ? note.getUser().getName() : "Sistema",
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
