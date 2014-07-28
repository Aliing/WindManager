package com.ah.util.classifiertag;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.network.SingleTableItem;

public class DefaultTagOrderComparator implements Comparator<SingleTableItem> {
    
    private static final String WILDCARD_SUFFIX = "*";
    private static final int INSERT_AFTERT = 1;
    private static final int INSERT_BEFORE = -1;
//    private static final int INSERT_EQUAL = 0;
//    private List<String> deviceTag1List = new ArrayList<>();
//    private List<String> deviceTag2List = new ArrayList<>();
//    private List<String> deviceTag3List = new ArrayList<>();
    
//    public void initDevicesTag(Long domainId) {
//        // TODO Auto-generated method stub
//        try {
//            List<SimpleHiveAp> simpleDevice = CacheMgmt.getInstance().getAllApList(domainId);
//            for (SimpleHiveAp sDevice : simpleDevice) {
//                deviceTag1List.add(sDevice.getTag1());
//                deviceTag2List.add(sDevice.getTag2());
//                deviceTag3List.add(sDevice.getTag3());
//            }
//        } catch (Exception e) {            
//            e.printStackTrace();
//        }
//    }

    @Override
    public int compare(SingleTableItem insertItem, SingleTableItem comparedItem) {
        // TODO Auto-generated method stub
        if(comparedItem.getType() == SingleTableItem.TYPE_GLOBAL) {
            return INSERT_BEFORE;
        }
        
        if(comparedItem.getType() == SingleTableItem.TYPE_MAP) {
        	if(insertItem.getType()!=SingleTableItem.TYPE_GLOBAL)
            return INSERT_BEFORE;
        }
        
        if(comparedItem.getType() == SingleTableItem.TYPE_HIVEAPNAME){
        	if(insertItem.getType()==SingleTableItem.TYPE_CLASSIFIER)  
        		return INSERT_BEFORE;
        	//both device name
        	if(insertItem.getType()==SingleTableItem.TYPE_HIVEAPNAME){
        		  final String iHostName = insertItem.getTypeName();
                  final String cHostName = comparedItem.getTypeName();
//                  if(StringUtils.isNotBlank(iHostName)
//                          && StringUtils.isNotBlank(cHostName)
//                          && !iHostName.equals(cHostName)
//                          && iHostName.endsWith(WILDCARD_SUFFIX)
//                          && cHostName.endsWith(WILDCARD_SUFFIX)
//                          && iHostName.startsWith(cHostName.substring(0, cHostName.length()-1))) {
//                      // insert the device name AH-123* (with wild card) before AH-*   
//                      return INSERT_BEFORE;
//                  }
				if (StringUtils.isNotBlank(iHostName)
						&& StringUtils.isNotBlank(cHostName)
						&& !iHostName.equals(cHostName)
						&& iHostName.endsWith(WILDCARD_SUFFIX)
						&& cHostName.endsWith(WILDCARD_SUFFIX)) {
                      // insert the device name AH-123* (with wild card) before AH-*   
					if (iHostName.startsWith(cHostName.substring(0,cHostName.length() - 1)))
						return INSERT_BEFORE;
					else
						return INSERT_AFTERT;
                  }
                  if(cHostName.endsWith(WILDCARD_SUFFIX)){
                	  return INSERT_BEFORE;
                  }
        	}
        }
        
        if(comparedItem.getType() == SingleTableItem.TYPE_CLASSIFIER){
        	if(insertItem.getType()==SingleTableItem.TYPE_CLASSIFIER){
        		int comSize=getItemTagSize(comparedItem);
        		int insertSize=getItemTagSize(insertItem);
        		if(insertSize>comSize)return INSERT_BEFORE;
        		if(insertSize==1&&comSize==1){
        			if((isTag1Exist(insertItem)&&isTag2Exist(comparedItem))||(isTag1Exist(insertItem)&&isTag3Exist(comparedItem))){
        				return INSERT_BEFORE;
        			}
        			if(isTag2Exist(insertItem)&&isTag3Exist(comparedItem)){
        				return INSERT_BEFORE;
        			}        			
        		}
        	}
        }
        
//        if(insertItem.getType() == SingleTableItem.TYPE_CLASSIFIER) {
//            if(enabled3Tags(insertItem)) {
//                if(contains3Tags(insertItem) && !contains3Tags(comparedItem)) {
//                    // match all 3 tags
//                    return INSERT_BEFORE;
//                } else if(contains2Tags(insertItem) && !contains2Tags(comparedItem)) {
//                    // match 2 tags
//                    return INSERT_BEFORE;
//                } else if(contains1Tag(insertItem) && !contains1Tag(comparedItem)) {
//                    // match 1 tag
//                    return INSERT_BEFORE;
//                }
//            } else if(enabled2Tags(insertItem)) {
//                if(contains2Tags(insertItem) && !contains2Tags(comparedItem)) {
//                    return INSERT_BEFORE;
//                } else if(contains1Tag(insertItem) && !contains1Tag(comparedItem)) {
//                    return INSERT_BEFORE;
//                }                
//            } else if(enabled1Tag(insertItem)) {
//                if(contains1Tag(insertItem) && !contains1Tag(comparedItem)) {
//                    return INSERT_BEFORE;
//                }
//            }
//            if(!contains1Tag(insertItem) && !contains1Tag(comparedItem)) {
//                // none match, insert as default (append to end)
//                return INSERT_EQUAL;
//            }
//        } else if(insertItem.getType() == SingleTableItem.TYPE_HIVEAPNAME) {
//            if(enabled1Tag(comparedItem) && !contains1Tag(comparedItem)) {
//                // insert before the no tags match item
//                return INSERT_BEFORE;
//            }else if(comparedItem.getType() == SingleTableItem.TYPE_HIVEAPNAME) {
//                final String iHostName = insertItem.getTypeName();
//                final String cHostName = comparedItem.getTypeName();
//                if(StringUtils.isNotBlank(iHostName)
//                        && StringUtils.isNotBlank(cHostName)
//                        && !iHostName.equals(cHostName)
//                        && iHostName.endsWith(WILDCARD_SUFFIX)
//                        && cHostName.endsWith(WILDCARD_SUFFIX)
//                        && iHostName.startsWith(cHostName.substring(0, cHostName.length()-1))) {
//                    // insert the device name AH-123* (with wild card) before AH-*   
//                    return INSERT_BEFORE;
//                }
//                
//            }else if(comparedItem.getType() == SingleTableItem.TYPE_MAP) {
//                // insert before topology
//                return INSERT_BEFORE;
//            }
//        } else if(insertItem.getType() == SingleTableItem.TYPE_MAP) {
//            if(enabled1Tag(comparedItem) && !contains1Tag(comparedItem)) {
//                // insert before the no tags match item
//                return INSERT_BEFORE;
//            }
//            //TODO sort the narrow scope
//        }
        return INSERT_AFTERT;
    }

