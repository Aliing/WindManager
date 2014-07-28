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
 * <p>Java class for LDAP-auth-verify-server-value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LDAP-auth-verify-server-value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="demand"/>
 *     &lt;enumeration value="never"/>
 *     &lt;enumeration value="try"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LDAP-auth-verify-server-value", namespace = "http://www.aerohive.com/configuration/aaa")
@XmlEnum
public enum LDAPAuthVerifyServerValue {

    @XmlEnumValue("demand")
    DEMAND("demand"),
    @XmlEnumValue("never")
    NEVER("never"),
    @XmlEnumValue("try")
    TRY("try");
    private final String value;

    LDAPAuthVerifyServerValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LDAPAuthVerifyServerValue fromValue(String v) {
        for (LDAPAuthVerifyServerValue c: LDAPAuthVerifyServerValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}