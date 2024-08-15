package com.dpd.gfarkas.service;

import com.dpd.gfarkas.entity.Customer;
import com.dpd.gfarkas.repository.CustomerRepository;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * CustomerService
 *
 * @author FarkasGábor
 */
@Service
public class CustomerService {
    @Value("#{${depersonalization.customer.text}}")
    private Map<String, String> customerTextFields;
    @Value("#{${depersonalization.customer.date}}")
    private Map<String, String> customerDateFields;
    @Value("#{${depersonalization.customer.numeric}}")
    private Map<String, Integer> customerNumericFields;

    @Value("#{${depersonalization.address.text}}")
    private Map<String, String> addressTextFields;
    @Value("#{${depersonalization.address.numeric}}")
    private Map<String, Integer> addressNumericFields;

    @Value("#{${depersonalization.phone.numeric}}")
    private Map<String, Integer> phoneNumericFields;

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Transactional(TxType.SUPPORTS)
    public Long addCustomer(Customer customer) {
        try {
            return customerRepository.save(customer).getCustomerId();
        } catch (Exception e) {
            return -1L;
        }
    }

    public Optional<Customer> getCustomer(Long customerId) {
        return customerRepository.findById(customerId);
    }
    
    public boolean depersonalizeCustomer(Long customerId) {
        try {
            return customerRepository.findById(customerId)
                .map( customer -> {
                    customerTextFields.forEach((k, v) -> callSetter(customer, k, v));
                    customerNumericFields.forEach((k, v) -> callSetter(customer, k, Long.valueOf(v)));
                    customerDateFields.forEach((k, v) -> callSetter(customer, k, parse(v)));
                    
                    customer.getAddressList().forEach(address -> {
                        addressTextFields.forEach((k, v) -> callSetter(address, k, v));
                        addressNumericFields.forEach((k, v) -> callSetter(address, k, v));
                    });
                    
                    customer.getPhoneList().forEach(phone -> {
                        phoneNumericFields.forEach((k, v) -> callSetter(phone, k, v));
                    });

                    customerRepository.save(customer);
                    return true;
                })
                .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
    
    private Date parse(String value) {
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
    
    private static void callSetter(Object obj, String propertyName, Object value) {
        try {
            String setterMethodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            Method setterMethod = obj.getClass().getMethod(setterMethodName, value.getClass());
            setterMethod.invoke(obj, value);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException | NullPointerException e) {
            throw new RuntimeException();
        }
    }
}