	private boolean isTag3Exist(SingleTableItem insertItem) {
		return insertItem.getTag3()!=null&&!insertItem.getTag3().trim().equals("");
	}

	private boolean isTag2Exist(SingleTableItem insertItem) {
		return insertItem.getTag2()!=null&&!insertItem.getTag2().trim().equals("");
	}

	private boolean isTag1Exist(SingleTableItem insertItem) {
		return insertItem.getTag1()!=null&&!insertItem.getTag1().trim().equals("");
	}
	

//    private boolean contains1Tag(SingleTableItem tagItem) {
//        return tagItem.getType() == SingleTableItem.TYPE_CLASSIFIER 
//                ? deviceTag1List.contains(tagItem.getTag1())
//                || deviceTag2List.contains(tagItem.getTag2())
//                || deviceTag3List.contains(tagItem.getTag3()) : false;
//    }
//    
//    private boolean contains2Tags(SingleTableItem tagItem) {
//        return tagItem.getType() == SingleTableItem.TYPE_CLASSIFIER
//                ? (deviceTag1List.contains(tagItem.getTag1()) && deviceTag2List.contains(tagItem.getTag2()))
//                || (deviceTag1List.contains(tagItem.getTag1()) && deviceTag3List.contains(tagItem.getTag3()))
//                || (deviceTag2List.contains(tagItem.getTag2()) && deviceTag3List.contains(tagItem.getTag3())) : false;
//    }
//
//    private boolean contains3Tags(SingleTableItem tagItem) {
//        return tagItem.getType() == SingleTableItem.TYPE_CLASSIFIER 
//                ? deviceTag1List.contains(tagItem.getTag1())
//                && deviceTag2List.contains(tagItem.getTag2())
//                && deviceTag3List.contains(tagItem.getTag3()) : false;
//    }
    
    private int getItemTagSize(SingleTableItem tagItem) {
    	int res=0;
    	if(isTag1Exist(tagItem))res++;
    	if(isTag2Exist(tagItem))res++;
    	if(isTag3Exist(tagItem))res++;
    	return res;
    }
////wrong implementation
//    private boolean enabled1Tag(SingleTableItem tagItem) {
//        return tagItem.getType() == SingleTableItem.TYPE_CLASSIFIER
//                && ((!tagItem.isTag1Checked() & !tagItem.isTag2Checked()) 
//                || (!tagItem.isTag1Checked() & !tagItem.isTag3Checked())
//                || (!tagItem.isTag2Checked() & !tagItem.isTag3Checked()));
//    }
//
//    private boolean enabled2Tags(SingleTableItem tagItem) {
//        return tagItem.getType() == SingleTableItem.TYPE_CLASSIFIER
//                && (tagItem.isTag1Checked() & tagItem.isTag2Checked() 
//                || tagItem.isTag1Checked() & tagItem.isTag3Checked()
//                || tagItem.isTag2Checked() & tagItem.isTag3Checked());
//    }
//
//    private boolean enabled3Tags(SingleTableItem tagItem) {
//        return tagItem.getType() == SingleTableItem.TYPE_CLASSIFIER 
//                & tagItem.isTag1Checked()
//                & tagItem.isTag2Checked() 
//                & tagItem.isTag3Checked();
//    }

}
