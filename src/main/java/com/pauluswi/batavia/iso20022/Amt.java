package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
public class Amt {

    @JacksonXmlProperty(isAttribute = true, localName = "Ccy")
    private String ccy;

    @JacksonXmlText
    private String value;
}
