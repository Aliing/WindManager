package com.ah.ui.actions.config;

import static com.ah.bo.network.SingleTableItem.TYPE_CLASSIFIER;
import static com.ah.bo.network.SingleTableItem.TYPE_GLOBAL;
import static com.ah.bo.network.SingleTableItem.TYPE_HIVEAPNAME;
import static com.ah.bo.network.SingleTableItem.TYPE_MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.MapNodeUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.network.SingleTableItem;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.classifiertag.DefaultTagOrderComparator;
import com.ah.util.classifiertag.type.ClassifierTagType;
import com.ah.util.values.BooleanMsgPair;

public class ClassifierTagAction extends BaseAction {

    private static final String SESSION_DEF_COMPARATOR = "ClassifierTag_Def_Comparator";

    private static final long serialVersionUID = 5871007682471809835L;
    
    private static final Tracer LOG = new Tracer(ClassifierTagAction.class.getSimpleName());
    
    private DefaultTagOrderComparator defaultComparator = null;
    
    private boolean defaultOrder;
    
    public enum ClassifierTagJSONKey {
        SUCC("succ"),
        ERRMSG("errmsg"),
        OLDINDEX("oIndex"),
        INDEX("index"),
        VALUE("value"),
        TAGVALUE("tagValue"),
        TAG1("tag1"),
        TAG2("tag2"),
        TAG3("tag3"),
        ITEMS("items");
        
        private String keyValue;
        private ClassifierTagJSONKey(String keValue) {
            this.keyValue = keValue;
        }
        @Override
        public String toString() {
            return this.keyValue;
        }
    }
    
    @Override
    public String execute() throws Exception {       
        jsonObject = new JSONObject();        
        ClassifierTagType tagType = null;
        List<SingleTableItem> items = null;
        try {
            // Error: {errmsg: 'error happen.'}
            
            if(!("tagValues".equals(operation)
                    || "deviceNames".equals(operation)
                    || "maps".equals(operation))) {
                // no need to initial the parameters when retrieve the values for tags 
            	 tagType = ClassifierTagType.getTag(tagKey);
                 items = tagType.getItems();
                defaultOrder = isDefaultOrder(items);
            }
            List<SingleTableItem> tempSubKeyItems = new ArrayList<SingleTableItem>();
			if (2 == tagKey || 3 == tagKey) {
				int subKey=vlanId;   
				tagType = ClassifierTagType.getTag(tagKey);
				items = tagType.getItems();
				for (SingleTableItem oldItem : items) {
           		 if(oldItem.getKey()==subKey)
           		   tempSubKeyItems.add(oldItem);
                }
			}
            if("add".equals(operation)) {
                // Success: {value: '123', tagValue: 'BR-100', oIndex: 0, index: 1, succ: true}
                final SingleTableItem item = createItem();
                if(null != item) {
                    // check whether there exists same tag value
                    BooleanMsgPair result = tagType.existsSameTagType(item, items);
                    if(result.getValue()) {
                        jsonObject.put(ClassifierTagJSONKey.ERRMSG.toString(), result.getDesc());
                    } else {
                        int[] indexs = addItem(items, item);
                        //update ClassifierTag at db and cache
                        if(item.getType()==SingleTableItem.TYPE_CLASSIFIER){
                        	DeviceTagUtil.getInstance().updateClassifierTag(item.getTag1(), 1, this.getDomain());
                        	DeviceTagUtil.getInstance().updateClassifierTag(item.getTag2(), 2, this.getDomain());
                        	DeviceTagUtil.getInstance().updateClassifierTag(item.getTag3(), 3, this.getDomain());
                        }
                        
                        if(item.getType()==SingleTableItem.TYPE_NONE){
                        	jsonObject.put(ClassifierTagJSONKey.VALUE.toString(), item.getMacRangeFrom()+"|"+item.getMacRangeTo());
                        }
                        else
                        jsonObject.put(ClassifierTagJSONKey.VALUE.toString(), tagType.getValue(item));
                        jsonObject.put(ClassifierTagJSONKey.TAGVALUE.toString(), item.getTagValue(domainId));
                        jsonObject.put(ClassifierTagJSONKey.OLDINDEX.toString(), indexs[0]);
                        jsonObject.put(ClassifierTagJSONKey.INDEX.toString(), indexs[1]);
                        jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
                    }
                } else {
                    jsonObject.put(ClassifierTagJSONKey.ERRMSG.toString(), "Unable the add a new item.");
                }
            } else if("remove".equals(operation)) {
                if(StringUtils.isNotBlank(selectedItems)) {
                    try {                    
                    	               	
                        String[] sItems = getItems();
                        for (int i = 0; i < sItems.length; i++) {
                            if (NumberUtils.isNumber(sItems[i])) {
                                int index = Integer.parseInt(sItems[i]);
                                if (items.size() >= index + 1) {
                                	if(2 == tagKey || 3 == tagKey){
                                		if(tempSubKeyItems.size()>=index + 1)
                                		 items.remove(tempSubKeyItems.get(index));
                                	}
                                	else
                                		items.remove(index);
                                }
                            }
                        }
                        jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
                    } catch (Exception e) {
                        LOG.error("Error when remove the selected items", e);
                    }
                }
            }
            else if("removeAllItem".equals(operation)) {
            	items.clear();
            	jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), false);
            }
            
