// $Id: NotificationMessageStatus.java,v 1.7.32.1 2013/09/26 08:52:12 xfeng Exp $
package com.ah.bo.notificationmsg;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.values.BooleanMsgPair;

@Entity
@Table(name = "NOTIFICATION_MESSAGE_STATUS")
@org.hibernate.annotations.Table(appliesTo = "NOTIFICATION_MESSAGE_STATUS", indexes = {
		@Index(name = "NOTIFICATION_MESSAGE_STATUS_OWNER", columnNames = { "OWNER" })
		})
public class NotificationMessageStatus implements HmBo {

    /*-----------------Constant-------------------*/
    private static final long serialVersionUID = -39014292997259862L;
    
    private static final int BLOCK_LIMIT = 2;
    /**  Set the block size limitation to 63, for the Long.MAX_VALUE = 2<sup>63</sup>-1.*/
    private static final int SECTION_BLOCK_SIZE = 63;
    /** The maximum of the message priority.*/
    private static final int MAX_PRIOPRITY = SECTION_BLOCK_SIZE * BLOCK_LIMIT;
    
    /*-----------------Data Field-------------------*/
    /**  Mapping with the id of the HmUser table */
    /**  change to email address, hmonline user does not have the id of the HmUser table */
    //private long userId;
    private String userEmail;
    /**
     * This field can store the display status for 63 messages which from priority=1 to priority=63.<br>
     * <pre>
     * e.g., <br>
     * this value is 1, if the priority=1 msg display flag is enable;
     * this value is 5(binary string: 101), if the priority=1 msg display flag is enable and the priority=3 is enable too;
     * this value is 9(binary string: 1001), if the priority=1 msg display flag is enable and the priority=4 is enable too;
     * and so on.
     * </pre> 
     */
    private long displayStatusSection1 = AhMsgDisplayFlag.NODISPLAY.getStatus();
    /**
     * This field can store the display status for 63 messages which from priority=64 to priority=126.<br>
     * <b>The actual priority = priority - 63</b>
     * @see the field displayStatusSection1
     */
    private long displayStatusSection2 = AhMsgDisplayFlag.NODISPLAY.getStatus();
    
    /** 
     * The field will be used to restore the display status of the previous messages in pool.<br>
     * <b>Currently, it should equals 7(binary value:111).<b>  
     */
    private long previousDefinedMsg1;
    private long previousDefinedMsg2;

    /*-----------------Common Methods-------------------*/
    public enum AhMsgDisplayFlag {
        DISPLAY(1), NODISPLAY(0), UNKOWN(-1), ERROR(-999);
        
