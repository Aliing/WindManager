package com.ah.be.parameter.constant;

import java.io.File;
import java.io.Serializable;

public interface AhConstantConfigParsable extends Serializable {

	enum ConstantConfigType {
		hiveap_product_name, device_page_style
	}

	Object parse(File constantConfig) throws AhConstantConfigParsedException;

}