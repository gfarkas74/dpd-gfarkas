package com.dpd.gfarkas.controller;

import com.dpd.gfarkas.controller.dto.CustomerId;
import com.dpd.gfarkas.entity.Customer;
import com.dpd.gfarkas.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * CustomerController
 *
 * @author FarkasGábor
 */
@RestController
@RequestMapping(path = "/customer", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
        
    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long customerId) {        
        return customerService.getCustomer(customerId)
            .map( customer -> ResponseEntity.ok().body(customer) )          //200 OK
            .orElse( ResponseEntity.notFound().build() );                  //404 if the customer cannot be found by the given id
    }

    @PostMapping("/addCustomer")
    public ResponseEntity<CustomerId> addCustomer(@RequestBody Customer customer) {
        Long customerId = customerService.addCustomer(customer);
        //200 OK, 400 if the request cannot be mapped to the entity, 422 if the request cannot be processed by the inserts
        return customerId == null ?
            ResponseEntity.unprocessableEntity().build() :
            customerId.equals(-1L) ?
                ResponseEntity.badRequest().build() :
                ResponseEntity.ok().body(new CustomerId(customerId));
    }

    @PatchMapping("/depersonalizeCustomer")
    public ResponseEntity depersonalizeCustomer(@RequestBody CustomerId customerId) {
        // 200 OK, 404 if the customer cannot be found by the given id
        return customerService.depersonalizeCustomer(customerId.getCustomerId()) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
