package com.axelcrm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.axelcrm.auth.entity.User;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.auth.repository.OrganizationRepository;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.commons.entity.enums.Role;
import java.util.UUID;

/**
 * Main entry point for the Axel CRM application.
 */
@SpringBootApplication
public class AxelCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(AxelCrmApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder,
            org.springframework.jdbc.core.JdbcTemplate jdbc) {
        return args -> {
            try {
                Organization org = organizationRepository.findById(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"))
                    .orElseGet(() -> {
                        Organization newOrg = new Organization();
                        newOrg.setId(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"));
                        newOrg.setName("Axel Empreendimentos");
                        newOrg.setDomain("axelcrm.com");
                        newOrg.setActive(true);
                        return organizationRepository.save(newOrg);
                    });

                userRepository.findByEmail("admin@axelcrm.com").ifPresentOrElse(
                    user -> {
                        user.setPassword(passwordEncoder.encode("admin123"));
                        user.setActive(true);
                        userRepository.save(user);
                        System.out.println("User admin@axelcrm.com password reset successfully!");
                    },
                    () -> {
                        User user = new User();
                        user.setId(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12"));
                        user.setOrganization(org);
                        user.setEmail("admin@axelcrm.com");
                        user.setPassword(passwordEncoder.encode("admin123"));
                        user.setName("Administrador");
                        user.setRole(Role.SUPER_ADMIN);
                        user.setActive(true);
                        userRepository.save(user);
                        System.out.println("User admin@axelcrm.com created successfully!");
                    }
                );

                userRepository.findByEmail("Gabrielalves6p@gmail.com").ifPresentOrElse(
                    user -> {
                        user.setPassword(passwordEncoder.encode("admin123"));
                        user.setActive(true);
                        userRepository.save(user);
                        System.out.println("User Gabrielalves6p@gmail.com password reset successfully!");
                    },
                    () -> {
                        User user = new User();
                        user.setId(UUID.fromString("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12"));
                        user.setOrganization(org);
                        user.setEmail("Gabrielalves6p@gmail.com");
                        user.setPassword(passwordEncoder.encode("admin123"));
                        user.setName("GABRIEL ATY");
                        user.setRole(Role.SUPER_ADMIN);
                        user.setActive(true);
                        userRepository.save(user);
                        System.out.println("User Gabrielalves6p@gmail.com created successfully!");
                    }
                );

                // Populate missing mock data areas: Leads, Prospects, Contacts, Products, Contracts, Invoices, Documents
                System.out.println("Populating missing database areas with mock data...");

                // Legacy Data Cleanup: Update any records with invalid string values in Enum columns
                jdbc.execute("UPDATE leads SET source = 'REFERRAL' WHERE source = 'OUTBOUND' OR source = 'INBOUND'");
                jdbc.execute("UPDATE prospects SET stage = 'PROSPECTING' WHERE stage = 'NOVO'");
                jdbc.execute("UPDATE prospects SET stage = 'CONTACTED' WHERE stage = 'EM_CONTATO'");
                jdbc.execute("UPDATE prospects SET source = 'REFERRAL' WHERE source = 'INDICAÇÃO'");
                jdbc.execute("UPDATE prospects SET source = 'SOCIAL_MEDIA' WHERE source = 'GOOGLE'");

                // 1. Leads
                jdbc.execute("""
                    INSERT INTO leads (id, organization_id, name, email, phone, company, position, source, stage, estimated_value, notes, assigned_to, converted, created_at)
                    VALUES 
                        ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Ana Souza', 'ana@techsolutions.com', '11987654321', 'Tech Solutions', 'Diretora de TI', 'REFERRAL', 'QUALIFIED', 12000.00, 'Interesse no sistema de CRM integrado', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', false, NOW()),
                        ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a02', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Carlos Lima', 'carlos@limaconsulting.com', '21976543210', 'Lima Consulting', 'CEO', 'WEBSITE', 'CONTACTED', 8000.00, 'Preencheu formulário de contato do site', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', false, NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 2. Prospects
                jdbc.execute("""
                    INSERT INTO prospects (id, organization_id, name, email, phone, company, source, stage, notes, created_at, updated_at)
                    VALUES
                        ('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380b01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Roberto Santos', 'roberto@vendasinova.com.br', '31965432109', 'Inova Vendas', 'REFERRAL', 'PROSPECTING', 'Lead potencial indicado pelo parceiro Gabriel.', NOW(), NOW()),
                        ('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380b02', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Juliana Costa', 'juliana@marketingdigital.com', '11954321098', 'MD Agência', 'SOCIAL_MEDIA', 'CONTACTED', 'Interessada em automação de e-mails para campanhas.', NOW(), NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 3. Contacts (referencing Gabriel client ID)
                jdbc.execute("""
                    INSERT INTO contacts (id, organization_id, client_id, name, email, phone, position, created_at)
                    VALUES
                        ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380c01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2a572c2f-e127-fae2-62b9-3c4b04e826c2', 'Pedro Rocha', 'pedro@gaty.com', '62988887777', 'Gerente de Contratos', NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 4. Products
                jdbc.execute("""
                    INSERT INTO products (id, organization_id, name, description, sku, category, unit_price, cost_price, unit, is_active, notes, created_at)
                    VALUES
                        ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380d01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Licença Mensal SaaS', 'Acesso completo à plataforma de CRM por usuário', 'SKU-LIC-MNS', 'Software', 99.90, 15.00, 'User/Month', true, 'Preço padrão do plano Team', NOW()),
                        ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380d02', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Consultoria de Implantação', 'Pacote de 20 horas de consultoria técnica e treinamento', 'SKU-CONS-IMP', 'Serviço', 3500.00, 1000.00, 'Pacote', true, 'Entregue em até 30 dias', NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 5. Contracts
                jdbc.execute("""
                    INSERT INTO contracts (id, organization_id, title, contract_number, description, client_id, deal_id, start_date, end_date, value, monthly_value, status, terms, auto_renew, created_at)
                    VALUES
                        ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380e01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Contrato de Licenciamento de Software', 'CONTR-2026-001', 'Fornecimento de licenças de software Axel CRM', '2a572c2f-e127-fae2-62b9-3c4b04e826c2', NULL, CURRENT_DATE - 10, CURRENT_DATE + 355, 12000.00, 1000.00, 'ACTIVE', 'Termos padrão de uso de software SaaS.', true, NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 6. Invoices
                jdbc.execute("""
                    INSERT INTO invoices (id, organization_id, invoice_number, client_id, contract_id, issue_date, due_date, paid_date, status, subtotal, tax_amount, discount_amount, total, payment_method, paid_amount, created_at)
                    VALUES
                        ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380f01', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'FAT-2026-001', '2a572c2f-e127-fae2-62b9-3c4b04e826c2', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380e01', CURRENT_DATE - 10, CURRENT_DATE + 20, NULL, 'SENT', 1000.00, 50.00, 0.00, 1050.00, 'BOLETO', 0.00, NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 7. Documents
                jdbc.execute("""
                    INSERT INTO documents (id, organization_id, name, description, category, tags, file_name, file_type, file_size, file_url, client_id, project_id, document_date, is_archived, created_at)
                    VALUES
                        ('90eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Manual do Usuário Axel CRM', 'Documento de auxílio no onboarding de novos usuários', 'Manuais', 'onboarding,manual,pdf', 'manual_axel_crm.pdf', 'application/pdf', 2048000, 'https://storage.axelcrm.com/manual_axel_crm.pdf', '2a572c2f-e127-fae2-62b9-3c4b04e826c2', NULL, CURRENT_DATE, false, NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 8. Proposals
                jdbc.execute("""
                    INSERT INTO proposals (id, organization_id, client_id, lead_id, title, description, status, total_value, valid_until, notes, created_by, created_at)
                    VALUES
                        ('10eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2a572c2f-e127-fae2-62b9-3c4b04e826c2', NULL, 'Proposta de Licenciamento Axel CRM - Gaty', 'Licenciamento de software Axel CRM e implantação dos módulos.', 'SENT', 15500.00, CURRENT_DATE + 30, 'Validade de 30 dias para fechamento comercial.', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                // 9. Proposal Items
                jdbc.execute("""
                    INSERT INTO proposal_items (id, organization_id, proposal_id, description, quantity, unit_price, total_price, created_at)
                    VALUES
                        ('11eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '10eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'Licença Mensal SaaS', 120, 100.00, 12000.00, NOW()),
                        ('11eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '10eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'Serviço de Implantação e Treinamento', 1, 3500.00, 3500.00, NOW())
                    ON CONFLICT (id) DO NOTHING
                """);

                System.out.println("Mock data populated successfully in all database areas!");
            } catch (Exception e) {
                System.err.println("Error initializing default database users: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
