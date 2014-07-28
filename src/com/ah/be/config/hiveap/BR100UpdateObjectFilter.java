package com.ah.be.config.hiveap;

public class BR100UpdateObjectFilter {
	
	public final static String DYNAMIC_ENGLISH_RES="en.lang";
	
	public final static String DYNAMIC_SIMPLIFIEDCHINESE_RES="zh-Hans.lang";
	
	public final static String DYNAMIC_GERMAN_RES="de.lang";
	
	public final static String DYNAMIC_TRADITIONALCHINESE_RES="zh-Hant.lang";
	
	public final static String DYNAMIC_ITALIAN_RES="it.lang";
	
	public final static String DYNAMIC_KOREAN_RES="ko.lang";
	
	public final static String DYNAMIC_SPANISH_RES="es.lang";
	
	public final static String DYNAMIC_DUTCH_RES="nl.lang";
	
	public final static String DYNAMIC_FRENCH_RES="fr.lang";
	
	private final static String DUTCHSUFFIX="_nl.js";
	
	private final static String  FRENCHSUFFIX="_fr.js";
	
	
//	private final static String  ENGLISHSUFFIX="_en.js";
	
	
	private final static String  GERMANSUFFIX="_de.js";
	
	
	private final static String  KOREANSUFFIX="_ko.js";
	
	
	private final static String  SPANISHSUFFIX="_es.js";
	
	
	private final static String  SIMPLIFIEDCHINESESUFFIX="_zh-Hans.js";
	
	
	private final static String  TRADITIONALCHINESESUFFIX="_zh-Hant.js";
	
	
	private final static String  ITALIANSUFFIX="_it.js";
	
	public static boolean isNeedFilter(String fileName){
		fileName=fileName.trim();
		boolean result=false;
		if(fileName.equals(DYNAMIC_ENGLISH_RES) || fileName.equals(DYNAMIC_SIMPLIFIEDCHINESE_RES)||fileName.equals(DYNAMIC_GERMAN_RES)
				||fileName.equals(DYNAMIC_TRADITIONALCHINESE_RES) || fileName.equals(DYNAMIC_ITALIAN_RES)||fileName.equals(DYNAMIC_KOREAN_RES)
				||fileName.equals(DYNAMIC_SPANISH_RES) || fileName.equals(DYNAMIC_DUTCH_RES)||fileName.equals(DYNAMIC_FRENCH_RES)
				||fileName.endsWith(DUTCHSUFFIX)||fileName.endsWith(FRENCHSUFFIX)||fileName.endsWith(GERMANSUFFIX)
				||fileName.endsWith(KOREANSUFFIX)||fileName.endsWith(SPANISHSUFFIX)||fileName.endsWith(SIMPLIFIEDCHINESESUFFIX)
				||fileName.endsWith(TRADITIONALCHINESESUFFIX)||fileName.endsWith(ITALIANSUFFIX)
				){
			result =true;
		}
		return result;
	}

}
