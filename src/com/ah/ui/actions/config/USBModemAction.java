package com.ah.ui.actions.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ah.be.common.file.XMLFileReadWriter;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.USBModem;
import com.ah.bo.network.USBSignalStrengthCheck;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class USBModemAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 6757538888359205036L;

	private static final Tracer log = new Tracer(USBModemAction.class.getSimpleName());
	
	private File upload;

	private String uploadContentType;

	private String uploadFileName;
	
	private InputStream inputStream;
	
	private String outputFileName;

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_USB_MODEM);
		setDataSource(USBModem.class);
		keyColumnId = COLUMN_MODEM_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_USB_MODEM;
	}

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		if ("import".equals(operation)) {
			if (null != uploadFileName) {
				if (null != upload && !"".equals(uploadFileName)) {
					// the file format is xml
					if (!uploadFileName.endsWith(".xml")) {
						addActionError(MgrUtil.getUserMessage(
							"error.formatInvalid", "XML File"));
						return prepareBoList();
					}
					// the file cannot be empty
					if (upload.length() == 0) {
						addActionError(MgrUtil.getUserMessage(
							"error.licenseFailed.file.invalid"));
						return prepareBoList();
					}
				}
			}
			if (upload == null) {
				log.error("No USB config file selected.");
			} else {
				if (!XMLFileReadWriter.isValidXMLFile(upload)) {
					addActionError(MgrUtil.getUserMessage(
						"error.formatInvalid", "XML File"));
					return prepareBoList();
				}
				List<USBModem> usbModemLst = readUSBConfigFile(XMLFileReadWriter.parser(upload));
				if (usbModemLst.size() > 0) {
					QueryUtil.bulkUpdateBos(usbModemLst);
				}
			}
			return prepareBoList();
		} else if ("export".equals(operation)) {
			Document document = generateExportDoc();
			if (document == null) {
				addActionError(MgrUtil.getUserMessage(
					"config.usb.modem.export.noRecord"));
				return prepareBoList();
			}
			inputStream = new ByteArrayInputStream(document.asXML().getBytes());
			outputFileName = "usbConfigFile.xml";
			return "export";
		} else {
			baseOperation();
			return prepareBoList();
		}
	}
	
	private List<USBModem> readUSBConfigFile(Document document) {
		List<USBModem> usbModemLst = new ArrayList<USBModem>();
		
		Element root = document.getRootElement();
		if (root != null) {
			for (Iterator<?> it = root.elementIterator(); it.hasNext(); ) {
				Element element = (Element)it.next();
				if ("modems".equals(element.getName())) {
					for (Iterator<?> it1 = element.elementIterator(); it1.hasNext(); ) {
						Element element1 = (Element)it1.next();
						if ("modem".equals(element1.getName())) {
							usbModemLst.add(readUSBModem(element1));
						}
					}
				}
			}
		}
		
		return usbModemLst;
	}
	
	private USBModem readUSBModem(Element element) {
		USBModem usbModem = new USBModem();
		usbModem.setOwner(getDomain());
		
		usbModem.setModemName(element.attributeValue("id"));
		
		USBModem usbModemToFind = QueryUtil.findBoByAttribute(USBModem.class, "modemName", usbModem.getModemName(), this);
		if (usbModemToFind != null) {
			usbModem.setId(usbModemToFind.getId());
		}
		
		Element elementTmp = element.element("display");
		if (elementTmp != null) {
			usbModem.setDisplayName(elementTmp.attributeValue("name"));
			usbModem.setDisplayType(elementTmp.attributeValue("type"));
		}
		elementTmp = element.element("usb-info");
		if (elementTmp != null) {
			usbModem.setUsbVendorId(elementTmp.attributeValue("vendor-id"));
			usbModem.setUsbProductId(elementTmp.attributeValue("product-id"));
			usbModem.setUsbModule(elementTmp.attributeValue("module"));
		}
		elementTmp = element.element("hiveos-version");
		if (elementTmp != null) {
			usbModem.setHiveOSVersionMin(elementTmp.attributeValue("min"));
		}
		//elementTmp = element.element("signal-strength-check");
		//if (elementTmp != null) {
			//usbModem.setUsbSignalStrengthCheckList(readUSBStrengthChecks(elementTmp));
		//}
		elementTmp = element.element("connect");
		if (elementTmp != null) {
			usbModem.setConnectType(elementTmp.attributeValue("type"));
			Element elementTmp1 = elementTmp.element("serial-port");
			if (elementTmp1 != null) {
				usbModem.setSerialPort(elementTmp1.attributeValue("value"));
			}
			elementTmp1 = elementTmp.element("dialstring");
			if (elementTmp1 != null) {
				usbModem.setDailupNumber(elementTmp1.attributeValue("value"));
			}
			elementTmp1 = elementTmp.element("apn");
			if (elementTmp1 != null) {
				usbModem.setApn(elementTmp1.attributeValue("value"));
			}
			//elementTmp1 = elementTmp.element("usepeerdns");
			//usbModem.setUsePeerDns(false);
			//if (elementTmp1 != null) {
			//	usbModem.setUsePeerDns("true".equals(elementTmp1.attributeValue("value")));
			//}
			elementTmp1 = elementTmp.element("user-auth");
			if (elementTmp1 != null) {
				usbModem.setAuthType(elementTmp1.attributeValue("type"));
				if ("password".equals(usbModem.getAuthType())) {
					Element elementTmp2 = elementTmp1.element("username");
					if (elementTmp2 != null) {
						usbModem.setUserId(elementTmp2.attributeValue("value"));
					}
					elementTmp2 = elementTmp1.element("password");
					if (elementTmp2 != null) {
						usbModem.setPassword(elementTmp2.attributeValue("value"));
					}
				}
			}
		}
		
		return usbModem;
	}
	
	@SuppressWarnings("unused")
	private List<USBSignalStrengthCheck> readUSBStrengthChecks(Element element) {
		List<USBSignalStrengthCheck> strengthCheckLst = new ArrayList<USBSignalStrengthCheck>();
		
		USBSignalStrengthCheck strengthCheck = new USBSignalStrengthCheck();
		
		strengthCheck.setType(element.attributeValue("type"));
		Element elementTmp = element.element("serial-port");
		if (elementTmp != null) {
			strengthCheck.setSerialPort(elementTmp.attributeValue("value"));
		}
		elementTmp = element.element("check-cmd");
		if (elementTmp != null) {
			strengthCheck.setCheckCmd(elementTmp.attributeValue("value"));
		}
		
		strengthCheckLst.add(strengthCheck);
		
		return strengthCheckLst;
	}
	
	private Document generateExportDoc() {
		List<USBModem> usbModemLst = QueryUtil.executeQuery(USBModem.class, null, null, null, this);
		if (usbModemLst.isEmpty()) {
			return null;
		}
		Document document = DocumentHelper.createDocument();
		Element modems = document.addElement("hiveos-modem-support-list").addElement("modems");
		for (USBModem usbModem : usbModemLst) {
			Element modem = modems.addElement("modem").addAttribute("id", usbModem.getModemName());
			modem.addElement("display").addAttribute("name", usbModem.getDisplayName()).addAttribute("type", usbModem.getDisplayType());
			modem.addElement("usb-info").addAttribute("vendor-id", usbModem.getUsbVendorId()).addAttribute("product-id", usbModem.getUsbProductId()).addAttribute("module", usbModem.getUsbModule());
			modem.addElement("hiveos-version").addAttribute("min", usbModem.getHiveOSVersionMin());
			//Element strengthCheck = modem.addElement("signal-strength-check");
			//if (usbModem.getUsbSignalStrengthCheckList() != null && usbModem.getUsbSignalStrengthCheckList().size()> 0) {
			//	strengthCheck.addAttribute("type", usbModem.getUsbSignalStrengthCheckList().get(0).getType());
			//	strengthCheck.addElement("serial-port").addAttribute("value", usbModem.getUsbSignalStrengthCheckList().get(0).getSerialPort());
			//	strengthCheck.addElement("check-cmd").addAttribute("value", usbModem.getUsbSignalStrengthCheckList().get(0).getCheckCmd());
			//}
			Element connect = modem.addElement("connect").addAttribute("type", usbModem.getConnectType());
			connect.addElement("serial-port").addAttribute("value", usbModem.getSerialPort());
			connect.addElement("dialstring").addAttribute("value", usbModem.getDailupNumber());
			connect.addElement("apn").addAttribute("value", usbModem.getApn());
			Element userAuth = connect.addElement("user-auth").addAttribute("type", usbModem.getAuthType());
			if ("password".equals(usbModem.getAuthType())) {
				userAuth.addElement("username").addAttribute("value", usbModem.getUserId());
				userAuth.addElement("password").addAttribute("value", usbModem.getPassword());
			}
			//modem.addElement("usepeerdns").addAttribute("value", usbModem.isUsePeerDns()?"true":"false");
		}
		
		return document;
	}

	/*-
	private boolean isNullString(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}*/
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof USBModem) {
			USBModem usbModem = (USBModem)bo;
			if (usbModem.getUsbSignalStrengthCheckList() != null
					&& !usbModem.getUsbSignalStrengthCheckList().isEmpty()) {
				usbModem.getUsbSignalStrengthCheckList().size();
			}
		}
		
		return null;
	}
	
	public static final int COLUMN_MODEM_NAME = 1;
	public static final int COLUMN_APN = 2;
	public static final int COLUMN_DAILUP_NUMBER = 3;
	public static final int COLUMN_USER_ID = 4;
	public static final int COLUMN_PASSWORD = 5;
	public static final int COLUMN_OBSCURE_PASSWORD = 6;

	@Override
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_MODEM_NAME:
			code = "config.usb.modem.name";
			break;
		case COLUMN_APN:
			code = "config.usb.modem.apn";
			break;
		case COLUMN_DAILUP_NUMBER:
			code = "config.usb.modem.dailup.number";
			break;
		case COLUMN_USER_ID:
			code = "config.usb.modem.userId";
			break;
		case COLUMN_PASSWORD:
			code = "config.usb.modem.password";
			break;
		case COLUMN_OBSCURE_PASSWORD:
			code = "config.usb.modem.obscure.password";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(6);
		
		columns.add(new HmTableColumn(COLUMN_MODEM_NAME));
		columns.add(new HmTableColumn(COLUMN_APN));
		columns.add(new HmTableColumn(COLUMN_DAILUP_NUMBER));
		columns.add(new HmTableColumn(COLUMN_USER_ID));
		columns.add(new HmTableColumn(COLUMN_PASSWORD));
		columns.add(new HmTableColumn(COLUMN_OBSCURE_PASSWORD));
		return columns;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}