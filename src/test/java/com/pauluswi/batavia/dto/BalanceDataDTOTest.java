package com.pauluswi.batavia.dto;

import org.junit.jupiter.api.Test;

import com.pauluswi.batavia.dto.BalanceDataDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BalanceDataDTOTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        BalanceDataDTO balanceData = new BalanceDataDTO();
        balanceData.setBankAccountNumber("1234567890");
        balanceData.setCustomerFullName("Ahmad Subarjo");
        balanceData.setBalance(1500.00);

        // Assert
        assertEquals("1234567890", balanceData.getBankAccountNumber());
        assertEquals("Ahmad Subarjo", balanceData.getCustomerFullName());
        assertEquals(1500.00, balanceData.getBalance());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        BalanceDataDTO balanceData1 = new BalanceDataDTO();
        balanceData1.setBankAccountNumber("1234567890");
        balanceData1.setCustomerFullName("Ahmad Subarjo");
        balanceData1.setBalance(1500.00);

        BalanceDataDTO balanceData2 = new BalanceDataDTO();
        balanceData2.setBankAccountNumber("1234567890");
        balanceData2.setCustomerFullName("Ahmad Subarjo");
        balanceData2.setBalance(1500.00);

        // Assert
        assertEquals(balanceData1, balanceData2);
        assertEquals(balanceData1.hashCode(), balanceData2.hashCode());

        // Modify one object and verify they are no longer equal
        balanceData2.setBalance(2000.00);
        assertNotEquals(balanceData1, balanceData2);
        assertNotEquals(balanceData1.hashCode(), balanceData2.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        BalanceDataDTO balanceData = new BalanceDataDTO();
        balanceData.setBankAccountNumber("1234567890");
        balanceData.setCustomerFullName("Ahmad Subarjo");
        balanceData.setBalance(1500.00);

        // Act
        String toStringOutput = balanceData.toString();

        // Assert - Check if the toString output contains the field values
        assertEquals("BalanceDataDTO(bankAccountNumber=1234567890, customerFullName=Ahmad Subarjo, balance=1500.0)", toStringOutput);
    }
}
