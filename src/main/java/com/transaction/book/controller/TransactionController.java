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
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/api/user")
@CrossOrigin
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
    public ResponseEntity<SuccessResponse> addTransaction(@Valid @RequestBody NewTransactionRequest request) {
        SuccessResponse response = new SuccessResponse();
        Customer customer = this.customerServiceImpl.getCustomerById(request.getCustomerId());
        if (customer == null) {
            response.setMessage("customer not present !");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if ((request.isGave() && request.isGot()) || (!request.isGave() && !request.isGot())) {
            response.setMessage("please set amount is gave or got ! you can set only one at a time");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            if (this.transactionMethods.addNewTransaction(request)) {
                response.setMessage("Add Transaction Successfully !");
                response.setHttpStatus(HttpStatus.OK);
                response.setStatusCode(200);
                return ResponseEntity.of(Optional.of(response));
            } else {
                response.setMessage("something went wrong !");
                response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setStatusCode(500);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/updateTransaction")
    public ResponseEntity<SuccessResponse> updateTransaction(@Valid @RequestBody UpdateTransaction request) {
        SuccessResponse response = new SuccessResponse();
        Transaction transaction = this.transactionServiceImpl.getTransactionById(request.getId());
        Customer customer = transaction.getCustomer();
        if ((request.isGave() && request.isGot()) || (!request.isGave() && !request.isGot())) {
            response.setMessage("please set amount is gave or got ! you can set only one at a time");
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(200);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        try {
            this.transactionMethods.updateTransaction(customer.getId(), request);

            response.setMessage("transaction update successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deleteTransaction/{id}")
    public ResponseEntity<SuccessResponse> deleteTransaction(@PathVariable("id") long id) {
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

            response.setMessage("transaction deleted successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getCustomersTransaction/{customerId}")
    public ResponseEntity<?> getCustomersTransaction(@PathVariable("customerId") long id) {

        try {
            DataResponse response = new DataResponse();
            response.setMessage("get All trasactions successfully !");
            response.setData(this.transactionServiceImpl.getTrasactionsByCustomerId(id));
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
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

        try {
            DataResponse response = new DataResponse();
            response.setMessage("get All trasactions successfully !");
            response.setData(this.transactionServiceImpl.getAllTrasactions(query, startDate, endDate));
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
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

        List<TransactionResponse> transactions = this.transactionServiceImpl.getAllTrasactions(customerId, startDate,
                endDate);
        if (transactions == null) {
            SuccessResponse response = new SuccessResponse();
            response.setHttpStatus(HttpStatus.NOT_FOUND);
            response.setStatusCode(404);
            response.setMessage("No transactions found !");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        byte[] pdfBytes = pdfFromat.generateTransactionStatement(transactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=statement.pdf");

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/downloadExcelReport")
    public ResponseEntity<?> exportExcelReports(@RequestParam(required = false) String query,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            byte[] excelBytes = ExcelFormat.generateExcel(this.transactionServiceImpl.getAllTrasactions(query,startDate,endDate));

            ByteArrayResource resource = new ByteArrayResource(excelBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx")
                    .header(HttpHeaders.CONTENT_TYPE,
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(resource);
        } catch (Exception e) {
            SuccessResponse response = new SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
