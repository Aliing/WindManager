package com.ericdaugherty.soht.client.core;

import java.nio.channels.SelectionKey;

public interface SelectionKeyHandler {

	void handle( SelectionKey key );

}