//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spanning-tree-bpdu-protection-value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="spanning-tree-bpdu-protection-value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="bpdu-guard"/>
 *     &lt;enumeration value="bpdu-filter"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "spanning-tree-bpdu-protection-value", namespace = "http://www.aerohive.com/configuration/interface")
@XmlEnum
public enum SpanningTreeBpduProtectionValue {

    @XmlEnumValue("bpdu-guard")
    BPDU_GUARD("bpdu-guard"),
    @XmlEnumValue("bpdu-filter")
    BPDU_FILTER("bpdu-filter");
    private final String value;

    SpanningTreeBpduProtectionValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SpanningTreeBpduProtectionValue fromValue(String v) {
        for (SpanningTreeBpduProtectionValue c: SpanningTreeBpduProtectionValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
