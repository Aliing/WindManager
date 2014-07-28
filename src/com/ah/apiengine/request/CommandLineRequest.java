package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.CommandLine;
import com.ah.apiengine.response.CommandLineResponse;

public class CommandLineRequest extends AbstractRequest {

	private static final long	serialVersionUID	= 1L;

	private CommandLine				command;

	public CommandLine getCommand() {
		return command;
	}

	public void setCommand(CommandLine command) {
		this.command = command;
	}

	@Override
	public void callback() {
	}

	@Override
	public ByteBuffer execute() throws EncodeException {
		CommandLineResponse response = new CommandLineResponse();
		return response.build(this);
	}

	@Override
	public String getMsgName() {
		return "Command Request";
	}

	@Override
	public int getMsgType() {
		return COMMAND_LINE_REQUEST;
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
					switch (e.getElemType()) {
					case COMMAND_LINE:
						command = (CommandLine) e;
						break;
					default:
						break;
					}
				}
			}
		}
	}

}
