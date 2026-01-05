package com.pauluswi.batavia.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FundTransferRequestDTOTest {

    @Test
    public void testGettersAndSetters() {
        FundTransferRequestDTO dto = new FundTransferRequestDTO();
        dto.setSourceAccountNumber("123");
        dto.setDestinationAccountNumber("456");
        dto.setAmount(100.0);
        dto.setCurrency("IDR");
        dto.setDescription("Test");

        assertEquals("123", dto.getSourceAccountNumber());
        assertEquals("456", dto.getDestinationAccountNumber());
        assertEquals(100.0, dto.getAmount());
        assertEquals("IDR", dto.getCurrency());
        assertEquals("Test", dto.getDescription());
    }

    @Test
    public void testEqualsAndHashCode() {
        FundTransferRequestDTO dto1 = new FundTransferRequestDTO();
        dto1.setSourceAccountNumber("123");
        dto1.setAmount(100.0);

        FundTransferRequestDTO dto2 = new FundTransferRequestDTO();
        dto2.setSourceAccountNumber("123");
        dto2.setAmount(100.0);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setAmount(200.0);
        assertNotEquals(dto1, dto2);
    }

    @Test
    public void testToString() {
        FundTransferRequestDTO dto = new FundTransferRequestDTO();
        dto.setSourceAccountNumber("123");
        dto.setDestinationAccountNumber("456");
        dto.setAmount(100.0);
        dto.setCurrency("IDR");
        dto.setDescription("Test");

        String toString = dto.toString();
        assertEquals("FundTransferRequestDTO(sourceAccountNumber=123, destinationAccountNumber=456, amount=100.0, currency=IDR, description=Test)", toString);
    }
}
