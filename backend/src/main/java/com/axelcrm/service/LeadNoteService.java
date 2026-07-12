package com.axelcrm.service;

import com.axelcrm.dto.LeadDetailDtos.LeadNoteRequest;
import com.axelcrm.dto.LeadDetailDtos.LeadNoteResponse;
import com.axelcrm.entity.Lead;
import com.axelcrm.entity.LeadNote;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.LeadNoteRepository;
import com.axelcrm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadNoteService {

    private final LeadNoteRepository leadNoteRepository;
    private final LeadRepository leadRepository;

    @Transactional(readOnly = true)
    public List<LeadNoteResponse> findNotesByLeadId(UUID organizationId, UUID leadId) {
        return leadNoteRepository.findByOrganization_IdAndLead_IdAndDeletedAtIsNullOrderByCreatedAtDesc(
                organizationId, leadId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeadNoteResponse createNote(UUID organizationId, UUID leadId, LeadNoteRequest request, UUID userId) {
        Lead lead = leadRepository.findByIdAndOrganization_Id(leadId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        LeadNote note = new LeadNote();
        note.setLead(lead);
        note.setContent(request.content());
        
        User user = new User();
        user.setId(userId);
        note.setUser(user);

        note = leadNoteRepository.save(note);
        return toResponse(note);
    }

    @Transactional
    public void deleteNote(UUID organizationId, UUID leadId, UUID noteId) {
        LeadNote note = leadNoteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("LeadNote", "id", noteId));

        if (!note.getLead().getId().equals(leadId) || !note.getOrganization().getId().equals(organizationId)) {
            throw new ResourceNotFoundException("LeadNote", "id", noteId);
        }

        note.setDeletedAt(java.time.LocalDateTime.now());
        leadNoteRepository.save(note);
    }

    private LeadNoteResponse toResponse(LeadNote note) {
        return new LeadNoteResponse(
                note.getId(),
                note.getContent(),
                note.getUser() != null ? note.getUser().getId() : null,
                note.getCreatedAt()
        );
    }
}
