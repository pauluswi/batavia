package com.pauluswi.batavia.service.demo;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class ISO20022ResponseParser {
    
    public static String getBalance(String xmlResponse) {
        return extractValueFromXml(xmlResponse, "//*[local-name()='Amt']");
    }

    public static String getCustomerFullName(String xmlResponse) {
        return extractValueFromXml(xmlResponse, "//*[local-name()='Name']");
    }

    public static String getBankAccountNumber(String xmlResponse) {
        return extractValueFromXml(xmlResponse, "//*[local-name()='Id']");
    }

    private static String extractValueFromXml(String xmlResponse, String expression) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression xPathExpression = xpath.compile(expression);
            Node node = (Node) xPathExpression.evaluate(doc, XPathConstants.NODE);

            return node != null ? node.getTextContent() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
