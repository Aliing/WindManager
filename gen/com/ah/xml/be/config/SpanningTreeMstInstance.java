//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.01 at 11:29:17 AM CST 
//


package com.ah.xml.be.config;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spanning-tree-mst-instance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spanning-tree-mst-instance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vlan" type="{http://www.aerohive.com/configuration/general}ah-name-act-value-quote-prohibited" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="priority" type="{http://www.aerohive.com/configuration/general}ah-int-act" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spanning-tree-mst-instance", namespace = "http://www.aerohive.com/configuration/interface", propOrder = {
    "cr",
    "vlan",
    "priority"
})
public class SpanningTreeMstInstance {

    protected String cr;
    protected List<AhNameActValueQuoteProhibited> vlan;
    protected AhIntAct priority;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;

    /**
     * Gets the value of the cr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCr() {
        return cr;
    }

    /**
     * Sets the value of the cr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCr(String value) {
        this.cr = value;
    }

    /**
     * Gets the value of the vlan property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vlan property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVlan().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AhNameActValueQuoteProhibited }
     * 
     * 
     */
    public List<AhNameActValueQuoteProhibited> getVlan() {
        if (vlan == null) {
            vlan = new ArrayList<AhNameActValueQuoteProhibited>();
        }
        return this.vlan;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link AhIntAct }
     *     
     */
    public AhIntAct getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhIntAct }
     *     
     */
    public void setPriority(AhIntAct value) {
        this.priority = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link AhEnumActValue }
     *     
     */
    public AhEnumActValue getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhEnumActValue }
     *     
     */
    public void setOperation(AhEnumActValue value) {
        this.operation = value;
    }

}