            else if ("edit".equals(operation)) {
            	if(StringUtils.isNotBlank(selectedItems)) {
                    try {                       
                        if (NumberUtils.isNumber(selectedItems)){                        	
                        int index = Integer.parseInt(selectedItems);
                        SingleTableItem tempItem=createItem();
                        tempItem.setType(items.get(index).getType());
                        setClassifierValue(tempItem);
                        List<SingleTableItem> totalItems = new ArrayList<SingleTableItem>();
                        /**for (SingleTableItem oldItem : items) {
                        	totalItems.add(oldItem);
                        }
                        totalItems.remove(items.get(index));**/
                        for(int i=0;i<items.size();i++){
                        	if(i != index){
                        		SingleTableItem oldItem = items.get(i);
                        		totalItems.add(oldItem);
                        	}
                        }
                        BooleanMsgPair result;
                        if(2 == tagKey || 3 == tagKey){
                        	 List<SingleTableItem> totalSubkeyItems = new ArrayList<SingleTableItem>();
                        	 for (SingleTableItem oldItem : tempSubKeyItems) {
                        		 totalSubkeyItems.add(oldItem);
                             }
                        	 totalSubkeyItems.remove(index);
                        	result = tagType.existsSameTagType(tempItem, totalSubkeyItems);
                        }
                        else
                   	 	  result = tagType.existsSameTagType(tempItem, totalItems);
                        if(result.getValue()) {
                            jsonObject.put(ClassifierTagJSONKey.ERRMSG.toString(), result.getDesc());
                        } else{  
                        	 String dispaly="";
                        	 if(2 == tagKey || 3 == tagKey){
                        		 setClassifierValue(tempSubKeyItems.get(index));
                        		 dispaly= tempSubKeyItems.get(index).getTagValue(domainId);
                        	 }else
                        	 {
                        		 setClassifierValue(items.get(index));
                        		 dispaly= items.get(index).getTagValue(domainId);                        		 
                             }
                        	 jsonObject.put(ClassifierTagJSONKey.VALUE.toString(),dispaly);
                    		 jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
                        }
                       }                                               
                    } catch (Exception e) {
                            LOG.error("Error when remove the selected items", e);
                        }
                    }  
			}
            //just return VALUE and TAGVALUE to client side
            else if ("edit1".equals(operation)) {
				if (StringUtils.isNotBlank(selectedItems)) {
					try {						
						if (NumberUtils.isNumber(selectedItems)) {
							int index = Integer.parseInt(selectedItems);
							SingleTableItem tempOldTableItem ;
							 if((2 == tagKey || 3 == tagKey)&&tempSubKeyItems.size()>=index + 1){								 
									 tempOldTableItem=tempSubKeyItems.get(index);
							 }else
								 tempOldTableItem = items.get(index);
							jsonObject.put(
									ClassifierTagJSONKey.VALUE.toString(),
									tempOldTableItem.getType());
							jsonObject.put(
									ClassifierTagJSONKey.TAGVALUE.toString(),
									tempOldTableItem.getTagValue(domainId));
							if(tempOldTableItem.getType()==SingleTableItem.TYPE_CLASSIFIER){
								jsonObject.put(ClassifierTagJSONKey.TAG1.toString(),handle(tempOldTableItem.getTag1()));
								jsonObject.put(ClassifierTagJSONKey.TAG2.toString(),handle(tempOldTableItem.getTag2()));
								jsonObject.put(ClassifierTagJSONKey.TAG3.toString(),handle(tempOldTableItem.getTag3()));								
							}
						}
					} catch (Exception e) {
						LOG.error("Error when remove the selected items", e);
					}
					jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
				}
			}
            else if("match".equals(operation)) {
                 if(StringUtils.isNotBlank(selectedItems)) {
                     try {
                         String[] sItems = getItems();                         
                         for (int i = 0; i < sItems.length; i++) {
                             if (NumberUtils.isNumber(sItems[i])) {
                                 int index = Integer.parseInt(sItems[i]);
                                 //if (items.size() > index + 1) {
                                 if (items.size() > index) {                                	 
                                	SingleTableItem tempItem = items.get(index);  
//                                		List<String> returnList=findMatchedDevice(tempItem);
                                		List<String> returnList = findMachtedTemplateDevice(tempItem);
                                		java.util.Collections.sort(returnList);
                                		jsonObject.put(ClassifierTagJSONKey.ITEMS.toString(),returnList.toString());
                                 }
                             }
                         }
                         jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
                     } catch (Exception e) {
                         LOG.error("Error when remove the selected items", e);
                     }
                 }             
            }
            else if ("view".equals(operation)) {
				if (StringUtils.isNotBlank(selectedItems)) {
					try {
						String[] sItems = getItems();
						for (int i = 0; i < sItems.length; i++) {
							if (NumberUtils.isNumber(sItems[i])) {
								int index = Integer.parseInt(sItems[i]);
								//if (items.size() > index + 1) {
								  if (items.size() > index) {
									Set<String> tempSet=findConfilctRule(index,items);
									jsonObject.put(ClassifierTagJSONKey.ITEMS.toString(), tempSet.toString());	
								}
							}
						}
						jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
					} catch (Exception e) {
						LOG.error("Error when remove the selected items", e);
					}
				}
			}   
            else if ("viewAll".equals(operation)) {
				try {
					Set<String> tempSet = findAllConfilctRule(items);
					jsonObject.put(ClassifierTagJSONKey.ITEMS.toString(),tempSet.toString());
				}
				catch (Exception e) {
					LOG.error("Error when handle viewaAll", e);
				}
				jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
			}   
            else if("changeOrder".equals(operation)) {
                if(prevIndex != curIndex) {
                    defaultOrder = false;
                    items.add(curIndex, items.remove(prevIndex));
                }
                jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
            } else if("resetOrder".equals(operation)) {
                //Should only update the position for item but not replace all HTML
                // Success: {items: [{id: 1, value: '12', type: 1, tagValue: 'BR-100', desc: 'description' , global: true}], 
                //          succ: true}
                defaultOrder = true;
                //================================================================
                JSONArray array = new JSONArray();
                if(0 == this.configTemplateId){
	                sortItemsOrder(items);
	                
	//                JSONArray array = new JSONArray();
	                int index = 0;
	                for (SingleTableItem item : items) {
                        JSONObject json = buildJSONTag(index++,
                                tagType.getValue(item), item.getType(),
                                item.getTagValue(domainId),
                                item.getConfigTemplateId(),
                                item.getDescription());
	                    if(item.getType() == TYPE_GLOBAL) {
	                        json.put("global", true);
	                    }
	                    array.put(json);
	                }
                }else{
		                List<SingleTableItem> orderItems = new ArrayList<SingleTableItem>();
		                List<SingleTableItem> otherItems = new ArrayList<SingleTableItem>();
		                for (SingleTableItem item : items) {
		                	if(item.getConfigTemplateId() == this.configTemplateId){
		                		orderItems.add(item);
	                	}else{
	                		otherItems.add(item);
	                	}
	                }
	                                
	                sortItemsOrder(orderItems);
	                
	                items.clear();
	               
	                
	//                JSONArray array = new JSONArray();
	                int index = 0;
	                for (SingleTableItem item : orderItems) {
                        JSONObject json = buildJSONTag(index++,
                                tagType.getValue(item), item.getType(),
                                item.getTagValue(domainId),
                                item.getConfigTemplateId(),
                                item.getDescription());
                        if(item.getType() == TYPE_GLOBAL) {
                            json.put("global", true);
                        }
	                    array.put(json);
	                    items.add(item);
	                }
	                for (SingleTableItem item : otherItems) {
                        JSONObject json = buildJSONTag(index++,
                                tagType.getValue(item), item.getType(),
                                item.getTagValue(domainId),
                                item.getConfigTemplateId(),
                                item.getDescription());
                        if(item.getType() == TYPE_GLOBAL) {
                            json.put("global", true);
                        }
	                    array.put(json);
	                    items.add(item);
	                }
                }
                //================================================================
                
                jsonObject.put(ClassifierTagJSONKey.ITEMS.toString(), array);
                jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
            } else if("tagValues".equals(operation)) {                
                try {
                	Map<String, Set<String>> cacheMap = DeviceTagUtil.getInstance().getExistedClassifierTag(this.getDomainId());
                    Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
                    JSONObject value = new JSONObject();
                    //get exist tag   
                    value.put(ClassifierTagJSONKey.TAG1.toString(), cacheMap.get(DeviceTagUtil.TAG1_TYPE).toArray(new String[0]));
                    value.put(ClassifierTagJSONKey.TAG2.toString(), cacheMap.get(DeviceTagUtil.TAG2_TYPE).toArray(new String[0]));
                    value.put(ClassifierTagJSONKey.TAG3.toString(), cacheMap.get(DeviceTagUtil.TAG3_TYPE).toArray(new String[0]));
                    //get custom device tag
                    value.put(DeviceTagUtil.CUSTOM_TAG1, cusMap.get(DeviceTagUtil.CUSTOM_TAG1));
                    value.put(DeviceTagUtil.CUSTOM_TAG2, cusMap.get(DeviceTagUtil.CUSTOM_TAG2));
                    value.put(DeviceTagUtil.CUSTOM_TAG3, cusMap.get(DeviceTagUtil.CUSTOM_TAG3));
                    jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
                    jsonObject.put(ClassifierTagJSONKey.VALUE.toString(), value);
                } catch (Exception e) {
                    LOG.error("Error when init the device tags", e);
                }
                
            } else if("deviceNames".equals(operation)) {
                List<String> deviceNames = new ArrayList<>();
                try {
                    List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getAllApList(domainId);
                    // Add variable "deviveNameType" to fix bug 24935
                   matchDeviceTypeWithTemplate(simpleDevice,deviceNames);
                    //end of fixing bug 24935
                   /* for (SimpleHiveAp sDevice : simpleDevice) {
                    		deviceNames.add(sDevice.getHostname());
                    }*/
                } catch (Exception e) {
                    LOG.error("Error when init the device names", e);
                }
                jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
                jsonObject.put(ClassifierTagJSONKey.VALUE.toString(), deviceNames.toArray(new String[deviceNames.size()]));
                
            } else if("maps".equals(operation)) {
                // Success: [{id: 1, mapName: 'K123', indent: 0}]
                try {
                    MapContainerNode rootMap = BoMgmt.getMapMgmt().getVHMRootMap(
                            QueryUtil.getDependentDomainFilter(userContext));
                    
                    JSONArray array = new JSONArray();
                    array.put(buildJSONMapNode(rootMap.getId(), rootMap.getLabel(), 0));
                    buildTopologyMap(array, rootMap, 0, domainId, false, null, false, false);
                    
                    jsonObject.put(ClassifierTagJSONKey.VALUE.toString(), array);
                    jsonObject.put(ClassifierTagJSONKey.SUCC.toString(), true);
                } catch (Exception e) {
                    LOG.error("Error when init the maps.",e);
                }
            } else {
                
            }
        } catch (Exception e) {
            LOG.error("ClassifierTag operation:"+operation, e);
        }
        return "json";
    }

    /**
     * Correct the delimiter "|" for split (Bug 29608)
     * 
     * @author Yunzhi Lin
     * - Time: Nov 11, 2013 4:40:13 PM
     * @return String array
     */
    private String[] getItems() {
        return selectedItems.split("\\|");
    }

	private void matchDeviceTypeWithTemplate(List<SimpleHiveAp> simpleDevice , List<String> deviceNames) {
		if (MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER) != null) {
			if (DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_VALUE.equals(MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER))) {
				for (SimpleHiveAp oneAp : simpleDevice) {
					if (MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_TYPE_VALUE).equals(oneAp.getDeviceType())
							&& MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_MODEL_VALUE).equals(String.valueOf(oneAp.getHiveApModel()))) {
						deviceNames.add(oneAp.getHostname());
					}
				}
			} else {
				for (SimpleHiveAp oneAp : simpleDevice) {
					deviceNames.add(oneAp.getHostname());
				}
			}
		} else {
			for (SimpleHiveAp oneAp : simpleDevice) {
				deviceNames.add(oneAp.getHostname());
			}
		}
	}
	private void setClassifierValue(SingleTableItem singleTableItem) {		 
		switch (singleTableItem.getType()) {
		  case TYPE_MAP:
		  MapContainerNode location = QueryUtil.findBoById(MapContainerNode.class, locationId);
		  singleTableItem.setLocation(location);  
		  break;
		case TYPE_HIVEAPNAME:
		 singleTableItem.setTypeName(typeName);
		  break;
		case TYPE_CLASSIFIER:
		  if (StringUtils.isNotBlank(tag1)) {
			  singleTableItem.setTag1Checked(true);
			  singleTableItem.setTag1(tag1);
		  } else {
			  singleTableItem.setTag1Checked(false);
			  singleTableItem.setTag1("");
		  }
		  if (StringUtils.isNotBlank(tag2)) {
			  singleTableItem.setTag2Checked(true);
			  singleTableItem.setTag2(tag2);
		  } else {
			  singleTableItem.setTag2Checked(false);
			  singleTableItem.setTag2("");
		  }
		  if (StringUtils.isNotBlank(tag3)) {
			  singleTableItem.setTag3Checked(true);
			  singleTableItem.setTag3(tag3);
		  } else {
			  singleTableItem.setTag3Checked(false);
			  singleTableItem.setTag3("");
		  }
		  break;
		}
	}
    
    private Set<String> findAllConfilctRule(List<SingleTableItem> items) throws JSONException {
    	Set<String> returnSet = new TempSetForJson();
    	if(null != items){
    		int size=items.size();
        	for(int i=0;i<size;i++){
        		Set<String> set=findConfilctRule(i,items);
        		if(!set.isEmpty())returnSet.addAll(set); 		
        	}
    	}
    	return returnSet;
    }

	private Set<String> findConfilctRule(int index,List<SingleTableItem> items) throws JSONException {
		SingleTableItem tgtTableItem = items.get(index);
		List<String> tagDeviceList=findMatchedDevice(tgtTableItem);
		int tagSize=tagDeviceList.size();
		Set<String> tempSet = new TempSetForJson();		
		Set<String> testSet=new HashSet<String>();		
		for (int i = 0; i < items.size(); i++) {
			if (i == index)
				continue;
			SingleTableItem comparecTableItem = items.get(i);
			List<String> compareDeviceList=findMatchedDevice(comparecTableItem);
			int compareSize=compareDeviceList.size();
			testSet.addAll(tagDeviceList);
			testSet.addAll(compareDeviceList);
			if(testSet.size()!=(compareSize+tagSize)){
				tempSet.add(""+i);
				tempSet.add(""+index);
			}
			testSet.clear();
		}
		return tempSet;			
	}

	

	private String handle(String source) {
		source= source.trim();
		if(source==null||source.equals(""))return "";
		return source;
	}

    private JSONObject buildJSONTag(int id, String value, short type,
            String tagValue, long templateId, String desc) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id).put("value", value).put("type", type)
                .put("tagValue", tagValue).put("configTemplateId", templateId)
                .put("desc", desc);
        return json;
    }
	private JSONObject buildJSONMapNode(long mapId, String mapName, int level) throws JSONException {
	    JSONObject json = new JSONObject();
	    json.put("id", mapId).put("mapName", mapName).put("indent", level);
	    return json;
	}
	

