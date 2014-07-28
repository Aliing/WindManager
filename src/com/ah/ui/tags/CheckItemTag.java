package com.ah.ui.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

public class CheckItemTag extends TagSupport {
	protected Tracer log = new Tracer(CheckItemTag.class.getSimpleName());

	protected String checked = null;
	
	protected String tag = null;

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int doStartTag() throws JspException {
		ValueStack vs = ActionContext.getContext().getValueStack();
		StringBuffer results = new StringBuffer(
				"<input type=\"checkbox\" name=\"selectedIds\" onClick=\"hm.util.toggleCheck(this);\" ");
		Long id = (Long) vs.findValue("id");
		results.append("value=\"" + id + "\" ");
		Boolean selected = (Boolean) vs.findValue("selected");
		if (selected) {
			results.append("checked ");
		}
		if(tag != null){
			results.append("tag=\"" + tag + "\"");
		}
		results.append("/>");
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
		checked = null;
		tag = null;
	}
}
