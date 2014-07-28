package com.ah.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.ah.be.common.AhDirTools;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.wlan.RadioProfile;
import com.ah.xml.countrycodes.ChannelObj;
import com.ah.xml.countrycodes.CountrycodeObj;
import com.ah.xml.countrycodes.Countrycodes;
import com.ah.xml.countrycodes.ExcludeObj;

public final class CountryCode {

	public static final int COUNTRY_CODE_US = 840;
	
	private static Map<Integer, AhCountryChannel> countryCodesMap;
	private static List<Entry<Integer, String>> m_hm_list;
	
	private static final Tracer log = new Tracer(CountryCode.class.getSimpleName());
	
	static{
		try {
			countryCodesMap = new HashMap<Integer, AhCountryChannel>();
			String cfgPath = AhDirTools.getConstantConfigDir() + "countrycode.xml";
			JAXBContext jc = JAXBContext.newInstance("com.ah.xml.countrycodes");
			Unmarshaller m = jc.createUnmarshaller();
			Countrycodes countryCodes = (Countrycodes) m.unmarshal(new File(cfgPath));
			
			Map<Integer, String> countryMap = new HashMap<>();
			for(CountrycodeObj countyCodeObj : countryCodes.getCountrycode()){
				countryCodesMap.put(countyCodeObj.getKey(), new AhCountryChannel(countyCodeObj));
				countryMap.put(countyCodeObj.getKey(), countyCodeObj.getName());
			}
			
			//Country code 392 will be used for devices with version 6.1r5 and earlier, and 4014 will be used for devices with HOS version 6.1r6 or newer. 
			if(countryCodesMap.containsKey(392)){
				countryCodesMap.put(4014, countryCodesMap.get(392));
			}
			
			//order country code.
			m_hm_list = new ArrayList<Entry<Integer, String>>(countryMap.entrySet());
			Collections.sort(m_hm_list, new Comparator<Entry<Integer, String>>(){
				@Override
				public int compare(Entry<Integer, String> o1,
						Entry<Integer, String> o2) {
					if(o1.getKey() == 0){
						return -1;
					}else if(o2.getKey() == 0){
						return 1;
					}
					
					int index_1 = o1.getValue().indexOf(")");
					int index_2 = o2.getValue().indexOf(")");
					index_1 = index_1 > 0 ? index_1 : 0;
					index_2 = index_2 > 0 ? index_2 : 0;
					
					String str_1 = o1.getValue().substring(index_1 + 1).trim();
					String str_2 = o2.getValue().substring(index_2 + 1).trim();
					return str_1.compareTo(str_2);
				}
			});
		} catch (Exception e) {
			log.error("CountryCode init", e);
		}
	}

	/**
	 * get the country code entity list.
	 *
	 * @return -
	 */
	public static List<Entry<Integer, String>> getCountryCodeList() {
		return m_hm_list;
	}

	/**
	 * get specify country or area name.
	 * 
	 * @param code -
	 * @return -
	 */
	public static String getCountryName(int code) {
		AhCountryChannel country = null;
		if (null != countryCodesMap) {
			country = countryCodesMap.get(code);
		}
		return (country == null ? "" : country.getCountryName());
	}

	/**
	 * get the channel list in 5 GHz, for a, na modes.
	 *
	 * @param countryCode -
	 * @param channelWidth -
	 * @param dfsChannelEnabled - for EU country and operation is access.
	 * @param tuborChannelEnabled - for ap20 only.
	 * @param apModel -
	 * @param isOutdoor -
	 * @return -
	 */
	public static int[] getChannelList_5GHz(int countryCode,
			short channelWidth, boolean dfsChannelEnabled,
			boolean tuborChannelEnabled, short apModel, Boolean... isOutdoor) {
		int[] channelList;
		boolean isOut = (isOutdoor.length != 0 && (isOutdoor[0] == null ? false : isOutdoor[0]));
		channelList = _5GHz_Channels(countryCode, channelWidth, apModel, dfsChannelEnabled, isOut);
		return channelList;
	}

