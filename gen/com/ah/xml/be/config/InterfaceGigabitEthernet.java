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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for interface-gigabit-ethernet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="interface-gigabit-ethernet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AH-DELTA-ASSISTANT" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="speed" type="{http://www.aerohive.com/configuration/interface}interface-speed" minOccurs="0"/>
 *         &lt;element name="duplex" type="{http://www.aerohive.com/configuration/interface}interface-duplex" minOccurs="0"/>
 *         &lt;element name="auto-mdix" type="{http://www.aerohive.com/configuration/interface}interface-auto-mdix" minOccurs="0"/>
 *         &lt;element name="flow-control" type="{http://www.aerohive.com/configuration/interface}interface-flow-control" minOccurs="0"/>
 *         &lt;element name="link-debounce" type="{http://www.aerohive.com/configuration/general}ah-int-act" minOccurs="0"/>
 *         &lt;element name="switchport" type="{http://www.aerohive.com/configuration/interface}interface-switchport" minOccurs="0"/>
 *         &lt;element name="mode" type="{http://www.aerohive.com/configuration/interface}geth-mode" minOccurs="0"/>
 *         &lt;element name="agg" type="{http://www.aerohive.com/configuration/general}ah-name" minOccurs="0"/>
 *         &lt;element name="spanning-tree" type="{http://www.aerohive.com/configuration/interface}interface-spanning-tree" minOccurs="0"/>
 *         &lt;element name="security-object" type="{http://www.aerohive.com/configuration/interface}geth-security-object" minOccurs="0"/>
 *         &lt;element name="dhcp" type="{http://www.aerohive.com/configuration/interface}ethx-dhcp" minOccurs="0"/>
 *         &lt;element name="ip" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *         &lt;element name="shutdown" type="{http://www.aerohive.com/configuration/general}ah-only-act" minOccurs="0"/>
 *         &lt;element name="storm-control" type="{http://www.aerohive.com/configuration/interface}interface-storm-control" minOccurs="0"/>
 *         &lt;element name="qos-classifier" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *         &lt;element name="qos-marker" type="{http://www.aerohive.com/configuration/general}ah-name-act" minOccurs="0"/>
 *         &lt;element name="qos-shaper" type="{http://www.aerohive.com/configuration/general}ah-int-act" minOccurs="0"/>
 *         &lt;element name="link-discovery" type="{http://www.aerohive.com/configuration/interface}interface-geth-link-discovery" minOccurs="0"/>
 *         &lt;element name="pse" type="{http://www.aerohive.com/configuration/interface}interface-geth-pse" minOccurs="0"/>
 *         &lt;element name="client-report" type="{http://www.aerohive.com/configuration/interface}interface-client-report" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.aerohive.com/configuration/general}ah-string-act" minOccurs="0"/>
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
@XmlType(name = "interface-gigabit-ethernet", namespace = "http://www.aerohive.com/configuration/interface", propOrder = {
    "ahdeltaassistant",
    "speed",
    "duplex",
    "autoMdix",
    "flowControl",
    "linkDebounce",
    "switchport",
    "mode",
    "agg",
    "spanningTree",
    "securityObject",
    "dhcp",
    "ip",
    "shutdown",
    "stormControl",
    "qosClassifier",
    "qosMarker",
    "qosShaper",
    "linkDiscovery",
    "pse",
    "clientReport",
    "description"
})
public class InterfaceGigabitEthernet {

