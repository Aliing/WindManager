/**
 *@filename		TestDialog.java
 *@version
 *@author		Fiona
 *@createtime	2008-10-16 PM 04:52:10
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.test.create_profile;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ah.be.common.NmsUtil;
import com.ah.be.os.FileManager;

/**
 * This is a guide line to create a single profile in our project.
 * @author Fiona
 * @version V1.0.0.0
 */
public class TestDialog extends JDialog implements ActionListener
{
	private static final long		serialVersionUID	= 1L;
	
	public static String templatePath = "test\\com\\ah\\test\\create_profile\\template\\";
	public static String boPath = "src\\com\\ah\\bo\\";
	public static String actionPath = "src\\com\\ah\\ui\\actions\\";
	public static String jspPath = "webapps\\config\\";
	public static String lastConfigFeature = "L2_FEATURE_ACCESS_CONSOLE";
	public static String templateJspKey = "ipAddress";

	/**
	 * Define all the components used in the ui.
	 */

	// the Panel
	private JPanel m_Panel_Body;
	private JPanel m_Panel_Whole = new JPanel();

	// the button
	private JButton m_Button;

	// the labels
	private JLabel m_Label1;
	private JLabel m_Label2;
	private JLabel m_Label3;

	// the textField
	private JTextField	m_TextField1 = new JTextField();
	private JTextField	m_TextField2 = new JTextField();
	private JTextField	m_TextField3 = new JTextField();

	/**
	 * Construct method
	 */
	public TestDialog(String arg_Title, String[] arg_Labels)
	{
		initUI(arg_Title, arg_Labels);
	}

	/**
	 * Setup the UI.
	 */
	private void initUI(String arg_Title, String[] arg_Labels)
	{
		this.setSize(500, 200);
		setTitle(arg_Title);
		this.setResizable(false);
		initVariable(arg_Labels);
		setUpGUI();
		addListener();
	}

	/**
	 * Initial all components and setup it's properties.
	 */
	private void initVariable(String[] arg_Labels)
	{
		// the button
		m_Button = new JButton("Next");
		m_Button.setMinimumSize(new Dimension(75, 25));
		m_Button.setPreferredSize(new Dimension(75, 25));

		// the labels
		m_Label1 = new JLabel(arg_Labels[0]);
		m_Label2 = new JLabel(arg_Labels[1]);
		m_Label3 = new JLabel(arg_Labels[2]);
	}

	/**
	 * Set the UI's layout and put all the components in it.
	 */
	private void setUpGUI()
	{
		layoutBodyPanel();
		m_Panel_Whole.setLayout(new GridBagLayout());
		GridBagConstraints gbc_Component = new GridBagConstraints();

		/*
		 * Layout the whole panel.
		 */

		setupGridBagConstraints(gbc_Component, 0, 0,
			GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(10,
				10, 0, 10), 0, 0);
		addComponent(m_Panel_Whole, m_Panel_Body, gbc_Component);

		JPanel m_Panel_Buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		m_Panel_Buttons.add(m_Button);
		
		setupGridBagConstraints(gbc_Component, 0, 1, 1, 1, 1.0, 0.0,
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 1,
				10, 5), 0, 0);
		addComponent(m_Panel_Whole, m_Panel_Buttons, gbc_Component);

