package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.ah.be.app.AhAppContainer;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.mo.SpectralAnalysisData;
import com.ah.be.communication.mo.SpectralAnalysisDataSample;
import com.ah.be.communication.mo.SpectralAnalysisInterference;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeSpectralAnalysisEvent extends BeCapwapClientResultEvent {
	
	public static final Tracer log = new Tracer(BeSpectralAnalysisEvent.class.getSimpleName());
	
	private List<SpectralAnalysisData> saDatas;
	
	private SpectralAnalysisDataSample sample;
	
	public static final byte	CHANNEL_WIDTH_20	= 0;
	public static final byte	CHANNEL_WIDTH_40	= 1;
	
	public static final short	SAMPLE_COUNT_56		= 56;
	public static final short	SAMPLE_COUNT_128	= 128;
	
	private static final int	MAX_QUEUE_SIZE      = 200;
	
	private byte chnNo;
	
	public BeSpectralAnalysisEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SPECTRALANALYSIS;
	}
	
	private boolean filteredBy3rd(String apMac, byte[] data) {
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().filteredBy3rd(apMac, data);
	}
	
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			
			if (filteredBy3rd(apMac, resultData)) return;
			
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			saDatas = new ArrayList<SpectralAnalysisData>();
			sample = new SpectralAnalysisDataSample();
			
			sample.setTimeStamp(System.currentTimeMillis());
			
			this.setChnNo(buf.get());
			
			while (buf.hasRemaining()) {
				SpectralAnalysisData saData = new SpectralAnalysisData();
				
				saData.setTimeStamp(sample.getTimeStamp());
				
				saData.setTag(buf.get());

				saData.setLength(AhDecoder.short2int(buf.getShort()));

				saData.setChnInfoLen(AhDecoder.short2int(buf.getShort()));

				saData.setChnFreq(buf.getShort());
				
				saData.setChnWidth(buf.get());
				
				if (saData.getChnWidth() == CHANNEL_WIDTH_40) {
					short freq = doWithHT40(saData.getChnFreq(), false);
					if (freq > 0) saData.setChnFreq(freq);
				}
				
				short pwrLen = buf.getShort();
				saData.setPwrRspLen(pwrLen);
				if (pwrLen > 0) {
					short cnt = 0;
					short pwrRsp[] = new short[pwrLen];
					while (buf.hasRemaining()) {
						pwrRsp[cnt] = AhDecoder.byte2short(buf.get());
						cnt++;

						if (cnt == pwrLen) {
							saData.setPwrRsp(pwrRsp);
							break;
						}
					}
					if (cnt != pwrLen)
						continue;
				}
				
				short dutyCycleLen = buf.getShort();
				saData.setDutyCycleLen(dutyCycleLen);
				if (dutyCycleLen > 0) {
					short cnt = 0;
					short dutyCycle[] = new short[dutyCycleLen];
					while (buf.hasRemaining()) {
						dutyCycle[cnt] = AhDecoder.byte2short(buf.get());
						cnt++;

						if (cnt == dutyCycleLen) {
							saData.setDutyCycle(dutyCycle);
							break;
						}
					}
					if (cnt != dutyCycleLen)
						continue;
				}
				
				byte interfCount = buf.get();
				saData.setInterfCount(interfCount);

				if (interfCount > 0) {
					short cnt = 0;
					byte interfType[] = new byte[interfCount];
					short interfMin[] = new short[interfCount];
					short interfMax[] = new short[interfCount];
					while (buf.hasRemaining()) {
						interfType[cnt] = buf.get();
						interfMin[cnt] = buf.getShort();
						interfMax[cnt] = buf.getShort();
						
						cnt++;
						if (cnt == interfCount) {
							saData.setInterfType(interfType);
							saData.setInterfMin(interfMin);
							saData.setInterfMax(interfMax);
							break;
						}
					}
					if (cnt != interfCount)
						continue;
				}
				
				saDatas.add(saData);
			}
			
			// calculate datas
			//calcucateFreqDatas();
			
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeSpectralAnalysisEvent.parsePacket() catch exception", e);
		}
	}

	private short doWithHT40(short orgFreq, boolean convert) {
		short freq = orgFreq;
		if (convert) {
			freq -= 10;
		}
		switch (freq) {
		case 5180:
		case 5220:
		case 5260:
		case 5300:
		case 5500:
		case 5540:
		case 5660:
		case 5745:
		case 5785:
		case 5580:
		case 5620:
			if (!convert) {
				freq += 10;
			}
			return freq;
		case 5200:
		case 5240:
		case 5280:
		case 5320:
		case 5520:
		case 5560:
		case 5680:
		case 5765:
		case 5805:
		case 5600:
		case 5640:
			if (!convert) {
				freq += 10;
			}
			return freq;
		default:
			return -1;
		}
	}

	public void calcucateFreqData(List<SpectralAnalysisData> saDatas, SpectralAnalysisDataSample sample) {
		//traceLog("sa data recieved", saDatas);
		Collections.sort(saDatas);
		
		SpectralAnalysisData saData;
		int size = saDatas.size();
		
		for (int j = 0; j < size; j++) {
			saData = saDatas.get(j);
			
			byte bw = saData.getChnWidth();
			int calcCnt = saData.getPwrRspLen();
			int count = 0;
			int calcSampleCount = 0;// must be odd number
			
			if (calcCnt != saData.getDutyCycleLen())
				return;
			
			if (calcCnt != SAMPLE_COUNT_56 && calcCnt != SAMPLE_COUNT_128)
				return;
			
			int loffset = 0;
			if (bw == CHANNEL_WIDTH_20) {
				count = (calcCnt == SAMPLE_COUNT_56 ? 3 : 6);
				calcSampleCount = (calcCnt == SAMPLE_COUNT_56 ? 20 : 20);
				loffset = (calcCnt == SAMPLE_COUNT_128 ? 4 : 0);
			} else if (bw == CHANNEL_WIDTH_40) {
				count = (calcCnt == SAMPLE_COUNT_56 ? 1 : 3);
				calcSampleCount = 40;
				loffset = (calcCnt == SAMPLE_COUNT_56 ? 8 : 4);
			} else {return;}
			short freq = (short) (saData.getChnFreq() - (calcSampleCount / 2));
			
			for (int i = 0; i < calcSampleCount; i++) {
				int calculateSampleCount=count;
				int offSetSample=loffset + i * count;
				if (calcCnt== SAMPLE_COUNT_56 && (i==0|| i==1 || i==calcSampleCount-2 || i==calcSampleCount-1)) {
					calculateSampleCount = calculateSampleCount -1;
					if (i==1) {
						offSetSample=offSetSample-1;
					} else if (i>1 && i<=calcSampleCount-2){
						offSetSample=offSetSample-2;
					} else if (i==calcSampleCount-1) {
						offSetSample=offSetSample-3;
					}
				}
				Short samplePwr = calcucateDatas(saData.getPwrRsp(), (short) (offSetSample), (short)calculateSampleCount);
				Short sampleDuty = calcucateDatas(saData.getDutyCycle(), (short) (offSetSample), (short)calculateSampleCount);
				
				if (samplePwr == null || sampleDuty == null) {
					freq++;
					continue;
				}
				
				if (freq >= SpectralAnalysisDataSample.START_24G_FREQ
						&& freq <= SpectralAnalysisDataSample.END_24G_FREQ) {
					sample.setPwrRsp24g(freq, samplePwr);
					sample.setDutyCycle24g(freq, sampleDuty);
				} else if (freq >= SpectralAnalysisDataSample.START_5G_SECTION1_FREQ
						&& freq <= SpectralAnalysisDataSample.END_5G_SECTION1_FREQ) {
					sample.setPwrRsp5g1(freq, samplePwr);
					sample.setDutyCycle5g1(freq, sampleDuty);
				} else if (freq >= SpectralAnalysisDataSample.START_5G_SECTION2_FREQ
						&& freq <= SpectralAnalysisDataSample.END_5G_SECTION2_FREQ) {
					sample.setPwrRsp5g2(freq, samplePwr);
					sample.setDutyCycle5g2(freq, sampleDuty);
				} else if (freq >= SpectralAnalysisDataSample.START_5G_SECTION3_FREQ
						&& freq <= SpectralAnalysisDataSample.END_5G_SECTION3_FREQ) {
					sample.setPwrRsp5g3(freq, samplePwr);
					sample.setDutyCycle5g3(freq, sampleDuty);
				}
				
				freq++;
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void traceLog(String header, List<SpectralAnalysisData> saDatas) {
		String pwr;
		
		log.info("============" + header + "============");
		for (SpectralAnalysisData saData : saDatas) {
			pwr = "Freq-power: " + saData.getChnFreq();
			int idx = 0;
			for (short pwrVal : saData.getPwrRsp()) {
				pwr += ("[" + idx + "]" + pwrVal + " ");
				idx++;
			}
			log.info(pwr);
		}
		
		for (SpectralAnalysisData saData : saDatas) {
			pwr = "Freq-duty: " + saData.getChnFreq();
			int idx = 0;
			for (short pwrVal : saData.getDutyCycle()) {
				pwr += ("[" + idx + "]" + pwrVal + " ");
				idx++;
			}
			log.info(pwr);
		}
	}

	public Short calcucateDatas(short[] calcDatas, short offset, short count) {
		if (calcDatas == null || calcDatas.length < count || count <= 0)
			return null;

		short sample = 0;
		int len = calcDatas.length;
		try {
			for (short j = offset; j < offset + count; j++) {
				if (j > (len - 1))break;
				sample += calcDatas[j];
			}
			sample = (short) (sample / count);
		} catch (Exception e) {
			return null;
		}

		return sample;
	}
	
	public List<SpectralAnalysisData> getSaDatas() {
		return saDatas;
	}

	public void setSaDatas(List<SpectralAnalysisData> saDatas) {
		this.saDatas = saDatas;
	}
	
	public byte getChnNo() {
		return chnNo;
	}

	public void setChnNo(byte chnNo) {
		this.chnNo = chnNo;
	}

	public SpectralAnalysisDataSample getSample() {
		return sample;
	}

	public void setSample(SpectralAnalysisDataSample sample) {
		this.sample = sample;
	}

	public void fillInterference(Queue<SpectralAnalysisInterference> interfMaps) {
		if (interfMaps == null) return;
		
		if (saDatas != null && saDatas.size() > 0) {
			for (SpectralAnalysisData saData : saDatas) {
				String apName = (getSimpleHiveAp() == null ? "" : getSimpleHiveAp().getHostname());
				byte interfType[] = saData.getInterfType();
				short interfMin[] = saData.getInterfMin();
				short interfMax[] = saData.getInterfMax();
				for (byte i = 0; i < saData.getInterfCount(); i++) {
					SpectralAnalysisInterference interf = new SpectralAnalysisInterference();
					interf.setApName(apName);
					interf.setTime(sample.getTimeStamp());
					interf.setBandwidth(saData.getChnWidth());
					
					if (saData.getChnWidth() == CHANNEL_WIDTH_40) {
						short freq = doWithHT40(saData.getChnFreq(), true);
						if (freq > 0) saData.setChnFreq(freq);
					}
					interf.setCenterFreq(saData.getChnFreq());
					interf.setDeviceType(interfType[i]);
					interf.setSignalMin(interfMin[i]);
					interf.setSignalMax(interfMax[i]);
					
					Iterator<SpectralAnalysisInterference> iterator = interfMaps.iterator();
					boolean updated = false;
					while (iterator.hasNext()) {
						SpectralAnalysisInterference next = iterator.next();
						if (next.equals(interf)) {
							next.setTime(interf.getTime());
							next.setSignalMin(interf.getSignalMin());
							next.setSignalMax(interf.getSignalMax());
							updated = true;
						}
					}
					if (!updated)interfMaps.add(interf);
					
					if (interfMaps.size() > MAX_QUEUE_SIZE)interfMaps.poll();
				}
			}
		}
	}
	
	public void fillMaxHold(SpectralAnalysisDataSample maxHold) {
		if (maxHold == null || this.sample == null) return;
		
		boolean hasMax = false;
		
		if (maxHold(maxHold.getPwrRsp24g(), this.sample.getPwrRsp24g())) hasMax = true;
		if (maxHold(maxHold.getPwrRsp5g1(), this.sample.getPwrRsp5g1())) hasMax = true;
		if (maxHold(maxHold.getPwrRsp5g2(), this.sample.getPwrRsp5g2())) hasMax = true;
		if (maxHold(maxHold.getPwrRsp5g3(), this.sample.getPwrRsp5g3())) hasMax = true;
		
		if (hasMax) maxHold.setTimeStamp(this.sample.getTimeStamp());
	}
	
	private boolean maxHold(short arr[], short cmp[]) {
		if (cmp == null || cmp.length == 0) return false;
		
		if (arr == null){arr = new short[cmp.length];SpectralAnalysisDataSample.setDefault(arr, (short)-1);}; 
		if (arr.length != cmp.length) return false;
		
		boolean hasReplaced = false;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == -1 && cmp[i] != -1) {arr[i] = cmp[i]; hasReplaced = true;continue;}// first
			
			if (arr[i] != -1 && cmp[i] != -1 && cmp[i] < arr[i]) {arr[i] = cmp[i]; hasReplaced = true;}
		}
		
		return hasReplaced;
	}
	private boolean maxHoldDuty(short arr[], short cmp[]) {
		if (cmp == null || cmp.length == 0) return false;
		
		if (arr == null){arr = new short[cmp.length];SpectralAnalysisDataSample.setDefault(arr, (short)-1);}; 
		if (arr.length != cmp.length) return false;
		
		boolean hasReplaced = false;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == -1 && cmp[i] != -1) {arr[i] = cmp[i]; hasReplaced = true;continue;}// first
			
			if (arr[i] != -1 && cmp[i] != -1 && cmp[i] > arr[i]) {arr[i] = cmp[i]; hasReplaced = true;}
		}
		
		return hasReplaced;
	}
	
	public void fillMaxHold(Map<Short, SpectralAnalysisData> maxHold) {
		if (saDatas == null || saDatas.size() == 0) return;
		
		boolean hasMax = false;
		for (SpectralAnalysisData saData : saDatas) {
			SpectralAnalysisData hold = maxHold.get(saData.getChnFreq());
			
			if (hold == null) {
				hold = new SpectralAnalysisData(); 
				hold.setChnFreq(saData.getChnFreq());
				hold.setPwrRspLen(saData.getPwrRspLen());
				hold.setDutyCycleLen(saData.getDutyCycleLen());
				hold.setChnWidth(saData.getChnWidth());
				maxHold.put(saData.getChnFreq(), hold);
			}
			
			if (maxHold(hold.getPwrRsp(saData.getPwrRspLen()), saData.getPwrRsp())) hasMax = true;
			if (maxHoldDuty(hold.getDutyCycle(saData.getDutyCycleLen()), saData.getDutyCycle())) hasMax = true;
		}
		
		if (hasMax)for (SpectralAnalysisData saData : saDatas)saData.setTimeStamp(this.sample.getTimeStamp());
	}

	public void fillData(SpectralAnalysisDataSample dataAll) {
		dataAll.setTimeStamp(this.sample.getTimeStamp());
		
		fill(dataAll.getPwrRsp24g(), this.sample.getPwrRsp24g());
		fill(dataAll.getPwrRsp5g1(), this.sample.getPwrRsp5g1());
		fill(dataAll.getPwrRsp5g2(), this.sample.getPwrRsp5g2());
		fill(dataAll.getPwrRsp5g3(), this.sample.getPwrRsp5g3());
		
		fill(dataAll.getDutyCycle24g(), this.sample.getDutyCycle24g());
		fill(dataAll.getDutyCycle5g1(), this.sample.getDutyCycle5g1());
		fill(dataAll.getDutyCycle5g2(), this.sample.getDutyCycle5g2());
		fill(dataAll.getDutyCycle5g3(), this.sample.getDutyCycle5g3());
	}

	private void fill(short[] arr, short[] org) {
		for (int i = 0; i < org.length; i++) {
			if (org[i] != -1)arr[i] = org[i];
		}
	}

	public void fillData(Map<Short, SpectralAnalysisData> saData, SpectralAnalysisDataSample sample) {
		List<SpectralAnalysisData> saDatas = new ArrayList<SpectralAnalysisData>();
		for (int i = 0; i < this.saDatas.size(); i++) {
			saData.put(this.saDatas.get(i).getChnFreq(), this.saDatas.get(i));
		}
		for (Short key : saData.keySet()) {
			if (key == (short)0) continue;
			saDatas.add(saData.get(key));
		}
		
		calcucateFreqData(saDatas, sample);
	}
}
