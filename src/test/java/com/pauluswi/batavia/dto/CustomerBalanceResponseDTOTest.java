package com.pauluswi.batavia.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CustomerBalanceResponseDTOTest {

    @Test
    public void testGettersAndSetters() {
        CustomerBalanceResponseDTO dto = new CustomerBalanceResponseDTO();
        dto.setMTI("0210");
        dto.setResponseCode("00");
        BalanceDataDTO data = new BalanceDataDTO();
        dto.setData(data);

        assertEquals("0210", dto.getMTI());
        assertEquals("00", dto.getResponseCode());
        assertEquals(data, dto.getData());
    }

    @Test
    public void testEqualsAndHashCode() {
        CustomerBalanceResponseDTO dto1 = new CustomerBalanceResponseDTO();
        dto1.setMTI("0210");
        dto1.setResponseCode("00");

        CustomerBalanceResponseDTO dto2 = new CustomerBalanceResponseDTO();
        dto2.setMTI("0210");
        dto2.setResponseCode("00");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setResponseCode("99");
        assertNotEquals(dto1, dto2);
    }

    @Test
    public void testToString() {
        CustomerBalanceResponseDTO dto = new CustomerBalanceResponseDTO();
        dto.setMTI("0210");
        dto.setResponseCode("00");

        String toString = dto.toString();
        assertEquals("CustomerBalanceResponseDTO(MTI=0210, responseCode=00, data=null)", toString);
    }
}
