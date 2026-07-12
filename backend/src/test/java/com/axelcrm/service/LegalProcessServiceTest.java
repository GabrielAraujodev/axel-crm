package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;

import com.axelcrm.commons.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegalProcessServiceTest {

    private final LegalProcessService service = new LegalProcessService(null);

    @Test
    void validateCnj_ShouldAcceptValidFormatAndCheckDigits() {
        service.validateCnj("0000001-05.0000.0.01.0000");
    }

    @Test
    void validateCnj_ShouldRejectInvalidFormat() {
        assertThrows(BadRequestException.class, () -> service.validateCnj("123"));
    }

    @Test
    void validateCnj_ShouldRejectInvalidCheckDigits() {
        assertThrows(BadRequestException.class, () -> service.validateCnj("0000001-99.0000.0.01.0000"));
    }

    @Test
    void validateCnj_ShouldRejectNull() {
        assertThrows(BadRequestException.class, () -> service.validateCnj(null));
    }

    @Test
    void validateCnj_ShouldAcceptRealisticValidCnj() {
        // 0000016-40.2026.4.03.0001 — calculated with correct módulo-11
        service.validateCnj("0000016-40.2026.4.03.0001");
    }
}
