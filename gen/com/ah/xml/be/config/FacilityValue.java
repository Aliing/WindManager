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
 * <p>Java class for facility-value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="facility-value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="auth"/>
 *     &lt;enumeration value="authpriv"/>
 *     &lt;enumeration value="local0"/>
 *     &lt;enumeration value="local1"/>
 *     &lt;enumeration value="local2"/>
 *     &lt;enumeration value="local3"/>
 *     &lt;enumeration value="local4"/>
 *     &lt;enumeration value="local5"/>
 *     &lt;enumeration value="local6"/>
 *     &lt;enumeration value="local7"/>
 *     &lt;enumeration value="security"/>
 *     &lt;enumeration value="user"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "facility-value", namespace = "http://www.aerohive.com/configuration/logging")
@XmlEnum
public enum FacilityValue {

    @XmlEnumValue("auth")
    AUTH("auth"),
    @XmlEnumValue("authpriv")
    AUTHPRIV("authpriv"),
    @XmlEnumValue("local0")
    LOCAL_0("local0"),
    @XmlEnumValue("local1")
    LOCAL_1("local1"),
    @XmlEnumValue("local2")
    LOCAL_2("local2"),
    @XmlEnumValue("local3")
    LOCAL_3("local3"),
    @XmlEnumValue("local4")
    LOCAL_4("local4"),
    @XmlEnumValue("local5")
    LOCAL_5("local5"),
    @XmlEnumValue("local6")
    LOCAL_6("local6"),
    @XmlEnumValue("local7")
    LOCAL_7("local7"),
    @XmlEnumValue("security")
    SECURITY("security"),
    @XmlEnumValue("user")
    USER("user");
    private final String value;

    FacilityValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FacilityValue fromValue(String v) {
        for (FacilityValue c: FacilityValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
