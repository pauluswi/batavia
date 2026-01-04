package com.pauluswi.batavia.iso20022;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class AcctInf {

    @JacksonXmlProperty(localName = "CIF")
    private String cif;

    @JacksonXmlProperty(localName = "Name")
    private String name;
}
