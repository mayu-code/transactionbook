package com.transaction.book.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.transaction.book.constants.RemainderStatus;
import com.transaction.book.dto.requestDTO.CustomerRequestDto;
import com.transaction.book.dto.requestDTO.DueDateRequest;
import com.transaction.book.dto.responseDTO.CusotomerFullResponse;
import com.transaction.book.dto.responseObjects.DataResponse;
import com.transaction.book.dto.responseObjects.SuccessResponse;
import com.transaction.book.dto.updateDto.UpdateCustomer;
import com.transaction.book.entities.Address;
import com.transaction.book.entities.Customer;
import com.transaction.book.entities.Remainder;
import com.transaction.book.entities.Transaction;
import com.transaction.book.helper.DateTimeFormat;
import com.transaction.book.helper.PdfFormat;
import com.transaction.book.services.serviceImpl.AddressServiceImpl;
import com.transaction.book.services.serviceImpl.CustomerServiceImpl;
import com.transaction.book.services.serviceImpl.TransactionServiceImpl;

import com.transaction.book.services.serviceImpl.RemainderServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @Autowired
    private RemainderServiceImpl RemainderServiceImpl;

    @Autowired
    private AddressServiceImpl addressServiceImpl;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private PdfFormat pdfFromat;

    @PostMapping("/addCustomer")
    public ResponseEntity<DataResponse> addCustomer(@RequestPart @Valid CustomerRequestDto request,
                                                    @RequestPart(value = "bill", required = false) MultipartFile bill) {
        log.info("Adding customer with name: {}", request.getName());
        DataResponse response = new DataResponse();
        Customer customer2 = new Customer();
        customer2 = this.customerServiceImpl.getCustomerByMobileNo(request.getMobileNo());
        if(customer2!=null){
            log.warn("Customer with mobile number: {} already exists", request.getMobileNo());
            response.setMessage("Customer alredy present!");
            response.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            response.setStatusCode(208);
            response.setData(customer2);
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
        }
        CusotomerFullResponse customer1 = this.customerServiceImpl.getCustomerResponseByName(request.getName());
        if(customer1!=null){
            log.warn("Customer with name: {} already exists", request.getName());
            response.setMessage("customer Name already present!");
            response.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            response.setStatusCode(208);
            response.setData(customer1);
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
        }
        try {
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setMobileNo(request.getMobileNo());
            customer.setGstinNo(request.getGstinNo());
            customer.setUpdateDate(DateTimeFormat.format(LocalDateTime.now()));
            customer.setReference(request.getReference());
            customer = this.customerServiceImpl.addCustomer(customer);

            if (request.getAmount() != 0) {
                Transaction transaction = new Transaction();
                if (request.isGave()) {
                    transaction.setAmount(request.getAmount() * (-1));
                } else {
                    transaction.setAmount(request.getAmount());
                }
                byte[] billBytes = null;
                if (bill != null && !bill.isEmpty()) {
                    billBytes = bill.getBytes();
                }
                customer.setAmount(transaction.getAmount());
                transaction.setDate(request.getDate());
                transaction.setBill(billBytes);
                transaction.setBalanceAmount(customer.getAmount());
                transaction.setCustomer(customer);
                this.transactionServiceImpl.addTransaction(transaction);
            }

            if(request.getAddress()!=null){
            Address address = new Address();
            address.setBuildingNo(request.getAddress().getBuildingNo());
            address.setArea(request.getAddress().getArea());
            address.setCity(request.getAddress().getCity());
            address.setPincode(request.getAddress().getPincode());
            address.setState(request.getAddress().getState());
            address.setCustomer(customer);
            this.addressServiceImpl.addAddress(address);
            }
            customer =this.customerServiceImpl.addCustomer(customer);
            response.setData(customer);
            log.info("Customer added successfully with name: {}", request.getName());
            response.setMessage("customer added successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error adding customer with name: {}: {}", request.getName(), e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<DataResponse> getAllCustomer(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) boolean gave,
            @RequestParam(required = false) boolean get,
            @RequestParam(required = false) boolean settle) {
        log.info("Fetching all customers with query: {}", query);
        DataResponse response = new DataResponse();
        try {
            response.setData(this.customerServiceImpl.findAllCustomerResponse(query, gave, get, settle));
            log.info("Fetched all customers successfully");
            response.setMessage("get all Customers !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error fetching customers: {}", e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<SuccessResponse> deleteCustomer(@PathVariable("id") long id) {
        log.info("Deleting customer with ID: {}", id);
        SuccessResponse response = new SuccessResponse();
        try {
            this.customerServiceImpl.deleteCusotmer(id);
            log.info("Customer with ID: {} deleted successfully", id);
            response.setMessage("delete Customer successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error deleting customer with ID: {}: {}", id, e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<?> getCustomerbyId(@PathVariable("id") long id) {
        log.info("Fetching customer with ID: {}", id);
        try {
            DataResponse response = new DataResponse();
            response.setData(this.customerServiceImpl.getCustomerResponseById(id));
            log.info("Customer with ID: {} fetched successfully", id);
            response.setMessage("Customer get successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error fetching customer with ID: {}: {}", id, e.getMessage());
            SuccessResponse response = new SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/updateCustomer")
    public ResponseEntity<SuccessResponse> updateCustomer(@Valid @RequestBody UpdateCustomer request){
        log.info("Updating customer with ID: {}", request.getId());
        SuccessResponse response = new SuccessResponse();
        Customer customer = this.customerServiceImpl.getCustomerById(request.getId());
        if(customer==null){
            log.warn("Customer with ID: {} not found", request.getId());
            response.setMessage("something went wrong !");
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        try {
            customer.setName(request.getName());
            customer.setGstinNo(request.getGstinNo());
            customer.setMobileNo(request.getMobileNo());
            customer.setReference(request.getReference());
            if(request.getAddress()!=null){
                if(customer.getAddress()==null){
                    Address address = new Address();
                    address.setArea(request.getAddress().getArea());
                    address.setBuildingNo(request.getAddress().getBuildingNo());
                    address.setCity(request.getAddress().getCity());
                    address.setPincode(request.getAddress().getPincode());
                    address.setState(request.getAddress().getState());
                    address.setCustomer(customer);
                    this.addressServiceImpl.addAddress(address);
                }else{
                    Address address = customer.getAddress();
                    address.setArea(request.getAddress().getArea());
                    address.setBuildingNo(request.getAddress().getBuildingNo());
                    address.setCity(request.getAddress().getCity());
                    address.setPincode(request.getAddress().getPincode());
                    address.setState(request.getAddress().getState());
                    this.addressServiceImpl.addAddress(address);
                }
            }
            log.info("Customer with ID: {} updated successfully", request.getId());
            response.setMessage("Customer Updated successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error updating customer with ID: {}: {}", request.getId(), e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/setDueDate")
    public ResponseEntity<SuccessResponse> setDueDate(@Valid @RequestBody DueDateRequest request){
        log.info("Setting due date for customer with ID: {}", request.getId());
        SuccessResponse response = new SuccessResponse();
        try {
            Customer customer = this.customerServiceImpl.getCustomerById(request.getId());
            customer.setDueDate(request.getDueDate());
            
            Remainder remainder1 = this.RemainderServiceImpl.getExactLastRemainder(customer.getId());
            if(remainder1!=null && remainder1.getStatus().equals(RemainderStatus.Upcoming)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
                
                LocalDate previousDate = LocalDate.parse(remainder1.getDueDate(), formatter);
                LocalDate newDate = LocalDate.parse(request.getDueDate(), formatter);
                
                if (newDate.isBefore(previousDate) || newDate.isEqual(previousDate)) {
                    response.setMessage("Set a later due date after "+(previousDate.format(formatter2))+" or remove the previous remainder.");
                    response.setHttpStatus(HttpStatus.BAD_REQUEST);
                    response.setStatusCode(400);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                
                customer = this.customerServiceImpl.addCustomer(customer);

                if(request.getReason()==null){
                    remainder1.setReason("Next time");
                }else{
                    remainder1.setReason(request.getReason());
                }
                remainder1.setAmount(request.getAmount());
                remainder1.setStatus(RemainderStatus.Failed);
                this.RemainderServiceImpl.addRemainder(remainder1);
            }

            Remainder remainder = new Remainder();
            remainder.setDueDate(request.getDueDate());
            remainder.setCustomer(customer);
            remainder.setAddedDate(String.valueOf(LocalDateTime.now()));
            remainder.setAmount(request.getAmount());
            this.RemainderServiceImpl.addRemainder(remainder);

            log.info("Due date set successfully for customer with ID: {}", request.getId());
            response.setMessage("Due Date set successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error setting due date for customer with ID: {}: {}", request.getId(), e.getMessage());
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getCustomersOnDueDate")
    public ResponseEntity<?> getCustomersOnDueDate(){
        log.info("Fetching customers on due date");
        try {
            DataResponse response = new DataResponse();
            response.setData(this.customerServiceImpl.getDueDateCustomer());
            log.info("Fetched customers on due date successfully");
            response.setMessage("Due Date set successfully !");
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            return ResponseEntity.of(Optional.of(response));
        } catch (Exception e) {
            log.error("Error fetching customers on due date: {}", e.getMessage());
            SuccessResponse response = new SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
   
    @GetMapping("/getCustomerRemainders/{customerId}")
    public ResponseEntity<?> getCustomerRemainders(@PathVariable("customerId")long id){
        log.info("Fetching remainders for customer with ID: {}", id);
        try {
            List<Remainder> remainders = this.RemainderServiceImpl.getAllRemindersByCustomerId(id);
            DataResponse response = new DataResponse();
            log.info("Fetched remainders for customer with ID: {} successfully", id);
            response.setMessage("get All Remainders Successfully !");
            response.setStatusCode(200);
            response.setHttpStatus(HttpStatus.OK);
            response.setData(remainders);
            return ResponseEntity.of(Optional.of(response));
            
        } catch (Exception e) {
            log.error("Error fetching remainders for customer with ID: {}: {}", id, e.getMessage());
            SuccessResponse response =new  SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }

    @GetMapping("/downLoadRemainders/{customerId}")
    public ResponseEntity<?> downloadRemainderPdf(@PathVariable("customerId")long id){
        log.info("Downloading remainder PDF for customer with ID: {}", id);
        try {
            CusotomerFullResponse customer = this.customerServiceImpl.getCustomerResponseById(id);


            List<Remainder> remainders= this.RemainderServiceImpl.getAllRemindersByCustomerId(id);
            byte[] pdfBytes = pdfFromat.generateCustomerRemainderPdf(remainders,customer);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=statement.pdf");

            log.info("Remainder PDF downloaded successfully for customer with ID: {}", id);
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error downloading remainder PDF for customer with ID: {}: {}", id, e.getMessage());
            SuccessResponse response = new SuccessResponse();
            response.setMessage(e.getMessage());
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("removeDueDate/{customerId}/{date}")
    public ResponseEntity<?>  removeDueDate(@PathVariable("customerId") long id ,@PathVariable("date")String date){
        log.info("Removing due date for customer with ID: {} and date: {}", id, date);
        try {
            Remainder remainder = this.RemainderServiceImpl.getRemainderByDate(date, id);
            remainder.setDelete(true);
            this.RemainderServiceImpl.addRemainder(remainder);


            Customer customer = this.customerServiceImpl.getCustomerById(id);
            customer.setDueDate(null);
            this.customerServiceImpl.addCustomer(customer);
            SuccessResponse response = new SuccessResponse();
            log.info("Due date removed successfully for customer with ID: {} and date: {}", id, date);
            response.setHttpStatus(HttpStatus.OK);
            response.setStatusCode(200);
            response.setMessage("Due Date remove successfully !");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error removing due date for customer with ID: {} and date: {}: {}", id, date, e.getMessage());
            SuccessResponse response = new SuccessResponse();
            response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatusCode(200);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
