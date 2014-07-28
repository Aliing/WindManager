package com.ah.be.config.xml.compare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
//import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ah.be.config.AhConfigConvertedException;
import com.ah.be.config.create.common.CompleteRunXml;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.Tracer;

public final class AhConfigComparator {

	private static final Tracer log = new Tracer(AhConfigComparator.class.getSimpleName());

	private final String AH_DIFF_XML_ROOT_ELEM = "deltaConfig";
	private final String AH_CLI_SCHEMA_XML_ELEM_CR = "cr";
	private final String AH_CLI_SCHEMA_XML_ELEM_ASSISTANT = "AH-DELTA-ASSISTANT";
	private final String AH_CLI_SCHEMA_XML_ATTR_NAME = "name";
	private final String AH_CLI_SCHEMA_XML_ATTR_VALUE = "value";
//	private final String AH_CLI_SCHEMA_XML_ATTR_UPDATE_TIME = "updateTime";
	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION = "operation";
	private final String AH_CLI_SCHEMA_XML_ATTR_QUOTE_PROHIBITED = "quoteProhibited";
	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES = "yes";
	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO = "no";
	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES_WITH_VALUE = "yesWithValue";
	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_VALUE = "noWithValue";
	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES_WITH_SHOW = "yesWithShow";
	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_HIDDEN = "noWithHidden";
//	private final String AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_CHILD = "noChild";
	private final String AH_CLI_SUFFIX = "\n";

	public enum FormatMode {
		COMPACT, PRETTY
	}

	private String encoding = "UTF-8";
	private final HiveAp hiveAp;
	private final File oldConfig;
	private final File newConfig;
	private final Document defValueDoc;
	private List<?> oldConfigElems;
	private List<Element> newConfigElems;
	private final static List<String> specialChars = new LinkedList<String>();
	private final static List<String> escapingChars = new LinkedList<String>();

	static {
		// Load special and escaping characters from resource file hmConfig.properties.
//		ResourceBundle bundle = ResourceBundle.getBundle("resources.hmConfig");
		String[] attrKeys = new String[] {"ap.config.special.char.set", "ap.config.escaping.char.set"};

		for (String attrKey : attrKeys) {
//			String value = bundle.getString(attrKey);
			String value = System.getProperty(attrKey);

			if (value != null) {
				// Break the complex string consists of multiple hex numbers separated by commas up.
				for (StringTokenizer token = new StringTokenizer(value.trim(), ","); token.hasMoreTokens();) {
					String hexStr = token.nextToken().toUpperCase();

					// Remove the hex identifier.
					if (hexStr.startsWith("0X")) {
						hexStr = hexStr.substring(2);
					}

					int intValue = Integer.parseInt(hexStr, 16);
					String s = new String(new int[] {intValue}, 0, 1);

					if (attrKey.contains("special")) {
						specialChars.add(s);
					} else {
						escapingChars.add(s);
					}
				}
			}
		}
	}

	public AhConfigComparator(File oldConfig, File newConfig, HiveAp hiveAp) {
		this.oldConfig = oldConfig;
		this.newConfig = newConfig;
		this.hiveAp = hiveAp;
		this.defValueDoc = CompleteRunXml.getDefValueXml(hiveAp, hiveAp.getSoftVer());
	}

	public AhConfigComparator(File oldConfig, File newConfig, HiveAp hiveAp, String encoding) {
		this(oldConfig, newConfig, hiveAp);
		this.encoding = encoding;
	}

	public AhConfigComparator(String oldConfigPath, String newConfigPath, HiveAp hiveAp) {
		this(new File(oldConfigPath), new File(newConfigPath), hiveAp);
	}

