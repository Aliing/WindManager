package com.ah.be.ls.data2;

import java.io.IOException;
import java.io.OutputStream;

public interface FileTxObject {
	public void write(OutputStream out) throws IOException;
}
