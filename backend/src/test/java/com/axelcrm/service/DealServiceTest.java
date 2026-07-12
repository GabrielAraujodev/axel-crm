package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.DealRequest;
import com.axelcrm.dto.DealResponse;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.Pipeline;
import com.axelcrm.entity.PipelineStage;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ContactRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.PipelineRepository;
import com.axelcrm.repository.PipelineStageRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    DealRepository dealRepository;

    @Mock
    PipelineRepository pipelineRepository;

    @Mock
    PipelineStageRepository pipelineStageRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    ContactRepository contactRepository;

    @InjectMocks
    DealService dealService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID dealId = UUID.randomUUID();
    private final UUID pipelineId = UUID.randomUUID();
    private final UUID stageId = UUID.randomUUID();
    private final UUID clientId = UUID.randomUUID();

    private Deal createDeal() {
        var org = new Organization();
        org.setId(orgId);

        var pipeline = new Pipeline();
        pipeline.setId(pipelineId);
        pipeline.setName("Sales");

        var stage = new PipelineStage();
        stage.setId(stageId);
        stage.setName("Qualified");

        var client = new Client();
        client.setId(clientId);
        client.setName("Client");

        var deal = new Deal();
        deal.setId(dealId);
        deal.setTitle("Enterprise Deal");
        deal.setValue(BigDecimal.valueOf(50000));
        deal.setPipeline(pipeline);
        deal.setStage(stage);
        deal.setClient(client);
        deal.setOrganization(org);
        return deal;
    }

    @Test
    void findAll_ShouldReturnPagedDeals() {
        var deal = createDeal();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(java.util.List.of(deal));

        when(dealRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<DealResponse> result = dealService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Enterprise Deal", result.getContent().getFirst().title());
    }

    @Test
    void findById_ShouldReturnDeal() {
        var deal = createDeal();
        when(dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, orgId))
                .thenReturn(Optional.of(deal));

        DealResponse result = dealService.findById(orgId, dealId);

        assertNotNull(result);
        assertEquals(dealId, result.id());
        assertEquals("Enterprise Deal", result.title());
        assertEquals(pipelineId, result.pipelineId());
        assertEquals(stageId, result.stageId());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dealService.findById(orgId, dealId));
    }

    @Test
    void create_ShouldSaveAndReturnDeal() {
        var request = new DealRequest(
                "New Deal", "Desc", BigDecimal.valueOf(100000),
                pipelineId, stageId, clientId, null, null, LocalDate.now().plusDays(30));

        var pipeline = new Pipeline();
        pipeline.setId(pipelineId);
        pipeline.setName("Sales");

        var stage = new PipelineStage();
        stage.setId(stageId);
        stage.setName("Lead");

        var client = new Client();
        client.setId(clientId);
        client.setName("ClientCo");

        var saved = new Deal();
        saved.setId(dealId);
        saved.setTitle("New Deal");
        saved.setValue(BigDecimal.valueOf(100000));
        saved.setPipeline(pipeline);
        saved.setStage(stage);
        saved.setClient(client);

        when(pipelineRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(pipelineId, orgId))
                .thenReturn(Optional.of(pipeline));
        when(pipelineStageRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(stageId, orgId))
                .thenReturn(Optional.of(stage));
        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));
        when(dealRepository.save(any(Deal.class))).thenReturn(saved);

        DealResponse result = dealService.create(orgId, request);

        assertNotNull(result);
        assertEquals("New Deal", result.title());
        assertEquals(pipelineId, result.pipelineId());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var deal = createDeal();
        when(dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, orgId))
                .thenReturn(Optional.of(deal));

        dealService.delete(orgId, dealId);

        assertNotNull(deal.getDeletedAt());
        verify(dealRepository).save(deal);
    }
}
