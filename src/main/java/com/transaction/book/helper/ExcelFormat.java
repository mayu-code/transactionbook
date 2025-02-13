package com.transaction.book.helper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.transaction.book.dto.responseDTO.TransactionResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelFormat {

    public static byte[] generateExcel(List<TransactionResponse> transactions) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // // Header Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Sr No", "Date", "Detail", "Customer Name", "Debit(-)", "Credit(+)"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowIndex = 1;
        for (TransactionResponse transaction:transactions) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(rowIndex-1);
            row.createCell(1).setCellValue(transaction.getDate() != null ? transaction.getDate() : " ");
            row.createCell(1).setCellValue(transaction.getDetail() != null ? transaction.getDetail() : " ");
            row.createCell(1).setCellValue(transaction.getCustomerName() != null ? transaction.getCustomerName() : " ");
            if(transaction.getAmount()<0){
                row.createCell(1).setCellValue(transaction.getAmount()*(-1));
            }else{
                row.createCell(1).setCellValue(transaction.getAmount());
            }
            
            
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
