package com.ah.be.config.cli.generate;

public interface CLIGenerateAutoAdaptive extends CLIGenerate {

	public void setPlatform(short platform);

	public void setVersion(String version);

	public void setType(short type);
}
