package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Othr {

    @JacksonXmlProperty(localName = "Id")
    private String id;
}
