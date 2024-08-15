package com.dpd.gfarkas.service;

import com.dpd.gfarkas.entity.Address;
import com.dpd.gfarkas.entity.Customer;
import com.dpd.gfarkas.entity.Phone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.flywaydb.core.Flyway;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * CustomerServiceTest
 *
 * @author FarkasGábor
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class CustomerServiceTest {

    @Autowired
    private Flyway flyway;
    
    @Autowired
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        // Ensure Flyway migrations are applied before each test
        flyway.clean();
        flyway.migrate();
    }
    
    @Test
    public void CustomerServiceTest() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Customer customer = new Customer();
        customer.setName("Test Name");
        try {
            customer.setDateBirth(formatter.parse("2010-01-03"));
        } catch (ParseException e) {
            fail();
        }
        customer.setPlaceBirth("Pecs");
        customer.setNameMother("Test Mother");
        customer.setTaj(1234L);
        customer.setTax(5678L);
        customer.setEmail("nospam@me.com");
          
        Address address1 = new Address();
        address1.setCity("Budapest");
        address1.setStreet("Main");
        address1.setHouseNumber("12A");
        address1.setZip(1111);
        
        Address address2 = new Address();
        address2.setCity("Sopron");
        address2.setStreet("Something");
        address2.setHouseNumber("20");
        address2.setZip(2222);

        customer.setAddressList(Arrays.asList(address1, address2));
        
        Phone phone1 = new Phone();
        phone1.setCountryCode(36);
        phone1.setPhoneNumber(1234567);
        
        Phone phone2 = new Phone();
        phone2.setCountryCode(36);
        phone2.setPhoneNumber(2345678);
        
        customer.setPhoneList(Arrays.asList(phone1, phone2));
        
        assertTrue(customerService.addCustomer(customer).equals(1L));
                
        Customer getCustomer = customerService.getCustomer(1L).map(c -> c).orElse(null);
        
        if (getCustomer == null) fail();
        
        assertTrue(getCustomer.getName().equals("Test Name"));
        assertTrue(formatter.format(getCustomer.getDateBirth()).equals("2010-01-03"));
        assertTrue(getCustomer.getPlaceBirth().equals("Pecs"));
        assertTrue(getCustomer.getNameMother().equals("Test Mother"));
        assertTrue(getCustomer.getTaj().equals(1234L));
        assertTrue(getCustomer.getTax().equals(5678L));
        assertTrue(getCustomer.getEmail().equals("nospam@me.com"));        

        assertTrue(getCustomer.getAddressList().get(1).getCity().equals("Sopron"));
        assertTrue(getCustomer.getAddressList().get(1).getStreet().equals("Something"));
        assertTrue(getCustomer.getAddressList().get(1).getHouseNumber().equals("20"));
        assertTrue(getCustomer.getAddressList().get(1).getZip() == 2222);

        assertTrue(getCustomer.getPhoneList().get(1).getCountryCode() == 36);
        assertTrue(getCustomer.getPhoneList().get(1).getPhoneNumber() == 2345678);

        assertTrue(customerService.depersonalizeCustomer(1L));

        getCustomer = customerService.getCustomer(1L).map(c -> c).orElse(null);
        
        if (getCustomer == null) fail();
        
        assertTrue(getCustomer.getName().equals("---"));
        assertTrue(formatter.format(getCustomer.getDateBirth()).equals("2000-01-01"));
        assertTrue(getCustomer.getPlaceBirth().equals("n/a"));
        assertTrue(getCustomer.getNameMother().equals("---"));
        assertTrue(getCustomer.getTaj().equals(0L));
        assertTrue(getCustomer.getTax().equals(0L));
        assertTrue(getCustomer.getEmail().equals("nospam@nospam.com"));        

        assertTrue(getCustomer.getAddressList().get(1).getCity().equals("---"));
        assertTrue(getCustomer.getAddressList().get(1).getStreet().equals("---"));
        assertTrue(getCustomer.getAddressList().get(1).getHouseNumber().equals("---"));
        assertTrue(getCustomer.getAddressList().get(1).getZip() == 0);

        assertTrue(getCustomer.getPhoneList().get(1).getCountryCode() == 0);
        assertTrue(getCustomer.getPhoneList().get(1).getPhoneNumber() == 0);
    }
}
