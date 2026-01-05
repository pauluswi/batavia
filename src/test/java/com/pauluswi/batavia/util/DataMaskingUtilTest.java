package com.pauluswi.batavia.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DataMaskingUtilTest {

    @Test
    public void testMask() {
        assertEquals("****", DataMaskingUtil.mask(null));
        assertEquals("****", DataMaskingUtil.mask("123"));
        assertEquals("****", DataMaskingUtil.mask("1234"));
        assertEquals("12**56", DataMaskingUtil.mask("123456"));
        assertEquals("12******90", DataMaskingUtil.mask("1234567890"));
    }

    @Test
    public void testMaskIso20022() {
        assertNull(DataMaskingUtil.maskIso20022(null));
        
        String xml = "<Id>1234567890</Id>";
        String expected = "<Id>12******90</Id>";
        assertEquals(expected, DataMaskingUtil.maskIso20022(xml));

        xml = "<Id><Othr><Id>1234567890</Id></Othr></Id>";
        expected = "<Id><Othr><Id>12******90</Id></Othr></Id>";
        assertEquals(expected, DataMaskingUtil.maskIso20022(xml));

        xml = "<Nm>John Doe</Nm>";
        expected = "<Nm>Jo****oe</Nm>";
        assertEquals(expected, DataMaskingUtil.maskIso20022(xml));
        
        // Test multiple occurrences
        xml = "<Id>123456</Id><Nm>Alice</Nm>";
        expected = "<Id>12**56</Id><Nm>Al*ce</Nm>";
        assertEquals(expected, DataMaskingUtil.maskIso20022(xml));
    }

    @Test
    public void testMaskIso8583Log() {
        assertNull(DataMaskingUtil.maskIso8583Log(null));

        String log = "Field 2: 1234567890123456, Field 3: 000000,";
        String expected = "Field 2: 12************56, Field 3: 000000,";
        assertEquals(expected, DataMaskingUtil.maskIso8583Log(log));

        log = "Field 102: 1234567890,";
        expected = "Field 102: 12******90,";
        assertEquals(expected, DataMaskingUtil.maskIso8583Log(log));

        log = "Field 103: John Doe,";
        expected = "Field 103: Jo****oe,";
        assertEquals(expected, DataMaskingUtil.maskIso8583Log(log));
        
        // Test multiple fields
        log = "Field 2: 11112222, Field 102: 33334444,";
        expected = "Field 2: 11****22, Field 102: 33****44,";
        assertEquals(expected, DataMaskingUtil.maskIso8583Log(log));
    }
}