		this.getContentPane().add(m_Panel_Whole);
	}

	/**
	 * This method is used to layout the panel's body part.
	 */
	private void layoutBodyPanel()
	{
		/*
		 * Layout all the bottompart.
		 */

		GridBagConstraints gbc_Component = new GridBagConstraints();
		m_Panel_Body = new JPanel(new GridBagLayout());
		m_Panel_Body.setBorder(BorderFactory.createEtchedBorder());

		setupGridBagConstraints(gbc_Component, 0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15,
				10, 0, 5), 0, 0);
		addComponent(m_Panel_Body, m_Label1, gbc_Component);

		setupGridBagConstraints(gbc_Component, 1, 0, 2, 1, 1.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(
				15, 5, 0, 5), 0, 0);
		addComponent(m_Panel_Body, m_TextField1, gbc_Component);

		setupGridBagConstraints(gbc_Component, 0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15,
				10, 5, 5), 0, 0);
		addComponent(m_Panel_Body, m_Label2, gbc_Component);

		setupGridBagConstraints(gbc_Component, 1, 1, 1, 1, 1.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(
				15, 5, 5, 5), 0, 0);
		addComponent(m_Panel_Body, m_TextField2, gbc_Component);

		setupGridBagConstraints(gbc_Component, 0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15,
				10, 10, 5), 0, 0);
		addComponent(m_Panel_Body, m_Label3, gbc_Component);

		setupGridBagConstraints(gbc_Component, 1, 2, 1, 1, 1.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(
				15, 5, 10, 5), 0, 0);
		addComponent(m_Panel_Body, m_TextField3, gbc_Component);
	}

	/**
	 * Setup all components' action listener.
	 */
	private void addListener()
	{
		m_Button.addActionListener(this);
		m_Button.setActionCommand("Next");
	}

	public void actionPerformed(ActionEvent e)
	{
		doActionEvent(e.getActionCommand());
	}

	private void doActionEvent(String command)
	{
		if (command.equals("Next"))
		{
			this.dispose();
			if (m_Label1.getText().equals("BO Packet Name")) {
				ContinueCreateBo(this);
			} else if (m_Label1.getText().equals("Action Packet Name")) {
				ContinueCreateAction(this);
			} else {
				ContinueCreateJsp(this);
			}
		}
	}
	
	public void ContinueCreateBo(TestDialog arg_Dialog) {
		String[] para = arg_Dialog.getAllParameters();
		boolean bool = generateNewBo(para[0], para[1], para[2]);
		if (bool) {
			int result = showMessageDialog(this.getContentPane(), "Add the BO in src\\com\\ah\\util\\HibernateUtil.java", 
				"Step 6", "Next");
			if (JOptionPane.CLOSED_OPTION != result) {
				result = showMessageDialog(this.getContentPane(), "Add the profile in src\\com\\ah\\ui\\actions\\Navigation.java", 
					"Step 7", "Next");
				if (JOptionPane.CLOSED_OPTION != result) {
					result = showMessageDialog(this.getContentPane(), "Add the profile in src\\com\\ah\\ui\\actions\\HmMenuAction.java", 
						"Step 8", "Next");
					if (JOptionPane.CLOSED_OPTION != result) {
						TestDialog dlg = new TestDialog("Step 9 : Create new Action", new String[]{"Action Packet Name", "The Feature Name", "BO Class Name"});
						dlg.setVisible(true);
					}
				}
			}
		} else {
			showMessageDialog(this.getContentPane(), "Create BO failed!", 
				"Step 5", "Finish");
		}
	}
	
	public void ContinueCreateAction(TestDialog arg_Dialog) {
		String[] para = arg_Dialog.getAllParameters();
		generateNewAction(para[0], para[1], para[2]);
		TestDialog dlg = new TestDialog("Step 10 : Create new Jsp", new String[]{"Jsp Packet Name", "The Action Key", "The Feature Name"});
		dlg.setVisible(true);
	}
	
	public void ContinueCreateJsp(TestDialog arg_Dialog) {
		String[] para = arg_Dialog.getAllParameters();
		generateNewJsp(para[0], para[1], para[2]);
		int result = showMessageDialog(this.getContentPane(), "Add the remove or clone the profile in src\\com\\ah\\bo\\mgmt\\impl\\DomainMgmtImpl.java", 
			"Step 11", "Next");
		if (JOptionPane.CLOSED_OPTION != result) {
			result = showMessageDialog(this.getContentPane(), "Add the profile restore in src\\com\\ah\\be\\admin\\restoredb", 
				"Step 12", "Next");
			if (JOptionPane.CLOSED_OPTION != result) {
				showMessageDialog(this.getContentPane(), "Add the profile which makes "+NmsUtil.getOEMCustomer().getAccessPonitName()+" mismatch in src\\com\\ah\\be\\db\\configuration\\ConfigurationUtils.java", 
					"Step 13", "Finish");
			}
		}
	}
	
	public String[] getAllParameters() {
		String[] result = new String[3];
		result[0] = m_TextField1.getText();
		result[1] = m_TextField2.getText();
		result[2] = m_TextField3.getText();
		return result;
	}

	/**
	 * set GridBagLayout parameters
	 * 
	 * @param arg_Constraints
	 *            the GridBagConstraints store parameters
	 */
	public static void setupGridBagConstraints(
		GridBagConstraints arg_Constraints,
		int arg_Gridx,
		int arg_Gridy,
		int arg_Gridxw,
		int arg_Gridyw,
		double arg_Weightx,
		double arg_Weighty,
		int arg_Anchor,
		int arg_Fill,
		Insets arg_Insets,
		int arg_Padx,
		int arg_Pady)
	{
		arg_Constraints.gridx = arg_Gridx;
		arg_Constraints.gridy = arg_Gridy;
		arg_Constraints.gridwidth = arg_Gridxw;
		arg_Constraints.gridheight = arg_Gridyw;
		arg_Constraints.weightx = arg_Weightx;
		arg_Constraints.weighty = arg_Weighty;
		arg_Constraints.anchor = arg_Anchor;
		arg_Constraints.fill = arg_Fill;
		arg_Constraints.insets = arg_Insets;
		arg_Constraints.ipadx = arg_Padx;
		arg_Constraints.ipady = arg_Pady;
	}

	/**
	 * add component to ui container
	 * 
	 * @param arg_Container
	 *            the ui Container will add componets
	 * @param arg_Componets
	 *            the ui components will be added
	 * @param arg_Constraints
	 *            the GridBagLayout parameter
	 */
	public static void addComponent(
		Container arg_Container,
		Component arg_Componets,
		GridBagConstraints arg_Constraints)
	{
		arg_Container.add(arg_Componets, arg_Constraints);
	}
	
	/**
	 * Show the message dialog,the message dialog is the sub dialog of parente
	 * dialog you should specify it's parent object,message,title and button string
	 * 
	 * @param arg_Parent -
	 *            parent dialog/frame object
	 * @param arg_Message -
	 *            the information prompt to the user
	 * @param arg_Title -
	 *            the title of message dialog
	 * @param arg_Button -
	 *            the button string
	 */
	public static int showMessageDialog(
		Component arg_Parent,
		String arg_Message,
		String arg_Title,
		String arg_Button)
	{
		JLabel label_Message = new JLabel();
		label_Message.setForeground(Color.RED);
		Font font_New = new Font("SanSerif", 0, 15);
		label_Message.setFont(font_New);
		label_Message.setText("<html><body><p align=justify>" + arg_Message
			+ "</p></body></html>");
		Object[] options =
		{ "<html><body style=\"font-weight:normal\">&nbsp;&nbsp;"+arg_Button+"&nbsp;&nbsp;</body></html>" };
		return JOptionPane.showOptionDialog(arg_Parent, label_Message, arg_Title,
			JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
			options[0]);
	}
	
	/**
	 * Generate the java class of new profile bo
	 *
	 *@param arg_BoPack : the packet name which this file under
	 *		 arg_Table : the bo table name in database
	 *		 arg_BoName : the java class name of this bo
	 *@return if generate successfully
	 */
	public static boolean generateNewBo (String arg_BoPack, String arg_Table, String arg_BoName) {
		FileManager fileMg = FileManager.getInstance();
		if ("".equals(arg_BoPack) || "".equals(arg_Table) || "".equals(arg_BoName)) {
			return true;
		}
		String newBoPath = "com.ah.bo." + arg_BoPack + ";";
		try {
			String newBoFile = boPath + arg_BoPack + "\\" + arg_BoName +".java";
			if ((new File(newBoFile)).exists()) {
				return true;
			}
			// get all the information from the template bo
			String[] oldStr =  fileMg.readFile(templatePath + "ProfileBo.java");
			oldStr[0] = "package " + newBoPath;
			oldStr[20] = oldStr[20].replace("PROFILE_BO", arg_Table);
			oldStr[21] = oldStr[21].replace("ProfileBo", arg_BoName);
			// generate the new java class file
			fileMg.createFile(newBoFile, oldStr);
			return true;
		} catch (Exception ex) {	
			System.out.println(ex.getMessage());
			return false;
		}
	}
	
	/**
	 * Generate the java class of new profile action
	 *
	 *@param arg_ActionP : the packet name which this file under
	 *		 arg_FeatureN : the feature public static final String
	 *		 arg_BoName : bo name
	 */
	public static void generateNewAction (String arg_ActionP, String arg_FeatureN, String arg_BoName) {
		FileManager fileMg = FileManager.getInstance();
		if ("".equals(arg_ActionP) || "".equals(arg_FeatureN) || "".equals(arg_BoName)) {
			return;
		}
		try {		
			String newActionFile = actionPath + arg_ActionP + "\\" + arg_BoName +"Action.java";
			if ((new File(newActionFile)).exists()) {
				return;
			}
			// get all the information from the template action
			String[] oldAction =  fileMg.readFile(templatePath + "ProfileAction.java");
			oldAction[0] = "package com.ah.ui.actions."+arg_ActionP+";";
			oldAction[4] = oldAction[4].replace("ProfileAction", arg_BoName+"Action");
			for (int i = 5; i < oldAction.length; i ++) {
				if (oldAction[i].contains("ProfileBo")) {
					oldAction[i] = oldAction[i].replace("ProfileBo", arg_BoName);
				}
				if (oldAction[i].contains(lastConfigFeature)) {
					oldAction[i] = oldAction[i].replace(lastConfigFeature, arg_FeatureN);
				}
			}
			// generate the new java class file
			fileMg.createFile(newActionFile, oldAction);
		} catch (Exception ex) {	
			System.out.println(ex.getMessage());
		}
	}
	
	/**
	 * Generate the jsp file of new profile
	 *
	 *@param arg_JspPack : the packet name which this file under
	 *		 arg_Key : the feature key(refer to )
	 *		 arg_FeatureN : the feature public static final String
	 *@return if generate successfully
	 */
	public static void generateNewJsp (String arg_JspPack, String arg_Key, String arg_FeatureN) {
		FileManager fileMg = FileManager.getInstance();
		if ("".equals(arg_JspPack) || "".equals(arg_Key) || "".equals(arg_FeatureN)) {
			return;
		}
		try {
			String newJspFile = jspPath + arg_JspPack + "\\" + arg_Key +".jsp";
			if ((new File(newJspFile)).exists()) {
				return;
			}
			// get all the information from the template jsp
			String[] oldStr =  fileMg.readFile(templatePath + "profile.jsp");
			oldStr[5] = oldStr[5].replace(templateJspKey, arg_Key);
			oldStr[24] = oldStr[24].replace(lastConfigFeature, arg_FeatureN);
			oldStr[54] = oldStr[54].replace(templateJspKey, arg_Key);
			oldStr[64] = oldStr[64].replace(templateJspKey, arg_Key);
			oldStr[87] = oldStr[87].replace(lastConfigFeature, arg_FeatureN);
			// generate the new jsp file
			fileMg.createFile(newJspFile, oldStr);
			
			String newJspLisFile = jspPath + arg_JspPack + "\\" + arg_Key +"List.jsp";
			if ((new File(newJspLisFile)).exists()) {
				return;
			}
			// get all the information from the template jsp list
			String[] oldJspStr =  fileMg.readFile(templatePath + "profileList.jsp");
			oldJspStr[5] = oldJspStr[5].replace(templateJspKey, arg_Key);
			oldJspStr[28] = oldJspStr[28].replace(templateJspKey, arg_Key);
			oldJspStr[82] = oldJspStr[82].replace(templateJspKey, arg_Key);
			oldJspStr[87] = oldJspStr[87].replace(templateJspKey, arg_Key);
			// generate the new jsp list file
			fileMg.createFile(newJspLisFile, oldJspStr);
			
		} catch (Exception ex) {	
			System.out.println(ex.getMessage());
		}
	}
	
	public static void main(String args[])
	{
		JPanel common = new JPanel();
		int result = showMessageDialog(common, "Add this profile node in webapps\\WEB-INF\\navigation.xml", "Step 1", "Next");
		if (JOptionPane.CLOSED_OPTION != result) {
			result = showMessageDialog(common, "Add the profile configuration information in webapps\\WEB-INF\\classes\\struts.xml", 
				"Step 2", "Next");
			if (JOptionPane.CLOSED_OPTION != result) {
				result = showMessageDialog(common, "Add the profile configuration information in webapps\\WEB-INF\\classes\\struts-config.xml", 
					"Step 3", "Next");
				if (JOptionPane.CLOSED_OPTION != result) {
					result = showMessageDialog(common, "Add the profile configuration information in webapps\\WEB-INF\\classes\\tiles-defs.xml", 
						"Step 4", "Next");
					if (JOptionPane.CLOSED_OPTION != result) {						
						TestDialog dlg = new TestDialog("Step 5 : Create new BO", new String[]{"BO Packet Name", "BO Table Name", "BO Class Name"});
						dlg.setVisible(true);
					}
				}
			}
		}
	}
}
