package com.ah.ui.actions.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.ah.bo.admin.HmAuditLog;
import com.ah.mdm.core.profile.entity.OnBoardUIInfo;
import com.ah.mdm.core.profile.impl.OnBoardUISettingServiceImpl;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class OnBoardUISettingAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Tracer logger = new Tracer(
			OnBoardUISettingAction.class.getSimpleName());

	private OnBoardUISettingServiceImpl onBoardImpl = new OnBoardUISettingServiceImpl();

	@Override
	public OnBoardUIInfo getDataSource() {
		return (OnBoardUIInfo) dataSource;
	}

	private File logoImageFile;

	private File horMainImageFile;

	private File verMainImageFile;

	private String uploadImageName = "";

	private int onboardUIPageId;

	private int onboardUIPreviewMethod;

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ONBOARD_UI_SETTING);
		setDataSource(OnBoardUIInfo.class);
		// keyColumnId = COLUMN_USERGROUPNAME;
		// tableId = HmTableColumn.TABLE_ADMINGROUP;
	}

	public int getNameLength() {
		return 32;
	}

	private void initValues() throws Exception {
		OnBoardUIInfo initUIInfo = onBoardImpl.getOnBoardUIPage(this
				.getUserContext().getOwner().getInstanceId());
		if (initUIInfo != null) {
			setSessionDataSource(initUIInfo);
		}
	}

	public String getDisabled4Index() {
		return "";
	}

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("new".equals(operation)) {
				return INPUT;
			} else if ("reset".equals(operation)) {
				boolean result = onBoardImpl.resetOnBoardUIPage(this
						.getUserContext().getOwner().getInstanceId());
//				if(result){
//					return SUCCESS;
//				}else{
//					return INPUT;
//				}

				return "success2";
			} else if ("previewImage".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				jsonObject.put(uploadImageName,
						transferLogoImage(uploadImageName));
				jsonObject.put("freshImageName", uploadImageName);

				return "json";
			} else if ("openPreviewPage".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				jsonObject.put("iFrameData", getPreviewData());
				jsonObject.put("previewPageName", getPreivewPageName());
				return "json";
			} else if ("customize".equals(operation)) {
				boolean result = onBoardImpl.customizedOnBoardUIPage(this
						.getUserContext().getOwner().getInstanceId(),
						getDataSource());
//				if (result) {
//					return SUCCESS;
//				} else {
//
//				}
//				return INPUT;
				return "success2";
			} else {
				initValues();
				baseOperation();
				return INPUT;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
					+ " " + MgrUtil.getUserMessage(e));
			try {
				return INPUT;
			} catch (Exception ne) {
				return prepareEmptyBoList();
			}
		}
	}

	private String getPreivewPageName() {
		String pageName = "";
		switch (onboardUIPageId) {

		case 1:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.pageName.1");
			break;

		case 2:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.pageName.2");
			break;
		case 3:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.pageName.3");
			break;

		default:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.pageName.1");

		}
		
		pageName="Preview " + pageName;
		return pageName;
	}

	private String getPreviewData() {
		String result = "";
		String pageName = "";
		switch (onboardUIPageId) {

		case 1:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.restful.pageName.1");
			break;

		case 2:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.restful.pageName.2");
			break;
		case 3:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.restful.pageName.3");
			break;

		default:
			pageName = MgrUtil
					.getEnumString("enum.onboardUISetting.restful.pageName.1");

		}
		result = onBoardImpl.previewOnBoardUIPage(this.getUserContext()
				.getOwner().getInstanceId(), pageName, getDataSource());
		return result;
	}

	public File getLogoImageFile() {
		return logoImageFile;
	}

	public void setLogoImageFile(File logoImageFile) {
		this.logoImageFile = logoImageFile;
	}

	public EnumItem[] getEnumOnboardUIPageName() {
		return MgrUtil.enumItems("enum.onboardUISetting.pageName.", new int[] {
				1, 2, 3 });
	}

	public EnumItem[] getEnumOnboardUIPreviewMethod() {
		return MgrUtil.enumItems("enum.onboardUISetting.previewMethod.",
				new int[] { 1, 2 });
	}

	private String transferLogoImage(String imageName) {
		String realImage = "";
		File transferFile = null;
		if (imageName.equals("logoImage")) {
			transferFile = logoImageFile;
		} else if (imageName.equals("horMainImage")) {
			transferFile = horMainImageFile;
		} else if (imageName.equals("verMainImage")) {
			transferFile = verMainImageFile;
		}
		try {
			if (null != transferFile) {
				// the file cannot be empty
				if (transferFile.length() == 0) {
					addActionError(MgrUtil
							.getUserMessage("error.licenseFailed.file.invalid"));
					return "";
				}

				byte[] buffer = file2ByteArray(transferFile);
				realImage = Base64.encodeBase64String(buffer);
				if (imageName.equals("logoImage")) {
					getDataSource().setLogoImage(realImage);
				} else if (imageName.equals("horMainImage")) {
					getDataSource().setHorMainImage(realImage);
				} else if (imageName.equals("verMainImage")) {
					getDataSource().setVerMainImage(realImage);
				}

			} else {
				addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
			}

		} catch (Exception e) {
			return "";
		}
		return realImage;
	}

	public static byte[] file2ByteArray(File inFile) {
		byte[] ret = null;
		InputStream in = null;
		try {
			in = new FileInputStream(inFile);

			byte[] buffer = new byte[1024];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int readSize = 0;
			try {
				while ((readSize = in.read(buffer)) >= 0) {
					out.write(buffer, 0, readSize);
				}
				ret = out.toByteArray();
			} catch (IOException e) {
				logger.error(e.getMessage());
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}
		return ret;
	}

	public File getHorMainImageFile() {
		return horMainImageFile;
	}

	public void setHorMainImageFile(File horMainImageFile) {
		this.horMainImageFile = horMainImageFile;
	}

	public File getVerMainImageFile() {
		return verMainImageFile;
	}

	public void setVerMainImageFile(File verMainImageFile) {
		this.verMainImageFile = verMainImageFile;
	}

	public String getUploadImageName() {
		return uploadImageName;
	}

	public void setUploadImageName(String uploadImageName) {
		this.uploadImageName = uploadImageName;
	}

	public int getOnboardUIPageId() {
		return onboardUIPageId;
	}

	public void setOnboardUIPageId(int onboardUIPageId) {
		this.onboardUIPageId = onboardUIPageId;
	}

	public int getOnboardUIPreviewMethod() {
		return onboardUIPreviewMethod;
	}

	public void setOnboardUIPreviewMethod(int onboardUIPreviewMethod) {
		this.onboardUIPreviewMethod = onboardUIPreviewMethod;
	}

}
