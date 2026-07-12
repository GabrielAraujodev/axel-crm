package com.axelcrm.service;

import com.axelcrm.dto.ChartOfAccountRequest;
import com.axelcrm.dto.ChartOfAccountResponse;
import com.axelcrm.entity.ChartOfAccount;
import com.axelcrm.entity.enums.ChartOfAccountType;
import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ChartOfAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ChartOfAccountService {

    private final ChartOfAccountRepository chartOfAccountRepository;

    public List<ChartOfAccountResponse> findTree(UUID organizationId) {
        List<ChartOfAccount> roots = chartOfAccountRepository.findByOrganization_IdAndParentIsNullAndDeletedAtIsNullOrderByCodeAsc(organizationId);
        return roots.stream().map(this::toTreeResponse).collect(Collectors.toList());
    }

    public List<ChartOfAccountResponse> findAllFlat(UUID organizationId) {
        List<ChartOfAccount> list = chartOfAccountRepository.findByOrganization_IdAndDeletedAtIsNullOrderByCodeAsc(organizationId);
        return list.stream().map(this::toFlatResponse).collect(Collectors.toList());
    }

    public ChartOfAccountResponse findById(UUID organizationId, UUID id) {
        return chartOfAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toFlatResponse)
                .orElseThrow(() -> new ResourceNotFoundException("ChartOfAccount", "id", id));
    }

    @Transactional
    public ChartOfAccountResponse create(UUID organizationId, ChartOfAccountRequest request) {
        // Check duplicate code
        if (chartOfAccountRepository.findByOrganization_IdAndCodeAndDeletedAtIsNull(organizationId, request.code()).isPresent()) {
            throw new BadRequestException("Já existe uma conta contábil com o código " + request.code());
        }

        ChartOfAccount account = new ChartOfAccount();
        account.setCode(request.code());
        account.setName(request.name());
        account.setType(request.type());

        if (request.parentId() != null) {
            ChartOfAccount parent = chartOfAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.parentId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("ChartOfAccount", "parentId", request.parentId()));
            account.setParent(parent);
            account.setLevel(parent.getLevel() + 1);
        } else {
            account.setParent(null);
            account.setLevel(1);
        }

        account = chartOfAccountRepository.save(account);
        return toFlatResponse(account);
    }

    @Transactional
    public ChartOfAccountResponse update(UUID organizationId, UUID id, ChartOfAccountRequest request) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("ChartOfAccount", "id", id));

        // Check duplicate code if changed
        if (!account.getCode().equals(request.code())) {
            if (chartOfAccountRepository.findByOrganization_IdAndCodeAndDeletedAtIsNull(organizationId, request.code()).isPresent()) {
                throw new BadRequestException("Já existe uma conta contábil com o código " + request.code());
            }
        }

        account.setCode(request.code());
        account.setName(request.name());
        account.setType(request.type());

        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new BadRequestException("Uma conta contábil não pode ser pai de si mesma.");
            }
            ChartOfAccount parent = chartOfAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.parentId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("ChartOfAccount", "parentId", request.parentId()));
            account.setParent(parent);
            account.setLevel(parent.getLevel() + 1);
        } else {
            account.setParent(null);
            account.setLevel(1);
        }

        account = chartOfAccountRepository.save(account);
        return toFlatResponse(account);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("ChartOfAccount", "id", id));

        // Check if has active children
        boolean hasChildren = account.getChildren().stream().anyMatch(c -> c.getDeletedAt() == null);
        if (hasChildren) {
            throw new BadRequestException("Não é possível excluir uma conta contábil que possui subcontas vinculadas.");
        }

        account.setDeletedAt(java.time.LocalDateTime.now());
        chartOfAccountRepository.save(account);
    }

    @Transactional
    public void importCsv(UUID organizationId, InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header: code;name;type;parentCode
                }

                String[] parts = line.split(";");
                if (parts.length < 3) continue;

                String code = parts[0].trim();
                String name = parts[1].trim();
                ChartOfAccountType type = ChartOfAccountType.valueOf(parts[2].trim().toUpperCase());
                String parentCode = parts.length > 3 ? parts[3].trim() : "";

                Optional<ChartOfAccount> existing = chartOfAccountRepository.findByOrganization_IdAndCodeAndDeletedAtIsNull(organizationId, code);
                if (existing.isPresent()) {
                    // Update name/type if exists
                    ChartOfAccount acc = existing.get();
                    acc.setName(name);
                    acc.setType(type);
                    chartOfAccountRepository.save(acc);
                    continue;
                }

                ChartOfAccount account = new ChartOfAccount();
                account.setCode(code);
                account.setName(name);
                account.setType(type);

                if (!parentCode.isEmpty()) {
                    ChartOfAccount parent = chartOfAccountRepository.findByOrganization_IdAndCodeAndDeletedAtIsNull(organizationId, parentCode)
                            .orElseThrow(() -> new BadRequestException("Conta contábil pai com código " + parentCode + " não encontrada ao importar " + code));
                    account.setParent(parent);
                    account.setLevel(parent.getLevel() + 1);
                } else {
                    account.setParent(null);
                    account.setLevel(1);
                }

                chartOfAccountRepository.save(account);
            }
        } catch (Exception e) {
            throw new BadRequestException("Erro ao processar importação de arquivo CSV: " + e.getMessage());
        }
    }

    private ChartOfAccountResponse toFlatResponse(ChartOfAccount account) {
        return new ChartOfAccountResponse(
                account.getId(),
                account.getCode(),
                account.getName(),
                account.getType(),
                account.getParent() != null ? account.getParent().getId() : null,
                account.getParent() != null ? account.getParent().getName() : null,
                account.getLevel(),
                new ArrayList<>()
        );
    }

    private ChartOfAccountResponse toTreeResponse(ChartOfAccount account) {
        List<ChartOfAccountResponse> children = account.getChildren().stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(this::toTreeResponse)
                .collect(Collectors.toList());

        return new ChartOfAccountResponse(
                account.getId(),
                account.getCode(),
                account.getName(),
                account.getType(),
                account.getParent() != null ? account.getParent().getId() : null,
                account.getParent() != null ? account.getParent().getName() : null,
                account.getLevel(),
                children
        );
    }
}
