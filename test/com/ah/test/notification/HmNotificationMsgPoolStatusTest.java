package com.ah.test.notification;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import com.ah.bo.notificationmsg.NotificationMessageStatus;
import com.ah.bo.notificationmsg.NotificationMessageStatus.AhMsgDisplayFlag;
import com.ah.util.values.BooleanMsgPair;

import static org.junit.Assert.*;

public class HmNotificationMsgPoolStatusTest extends HmNotificationMsgTest {
    
    private static final int SECTION_BLOCK_SIZE = 63;
    private int[] newMsgPriorities;

    /*----------------------getLastDefinedMsgPriorities()--------------------------------------*/
    @Test
    public void getLastDefinedMsgPriorities_Zero() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = 0L;
        previousDefinedMsg2 = 0L;
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        assertNull(this.getDebugInfo()+"the previous msgs didn't exist.", status.getLastDefinedMsgPriorities());
    }
    
    @Test
    public void getLastDefinedMsgPriorities_111_0() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("111", 2);
        previousDefinedMsg2 = 0L;
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        Integer[] msgPriorities = status.getLastDefinedMsgPriorities();
        //getPreviousMsgsBinaryValue(msgPriorities, status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2());
        assertNotNull(this.getDebugInfo()+"The priorities should not be Null.", msgPriorities);
        assertArrayEquals(this.getDebugInfo()+"The priority values isn't correct.", new Integer[]{1, 2, 3}, msgPriorities);
    }
    @Test
    public void getLastDefinedMsgPriorities_101_0() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("101", 2);
        previousDefinedMsg2 = 0L;
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        Integer[] msgPriorities = status.getLastDefinedMsgPriorities();
        //getPreviousMsgsBinaryValue(msgPriorities, status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2());
        assertNotNull(this.getDebugInfo()+"The priorities should not be Null.", msgPriorities);
        assertArrayEquals(this.getDebugInfo()+"The priority values isn't correct.", new Integer[]{1, 3}, msgPriorities);
    }
    @Test
    public void getLastDefinedMsgPriorities_0_111() {
        status = new NotificationMessageStatus();
        previousDefinedMsg2 = Long.parseLong("111", 2);
        previousDefinedMsg1 = 0L;
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        Integer[] msgPriorities = status.getLastDefinedMsgPriorities();
        //getPreviousMsgsBinaryValue(msgPriorities, status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2());
        assertNotNull(this.getDebugInfo()+"The priorities should not be Null.", msgPriorities);
        assertArrayEquals(this.getDebugInfo()+"The priority values isn't correct.", new Integer[] {
                1 + SECTION_BLOCK_SIZE, 2 + SECTION_BLOCK_SIZE, 3 + SECTION_BLOCK_SIZE },
                msgPriorities);
    }
    @Test
    public void getLastDefinedMsgPriorities_11001_1011() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);
        previousDefinedMsg2 = Long.parseLong("1011", 2);
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        Integer[] msgPriorities = status.getLastDefinedMsgPriorities();
        //getPreviousMsgsBinaryValue(msgPriorities, status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2());
        assertNotNull(this.getDebugInfo()+"The priorities should not be Null.", msgPriorities);
        assertArrayEquals(this.getDebugInfo()+"The priority values isn't correct.", new Integer[] {
                1, 4, 5, 1 + SECTION_BLOCK_SIZE, 2 + SECTION_BLOCK_SIZE, 4 + SECTION_BLOCK_SIZE },
                msgPriorities);
    }
    @Test
    public void getLastDefinedMsgPriorities_MAX_Negative_1() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.MAX_VALUE;
        previousDefinedMsg2 = -1L;
        
        Integer[] tmpProperties = new Integer[SECTION_BLOCK_SIZE];
        for (int i = 1; i <= SECTION_BLOCK_SIZE; i++) {
            tmpProperties[i-1] = i;
        }
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        Integer[] msgPriorities = status.getLastDefinedMsgPriorities();
        //getPreviousMsgsBinaryValue(msgPriorities, status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2());
        assertNotNull(this.getDebugInfo()+"The priorities should not be Null.", msgPriorities);
        assertArrayEquals(this.getDebugInfo()+"The priority values isn't correct.", tmpProperties,
            msgPriorities);
    }
    @Test
    public void getLastDefinedMsgPriorities_Negative_1_MAX() {
        status = new NotificationMessageStatus();
        previousDefinedMsg2 = Long.MAX_VALUE;
        previousDefinedMsg1 = -1L;
        
        Integer[] tmpProperties = new Integer[SECTION_BLOCK_SIZE];
        for (int i = 1; i <= SECTION_BLOCK_SIZE; i++) {
            tmpProperties[i-1] = i + SECTION_BLOCK_SIZE;
        }
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        Integer[] msgPriorities = status.getLastDefinedMsgPriorities();
        //getPreviousMsgsBinaryValue(msgPriorities, status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2());
        assertNotNull(this.getDebugInfo()+"The priorities should not be Null.", msgPriorities);
        assertArrayEquals(this.getDebugInfo()+"The priority values isn't correct.", tmpProperties,
                msgPriorities);
    }
    /*----------------------updateLastDefinedMsgPriorities()--------------------------------------*/
    @Test
    public void updateLastDefinedMsgPriorities_Zero_or_Negative() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = 0L;
        previousDefinedMsg2 = -1L;
        displayStatusSection1 = -1L;
        displayStatusSection2 = -1L;
        newMsgPriorities = new int[]{1, 2, 3, 64, 67};
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        status.setDisplayStatusSection1(displayStatusSection1);
        status.setDisplayStatusSection2(displayStatusSection2);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertTrue(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        for (int newPriority : newMsgPriorities) {
            AhMsgDisplayFlag tmpStatus = status.getMsgDisplayStatus(newPriority);
            assertEquals(this.getDebugInfo() + "prirority:" + newPriority
                    + " should be [DISPLAY] status", AhMsgDisplayFlag.DISPLAY, tmpStatus);
        }
    }
    @Test
    public void updateLastDefinedMsgPriorities_11001_0_Nochange() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);
        displayStatusSection1 = Long.parseLong("11001", 2);
        newMsgPriorities = new int[]{1, 4, 5};
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        
        status.setDisplayStatusSection1(displayStatusSection1);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertFalse(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        assertEquals(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection1, status.getDisplayStatusSection1());
    }
    @Test
    public void updateLastDefinedMsgPriorities_11001_0_Nochange_Disorder() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);
        displayStatusSection1 = Long.parseLong("11001", 2);
        newMsgPriorities = new int[]{4, 1, 5};
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        
        status.setDisplayStatusSection1(displayStatusSection1);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertFalse(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        assertEquals(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection1, status.getDisplayStatusSection1());
    }
    @Test
    public void updateLastDefinedMsgPriorities_11001_1101_Nochange() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);
        previousDefinedMsg2 = Long.parseLong("1101", 2);
        displayStatusSection1 = Long.parseLong("11001", 2);
        newMsgPriorities = new int[]{1, 4, 5, 1+SECTION_BLOCK_SIZE, 3+SECTION_BLOCK_SIZE, 4+SECTION_BLOCK_SIZE};
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        
        status.setDisplayStatusSection1(displayStatusSection1);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertFalse(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        assertEquals(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection1, status.getDisplayStatusSection1());
        assertEquals(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection2, status.getDisplayStatusSection2());
    }
    @Test
    public void updateLastDefinedMsgPriorities_11001_0_Add() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);//1, 4 , 5
        displayStatusSection1 = Long.parseLong("10001", 2);//4:Disable
        newMsgPriorities = new int[]{1, 2, 4, 5};//11011, add 2
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        
        status.setDisplayStatusSection1(displayStatusSection1);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertTrue(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        assertNotSame(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection1, status.getDisplayStatusSection1());
        assertEquals(this.getDebugInfo() + " the new priority=[2] should be [DISPLAY] status.",
                AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(2));
        assertEquals(this.getDebugInfo() + " the new priority=[4] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(4));
    }
    @Test
    public void updateLastDefinedMsgPriorities_11001_11_Add() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);//1, 4 , 5
        previousDefinedMsg2 = Long.parseLong("11", 2);//1+SECTION_BLOCK_SIZE, 2+SECTION_BLOCK_SIZE
        displayStatusSection1 = Long.parseLong("10001", 2);//4:Disable
        newMsgPriorities = new int[] { 1, 2, 4, 5, 1 + SECTION_BLOCK_SIZE, 2 + SECTION_BLOCK_SIZE,
                3 + SECTION_BLOCK_SIZE };// section1: 11011, add 2; section2: 111, add 3. 
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        
        status.setDisplayStatusSection1(displayStatusSection1);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertTrue(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        assertNotSame(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection1, status.getDisplayStatusSection1());
        assertEquals(this.getDebugInfo() + " the new priority=[2] should be [DISPLAY] status.",
                AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(2));
        assertEquals(this.getDebugInfo() + " the new priority=[4] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(4));
        
        assertEquals(this.getDebugInfo() + " the new priority=["+(1 + SECTION_BLOCK_SIZE)+"] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(1 + SECTION_BLOCK_SIZE));
        assertEquals(this.getDebugInfo() + " the new priority=["+(2 + SECTION_BLOCK_SIZE)+"] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(2 + SECTION_BLOCK_SIZE));
        assertEquals(this.getDebugInfo() + " the new priority=["+(3 + SECTION_BLOCK_SIZE)+"] should be [DISPLAY] status.",
                AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(3 + SECTION_BLOCK_SIZE));
    }
    @Test
    public void updateLastDefinedMsgPriorities_11001_0_Del() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);//1, 4 , 5
        displayStatusSection1 = Long.parseLong("10001", 2);//1:Enable, 4:Disable
        newMsgPriorities = new int[]{4, 5};//11000, del 1
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        
        status.setDisplayStatusSection1(displayStatusSection1);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertTrue(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        assertNotSame(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection1, status.getDisplayStatusSection1());
        assertEquals(this.getDebugInfo() + " the new priority=[1] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(1));
        assertEquals(this.getDebugInfo() + " the new priority=[4] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(4));
    }
    @Test
    public void updateLastDefinedMsgPriorities_11001_111_Add_Del() {
        status = new NotificationMessageStatus();
        previousDefinedMsg1 = Long.parseLong("11001", 2);//1, 4 , 5
        previousDefinedMsg2 = Long.parseLong("111", 2);//1+SECTION_BLOCK_SIZE, 2+SECTION_BLOCK_SIZE, 3+SECTION_BLOCK_SIZE
        displayStatusSection1 = Long.parseLong("10001", 2);//4:Disable
        displayStatusSection2 = Long.parseLong("101", 2);//2+SECTION_BLOCK_SIZE:Disable
        newMsgPriorities = new int[] { 1, 5, 1 + SECTION_BLOCK_SIZE, 2 + SECTION_BLOCK_SIZE,
                7 + SECTION_BLOCK_SIZE };// section1: 11011, del 4; section2: 1000011, del3, add 7. 
        
        status.setPreviousDefinedMsg1(previousDefinedMsg1);
        status.setPreviousDefinedMsg2(previousDefinedMsg2);
        
        status.setDisplayStatusSection1(displayStatusSection1);
        status.setDisplayStatusSection2(displayStatusSection2);
        
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"Brefore Update");
        
        BooleanMsgPair result = status.updateLastDefinedMsgPriorities(newMsgPriorities);
        
//        debug(result.getDesc());
//        getTotalBinaryValue(status.getDisplayStatusSection1(), status.getDisplayStatusSection2(),
//                status.getPreviousDefinedMsg1(), status.getPreviousDefinedMsg2(),"After Update");
        
        assertNotNull(this.getDebugInfo()+" After update the priorities should NOT be null.", result);
        assertTrue(this.getDebugInfo()+" "+result.getDesc(), result.getValue());
        assertArrayEquals(this.getDebugInfo() + " the array is NOT correct.", newMsgPriorities,
                ArrayUtils.toPrimitive(status.getLastDefinedMsgPriorities()));
        assertSame(this.getDebugInfo() + " the display status should not be changed.",
                displayStatusSection1, status.getDisplayStatusSection1());
        assertEquals(this.getDebugInfo() + " the new priority=[2] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(2));
        assertEquals(this.getDebugInfo() + " the new priority=[4] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(4));
        
        assertEquals(this.getDebugInfo() + " the new priority=["+(1 + SECTION_BLOCK_SIZE)+"] should be [DISPLAY] status.",
                AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(1 + SECTION_BLOCK_SIZE));
        assertEquals(this.getDebugInfo() + " the new priority=["+(2 + SECTION_BLOCK_SIZE)+"] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(2 + SECTION_BLOCK_SIZE));
        assertEquals(this.getDebugInfo() + " the new priority=["+(3 + SECTION_BLOCK_SIZE)+"] should be [NODISPLAY] status.",
                AhMsgDisplayFlag.NODISPLAY, status.getMsgDisplayStatus(3 + SECTION_BLOCK_SIZE));
        assertEquals(this.getDebugInfo() + " the new priority=["+(7 + SECTION_BLOCK_SIZE)+"] should be [DISPLAY] status.",
                AhMsgDisplayFlag.DISPLAY, status.getMsgDisplayStatus(7 + SECTION_BLOCK_SIZE));
    }
}
