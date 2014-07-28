package com.ah.ui.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.struts2.ServletActionContext;

import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class SortTag extends ActionSupport implements Tag {
	protected Tracer log = new Tracer(SortTag.class.getSimpleName());

	protected String key = null;

	protected String name = null;

	protected String params = null;
	
	protected boolean orderByNumber = false;
	
	private boolean orderByIp = false;

	public String getKey() {
		return (this.key);
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return (name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		ValueStack vs = ServletActionContext.getValueStack(request);
		String sortOperation = (String) vs.findValue("sortOperation");
		SortParams sortParams = null;
		if (params == null) {
			sortParams = (SortParams) ActionContext.getContext().get(
					SessionKeys.PAGE_SORTING);
		} else {
			sortParams = (SortParams) MgrUtil.getSessionAttribute(params);
		}
		StringBuffer url = new StringBuffer();
		if (sortParams == null) {
			url.append("unknown");
		} else {
			if (sortParams.getUrl() == null
					|| sortParams.getUrl().trim().length() == 0) {
				url.append(request.getContextPath() + "/"
						+ ActionContext.getContext().getName() + ".action");
			} else {
				url.append(sortParams.getUrl());
			}
			url.append("?operation=");
			if (sortOperation == null || sortOperation.length() == 0) {
				url.append(Navigation.OPERATION_SORT);
			} else {
				url.append(sortOperation);
			}
			if (url.indexOf("?") < 0) {
				url.append("?");
			} else {
				url.append("&");
			}
			url.append("orderBy");
			url.append("=");
			url.append(name);
			// url.append(ResponseUtils.filter(name));
			url.append("&");

			boolean ascending = true;
			// if customizeOrderKey==true sortParams.getOrderBy() will no equal with name
			if (name.equals(sortParams.getOrderBy()) || isCustomizeOrderKey(name, sortParams.getOrderBy())) {
				ascending = !sortParams.isAscending();
			}
			url.append("ascending");
			url.append("=");
			url.append(Boolean.toString(ascending));
			if (orderByNumber) {
				url.append("&");
				url.append("orderByNumber");
				url.append("=");
				url.append(Boolean.toString(orderByNumber));
			}
			if(orderByIp){
				url.append("&");
				url.append("orderByIp");
				url.append("=");
				url.append(Boolean.toString(orderByIp));
			}
		}

		StringBuffer results = new StringBuffer("<a href=\"");
		results.append(response.encodeURL(url.toString()));
		results.append("\">");
		results.append("<b>");
		results.append(getText(key));
		results.append("</b></a>");
		if (sortParams != null && (name.equals(sortParams.getOrderBy()) || isCustomizeOrderKey(name, sortParams.getOrderBy()))) {
			results.append("<img src=\"");
			results.append(request.getContextPath());
			results.append("/images/spacer.gif\" class=\"dinl\" width=\"5\" >");
			results.append("<img src=\"");
			results.append(request.getContextPath());
			if (sortParams.isAscending()) {
				results
						.append("/images/sorting/arrow_up.png\" class=\"dinl\" >");
			} else {
				results
						.append("/images/sorting/arrow_down.png\" class=\"dinl\" >");
			}
		}
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
		this.name = null;
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

	public boolean isOrderByNumber() {
		return orderByNumber;
	}

	public void setOrderByNumber(boolean orderByNumber) {
		this.orderByNumber = orderByNumber;
	}

	public boolean isOrderByIp() {
		return orderByIp;
	}

	public void setOrderByIp(boolean orderByIp) {
		this.orderByIp = orderByIp;
	}

	public boolean isCustomizeOrderKey(String name, String sessionOrderBy) {
		if ("ifName".equals(name)
				&& sessionOrderBy != null
				&& sessionOrderBy.indexOf("case when") >= 0
				&& sessionOrderBy.indexOf("ifName") >= 0) {
			// client monitor page click interface name to order by, value of sessionOrderBy reference ClientMonitorAction.updateSortParams()
			return true;
		}
		
		return false;
	}
}
