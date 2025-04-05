package com.transaction.book.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.transaction.book.dto.requestDTO.NewTransactionRequest;
import com.transaction.book.dto.responseDTO.TransactionResponse;
import com.transaction.book.dto.responseObjects.DataResponse;
import com.transaction.book.dto.responseObjects.SuccessResponse;
import com.transaction.book.dto.updateDto.UpdateTransaction;
import com.transaction.book.entities.Customer;
import com.transaction.book.entities.Transaction;
import com.transaction.book.helper.DateTimeFormat;
import com.transaction.book.helper.ExcelFormat;
import com.transaction.book.helper.PdfFormat;
import com.transaction.book.services.logicService.TransactionMethods;
import com.transaction.book.services.serviceImpl.CustomerServiceImpl;
import com.transaction.book.services.serviceImpl.TransactionServiceImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
@Slf4j
public class TransactionController {

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private TransactionMethods transactionMethods;

    @Autowired
    private PdfFormat pdfFromat;

    @PostMapping("/addTransaction")
    public ResponseEntity<SuccessResponse> addTransaction(@RequestPart("transaction") @Valid NewTransactionRequest request,
                                                            @RequestPart(value = "bill", required = false) MultipartFile bill) {
        log.info("Adding transaction for customer ID: {}", request.getCustomerId());
        SuccessResponse response = new SuccessResponse();
        Customer customer = this.customerServiceImpl.getCustomerById(request.getCustomerId());
        if (customer == null) {
            log.warn("Customer with ID: {} not found", request.getCustomerId());
            response.setMessage("customer not present !");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if ((request.isGave() && request.isGot()) || (!request.isGave() && !request.isGot())) {
            log.warn("Invalid transaction type for customer ID: {}", request.getCustomerId());
            response.setMessage("please set amount is gave or got ! you can set only one at a time");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            byte[] billBytes = null;
            if (bill != null && !bill.isEmpty()) {
                billBytes = bill.getBytes();
            }
            if (this.transactionMethods.addNewTransaction(request,billBytes)) {
                log.info("Transaction added successfully for customer ID: {}", request.getCustomerId());
                response.setMessage("Add Transaction Successfully !");
                response.setHttpStatus(HttpStatus.OK);
                response.setStatusCode(200);
                return ResponseEntity.of(Optional.of(response));
            } else {
                log.error("Something went wrong while adding transaction for customer ID: {}", request.getCustomerId());
                response.setMessage("something went wrong !");
                response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setStatusCode(500);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            log.error("Error adding transaction for customer ID: {}: {}", request.getCustomerId(), e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/updateTransaction")
    public ResponseEntity<SuccessResponse> updateTransaction(@RequestPart("updateTransaction") @Valid UpdateTransaction request,
                                                            @RequestPart(value = "bill", required = false) MultipartFile bill) {
        log.info("Updating transaction with ID: {}", request.getId());
        SuccessResponse response = new SuccessResponse();
        Transaction transaction = this.transactionServiceImpl.getTransactionById(request.getId());
        if(transaction ==null){
            log.warn("Transaction with ID: {} not found", request.getId());
            response.setMessage("transaction not found!");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Customer customer = transaction.getCustomer();
        if ((request.isGave() && request.isGot()) || (!request.isGave() && !request.isGot())) {
            log.warn("Invalid transaction type for transaction ID: {}", request.getId());
            response.setMessage("please set amount is gave or got ! you can set only one at a time");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        try {

            byte[] billBytes = null;
            if (bill != null && !bill.isEmpty()) {
                billBytes = bill.getBytes();
            }
            this.transactionMethods.updateTransaction(customer.getId(), request,billBytes);

            log.info("Transaction with ID: {} updated successfully", request.getId());
            response.setMessage("transaction update successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error updating transaction with ID: {}: {}", request.getId(), e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deleteTransaction/{id}")
    public ResponseEntity<SuccessResponse> deleteTransaction(@PathVariable("id") long id) {
        log.info("Deleting transaction with ID: {}", id);
        SuccessResponse response = new SuccessResponse();
        Transaction transaction = this.transactionServiceImpl.getTransactionById(id);
        Customer customer = transaction.getCustomer();
        try {
            customer.setAmount(customer.getAmount() - transaction.getAmount());
            customer.setUpdateDate(DateTimeFormat.format(LocalDateTime.now()));
            this.customerServiceImpl.addCustomer(customer);

            for (Transaction transaction2 : this.transactionServiceImpl.getAfterTransactions(customer.getId(),
                    transaction.getDate())) {
                transaction2.setBalanceAmount(transaction2.getBalanceAmount() - transaction.getAmount());
                this.transactionServiceImpl.addTransaction(transaction2);
            }

            this.transactionServiceImpl.deleteTransaction(id);

            log.info("Transaction with ID: {} deleted successfully", id);
            response.setMessage("transaction deleted successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error deleting transaction with ID: {}: {}", id, e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getCustomersTransaction/{customerId}")
    public ResponseEntity<?> getCustomersTransaction(@PathVariable("customerId") long id) {
        log.info("Fetching transactions for customer ID: {}", id);
        try {
            DataResponse response = new DataResponse();
            log.info("Fetched transactions successfully for customer ID: {}", id);
            response.setMessage("get All trasactions successfully !");
            response.setData(this.transactionServiceImpl.getTrasactionsByCustomerId(id));
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error fetching transactions for customer ID: {}: {}", id, e.getMessage());
            SuccessResponse response = new SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getTransactions")
    public ResponseEntity<?> getTransactions(@RequestParam(required = false) String query,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching transactions with query: {}, startDate: {}, endDate: {}", query, startDate, endDate);
        try {
            DataResponse response = new DataResponse();
            log.info("Fetched transactions successfully");
            response.setMessage("get All trasactions successfully !");
            response.setData(this.transactionServiceImpl.getAllTrasactions(query, startDate, endDate));
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage());
            SuccessResponse response = new SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/downloadReport")
    public ResponseEntity<?> downloadPDF(
            @RequestParam(required = true) long customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Downloading PDF report for customer ID: {}, startDate: {}, endDate: {}", customerId, startDate, endDate);
        List<TransactionResponse> transactions = this.transactionServiceImpl.getAllTrasactions(customerId, startDate,
                endDate);
        if (transactions == null) {
            log.warn("No transactions found for customer ID: {}", customerId);
            SuccessResponse response = new SuccessResponse();
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(404);
            response.setMessage("No transactions found !");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        byte[] pdfBytes = pdfFromat.generateTransactionStatement(transactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=statement.pdf");

        log.info("PDF report downloaded successfully for customer ID: {}", customerId);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/downloadExcelReport")
    public ResponseEntity<?> exportExcelReports(@RequestParam(required = false) String query,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Exporting Excel report with query: {}, startDate: {}, endDate: {}", query, startDate, endDate);
        try {
            byte[] excelBytes = ExcelFormat.generateExcel(this.transactionServiceImpl.getAllTrasactions(query,startDate,endDate));

            ByteArrayResource resource = new ByteArrayResource(excelBytes);

            log.info("Excel report exported successfully");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                    .header(HttpHeaders.CONTENT_TYPE,
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error exporting Excel report: {}", e.getMessage());
            SuccessResponse response = new SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
