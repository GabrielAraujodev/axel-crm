package com.axelcrm.config;

import com.axelcrm.auth.security.TenantContext;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    protected static final UUID ORG_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    protected static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUpTenant() {
        TenantContext.setOrganizationId(ORG_ID);
    }

    @AfterEach
    void tearDownTenant() {
        TenantContext.clear();
    }
}
