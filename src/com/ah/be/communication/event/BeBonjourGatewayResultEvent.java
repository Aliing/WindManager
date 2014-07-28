package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.network.BonjourServiceDetail;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhCompressByte;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeBonjourGatewayResultEvent extends BeCapwapClientResultEvent {
	
	private static final Tracer log = new Tracer(BeBonjourGatewayResultEvent.class
			.getSimpleName());
	
	public static final int ELEMENT_TYPE_BDD = 1;
	public static final int ELEMENT_TYPE_REALM = 2;
	public static final int ELEMENT_TYPE_SERVICE = 3;
	
	public static final int OPER_TYPE_ADD = 1;
	public static final int OPER_TYPE_REMOVE = 2;
	public static final int OPER_TYPE_UPDATE = 3;
	
	private Map<Integer, Integer> operTypeMap = null;

	private byte bddFlag = -1;
	private String realmId="";
	List<String> neighbors = null;
	private String hiveName="";

	List<BonjourServiceDetail> serviceInfos = null;
	
	public BeBonjourGatewayResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_BONJOUR_GATEWAY;
	}

	/** 
	 * parse packet message to event data
	 * 
	 * @see com.ah.be.communication.event.BeCapwapClientResultEvent#parsePacket(byte[])
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
	
			byte compressFlag = buf.get();
			buf.getInt();// int originalLen =
			
			if (compressFlag == BeCommunicationConstant.COMPRESS) {
				byte[] bytes_cli = Arrays.copyOfRange(buf.array(), 5, buf
						.array().length);
				byte[] unCompress = AhCompressByte.uncompress(bytes_cli);
				ByteBuffer newBuf = ByteBuffer.wrap(unCompress);
				parseData(newBuf);
				
			} else if (compressFlag == BeCommunicationConstant.NOTCOMPRESS) {
				parseData(buf);
			} else {
				throw new BeCommunicationDecodeException(
						"BeBonjourGatewayResultEvent.parsePacket() invalid compress flag");
			}
			
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeBonjourGatewayResultEvent.parsePacket() catch exception", e);
		}
	}

	private void parseData(ByteBuffer buf) throws BeCommunicationDecodeException{
		serviceInfos = new ArrayList<BonjourServiceDetail>();
		operTypeMap = new HashMap<Integer, Integer>();
		while (buf.hasRemaining()) {
			byte elementType = buf.get();
			byte operType = buf.get();
			int length = buf.getInt();
			int start = buf.position();
			operTypeMap.put(AhDecoder.byte2int(elementType), AhDecoder.byte2int(operType));
			if(AhDecoder.byte2int(elementType) == ELEMENT_TYPE_BDD){  
				bddFlag = buf.get();
				if ((buf.position() - start) < length) {
					byte remoteBddsLen = buf.get();
					byte[] neighbor_array = new byte[AhDecoder.byte2int(remoteBddsLen)];
					buf.get(neighbor_array);
					ByteBuffer beighborBuf = ByteBuffer.wrap(neighbor_array);
					neighbors = new ArrayList<String>();
					while(beighborBuf.hasRemaining()){
						String ipAddress = AhDecoder.int2IP(beighborBuf.getInt());
						neighbors.add(ipAddress);
					}
					if((buf.position() - start) < length){
						byte hiveNameLen = buf.get();
						hiveName=AhDecoder.bytes2String(buf,AhDecoder.byte2int(hiveNameLen));
					}
				}
				
			} else if(AhDecoder.byte2int(elementType) == ELEMENT_TYPE_REALM){
				byte realmLength = buf.get();
				if (simpleHiveAp == null) {
					throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
							+ "), Can't find corresponding data in cache.");
				}
				if(NmsUtil.compareSoftwareVersion(
						"6.0.1.0", simpleHiveAp.getSoftVer()) <= 0){
					realmId=AhDecoder.bytes2String(buf, AhDecoder.byte2int(realmLength));
				} else {
					realmId=AhDecoder.bytes2hex(buf, AhDecoder.byte2int(realmLength));
				}
				
			} else if(AhDecoder.byte2int(elementType) == ELEMENT_TYPE_SERVICE){
				int serviceNm = buf.getInt();
				//for(int i=0;i<serviceNm;i++){
				int index=0;
				while (buf.hasRemaining() && index < serviceNm) {
					index++;
					short serviceLen = buf.getShort();
					int serviceStart = buf.position();
					BonjourServiceDetail serviceDetail = new BonjourServiceDetail();
					serviceDetail.setShared(buf.get()==1);
					serviceDetail.setVlan(buf.getShort());
					serviceDetail.setIp4(AhDecoder.int2IP(buf.getInt()));
					serviceDetail.setPort(AhDecoder.short2int(buf.getShort()));
					byte typeLen = buf.get();
					serviceDetail.setType(AhDecoder.bytes2String(buf, AhDecoder.byte2int(typeLen)));
					byte nameLen = buf.get();
					byte[] b_array = new byte[AhDecoder.byte2int(nameLen)];
					buf.get(b_array);
					serviceDetail.setName(AhDecoder.bytes2String(b_array));
					byte hostLen = buf.get();
					serviceDetail.setHost(AhDecoder.bytes2String(buf,AhDecoder.byte2int(hostLen)));
					int textLen = buf.getInt();
					serviceDetail.setText(getServiceTextInfo(buf,textLen));
					serviceDetail.setMacAddress(apMac);
					if ((buf.position() - serviceStart) < serviceLen) {
						int neighborNm = buf.get();
						String action = "";
						String vlanGroupName ="";
						for(int i=0;i<neighborNm;i++){
							action +=(buf.get() == 0 ? "deny" :"permit") + BonjourServiceDetail.SEPARATOR_CHAR;
							byte vlanGroupLen = buf.get();
							String _vlanGroupName = AhDecoder.bytes2String(buf,AhDecoder.byte2int(vlanGroupLen));
							if(vlanGroupLen == 0){
								vlanGroupName += MgrUtil.getUserMessage("config.ipPolicy.any")+BonjourServiceDetail.SEPARATOR_CHAR;
							} else {
								vlanGroupName += _vlanGroupName+BonjourServiceDetail.SEPARATOR_CHAR;
							}
						}
						serviceDetail.setAction("".equals(action) ? action : action.substring(0, action.length()-BonjourServiceDetail.SEPARATOR_CHAR.length()));
						serviceDetail.setVlanGroupName("".equals(vlanGroupName) ? vlanGroupName : vlanGroupName.substring(0, vlanGroupName.length()-BonjourServiceDetail.SEPARATOR_CHAR.length()));
						
						byte shareRemoteBddsLen = buf.get();
						byte[] service_neighbor_array = new byte[AhDecoder.byte2int(shareRemoteBddsLen)];
						buf.get(service_neighbor_array);
						ByteBuffer serviceNeighborBuf = ByteBuffer.wrap(service_neighbor_array);
						String serviceNb="";
						while(serviceNeighborBuf.hasRemaining()){
							String ipAddress = AhDecoder.int2IP(serviceNeighborBuf.getInt());
							serviceNb+=ipAddress+",";
						}
						if(!"".equals(serviceNb)){
							serviceNb = serviceNb.substring(0, serviceNb.length()-1);
						}
						
						serviceDetail.setShareRomoteBdd(serviceNb);
					}
					serviceInfos.add(serviceDetail);
				}
			} else {
				throw new BeCommunicationDecodeException(
					"BeBonjourGatewayResultEvent.parsePacket() invalid compress flag");
			}
		}
	}

	private String getServiceTextInfo(ByteBuffer buf,int textLen){
		String text = "";
		try{
			byte[] bytes_text = new byte[textLen];
			buf.get(bytes_text);
			ByteBuffer textBuf = ByteBuffer.wrap(bytes_text);
			while (textBuf.hasRemaining()) {
				byte len = textBuf.get();
				byte[] b_array = new byte[AhDecoder.byte2int(len)];
				textBuf.get(b_array);
				text=text+AhDecoder.bytes2String(b_array);
				text+="\n";
			}
		} catch(Exception e){
			log.debug("getServiceTextInfo error"," mac:"+apMac);
		}
		return text;
	}
	
	public String getRealmId() {
		return realmId;
	}

	public void setRealmId(String realmId) {
		this.realmId = realmId;
	}

	public List<BonjourServiceDetail> getServiceInfos() {
		return serviceInfos;
	}

	public void setServiceInfos(List<BonjourServiceDetail> serviceInfos) {
		this.serviceInfos = serviceInfos;
	}

	public Map<Integer, Integer> getOperTypeMap() {
		return operTypeMap;
	}

	public void setOperTypeMap(Map<Integer, Integer> operTypeMap) {
		this.operTypeMap = operTypeMap;
	}

	public byte getBddFlag() {
		return bddFlag;
	}

	public void setBddFlag(byte bddFlag) {
		this.bddFlag = bddFlag;
	}

	public List<String> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<String> neighbors) {
		this.neighbors = neighbors;
	}

	public String getHiveName() {
		return hiveName;
	}

	public void setHiveName(String hiveName) {
		this.hiveName = hiveName;
	}
	

}
