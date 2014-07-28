package com.ah.ui.actions.teacherView;

import java.io.ByteArrayInputStream;

import com.ah.be.common.ValiDateImage;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

public class RandomAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private ByteArrayInputStream inputStream;

	public String execute() throws Exception {
		ValiDateImage rdnu = ValiDateImage.Instance();
		this.setInputStream(rdnu.getImage());
		MgrUtil.setSessionAttribute("VALIDATE_IMAGE_CODE", rdnu.getString());

		return SUCCESS;
	}

	public void setInputStream(ByteArrayInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public ByteArrayInputStream getInputStream() {
		return inputStream;
	}
}
