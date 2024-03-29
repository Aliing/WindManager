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
 * <p>Java class for spanning-tree-mode-value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="spanning-tree-mode-value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="stp"/>
 *     &lt;enumeration value="rstp"/>
 *     &lt;enumeration value="mstp"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "spanning-tree-mode-value", namespace = "http://www.aerohive.com/configuration/interface")
@XmlEnum
public enum SpanningTreeModeValue {

    @XmlEnumValue("stp")
    STP("stp"),
    @XmlEnumValue("rstp")
    RSTP("rstp"),
    @XmlEnumValue("mstp")
    MSTP("mstp");
    private final String value;

    SpanningTreeModeValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SpanningTreeModeValue fromValue(String v) {
        for (SpanningTreeModeValue c: SpanningTreeModeValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
