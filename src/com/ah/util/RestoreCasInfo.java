package com.ah.util;

public class RestoreCasInfo {
	
	public static void main(String[] args)
	{
		if(args.length != 2)
		{
			//add log paramer error
			System.exit(1);
		}
		
		String strSrcFile = args[0];
		String strDesFile = args[1];
		
		if(!CasTool.copyCasInfo(strSrcFile, strDesFile))
		{
			//add log error
			System.exit(1);
		}
		
		System.exit(0);
	}

}
