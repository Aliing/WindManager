package com.ericdaugherty.soht.client.applet;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;

import com.ericdaugherty.soht.client.configuration.ConfigurationManager;
import com.ericdaugherty.soht.client.configuration.Host;
import com.ericdaugherty.soht.client.core.Proxy;
import com.ericdaugherty.soht.client.util.AvailablePortFinder;
import com.ericdaugherty.soht.client.util.StringManager;

public class ProxyMonitorWindow extends JWindow implements ActionListener, Runnable {

	private static final long serialVersionUID = 1L;

	private static final int MINUTE_VALUE_IN_SECOND = 60;

	private static final int HOUR_VALUE_IN_SECOND = 60 * MINUTE_VALUE_IN_SECOND;

	private static final int DAY_VALUE_IN_SECOND = 24 * HOUR_VALUE_IN_SECOND;

	private static final int YEAR_VALUE_IN_SECOND = 365 * DAY_VALUE_IN_SECOND;

	private JPanel bgPanel;

	private JTextArea msgTextArea;

	private JTextArea timerTextArea;

	private JButton stopButton;

	private int localPort;

	private int proxyTimeout;

	private Thread proxyMonitor;

	private boolean running;

	/** SSH client proxy */
	private Proxy proxy;

	/* Resource Bundle */
	private final StringManager sm;

	public ProxyMonitorWindow(JApplet applet) {
		sm = StringManager.getManager(ProxyMonitorWindow.class.getPackage().getName());
		loadParams(applet);
		initUI();
		addListener(applet);
	}

	public synchronized void start() {
		if (proxyMonitor == null || !proxyMonitor.isAlive()) {
			proxyMonitor = new Thread(this);
			proxyMonitor.start();
		}
	}

	public synchronized void stop() {
		setUserMessage(sm.getString("info.ssh.proxy.stopping"), Color.RED);
		running = false;
	}

