package com.ah.util;

public class GetCasClient {
	
	public static void main(String[] args)
	{
		if(args.length != 1)
		{
			//add parameter log
			System.exit(1);
		}
		
		String strFile = args[0];
		
		String strReturn = CasTool.getCasClient(strFile);
		
		if(null == strReturn)
		{
			System.exit(1);
		}
		
		System.out.println(strReturn);
		
		System.exit(0);		
	}

}
