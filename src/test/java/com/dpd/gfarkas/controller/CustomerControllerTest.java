package com.dpd.gfarkas.controller;

import com.dpd.gfarkas.controller.dto.CustomerId;
import com.dpd.gfarkas.entity.Address;
import com.dpd.gfarkas.entity.Customer;
import com.dpd.gfarkas.entity.Phone;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.flywaydb.core.Flyway;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CustomerControllerTest
 *
 * @author FarkasGábor
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class CustomerControllerTest {
    private final MockMvc mockMvc;
    
    @Autowired
    public CustomerControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        // Ensure Flyway migrations are applied before each test
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testAll() throws Exception {
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
        
        customer.setPhoneList(Arrays.asList(phone1, phone2));
        
        String badRequest = (new ObjectMapper()).valueToTree(customer).toString();

        mockMvc.perform( MockMvcRequestBuilders
                .post("/customer/addCustomer")
                .content(badRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        phone2.setPhoneNumber(2345678);
        customer.setPhoneList(Arrays.asList(phone1, phone2));

        String request = (new ObjectMapper()).valueToTree(customer).toString();

        mockMvc.perform( MockMvcRequestBuilders
                .post("/customer/addCustomer")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(2));

        mockMvc.perform( MockMvcRequestBuilders
                .get("/customer/getCustomer/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform( MockMvcRequestBuilders
                .get("/customer/getCustomer/{id}", 2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placeBirth").value("Pecs"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameMother").value("Test Mother"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taj").value(1234))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tax").value(5678))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("nospam@me.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].city").value("Sopron"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].street").value("Something"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].houseNumber").value("20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].zip").value(2222))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneList[1].countryCode").value(36))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneList[1].phoneNumber").value(2345678));

        CustomerId customerId = new CustomerId();
        customerId.setCustomerId(1L);
        String badDepersonalizeRequest = (new ObjectMapper()).valueToTree(customerId).toString();
        
        mockMvc.perform( MockMvcRequestBuilders
                .patch("/customer/depersonalizeCustomer")
                .content(badDepersonalizeRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        CustomerId goodCustomerId = new CustomerId(2L);
        String depersonalizeRequest = (new ObjectMapper()).valueToTree(goodCustomerId).toString();
        
        mockMvc.perform( MockMvcRequestBuilders
                .patch("/customer/depersonalizeCustomer")
                .content(depersonalizeRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform( MockMvcRequestBuilders
                .get("/customer/getCustomer/{id}", 2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("---"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placeBirth").value("n/a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nameMother").value("---"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taj").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tax").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("nospam@nospam.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].city").value("---"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].street").value("---"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].houseNumber").value("---"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressList[1].zip").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneList[1].countryCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneList[1].phoneNumber").value(0));    
    }
}
