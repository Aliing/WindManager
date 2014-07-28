package com.ah.be.config.cli.generate;

import java.util.List;

public interface CLIGenerate {
	
	public void init() throws CLIGenerateException;
	
	public boolean isValid();
	
	public List<String> generateCLIs() throws CLIGenerateException;
}
