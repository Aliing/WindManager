//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bonjour-gateway-rule-to complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bonjour-gateway-rule-to">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="metric" type="{http://www.aerohive.com/configuration/userProfile}bonjour-gateway-rule-metric" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bonjour-gateway-rule-to", namespace = "http://www.aerohive.com/configuration/userProfile", propOrder = {
    "metric"
})
public class BonjourGatewayRuleTo {

    protected BonjourGatewayRuleMetric metric;
    @XmlAttribute(name = "value")
    protected String value;

    /**
     * Gets the value of the metric property.
     * 
     * @return
     *     possible object is
     *     {@link BonjourGatewayRuleMetric }
     *     
     */
    public BonjourGatewayRuleMetric getMetric() {
        return metric;
    }

    /**
     * Sets the value of the metric property.
     * 
     * @param value
     *     allowed object is
     *     {@link BonjourGatewayRuleMetric }
     *     
     */
    public void setMetric(BonjourGatewayRuleMetric value) {
        this.metric = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}