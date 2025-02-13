package com.transaction.book.helper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.transaction.book.dto.responseDTO.TransactionResponse;

public class ExcelFormat {

    public static byte[] generateExcel(List<TransactionResponse> transactions) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        // Header Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Sr No", "Date", "Detail", "Customer Name", "Debit(-)", "Credit(+)"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            cell.setCellStyle(headerStyle);
        }

        // Data Rows
        int rowIndex = 1;
        double totalDebit = 0;
        double totalCredit = 0;

        for (TransactionResponse transaction : transactions) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(rowIndex);
            row.createCell(1).setCellValue(transaction.getDate() != null ? transaction.getDate() : " ");
            row.createCell(2).setCellValue(transaction.getDetail() != null ? transaction.getDetail() : " ");
            row.createCell(3).setCellValue(transaction.getCustomerName() != null ? transaction.getCustomerName() : " ");

            if (transaction.getAmount() < 0) {
                row.createCell(4).setCellValue(Math.abs(transaction.getAmount())); // Debit Column
                totalDebit += Math.abs(transaction.getAmount());
                row.createCell(5).setCellValue(0); // Credit Column
            } else {
                row.createCell(4).setCellValue(0); // Debit Column
                totalCredit += transaction.getAmount();
                row.createCell(5).setCellValue(transaction.getAmount()); // Credit Column
            }

            rowIndex++;
        }

        // Add an empty row for spacing
        rowIndex++;
        sheet.createRow(rowIndex);

        // Grand Total Row
        rowIndex++;
        Row totalRow = sheet.createRow(rowIndex);
        CellStyle totalStyle = workbook.createCellStyle();
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);
        totalStyle.setAlignment(HorizontalAlignment.CENTER);

        // Merge cells (0 to 3) for "Grand Total"
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));

        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Grand Total");
        totalCell.setCellStyle(totalStyle);

        Cell totalDebitCell = totalRow.createCell(4);
        totalDebitCell.setCellValue(totalDebit);
        totalDebitCell.setCellStyle(totalStyle);

        Cell totalCreditCell = totalRow.createCell(5);
        totalCreditCell.setCellValue(totalCredit);
        totalCreditCell.setCellStyle(totalStyle);

        // Add another empty row for spacing before the balance summary
        rowIndex++;
        sheet.createRow(rowIndex);

        // Balance Summary Row
        rowIndex++;
        Row balanceRow = sheet.createRow(rowIndex);
        double totalBalance = totalCredit - totalDebit;

        if (totalBalance < 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 4, 5));

            Cell balanceCell = balanceRow.createCell(0);
            balanceCell.setCellValue("You Will Get");
            balanceCell.setCellStyle(totalStyle);

            Cell balanceAmountCell = balanceRow.createCell(4);
            balanceAmountCell.setCellValue(Math.abs(totalBalance));
            balanceAmountCell.setCellStyle(totalStyle);

        } else if (totalBalance > 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 4, 5));

            Cell balanceCell = balanceRow.createCell(0);
            balanceCell.setCellValue("You Will Give");
            balanceCell.setCellStyle(totalStyle);

            Cell balanceAmountCell = balanceRow.createCell(4);
            balanceAmountCell.setCellValue(totalBalance);
            balanceAmountCell.setCellStyle(totalStyle);

        } else {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 5));

            Cell balanceCell = balanceRow.createCell(0);
            balanceCell.setCellValue("Settled");
            balanceCell.setCellStyle(totalStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert workbook to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
