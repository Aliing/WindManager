package com.ah.ha.communication;

import java.nio.channels.SelectionKey;

public interface SelectionKeyHandler {

	void handle(SelectionKey key);

}