//	private boolean isConfilictMap(MapContainerNode srcTopo,MapContainerNode tgtTopo) {		
//		if(itrate(srcTopo,tgtTopo))return true;
//		if(itrate(tgtTopo,srcTopo))return true;
//		return false;
//	}
//
//	private boolean itrate(MapContainerNode srcTopo, MapContainerNode tgtTopo) {
//		if(srcTopo.getId()==tgtTopo.getId())return true;
//		MapContainerNode nextTopo=srcTopo.getParentMap();
//		if(nextTopo==null)return false;
//		else{ 
//			if(nextTopo.getId()==tgtTopo.getId()){
//				return true;
//			}
//			return itrate(nextTopo,tgtTopo);
//		}		
//	}
	private List<String> findMachtedTemplateDevice(SingleTableItem tempItem) throws JSONException{
		List<String> tempList = new ArrayList<String>(){
			
			private static final long serialVersionUID = 6436833333930030423L;

			public String toString(){	
				String result= super.toString();
				result=result.replaceAll(",", "|");
				result=result.substring(1);
				result=result.substring(0,result.length()-1);
				return result;
			}
		};
		if(tempItem.getType()==SingleTableItem.TYPE_HIVEAPNAME){
			String deviceName=tempItem.getTypeName();
			if (deviceName != null) {
				String hostNameRule = deviceName;
				hostNameRule = hostNameRule.replace("*", ".*");
				hostNameRule = "^" + hostNameRule + "$";
				List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getAllApList(domainId);
				Pattern pattern = Pattern.compile(hostNameRule);
				for (SimpleHiveAp sDevice : filterDeviceWhenMatchingTags(simpleDevice)) {
//					for (SimpleHiveAp sDevice : simpleDevice) {		
					Matcher matcher = pattern.matcher(sDevice.getHostname());
					if (matcher.matches()) {
						tempList.add(sDevice.getHostname());
					}
				}
			}
		}
		if (tempItem.getType() == SingleTableItem.TYPE_MAP) {
			MapContainerNode topoCntnerNode = tempItem.getLocation();	
			if (topoCntnerNode != null) {
				List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getApListByMapContainer(topoCntnerNode.getId(),domainId);
				for (SimpleHiveAp sDevice : filterDeviceWhenMatchingTags(simpleDevice)) {
//					for (SimpleHiveAp sDevice : simpleDevice) {
					tempList.add(sDevice.getHostname());
				}
			}
		}
		if (tempItem.getType() == SingleTableItem.TYPE_CLASSIFIER) {
			String tag1 = tempItem.getTag1();
			String tag2 = tempItem.getTag2();
			String tag3 = tempItem.getTag3();
			List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getAllApList(domainId);
			 for (SimpleHiveAp sDevice : filterDeviceWhenMatchingTags(simpleDevice)) {
//			for (SimpleHiveAp sDevice : simpleDevice) {
				 if(isEqual(sDevice.getTag1(),tag1)&&isEqual(sDevice.getTag2(),tag2)&&isEqual(sDevice.getTag3(),tag3)){
					 tempList.add(sDevice.getHostname());
				 }				 
			 } 
		}	
		return tempList;
	}
	private List<String> findMatchedDevice(SingleTableItem tempItem) throws JSONException {		
		List<String> tempList = new ArrayList<String>(){			
			private static final long serialVersionUID = 6436833333930030423L;
			public String toString(){	
				String result= super.toString();
				result=result.replaceAll(",", "|");
				result=result.substring(1);
				result=result.substring(0,result.length()-1);
				return result;		    
			}
		};
		if(tempItem.getType()==SingleTableItem.TYPE_HIVEAPNAME){
			String deviceName=tempItem.getTypeName();
			if (deviceName != null) {
				String hostNameRule = deviceName;
				hostNameRule = hostNameRule.replace("*", ".*");
				hostNameRule = "^" + hostNameRule + "$";
				List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getAllApList(domainId);
				Pattern pattern = Pattern.compile(hostNameRule);
//				for (SimpleHiveAp sDevice : filterDeviceWhenMatchingTags(simpleDevice)) {
				for (SimpleHiveAp sDevice : simpleDevice) {		
					Matcher matcher = pattern.matcher(sDevice.getHostname());
					if (matcher.matches()) {
						tempList.add(sDevice.getHostname());
					}
				}
			}
		}
		if (tempItem.getType() == SingleTableItem.TYPE_MAP) {
			MapContainerNode topoCntnerNode = tempItem.getLocation();	
			if (topoCntnerNode != null) {
				List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getApListByMapContainer(topoCntnerNode.getId(),domainId);
//				for (SimpleHiveAp sDevice : filterDeviceWhenMatchingTags(simpleDevice)) {
				for (SimpleHiveAp sDevice : simpleDevice) {
					tempList.add(sDevice.getHostname());
				}
			}
		}
		if (tempItem.getType() == SingleTableItem.TYPE_CLASSIFIER) {
			String tag1 = tempItem.getTag1();
			String tag2 = tempItem.getTag2();
			String tag3 = tempItem.getTag3();
			List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getAllApList(domainId);
//			for (SimpleHiveAp sDevice : filterDeviceWhenMatchingTags(simpleDevice)) {
			for (SimpleHiveAp sDevice : simpleDevice) {
				 if(isEqual(sDevice.getTag1(),tag1)&&isEqual(sDevice.getTag2(),tag2)&&isEqual(sDevice.getTag3(),tag3)){
					 tempList.add(sDevice.getHostname());
				 }				 
			 } 
		}	
		return tempList;
	}
	public List<SimpleHiveAp> filterDeviceWhenMatchingTags(List<SimpleHiveAp> simpleDevice){
		List<SimpleHiveAp> filtered = new ArrayList<SimpleHiveAp>();
		if (MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER) != null) {
			if (DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_VALUE.equals(MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER))) {
				for (SimpleHiveAp oneAp : simpleDevice) {
					if (MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_TYPE_VALUE).equals(oneAp.getDeviceType())
						&& MgrUtil.getSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_MODEL_VALUE).equals(String.valueOf(oneAp.getHiveApModel()))) {
						filtered.add(oneAp);
					}
				}
			} else {
				for (SimpleHiveAp oneAp : simpleDevice) {
					filtered.add(oneAp);
				}
			}
		} else {
			for (SimpleHiveAp oneAp : simpleDevice) {
				filtered.add(oneAp);
			}
		}
		return filtered;
	}
	 private boolean isEqual(String apTag, String ruleTag) {
			if(apTag==null||apTag.equals("")){
				if (ruleTag != null) {
					if (!ruleTag.equals(""))
						return false;
					else
						return true;
				}
				else return true;
			}
			if(ruleTag==null||ruleTag.equals("")){
				return true;	
			}		
			String ruleTagStr =  ruleTag ;
			ruleTagStr = ruleTagStr.replace("*", ".*");
			ruleTagStr = "^" + ruleTagStr + "$";
			Pattern pattern = Pattern.compile(ruleTagStr);
			Matcher matcher = pattern.matcher(apTag);
			return matcher.matches();
		}

	@Override
    public void removeSessionAttributes() {
        super.removeSessionAttributes();
        MgrUtil.removeSessionAttribute(SESSION_DEF_COMPARATOR);
        MgrUtil.removeSessionAttribute(DeviceTagUtil.SESSION_MARK_AVAILABLE_DEVICE_TEMPLATE_FILTER);
		MgrUtil.removeSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_TYPE_VALUE);
		MgrUtil.removeSessionAttribute(DeviceTagUtil.SESSION_MARK_DEVICE_TEMPLATE_FILTER_MODEL_VALUE);
    }

    private int[] addItem(List<SingleTableItem> items, final SingleTableItem item) {
        items.add(item);
        return new int[]{getIndexOfItem(items,item), getNewIndexOfItem(items,item)};
    }
    
    private int getIndexOfItem(List<SingleTableItem> items, final SingleTableItem item) {
        return item.getType() == TYPE_GLOBAL ? -1 :items.indexOf(item);
    }
    
    private int getNewIndexOfItem(List<SingleTableItem> items, final SingleTableItem item) {
    	defaultOrder=true;
    	ArrayList<SingleTableItem> list=new ArrayList<SingleTableItem>(items);
        sortItemsOrder(list);
        int newIndex=getIndexOfItem(list, item);
        if(newIndex!=-1){
        	items.remove(items.size()-1);
        	items.add(newIndex, item);
        }        
        return newIndex;
    }

    private void sortItemsOrder(List<SingleTableItem> items) {
        if(defaultOrder) {
            Collections.sort(items, defaultComparator);
            LOG.debug("sortItemsOrder", "sort as default system order");
        }
    }
    
    private SingleTableItem createItem() {
        
        SingleTableItem oneItem = new SingleTableItem();
        
        // handle the value fields
        oneItem.setVlanId(vlanId);
        oneItem.setAttributeValue(StringEscapeUtils.escapeHtml4(attributeValue));
        oneItem.setMacEntry(StringEscapeUtils.escapeHtml4(macEntry));
        oneItem.setIpAddress(StringEscapeUtils.escapeHtml4(ipAddress));
        oneItem.setNetmask(StringEscapeUtils.escapeHtml4(netmask));
        oneItem.setMacRangeFrom(StringEscapeUtils.escapeHtml4(macRangeFrom));
        oneItem.setMacRangeTo(StringEscapeUtils.escapeHtml4(macRangeTo));
        oneItem.setOperatorName(StringEscapeUtils.escapeHtml4(operatorName));
        oneItem.setNameSpaceId(nameSpaceId);
        if (ipAddress == null) {
			oneItem.setIpAddress(StringEscapeUtils.escapeHtml4(ipReserveClassIp));
		}
        oneItem.setDescription(StringEscapeUtils.escapeHtml4(description));
        if (MgrUtil.getSessionAttribute("ConfigTemplateSource") != null)
        	oneItem.setConfigTemplateId(((ConfigTemplate)MgrUtil.getSessionAttribute("ConfigTemplateSource")).getId());
        oneItem.setNonGlobalId(nonGlobalId);
        
        oneItem.setTag1Checked(false);
        oneItem.setTag2Checked(false);
        oneItem.setTag3Checked(false);
        oneItem.setType(type);
        
        typeName = StringEscapeUtils.escapeHtml4(typeName); 
        switch (type) {
        case TYPE_MAP:
            if (locationId > -1) {
                MapContainerNode location = QueryUtil.findBoById(MapContainerNode.class, locationId);
                oneItem.setLocation(location);
            }
            oneItem.setTypeName("");
            oneItem.setTag1("");
            oneItem.setTag2("");
            oneItem.setTag3("");
            break;
        case TYPE_HIVEAPNAME:
            oneItem.setLocation(null);
            oneItem.setTypeName(typeName);
            oneItem.setTag1("");
            oneItem.setTag2("");
            oneItem.setTag3("");
            break;
        case TYPE_CLASSIFIER:
            oneItem.setLocation(null);
            oneItem.setTypeName("");
            if (StringUtils.isNotBlank(StringEscapeUtils.escapeHtml4(tag1))) {
                oneItem.setTag1Checked(true);
                oneItem.setTag1(tag1);
            } else {
                oneItem.setTag1("");
            }
            if (StringUtils.isNotBlank(StringEscapeUtils.escapeHtml4(tag2))) {
                oneItem.setTag2Checked(true);
                oneItem.setTag2(tag2);
            } else {
                oneItem.setTag2("");
            }
            if (StringUtils.isNotBlank(StringEscapeUtils.escapeHtml4(tag3))) {
                oneItem.setTag3Checked(true);
                oneItem.setTag3(tag3);
            } else {
                oneItem.setTag3("");
            }
            break;
        default:
            oneItem.setLocation(null);
            oneItem.setTypeName("");
            oneItem.setTag1("");
            oneItem.setTag2("");
            oneItem.setTag3("");
            break;
        }
        
        return oneItem;
    }
    
    private boolean isDefaultOrder(List<SingleTableItem> items) {
    	if(items==null||items.size()==0)return true;
        initDefComparator();
        
        final int size = items.size();
        for (int i = 0, j = i+1; i < size-1 && j < size; i++, j++) {
            if(defaultComparator.compare(items.get(i), items.get(j)) > 0) {
                LOG.warn(
                        "isDefaultOrder",
                        "False, i=" + i + " " + ReflectionToStringBuilder.toString(items.get(i))
                                + ", j=" + j + " "
                                + ReflectionToStringBuilder.toString(items.get(j)));
                return false;
            }
        }
        return true;
    }

    private void initDefComparator() {
        if(null == MgrUtil.getSessionAttribute(SESSION_DEF_COMPARATOR)) {
            defaultComparator = new DefaultTagOrderComparator();
//            defaultComparator.initDevicesTag(getDomainId());
            MgrUtil.setSessionAttribute(SESSION_DEF_COMPARATOR, defaultComparator);
            LOG.debug("isDefaultOrder", "init the classifier tag");
        } else {
            defaultComparator = (DefaultTagOrderComparator) MgrUtil.getSessionAttribute(SESSION_DEF_COMPARATOR);
        }
    }
    
    private void buildTopologyMap(JSONArray array, final MapContainerNode currentNode, int level, Long domainId,
            final boolean orderFolders, Collection<Long> includeOnly,
            boolean containersOnly, boolean allNode) throws JSONException {
        if(null == currentNode) {
            return;
        }
        // order by id
        Set<MapNode> children = currentNode.getChildNodes();
		List<MapNode> list =  MapNodeUtil.sortMapTree(children, orderFolders, currentNode,true);
        level++;
        for (MapNode mapNode : list) {
            if (mapNode instanceof MapContainerNode) {
                if (mapNode.getOwner().getId().equals(domainId)) {
                    if (includeOnly == null
                            || includeOnly.contains(mapNode.getId())) {
                        JSONObject jsonObj;
                        if (allNode) {
                            jsonObj = buildJSONMapNode(mapNode.getId(), mapNode.getLabel(), level);
                        } else {
                            if (((MapContainerNode) mapNode).getMapType() == 1) {
                                jsonObj = buildJSONMapNode(mapNode.getId(), mapNode.getLabel(), level);
                            } else {
                                jsonObj = buildJSONMapNode(mapNode.getId(), currentNode.getLabel() + "_" + mapNode.getLabel(), level-1);
                            }
                        }
                        
                        if (MapMgmt.VHM_ROOT_MAP_NAME.equals(mapNode.getLabel())) {
                            continue;
                        }
                        if (allNode) {
                            array.put(jsonObj);
                        } else {
                            if (containersOnly) {
                                if (((MapContainerNode) mapNode).getMapType() == 1) {
                                    array.put(jsonObj);
                                }
                            } else {
                                if (((MapContainerNode) mapNode).getMapType() != MapContainerNode.MAP_TYPE_BUILDING) {
                                    array.put(jsonObj);
                                }
                            }
                        }
                    }
                    buildTopologyMap(array, (MapContainerNode) mapNode, level,
                            domainId, orderFolders, includeOnly,
                            containersOnly, allNode);
                }
            }
        }
    }
    
    /*------------Parameters in UI--------------*/
    private int tagKey;
    
    private String selectedItems;
    
    // For change order
    private int prevIndex;
    private int curIndex;
    
    // --------------- Tag Item fields---------------
    // For item values
    private int vlanId;
    private String attributeValue;
    private String macEntry;
    private String  ipAddress;
    private String ipReserveClassIp;
    private String  netmask;
    private String macRangeFrom; 
    private String macRangeTo; 
    private String operatorName;
    private short nameSpaceId;
    // For tag type
    private short type;
    // For tag value
    private String tag1;
    private String tag2;
    private String tag3;
    private String typeName;
    private long locationId;
    // For description
    private String description;
    private long configTemplateId;
    private long nonGlobalId;
    
    /*------------Getter/Setter--------------*/
    public int getTagKey() {
        return tagKey;
    }
    
    public long getConfigTemplateId() {
		return configTemplateId;
	}
	public void setConfigTemplateId(long configTemplateId) {
		this.configTemplateId = configTemplateId;
	}
	public long getNonGlobalId() {
		return nonGlobalId;
	}
	public void setNonGlobalId(long nonGlobalId) {
		this.nonGlobalId = nonGlobalId;
	}
	public int getPrevIndex() {
        return prevIndex;
    }
    
    public int getCurIndex() {
        return curIndex;
    }
    
    public void setTagKey(int tagKey) {
        this.tagKey = tagKey;
    }
    
    public void setPrevIndex(int prevIndex) {
        this.prevIndex = prevIndex;
    }
    
    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
    }

    public short getType() {
        return type;
    }

    public String getTag1() {
        return tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public String getTypeName() {
        return typeName;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setType(short type) {
        this.type = type;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVlanId() {
        return vlanId;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public String getMacEntry() {
        return macEntry;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public String getMacRangeFrom() {
        return macRangeFrom;
    }

    public String getMacRangeTo() {
        return macRangeTo;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public short getNameSpaceId() {
        return nameSpaceId;
    }

    public void setVlanId(int vlanId) {
        this.vlanId = vlanId;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public void setMacEntry(String macEntry) {
        this.macEntry = macEntry;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public void setMacRangeFrom(String macRangeFrom) {
        this.macRangeFrom = macRangeFrom;
    }

    public void setMacRangeTo(String macRangeTo) {
        this.macRangeTo = macRangeTo;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public void setNameSpaceId(short nameSpaceId) {
        this.nameSpaceId = nameSpaceId;
    }

    public boolean isDefaultOrder() {
        return defaultOrder;
    }

    public void setDefaultOrder(boolean defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    public String getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(String selectedItems) {
        this.selectedItems = selectedItems;
    }

	public String getIpReserveClassIp() {
		return ipReserveClassIp;
	}

	public void setIpReserveClassIp(String ipReserveClassIp) {
		this.ipReserveClassIp = ipReserveClassIp;
	}
    

}

class TempSetForJson extends HashSet<String>{
	private static final long serialVersionUID = 6436833333930030423L;
	public String toString(){	
		String result= super.toString();
		result=result.replaceAll(",", "|");
		result=result.replaceAll(" ", "");
		result=result.substring(1);
		result=result.substring(0,result.length()-1);
		return result;		    
	}
}