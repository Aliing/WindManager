package com.ah.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class HibernateDbConfigTool {
	private static Logger	log	= Logger.getLogger("HibernateDbConfigTool");
	
	public static String		ROOT_DIR					= System.getenv("HM_ROOT");
	public static final String	hibernateCfgFile			= ROOT_DIR + File.separator + "WEB-INF" + File.separator
															+ "classes" + File.separator + "hibernate.cfg.xml";
	public static Pattern 		postgresqlPattern 			= Pattern.compile("jdbc:postgresql:((//([a-zA-Z0-9_\\-.]+|\\[[a-fA-F0-9:]+])((:(\\d+))|))/|)([^\\s?]*).*$");
	static final String JDBC_PostGreSQL = "jdbc:postgresql://";
	static private final String PORT = "5432";
	
	
	public static List<String> getHostAndPort() {
		List<String> hostAndPort = new ArrayList<String>();
		String xmlFile = readFile(hibernateCfgFile);
		int sidx = xmlFile.indexOf(JDBC_PostGreSQL);
		int einx = xmlFile.indexOf('\n', sidx);
		String text = xmlFile.substring(sidx, einx);

		if (text == null || (text = text.trim()).isEmpty())
			return null;
		String[] infos = parseJDBCUrl(text);
		if (infos == null || infos.length != 3 || infos[0] == null || infos[2] == null)
			return null;
		String host = infos[0];
		String port = infos[1] == null ? PORT : infos[1];
		String database = infos[2];
		String url = text;
		hostAndPort.add(host);
		hostAndPort.add(port);
		hostAndPort.add(database);
		hostAndPort.add(url);
		return hostAndPort;
	}
	

	private static String readFile(String fileName) {
		StringBuffer buf = new StringBuffer();
		BufferedReader breader = null;
		try {
			breader = new BufferedReader(new InputStreamReader(
					new FileInputStream((fileName)),
					Charset.forName("utf-8")));
			while (breader.ready())
				buf.append((char) breader.read());
			breader.close();
		} catch (Exception e) {
			log.error("HibernateDbConfigTool.readFile()", e);
			return null;
		}
		return buf.toString();
	}
	
	private static String[] parseJDBCUrl(String url) {
		Matcher matcher = postgresqlPattern.matcher(url);
		if (matcher.matches()) {
			return new String[] { matcher.group(3), matcher.group(6),
					matcher.group(7) };
		} else {
			return null;
		}
	}
}
