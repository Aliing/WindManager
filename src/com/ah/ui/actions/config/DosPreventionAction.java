package com.ah.ui.actions.config;

import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosParams.DosAction;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.ui.actions.BaseAction;

public class DosPreventionAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	protected void updateDosParams() {
		DosPrevention dosPrevention = getDataSource();
		int i = 0, ei = 0;
		for (DosParams dosParams : dosPrevention.getDosParamsMap().values()) {
			boolean enableRow = false;
			if (enabled != null && ei < enabled.length) {
				try {
					int enabledIndex = Integer.parseInt(enabled[ei]);
					if (i == enabledIndex) {
						enableRow = true;
						ei++;
					}
				} catch (NumberFormatException e) {
					// Bug in struts, it should not set false in this array if
					// no row is enabled
					enabled = null;
				}
			}
			dosParams.setEnabled(enableRow);
			if ((radioMacDos == null && getDataSource().getDosType()== DosType.MAC_STATION)
			 || (radioMacDos != null && radioMacDos.equals("station"))) {
				dosParams.setDosActionTime(dosActionTime[i]);
				// dosParams.setDosAction(DosAction.valueOf(dosAction[i]));
			}
			if (dosAction != null) {
				dosParams.setDosActionTime(dosActionTime[i]);
				dosParams.setDosAction(DosAction.valueOf(dosAction[i]));
			}
			if (alarmInterval != null) {
				dosParams.setAlarmInterval(alarmInterval[i]);
			}
			dosParams.setAlarmThreshold(alarmThreshold[i++]);
		}
	}

	public DosPrevention getDataSource() {
		return (DosPrevention) super.getDataSource();
	}

	protected int[] alarmThreshold;

	protected int[] alarmInterval;

	protected String[] dosAction;

	protected int[] dosActionTime;

	protected String[] enabled;

	public void setAlarmInterval(int[] alarmInterval) {
		this.alarmInterval = alarmInterval;
	}

	public void setAlarmThreshold(int[] alarmThreshold) {
		this.alarmThreshold = alarmThreshold;
	}

	public void setEnabled(String[] enabled) {
		this.enabled = enabled;
	}

	public void setDosAction(String[] dosAction) {
		this.dosAction = dosAction;
	}

	public void setDosActionTime(int[] dosActionTime) {
		this.dosActionTime = dosActionTime;
	}

	private String radioMacDos;

	public String getRadioMacDos() {
		return radioMacDos;
	}

	public void setRadioMacDos(String radioMacDos) {
		this.radioMacDos = radioMacDos;
	}

	private String actionTime_div = "none";

	public String getActionTime_div() {
		return actionTime_div;
	}

	public void setActionTime_div(String actionTime_div) {
		this.actionTime_div = actionTime_div;
	}

}