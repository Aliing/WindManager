package com.ah.be.communication.mo;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SpectralAnalysisDataSample implements Serializable, Cloneable {
	
	public static final int	START_24G_FREQ = 2402;
	public static final int	END_24G_FREQ = 2492;
	
	public static final int	START_5G_SECTION1_FREQ = 5160;
	public static final int	END_5G_SECTION1_FREQ = 5340;
	public static final int	START_5G_SECTION2_FREQ = 5480;
	public static final int	END_5G_SECTION2_FREQ = 5720;
	public static final int	START_5G_SECTION3_FREQ = 5725;
	public static final int	END_5G_SECTION3_FREQ = 5845;
	
	private long timeStamp;
	
	private short pwrRsp24g[];
	
	private short dutyCycle24g[];
	
	private short pwrRsp5g1[];
	
	private short dutyCycle5g1[];
	
	private short pwrRsp5g2[];

	private short dutyCycle5g2[];
	
	private short pwrRsp5g3[];
	
	private short dutyCycle5g3[];

	public short[] getPwrRsp24g() {
		if (this.pwrRsp24g == null) {
			this.pwrRsp24g = new short[END_24G_FREQ - START_24G_FREQ + 1];
			setDefault(this.pwrRsp24g, (short)-1);
		}
		return pwrRsp24g;
	}

	public void setPwrRsp24g(short freq, short pwrRsp24g) {
		if (freq > END_24G_FREQ || freq < START_24G_FREQ)
			return;
		
		if (this.pwrRsp24g == null) {
			this.pwrRsp24g = new short[END_24G_FREQ - START_24G_FREQ + 1];
			setDefault(this.pwrRsp24g, (short)-1);
		}
		if (this.pwrRsp24g[freq - START_24G_FREQ] == -1 || this.pwrRsp24g[freq - START_24G_FREQ] > pwrRsp24g)
			this.pwrRsp24g[freq - START_24G_FREQ] = pwrRsp24g;
	}

	public short[] getDutyCycle24g() {
		if (this.dutyCycle24g == null) {
			this.dutyCycle24g = new short[END_24G_FREQ - START_24G_FREQ + 1];
			setDefault(this.dutyCycle24g, (short)-1);
		}
		return dutyCycle24g;
	}

	public void setDutyCycle24g(short freq, short dutyCycle24g) {
		if (freq > END_24G_FREQ || freq < START_24G_FREQ)
			return;
		
		if (this.dutyCycle24g == null) {
			this.dutyCycle24g = new short[END_24G_FREQ - START_24G_FREQ + 1];
			setDefault(this.dutyCycle24g, (short)-1);
		}
		if (this.dutyCycle24g[freq - START_24G_FREQ] == -1 || this.dutyCycle24g[freq - START_24G_FREQ] < dutyCycle24g)
			this.dutyCycle24g[freq - START_24G_FREQ] = dutyCycle24g;
	}

	public short[] getPwrRsp5g1() {
		if (this.pwrRsp5g1 == null) {
			this.pwrRsp5g1 = new short[END_5G_SECTION1_FREQ - START_5G_SECTION1_FREQ + 1];
			setDefault(this.pwrRsp5g1, (short)-1);
		}
		return pwrRsp5g1;
	}

	public void setPwrRsp5g1(short freq, short pwrRsp5g1) {
		if (freq > END_5G_SECTION1_FREQ || freq < START_5G_SECTION1_FREQ)
			return;
		
		if (this.pwrRsp5g1 == null) {
			this.pwrRsp5g1 = new short[END_5G_SECTION1_FREQ - START_5G_SECTION1_FREQ + 1];
			setDefault(this.pwrRsp5g1, (short)-1);
		}
		if (this.pwrRsp5g1[freq - START_5G_SECTION1_FREQ] == -1 || this.pwrRsp5g1[freq - START_5G_SECTION1_FREQ] > pwrRsp5g1)
			this.pwrRsp5g1[freq - START_5G_SECTION1_FREQ] = pwrRsp5g1;
	}

	public short[] getDutyCycle5g1() {
		if (this.dutyCycle5g1 == null) {
			this.dutyCycle5g1 = new short[END_5G_SECTION1_FREQ - START_5G_SECTION1_FREQ + 1];
			setDefault(this.dutyCycle5g1, (short)-1);
		}
		return dutyCycle5g1;
	}

	public void setDutyCycle5g1(short freq, short dutyCycle5g1) {
		if (freq > END_5G_SECTION1_FREQ || freq < START_5G_SECTION1_FREQ)
			return;
		
		if (this.dutyCycle5g1 == null) {
			this.dutyCycle5g1 = new short[END_5G_SECTION1_FREQ - START_5G_SECTION1_FREQ + 1];
			setDefault(this.dutyCycle5g1, (short)-1);
		}
		if (this.dutyCycle5g1[freq - START_5G_SECTION1_FREQ] == -1 || this.dutyCycle5g1[freq - START_5G_SECTION1_FREQ] < dutyCycle5g1)
			this.dutyCycle5g1[freq - START_5G_SECTION1_FREQ] = dutyCycle5g1;
	}

	public short[] getPwrRsp5g2() {
		if (this.pwrRsp5g2 == null) {
			this.pwrRsp5g2 = new short[END_5G_SECTION2_FREQ - START_5G_SECTION2_FREQ + 1];
			setDefault(this.pwrRsp5g2, (short)-1);
		}
		return pwrRsp5g2;
	}

	public void setPwrRsp5g2(short freq, short pwrRsp5g2) {
		if (freq > END_5G_SECTION2_FREQ || freq < START_5G_SECTION2_FREQ)
			return;
		
		if (this.pwrRsp5g2 == null) {
			this.pwrRsp5g2 = new short[END_5G_SECTION2_FREQ - START_5G_SECTION2_FREQ + 1];
			setDefault(this.pwrRsp5g2, (short)-1);
		}
		if (this.pwrRsp5g2[freq - START_5G_SECTION2_FREQ] == -1 || this.pwrRsp5g2[freq - START_5G_SECTION2_FREQ] > pwrRsp5g2)
			this.pwrRsp5g2[freq - START_5G_SECTION2_FREQ] = pwrRsp5g2;
	}

	public short[] getDutyCycle5g2() {
		if (this.dutyCycle5g2 == null) {
			this.dutyCycle5g2 = new short[END_5G_SECTION2_FREQ - START_5G_SECTION2_FREQ + 1];
			setDefault(this.dutyCycle5g2, (short)-1);
		}
		return dutyCycle5g2;
	}

	public void setDutyCycle5g2(short freq, short dutyCycle5g2) {
		if (freq > END_5G_SECTION2_FREQ || freq < START_5G_SECTION2_FREQ)
			return;
		
		if (this.dutyCycle5g2 == null) {
			this.dutyCycle5g2 = new short[END_5G_SECTION2_FREQ - START_5G_SECTION2_FREQ + 1];
			setDefault(this.dutyCycle5g2, (short)-1);
		}
		if (this.dutyCycle5g2[freq - START_5G_SECTION2_FREQ] == -1 || this.dutyCycle5g2[freq - START_5G_SECTION2_FREQ] < dutyCycle5g2)
			this.dutyCycle5g2[freq - START_5G_SECTION2_FREQ] = dutyCycle5g2;
	}

	public short[] getPwrRsp5g3() {
		if (this.pwrRsp5g3 == null) {
			this.pwrRsp5g3 = new short[END_5G_SECTION3_FREQ - START_5G_SECTION3_FREQ + 1];
			setDefault(this.pwrRsp5g3, (short)-1);
		}
		return pwrRsp5g3;
	}

	public void setPwrRsp5g3(short freq, short pwrRsp5g3) {
		if (freq > END_5G_SECTION3_FREQ || freq < START_5G_SECTION3_FREQ)
			return;
		
		if (this.pwrRsp5g3 == null) {
			this.pwrRsp5g3 = new short[END_5G_SECTION3_FREQ - START_5G_SECTION3_FREQ + 1];
			setDefault(this.pwrRsp5g3, (short)-1);
		}
		if (this.pwrRsp5g3[freq - START_5G_SECTION3_FREQ] == -1 || this.pwrRsp5g3[freq - START_5G_SECTION3_FREQ] > pwrRsp5g3)
			this.pwrRsp5g3[freq - START_5G_SECTION3_FREQ] = pwrRsp5g3;
	}

	public short[] getDutyCycle5g3() {
		if (this.dutyCycle5g3 == null) {
			this.dutyCycle5g3 = new short[END_5G_SECTION3_FREQ - START_5G_SECTION3_FREQ + 1];
			setDefault(this.dutyCycle5g3, (short)-1);
		}
		return dutyCycle5g3;
	}

	public void setDutyCycle5g3(short freq, short dutyCycle5g3) {
		if (freq > END_5G_SECTION3_FREQ || freq < START_5G_SECTION3_FREQ)
			return;
		
		if (this.dutyCycle5g3 == null) {
			this.dutyCycle5g3 = new short[END_5G_SECTION3_FREQ - START_5G_SECTION3_FREQ + 1];
			setDefault(this.dutyCycle5g3, (short)-1);
		}
		if (this.dutyCycle5g3[freq - START_5G_SECTION3_FREQ] == -1 || this.dutyCycle5g3[freq - START_5G_SECTION3_FREQ] < dutyCycle5g3)
			this.dutyCycle5g3[freq - START_5G_SECTION3_FREQ] = dutyCycle5g3;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public static void setDefault(short[] values, short i) {
		for (int j = 0; j < values.length; j++) {
			values[j] = i;
		}
	}

	public SpectralAnalysisDataSample copy() throws CloneNotSupportedException {
		SpectralAnalysisDataSample copy = (SpectralAnalysisDataSample) super.clone();
		copy.pwrRsp24g = (pwrRsp24g != null ? pwrRsp24g.clone() : null);
		copy.pwrRsp5g1 = (pwrRsp5g1 != null ? pwrRsp5g1.clone() : null);
		copy.pwrRsp5g2 = (pwrRsp5g2 != null ? pwrRsp5g2.clone() : null);
		copy.pwrRsp5g3 = (pwrRsp5g3 != null ? pwrRsp5g3.clone() : null);
		
		copy.dutyCycle24g = (dutyCycle24g != null ? dutyCycle24g.clone() : null);
		copy.dutyCycle5g1 = (dutyCycle5g1 != null ? dutyCycle5g1.clone() : null);
		copy.dutyCycle5g2 = (dutyCycle5g2 != null ? dutyCycle5g2.clone() : null);
		copy.dutyCycle5g3 = (dutyCycle5g3 != null ? dutyCycle5g3.clone() : null);
		return copy;
	}
}
