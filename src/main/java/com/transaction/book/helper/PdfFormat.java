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
import com.transaction.book.dto.responseDTO.CusotomerFullResponse;
import com.transaction.book.dto.responseDTO.TransactionResponse;
import com.transaction.book.entities.Remainder;
import com.transaction.book.services.serviceImpl.CustomerServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class PdfFormat {

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    public byte[] generateTransactionStatement(List<TransactionResponse> transactions) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a | dd MMM ''yy");
        String formattedDateTime = now.format(formatter);

        CusotomerFullResponse customer = this.customerServiceImpl
                .getCustomerResponseByName(transactions.get(0).getCustomerName());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            Paragraph title = new Paragraph(transactions.get(0).getCustomerName() + " Statement\n")
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Ensure the list is not empty before accessing elements
            if (!transactions.isEmpty()) {
                String startDate = formatedDate(transactions.get(0).getDate());
                String endDate = formatedDate(transactions.get(transactions.size() - 1).getDate());

                Paragraph subtitle = new Paragraph(
                        "Phone Number :-" + customer.getMobileNo() + "\n( " + startDate + " - " + endDate + " )")
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER);

                document.add(subtitle);
            }

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

            addSummaryCell(summaryTable, "₹" + totalDebit, true);
            addSummaryCell(summaryTable, "₹" + totalCredit, false);
            if (totalCredit + totalDebit < 0) {
                addSummaryCell(summaryTable, "₹" + (totalCredit + totalDebit), true);
            } else if (totalCredit + totalDebit > 0) {
                addSummaryCell(summaryTable, "₹" + (totalCredit + totalDebit), false);
            } else {
                addSummaryCell(summaryTable, "₹" + (totalCredit + totalDebit));
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
                addTransactionRow(transactionTable, formatedDate(transaction.getDate()), transaction.getDetail(), debit,
                        credit,
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
        Cell cell = new Cell().add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
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

        table.addCell(
                new Cell().add(new Paragraph(detail != null ? detail : " ")).setTextAlignment(TextAlignment.CENTER));

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

    public static String formatedDate(String str) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(str);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
            return outputFormat.format(date);
        } catch (Exception e) {
            return " ";
        }
    }




    public byte[] generateCustomerRemainderPdf(List<Remainder> remainders, CusotomerFullResponse customer) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a | dd MMM ''yy");
        String formattedDateTime = now.format(formatter);
    
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
    
            // Title
            Paragraph title = new Paragraph("Customer Failed Remainder Statement\n")
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
    
            // Customer Information Section
            document.add(new Paragraph("Customer Details:")
                    .setFontSize(14)
                    .setMarginBottom(5));
    
            Table customerTable = new Table(new float[]{100F, 300F});
            customerTable.setWidth(UnitValue.createPercentValue(100));
    
            customerTable.addCell(createBoldCell("Name:"));
            customerTable.addCell(new Cell().add(new Paragraph(customer.getName())));
    
            customerTable.addCell(createBoldCell("Mobile No:"));
            customerTable.addCell(new Cell().add(new Paragraph(customer.getMobileNo())));
    
            customerTable.addCell(createBoldCell("GSTIN:"));
            customerTable.addCell(new Cell().add(new Paragraph(customer.getGstinNo())));
    
            customerTable.addCell(createBoldCell("Outstanding Amount:"));
            customerTable.addCell(new Cell().add(new Paragraph(String.valueOf(customer.getAmount()))));
    
            document.add(customerTable);
            document.add(new Paragraph("\n")); // Space before remainder table
    
            // Ensure the list is not empty before accessing elements
            if (!remainders.isEmpty()) {
                String startDate = remainders.get(0).getDueDate().toString();
                String endDate = remainders.get(remainders.size() - 1).getDueDate().toString();
    
                Paragraph subtitle = new Paragraph("Remainders from " + endDate + " to " + startDate) // Corrected order
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(subtitle);
            }
    
            document.add(new Paragraph("\n"));
    
            // Table Header (Without Remainder ID)
            float[] columnWidths = {150F, 250F}; // Adjusted for two columns
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));
    
            addHeaderCell(table, "Due Date");
            addHeaderCell(table, "Reason");
    
            // Add Remainder Data (Without ID & Settled)
            for (Remainder remainder : remainders) {
                addRemainderRow(table,
                        remainder.getDueDate().toString(),
                        remainder.getReason() != null ? remainder.getReason() : "-");
            }
    
            document.add(table);
            document.add(new Paragraph("\n"));
    
            // Footer
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
    
    
                
                        
                private Cell createBoldCell(String text) {
                    return new Cell().add(new Paragraph(text));
                }
                           
            
                private void addRemainderRow(Table table, String dueDate, String reason) {
                    table.addCell(new Cell().add(new Paragraph(dueDate).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(reason).setTextAlignment(TextAlignment.LEFT)));
                }
                
            

}
