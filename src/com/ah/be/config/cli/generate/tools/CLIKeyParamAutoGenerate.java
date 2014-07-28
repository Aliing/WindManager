package com.ah.be.config.cli.generate.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ah.be.config.cli.generate.CLIGenerateManager;
import com.ah.be.config.cli.generate.CLIGenerateManager.CliGenParameter;
import com.ah.be.config.cli.util.CLIConfigFileUtil;

public class CLIKeyParamAutoGenerate {
	
	private static String BR;
	
	static{
		if(System.getProperties().getProperty("os.name").toLowerCase().contains("win")){
			BR = "\r\n";
		}else{
			BR = "\n";
		}
	}

	private String generateClassSource(Map<String, CliGenParameter> keyMap) {
		StringBuffer sBuff = new StringBuffer();

		sBuff.append("package com.ah.be.config.cli.generate;");
		sBuff.append(BR);
		sBuff.append(BR);
		sBuff.append("public interface CLIKeyParam {");
		sBuff.append(BR);

		Iterator<String> keyItem = keyMap.keySet().iterator();
		String keyStr, group, column;
		int groupInt;
		List<KeyGroup> allKeys = new ArrayList<KeyGroup>();
		while (keyItem.hasNext()) {
			keyStr = keyItem.next();
			groupInt = keyStr.indexOf(".");
			group = groupInt > 0 ? keyStr.substring(0, groupInt) : keyStr;
			allKeys.add(new KeyGroup(keyStr, group));
		}

		Collections.sort(allKeys);
		String keyGroup = "";
		for (KeyGroup gKey : allKeys) {
			column = transformColumn(gKey.getKey());
			if (!keyGroup.equals(gKey.getGroup())) {
				sBuff.append(BR);
				sBuff.append("/********************************************************************************************/");
				sBuff.append(BR);
			}
			keyGroup = gKey.getGroup();
			sBuff.append("\t/** " + keyMap.get(gKey.getKey()).getGenParam().getCmd() + " */");
			sBuff.append(BR);
			sBuff.append("\t");
			sBuff.append("public static final String ");
			sBuff.append(column);
			sBuff.append(" = ");
			sBuff.append("\"" + gKey.getKey() + "\";");
			sBuff.append(BR);
			sBuff.append(BR);
		}

		sBuff.append(BR);
		sBuff.append("}");
		return sBuff.toString();
	}

	private String transformColumn(String column) {
		column.trim();
		column = column.replace(".", "_").replace("-", "_");
		column = column.toUpperCase();

		return column;
	}

	private void writeClass(String content) {
		try {
			FileWriter fw = new FileWriter(
					"src/com/ah/be/config/cli/generate/CLIKeyParam.java");
			PrintWriter pw = new PrintWriter(fw);
			pw.println(content);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class KeyGroup implements Comparable<KeyGroup> {
		private String key;
		private String group;

		public KeyGroup(String key, String group) {
			this.key = key;
			this.group = group;
		}

		public String getKey() {
			return key;
		}

		public String getGroup() {
			return group;
		}

		@Override
		public int compareTo(KeyGroup o) {
			return key.compareTo(o.getKey());
		}
	}

	public static void main(String[] args) throws IOException {
		CLIConfigFileUtil.setSchemaPath("webapps"+File.separator+"schema"+File.separator);
		CLIKeyParamAutoGenerate autoGen = new CLIKeyParamAutoGenerate();
		String content = autoGen.generateClassSource(CLIGenerateManager
				.getInstance().getCliParams());
		autoGen.writeClass(content);
	}
}