	public AhConfigComparator(String oldConfigPath, String newConfigPath, HiveAp hiveAp, String encoding) {
		this(oldConfigPath, newConfigPath, hiveAp);
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	/**
	 * <p>Generate XML using with the specified file path, encoding and format mode.</p>
	 *
	 * @param document Document to format.
	 * @param filePath The path in which the XML is saved.
	 * @param encoding Text encoding to write the XML.
	 * @param formatMode The mode of formatting to format XML.
	 * @throws IOException if there is any problem in writing XML.
	 */
	public static void generateXml(Document document, String filePath, String encoding, FormatMode formatMode) throws IOException {
		OutputFormat format;

		switch (formatMode) {
			case COMPACT:
				format = OutputFormat.createCompactFormat();
				break;
			case PRETTY:
				format = OutputFormat.createPrettyPrint();
				break;
			default:
				throw new AssertionError("Unexpected Format Mode: " + formatMode.toString());
		}

		format.setEncoding(encoding);

		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(filePath, false), encoding), format);
		writer.write(document);
		writer.flush();
		writer.close();
	}

	/**
	 * <p>Generate XML with the specified file path, encoding and pretty printing format.</p>
	 *
	 * @param doc Document to format.
	 * @param filePath The path in which the XML is saved.
	 * @param encoding Text encoding to write the XML.
	 * @throws IOException if there is any problem in writing XML.
	 */
	public static void generateXml(Document doc, String filePath, String encoding) throws IOException {
		generateXml(doc, filePath, encoding, FormatMode.PRETTY);
	}

	/**
	 * <p>Format the XML with the specified encoding and format mode.</p>
	 *
	 * @param xml which is going  to be formatted.
	 * @param encoding Text encoding to write the XML.
	 * @param formatMode The mode of formatting to format XML.
	 * @throws IOException if there is any problem in writing XML.
	 * @throws DocumentException if an error occurs in parsing XML.
	 */
	public static void formatXml(File xml, String encoding, FormatMode formatMode) throws IOException, DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(xml);
		OutputFormat format;

		switch (formatMode) {
			case COMPACT:
				format = OutputFormat.createCompactFormat();
				break;
			case PRETTY:
				format = OutputFormat.createPrettyPrint();
				break;
			default:
				throw new AssertionError("Unexpected Format Mode: " + formatMode.toString());
		}

		format.setEncoding(encoding);

		// Write XML.
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xml, false), encoding), format);
		writer.write(doc);
		// Flush the underlying writer.
		writer.flush();
		writer.close();
	}

	/**
	 * <p>Format the XML with the specified encoding and pretty printing format.</p>
	 *
	 * @param xml which is going to be formatted.
	 * @param encoding Text encoding to write the XML.
	 * @throws IOException if there is any problem in writing XML.
	 * @throws DocumentException if an error occurs in parsing XML.
	 */
	public static void formatXml(File xml, String encoding) throws IOException, DocumentException {
		formatXml(xml, encoding, FormatMode.PRETTY);
	}

	/**
	 * <p>Format the XML with the specified encoding and format mode.</p>
	 *
	 * @param xmlPath The path in which the XML resides.
	 * @param encoding Text encoding to write the XML.
	 * @param formatMode The mode of formatting to format XML.
	 * @throws IOException if there is any problem in writing XML.
	 * @throws DocumentException if an error occurs in parsing XML.
	 */
	public static void formatXml(String xmlPath, String encoding, FormatMode formatMode) throws IOException, DocumentException {
		formatXml(new File(xmlPath), encoding, formatMode);
	}

	/**
	 * <p>Format the XML with the specified encoding and pretty printing format.</p>
	 *
	 * @param xmlPath The path in which the XML resides.
	 * @param encoding Text encoding to write the XML.
	 * @throws IOException if there is any problem in writing XML.
	 * @throws DocumentException if an error occurs in parsing XML.
	 */
	public static void formatXml(String xmlPath, String encoding) throws IOException, DocumentException {
		formatXml(new File(xmlPath), encoding);
	}

	private boolean checkDefaultNode(Element ele) {
		// Could not find any CLI default value XML relative to the HiveAP product name and software version specified.
		if (defValueDoc == null) {
			return false;
		}

		String attrName = ele.attributeValue(AH_CLI_SCHEMA_XML_ATTR_NAME);
		String attrValue = ele.attributeValue(AH_CLI_SCHEMA_XML_ATTR_VALUE);
		String attrOper = ele.attributeValue(AH_CLI_SCHEMA_XML_ATTR_OPERATION);

		// Do not handle such element either with 'name' or 'value' attribute.
		if (attrName != null || attrValue != null) {
			return false;
		}

		String absoluteXPath = CompleteRunXml.getHmElementPath(ele, this.defValueDoc);
		absoluteXPath += "[@" + AH_CLI_SCHEMA_XML_ATTR_OPERATION + "='" + attrOper + "']";
		Node defNode = defValueDoc.selectSingleNode(absoluteXPath);

		return defNode != null;
	}

	/**
	 * <p>Change the value of 'operation' attribute to "no" for the specified element and remove its child elements.</p>
	 *
	 * @param e The 'operation' attribute within the element needs to be changed.
	 * @return <tt>true</tt> if the value of the 'operation' attribute is changed,
	 *         <tt>false</tt> there is not such an attribute in the element.
	 */
	private boolean changeOperation(Element e) {
		String attrOper = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_OPERATION);

		if (attrOper == null) {
			return false;
		}
		
		boolean clearChilds = true;

		if (attrOper.equalsIgnoreCase(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES)) {
			String oldAttrName = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_NAME);
			String oldAttrValue = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_VALUE);

			if (oldAttrName == null && oldAttrValue == null) {
				boolean isDefNode = checkDefaultNode(e);

				if (isDefNode) {
					// The CLI reflects to a default value node shouldn't be generated.
					// Change operation from "yes" to "noWithHidden".
					e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_HIDDEN);
				} else {
					// Change operation from "yes" to "no".
					e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO);
				}
			}  else {
				// Change operation from "yes" to "no".
				e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO);
			}
		} else if (attrOper.equalsIgnoreCase(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO)) {
			String oldAttrName = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_NAME);
			String oldAttrValue = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_VALUE);

			if (oldAttrName == null && oldAttrValue == null) {
				boolean isDefNode = checkDefaultNode(e);

				if (isDefNode) {
					// The CLI reflects to a default value node shouldn't be generated.
					// Change operation from "no" to "noWithHidden".
					e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_HIDDEN);
				} else {
					// Change operation from "no" to "yes".
					e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES);
				}
			} else {
				// Change operation from "no" to "yes".
				e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES);
			}
		} else if (attrOper.equalsIgnoreCase(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES_WITH_VALUE)) {
			// Change operation from "yesWithValue" to "noWithValue".
			e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_VALUE);
		} else if (attrOper.equalsIgnoreCase(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_VALUE)) {
			// Change operation from "noWithValue" to "yesWithValue".
			e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES_WITH_VALUE);
		} else if (attrOper.equalsIgnoreCase(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_YES_WITH_SHOW)) {
			// Change operation from "yesWithShow" to "noWithHidden".
			e.addAttribute(AH_CLI_SCHEMA_XML_ATTR_OPERATION, AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_HIDDEN);
		} 