	private static int[] _5GHz_Channels (int countryCode, short channelWidth, 
			short apModel, boolean enableDfs, boolean isOut) {
		if(countryCodesMap.containsKey(countryCode)){
			AhCountryChannel ahCountry = countryCodesMap.get(countryCode);
			switch (channelWidth) {
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20:
				return getChannels_5G(ahCountry, (short)20, AhInterface.CHANNEL_OFFSET_0, apModel, enableDfs, isOut);
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B:
				return getChannels_5G(ahCountry, (short)40, AhInterface.CHANNEL_OFFSET_1, apModel, enableDfs, isOut);
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A:
				return getChannels_5G(ahCountry, (short)40, AhInterface.CHANNEL_OFFSET_0, apModel, enableDfs, isOut);
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40:
				return getChannels_5G(ahCountry, (short)40, AhInterface.CHANNEL_OFFSET_AUTO, apModel, enableDfs, isOut);
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80:
				return getChannels_5G(ahCountry, (short)80, AhInterface.CHANNEL_OFFSET_AUTO, apModel, enableDfs, isOut);
			default:
				return getChannels_5G(ahCountry, (short)20, AhInterface.CHANNEL_OFFSET_0, apModel, enableDfs, isOut);
			}
		}else{
			return new int[] { AhInterface.CHANNEL_A_AUTO };
		}
	}

