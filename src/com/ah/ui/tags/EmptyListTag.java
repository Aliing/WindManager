package com.ah.ui.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionSupport;

public class EmptyListTag extends ActionSupport implements Tag {
	protected Tracer log = new Tracer(EmptyListTag.class.getSimpleName());

	protected String key = null;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int doStartTag() throws JspException {
		StringBuffer results = new StringBuffer();
		if (key == null) {
			key = "info.emptyList";
		}
		results
				.append("<tr><td colspan=\"100\" style=\"padding: 2px 0 0 5px;\">");
		results.append(getText(key));
		results.append("</td></tr>");
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
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_PAGE);
	}

	public void release() {
		this.key = null;
	}

	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	public void setParent(Tag parent) {
		this.parent = parent;
	}

	public Tag getParent() {
		return parent;
	}

	private PageContext pageContext;

	private Tag parent;
}
