package com.ah.be.config.cli.brackets;

import java.io.Serializable;


public class BracketObj implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static short NO_BRACKET = 0;
	public static short SMALL_BRACKET = 1;		//()
	public static short MIDDLE_BRACKET = 2;		//[]
	public static short BIG_BRACKET = 3;		//{}
	public static short ANGLE_BRACKET = 4;		//<>
	public static short QUOT = 5;				//" '
	
	protected String fullText;
	protected String content = null;
	protected short bracketType = NO_BRACKET;
	protected int openIndex = -1;
	protected int closeIndex = -1;
	protected int count = 0;
//	protected BracketObj parent;
	
	public BracketObj(){}
	
	public BracketObj(char c){
		bracketType = convertBracketType(c);
	}
	
	public void init(){}
	
	public boolean isValide(){
		if(openIndex > -1 && closeIndex > -1 && closeIndex > openIndex){
			return true;
		}else{
			return false;
		}
	}
	
	public void countAdd(char c){
		if(isValide()){
			return;
		}
		
		short type = convertBracketType(c);
		if(type != this.bracketType){
			return;
		}
		count++;
	}
	
	public void countSubtract(char c, int index){
		if(isValide()){
			return;
		}
		
		short type = convertBracketType(c);
		if(type != this.bracketType){
			return;
		}
		count--;
		if(count == 0){
			this.closeIndex = index;
		}
	}
	
	public BracketObj clone() {
		try{
			return (BracketObj)super.clone();
		}catch(CloneNotSupportedException e){
			return null;
		}
	}
	
	private short convertBracketType(char c){
		switch(c){
			case '(':
			case ')':
				return SMALL_BRACKET;
			case '[':
			case ']':
				return MIDDLE_BRACKET;
			case '{':
			case '}':
				return BIG_BRACKET;
			case '<':
			case '>':
				return ANGLE_BRACKET;
			case '"':
			case 39:	// '
				return QUOT;
			default:
				return NO_BRACKET;
		}
	}
	
	public String getContent(){
		if(this.content != null){
			return this.content;
		}else if(this.isValide()){
			this.content = fullText.substring(openIndex+1, closeIndex);
			return this.content;
		}else{
			return null;
		}
	}
	public void setContent(String content){
		this.content = content;
	}
	
	public String getFullText() {
		return fullText;
	}
	public void setFullText(String fullText) {
		this.fullText = fullText;
	}
	
	public short getBracketType() {
		return bracketType;
	}
	public void setBracketType(short bracketType) {
		this.bracketType = bracketType;
	}
	public void setBracketType(char c) {
		this.bracketType = convertBracketType(c);
	}
	
	public int getOpenIndex() {
		return openIndex;
	}
	public void setOpenIndex(int openIndex) {
		this.openIndex = openIndex;
	}
	
	public int getCloseIndex() {
		return closeIndex;
	}
	public void setCloseIndex(int closeIndex) {
		this.closeIndex = closeIndex;
		if(isValide()){
			getContent();
		}
	}
	
//	public BracketObj getParent() {
//		return parent;
//	}
//	public void setParent(BracketObj parentObj) {
//		if(parentObj == null){
//			return;
//		}
//		if(!(parentObj.getOpenIndex() < this.openIndex && parentObj.getCloseIndex() > this.closeIndex)){
//			//not parent
//			return;
//		}
//		if(this.parent == null){
//			this.parent = parentObj;
//		}else if(parentObj.getOpenIndex() > this.parent.getOpenIndex() && 
//				parentObj.getCloseIndex() < this.parent.getCloseIndex() ){
//			//new parent range small than current parent.
//			this.parent = parentObj;
//		}
//	}
}
