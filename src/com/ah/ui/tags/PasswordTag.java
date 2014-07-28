package com.ah.ui.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

@SuppressWarnings("serial")
@Deprecated
//write it for show password in clear text feature(3.2r2)
//deprecated comment: Not satisfied with all the circumstances.
public class PasswordTag extends TagSupport {

	protected Tracer	log	= new Tracer(PasswordTag.class.getSimpleName());

	protected String	rangeName;

	protected String	id;

	protected String	name;

	protected String	maxlength;

	protected String	size;

	protected String	onkeypress;

	public int doStartTag() throws JspException {
		ValueStack vs = ActionContext.getContext().getValueStack();

		// password field
		StringBuffer results = new StringBuffer("<input type=\"password\"");
		if (id != null && id.trim().length() > 0) {
			results.append(" id=\"" + id + "\"");
		}
		if (name != null && name.trim().length() > 0) {
			results.append(" name=\"" + name + "\"");
		}
		if (size != null && size.trim().length() > 0) {
			if (size.startsWith("%{") && size.endsWith("}")) {
				size = size.substring(2, size.length() - 1);
				size = String.valueOf(vs.findValue(size));
			}

			results.append(" size=\"" + size + "\"");
		}
		if (maxlength != null && maxlength.trim().length() > 0) {
			if (maxlength.startsWith("%{") && maxlength.endsWith("}")) {
				maxlength = maxlength.substring(2, maxlength.length() - 1);
				maxlength = String.valueOf(vs.findValue(maxlength));
			}

			results.append(" maxlength=\"" + maxlength + "\"");
		}
		if (onkeypress != null && onkeypress.trim().length() > 0) {
			results.append(" onkeypress=\"" + onkeypress + "\"");
		}

		results.append(" />");

		// password range field
		if (rangeName != null && rangeName.trim().length() > 0) {
			results.append("&nbsp;");
			results.append(MgrUtil.getUserMessage(rangeName));
		}

		// show password checkbox field
		results.append("\n");
		results
				.append("<input type=\"checkbox\" id=\"ignore\" name=\"ignore\" onclick=\"hm.util.toggleShowPassword(this.checked,'"
						+ id + "');\" />");
		results.append(MgrUtil.getUserMessage("admin.user.showPassword"));

		JspWriter writer = pageContext.getOut();
		try {
			writer.print(results.toString());
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_BODY_INCLUDE);
	}

	public int doEndTag() throws JspException {
		JspWriter writer = pageContext.getOut();
		try {
			writer.print("");
			release();
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_PAGE);
	}

	public void release() {
		id = null;
		name = null;
		maxlength = null;
		rangeName = null;
		size = null;
	}

	public String getRangeName() {
		return rangeName;
	}

	public void setRangeName(String rangerName) {
		this.rangeName = rangerName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getOnkeypress() {
		return onkeypress;
	}

	public void setOnkeypress(String showPassword) {
		this.onkeypress = showPassword;
	}
}
