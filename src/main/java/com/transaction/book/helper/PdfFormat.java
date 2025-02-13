package com.transaction.book.helper;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.transaction.book.dto.responseDTO.TransactionResponse;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfFormat {

    public byte[] generateTransactionStatement(List<TransactionResponse> transactions) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a | dd MMM ''yy");
        String formattedDateTime = now.format(formatter);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            Paragraph title = new Paragraph(transactions.get(0).getCustomerName())
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Paragraph subtitle = new Paragraph("\n")
            // .setFontSize(12)
            // .setTextAlignment(TextAlignment.CENTER);
            // document.add(subtitle);

            document.add(new Paragraph("\n"));

            float[] summaryColumnWidths = { 150F, 150F, 150F };
            Table summaryTable = new Table(summaryColumnWidths);
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(summaryTable, "Total Debit(-)");
            addHeaderCell(summaryTable, "Total Credit(+)");
            addHeaderCell(summaryTable, "Net Balance");

            double totalCredit = 0.0;
            double totalDebit = 0.0;
            for (TransactionResponse transaction : transactions) {
                if (transaction.getAmount() > 0) {
                    totalCredit = totalCredit + transaction.getAmount();
                } else {
                    totalDebit = totalDebit + transaction.getAmount();
                }
            }

            addSummaryCell(summaryTable, "₹" + totalDebit);
            addSummaryCell(summaryTable, "₹" + totalCredit);
            if (totalCredit + totalDebit > 0) {
                addSummaryCell(summaryTable, "₹" + (totalCredit + totalDebit), false);
            } else {
                addSummaryCell(summaryTable, "₹" + (totalCredit + totalDebit), true);
            }

            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            float[] transactionColumnWidths = { 100F, 150F, 150F, 150F, 150F };
            Table transactionTable = new Table(transactionColumnWidths);
            transactionTable.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(transactionTable, "Date");
            addHeaderCell(transactionTable, "Detail");
            addHeaderCell(transactionTable, "Debit(-)");
            addHeaderCell(transactionTable, "Credit(+)");
            addHeaderCell(transactionTable, "Balance");

            for (TransactionResponse transaction : transactions) {
                String credit = "";
                String debit = "";
                if (transaction.getAmount() > 0) {
                    credit = String.valueOf(transaction.getAmount());
                } else {
                    debit = String.valueOf(transaction.getAmount());
                }
                addTransactionRow(transactionTable, transaction.getDate(), transaction.getDetail(), debit, credit,
                        String.valueOf(transaction.getBalanceAmount()));
            }

            document.add(transactionTable);
            document.add(new Paragraph("\n"));

            Paragraph footer = new Paragraph("Report Generated: " + formattedDateTime)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addHeaderCell(Table table, String text) {
        table.addCell(new Cell().add(new Paragraph(text))
                .setBackgroundColor(new DeviceRgb(220, 220, 0))
                .setTextAlignment(TextAlignment.CENTER));
    }

    private void addSummaryCell(Table table, String text) {
        addSummaryCell(table, text, false);
    }

    private void addSummaryCell(Table table, String text, boolean isRed) {
        Cell cell = new Cell().add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER);
        if (isRed) {
            cell.setFontColor(new DeviceRgb(255, 0, 0));
        } else {
            cell.setFontColor(new DeviceRgb(0, 255, 0));
        }
        table.addCell(cell);
    }

    // Updated method to set green background for Credit column and red for Debit
    // column
    private void addTransactionRow(Table table, String date, String detail, String debit, String credit,
            String balance) {
        table.addCell(new Cell().add(new Paragraph(date != null ? date : " ")).setTextAlignment(TextAlignment.CENTER));

        table.addCell(new Cell().add(new Paragraph(detail != null ? detail : " ")).setTextAlignment(TextAlignment.CENTER));

        // Debit column with red background
        Cell debitCell = new Cell().add(new Paragraph(debit)).setTextAlignment(TextAlignment.RIGHT);
        if (!debit.isEmpty()) {
            debitCell.setBackgroundColor(new DeviceRgb(255, 182, 193)); // Light red (pinkish)
        }
        table.addCell(debitCell);

        // Credit column with green background
        Cell creditCell = new Cell().add(new Paragraph(credit)).setTextAlignment(TextAlignment.RIGHT);
        if (!credit.isEmpty()) {
            creditCell.setBackgroundColor(new DeviceRgb(144, 238, 144)); // Light green
        }
        table.addCell(creditCell);

        table.addCell(new Cell().add(new Paragraph(balance)).setTextAlignment(TextAlignment.RIGHT));
    }
}
