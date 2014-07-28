package com.ah.be.config.cli.testTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.be.config.cli.parse.CLIParseResult;
import com.ah.be.config.cli.util.CmdRegexUtil;

public class CLICheck {
	
	private static List<CmdPattern> cmdPatternList = new ArrayList<CmdPattern>();

	public static List<String> readLine(File file) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader bw = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bw.readLine()) != null) {
				list.add(line);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static void writeFile(File file, String context){
		BufferedWriter output = null;
		try{
			output = new BufferedWriter(new FileWriter(file));
			output.write(context);
		}catch(Exception e){
			
		}finally{
			if(output != null){
				try{
					output.close();
				}catch(Exception e){
					
				}
			}
		}
	}
	
	public static CLIParseResult parse(String cli){
		if(cli == null){
			return null;
		}
		
		String oCli = cli;
		boolean isNoCmd = cli.startsWith("no");
		if(isNoCmd){
			cli = cli.substring(2);
		}
		
		for(CmdPattern cp : cmdPatternList){
			if(cp.isMatch(cli)){
				CLIParseResult result = new CLIParseResult();
				if(isNoCmd){
					result.setCli("no"+cli);
				}else{
					result.setCli(cli);
				}
				result.setCmd(cp.getCmd());
				result.setMatche(true);
				result.setNoCmd(isNoCmd);
				return result;
			}
		}
		return null;
	}
	
	public static class CmdPattern{
		private String cmd;
		private String cmdRegex;
		private Pattern pattern;
		
		public CmdPattern(String cmd){
			this.cmd = cmd;
		}
		
		public void init(){
			cmdRegex = CmdRegexUtil.generate(cmd);
			pattern = Pattern.compile(cmdRegex);
		}
		
		public String getCmd(){
			return this.cmd;
		}
		
		public boolean isMatch(String cli){
			Matcher match = pattern.matcher(cli);
			return match.matches();
		}
	}
	
	public static void main(String... args){
		long startTime = System.currentTimeMillis();
		String cmdFilePath = "D:\\test\\all_cmds\\ap-br-sr-2.txt";
		List<String> allCmds = readLine(new File(cmdFilePath));
		for(String cmd : allCmds){
			CmdPattern cp = new CmdPattern(cmd);
			cp.init();
			cmdPatternList.add(cp);
		}
		System.out.println("init end, spend:"+(System.currentTimeMillis()-startTime) + "ms");
		
		startTime = System.currentTimeMillis();
		String cliFilePath = "D:\\test\\all_cmds\\testcase_cli_1.txt";
		List<String> cllClis = readLine(new File(cliFilePath));
		Collections.sort(cllClis);
		CLIParseResult resObj = null;
		List<CLIParseResult> allResults = new ArrayList<CLIParseResult>();
		List<String> failedClis = new ArrayList<String>();
		for(String cli : cllClis){
			resObj = parse(cli);
			if(resObj != null){
				allResults.add(resObj);
			}else{
				failedClis.add(cli);
			}
		}
		
		StringBuffer sbuff = new StringBuffer();
		for(CLIParseResult res11 : allResults){
			sbuff.append(res11.getCli());
			sbuff.append("\n");
			sbuff.append(res11.getCmd());
			sbuff.append("\n");
			sbuff.append("\n");
		}
		writeFile(new File("D:\\test\\all_cmds\\result.txt"), sbuff.toString());
		
		System.out.println("CLI parse end, spend:"+(System.currentTimeMillis()-startTime) + "ms");
		System.out.println(allResults.size());
		
		Collections.sort(failedClis);
		StringBuffer failedBuf = new StringBuffer();
		for(String failCli : failedClis){
			failedBuf.append(failCli);
			failedBuf.append("\n");
		}
		writeFile(new File("D:\\test\\all_cmds\\parse_failed.txt"), failedBuf.toString());
	}
}
