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
 * <p>Java class for mobility-policy-obj complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mobility-policy-obj">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dnxp" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="nomadic-roaming" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                   &lt;element name="unroam-threshold" type="{http://www.aerohive.com/configuration/general}ah-string-act-quote-prohibited" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="inxp" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="gre-tunnel" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *                             &lt;element name="from" type="{http://www.aerohive.com/configuration/policy}mobility-policy-from" maxOccurs="unbounded" minOccurs="0"/>
 *                             &lt;element name="to" type="{http://www.aerohive.com/configuration/policy}mobility-policy-to" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operation" use="required" type="{http://www.aerohive.com/configuration/general}ah-enum-act-value" />
 *       &lt;attribute name="updateTime" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mobility-policy-obj", namespace = "http://www.aerohive.com/configuration/policy", propOrder = {
    "dnxp",
    "inxp"
})
public class MobilityPolicyObj {

    protected MobilityPolicyObj.Dnxp dnxp;
    protected MobilityPolicyObj.Inxp inxp;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;
    @XmlAttribute(name = "updateTime")
    protected String updateTime;

    /**
     * Gets the value of the dnxp property.
     * 
     * @return
     *     possible object is
     *     {@link MobilityPolicyObj.Dnxp }
     *     
     */
    public MobilityPolicyObj.Dnxp getDnxp() {
        return dnxp;
    }

    /**
     * Sets the value of the dnxp property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobilityPolicyObj.Dnxp }
     *     
     */
    public void setDnxp(MobilityPolicyObj.Dnxp value) {
        this.dnxp = value;
    }

    /**
     * Gets the value of the inxp property.
     * 
     * @return
     *     possible object is
     *     {@link MobilityPolicyObj.Inxp }
     *     
     */
    public MobilityPolicyObj.Inxp getInxp() {
        return inxp;
    }

    /**
     * Sets the value of the inxp property.
     * 
     * @param value
     *     allowed object is
     *     {@link MobilityPolicyObj.Inxp }
     *     
     */
    public void setInxp(MobilityPolicyObj.Inxp value) {
        this.inxp = value;
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

    /**
     * Gets the value of the updateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * Sets the value of the updateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateTime(String value) {
        this.updateTime = value;
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
     *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *         &lt;element name="cr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="nomadic-roaming" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *         &lt;element name="unroam-threshold" type="{http://www.aerohive.com/configuration/general}ah-string-act-quote-prohibited" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ahdeltaassistant",
        "cr",
        "nomadicRoaming",
        "unroamThreshold"
    })
    public static class Dnxp {

        @XmlElement(name = "AH-DELTA-ASSISTANT")
        protected AhOnlyAct ahdeltaassistant;
        protected String cr;
        @XmlElement(name = "nomadic-roaming")
        protected AhOnlyAct nomadicRoaming;
        @XmlElement(name = "unroam-threshold")
        protected AhStringActQuoteProhibited unroamThreshold;

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
         * Gets the value of the nomadicRoaming property.
         * 
         * @return
         *     possible object is
         *     {@link AhOnlyAct }
         *     
         */
        public AhOnlyAct getNomadicRoaming() {
            return nomadicRoaming;
        }

        /**
         * Sets the value of the nomadicRoaming property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhOnlyAct }
         *     
         */
        public void setNomadicRoaming(AhOnlyAct value) {
            this.nomadicRoaming = value;
        }

        /**
         * Gets the value of the unroamThreshold property.
         * 
         * @return
         *     possible object is
         *     {@link AhStringActQuoteProhibited }
         *     
         */
        public AhStringActQuoteProhibited getUnroamThreshold() {
            return unroamThreshold;
        }

        /**
         * Sets the value of the unroamThreshold property.
         * 
         * @param value
         *     allowed object is
         *     {@link AhStringActQuoteProhibited }
         *     
         */
        public void setUnroamThreshold(AhStringActQuoteProhibited value) {
            this.unroamThreshold = value;
        }

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
     *         &lt;element name="gre-tunnel" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
     *                   &lt;element name="from" type="{http://www.aerohive.com/configuration/policy}mobility-policy-from" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element name="to" type="{http://www.aerohive.com/configuration/policy}mobility-policy-to" minOccurs="0"/>
     *                 &lt;/sequence>
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
    @XmlType(name = "", propOrder = {
        "greTunnel"
    })
    public static class Inxp {

        @XmlElement(name = "gre-tunnel")
        protected MobilityPolicyObj.Inxp.GreTunnel greTunnel;

        /**
         * Gets the value of the greTunnel property.
         * 
         * @return
         *     possible object is
         *     {@link MobilityPolicyObj.Inxp.GreTunnel }
         *     
         */
        public MobilityPolicyObj.Inxp.GreTunnel getGreTunnel() {
            return greTunnel;
        }

        /**
         * Sets the value of the greTunnel property.
         * 
         * @param value
         *     allowed object is
         *     {@link MobilityPolicyObj.Inxp.GreTunnel }
         *     
         */
        public void setGreTunnel(MobilityPolicyObj.Inxp.GreTunnel value) {
            this.greTunnel = value;
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
         *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
         *         &lt;element name="from" type="{http://www.aerohive.com/configuration/policy}mobility-policy-from" maxOccurs="unbounded" minOccurs="0"/>
         *         &lt;element name="to" type="{http://www.aerohive.com/configuration/policy}mobility-policy-to" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "ahdeltaassistant",
            "from",
            "to"
        })
        public static class GreTunnel {

            @XmlElement(name = "AH-DELTA-ASSISTANT")
            protected AhOnlyAct ahdeltaassistant;
            protected List<MobilityPolicyFrom> from;
            protected MobilityPolicyTo to;

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
             * Gets the value of the from property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the from property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getFrom().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link MobilityPolicyFrom }
             * 
             * 
             */
            public List<MobilityPolicyFrom> getFrom() {
                if (from == null) {
                    from = new ArrayList<MobilityPolicyFrom>();
                }
                return this.from;
            }

            /**
             * Gets the value of the to property.
             * 
             * @return
             *     possible object is
             *     {@link MobilityPolicyTo }
             *     
             */
            public MobilityPolicyTo getTo() {
                return to;
            }

            /**
             * Sets the value of the to property.
             * 
             * @param value
             *     allowed object is
             *     {@link MobilityPolicyTo }
             *     
             */
            public void setTo(MobilityPolicyTo value) {
                this.to = value;
            }

        }

    }

}