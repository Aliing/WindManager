package com.ah.test.notification;

import org.junit.Test;

import com.ah.bo.notificationmsg.NotificationMessageStatus;
import com.ah.bo.notificationmsg.NotificationMessageStatus.AhMsgDisplayFlag;
import com.ah.util.values.BooleanMsgPair;

import static org.junit.Assert.*;

public class HmNotificationMsgDisplayFlagTest extends HmNotificationMsgTest{

    public void getDisplayStatus() {
        int priority = 3;
        long initFlagValue = 7L;
        long positionValue = 1L << (priority-1);
        long displayFlag = initFlagValue & positionValue;
        System.out.println("initFlagValue="+initFlagValue+" ["+Long.toBinaryString(initFlagValue)+"]");
        System.out.println("displayFlag="+displayFlag+", positionValue="+positionValue+" ["+Long.toBinaryString(positionValue)+"]");
        if(displayFlag == positionValue) {
            System.out.println("Yeah! The flag was setting on.");
        }
    }
    /*----------------------getDisplayStatus()--------------------------------------*/
    @Test
    public void getDisplayStatus_flag_flag_priority10Random() {
        status = new NotificationMessageStatus();
        
        priority = 1;
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
        
        priority = 0;
        result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.ERROR, result);
        
        priority = getRandomInt(1, 63); 
        result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo("this Random priority="+priority)+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag0_flag_priority1() {
        displayStatusSection1 = 0L;
        priority = 1;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }

    @Test
    public void getDisplayStatus_flag1_flag_priority1() {
        displayStatusSection1 = 1L;
        priority = 1;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag3_flag_priority1() {
        displayStatusSection1 = 3L;
        priority = 1;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag4_flag_priority63() {
        displayStatusSection1 = 4L;
        priority = 63;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
//        getStatusBinaryValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2());
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flagMax_flag_priority63() {
        displayStatusSection1 = Long.MAX_VALUE;
        priority = 63;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2());
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag4_flag_priority64() {
        displayStatusSection1 = 4L;
        priority = 64;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag4_flag0_priority64() {
        displayStatusSection1 = 4L;
        displayStatusSection2 = 0L;
        priority = 64;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        status.setDisplayStatusSection2(displayStatusSection2);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag4_flag1_priority64() {
        displayStatusSection1 = 4L;
        displayStatusSection2 = 1L;
        priority = 64;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        status.setDisplayStatusSection2(displayStatusSection2);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag_flag4_priority64() {
        displayStatusSection2 = 4L;
        priority = 64;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2());
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag_flag4_priority65() {
        displayStatusSection2 = 4L;
        priority = 65;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2());
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag_flag4_priority126() {
        displayStatusSection2 = 4L;
        priority = 126;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2());
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, result);
    }
    
    @Test
    public void getDisplayStatus_flag_flag4_priority127() {
        displayStatusSection2 = 4L;
        priority = 127;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        AhMsgDisplayFlag result = status.getMsgDisplayStatus(priority);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2());
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.ERROR, result);
    }
    /*----------------------updateDisplayStatus()--------------------------------------*/
    @Test
    public void updateDisplayStatus_priorityless1_or_prioritymore126() {
        status = new NotificationMessageStatus();
        
        priority = 0;
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertFalse(this.getDebugInfo()+" operation error!", result);
        
        priority = 0;
        result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertFalse(this.getDebugInfo()+" operation error!", result);

        priority = 127;
        result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertFalse(this.getDebugInfo()+" operation error!", result);
        
        priority = 127;
        result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertFalse(this.getDebugInfo()+" operation error!", result);
    }
    
    @Test
    public void updateDisplayStatus_priority1_flagOne0_display() {
        priority = 1;
        displayStatusSection1 = 0L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority1_flagOne0_nonDisplay_nonUpdate() {
        priority = 1;
        displayStatusSection1 = 0L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", displayStatusSection1, status.getDisplayStatusSection1());
    }
    
    @Test
    public void updateDisplayStatus_priority1_flagOne1_display_nonUpdate() {
        priority = 1;
        displayStatusSection1 = 1L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
       // getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
    }
    
    @Test
    public void updateDisplayStatus_priority1_flagOne1_nonDisplay() {
        priority = 1;
        displayStatusSection1 = 1L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        BooleanMsgPair updateDisplayStatus = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY);
        boolean result = updateDisplayStatus.getValue();
        assertTrue(this.getDebugInfo()+" "+updateDisplayStatus.getDesc(), result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority3_flagOne10_display() {
        priority = 3; // 100
        displayStatusSection1 = 10L; // 1010
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority3_flagOne10_nonDisplay_nonUpdate() {
        priority = 3; // 100
        displayStatusSection1 = 10L; // 1010
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority4_flagOne10_display_nonUpdate() {
        priority = 4; // 1000
        displayStatusSection1 = 10L; // 1010
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority4_flagOne10_nonDisplay() {
        priority = 4; // 1000
        displayStatusSection1 = 10L; // 1010
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority8_flagOne10_display() {
        priority = 8;
        displayStatusSection1 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority8_flagOne10_nonDisplay_nonUpdate() {
        priority = 8;
        displayStatusSection1 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority63_flagOne10_display() {
        priority = 63;
        displayStatusSection1 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority63_flagOneMax_display() {
        priority = 63;
        displayStatusSection1 = Long.MAX_VALUE;
        //System.out.println(Long.toHexString(displayStatusSection1));
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority63_flagOneMax_nonDisplay() {
        priority = 63;
        displayStatusSection1 = Long.MAX_VALUE;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority64_flagOne10_display() {
        priority = 64; // 1
        displayStatusSection1 = 10L; // 1010
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority64_flagOne10_nonDisplay_nonUpdate() {
        priority = 64;
        displayStatusSection1 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        BooleanMsgPair updateDisplayStatus = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY);
        boolean result = updateDisplayStatus.getValue();
        assertTrue(this.getDebugInfo()+" "+updateDisplayStatus.getDesc(), result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority64_flagTwo10_display() {
        priority = 64;
        displayStatusSection2 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority64_flagTwo10_nonDisplay_nonUpdate() {
        priority = 64; // 1
        displayStatusSection2 = 10L; // 1010
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority126_flagTwo10_display() {
        priority = 126;
        displayStatusSection2 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority126_flagTwo10_nonDisplay() {
        priority = 126;
        displayStatusSection2 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        boolean result = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY).getValue();
        assertTrue(this.getDebugInfo()+" operation error!", result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority127_flagTwo10_display_nonUpdate() {
        priority = 127;
        displayStatusSection2 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        BooleanMsgPair updateDisplayStatus = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY);
        boolean result = updateDisplayStatus.getValue();
        assertFalse(this.getDebugInfo()+" "+updateDisplayStatus.getDesc(), result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.ERROR, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_priority127_flagTwo10_nondisplay() {
        priority = 127;
        displayStatusSection2 = 10L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection2(displayStatusSection2);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        BooleanMsgPair updateDisplayStatus = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.NODISPLAY);
        boolean result = updateDisplayStatus.getValue();
        assertFalse(this.getDebugInfo()+" "+updateDisplayStatus.getDesc(), result);
        
        //getStatusHexValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.ERROR, status.getMsgDisplayStatus(priority));
    }
    
    @Test
    public void updateDisplayStatus_unknown_or_error() {
        priority = 1;
        displayStatusSection1 = 1L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
        BooleanMsgPair updateDisplayStatus = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.UNKOWN);
        boolean result = updateDisplayStatus.getValue();
        assertFalse(this.getDebugInfo()+" "+updateDisplayStatus.getDesc(), result);
        
        updateDisplayStatus = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.ERROR);
        result = updateDisplayStatus.getValue();
        assertFalse(this.getDebugInfo()+" "+updateDisplayStatus.getDesc(), result);
        
    }
    
    @Test
    public void updateDisplayStatus_priority1_flagOne_Negative_display() {
        priority = 1;
        displayStatusSection1 = -1L;
        
        status = new NotificationMessageStatus();
        status.setDisplayStatusSection1(displayStatusSection1);
        
//        getStatusBinaryValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "Before update");
        BooleanMsgPair updateDisplayStatus = status.updateMsgDisplayStatus(priority, AhMsgDisplayFlag.DISPLAY);
        boolean result = updateDisplayStatus.getValue();
        assertTrue(this.getDebugInfo()+" "+updateDisplayStatus.getDesc(), result);
        
//        getStatusBinaryValue(priority, status.getDisplayStatusSection1(), status.getDisplayStatusSection2(), "After update");
        assertEquals(this.getDebugInfo()+" operation error!", AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(priority));
    }
    
}
