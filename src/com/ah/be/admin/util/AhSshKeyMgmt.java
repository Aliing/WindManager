package com.ah.be.admin.util;

import java.util.Map;

public interface AhSshKeyMgmt {

	boolean generateKeys(String keyType);

	Map<Byte, String> getKeys();

}