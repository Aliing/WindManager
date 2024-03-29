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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for airscreen-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="airscreen-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="action" type="{http://www.aerohive.com/configuration/others}airscreen-action" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="source" type="{http://www.aerohive.com/configuration/others}airscreen-source" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="behavior" type="{http://www.aerohive.com/configuration/others}airscreen-behavior" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="rule" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="action" type="{http://www.aerohive.com/configuration/general}ah-name-act" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="source" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *                   &lt;element name="behavior" type="{http://www.aerohive.com/configuration/general}ah-name-act" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "airscreen-obj", propOrder = {
    "ahdeltaassistant",
    "action",
    "source",
    "behavior",
    "rule"
})
public class AirscreenObj {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected List<AirscreenAction> action;
    protected List<AirscreenSource> source;
    protected List<AirscreenBehavior> behavior;
    protected List<AirscreenObj.Rule> rule;

    /**
     * Gets the value of the ahdeltaassistant property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getAHDELTAASSISTANT() {
        return ahdeltaassistant;
    }

    /**
     * Sets the value of the ahdeltaassistant property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setAHDELTAASSISTANT(AhOnlyAct value) {
        this.ahdeltaassistant = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the action property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AirscreenAction }
     * 
     * 
     */
    public List<AirscreenAction> getAction() {
        if (action == null) {
            action = new ArrayList<AirscreenAction>();
        }
        return this.action;
    }

    /**
     * Gets the value of the source property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the source property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AirscreenSource }
     * 
     * 
     */
    public List<AirscreenSource> getSource() {
        if (source == null) {
            source = new ArrayList<AirscreenSource>();
        }
        return this.source;
    }

    /**
     * Gets the value of the behavior property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the behavior property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBehavior().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AirscreenBehavior }
     * 
     * 
     */
    public List<AirscreenBehavior> getBehavior() {
        if (behavior == null) {
            behavior = new ArrayList<AirscreenBehavior>();
        }
        return this.behavior;
    }

    /**
     * Gets the value of the rule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AirscreenObj.Rule }
     * 
     * 
     */
    public List<AirscreenObj.Rule> getRule() {
        if (rule == null) {
            rule = new ArrayList<AirscreenObj.Rule>();
        }
        return this.rule;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="action" type="{http://www.aerohive.com/configuration/general}ah-name-act" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="source" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
     *         &lt;element name="behavior" type="{http://www.aerohive.com/configuration/general}ah-name-act" maxOccurs="unbounded" minOccurs="0"/>
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
    @XmlType(name = "", propOrder = {
        "cr",
        "action",
        "source",
        "behavior"
    })
    public static class Rule {

        protected String cr;
        protected List<AhNameAct> action;
        protected AhNameAct source;
        protected List<AhNameAct> behavior;
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
         * Gets the value of the action property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the action property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAction().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AhNameAct }
         * 
         * 
         */
        public List<AhNameAct> getAction() {
            if (action == null) {
                action = new ArrayList<AhNameAct>();
            }
            return this.action;
        }

        /**
         * Gets the value of the source property.
         * 
         * @return
         *     possible object is
         *     {@link AhNameAct }
         *     
         */
        public AhNameAct getSource() {
            return source;
        }

        /**
         * Sets the value of the source property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhNameAct }
         *     
         */
        public void setSource(AhNameAct value) {
            this.source = value;
        }

        /**
         * Gets the value of the behavior property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the behavior property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBehavior().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AhNameAct }
         * 
         * 
         */
        public List<AhNameAct> getBehavior() {
            if (behavior == null) {
                behavior = new ArrayList<AhNameAct>();
            }
            return this.behavior;
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

}