    @XmlElement(name = "AH-DELTA-ASSISTANT")
    protected AhOnlyAct ahdeltaassistant;
    protected InterfaceSpeed speed;
    protected InterfaceDuplex duplex;
    @XmlElement(name = "auto-mdix")
    protected InterfaceAutoMdix autoMdix;
    @XmlElement(name = "flow-control")
    protected InterfaceFlowControl flowControl;
    @XmlElement(name = "link-debounce")
    protected AhIntAct linkDebounce;
    protected InterfaceSwitchport switchport;
    protected GethMode mode;
    protected AhName agg;
    @XmlElement(name = "spanning-tree")
    protected InterfaceSpanningTree spanningTree;
    @XmlElement(name = "security-object")
    protected GethSecurityObject securityObject;
    protected EthxDhcp dhcp;
    protected AhNameAct ip;
    protected AhOnlyAct shutdown;
    @XmlElement(name = "storm-control")
    protected InterfaceStormControl stormControl;
    @XmlElement(name = "qos-classifier")
    protected AhNameAct qosClassifier;
    @XmlElement(name = "qos-marker")
    protected AhNameAct qosMarker;
    @XmlElement(name = "qos-shaper")
    protected AhIntAct qosShaper;
    @XmlElement(name = "link-discovery")
    protected InterfaceGethLinkDiscovery linkDiscovery;
    protected InterfaceGethPse pse;
    @XmlElement(name = "client-report")
    protected InterfaceClientReport clientReport;
    protected AhStringAct description;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "operation", required = true)
    protected AhEnumActValue operation;

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
     * Gets the value of the speed property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceSpeed }
     *     
     */
    public InterfaceSpeed getSpeed() {
        return speed;
    }

    /**
     * Sets the value of the speed property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceSpeed }
     *     
     */
    public void setSpeed(InterfaceSpeed value) {
        this.speed = value;
    }

    /**
     * Gets the value of the duplex property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceDuplex }
     *     
     */
    public InterfaceDuplex getDuplex() {
        return duplex;
    }

    /**
     * Sets the value of the duplex property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceDuplex }
     *     
     */
    public void setDuplex(InterfaceDuplex value) {
        this.duplex = value;
    }

    /**
     * Gets the value of the autoMdix property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceAutoMdix }
     *     
     */
    public InterfaceAutoMdix getAutoMdix() {
        return autoMdix;
    }

    /**
     * Sets the value of the autoMdix property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceAutoMdix }
     *     
     */
    public void setAutoMdix(InterfaceAutoMdix value) {
        this.autoMdix = value;
    }

    /**
     * Gets the value of the flowControl property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceFlowControl }
     *     
     */
    public InterfaceFlowControl getFlowControl() {
        return flowControl;
    }

    /**
     * Sets the value of the flowControl property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceFlowControl }
     *     
     */
    public void setFlowControl(InterfaceFlowControl value) {
        this.flowControl = value;
    }

    /**
     * Gets the value of the linkDebounce property.
     * 
     * @return
     *     possible object is
     *     {@link AhIntAct }
     *     
     */
    public AhIntAct getLinkDebounce() {
        return linkDebounce;
    }

    /**
     * Sets the value of the linkDebounce property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhIntAct }
     *     
     */
    public void setLinkDebounce(AhIntAct value) {
        this.linkDebounce = value;
    }

    /**
     * Gets the value of the switchport property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceSwitchport }
     *     
     */
    public InterfaceSwitchport getSwitchport() {
        return switchport;
    }

    /**
     * Sets the value of the switchport property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceSwitchport }
     *     
     */
    public void setSwitchport(InterfaceSwitchport value) {
        this.switchport = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link GethMode }
     *     
     */
    public GethMode getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link GethMode }
     *     
     */
    public void setMode(GethMode value) {
        this.mode = value;
    }

    /**
     * Gets the value of the agg property.
     * 
     * @return
     *     possible object is
     *     {@link AhName }
     *     
     */
    public AhName getAgg() {
        return agg;
    }

    /**
     * Sets the value of the agg property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhName }
     *     
     */
    public void setAgg(AhName value) {
        this.agg = value;
    }

    /**
     * Gets the value of the spanningTree property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceSpanningTree }
     *     
     */
    public InterfaceSpanningTree getSpanningTree() {
        return spanningTree;
    }

    /**
     * Sets the value of the spanningTree property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceSpanningTree }
     *     
     */
    public void setSpanningTree(InterfaceSpanningTree value) {
        this.spanningTree = value;
    }

    /**
     * Gets the value of the securityObject property.
     * 
     * @return
     *     possible object is
     *     {@link GethSecurityObject }
     *     
     */
    public GethSecurityObject getSecurityObject() {
        return securityObject;
    }

    /**
     * Sets the value of the securityObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link GethSecurityObject }
     *     
     */
    public void setSecurityObject(GethSecurityObject value) {
        this.securityObject = value;
    }

    /**
     * Gets the value of the dhcp property.
     * 
     * @return
     *     possible object is
     *     {@link EthxDhcp }
     *     
     */
    public EthxDhcp getDhcp() {
        return dhcp;
    }

    /**
     * Sets the value of the dhcp property.
     * 
     * @param value
     *     allowed object is
     *     {@link EthxDhcp }
     *     
     */
    public void setDhcp(EthxDhcp value) {
        this.dhcp = value;
    }

    /**
     * Gets the value of the ip property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameAct }
     *     
     */
    public AhNameAct getIp() {
        return ip;
    }

    /**
     * Sets the value of the ip property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameAct }
     *     
     */
    public void setIp(AhNameAct value) {
        this.ip = value;
    }

    /**
     * Gets the value of the shutdown property.
     * 
     * @return
     *     possible object is
     *     {@link AhOnlyAct }
     *     
     */
    public AhOnlyAct getShutdown() {
        return shutdown;
    }

    /**
     * Sets the value of the shutdown property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhOnlyAct }
     *     
     */
    public void setShutdown(AhOnlyAct value) {
        this.shutdown = value;
    }

    /**
     * Gets the value of the stormControl property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceStormControl }
     *     
     */
    public InterfaceStormControl getStormControl() {
        return stormControl;
    }

    /**
     * Sets the value of the stormControl property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceStormControl }
     *     
     */
    public void setStormControl(InterfaceStormControl value) {
        this.stormControl = value;
    }

    /**
     * Gets the value of the qosClassifier property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameAct }
     *     
     */
    public AhNameAct getQosClassifier() {
        return qosClassifier;
    }

    /**
     * Sets the value of the qosClassifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameAct }
     *     
     */
    public void setQosClassifier(AhNameAct value) {
        this.qosClassifier = value;
    }

    /**
     * Gets the value of the qosMarker property.
     * 
     * @return
     *     possible object is
     *     {@link AhNameAct }
     *     
     */
    public AhNameAct getQosMarker() {
        return qosMarker;
    }

    /**
     * Sets the value of the qosMarker property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhNameAct }
     *     
     */
    public void setQosMarker(AhNameAct value) {
        this.qosMarker = value;
    }

    /**
     * Gets the value of the qosShaper property.
     * 
     * @return
     *     possible object is
     *     {@link AhIntAct }
     *     
     */
    public AhIntAct getQosShaper() {
        return qosShaper;
    }

    /**
     * Sets the value of the qosShaper property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhIntAct }
     *     
     */
    public void setQosShaper(AhIntAct value) {
        this.qosShaper = value;
    }

    /**
     * Gets the value of the linkDiscovery property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceGethLinkDiscovery }
     *     
     */
    public InterfaceGethLinkDiscovery getLinkDiscovery() {
        return linkDiscovery;
    }

    /**
     * Sets the value of the linkDiscovery property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceGethLinkDiscovery }
     *     
     */
    public void setLinkDiscovery(InterfaceGethLinkDiscovery value) {
        this.linkDiscovery = value;
    }

    /**
     * Gets the value of the pse property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceGethPse }
     *     
     */
    public InterfaceGethPse getPse() {
        return pse;
    }

    /**
     * Sets the value of the pse property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceGethPse }
     *     
     */
    public void setPse(InterfaceGethPse value) {
        this.pse = value;
    }

    /**
     * Gets the value of the clientReport property.
     * 
     * @return
     *     possible object is
     *     {@link InterfaceClientReport }
     *     
     */
    public InterfaceClientReport getClientReport() {
        return clientReport;
    }

    /**
     * Sets the value of the clientReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterfaceClientReport }
     *     
     */
    public void setClientReport(InterfaceClientReport value) {
        this.clientReport = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link AhStringAct }
     *     
     */
    public AhStringAct getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link AhStringAct }
     *     
     */
    public void setDescription(AhStringAct value) {
        this.description = value;
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