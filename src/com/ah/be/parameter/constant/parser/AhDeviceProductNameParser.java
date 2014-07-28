package com.ah.be.parameter.constant.parser;

import java.io.File;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.constant.AhConstantConfigParsable;
import com.ah.be.parameter.constant.AhConstantConfigParsedException;
import com.ah.util.Tracer;

public class AhDeviceProductNameParser implements AhConstantConfigParsable {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AhDeviceProductNameParser.class.getSimpleName());

	/** Device Product Type */
	public enum DeviceProductType {
		hiveap20, hiveap28, hiveap110, hiveap120, hiveap170, hiveap320, hiveap330, hiveap340, hiveap350, hiveap370 , hiveap390, hiveap230,hiveap380, hiveap5020,
		cvg, cvgappliance, br100, br200, br200_wp,br200_lte_vz, hiveap121, hiveap141, sr24, sr48, sr2124p, sr2148p,sr2024p
	}

	public Map<DeviceProductType, Collection<String>> parse(File configFile)
			throws AhConstantConfigParsedException {
		if (configFile == null) {
			throw new AhConstantConfigParsedException("Invalid argument: " + configFile);
		}

		if (!configFile.exists()) {
			throw new AhConstantConfigParsedException(NmsUtil.getOEMCustomer().getAccessPonitName()+" product type file["
					+ configFile.getName() + "] doesn't exist.");
		}

		SAXReader reader = new SAXReader();

		try {
			Document document = reader.read(configFile);
			Element root = document.getRootElement();
			List<?> productTypeElems = root.elements();
			Map<DeviceProductType, Collection<String>> hiveApProductNames = new EnumMap<DeviceProductType, Collection<String>>(
					DeviceProductType.class);

			for (Object obj : productTypeElems) {
				Element productTypeElem = (Element) obj;
				String productType = productTypeElem.getName();

				try {
					DeviceProductType hiveApProductType = DeviceProductType.valueOf(productType);

					log.debug("parse", "Parsing HiveAP product type: " + productType);

					Collection<String> productNames = parseHiveApProductNames(productTypeElem);
					hiveApProductNames.put(hiveApProductType, productNames);
				} catch (IllegalArgumentException iae) {
					log.warning("parse", "Unknown HiveAP product type[" + productType
							+ "], ignore parsing.", iae);
				}
			}

			return hiveApProductNames;
		} catch (DocumentException de) {
			throw new AhConstantConfigParsedException("Failed to parse "+NmsUtil.getOEMCustomer().getAccessPonitName()+" product type ["
					+ configFile.getName() + "] because of wrong file format.");
		}
	}

	private Collection<String> parseHiveApProductNames(Element productTypeElem) {
		Collection<String> productNames = new LinkedHashSet<String>();
		List<?> productNameElems = productTypeElem.elements();

		for (Object obj : productNameElems) {
			Element productNameElem = (Element) obj;
			String productName = productNameElem.attributeValue("name");

			if (productName != null) {
				log.debug("parseHiveApProductNames", "Available HiveAP product name: "
						+ productName);

				productNames.add(productName);
			}
		}

		return productNames;
	}

}