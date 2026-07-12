package com.axelcrm.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class FlywayMigrationConfig implements CommandLineRunner {
    private final DataSource dataSource;

    public FlywayMigrationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Running Flyway Migration Programmatically (with Repair) ---");
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load();
        flyway.repair();
        flyway.migrate();
        System.out.println("--- Flyway Migration Completed ---");
    }
}