	/**
	 * get the channel list in 2.4GHz, for b/g n/g modes.
	 * 
	 * @param countryCode -
	 * @param channelWidth -
	 * @return -
	 */
	public static int[] getChannelList_2_4GHz(int countryCode,
			short channelWidth) {
		int[] channelList;
		switch (countryCode) {
		case 124:
		case 214:
		case 320:
		case 394:
		case 591:
		case 630:
		case 840:
		case 842:
		case 860:
		case 158:
		case 356:
			switch (channelWidth) {
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_1, AhInterface.CHANNEL_BG_2,
						AhInterface.CHANNEL_BG_3, AhInterface.CHANNEL_BG_4,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7 };
				break;
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9, AhInterface.CHANNEL_BG_10,
						AhInterface.CHANNEL_BG_11 };
				break;
			default:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_1, AhInterface.CHANNEL_BG_2,
						AhInterface.CHANNEL_BG_3, AhInterface.CHANNEL_BG_4,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9, AhInterface.CHANNEL_BG_10,
						AhInterface.CHANNEL_BG_11 };
			}
			break;
		case 40:
		case 56:
		case 100:
		case 196:
		case 203:
		case 208:
		case 233:
		case 246:
		case 250:
		case 255:
		case 276:
		case 300:
		case 348:
		case 372:
		case 380:
		case 428:
		case 440:
		case 442:
		case 470:
		case 480:
		case 528:
		case 616:
		case 620:
		case 642:
		case 703:
		case 705:
		case 724:
		case 752:
		case 826:
			switch (channelWidth) {
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_1, AhInterface.CHANNEL_BG_2,
						AhInterface.CHANNEL_BG_3, AhInterface.CHANNEL_BG_4,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9 };
				break;
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9, AhInterface.CHANNEL_BG_10,
						AhInterface.CHANNEL_BG_11, AhInterface.CHANNEL_BG_12,
						AhInterface.CHANNEL_BG_13 };
				break;
			default:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_1, AhInterface.CHANNEL_BG_2,
						AhInterface.CHANNEL_BG_3, AhInterface.CHANNEL_BG_4,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9, AhInterface.CHANNEL_BG_10,
						AhInterface.CHANNEL_BG_11, AhInterface.CHANNEL_BG_12,
						AhInterface.CHANNEL_BG_13 };
			}
			break;
		case -1:// for multiEdit hiveAP with different type country code.
			channelList = new int[] { AhInterface.CHANNEL_BG_AUTO };
			break;
		default:
			switch (channelWidth) {
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_1, AhInterface.CHANNEL_BG_2,
						AhInterface.CHANNEL_BG_3, AhInterface.CHANNEL_BG_4,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9 };
				break;
			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9, AhInterface.CHANNEL_BG_10,
						AhInterface.CHANNEL_BG_11, AhInterface.CHANNEL_BG_12,
						AhInterface.CHANNEL_BG_13 };
				break;
			default:
				channelList = new int[] { AhInterface.CHANNEL_BG_AUTO,
						AhInterface.CHANNEL_BG_1, AhInterface.CHANNEL_BG_2,
						AhInterface.CHANNEL_BG_3, AhInterface.CHANNEL_BG_4,
						AhInterface.CHANNEL_BG_5, AhInterface.CHANNEL_BG_6,
						AhInterface.CHANNEL_BG_7, AhInterface.CHANNEL_BG_8,
						AhInterface.CHANNEL_BG_9, AhInterface.CHANNEL_BG_10,
						AhInterface.CHANNEL_BG_11, AhInterface.CHANNEL_BG_12,
						AhInterface.CHANNEL_BG_13 };
			}
		}
		return channelList;
	}

	public static boolean isSameChannelList_2_4GHz(int code, int anotherCode) {
		int[] channelList1 = getChannelList_2_4GHz(code,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
		int[] channelList2 = getChannelList_2_4GHz(anotherCode,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
		return isSameChannelList(channelList1, channelList2);
	}

	public static boolean isSameChannelList_5GHz(int code, int anotherCode, short hiveApModel) {
		int[] channelList1 = getChannelList_5GHz(code,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20, false, false,hiveApModel);
		int[] channelList2 = getChannelList_5GHz(anotherCode,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20, false, false,hiveApModel);
		return isSameChannelList(channelList1, channelList2);
	}

	private static boolean isSameChannelList(int[] channelList1,
			int[] channelList2) {
		if (null == channelList1 || null == channelList2) {
			return false;
		}
		if (channelList1.length != channelList2.length) {
			return false;
		}
		for (int i = 0; i < channelList1.length; i++) {
			if (channelList1[i] != channelList2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * Turbo mode is not allowed in the 5ghz spectrum for EU country
	 *
	 * @param code -
	 * @return -
	 */
	public static boolean isAllowTurboMode(int code) {
		switch (code) {
		case 40:
		case 56:
		case 100:
		case 196:
		case 203:
		case 208:
		case 233:
		case 246:
		case 250:
		case 255:
		case 276:
		case 300:
		case 348:
		case 372:
		case 380:
		case 428:
		case 440:
		case 442:
		case 470:
		case 480:
		case 528:
		case 616:
		case 620:
		case 642:
		case 703:
		case 705:
		case 724:
		case 752:
		case 826:
			return false;
		default:
			return true;
		}
	}

	/**
	 * some country does not support wifi channels in 5 GHz, for a, n/a modes.
	 *
	 * @param countryCode -
	 * @return -
	 */
	public static boolean is5GHzChannelAvailable(int countryCode) {
		switch (countryCode) {
		case 8:
		case 12:
		case 112:
		case 188:
		case 222:
		case 340:
		case 376:
		case 398:
		case 404:
		case 414:
		case 422:
		case 504:
		case 586:
		case 634:
		case 642:
		case 643:
		case 716:
		case 764:
		case 804:
		case 807:
		case 887:
			return false;
		default:
			return true;
		}
	}
	
	public static boolean isEuropeCountry(int countryCode){
		switch(countryCode){
		case 56 :
		case 250:
		case 255:
		case 372:
		case 442:
		case 492:
		case 528:
		case 826:
		case 208:
		case 246:
		case 352:
		case 752:
		case 40 :
		case 203:
		case 276:
		case 348:
		case 438:
		case 616:
		case 703:
		case 756:
		case 784:
		case 112:
		case 233:
		case 428:
		case 440:
		case 643:
		case 804:
		case 8  :
		case 100:
		case 191:
		case 196:
		case 300:
		case 380:
		case 807:
		case 470:
		case 620:
		case 642:
		case 705:
		case 724:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isUSA(int countryCode){
		switch(countryCode){
		case 840:
		case 842:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isJapan(int countryCode){
		switch(countryCode){
		case 392 :
		case 393 :
		case 394 :
		case 395 :
		case 396 :
		case 397 :
		case 399 :
		case 4007:
		case 4008:
		case 4009:
		case 4010:
		case 4011:
		case 4012:
		case 4013:
		case 4014:
		case 4015:
		case 4016:
		case 4017:
		case 4018:
		case 4019:
		case 4020:
		case 4021:
		case 4022:
		case 4023:
		case 4024:
			return true;
		default:
			return false;
		}
	}
	
	private static int[] getChannels_5G(AhCountryChannel ahCountry, short channelWidth, short channelOffset, 
			short apModel, boolean isDfs, boolean isOutDoor){
		
		List<Integer> resultList = new ArrayList<>();
		resultList.add((int)AhInterface.CHANNEL_A_AUTO);
		
		if(ahCountry == null || ahCountry.getChannels() == null || ahCountry.getChannels().isEmpty()){
			return ArrayUtils.toPrimitive(resultList.toArray(new Integer[resultList.size()]));
		}
		
		boolean dfsEnable = isDfs && ahCountry.isPlatformSupportDfs(apModel);
		for(AhChannel ahChannel : ahCountry.getChannels()){
			if(isOutDoor && !ahChannel.isOutDoorChannel()){
				//if outdoor enable, ignore indoor channel.
				continue;
			}
			if(!dfsEnable && ahChannel.isDfsChannel()){
				//if DFS disable, ignore all DFS channel.
				continue;
			}
			
			if(!ahChannel.isChannelWidthMatch(channelWidth, channelOffset, apModel)){
				//if channel width not match, ignore this channel.
				continue;
			}

			resultList.add(ahChannel.getChannel());
		}
		
		return ArrayUtils.toPrimitive(resultList.toArray(new Integer[resultList.size()]));
	}
	
	public static boolean isSupportDfs(int code, short hiveApModel){
		AhCountryChannel ahCountry = countryCodesMap.get(Integer.valueOf(code));
		if(ahCountry == null){
			return false;
		}else{
			return ahCountry.isPlatformSupportDfs(hiveApModel);
		}
	}
	
	private static class AhCountryChannel{
		private int countryCode;
		private String countryName;
		private Pattern dfsPlatform = null;		
		private List<AhChannel> channels = new ArrayList<>();
		
		public AhCountryChannel(CountrycodeObj countryCodeObj){
			this.countryCode = countryCodeObj.getKey();
			this.countryName = countryCodeObj.getName();
			if(!StringUtils.isEmpty(countryCodeObj.getDfsProduct())){
				dfsPlatform = Pattern.compile(countryCodeObj.getDfsProduct());
			}
			
			if(countryCodeObj.getChannel() != null){
				for(ChannelObj clObj : countryCodeObj.getChannel()){
					if(clObj == null){
						continue;
					}
					channels.add(new AhChannel(clObj));
				}
			}
		}
		
		public boolean isPlatformSupportDfs(short hiveApModel) {
			if(dfsPlatform == null){
				return false;
			}
			String modelStr = String.valueOf(hiveApModel);
			Matcher match = dfsPlatform.matcher(modelStr);
			return match.matches();
		}

		public int getCountryCode() {
			return countryCode;
		}

		public String getCountryName() {
			return countryName;
		}

		public List<AhChannel> getChannels() {
			return channels;
		}
	}
	
	private static class AhChannel{
		private int channel;
		private short[][] channelWidth;
		private boolean dfsChannel;
		private boolean outDoorChannel;
		private List<ExcludeObj> excludePfs = null;
		
		public AhChannel(ChannelObj channelObj){
			this.channel = channelObj.getValue();
			this.dfsChannel = channelObj.isIsdfs() != null ? channelObj.isIsdfs() : false;
			this.outDoorChannel = channelObj.isIsoutdoor() != null ? channelObj.isIsoutdoor() : false;
			this.excludePfs = channelObj.getExclude();
			if(channelObj.getChannelwidth() == null){
				return;
			}
			
			String[] widthArgs = channelObj.getChannelwidth().split("\\|");
			channelWidth = new short[widthArgs.length][2];
			for(int i=0; i<widthArgs.length; i++){
				int idx = widthArgs[i].indexOf('-');
				if(idx > 0){
					channelWidth[i][0] = Short.valueOf(widthArgs[i].substring(0, idx).trim());
					channelWidth[i][1] = Short.valueOf(widthArgs[i].substring(idx+1).trim());
				}else{
					channelWidth[i][0] = Short.valueOf(widthArgs[i].trim());
				}
			}	
		}
		
		public boolean isChannelWidthMatch(short width, short offset, short hiveApModel) {
			if(channelWidth == null){
				return false;
			}
			ExcludeObj excludePf = null;
			for(ExcludeObj excludeObj : excludePfs){					
				if(isPfExculdeFromChannel(excludeObj, hiveApModel)){
					excludePf = excludeObj;
					break;
				}
			}
			
			if(excludePf != null){
				if(excludePf.getChannelwidth() == null){
					return false;
				}
				
				String[] widthArgs = excludePf.getChannelwidth().split("\\|");
				for(int i=0; i<widthArgs.length; i++){
					int idx = widthArgs[i].indexOf('-');
					if(idx > 0 && 
						width == Short.valueOf(widthArgs[i].substring(0, idx).trim()) && 
					    (offset == Short.valueOf(widthArgs[i].substring(idx+1).trim())|| offset == AhInterface.CHANNEL_OFFSET_AUTO)){
						return false;
					}
				}				
			}
			
			for(int i=0; i<channelWidth.length; i++){
				if(width == channelWidth[i][0] && (offset == AhInterface.CHANNEL_OFFSET_AUTO || offset == channelWidth[i][1]) ){
					return true;
				}
			}
			return false;
		}
		
		private boolean isPfExculdeFromChannel(ExcludeObj excludeObj, short hiveApModel) {
			Pattern excludePlatform = null;	
			if(!StringUtils.isEmpty(excludeObj.getPlatform())){
				excludePlatform = Pattern.compile(excludeObj.getPlatform());
			}
			if(excludePlatform == null)
				return false;
			String modelStr = String.valueOf(hiveApModel);
			Matcher match = excludePlatform.matcher(modelStr);
			return match.matches();
		}

		public int getChannel() {
			return channel;
		}
		
		public boolean isDfsChannel() {
			return dfsChannel;
		}

		public boolean isOutDoorChannel() {
			return outDoorChannel;
		}
	}
}
