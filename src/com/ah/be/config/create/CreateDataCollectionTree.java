package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.DataCollectionInt;
import com.ah.xml.be.config.DataCollectionObj;

public class CreateDataCollectionTree {
	
	private DataCollectionInt collectionImp;
	private GenerateXMLDebug oDebug;
	
	private DataCollectionObj collectionObj;

	public CreateDataCollectionTree(DataCollectionInt collectionImp, GenerateXMLDebug oDebug){
		this.collectionImp = collectionImp;
		this.oDebug = oDebug;
	}
	
	public DataCollectionObj getDataCollectionObj(){
		return this.collectionObj;
	}
	
	public void generate() throws Exception{
		collectionObj = new DataCollectionObj();
		
		generateDataCollectionLevel_1();
	}
	
	private void generateDataCollectionLevel_1() {
		
		/** element: <data-collection>.<enable> */
		collectionObj.setEnable(CLICommonFunc.getAhOnlyAct(collectionImp.isDataCollectionEnable()));
	}
}
