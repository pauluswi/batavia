package com.pauluswi.batavia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryIdempotencyServiceTest {

    private InMemoryIdempotencyService idempotencyService;

    @BeforeEach
    public void setUp() {
        idempotencyService = new InMemoryIdempotencyService();
    }

    @Test
    public void testPutAndGet() {
        String key = "req-123";
        String value = "response-data";

        idempotencyService.put(key, value);

        assertTrue(idempotencyService.contains(key));
        assertEquals(value, idempotencyService.get(key));
    }

    @Test
    public void testContains_False() {
        assertFalse(idempotencyService.contains("non-existent-key"));
    }

    @Test
    public void testGet_Null() {
        assertNull(idempotencyService.get("non-existent-key"));
    }
}