        private final int status;
        private AhMsgDisplayFlag(int status) {
            this.status = status;
        }
        public int getStatus() {
            return status;
        }
    }
    /**
     * Get the display status by the message priority<br>
     *
	 * @param priority -
     * @return the enumeration type {@link AhMsgDisplayFlag}
     */
    public AhMsgDisplayFlag getMsgDisplayStatus(int priority) {
        if(priority > 0) {
            long displayValue, positionValue, blockValue;
            int block = getMsgDisplayStatusBlockNum(priority);
            
            // current block limit number is 2 (1~63, 64~126).
            if(block > BLOCK_LIMIT) {
                return AhMsgDisplayFlag.ERROR;
            }
            
            if(block == 1) {
                blockValue = this.displayStatusSection1; 
            } else {
                blockValue = this.displayStatusSection2; 
            }
            
            if(blockValue < 0) {
                // the message display flag was unset.
                return AhMsgDisplayFlag.UNKOWN;
            } else {
                positionValue = getPriorityValuePosition(priority, block);
                displayValue = blockValue & positionValue;
            }
            if(displayValue == positionValue) {
                // the message display flag is enable.
                return AhMsgDisplayFlag.DISPLAY;
            } else { 
                // the message display flag is disable.
                return AhMsgDisplayFlag.NODISPLAY;
            }
        } else {
            // the priority of message should be a non-zero and positive number.
            return AhMsgDisplayFlag.ERROR;
        }
    }
    /**
     * Update the specific priority message display status.
     * @author Yunzhi Lin
     * - Time: Nov 30, 2011 7:13:29 PM
     * @param priority The message priority
     * @param status {@link AhMsgDisplayFlag}
     * @return {true|false, description}
     */
    public BooleanMsgPair updateMsgDisplayStatus(int priority, AhMsgDisplayFlag status) {
        if(status == AhMsgDisplayFlag.ERROR || status == AhMsgDisplayFlag.UNKOWN) {
            return new BooleanMsgPair(false, "Unable to update the display status, invalid status input:"+status);
        }
        if(priority < 1 || priority > MAX_PRIOPRITY) {
            return new BooleanMsgPair(false, 
                    "The priority of message ["+priority+"] is out of the specific range [0~"+MAX_PRIOPRITY+"].");
        }
        
        int block = getMsgDisplayStatusBlockNum(priority);
        if(block > BLOCK_LIMIT) {
            return new BooleanMsgPair(false, "Can't locate the priority cause the block size =["
                    + block + "] exceeds the limit size =[" + BLOCK_LIMIT + "].");
        }
        
        AhMsgDisplayFlag oldStatus = getMsgDisplayStatus(priority);
        if(oldStatus == AhMsgDisplayFlag.ERROR) {
            return new BooleanMsgPair(false,
                    "Unable to update the display status for error occurs when get the display status.");
        } else if (oldStatus == AhMsgDisplayFlag.UNKOWN
                && status == AhMsgDisplayFlag.DISPLAY) {
            // reset the data
            if(block == 1 && this.displayStatusSection1 < 0L) {
                this.displayStatusSection1 = 0L;
            }
            if(block == 2 && this.displayStatusSection2 < 0L) {
                this.displayStatusSection2 = 0L;
            }
            oldStatus = AhMsgDisplayFlag.NODISPLAY;
        }
        long positionValue = getPriorityValuePosition(priority, block);
        
        if(oldStatus == AhMsgDisplayFlag.NODISPLAY) {
            if(status == AhMsgDisplayFlag.DISPLAY) {
                if(block == 1) {
                    this.displayStatusSection1 = this.displayStatusSection1 | positionValue;
                } else {
                    this.displayStatusSection2 = this.displayStatusSection2 | positionValue;
                }
                return new BooleanMsgPair(true, 
                        "Update the specific priority message display status [Enable] successfully.");
            }
        } else if(oldStatus == AhMsgDisplayFlag.DISPLAY) {
            if(status == AhMsgDisplayFlag.NODISPLAY) {
                if(block == 1) {
                    this.displayStatusSection1 = this.displayStatusSection1 & (~positionValue);
                } else {
                    this.displayStatusSection2 = this.displayStatusSection2 & (~positionValue);
                }
                return new BooleanMsgPair(true, 
                        "Update the specific priority message display status [Disable] successfully.");
            }
        }
        
        return new BooleanMsgPair(true, "No need to udapte the specific priority message display status.");
    }
    /**
     * @author Yunzhi Lin
     * - Time: Dec 13, 2011 3:18:04 PM
     * @param priority -
     * @param block -
     * @return position value
     */
    private long getPriorityValuePosition(int priority, int block) {
        int newPriority = priority - SECTION_BLOCK_SIZE * (block -1);
		return 1L << (newPriority -1);
    }
    /**
     * Locate the priority in which block section, and it will start from 1.
     * 
     * @author Yunzhi Lin
     * - Time: Nov 30, 2011 2:17:25 PM
     * @param priority message priority
     * @return the block number
     */
    private int getMsgDisplayStatusBlockNum(int priority) {
        // calculate the which block the priority value locate (start from 1)
        return priority / SECTION_BLOCK_SIZE + (priority % SECTION_BLOCK_SIZE == 0 ? 0 : 1);
    }
    
