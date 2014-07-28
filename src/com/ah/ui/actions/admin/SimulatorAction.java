package com.ah.ui.actions.admin;

import org.json.JSONObject;

import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.mo.SimulateHiveAP;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.devices.impl.Device;

public class SimulatorAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(SimulatorAction.class
															.getSimpleName());

	private short				hiveApModel			= HiveAp.HIVEAP_MODEL_20;

	private int					apNumber			= 0;

	private int					clientNumber		= 0;

	private int					appleRate			= 20;

	private int					dellRate			= 30;

	private int					hpRate				= 30;

	private int					lenovoRate			= 20;

	private static final String	APPLE_OUI			= "0016CB";

	private static final String	DELL_OUI			= "0019B9";

	private static final String	HP_OUI				= "001DB3";

	private static final String	LENOVO_OUI			= "0012FE";

	private boolean				isEnterFromTool		= true;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("simulate".equals(operation)) {
				jsonObject = new JSONObject();

				boolean isSucc = false;
				String jsonMessage;

				String errorMessage = simulatorOperation();
				if (errorMessage == null || errorMessage.length() == 0) {
					isSucc = true;
					jsonMessage = HmBeResUtil.getString("tools.simulator.success");
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.simulate.client",NmsUtil.getOEMCustomer().getAccessPonitName()));

					// wait for simulate event report to HM, let's wait here for 2 seconds at first.
					synchronized (Thread.currentThread()) {
						try {
							Thread.currentThread().wait(2000);
						} catch (Exception e) {
							log.error("wait thread error.", e);
						}
					}
				} else {
					jsonMessage = HmBeResUtil.getString("tools.simulator.error") + " "
							+ errorMessage;
					generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.simulate.client",NmsUtil.getOEMCustomer().getAccessPonitName()));
				}

				jsonObject.put("succ", isSucc);
				jsonObject.put("message", jsonMessage);
				jsonObject.put("apNumber", getRemainingAPNum());
				return "json";
			} else if ("blankSimulator".equals(operation)) {
				isEnterFromTool = false;
				return "blankSimulator";
			} else if ("setSimulatorFlag".equals(operation)) {
				MgrUtil.setSessionAttribute("hasPopupSimulator", true);
				return "json";
			} else {
				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HM_SIMULATOR);
	}

	private String simulatorOperation() {
		int remaining = getRemainingAPNum();
		if (apNumber > remaining) {
			return "Cannot simulate " + apNumber + " "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s. The remaining "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s number is "
					+ remaining + ".";
		}

		return BeTopoModuleUtil.simulateAP(hiveApModel, SimulateHiveAP
				.getFullProductName(hiveApModel), apNumber, getClientInfo(), getDomain());
	}

	private String getClientInfo() {
		StringBuffer buf = new StringBuffer(String.valueOf(clientNumber));
		buf.append(",");
		buf.append(APPLE_OUI);
		buf.append(",");
		buf.append(String.valueOf(appleRate));
		buf.append(",");
		buf.append(DELL_OUI);
		buf.append(",");
		buf.append(String.valueOf(dellRate));
		buf.append(",");
		buf.append(HP_OUI);
		buf.append(",");
		buf.append(String.valueOf(hpRate));
		buf.append(",");
		buf.append(LENOVO_OUI);
		buf.append(",");
		buf.append(String.valueOf(lenovoRate));

		return buf.toString();
	}

	public int getRemainingAPNum() {
		return getDomain().getMaxSimuAp() - getDomain().computeManagedSimApNum();
	}

	@Override
	public String getWriteDisabled() {
		if (!writePermission) {
			return "disabled";
		}

		String simulatorFlag = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_APPLICATION_SUPPORTSIMULATOR, "1");
		if (Integer.valueOf(simulatorFlag) != 1) {
			return "disabled";
		}

		return "";
	}

	public EnumItem[] getApModelList() {
		return NmsUtil.filterHiveAPModel(HIVEAP_MODEL4Simulator, this.isEasyMode());
	}

	public static EnumItem[]	HIVEAP_MODEL4Simulator	= MgrUtil.enumItems(Device.NAME,
			HiveAp.HIVEAP_MODEL_320, HiveAp.HIVEAP_MODEL_340, HiveAp.HIVEAP_MODEL_330, HiveAp.HIVEAP_MODEL_350, HiveAp.HIVEAP_MODEL_370,HiveAp.HIVEAP_MODEL_390, HiveAp.HIVEAP_MODEL_120,
			HiveAp.HIVEAP_MODEL_110, HiveAp.HIVEAP_MODEL_170, HiveAp.HIVEAP_MODEL_BR100, HiveAp.HIVEAP_MODEL_BR200, HiveAp.HIVEAP_MODEL_BR200_WP, HiveAp.HIVEAP_MODEL_BR200_LTE_VZ,
			HiveAp.HIVEAP_MODEL_121, HiveAp.HIVEAP_MODEL_141, HiveAp.HIVEAP_MODEL_SR24, HiveAp.HIVEAP_MODEL_SR2124P, HiveAp.HIVEAP_MODEL_SR2148P, HiveAp.HIVEAP_MODEL_SR2024P,HiveAp.HIVEAP_MODEL_230);

	public short getHiveApModel() {
		return hiveApModel;
	}

	public void setHiveApModel(short hiveApModel) {
		this.hiveApModel = hiveApModel;
	}

	public int getApNumber() {
		return apNumber;
	}

	public void setApNumber(int apNumber) {
		this.apNumber = apNumber;
	}

	public int getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(int clientNumber) {
		this.clientNumber = clientNumber;
	}

	public int getAppleRate() {
		return appleRate;
	}

	public void setAppleRate(int appleRate) {
		this.appleRate = appleRate;
	}

	public int getDellRate() {
		return dellRate;
	}

	public void setDellRate(int dellRate) {
		this.dellRate = dellRate;
	}

	public int getHpRate() {
		return hpRate;
	}

	public void setHpRate(int hpRate) {
		this.hpRate = hpRate;
	}

	public int getLenovoRate() {
		return lenovoRate;
	}

	public void setLenovoRate(int lenovoRate) {
		this.lenovoRate = lenovoRate;
	}

	public boolean getIsEnterFromTool() {
		return isEnterFromTool;
	}

	public void setEnterFromTool(boolean isEnterFromTool) {
		this.isEnterFromTool = isEnterFromTool;
	}

	public int getMaxSimuApNumLength() {
		return String.valueOf(getRemainingAPNum()).length();
	}

	public int getMaxSimuClientNumLength() {
		return String.valueOf(getDomain().getMaxSimuClient()).length();
	}

}