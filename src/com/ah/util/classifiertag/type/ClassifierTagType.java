package com.ah.util.classifiertag.type;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.ui.actions.hiveap.NetworkPolicyAction;
import com.ah.util.MgrUtil;
import com.ah.util.values.BooleanMsgPair;

public enum ClassifierTagType {

    VLAN(1) {
        @Override
        Class<?> getClazz() {
            return Vlan.class;
        }

        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof Vlan) {
                return ((Vlan) obj).getItems();
            }
            return null;
        }

        @Override
        public String getValue(SingleTableItem item) {
            return String.valueOf(item.getVlanId());
        }

    },
    NETWORK_SUBCLASS(2) {
        @Override
        Class<?> getClazz() {
            return VpnNetwork.class;
        }

        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof VpnNetwork) {
                return ((VpnNetwork) obj).getSubNetwokClass();
            }
            return null;
        }

        @Override
        public String getValue(SingleTableItem item) {
            // TODO Auto-generated method stub
            return null;
        }
    },
    NETWORK_IP(3) {
        @Override
        Class<?> getClazz() {
            return VpnNetwork.class;
        }

        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof VpnNetwork) {
                return ((VpnNetwork) obj).getReserveClass();
            }
            return null;
        }

        @Override
        public String getValue(SingleTableItem item) {         
            return item.getIpAddress();
        }
    },
    IPADDRESS(4) {
        @Override
        Class<?> getClazz() {
            return IpAddress.class;
        }
        
        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof IpAddress) {
                return ((IpAddress) obj).getItems();
            }
            return null;
        }
        
        @Override
        public String getValue(SingleTableItem item) {
            return item.getIpAddress()
                    + (StringUtils.isNotBlank(item.getNetmask()) ? "|" + item.getNetmask() : "");
        }
    },
    USERATTR(8) {
        @Override
        Class<?> getClazz() {
            return UserProfileAttribute.class;
        }
        
        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof UserProfileAttribute) {
                return ((UserProfileAttribute) obj).getItems();
            }
            return null;
        }
        
        @Override
        public String getValue(SingleTableItem item) {
            String attributeValue = item.getAttributeValue();
            if(attributeValue==null)attributeValue=" ";
			return attributeValue;
        }
        
       
    },
    RADIUSATTR(16) {
        @Override
        Class<?> getClazz() {
            return RadiusAttrs.class;
        }
        
        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof RadiusAttrs) {
                return ((RadiusAttrs) obj).getItems();
            }
            return null;
        }
        
        @Override
        public String getValue(SingleTableItem item) {
			String operatorName = item.getOperatorName();
			if (operatorName == null)
				operatorName = " ";
			else
				operatorName = operatorName + "|" + item.getNameSpaceId();
			return operatorName;
        }
        
       
    },
    MACOROUI(24) {
        @Override
        Class<?> getClazz() {
            return MacOrOui.class;
        }
        
        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof MacOrOui) {
                return ((MacOrOui) obj).getItems();
            }
            return null;
        }
        
        @Override
        public String getValue(SingleTableItem item) {
            String operatorName = item.getMacEntry();
            if(operatorName==null)operatorName=" ";
			return operatorName;
        }
        
       
    }
    ,    
    LocationClientWatch(40) {
        @Override
        Class<?> getClazz() {
            return LocationClientWatch.class;
        }
        
        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof LocationClientWatch) {
                return ((LocationClientWatch) obj).getItems();
            }
            return null;
        }
        
        @Override
        public String getValue(SingleTableItem item) {
            String operatorName = item.getMacEntry();
            if(operatorName==null)operatorName=" ";
			return operatorName;
        }
    }
    
    ,
    PortGroupProfile(41) {
        @Override
        Class<?> getClazz() {
            return PortGroupProfile.class;
        }

        
        @Override
        public List<SingleTableItem> getItems() {
            Object obj = getInstanceFromSession();
            if (null != obj && obj instanceof PortGroupProfile) {
                return ((PortGroupProfile) obj).getItems();
            }
            return null;
        }
        
        @Override
        public String getValue(SingleTableItem item) {
       
        	return NetworkPolicyAction.getPortTemplateName(item.getNonGlobalId(),false); 
    
        }  
    }       


;

private int key;

private ClassifierTagType(int key) {
    this.key = key;
}

    private int getKey() {
        return key;
    }

    Object getInstanceFromSession() {
        return MgrUtil.getSessionAttribute(getClazz().getSimpleName() + "Source");
    }

    public static ClassifierTagType getTag(int key) {
        for (ClassifierTagType tagItem : values()) {
            if (tagItem.getKey() == key) {
                return tagItem;
            }
        }
        return null;
    }
    
    /**
     * Tests whether the tag values in the same tag type exists<br>
     * Please override this method if need to specific for the profile
     * 
     * @author Yunzhi Lin
     * - Time: Aug 24, 2012 2:35:34 PM
     * @param item - new item which will be append to list
     * @param items - the item list
     * @return {true|false, message}
     */
    public BooleanMsgPair existsSameTagType(SingleTableItem item, List<SingleTableItem> items) {
        BooleanMsgPair msg = new BooleanMsgPair(true, MgrUtil.getUserMessage("error.addObjectTypeExists"));
        short type = item.getType();
        if(items!=null){
        for (SingleTableItem oldItem : items) {
            if (oldItem.getType() == type) {
                switch (type) {
                    case SingleTableItem.TYPE_MAP:
                        if (item.getLocation().getId().equals(oldItem.getLocation().getId())
                        		&& (item.getConfigTemplateId() == oldItem.getConfigTemplateId())) {
                            return msg;
                        }
                        break;
                    case SingleTableItem.TYPE_HIVEAPNAME:
                        if (item.getTypeName().equals(oldItem.getTypeName())
                        		&& (item.getConfigTemplateId() == oldItem.getConfigTemplateId())) {
                            return msg;
                        }
                        break;
                    case SingleTableItem.TYPE_CLASSIFIER:
                        if (item.getTag1().equals(oldItem.getTag1())
                            && item.getTag2().equals(oldItem.getTag2())
                            && item.getTag3().equals(oldItem.getTag3())
                            && (item.getConfigTemplateId() == oldItem.getConfigTemplateId())) {
                            return msg;
                        }
                        break;
                    case SingleTableItem.TYPE_NONE://add for mac rang special,need consider later...
                        if (item.getMacRangeFrom().equals(oldItem.getMacRangeFrom())
                            && item.getMacRangeTo().equals(oldItem.getMacRangeTo())) {
                            return msg;
                        }
                        break;    
                    default:
                        return msg;
                }
            }
        }
        }
        msg.setValue(false);
        msg.setDesc(null);
        return msg;
    }

    abstract Class<?> getClazz();

    /**
     * Get the items for the specific profile object
     * 
     * @author Yunzhi Lin
     * - Time: Aug 24, 2012 2:01:05 PM
     * @return <b>Null</b> or List
     */
    abstract public List<SingleTableItem> getItems();
    
    /**
     * Get the values string for the specific profile object.
     * 
     * @author Yunzhi Lin
     * - Time: Aug 24, 2012 2:02:26 PM
     * @param item
     * @return <b>Null</b> or String
     */
    abstract public String getValue(SingleTableItem item);

}
