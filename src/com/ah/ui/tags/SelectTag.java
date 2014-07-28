package com.ah.ui.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

public class SelectTag extends TagSupport {
	protected Tracer log = new Tracer(SelectTag.class.getSimpleName());

	String list;

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public int doStartTag() throws JspException {
		StringBuffer results = new StringBuffer("\n");

		generateSelectTag(results);

		JspWriter writer = pageContext.getOut();
		try {
			writer.print(results.toString());
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_BODY_INCLUDE);
	}

	protected void generateSelectTag(StringBuffer results) {
		ValueStack vs = ActionContext.getContext().getValueStack();
		if (list == null) {
			return;
		}
		Collection options = (Collection) vs.findValue(list);
		if (options == null) {
			options = new ArrayList();
		}
		String name = vs.findString("name");
		String listKeyAttr = vs.findString("listKey");
		String listValueAttr = vs.findString("listValue");
		String width = vs.findString("width");
		String size = vs.findString("size");

		if (width == null || width.equals("")) {
			width = "130px";
		}

		if (size == null || size.equals("") || size.equals("0")) {
			size = "8";
		}

		if (listKeyAttr == null || listValueAttr == null) {
			return;
		}
		boolean left = "leftOptions".equals(list);
		String opponentId = "leftOptions_" + name;
		results.append("<select id=\"");
		if (left) {
			results.append(list + "_");
			opponentId = name;
		}
		results.append(name + "\" multiple");
		if (!left) {
			results.append(" name=\"" + name + "\"");
		}
		results
				.append(" size=\""
						+ size
						+ "\" style=\"width: "
						+ width
						+ ";\" onchange=\"hm.util.showtitle(this); hm.util.unselectItems('"
						+ opponentId + "'); \">");
		if (options.size() == 0 && left) {
			results.append("<option value=\"-1\">"
					+ MgrUtil.getUserMessage("config.optionsTransfer.none")
					+ "</option>");
		} else {
			for (Object option : options) {
				results.append("<option value=\"");
				results.append(MgrUtil.getAttributeValue(option, listKeyAttr));
				results.append("\">");
				results
						.append(MgrUtil
								.getAttributeValue(option, listValueAttr));
				results.append("</option>");
			}
		}
		results.append("</select>");
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
		list = null;
	}
}
