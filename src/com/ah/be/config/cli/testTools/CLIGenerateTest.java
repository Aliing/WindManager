package com.ah.be.config.cli.testTools;

import com.ah.be.config.cli.generate.CLIGenerateManager;
import com.ah.be.config.cli.generate.CLIKeyParam;

public class CLIGenerateTest {

	public CLIGenerateTest() {
	}

	public static void main(String args[]) throws Exception {
		Object[] arrays = new Object[]{"test", 2};
		String resCli = CLIGenerateManager.getInstance().getCLI(CLIKeyParam.RADIO_PROFILE_CHANNEL_WIDTH_OLD, arrays);
	
	}
}