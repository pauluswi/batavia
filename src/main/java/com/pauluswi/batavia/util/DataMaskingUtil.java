package com.pauluswi.batavia.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMaskingUtil {

    // Regex for XML tags (ISO 20022)
    private static final Pattern XML_ACCOUNT_PATTERN = Pattern.compile("(<Id>|<Id><Othr><Id>)([^<]+)(</Id>|</Id></Othr></Id>)");
    private static final Pattern XML_NAME_PATTERN = Pattern.compile("(<Nm>)([^<]+)(</Nm>)");

    /**
     * Masks sensitive data in a string.
     * Simple implementation: keeps first 2 and last 2 chars, masks the rest.
     */
    public static String mask(String input) {
        if (input == null || input.length() <= 4) {
            return "****";
        }
        int length = input.length();
        return input.substring(0, 2) + "*".repeat(length - 4) + input.substring(length - 2);
    }

    /**
     * Masks sensitive fields in an ISO 20022 XML string.
     */
    public static String maskIso20022(String xml) {
        if (xml == null) return null;
        
        String masked = xml;
        
        // Mask Account Numbers
        Matcher accountMatcher = XML_ACCOUNT_PATTERN.matcher(masked);
        StringBuffer sb = new StringBuffer();
        while (accountMatcher.find()) {
            accountMatcher.appendReplacement(sb, accountMatcher.group(1) + mask(accountMatcher.group(2)) + accountMatcher.group(3));
        }
        accountMatcher.appendTail(sb);
        masked = sb.toString();

        // Mask Names (PII)
        Matcher nameMatcher = XML_NAME_PATTERN.matcher(masked);
        sb = new StringBuffer();
        while (nameMatcher.find()) {
            nameMatcher.appendReplacement(sb, nameMatcher.group(1) + mask(nameMatcher.group(2)) + nameMatcher.group(3));
        }
        nameMatcher.appendTail(sb);
        
        return sb.toString();
    }

    /**
     * Masks sensitive fields in an ISO 8583 message string (simple key-value representation).
     * Since the current implementation logs "Field X: Value", we can target specific fields.
     * 
     * Field 2: PAN (Primary Account Number)
     * Field 102: Account Number 1
     * Field 103: Account Number 2 / Name
     */
    public static String maskIso8583Log(String logMessage) {
        if (logMessage == null) return null;
        
        // Regex to find "Field 102: value," or "Field 2: value,"
        // We target specific sensitive fields: 2 (PAN), 34 (PAN ext), 102 (Account), 103 (Account/Name)
        Pattern fieldPattern = Pattern.compile("(Field (?:2|34|102|103): )([^,]+)(,)");
        
        Matcher matcher = fieldPattern.matcher(logMessage);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + mask(matcher.group(2)) + matcher.group(3));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
}
