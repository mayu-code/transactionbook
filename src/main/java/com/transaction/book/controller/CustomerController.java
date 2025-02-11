package com.transaction.book.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.transaction.book.dto.requestDTO.CustomerRequestDto;
import com.transaction.book.dto.responseObjects.DataResponse;
import com.transaction.book.dto.responseObjects.SuccessResponse;
import com.transaction.book.dto.updateDto.UpdateCustomer;
import com.transaction.book.entities.Address;
import com.transaction.book.entities.Customer;
import com.transaction.book.entities.Transaction;
import com.transaction.book.helper.DateTimeFormat;
import com.transaction.book.services.serviceImpl.AddressServiceImpl;
import com.transaction.book.services.serviceImpl.CustomerServiceImpl;
import com.transaction.book.services.serviceImpl.TransactionServiceImpl;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @Autowired
    private AddressServiceImpl addressServiceImpl;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @PostMapping("/addCustomer")
    public ResponseEntity<SuccessResponse> addCustomer(@RequestBody CustomerRequestDto request) {
        SuccessResponse response = new SuccessResponse();
        Customer customer = new Customer();
        try {
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
                customer.setAmount(transaction.getAmount());
                transaction.setDate(request.getDate());
                transaction.setBalanceAmount(customer.getAmount());
                transaction.setCustomer(customer);
                this.transactionServiceImpl.addTransaction(transaction);
            }

            if(request.getAddress()!=null){
            Address address = new Address();
            address.setBuildingNO(request.getAddress().getBuildingNo());
            address.setArea(request.getAddress().getArea());
            address.setCity(request.getAddress().getCity());
            address.setPincode(request.getAddress().getPincode());
            address.setState(request.getAddress().getState());
            address.setCustomer(customer);
            this.addressServiceImpl.addAddress(address);
            }
            this.customerServiceImpl.addCustomer(customer);

            response.setMessage("customer added successfully !");
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

    @GetMapping("/getAllCustomers")
    public ResponseEntity<DataResponse> getAllCustomer(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) boolean gave,
            @RequestParam(required = false) boolean get,
            @RequestParam(required = false) boolean settle) {
        DataResponse response = new DataResponse();
        try {
            response.setData(this.customerServiceImpl.findAllCustomerResponse(query, gave, get, settle));
            response.setMessage("get all Customers !");
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

    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<SuccessResponse> deleteCustomer(@PathVariable("id") long id) {
        SuccessResponse response = new SuccessResponse();
        try {
            this.customerServiceImpl.deleteCusotmer(id);
            response.setMessage("delete Customer successfully !");
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

    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<?> getCustomerbyId(@PathVariable("id") long id) {
        try {
            DataResponse response = new DataResponse();
            response.setData(this.customerServiceImpl.getCustomerResponseById(id));
            response.setMessage("delete Customer successfully !");
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

    @PostMapping("/updateCustomer")
    public ResponseEntity<SuccessResponse> updateCustomer(@RequestBody UpdateCustomer request){
        SuccessResponse response = new SuccessResponse();
        Customer customer = this.customerServiceImpl.getCustomerById(request.getId());
        if(customer==null){
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
                    address.setBuildingNO(request.getAddress().getBuildingNo());
                    address.setCity(request.getAddress().getCity());
                    address.setPincode(request.getAddress().getPincode());
                    address.setState(request.getAddress().getState());
                    address.setCustomer(customer);
                    this.addressServiceImpl.addAddress(address);
                }else{
                    Address address = customer.getAddress();
                    address.setArea(request.getAddress().getArea());
                    address.setBuildingNO(request.getAddress().getBuildingNo());
                    address.setCity(request.getAddress().getCity());
                    address.setPincode(request.getAddress().getPincode());
                    address.setState(request.getAddress().getState());
                    this.addressServiceImpl.addAddress(address);
                }
            }
            response.setMessage("Customer Updated successfully !");
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
}
