package com.pauluswi.batavia.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CustomerBalanceRequestDTOTest {

    @Test
    public void testGettersAndSetters() {
        CustomerBalanceRequestDTO dto = new CustomerBalanceRequestDTO();
        dto.setBankAccountNumber("123456");
        dto.setCustomerFullName("John Doe");

        assertEquals("123456", dto.getBankAccountNumber());
        assertEquals("John Doe", dto.getCustomerFullName());
    }

    @Test
    public void testEqualsAndHashCode() {
        CustomerBalanceRequestDTO dto1 = new CustomerBalanceRequestDTO();
        dto1.setBankAccountNumber("123456");
        dto1.setCustomerFullName("John Doe");

        CustomerBalanceRequestDTO dto2 = new CustomerBalanceRequestDTO();
        dto2.setBankAccountNumber("123456");
        dto2.setCustomerFullName("John Doe");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setBankAccountNumber("654321");
        assertNotEquals(dto1, dto2);
    }

    @Test
    public void testToString() {
        CustomerBalanceRequestDTO dto = new CustomerBalanceRequestDTO();
        dto.setBankAccountNumber("123456");
        dto.setCustomerFullName("John Doe");

        String toString = dto.toString();
        assertEquals("CustomerBalanceRequestDTO(bankAccountNumber=123456, customerFullName=John Doe)", toString);
    }
}
