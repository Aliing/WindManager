/**
 * @filename			CheckListTag.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				5.0
 * 
 * Copyright (c) 2006-2011 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ui.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.bo.port.PortBasicProfile;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.CheckItem;
import com.ah.util.CheckItem3;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

public class CheckListTag extends TagSupport {
	private static final long serialVersionUID = 3850818831202749251L;
	
	private static final String DEFAULT_SELECTED_COLOR = "#ffc20e";
	
	private static final String GRAY_COLOR = "#B9C1C1";
	
	protected Tracer log = new Tracer(CheckListTag.class.getSimpleName());
	
	private String name;
	
	private String list;
	
	private String listKey;
	
	private String listValue;
	
	private String value;

	private String width;
	
	private String height;
	
	private String multiple;
	// Specific id if the container (id of the table)
	private String containerId;
	// Edit event, use for enable to edit the item in list.
	private String editEvent;
	// Edit menu
	private String editMenuText = "Edit";
	// Clone event, use for enable to clone the item in list.
	private String cloneEvent;
	// Remove event, use for enable to remove the item in list.
	private String removeEvent;
	
	// Width for the item (for override the width which is defined in CSS class - 'ellipsis')
	private String itemWidth;
	// Disable ellipsis flag
	private boolean disableEllipsis;
	
	private String specialMultiple;
	
	private String specialValue;
	
	private boolean enableMouseEvent;
	
	private boolean autoSort = true;
	
	// Click event,use for click the item in list
	private String clickEvent;
	
	private String menuContainerStyle = "";
	
	//In general case, listKey type is long, but for some special case, listKye type is String
	//It is used at BonjourGatewayMonitor page.
	private boolean listKeyIsString = false;
	
	//gray out the unselected device templates when it's device function and type is same as the selected one!
	//and make it disabled!
	private boolean grayOutSimilar = false;
	
	public int doStartTag() throws JspException {
		StringBuffer results = new StringBuffer("\n");

		generateCheckListTag(results);

		JspWriter writer = pageContext.getOut();
		try {
			writer.print(results.toString());
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_BODY_INCLUDE);
	}
	
	protected void generateCheckListTag(StringBuffer results) {
		ValueStack vs = ActionContext.getContext().getValueStack();
		
		if (list == null) {
			log.error("Property 'list' is null.");
			return ;
		}
		
		if (name == null) {
			log.error("Property 'name' is null.");
			return ;
		}
		
		if(width == null) {
			width = "250px";
		}
		
		if(height == null) {
			height = "120px";
		}
		
		if(listKey == null) {
			listKey = "id";
		}
		
		if(listValue == null) {
			listValue = "value";
		}
		
		boolean isMultiple;
		
		if(multiple == null) {
			isMultiple = false;
		} else {
			isMultiple = (Boolean)vs.findValue(multiple);
		}
		
		boolean isSpecialMultiple;
		if (specialMultiple == null) {
			isSpecialMultiple = false;
		} else {
			isSpecialMultiple = (Boolean)vs.findValue(specialMultiple);
		}
		Collection specialValues = (Collection)vs.findValue(specialValue);
		
		Collection options = (Collection) vs.findValue(list);
		
		if (options == null) {
			options = new ArrayList();
		}
		
		Object values =  vs.findValue(value);
		
        results.append("<div id=\"" + name+ "Div" + "\" " +
				"style=\"text-align: left; overflow-x: hidden; overflow-y: auto; height: " + height + ";border: 1px solid darkgray;\">");
		String tableId = null == containerId ? "dataTable" : containerId;
		results.append("<table id=\"" + tableId + "\" cellspacing=\"0\" cellpadding=\"0\" width=\"" + width + "\">");
		
		//"None available" no click event
		boolean noneFlag = false;
		if(options.size() == 1){
			if (options.toArray()[0] instanceof CheckItem) {
				CheckItem item = (CheckItem) options.toArray()[0];
				if(item.getId() == -1){
					noneFlag = true;
				}
			} else if(options.toArray()[0] instanceof CheckItem3) {
				CheckItem3 item = (CheckItem3) options.toArray()[0];
				if("-1".equals(item.getId())){
					noneFlag = true;
				}
			}
		}
		
		if (options.size() == 0 || noneFlag) {
			results.append("<tr><td style=\"display: none;\">");
			results.append("<input type=\"checkbox\" name=\"" + name + "\" value=\"-1\" /></td>");
			results.append("<td class=\"selectListTD\">" + MgrUtil.getUserMessage("config.optionsTransfer.none")+ "</td></tr>");
		} else {
		    // sort the list value by alpha order
			sortValuesByAlpha(options);
			
			String spanWidth ="";
			if(null != itemWidth) {
				spanWidth = "style= \"width: " + itemWidth + ";\"";
			}
			
			boolean isSpecialValuesSet = (specialValues != null && !specialValues.isEmpty());
			short DeviceType = 0;
			String model=null;
			for(Object option : options) {
				Object itemKey = MgrUtil.getAttributeValue(option, listKey);
				Object textValue = MgrUtil.getAttributeValue(option, listValue);
				boolean isSpecialRow = inValues(specialValues, itemKey);
				
				results.append("<tr><td style=\"display: none;\">");
				results.append("<input type=\"checkbox\" name=\"" + name + "\" value=\"");
				if(grayOutSimilar){
					CheckItem item = (CheckItem)option;
					 DeviceType =  item.getDeviceType();
					
					 if(item.getStrModels().contains(PortBasicProfile.PORTS_SPERATOR)){
						 model = item.getStrModels().replaceAll(PortBasicProfile.PORTS_SPERATOR, "-");
					 }else{
						 model = item.getStrModels();
					 }
				}
				//boolean gray = false;
				int gray = 0;
				String spanDisplayType = "display: none;";
				if(inValues(values, itemKey)) {
					results.append(itemKey).append("\" checked/></td>");
					results.append("<td class=\"selectListTD\" ");
					results.append(grayOutSimilar?"deviceType=\""+ DeviceType +"\" ":"");
					results.append(grayOutSimilar?"deviceModels=\""+ model +"\" ":"");
					results.append("style=\"font-size: 17; background-color: ").append(DEFAULT_SELECTED_COLOR).append(";\"");
					spanDisplayType = "display: inline-block;";
				} else {
					if(grayOutSimilar){
						gray = grayOutSimilarTemplate(option,values,options);
						results.append(itemKey).append("\" /></td>");
						results.append("<td class=\"selectListTD\" style=\"font-size: 17;");
						//results.append(gray?"color:white;background-color:#B9C1C1;\"":"\"");
						results.append((gray == 1 || gray == 2)?"color:white;background-color:#B9C1C1;\"":"\"");
						results.append(" deviceType=\""+ DeviceType +"\" ");
						results.append(" deviceModels=\""+ model +"\" ");
				    }else{
					results.append(itemKey).append("\" /></td>");
					results.append("<td class=\"selectListTD\" style=\"font-size: 17;\"");
				    }
				}
				String mouseEvent = "";
				if (!isSpecialValuesSet) {
					if(null == containerId) {
						if(isMultiple) {
							if(grayOutSimilar){
								mouseEvent = isEnableMouseEvent() ? " onmousemove=\"hm.util.overRow(this, true, event);\" onmouseout=\"hm.util.outRow(this, true, event);\" " : "";
								results.append(" onclick=\""+addClickEventMethod(itemKey,textValue)+"\" " + mouseEvent + ">");
							}else{
								mouseEvent = isEnableMouseEvent() ? " onmousemove=\"hm.util.overRow(this, true, event);\" onmouseout=\"hm.util.outRow(this, true, event);\" " : "";
								results.append(" onclick=\"hm.util.selectRow(this, true);"+addClickEventMethod(itemKey,textValue)+"\" " + mouseEvent + ">");
							}
						    
						} else {
						    mouseEvent = isEnableMouseEvent() ? " onmousemove=\"hm.util.overRow(this, false, event);\" onmouseout=\"hm.util.outRow(this, false, event);\" " : "";
							results.append(" onclick=\"hm.util.selectRow(this, false);"+addClickEventMethod(itemKey,textValue)+"\" " + mouseEvent + ">");
						}
					} else {
						if(isMultiple) {
						    mouseEvent = isEnableMouseEvent() ? " onmousemove=\"hm.util.overRow(this, true, event, '"+containerId+"');\" onmouseout=\"hm.util.outRow(this, true, event, '"+containerId+"');\"" : "";
							results.append(" onclick=\"hm.util.selectRow(this, true ,'"+containerId+"');"+addClickEventMethod(itemKey,textValue)+"\" " + mouseEvent + ">");
						} else {
						    mouseEvent = isEnableMouseEvent() ? " onmousemove=\"hm.util.overRow(this, false, event, '"+containerId+"', event);\" onmouseout=\"hm.util.outRow(this, false, event, '"+containerId+"');\"" : "";
							results.append(" onclick=\"hm.util.selectRow(this, false ,'"+containerId+"');"+addClickEventMethod(itemKey,textValue)+"\" " + mouseEvent + ">");
						}
					}
				} else {
					results.append(" onclick=\"hm.util.selectSpecialRow(this, ");
					String multiTmp = isSpecialRow ? (isSpecialMultiple?"true":"false") : (isMultiple?"true":"false");
					results.append(multiTmp);
					results.append(", " + (isSpecialRow?"true":"false"));
					mouseEvent = isEnableMouseEvent() ? " onmousemove=\"hm.util.overRow(this, "+multiTmp+" , event, '"+containerId+"');\" onmouseout=\"hm.util.outRow(this, "+multiTmp+" , event, '"+containerId+"');\"" : "";
					if (null != containerId) {
						results.append(",'"+containerId+"');"+addClickEventMethod(itemKey,textValue)+"\" " + mouseEvent + ">");
					} else {
						results.append(");"+addClickEventMethod(itemKey,textValue)+"\" " + mouseEvent + ">");
					}
				}
				
				// initial the menu 
				// hide edit menu when choose network policy
				String menuString = null;
				HmUser userContext = (HmUser) MgrUtil
						.getSessionAttribute(SessionKeys.USER_CONTEXT);
				HmPermission featurePermission = userContext.getUserGroup()
						.getFeaturePermissions()
						.get(Navigation.L2_FEATURE_CONFIGURATION_TEMPLATE);
				
				String checkedIcon = "<span class=\"checkedIcon\" style=\"" + spanDisplayType + "\">&nbsp;</span>";
				
				results.append(checkedIcon);
				
				if (featurePermission.hasAccess(HmPermission.OPERATION_WRITE)) {
					menuString = initEditMenu(results, tableId, itemKey, spanDisplayType,textValue);
				}
				
				if (disableEllipsis) {
					results.append("<span id=\"" + tableId + itemKey + "Span" +"\""+">" + textValue +"</span></td>");
				} else {
					if(grayOutSimilar){
						results.append("<span id=\"" + tableId + itemKey + "Span" +"\" "+"class=\"");
						//results.append(gray?"word-wrap1\" ":"word-wrap\" ");
						results.append((gray == 2)?"word-wrap1\" ":((gray == 1)?"word-wrap2\" ":"word-wrap\" "));
						results.append("title=\""+textValue+"\" "+spanWidth +">"+textValue + "</span></td>");
					}else{
						results.append("<span id=\"" + tableId + itemKey + "Span" +"\" "+"class=\"word-wrap\" title=\""+textValue+"\" "+spanWidth +">"+textValue + "</span></td>");
					}
					
				}
				
				if (isSpecialValuesSet) {
					results.append("<td style=\"display: none;\">");
					if(isSpecialRow) {
						results.append("<input type=\"hidden\" name=\"specialRow\"/>");
					}
					results.append("</td>");
				}
				
				if (StringUtils.isNotBlank(menuString)) {
					results.append(menuString);
				}
				
				results.append("</tr>");
			}
		}
		
		results.append("</table></div>");
	}

    private int grayOutSimilarTemplate(Object option, Object values,
    		Object options) {
    	int flag = 0;
		if( values instanceof Collection && options instanceof Collection){
		//get device function and type of selected template
		CheckItem unselectItem = (CheckItem) option;
		//int flag = 0;
		for(Object value : (Collection) values){
			CheckItem selectItem = new CheckItem(Long.parseLong(value.toString()),"");
			List<CheckItem> portTemplateList = (List<CheckItem>) options;
			if(portTemplateList.contains(selectItem)){
				int index = portTemplateList.indexOf(selectItem);
				selectItem = portTemplateList.get(index);
			}
			//decide whether to gray out the background
			if(selectItem.getDeviceType() == unselectItem.getDeviceType()){
				/*
				for(String devicemodle : unselectItem.getDeviceModels()){
					int returnValue = Arrays.binarySearch(selectItem.getDeviceModels(), devicemodle);
					if(returnValue >= 0){
						++flag;
					}
				}
				*/
				if(selectItem.getStrModels().equals(unselectItem.getStrModels())){
					flag = 1;
				}else{
					for(String devicemodle : unselectItem.getDeviceModels()){
						int returnValue = Arrays.binarySearch(selectItem.getDeviceModels(), devicemodle);
						if(returnValue >= 0){
							flag = 2;
						}
					}
				}
				//===================================
			}
		}
		//return flag > 0 ? true:false;
	}else{
		//return false;
	}
		return flag;
	}

	private void sortValuesByAlpha(Collection options) {
        if(autoSort){
        	//sort by alphabetically
        	if(options.toArray()[0] instanceof CheckItem){
        		Collections.sort((List<CheckItem>)options, new Comparator<CheckItem>() {
        			@Override
        			public int compare(CheckItem o1, CheckItem o2) {
        				if(o1.getId() < 0 ){
        					return -1;
        				}
        				if(o2.getId() < 0){
        					return 1;
        				}
        				return o1.getValue().compareToIgnoreCase(o2.getValue());
        			}
        		});
        	}
        }
    }
	
    /**
     * Adjust if exist the custom event
     * @author Yunzhi Lin
     * - Time: Feb 7, 2012 5:51:20 PM
     * @return True || False
     */
    private boolean isExistCustomEvent() {
        return StringUtils.isNotBlank(cloneEvent) 
        			|| StringUtils.isNotBlank(editEvent)
        			|| StringUtils.isNotBlank(removeEvent);
    }

    /**
     * Initial the operation menu
     * @author Yunzhi Lin
     * - Time: Feb 7, 2012 4:37:41 PM
     */
    private String initEditMenu(StringBuffer results, String tableId, Object itemKey,
            String spanDisplayType,Object textValue ) {
        String menuString = "";
        if(isExistCustomEvent()) {
            // handle the invalidate value: -1
            int keyIntValue = 0;
            try {
                if(listKeyIsString){
                	if("-1".equals(itemKey.toString()) && MgrUtil.getUserMessage("config.optionsTransfer.none").equals(textValue.toString())){
                		keyIntValue = -1;
                	} else {
                		keyIntValue = 1;
                	}
                } else {
                	keyIntValue = Integer.parseInt(itemKey.toString());
                }
                
                if(keyIntValue > 0) {
                    // menu icon
                    String moreOPHTMLItem = "<span class=\"editText\" title=\"More\" style=\"" + spanDisplayType
                    + "\" onclick=\"hm.util.listMenu('" + tableId + itemKey + "Menu" + "', '" + name + "Div', event);\">&nbsp;</span>";
                    results.append(moreOPHTMLItem);
                    
                    menuString = "<td><div id=\"" + tableId + itemKey + "Menu" + "\" name=\""
                            + tableId + "Menu" + "\" class=\"dialogListMenu\" style=\"display: none; "
                            + menuContainerStyle + "\">";
                    
                    menuString += addMenuItem(itemKey, editEvent, editMenuText);
                    menuString += addMenuItem(itemKey, cloneEvent, "Clone");
                    
                    if (!StringUtils.isBlank(removeEvent)) {
                    	menuString += addMenuItem(itemKey, removeEvent, MgrUtil.getUserMessage("common.button.remove"));
                    }
                    
                    menuString += "</div></td>";
                }
            } catch (NumberFormatException e) {
                log.error("Error when parse the key to interger", e);
            }
        }
        return menuString;
    }

    /**
     * Add the menu item
     * @author Yunzhi Lin
     * - Time: Feb 7, 2012 4:39:02 PM
     */
    private String addMenuItem(Object itemKey, String event, String text) {
        String menuString = "";
        if(StringUtils.isNotBlank(event)) {
        	if(listKeyIsString){
        		itemKey = "'"+itemKey.toString()+"'";
        	}
            menuString = "<ul><li onclick=\"" + event + "(" + itemKey + ", event);\"" +
            " onmousemove=\"hm.util.mouseoverMenu(this, event);\"" +
            " onmouseout=\"hm.util.mouseoutMenu(this, event);;\"" +
            ">" + text + "</li></ul>";
        }
        return menuString;
    }
	
    private String addClickEventMethod(Object itemKey,Object textValue){
    	String result="";
    	if(StringUtils.isNotBlank(clickEvent)){
    		if(listKeyIsString){
    			result +=" "+clickEvent+"('" + itemKey + "','"+ textValue +"', event);";
    		} else {
    			result +=" "+clickEvent+"('" + itemKey + "', event);";
    		}
    		
    	}
    	return result;
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
	
	private boolean inValues(Object values, Object value) {
		if(values == null || value == null) {
			return false;
		}
		
		if(values instanceof Collection) {
			return ((Collection) values).contains(value);
		} else {
			return values.equals(value);
		}
	}

	public void release() {
		this.list = null;
	}
	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getListKey() {
		return listKey;
	}

	public void setListKey(String listKey) {
		this.listKey = listKey;
	}

	public String getListValue() {
		return listValue;
	}

	public void setListValue(String listValue) {
		this.listValue = listValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getEditEvent() {
		return editEvent;
	}

	public void setEditEvent(String editEvent) {
		this.editEvent = editEvent;
	}

	public String getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(String itemWidth) {
		this.itemWidth = itemWidth;
	}

	public boolean isDisableEllipsis() {
		return disableEllipsis;
	}

	public void setDisableEllipsis(boolean disableEllipsis) {
		this.disableEllipsis = disableEllipsis;
	}

	public String getSpecialMultiple() {
		return specialMultiple;
	}

	public void setSpecialMultiple(String specialMultiple) {
		this.specialMultiple = specialMultiple;
	}

	public String getSpecialValue() {
		return specialValue;
	}

	public void setSpecialValue(String specialValue) {
		this.specialValue = specialValue;
	}

	public String getCloneEvent() {
		return cloneEvent;
	}

	public void setCloneEvent(String cloneEvent) {
		this.cloneEvent = cloneEvent;
	}

    public boolean isEnableMouseEvent() {
        return enableMouseEvent;
    }

	public String getClickEvent() {
		return clickEvent;
	}

	public void setClickEvent(String clickEvent) {
		this.clickEvent = clickEvent;
	}

	public boolean isAutoSort() {
		return autoSort;
	}

	public void setAutoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

    public String getEditMenuText() {
        return editMenuText;
    }

    public void setEditMenuText(String editMenuText) {
        this.editMenuText = editMenuText;
    }

    public String getMenuContainerStyle() {
        return menuContainerStyle;
    }

    public void setMenuContainerStyle(String menuContainerStyle) {
        this.menuContainerStyle = menuContainerStyle;
    }

	public String getRemoveEvent() {
		return removeEvent;
	}

	public void setRemoveEvent(String removeEvent) {
		this.removeEvent = removeEvent;
	}

	public boolean isListKeyIsString() {
		return listKeyIsString;
	}

	public void setListKeyIsString(boolean listKeyIsString) {
		this.listKeyIsString = listKeyIsString;
	}

	public boolean isGrayOutSimilar() {
		return grayOutSimilar;
	}

	public void setGrayOutSimilar(boolean grayOutSimilar) {
		this.grayOutSimilar = grayOutSimilar;
	}
	
	
}
