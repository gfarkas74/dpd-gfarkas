package com.dpd.gfarkas.controller.dto;

/**
 * CustomerId
 *
 * @author FarkasGábor
 */
public class CustomerId {
    private Long customerId;

    public CustomerId() {}    

    public CustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
