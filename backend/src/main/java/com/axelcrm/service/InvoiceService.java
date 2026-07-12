package com.axelcrm.service;

import com.axelcrm.dto.InvoiceRequest;
import com.axelcrm.dto.InvoiceResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Contract;
import com.axelcrm.entity.Invoice;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ContractRepository;
import com.axelcrm.repository.InvoiceRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Element;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;

    public Page<InvoiceResponse> findAll(UUID organizationId, Pageable pageable) {
        return invoiceRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public InvoiceResponse findById(UUID organizationId, UUID id) {
        return invoiceRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    @Transactional
    public InvoiceResponse create(UUID organizationId, InvoiceRequest request) {
        Client client = clientRepository.findByIdAndOrganization_Id(
                request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(request.invoiceNumber());
        invoice.setClient(client);
        invoice.setIssueDate(request.issueDate());
        invoice.setDueDate(request.dueDate());
        invoice.setPaidDate(request.paidDate());
        invoice.setStatus(request.status() != null ? request.status() : "DRAFT");
        invoice.setSubtotal(request.subtotal() != null ? request.subtotal() : java.math.BigDecimal.ZERO);
        invoice.setTaxAmount(request.taxAmount());
        invoice.setDiscountAmount(request.discountAmount());
        invoice.setTotal(request.total() != null ? request.total() : java.math.BigDecimal.ZERO);
        invoice.setNotes(request.notes());
        invoice.setPaymentMethod(request.paymentMethod());
        invoice.setPaidAmount(request.paidAmount());

        if (request.contractId() != null) {
            Contract contract = contractRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(
                    request.contractId(), organizationId)
                    .orElse(null);
            invoice.setContract(contract);
        }

        invoice = invoiceRepository.save(invoice);
        return toResponse(invoice);
    }

    @Transactional
    public InvoiceResponse update(UUID organizationId, UUID id, InvoiceRequest request) {
        Invoice invoice = invoiceRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        Client client = clientRepository.findByIdAndOrganization_Id(
                request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        invoice.setInvoiceNumber(request.invoiceNumber());
        invoice.setClient(client);
        invoice.setIssueDate(request.issueDate());
        invoice.setDueDate(request.dueDate());
        invoice.setPaidDate(request.paidDate());
        invoice.setStatus(request.status() != null ? request.status() : "DRAFT");
        invoice.setSubtotal(request.subtotal() != null ? request.subtotal() : java.math.BigDecimal.ZERO);
        invoice.setTaxAmount(request.taxAmount());
        invoice.setDiscountAmount(request.discountAmount());
        invoice.setTotal(request.total() != null ? request.total() : java.math.BigDecimal.ZERO);
        invoice.setNotes(request.notes());
        invoice.setPaymentMethod(request.paymentMethod());
        invoice.setPaidAmount(request.paidAmount());

        if (request.contractId() != null) {
            Contract contract = contractRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(
                    request.contractId(), organizationId)
                    .orElse(null);
            invoice.setContract(contract);
        } else {
            invoice.setContract(null);
        }

        invoice = invoiceRepository.save(invoice);
        return toResponse(invoice);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Invoice invoice = invoiceRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        invoice.setDeletedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getClient().getId(),
                invoice.getClient().getName(),
                invoice.getContract() != null ? invoice.getContract().getId() : null,
                invoice.getContract() != null ? invoice.getContract().getTitle() : null,
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getPaidDate(),
                invoice.getStatus(),
                invoice.getSubtotal(),
                invoice.getTaxAmount(),
                invoice.getDiscountAmount(),
                invoice.getTotal(),
                invoice.getNotes(),
                invoice.getPaymentMethod(),
                invoice.getPaidAmount(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }

    public byte[] generateInvoicePdf(UUID organizationId, UUID id) {
        Invoice invoice = invoiceRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(18, 52, 153));
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(71, 85, 105));
            Font boldTextFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(15, 23, 42));
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(71, 85, 105));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

            Paragraph title = new Paragraph("FATURA " + invoice.getInvoiceNumber(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph info = new Paragraph();
            info.setFont(textFont);
            info.add(new Phrase("Cliente: ", boldTextFont));
            info.add(invoice.getClient().getName() + "\n");
            if (invoice.getContract() != null) {
                info.add(new Phrase("Contrato: ", boldTextFont));
                info.add(invoice.getContract().getTitle() + "\n");
            }
            info.add(new Phrase("Status: ", boldTextFont));
            info.add(translateStatus(invoice.getStatus()) + "\n");
            info.add(new Phrase("Data de Emissão: ", boldTextFont));
            info.add(invoice.getIssueDate().toString().substring(0, 10) + "\n");
            info.add(new Phrase("Data de Vencimento: ", boldTextFont));
            info.add(invoice.getDueDate().toString().substring(0, 10) + "\n");
            if (invoice.getPaidDate() != null) {
                info.add(new Phrase("Data de Pagamento: ", boldTextFont));
                info.add(invoice.getPaidDate().toString().substring(0, 10) + "\n");
            }
            if (invoice.getPaymentMethod() != null) {
                info.add(new Phrase("Método de Pagamento: ", boldTextFont));
                info.add(invoice.getPaymentMethod() + "\n");
            }
            info.setSpacingAfter(20);
            document.add(info);

            Paragraph detailHeader = new Paragraph("Detalhamento Financeiro", subTitleFont);
            detailHeader.setSpacingAfter(8);
            document.add(detailHeader);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingAfter(20);

            Color navy = new Color(18, 52, 153);
            table.addCell(createCell("Descrição", headerFont, navy, true));
            table.addCell(createCell("Valor", headerFont, navy, true));

            table.addCell(createCell("Subtotal", textFont, Color.WHITE, true));
            table.addCell(createCell(formatCurrencyBRL(invoice.getSubtotal()), textFont, Color.WHITE, true));

            table.addCell(createCell("Imposto", textFont, Color.WHITE, true));
            table.addCell(createCell(formatCurrencyBRL(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO), textFont, Color.WHITE, true));

            table.addCell(createCell("Desconto", textFont, Color.WHITE, true));
            table.addCell(createCell(formatCurrencyBRL(invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO), textFont, Color.WHITE, true));

            table.addCell(createCell("Total", boldTextFont, Color.WHITE, true));
            table.addCell(createCell(formatCurrencyBRL(invoice.getTotal()), boldTextFont, Color.WHITE, true));

            if (invoice.getPaidAmount() != null) {
                table.addCell(createCell("Valor Pago", boldTextFont, Color.WHITE, true));
                table.addCell(createCell(formatCurrencyBRL(invoice.getPaidAmount()), boldTextFont, Color.WHITE, true));
            }

            document.add(table);

            if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
                Paragraph notesHeader = new Paragraph("Observações", subTitleFont);
                notesHeader.setSpacingAfter(8);
                document.add(notesHeader);

                Paragraph notes = new Paragraph(invoice.getNotes(), textFont);
                document.add(notes);
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da fatura", e);
        }
    }

    public byte[] generateInvoicesReportPdf(UUID organizationId) {
        List<Invoice> invoices = invoiceRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(18, 52, 153));
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(71, 85, 105));
            Font boldTextFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(15, 23, 42));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

            Paragraph title = new Paragraph("Relatório Geral de Faturamento", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(25);
            document.add(title);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f});
            table.setSpacingAfter(20);

            Color navy = new Color(18, 52, 153);
            table.addCell(createCell("Nº Fatura", headerFont, navy, true));
            table.addCell(createCell("Cliente", headerFont, navy, true));
            table.addCell(createCell("Emissão", headerFont, navy, true));
            table.addCell(createCell("Vencimento", headerFont, navy, true));
            table.addCell(createCell("Valor", headerFont, navy, true));
            table.addCell(createCell("Status", headerFont, navy, true));

            BigDecimal grandTotal = BigDecimal.ZERO;
            BigDecimal paidTotal = BigDecimal.ZERO;

            for (Invoice invoice : invoices) {
                table.addCell(createCell(invoice.getInvoiceNumber(), textFont, Color.WHITE, true));
                table.addCell(createCell(invoice.getClient().getName(), textFont, Color.WHITE, true));
                table.addCell(createCell(invoice.getIssueDate().toString().substring(0, 10), textFont, Color.WHITE, true));
                table.addCell(createCell(invoice.getDueDate().toString().substring(0, 10), textFont, Color.WHITE, true));
                table.addCell(createCell(formatCurrencyBRL(invoice.getTotal()), textFont, Color.WHITE, true));
                table.addCell(createCell(translateStatus(invoice.getStatus()), textFont, Color.WHITE, true));

                grandTotal = grandTotal.add(invoice.getTotal());
                if ("PAID".equals(invoice.getStatus())) {
                    paidTotal = paidTotal.add(invoice.getTotal());
                }
            }

            document.add(table);

            Paragraph summary = new Paragraph();
            summary.setFont(textFont);
            summary.add(new Phrase("Faturamento Total Geral: ", boldTextFont));
            summary.add(formatCurrencyBRL(grandTotal) + "\n");
            summary.add(new Phrase("Total Recebido (Pagas): ", boldTextFont));
            summary.add(formatCurrencyBRL(paidTotal) + "\n");
            document.add(summary);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de faturamento", e);
        }
    }

    private PdfPCell createCell(String text, Font font, Color bgColor, boolean border) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        if (!border) {
            cell.setBorder(PdfPCell.NO_BORDER);
        } else {
            cell.setBorderColor(new Color(226, 232, 240));
        }
        return cell;
    }

    private String formatCurrencyBRL(BigDecimal value) {
        if (value == null) return "R$ 0,00";
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        return nf.format(value);
    }

    private String translateStatus(String status) {
        if (status == null) return "Rascunho";
        return switch (status) {
            case "ISSUED" -> "Emitida";
            case "PAID" -> "Paga";
            case "OVERDUE" -> "Vencida";
            case "CANCELLED" -> "Cancelada";
            default -> "Rascunho";
        };
    }
}
