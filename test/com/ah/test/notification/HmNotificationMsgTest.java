package com.ah.test.notification;

import java.util.Arrays;

import com.ah.bo.notificationmsg.NotificationMessageStatus;

public class HmNotificationMsgTest {
    
    protected int priority;
    protected long displayStatusSection1;
    protected long displayStatusSection2;
    protected long previousDefinedMsg1;
    protected long previousDefinedMsg2;
    protected NotificationMessageStatus status;
    
    public String getDebugInfo(String... args) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        String fileName = stackTraceElement.getFileName();
        int lineNumber = stackTraceElement.getLineNumber();
        
        StringBuilder sb = new StringBuilder(methodName + "()@[line:" + lineNumber + "] ");
        if (null != args && args.length > 0) {
            sb.append(" args:").append(Arrays.toString(args)).append(" ");
        }
        return sb.toString();
        
    }
    public void debug(String debugMsg) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        String methodName = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        String fileName = stackTraceElement.getFileName();
        int lineNumber = stackTraceElement.getLineNumber();
             
        System.out.println(className + "." + methodName + "()@[lineNum="
                + lineNumber + "]" + " >> [Message] " + debugMsg);
    }
    public int getRandomInt(int min, int max) {
        return (int) Math.round(Math.random()*(max-min)+min);
    }
    public void getStatusBinaryValue(int pIntVlaue, long section1Value, long section2Value,String... prexfix) {
        if(prexfix.length > 0)
            System.out.println(">>>>> "+Arrays.toString(prexfix));
        int blockSize = 63;
        int block = pIntVlaue / blockSize + (pIntVlaue % blockSize == 0 ? 0 : 1);
        int newPriority = pIntVlaue - blockSize * (block -1);
        long pValue = 1L << (newPriority-1);
        String section1Str = Long.toBinaryString(section1Value);
        String section2Str = Long.toBinaryString(section2Value);
        String pValueStr = Long.toBinaryString(pValue);
        System.out.println("priority[int]=" + pIntVlaue + " in the block<" + block
                + ">, section1[long]=" + section1Value + ", section2[long]=" + section2Value
                + ",\npriority=" + pValueStr + "(length=" + pValueStr.length() + "),\nsection1="
                + section1Str + "(length=" + section1Str.length() + "),\nsection2=" + section2Str
                + "(length=" + section2Str.length() + ")");
        System.out.println("**************************************************************************");
    }
    public void getPreviousMsgsBinaryValue(Integer[] priorities, long section1Value, long section2Value,String... prexfix) {
        if(prexfix.length > 0)
            System.out.println(">>>>> "+Arrays.toString(prexfix));
        String section1Str = Long.toBinaryString(section1Value);
        String section2Str = Long.toBinaryString(section2Value);
        System.out.println("priorities =" + Arrays.toString(priorities)
                + ", (prev)section1[long]=" + section1Value + ", (prev)section2[long]=" + section2Value
                + "\nsection1="
                + section1Str + "(length=" + section1Str.length() + "),\nsection2=" + section2Str
                + "(length=" + section2Str.length() + ")");
        System.out.println("**************************************************************************");
    }
    public void getTotalBinaryValue(long section1Value, long section2Value, long lastDefined1Value, long lastDefined2Value, String... prexfix) {
        if(prexfix.length > 0)
            System.out.println(">>>>> "+Arrays.toString(prexfix));
        String section1Str = Long.toBinaryString(section1Value);
        String section2Str = Long.toBinaryString(section2Value);
        String last1Str = Long.toBinaryString(lastDefined1Value);
        String last2Str = Long.toBinaryString(lastDefined2Value);
        System.out.println("display section1[long]=" + section1Value +", display section2[long]=" + section2Value
                + "\nlast defined section1[long]=" + lastDefined1Value + ", last defined section2[long]=" + lastDefined2Value
                + "\ndisplaySection1=" + section1Str + "(length=" + section1Str.length() 
                + "),\ndisplaySection2=" + section2Str + "(length=" + section2Str.length()
                + "),\nlastDefinedSection1=" + last1Str +"(length=" + last1Str.length()
                + "),\nlastDefinedSection2=" + last2Str +"(length=" +last2Str.length()+")");
        System.out.println("**************************************************************************");
    }
}