    /**
     * Get the priorities of the last defined messages in pool for specific user. 
     * @author Yunzhi Lin
     * - Time: Dec 14, 2011 2:48:39 PM
     * @return null or Integer array (never return an empty array)
     */
    public Integer[] getLastDefinedMsgPriorities() {
        if (this.previousDefinedMsg1 <= 0L && this.previousDefinedMsg2 <= 0L) {
            return null;
        }
        
        List<Integer> priorityList = new ArrayList<Integer>();
        if(this.previousDefinedMsg1 > 0L) {
            priorityList.addAll(getPriorityList(this.previousDefinedMsg1, 1));
        }
        if(this.previousDefinedMsg2 > 0L) {
            priorityList.addAll(getPriorityList(this.previousDefinedMsg2, 2));
        }
        return priorityList.isEmpty() ? null : priorityList.toArray(new Integer[]{priorityList.size()});
    }
    /**
     * Get the priorities of the defined messages by specific value block.
     * Find the defined messages in the binary string (position) of the long value.<br>
     * e.g.,<br>
     * <pre>
     * long=7, binary string="111", return list=[1, 2, 3];
     * long=11, binary string="1011", return list=[1, 2, 4];
     * ...
     * </pre>
     * @author Yunzhi Lin
     * - Time: Dec 14, 2011 3:07:52 PM
     * @param prioritiesLongValue - the priorities value of last defined messages
     * @param block - the specific value block
     * @return (Empty) List
     */
    private List<Integer> getPriorityList(long prioritiesLongValue, int block) {
        List<Integer> priorityList = new ArrayList<Integer>();
        if(block > 0 && block <= BLOCK_LIMIT) {
            String binaryValue = Long.toBinaryString(prioritiesLongValue);
            int length = binaryValue.length();
            for (int index = length -1 ; index >= 0; index--) {
                char ch = binaryValue.charAt(index);
                if(CharUtils.toIntValue(ch, -1) == 1) {
                    priorityList.add(length-index + SECTION_BLOCK_SIZE * (block-1));
                }
            }
        }
        return priorityList;
    }
    /**
     * Update the last defined priorities values 
     * @author Yunzhi Lin
     * - Time: Dec 15, 2011 1:41:37 PM
     * @param newMsgPriorities - the new priorities
     * @return {@link BooleanMsgPair}, it will return true when update successfully; otherwise return false.
     */
    public BooleanMsgPair updateLastDefinedMsgPriorities(int[] newMsgPriorities) {
        BooleanMsgPair valuePair = null;
        if(null != newMsgPriorities) {
            int[] oldMsgPriorities = ArrayUtils.toPrimitive(this.getLastDefinedMsgPriorities());
            Arrays.sort(newMsgPriorities); // sort the new array
            if (Arrays.equals(newMsgPriorities, oldMsgPriorities)) {
                valuePair = new BooleanMsgPair(false,
                        "No need to update the last defined message priorities.");
            } else {
                // reset the last defined priorities value
                this.previousDefinedMsg1 = 0L;
                this.previousDefinedMsg2 = 0L;
                // handle the removed message display status
                if(ArrayUtils.isNotEmpty(oldMsgPriorities)) {
                    for (int oldPriority : oldMsgPriorities) {
                        if(!ArrayUtils.contains(newMsgPriorities, oldPriority)) {
                            //update the display status to NODISPLAY
                            valuePair = updateMsgDisplayStatus(oldPriority, AhMsgDisplayFlag.NODISPLAY);
                            if(!valuePair.getValue()) {
                                return valuePair;
                            }   
                        }
                    }
                }
                // handle the newer message display status
                for (int newPriority : newMsgPriorities) {
                    if(!ArrayUtils.contains(oldMsgPriorities, newPriority)) {
                        //update the display status to DISPLAY (only update the new priority status)
                        valuePair = updateMsgDisplayStatus(newPriority, AhMsgDisplayFlag.DISPLAY);
                        if(!valuePair.getValue()) {
                            return valuePair;
                        }
                    }
                    
                    // update the lastDefined priorities value
                    int block = getMsgDisplayStatusBlockNum(newPriority);
                    long positionValue = getPriorityValuePosition(newPriority, block);
                    if(block == 1) {
                        this.previousDefinedMsg1 = this.previousDefinedMsg1 | positionValue; 
                    } else {
                        this.previousDefinedMsg2 = this.previousDefinedMsg2 | positionValue; 
                    }
                }

                valuePair = new BooleanMsgPair(true, "Update the last defined message priorities successfully.");
            }
        }
        return valuePair;
    }
    /*-----------------Override Methods-------------------*/
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("User Email:", userEmail)
                .append("displayStatus in section1:", displayStatusSection1)
                .append("displayStatus in section1:", displayStatusSection2)
                .append("previous msg pool1:", previousDefinedMsg1)
                .append("previous msg pool2:", previousDefinedMsg2).toString();
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NotificationMessageStatus)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        NotificationMessageStatus otherObj = (NotificationMessageStatus) obj;
        return new EqualsBuilder().append(this.userEmail, otherObj.userEmail)
                .append(this.displayStatusSection1, otherObj.displayStatusSection1)
                .append(this.displayStatusSection2, otherObj.displayStatusSection2)
                .append(this.previousDefinedMsg1, otherObj.previousDefinedMsg1)
                .append(this.previousDefinedMsg2, otherObj.previousDefinedMsg2).isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.userEmail).append(this.displayStatusSection1)
                .append(this.displayStatusSection2)
                .append(this.previousDefinedMsg1)
                .append(this.previousDefinedMsg2).toHashCode();
    }
    /*-----------------Getter/Setter-------------------*/
    public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
    public long getDisplayStatusSection1() {
        return displayStatusSection1;
    }
	public void setDisplayStatusSection1(long displayStatusSection1) {
        this.displayStatusSection1 = displayStatusSection1;
    }
    public long getDisplayStatusSection2() {
        return displayStatusSection2;
    }
    public void setDisplayStatusSection2(long displayStatusSection2) {
        this.displayStatusSection2 = displayStatusSection2;
    }
    public long getPreviousDefinedMsg1() {
        return previousDefinedMsg1;
    }
    public void setPreviousDefinedMsg1(long previousDefinedMsg1) {
        this.previousDefinedMsg1 = previousDefinedMsg1;
    }
    public long getPreviousDefinedMsg2() {
        return previousDefinedMsg2;
    }
    public void setPreviousDefinedMsg2(long previousDefinedMsg2) {
        this.previousDefinedMsg2 = previousDefinedMsg2;
    }
    /*------------Implements from HmBo-----------------*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER", nullable = false)
    private HmDomain owner;
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Version
    private Timestamp version;
    @Override
    public HmDomain getOwner() {
        return owner;
    }
    @Override
    public void setOwner(HmDomain owner) {
        this.owner = owner;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Timestamp getVersion() {
        return version;
    }

    @Override
    public void setVersion(Timestamp version) {
        this.version = version;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
    }

}