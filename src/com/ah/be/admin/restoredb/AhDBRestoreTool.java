package com.ah.be.admin.restoredb;

public class AhDBRestoreTool {
	public static void restoreTable(final String tableName) {
		AhDBTool aa = new AhDBTool();
		if(aa.generateColumnInfo(tableName)) {
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(tableName, tableName);
		}
	}
	
	public static void restoreConvertTable(String baseXmlFileName,final String tableName) {
		AhDBTool aa = new AhDBTool();
		if(aa.generateColumnInfo(tableName)) {
			AhConvertXMLToCSV xmlToCSV = new AhConvertXMLToCSV();
			xmlToCSV.setColumnInfo(aa.getColumnNames(), aa.getColumnTypes(), aa
					.getColumnDefaultValues());
			xmlToCSV.importAllXMLToDB(baseXmlFileName, tableName);
		}
	}
}