	@Override
	public void run() {
		String displayMsg = null;

		try {
			// Start SSH client proxy.
			proxy.startProxy();

			setUserMessage(sm.getString("info.ssh.proxy.started", String.valueOf(localPort)), Color.BLACK);

			stopButton.setEnabled(true);
			running = true;
			int remaining = proxyTimeout * 60;

			while (running && proxy.isAlive()) {
				if (remaining > 0) {
					timerTextArea.setText(sm.getString("info.estimated.time.remaining", transformTime(remaining--)));
				} else {
					timerTextArea.setText(sm.getString("info.ssh.proxy.session.expired"));
					stop();
				}

				Thread.sleep(1000);
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = e.getMessage();
			String userMsg = null;

			if (e instanceof SecurityException) {
				userMsg = sm.getString("error.ssh.proxy.socket.create.blocking", String.valueOf(localPort));
			} else {
				if (errorMsg != null) {
					if (errorMsg.contains("Address already in use")) {
						userMsg = sm.getString("error.ssh.proxy.socket.port.occupied", String.valueOf(localPort));
					}
				}
			}

			displayMsg = userMsg != null ? userMsg : sm.getString("error.ssh.proxy.start.failed.withReason", errorMsg);
		} finally {
			if (stopButton.isEnabled()) {
				stopButton.setEnabled(false);
			}

			if (proxy != null) {
				// Stop SSH client proxy.
				proxy.stopProxy();
			}

			if (displayMsg == null) {
				displayMsg = running ? sm.getString("error.ssh.proxy.stop.by.accident") : sm.getString("info.ssh.proxy.stopped");

			}

			setUserMessage(displayMsg, Color.RED);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (action.equals("stop")) {
			System.out.println(action);
			stop();
		}
	}

	private void loadParams(JApplet applet) {
		String host = applet.getParameter("host");

		String strPort = applet.getParameter("port");
		int port = Integer.parseInt(strPort);

		String serverURL = applet.getParameter("serverURL");

		String serverUsername = applet.getParameter("serverUsername");

		String serverPassword = applet.getParameter("serverPassword");

		// String strLocalPort = applet.getParameter("localPort");
		// localPort = Integer.parseInt(strLocalPort);

		// Automatically select an available port.
		localPort = AvailablePortFinder.getNextAvailable(10000);

		String strProxyTimeout = applet.getParameter("proxyTimeout");
		proxyTimeout = Integer.parseInt(strProxyTimeout);

		System.out.println("Host Info -> Host:<" + host + "> Port:<" + port + ">");
		System.out.println("SSH Proxy Info -> Local Port:<" + localPort + ">" + " Timeout:<"
				+ proxyTimeout + ">");

		ConfigurationManager configurationManager = new ConfigurationManager();
		configurationManager.setServerURL(serverURL);
		configurationManager.setServerLoginRequired(true);
		configurationManager.setServerUsername(serverUsername);
		configurationManager.setServerPassword(serverPassword);

		String httpProxyHost = applet.getParameter("httpProxyHost");

		if (httpProxyHost != null && !httpProxyHost.isEmpty()) {
			String httpProxyPort = applet.getParameter("httpProxyPort");
			String httpProxyUsername = applet.getParameter("httpProxyUsername");
			String httpProxyPassword = applet.getParameter("httpProxyPassword");
			String strPersistentConnectivity = applet.getParameter("persistentConnectivity");
			boolean persistentConnectivity = Boolean.parseBoolean(strPersistentConnectivity);

			System.out.println("HTTP Proxy Info -> Host:<" + httpProxyHost + "> Port:<"
					+ httpProxyPort + "> PersistentConnectivity:<" + persistentConnectivity + ">");

			configurationManager.setUseHTTPProxy(true);
			configurationManager.setProxyHost(httpProxyHost);
			configurationManager.setProxyPort(httpProxyPort);
			configurationManager.setProxyLogin(httpProxyUsername);
			configurationManager.setProxyPassword(httpProxyPassword);
			configurationManager.setUseStatelessConnection(!persistentConnectivity);
		}

		Host hostInfo = new Host(localPort, host, port);
		proxy = new Proxy(configurationManager, hostInfo);
	}

	private void initUI() {
		timerTextArea = new JTextArea();
		timerTextArea.setPreferredSize(new Dimension(500, 50));
		timerTextArea.setFont(new Font("Serif", Font.PLAIN, 16));
		timerTextArea.setEditable(false);
		timerTextArea.setLineWrap(true);
		timerTextArea.setWrapStyleWord(true);

		JPanel centerPanel = new JPanel();
		centerPanel.add(timerTextArea);

		msgTextArea = new JTextArea();
		msgTextArea.setPreferredSize(new Dimension(500, 170));
		msgTextArea.setFont(new Font("Serif", Font.BOLD, 22));
		msgTextArea.setForeground(Color.BLACK);
		msgTextArea.setEditable(false);
		msgTextArea.setLineWrap(true);
		msgTextArea.setWrapStyleWord(true);

		JPanel msgPanel = new JPanel();
		msgPanel.add(msgTextArea);

		stopButton = new JButton();
		stopButton.setText("Stop");
		stopButton.addActionListener(this);
		stopButton.setActionCommand("stop");
		stopButton.setEnabled(false);

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(stopButton);
		bottomPanel.setPreferredSize(new Dimension(500, 30));

		bgPanel = new JPanel();
		bgPanel.setLayout(new BoxLayout(bgPanel, BoxLayout.Y_AXIS));
		bgPanel.add(msgPanel);
		bgPanel.add(centerPanel);
		bgPanel.add(bottomPanel);
		bgPanel.add(new JPanel());
		bgPanel.setVisible(true);

		getContentPane().add(bgPanel, BorderLayout.CENTER);

		pack();
		setLocation(300, 100);
		setFocusable(true);
	}

	private void addListener(Component component) {
		enableEvents(AWTEvent.KEY_EVENT_MASK);

		ComponentListener l = new ComponentListener() {
			@Override
			public void componentHidden(ComponentEvent e) {

			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {
				resizePanel(e);
			}

			@Override
			public void componentShown(ComponentEvent e) {
				resizePanel(e);
			}

			private void resizePanel(ComponentEvent e) {
				Component c = e.getComponent();
				int cw = c.getWidth();
				int ch = c.getHeight();
				int cwm, chm;

				if (c instanceof JApplet) {
					cwm = (c.getWidth() - ((JApplet) c).getContentPane().getWidth());
					chm = (c.getHeight() - ((JApplet) c).getContentPane().getHeight());
				} else {
					cwm = (c.getWidth() - ((JWindow) c).getContentPane().getWidth());
					chm = (c.getHeight() - ((JWindow) c).getContentPane().getHeight());
				}

				cw -= cwm;
				ch -= chm;
				bgPanel.setSize(cw, ch);
				bgPanel.repaint();
			}
		};

		component.addComponentListener(l);
	}

	private void setUserMessage(String msg, Color color) {
		Color candidate = color != null ? color : Color.BLACK;
		msgTextArea.setForeground(candidate);
		msgTextArea.setText(msg);
	}

	/**
	 * This method is transform time int value to String value to display.
	 * 
	 * @param second
	 *            -
	 * @return -
	 */
	private String transformTime(int second) {
		second = second < 0 ? 0 : second;
		String str_time;
		int int_year = second / YEAR_VALUE_IN_SECOND;
		int remain_days = second % YEAR_VALUE_IN_SECOND;
		int int_day = remain_days / DAY_VALUE_IN_SECOND;
		int remain_hours = remain_days % DAY_VALUE_IN_SECOND;
		int int_hour = remain_hours / HOUR_VALUE_IN_SECOND;
		int remain_minutes = remain_hours % HOUR_VALUE_IN_SECOND;
		int int_min = remain_minutes / MINUTE_VALUE_IN_SECOND;
		int int_sec = remain_minutes % MINUTE_VALUE_IN_SECOND;
		String str_sec = int_sec < 10 ? "0" + int_sec : String.valueOf(int_sec);

		if (second >= YEAR_VALUE_IN_SECOND) {
			str_time = int_year + " years " + int_day + " days, " + int_hour + " hours " + int_min
					+ " minutes " + str_sec + " seconds";
		} else if (second >= DAY_VALUE_IN_SECOND && second < YEAR_VALUE_IN_SECOND) {
			str_time = int_day + " days, " + int_hour + " hours " + int_min + " minutes " + str_sec
					+ " seconds";
		} else if (second >= HOUR_VALUE_IN_SECOND && second < DAY_VALUE_IN_SECOND) {
			str_time = int_hour + " hours " + int_min + " minutes " + str_sec + " seconds";
		} else if (second >= MINUTE_VALUE_IN_SECOND && second < HOUR_VALUE_IN_SECOND) {
			str_time = int_min + " minutes " + str_sec + " seconds";
		} else {
			str_time = int_sec + " seconds";
		}

		return str_time;
	}

}