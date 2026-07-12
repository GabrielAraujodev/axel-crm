package com.axelcrm.service;

import com.axelcrm.dto.LegalProcessRequest;
import com.axelcrm.dto.LegalProcessResponse;
import com.axelcrm.entity.LegalProcess;
import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.LegalProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class LegalProcessService {

    private final LegalProcessRepository legalProcessRepository;

    private static final Pattern CNJ_PATTERN = Pattern.compile("^\\d{7}-\\d{2}\\.\\d{4}\\.\\d\\.\\d{2}\\.\\d{4}$");

    public void validateCnj(String cnjNumber) {
        if (cnjNumber == null || !CNJ_PATTERN.matcher(cnjNumber).matches()) {
            throw new BadRequestException("Número de processo CNJ inválido. O formato deve ser NNNNNNN-DD.AAAA.J.TR.OOOO");
        }

        // Validate check digits (Resolução CNJ 65/2008, Art. 2, §2º — módulo 11)
        String digits = cnjNumber.replaceAll("\\D", "");
        String base = digits.substring(0, 7) + digits.substring(9, 16); // NNNNNNN + AAAA + J + TR = 14 digits
        String informedDv = digits.substring(7, 9);

        int dv1 = computeCheckDigit(base);
        int dv2 = computeCheckDigit(base + dv1);

        if (!informedDv.equals(dv1 + "" + dv2)) {
            throw new BadRequestException("Dígito verificador do CNJ inválido. O número informado não é um CNJ válido.");
        }
    }

    private static int computeCheckDigit(String base) {
        int sum = 0;
        int weight = 9;
        for (int i = base.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(base.charAt(i)) * weight;
            weight--;
            if (weight < 2) weight = 9;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    public Page<LegalProcessResponse> findAll(UUID organizationId, Pageable pageable) {
        return legalProcessRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public LegalProcessResponse findById(UUID organizationId, UUID id) {
        return legalProcessRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("LegalProcess", "id", id));
    }

    public LegalProcessResponse searchDataJud(String cnjNumber) {
        validateCnj(cnjNumber);

        // Simulated/mocked DataJud integration
        // Extracting year and court details from CNJ for slightly dynamic mock data
        String[] parts = cnjNumber.split("\\.");
        String yearStr = parts[0].substring(parts[0].length() - 4);
        int year = Integer.parseInt(yearStr);
        String courtType = parts[1]; // 'J' parameter

        String courtName = "Tribunal de Justiça Estadual (TJSP)";
        if ("4".equals(courtType)) {
            courtName = "Tribunal Regional Federal (TRF-3)";
        } else if ("5".equals(courtType)) {
            courtName = "Tribunal Regional do Trabalho (TRT-2)";
        }

        BigDecimal value = BigDecimal.valueOf(150000.00 + (year * 100));

        return new LegalProcessResponse(
                null,
                cnjNumber,
                courtName,
                LocalDate.of(year, 3, 15),
                value,
                "EM_ANDAMENTO",
                "Processo judicial consultado automaticamente via integração DataJud (Mock).",
                null, null
        );
    }

    @Transactional
    public LegalProcessResponse create(UUID organizationId, LegalProcessRequest request) {
        validateCnj(request.cnjNumber());

        if (legalProcessRepository.findByOrganization_IdAndCnjNumberAndDeletedAtIsNull(organizationId, request.cnjNumber()).isPresent()) {
            throw new BadRequestException("Já existe um processo judicial cadastrado com o número " + request.cnjNumber());
        }

        LegalProcess process = new LegalProcess();
        process.setCnjNumber(request.cnjNumber());
        process.setCourt(request.court());
        process.setDistributionDate(request.distributionDate());
        process.setValue(request.value() != null ? request.value() : BigDecimal.ZERO);
        process.setStatus(request.status());
        process.setDescription(request.description());

        process = legalProcessRepository.save(process);
        return toResponse(process);
    }

    @Transactional
    public LegalProcessResponse update(UUID organizationId, UUID id, LegalProcessRequest request) {
        validateCnj(request.cnjNumber());

        LegalProcess process = legalProcessRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("LegalProcess", "id", id));

        if (!process.getCnjNumber().equals(request.cnjNumber())) {
            if (legalProcessRepository.findByOrganization_IdAndCnjNumberAndDeletedAtIsNull(organizationId, request.cnjNumber()).isPresent()) {
                throw new BadRequestException("Já existe um processo judicial cadastrado com o número " + request.cnjNumber());
            }
        }

        process.setCnjNumber(request.cnjNumber());
        process.setCourt(request.court());
        process.setDistributionDate(request.distributionDate());
        process.setValue(request.value() != null ? request.value() : BigDecimal.ZERO);
        process.setStatus(request.status());
        process.setDescription(request.description());

        process = legalProcessRepository.save(process);
        return toResponse(process);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        LegalProcess process = legalProcessRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("LegalProcess", "id", id));
        process.setDeletedAt(java.time.LocalDateTime.now());
        legalProcessRepository.save(process);
    }

    private LegalProcessResponse toResponse(LegalProcess process) {
        return new LegalProcessResponse(
                process.getId(),
                process.getCnjNumber(),
                process.getCourt(),
                process.getDistributionDate(),
                process.getValue(),
                process.getStatus(),
                process.getDescription(),
                process.getCreatedAt(),
                process.getUpdatedAt()
        );
    }
}
