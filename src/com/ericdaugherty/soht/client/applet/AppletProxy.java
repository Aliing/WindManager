package com.ericdaugherty.soht.client.applet;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;

public class AppletProxy extends JApplet {

	private static final long serialVersionUID = 1L;

	protected ProxyMonitorWindow window;

	public AppletProxy() {

	}

	@Override
	public void init() {
		window = new ProxyMonitorWindow(this);
		Container content = getContentPane();
		content.add(window.getContentPane(), BorderLayout.CENTER);
		setVisible(false);// trigger ProxyMonitorWindow-componentShown event to resize
		setFocusable(true);
		requestFocusInWindow();
	}

	@Override
	public void start() {
		if (window != null) {
			window.start();
		}
	}

	@Override
	public void stop() {
		if (window != null) {
			window.stop();
		}
	}

	@Override
	public void destroy() {
		if (window != null) {
			window.dispose();
		}
	}

}