/**
 *@filename		CreateOrSelectObjectTag.java
 *@version
 *@author		Fiona
 *@createtime	2009-03-12 AM 09:57:09
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.ui.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class CreateOrSelectObjectTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	protected Tracer log = new Tracer(SelectTag.class.getSimpleName());

	private String list;

	private String typeString;

	private String selectIdName;

	private String inputValueName;

	private String divId;

	private String swidth;

	private String stitle;

	private String tlength;
	
	private boolean hideButton = false;
	
	private String callbackFn = "";
	
	private String inputKeyPress = "name";
	
	private String newFn;
	private String editFn;

	public String getCallbackFn()
	{
		return callbackFn;
	}

	public void setCallbackFn(String callbackFn)
	{
		this.callbackFn = callbackFn;
	}

	public boolean isHideButton()
	{
		return hideButton;
	}

	public void setHideButton(boolean hideButton)
	{
		this.hideButton = hideButton;
	}

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
		if (null == swidth) {
			swidth = "250px";
		}
		int twidth = Integer.parseInt(swidth.substring(0, swidth.length()-2))-28;
		if (null == tlength) {
			tlength = "32";
		}
		String strWrite = (String) vs.findValue("writeDisabled");
		String strText = (String) vs.findValue(inputValueName);
		boolean isFullMode = (Boolean) vs.findValue("fullMode");
		boolean isDisable = false;
		String objType = BaseAction.SIMPLE_OBJECT_IP;
		if (null != typeString) {
			// for user profile's user attribute group
			if ("UserAttribute".equals(typeString)) {
				isDisable = (Boolean) vs.findValue("disabledName");
			}
			// for tunnel setting
			if ("IpAddressTunnel".equals(typeString)) {
				isDisable = (Boolean) vs.findValue("disabledIpAddress");
			}
			
			if (typeString.toLowerCase().contains("mac")) {
				objType = BaseAction.SIMPLE_OBJECT_MAC;
			} else if (typeString.toLowerCase().contains("vlan")) {
				objType = BaseAction.SIMPLE_OBJECT_VLAN;
			} else if (typeString.toLowerCase().contains("attribute")) {
				objType = BaseAction.SIMPLE_OBJECT_ATTRI;
			}
		}
		String strTitle = "";
		if (null != stitle) {
			strTitle = MgrUtil.getUserMessage(stitle);
			if (null == strTitle) {
				strTitle = stitle;
			}
			strTitle = "&#10;" + strTitle;
		}
		if (typeString!=null && typeString.toLowerCase().contains("vlan")) {
			strTitle = "To add a VLAN, choose \"Create new VLAN\" and type a new entry." + strTitle;
		} else {
			strTitle = "To add: Choose the blank space and type a new entry." + strTitle;
		}
		
//		int inputTagHeight = adjustInputHeightForIE(16);
//		int inputTagWidth = adjustInputWidth(twidth);
		
		// deal with the bug in iframe
//		if (null != vs.findValue("showUpInNavTree")) {
//			boolean isFrame = (Boolean) vs.findValue("showUpInNavTree");
//			
//			if (!isFrame) {
//				inputTagHeight = adjustInputHeightForIE(19);
//				twidth += 5;
//			}
//		}
		
		results.append("<div style=\"position:relative;\" id=\""+divId+"\">");
		results.append("<span style=\"overflow:hidden;\">");
		results.append("<select id=\""+selectIdName+"\"");
		
		if (isDisable) {
			results.append(" disabled=\""+isDisable+"\"");
		}
		results.append(" style=\"width:"+swidth+";\" onchange=\"hm.util.singObjectSelect(this, '");
		results.append(inputValueName);
		results.append("');\"");
		// press delete key can remove direct (except user profile attribute)
		if (!isFullMode && objType != BaseAction.SIMPLE_OBJECT_ATTRI) {
			results.append(" onkeydown=\"hm.simpleObject.removeSimpleByDelKey(event, '");
			results.append(objType);
			results.append("','");
			results.append(selectIdName);
			results.append("','");
			results.append(inputValueName);
			results.append("','");
			results.append(callbackFn);
			results.append("')\"");
			strTitle += " To remove: Choose an entry and press Delete.";
		}
		results.append(" alt='"+strTitle.trim()+"' title='"+strTitle+"'");
		results.append(">");
		boolean isInlist = false;
		if (options.size() == 0) {
			results.append("<option value=\"-1\">"
					+ MgrUtil.getUserMessage("config.optionsTransfer.none")
					+ "</option>");
		} else {
			for (Object option : options) {
				if (null != strText && strText.equals(MgrUtil
						.getAttributeValue(option, "value"))) {
					if (!"".equals(strText)) {
						isInlist = true;
					}
					results.append("<option selected value=\"");
				} else {
					results.append("<option value=\"");
				}
				results.append(MgrUtil.getAttributeValue(option, "id"));
				results.append("\">");
				results
						.append(MgrUtil
								.getAttributeValue(option, "value"));
				results.append("</option>");
			}
		}
		results.append("</select>");
		results.append("<input type=\"text\" id=\""+inputValueName+"\" name=\""+inputValueName+"\" autocomplete=\"off\"");
		if (null != strText && !"".equals(strText)) {
			results.append(" value=\""+strText+"\"");
		} else if (isDisable) {
			results.append(" disabled=\"true\"");
		}
		results.append(" style=\"width:"+(twidth - 4)+"px;z-index:");
		if (isInlist) {
			results.append("-1;\"");
		} else {
			results.append("2;\"");
		}
		results.append("class=\"selectInput\" onkeypress=\"return hm.util.keyPressPermit(event,'"+inputKeyPress+"');\" onblur=\"return hm.util.onblurEvent(this,'"+inputValueName+"');\" maxlength=\""+tlength+"\"/>");
		results.append("</span>");
		if (null != typeString && !"".equals(typeString) && !hideButton && isFullMode) {
			if (strWrite.equals("disabled") || isDisable) {
				results.append("&nbsp;<img class=\"dinl marginBtn\" src=\"images/new_disable.png\"");
				results.append(" width=\"16\" height=\"16\" alt=\"New\" title=\"New\" />");
				results.append("&nbsp;<img class=\"dinl marginBtn\" src=\"images/modify_disable.png\"");
				results.append(" width=\"16\" height=\"16\" alt=\"Modify\" title=\"Modify\" />");
			} else {
			    if(StringUtils.isNotBlank(newFn) && StringUtils.isNotBlank(editFn)) {
			        results.append("&nbsp;<a class=\"marginBtn\" href=\"javascript:"+ newFn +"()\">");
			        results.append("<img class=\"dinl\"");
			        results.append(" src=\"images/new.png\" width=\"16\" height=\"16\" alt=\"New\" title=\"New\" /></a>");
			        results.append("&nbsp;<a class=\"marginBtn\" href=\"javascript:" + editFn + "()\">");
			        results.append("<img class=\"dinl\"");
			        results.append(" src=\"images/modify.png\" width=\"16\" height=\"16\" alt=\"Modify\" title=\"Modify\" /></a>");
			    } else {
			        results.append("&nbsp;<a class=\"marginBtn\" href=\"javascript:submitAction('new"+typeString+"')\">");
			        results.append("<img class=\"dinl\"");
			        results.append(" src=\"images/new.png\" width=\"16\" height=\"16\" alt=\"New\" title=\"New\" /></a>");
			        results.append("&nbsp;<a class=\"marginBtn\" href=\"javascript:submitAction('edit"+typeString+"')\">");
			        results.append("<img class=\"dinl\"");
			        results.append(" src=\"images/modify.png\" width=\"16\" height=\"16\" alt=\"Modify\" title=\"Modify\" /></a>");
			    }
			}
		}
		results.append("</div>");
	}

	/**
	 * fix issue with FF2, input tag lower 2px than select tag
	 */
	private int adjustInputHeight(int normalValue) {
		ServletRequest req = pageContext.getRequest();
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			String userAgent = httpReq.getHeader("user-agent");
			if (null != userAgent) {
				// e.g Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN;
				// rv:1.8.1.17) Gecko/20080829 Firefox/2.0.0.17
				if (userAgent.contains("Mozilla")
						&& userAgent.contains("Gecko")
						&& userAgent.contains("rv:1.8")) {
					normalValue = normalValue + 2;
				}
			}
		}
		return normalValue;
	}
	
	private int adjustInputHeightForIE(int normalValue) {
		ServletRequest req = pageContext.getRequest();
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			String userAgent = httpReq.getHeader("user-agent");
			if (null != userAgent) {
				// IE browser
				if (userAgent.contains("MSIE")) {
					normalValue -= 1;
				}
			}
		}
		return normalValue;
	}
	
	private int adjustInputWidth(int normalValue) {
		ServletRequest req = pageContext.getRequest();
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			String userAgent = httpReq.getHeader("user-agent");
			if (null != userAgent) {
				// IE browser
				if (userAgent.contains("MSIE")) {
					normalValue -= 5;
				}else if(userAgent.contains("Chrome/")){
					normalValue -= 6;
				}
			}
		}
		return normalValue;
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
		this.list = null;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public String getSelectIdName() {
		return selectIdName;
	}

	public void setSelectIdName(String selectIdName) {
		this.selectIdName = selectIdName;
	}

	public String getInputValueName() {
		return inputValueName;
	}

	public void setInputValueName(String inputValueName) {
		this.inputValueName = inputValueName;
	}

	public String getSwidth() {
		return swidth;
	}

	public void setSwidth(String swidth) {
		this.swidth = swidth;
	}

	public String getTlength() {
		return tlength;
	}

	public void setTlength(String tlength) {
		this.tlength = tlength;
	}

	public String getDivId() {
		return divId;
	}

	public void setDivId(String divId) {
		this.divId = divId;
	}

	public String getStitle() {
		return stitle;
	}

	public void setStitle(String stitle) {
		this.stitle = stitle;
	}

	public String getInputKeyPress()
	{
		return inputKeyPress;
	}

	public void setInputKeyPress(String inputKeyPress)
	{
		this.inputKeyPress = inputKeyPress;
	}

    public String getNewFn() {
        return newFn;
    }

    public String getEditFn() {
        return editFn;
    }

    public void setNewFn(String newFn) {
        this.newFn = newFn;
    }

    public void setEditFn(String editFn) {
        this.editFn = editFn;
    }

}
