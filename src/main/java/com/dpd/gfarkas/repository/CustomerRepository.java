package com.dpd.gfarkas.repository;

import com.dpd.gfarkas.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CustomerRepository
 *
 * @author FarkasGábor
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
}
