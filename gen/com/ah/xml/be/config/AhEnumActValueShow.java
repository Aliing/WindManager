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
 * <p>Java class for ah-enum-act-value-show.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ah-enum-act-value-show">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="yesWithValue"/>
 *     &lt;enumeration value="noWithValue"/>
 *     &lt;enumeration value="yesWithShow"/>
 *     &lt;enumeration value="noWithHidden"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ah-enum-act-value-show", namespace = "http://www.aerohive.com/configuration/general")
@XmlEnum
public enum AhEnumActValueShow {

    @XmlEnumValue("yesWithValue")
    YES_WITH_VALUE("yesWithValue"),
    @XmlEnumValue("noWithValue")
    NO_WITH_VALUE("noWithValue"),
    @XmlEnumValue("yesWithShow")
    YES_WITH_SHOW("yesWithShow"),
    @XmlEnumValue("noWithHidden")
    NO_WITH_HIDDEN("noWithHidden");
    private final String value;

    AhEnumActValueShow(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AhEnumActValueShow fromValue(String v) {
        for (AhEnumActValueShow c: AhEnumActValueShow.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
