package com.pauluswi.batavia.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FundTransferResponseDTOTest {

    @Test
    public void testGettersAndSetters() {
        FundTransferResponseDTO dto = new FundTransferResponseDTO();
        dto.setResponseCode("00");
        dto.setTransactionId("TRX123");
        dto.setMessage("Success");

        assertEquals("00", dto.getResponseCode());
        assertEquals("TRX123", dto.getTransactionId());
        assertEquals("Success", dto.getMessage());
    }

    @Test
    public void testEqualsAndHashCode() {
        FundTransferResponseDTO dto1 = new FundTransferResponseDTO();
        dto1.setResponseCode("00");
        dto1.setTransactionId("TRX123");

        FundTransferResponseDTO dto2 = new FundTransferResponseDTO();
        dto2.setResponseCode("00");
        dto2.setTransactionId("TRX123");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setTransactionId("TRX999");
        assertNotEquals(dto1, dto2);
    }

    @Test
    public void testToString() {
        FundTransferResponseDTO dto = new FundTransferResponseDTO();
        dto.setResponseCode("00");
        dto.setTransactionId("TRX123");
        dto.setMessage("Success");

        String toString = dto.toString();
        assertEquals("FundTransferResponseDTO(responseCode=00, transactionId=TRX123, message=Success)", toString);
    }
}
