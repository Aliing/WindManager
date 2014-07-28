package com.ah.util;

public class SetCasClient {
	
	public static void main(String[] args)
	{
		if(args.length != 3)
		{
			//add para log
			System.exit(1);
		}
		
		String strFile = args[0];
		String strOldClient = args[1];
		String strNewClient = args[2];
		
		if(!CasTool.overwriteCasClient(strFile, strOldClient, strNewClient))
		{
			//add log
			System.exit(1);
		}
		
		System.exit(0);
	}

}