//		else if(attrOper.equalsIgnoreCase(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_CHILD)){
//			clearChilds = false;
//			changeOperationTreeWalkChild(e);
//			System.out.println(e.asXML());
//		}

		// Remove its child elements.
		if(clearChilds){
			e.elements().clear();
		}

		return true;
	}
	
//	private void changeOperationTreeWalkChild(Element e){
//		Iterator<?> childEles = e.elements().iterator();
//		while(childEles.hasNext()){
//			Element childEle = (Element)childEles.next();
//			boolean res = changeOperation(childEle);
//			//res == false not exists operation attribute.
//			if(!res && !childEle.elements().isEmpty()){
//				changeOperationTreeWalkChild(childEle);
//			}
//		}
//	}

	/**
	 * <p>Compare child elements reside in old and new parent config elements respectively.</p>
	 *
	 * @param oldElem An old parent element belongs to the old XML-formatted config.
	 * @param newElem A new parent element belongs to the new XML-formatted config.
	 * @param newElemCopy A deep copy of the new parent element.
	 * @param oldElemIter Iterator instance comes from the old parent element.
	 * @param newElemIter Iterator instance comes from the new child element list.
	 * @param newElems A child element list which are as sub elements persist in the _new.xml
	 * @return <tt>true</tt> indicates child elements comparison are finished,
	 *         <tt>false</tt> indicates the 'operation' attribute of the old element requires a 'no' command.
	 * @throws AhConfigComparedException if there is any problem in comparing.
	 */
	private boolean compareElements(
		Element oldElem,
		Element newElem,
		Element newElemCopy,
		Iterator<?> oldElemIter,
		Iterator<Element> newElemIter,
		List<Element> newElems) throws AhConfigComparedException {
//		String oldConfigUpdateTime = oldElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_UPDATE_TIME);
//		String newConfigUpdateTime = newElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_UPDATE_TIME);
//
//		if (oldConfigUpdateTime != null &&
//			newConfigUpdateTime != null &&
//			oldConfigUpdateTime.equals(newConfigUpdateTime)) {
//			// Do not compare the current two elements and their child elements any more if they hold the same value for the 'updateTime' attribute. It indicates that the current two elements are the same because the value of 'update time' column against each element is the same in the database.
//			oldElemIter.remove();
//			newElemIter.remove();
//		} else {
			if (oldElem.elements().isEmpty()) {
				oldElemIter.remove();

				if (newElem.elements().isEmpty()) {
					newElemIter.remove();
				}
			} else {
				// Continue comparing between their child elements deeply.
				boolean ret = compareElements(true, oldElem.elements(), newElem.elements());

				if (ret) {
					if (oldElem.elements().isEmpty()) {
						oldElemIter.remove();
					}

					if (newElem.elements().isEmpty()) {
						newElemIter.remove();
					}
				} else {
					// Require "no" the old element.
					ret = changeOperation(oldElem);

					if (!ret) {
						return false;
					}

					// Retrieve the original new element compared with the old element whose operation has been set "no" or "noWithValue".
					int index = newElems.indexOf(newElem);
					newElems.set(index, newElemCopy);
				}
			}
//		}

		return true;
	}

	/**
	 * <p>Compare child elements reside in old and new parent config elements respectively.</p>
	 *
	 * @param oldElem An old parent element belongs to the old XML-formatted config.
	 * @param newElem A new parent element belongs to the new XML-formatted config.
	 * @param newElemCopy A deep copy of the new parent element.
	 * @param oldElemIter Iterator instance comes from the old parent element.
	 * @param newElems A child element list which are as sub elements persist in the _new.xml
	 * @return <tt>true</tt> indicates child elements comparison is accomplished,
	 *         <tt>false</tt> indicates the 'operation' attribute of the old element requires to a 'no' command.
	 * @throws AhConfigComparedException if there is any problem in comparing.
	 */
	private boolean compareElements(
		Element oldElem,
		Element newElem,
		Element newElemCopy,
		Iterator<?> oldElemIter,
		List<Element> newElems) throws AhConfigComparedException {
		if (oldElem.elements().isEmpty()) {
			oldElemIter.remove();
		} else {
			boolean ret = compareElements(true, oldElem.elements(), newElem.elements());

			if (ret) {
				if (oldElem.elements().isEmpty()) {
					oldElemIter.remove();
				}

				if (newElem.elements().isEmpty()) {
					List<?> childElems = newElemCopy.elements();

					if (!childElems.isEmpty()) {
						Element firstChildElem = (Element) childElems.get(0);

						if (!firstChildElem.getName().equals(AH_CLI_SCHEMA_XML_ELEM_CR)) {
//							for (Iterator<?> childElems = list.iterator(); iter.hasNext(); ) {
//								if (!element.equals(iter.next())) {
//									iter.remove();
//								}
//							}

							// If the current new config element can not stand for an entire CLI syntax item after comparison with another old config element. Using the original new config element instead of the current one.
							int index = newElems.indexOf(newElem);
							newElems.set(index, newElemCopy);
						}
					}
				}
			} else {
				ret = changeOperation(oldElem);

				if (!ret) {
					return false;
				}

				// Use the original new config element instead of the current one which is being compared with another old config element.
				int index = newElems.indexOf(newElem);
				newElems.set(index, newElemCopy);
			}
		}

		return true;
	}

	/**
	 * <p>Compare elements between the old and new XML-formatted config.</p>
	 *
	 * @param isInvokedBySelf true indicates it is invoked by another method which has the same name as the invoked one, false otherwise.
	 * @param oldElems A child element list which are as sub elements persist in the _old.xml
	 * @param newElems A child element list which are as sub elements persist in the _new.xml
	 * @return <tt>true</tt> indicates child elements comparison is accomplished,
	 *         <tt>false</tt> indicates the 'operation' attribute of the old element requires to a 'no' command.
	 * @throws AhConfigComparedException if there is any problem in comparing.
	 */
	private boolean compareElements(boolean isInvokedBySelf, List<?> oldElems, List<Element> newElems) throws AhConfigComparedException {
		boolean ret;

		for (Iterator<?> oldElemIter = oldElems.iterator(); oldElemIter.hasNext(); ) {
			Element oldElem = (Element) oldElemIter.next();
			String oldElemName = oldElem.getName();
			String oldAttrName = oldElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_NAME);
			String oldAttrValue = oldElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_VALUE);

			if (newElems.isEmpty()) {
				ret = changeOperation(oldElem);

				if (!ret) {
					if (isInvokedBySelf) {
						return false;
					} else {
						throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
					}
				} else {
					continue;
				}
			}

			for (Iterator<Element> newElemIter = newElems.iterator(); newElemIter.hasNext(); ) {
				Element newElem = newElemIter.next();
				String newElemName = newElem.getName();
				String newAttrName = newElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_NAME);
				String newAttrValue = newElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_VALUE);

				if (oldElemName.equals(newElemName)) {
					// Clone a same instance with the new config element to use for roll back.
					Element newElemCopy = newElem.createCopy();

					if (oldAttrName != null) {// 'name' attribute is exist.
						if (newAttrName == null) {
							throw new AhConfigComparedException("The 'name' attribute["+oldAttrName+"] is included in the element["+oldElemName+"] for the old config but excluded for the new config.");
						}

						if (oldAttrName.equals(newAttrName)) {
							ret = compareElements(oldElem, newElem, newElemCopy, oldElemIter, newElemIter, newElems);

							if (!ret) {
								if (isInvokedBySelf) {
									return false;
								} else {
									throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
								}
							}
						} else {
							if (newElemIter.hasNext()) {
								// Probably there are several child elements with the same element name but with different values for the 'name' attribute. (E.g. multi-schedulers are band to a SSID profile), so need comparing continuously.
								continue;
							}

							// If there is no such one element with the same value for the 'name' attribute, then remove the old element with 'no' command.
							ret = changeOperation(oldElem);

							if (!ret) {
								if (isInvokedBySelf) {
									return false;
								} else {
									throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
								}
							}
						}
					} else if (oldAttrValue != null) {// 'value' attribute is exist.
						if (newAttrValue == null) {
							throw new AhConfigComparedException("The 'value' attribute["+oldAttrName+"] is included in the element["+oldElemName+"] for the old config but excluded for the new config.");
						}

						if (oldAttrValue.equals(newAttrValue)) {
							ret = compareElements(oldElem, newElem, newElemCopy, oldElemIter, newElemIter, newElems);

							if (!ret) {
								if (isInvokedBySelf) {
									return false;
								} else {
									throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
								}
							}
						} else {
							// Replace 'value' attribute.

							// None of child elements for the "old" element should be involved into the further comparison for the "value" attribute replacement.
							oldElem.elements().clear();

							ret = compareElements(oldElem, newElem, newElemCopy, oldElemIter, newElems);

							if (!ret) {
								if (isInvokedBySelf) {
									return false;
								} else {
									throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
								}
							}
						}
					} else {// 'name' and 'value' attributes are both absent.
						String oldAttrOper = oldElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_OPERATION);
						String newAttrOper = newElem.attributeValue(AH_CLI_SCHEMA_XML_ATTR_OPERATION);

						if (oldAttrOper != null) {
							if (newAttrOper == null) {
								throw new AhConfigComparedException("The 'operation' attribute is included in the element["+oldElemName+"] for the old config but excluded for the new config.");
							}

							if (oldAttrOper.equals(newAttrOper)) {
								ret = compareElements(oldElem, newElem, newElemCopy, oldElemIter, newElemIter, newElems);

								if (!ret) {
									if (isInvokedBySelf) {
										return false;
									} else {
										throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
									}
								}
							} else {
								// Replace the 'operation' attribute.
								ret = compareElements(oldElem, newElem, newElemCopy, oldElemIter, newElems);

								if (!ret) {
									if (isInvokedBySelf) {
										return false;
									} else {
										throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
									}
								}
							}
						} else {
							if (newAttrOper != null) {
								throw new AhConfigComparedException("The 'operation' attribute is included in the element["+oldElemName+"] for the new config but excluded for the old config.");
							}

							ret = compareElements(oldElem, newElem, newElemCopy, oldElemIter, newElemIter, newElems);

							if (!ret) {
								if (isInvokedBySelf) {
									return false;
								} else {
									throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
								}
							}
						}
					}

					break;
				} else {
					if (!newElemIter.hasNext()) {
						ret = changeOperation(oldElem);

						if (!ret) {
							if (isInvokedBySelf) {
								return false;
							} else {
								throw new AhConfigComparedException("Could not change the 'operation' attribute since it is excluded in the element["+oldElemName+"].");
							}
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * <p>Compare old XML-formatted config to new XML-formatted config. Obtain the differences from a third XML-formatted config generated from the comparison.</p>
	 *
	 * @throws DocumentException if there is any problem in writing XML.
	 * @throws AhConfigComparedException if there is any problem in comparing.
	 * @return true indicates the comparison is successful, false otherwise.
	 */
	public boolean compare() throws DocumentException, AhConfigComparedException {
		SAXReader reader = new SAXReader();
		Document oldConfigDoc = reader.read(oldConfig);
		Document newConfigDoc = reader.read(newConfig);
		oldConfigElems = oldConfigDoc.getRootElement().elements();
		newConfigElems = newConfigDoc.getRootElement().elements();

		// Remove the same element from both the old and new XML-formatted config.
		long startTime = System.currentTimeMillis();
		boolean ret = compareElements(false, oldConfigElems, newConfigElems);
		long endTime = System.currentTimeMillis();
		log.info("compare", "It took " + (endTime - startTime) + " ms to compare a pair of " + hiveAp.getSoftVer() + " version-based configs.");

		return ret;
	}

	/**
	 * <p>Generate a difference XML with the given file path and format mode.</p>
	 *
	 * @param filePath The path in which the difference XML is saved.
	 * @param formatMode The mode of formatting used to format XML.
	 * @throws IOException if there is any problem in writing XML.
	 */
	public void generateDiffXml(String filePath, FormatMode formatMode) throws IOException {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement(AH_DIFF_XML_ROOT_ELEM);
		Document doc = factory.createDocument(root);

		// After comparison, all the elements left in the old profile element list with the 'operation' attribute has been set to 'no' or 'noWithValue'.
		for (Object obj : oldConfigElems) {
			root.add(((Element) obj).createCopy());
		}

		// At the mean time, the other elements left in the new profile element list need to be added into the different XML as well.
		for (Object obj : newConfigElems) {
			root.add(((Element) obj).createCopy());
		}

		generateXml(doc, filePath, encoding, formatMode);
	}

	/**
	 * <p>Generate the difference XML with the given file path and 'PRETTY' mode to format.</p>
	 *
	 * @param filePath The path in which the difference XML is saved.
	 * @throws IOException if there is any problem in writing XML.
	 */
	public void generateDiffXml(String filePath) throws IOException {
		generateDiffXml(filePath, FormatMode.PRETTY);
	}

	private String handleSpecialCharacters(String doubleQuoteMarksProhibited, String value) {
		String candidate = value;

		if (doubleQuoteMarksProhibited == null || doubleQuoteMarksProhibited.trim().equalsIgnoreCase("no")) {
			if (candidate != null && !candidate.isEmpty()) {
				boolean doubleQuoteRequired = false;

				// Handle special characters.
				for (String specialChar : specialChars) {
					if (candidate.contains(specialChar)) {
						doubleQuoteRequired = true;
						break;
					}
				}

				// Handle escaping characters.
				for (String escapingChar : escapingChars) {
					if (candidate.contains(escapingChar)) {
						candidate = candidate.replace(escapingChar, "\\" + escapingChar);

						if (!doubleQuoteRequired) {
							doubleQuoteRequired = true;
						}
					}
				}

				if (doubleQuoteRequired) {
					candidate = "\"" + candidate + "\"";
				}
			}
		}

		return candidate;
	}

	/**
	 * <p>Generate different CLIs through walking the difference XML.</p>
	 *
	 * @param e An element of the difference XML.
	 * @param cliBuf A StringBuilder instance used to save and output the generated CLIs through <code>toString</code>.
	 * @return A CLI list comes from the element and its child elements.
	 * @throws AhConfigConvertedException if any problem occurs while converting XML-formatted config into CLIs.
	 */
	private List<String> treeWalk(Element e, StringBuilder cliBuf) throws AhConfigConvertedException {
		String elemName = e.getName();
		String attrName = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_NAME);
		String attrValue = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_VALUE);
		String attrOper = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_OPERATION);
		String attrQuoteProhibit = e.attributeValue(AH_CLI_SCHEMA_XML_ATTR_QUOTE_PROHIBITED);
		String value = attrName != null ? attrName : attrValue;
		List<?> childElems = e.elements();
		List<String> clis = new LinkedList<String>();

		if (!elemName.equals(AH_CLI_SCHEMA_XML_ELEM_ASSISTANT) && !AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_HIDDEN.equalsIgnoreCase(attrOper)) {
			if ("".equals(value)) {
				if (childElems.isEmpty()) {
					// Output directly but excluding the current element with an empty string "value" attribute and none child elements.
					String cli = cliBuf.toString();
					clis.add(cli.trim() + AH_CLI_SUFFIX);
				}
			} else {
				if (!elemName.equals(AH_CLI_SCHEMA_XML_ELEM_CR)) {
					cliBuf.append(elemName).append(" ");
				}

				// CLI with "no " prefix have higher priority than the other CLIs. All the child elements belonging to this kind of element should not be used to compose CLIs at all.
				if (attrOper != null && (attrOper.equals(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO) || attrOper.equals(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_VALUE))) {
					String cli = cliBuf.toString();

					if (attrOper.equals(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO)) {
						cli = "no " + cli;
					} else if (attrOper.equals(AH_CLI_SCHEMA_XML_ATTR_OPERATION_VALUE_NO_WITH_VALUE)) {
						if (value == null) {
							throw new AhConfigConvertedException("Lack of 'value' attribute in the element["+elemName+"], since the value of the 'operation' attribute in this element is 'yesWithValue'.");
						}

						cli = "no " + cli + handleSpecialCharacters(attrQuoteProhibit, value);
					}

					clis.add(cli.trim() + AH_CLI_SUFFIX);

					return clis;
				} else if (childElems.isEmpty()) {
					String cli = cliBuf.toString();

					if (value != null) {
						cli += handleSpecialCharacters(attrQuoteProhibit, value);
					}

					clis.add(cli.trim() + AH_CLI_SUFFIX);
				}

				if (value != null) {
					cliBuf.append(handleSpecialCharacters(attrQuoteProhibit, value)).append(" ");
				}
			}
		}

		for (Object obj : childElems) {
			List<String> cliList = treeWalk((Element) obj, new StringBuilder(cliBuf));
			clis.addAll(cliList);
		}

		return clis;
	}

	/**
	 * <p>Generate the difference XML-formatted config after comparison between the old and new XML-formatted config.</p>
	 *
	 * @return the difference CLIs generated from the comparison.
	 * @throws AhConfigConvertedException if any problem occurs while converting XML-formatted config into CLIs.
	 */
	public String generateDiffClis() throws AhConfigConvertedException {
		// Using 'Set' object to get ride of duplicate CLIs.
		Set<String> set = new LinkedHashSet<String>();
		List<List<String>> profiles = new LinkedList<List<String>>();

		for (Object obj : oldConfigElems) {
			List<String> clis = treeWalk((Element) obj, new StringBuilder());

			// Each CLI with the 'no' prefix comes from the old XML-formatted config after comparison needs for reversing order.
			Collections.reverse(clis);
			profiles.add(clis);
		}

		// Each profile removal CLI needs for reversing order as well.
		Collections.reverse(profiles);

		// Filter the same syntax CLIs out of the two generated CLI list.
		for (List<String> profile : profiles) {
			set.addAll(profile);
		}

		for (Object obj : newConfigElems) {
			List<String> clis = treeWalk((Element) obj, new StringBuilder());
			set.addAll(clis);
		}

		StringBuilder cliBuf = new StringBuilder();

		for (String cli : set) {
			cliBuf.append(cli);
		}

		return cliBuf.toString();
	}

}