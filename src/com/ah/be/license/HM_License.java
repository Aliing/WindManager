/**
 *@filename		HM_License.java
 *@version
 *@author		Yuqing Mai
 *@createtime	Mar 6, 2008
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modification history*
 * Yuqing Mai - Initial version
 */
package com.ah.be.license;

import java.io.InputStream;
import java.io.IOException;
import java.lang.Process;
import java.lang.Runtime;
import java.lang.InterruptedException;

import com.ah.be.app.DebugUtil;

/**
 * @author Yuqing Mai
 * @version V0.0.0.3
 */
public final class HM_License
{
	private static HM_License g_ah_license_obj = new HM_License();
	private final static String LICENSE_EXE_PATH = "/HiveManager/license/";
	private final static String DECRYPT_PROG = "decrypt_license";
	private final static String SYSTEM_ID_PROG = "get_system_id -c";

	private static String bios_string = null;

	public final static int VM_TYPE_NONE = 0;
	public final static int VM_TYPE_VMWARE = 1;
	public final static int VM_TYPE_MICROSOFT = 2;
	public final static int VM_TYPE_XEN = 3;
	public final static int VM_TYPE_QEMU = 4;
	public final static int VM_TYPE_OTHERS = 999;

	public final static String VM_TYPE_STRING_NONE = "Not VM";
	public final static String VM_TYPE_STRING_VMWARE = "VMWare";
	public final static String VM_TYPE_STRING_MICROSOFT = "Microsoft";
	public final static String VM_TYPE_STRING_XEN = "Xen";
	public final static String VM_TYPE_STRING_QEMU = "QEMU";
	public final static String VM_TYPE_STRING_OTHERS = "Unidentified";

	/* If this file exists then licensing system will treat VM System as a real system */
	//private final static String VM_SPECIAL_FILE = "/etc/sysconfig/.vm_override";

	/*
	 * Protected Constructor of this class, please use get_instance to retrieve an instance
	 * of this class. Only one instance is allowed at anytime.
	 */
	protected HM_License ()
	{
		/* Nothing here */
	}

	/*
	 * Return an instance of this class, only a single instance is returned.
	 */
	public static HM_License getInstance ()
	{
		return g_ah_license_obj;
	}

	/*
	 * Function that returns the DMI BIOS Manufacture String
	 */
	private String getDMIString ()
	{
        // windows system cannot run this method
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                return VM_TYPE_STRING_NONE;
        }

        Runtime rt = Runtime.getRuntime();
		String command = "/usr/sbin/dmidecode -qt 1";
		String platform = "";

        try
        {
        	Process new_process = rt.exec (command);
        	/* Any error from the dmidecode command will be treated as Others */
            if (new_process.waitFor() != 0)
            	return VM_TYPE_STRING_OTHERS;
            else 
            {
            	byte output [] = new byte [300];
                InputStream input = new_process.getInputStream();
                input.read(output);

                platform = (new String (output)).toLowerCase();
            }
        }
        catch (IOException e)
        {
        	/* Any error from the dmidecode command will be treated as Others */
        	DebugUtil.licenseDebugError("HM_License.isVMWareSystem(): catch ioexception", e);
        	platform = VM_TYPE_STRING_OTHERS;
        }
        catch (InterruptedException e)
        {
        	/* Any error from the dmidecode command will be treated as Others */
            DebugUtil.licenseDebugError("HM_License.isVMWareSystem(): catch InterruptedException", e);
            platform = VM_TYPE_STRING_OTHERS;
        }

		return platform;
	}

	/*
	 * Return the type of VMHost being useda
	 * @return an integer in which the value indicates the type of VM Host 
	 */
	public int getVMHostType ()
	{
		/* Read information from dmidecode method */
		if (bios_string == null)
		{
			bios_string = getDMIString();
		}

		if (bios_string.compareTo (VM_TYPE_STRING_OTHERS) == 0)
			return VM_TYPE_OTHERS;
        if (bios_string.indexOf (VM_TYPE_STRING_VMWARE.toLowerCase()) != -1)
			return VM_TYPE_VMWARE;
        if (bios_string.indexOf (VM_TYPE_STRING_MICROSOFT.toLowerCase()) != -1)
			return VM_TYPE_MICROSOFT;
        if (bios_string.indexOf (VM_TYPE_STRING_XEN.toLowerCase()) != -1)
			return VM_TYPE_XEN;
        if (bios_string.indexOf (VM_TYPE_STRING_QEMU.toLowerCase()) != -1)
			return VM_TYPE_QEMU;

		/* For non-VM platforms */
		return VM_TYPE_NONE;
	}

	/*
	 * Function returns the string for the VM Type inputted, valid types are:
	 * 
         * VM_TYPE_NONE = 0;
         * VM_TYPE_VMWARE = 1;
         * VM_TYPE_MICROSOFT = 2;
         * VM_TYPE_XEN = 3;
         * VM_TYPE_QEMU = 4;
         * VM_TYPE_OTHERS = 999;
	 *
	 * @return Returns the string for the corresponding vm type identified.
	 *
	 */
	public String getVMHostTypeString (int vm_type)
	{
		switch (vm_type)
		{
			case VM_TYPE_NONE :
				return VM_TYPE_STRING_NONE;
			case VM_TYPE_VMWARE :
				return VM_TYPE_STRING_VMWARE;
			case VM_TYPE_MICROSOFT :
				return VM_TYPE_STRING_MICROSOFT;
			case VM_TYPE_XEN :
				return VM_TYPE_STRING_XEN;
			case VM_TYPE_QEMU :
				return VM_TYPE_STRING_QEMU;
			case VM_TYPE_OTHERS : 
			default :
				return VM_TYPE_STRING_OTHERS;

		}
	}
	
	/*
	 * Function isVMWareSystem() returns true if we are running on VMWare platform
	 * Exception: If a special file exists, this command will always return false
	 * @return true if it is on a vmware platform or fails to run, false otherwise
	 * @Deprecated This function is deprecated, please use getVMHostType () instead.
	 */
//	public boolean isVMWareSystem ()
//	{
//		// windows system cannot run this method
//		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
//			return false;
//		}
//		Runtime rt = Runtime.getRuntime();
//		String command = "/usr/sbin/dmidecode -qt 1";
//		//File override_file = new File (VM_SPECIAL_FILE);
//
//		/* Check for special override file */
////		if (override_file.exists())
////			return false;
//		
//		// blackbox system does not support vmware license
//		if (NmsUtil.isHMForOEM())
//			return false;
//
//		try
//		{
//			Process new_process = rt.exec (command);
//			if (new_process.waitFor() != 0)
//				return true;
//			else
//			{
//				byte output [] = new byte [300];
//				InputStream input = new_process.getInputStream();
//				input.read(output);
//
//				String platform = (new String (output)).toLowerCase();
//				if (platform.indexOf ("vmware") != -1)
//					return true;
//				else return false;
//			}
//		}
//		catch (IOException e)
//		{
//			DebugUtil.licenseDebugError("HM_License.isVMWareSystem(): catch ioexception", e);
//			return true;
//		}
//		catch (InterruptedException e)
//		{
//			DebugUtil.licenseDebugError("HM_License.isVMWareSystem(): catch InterruptedException", e);
//			return true;
//		}
//	}
	
	public boolean isVirtualMachineSystem() {
		return VM_TYPE_NONE != getVMHostType();
	}

	/*
     * Returns system id of this running platform
	 */
	public String get_system_id ()
	{
		String os = System.getProperty("os.name");

		// Cannot gain the system id if project is working on Windows.
		if (os.toLowerCase().contains("windows")) {
			return null;
		}

		Runtime rt = Runtime.getRuntime();
		String command = LICENSE_EXE_PATH + SYSTEM_ID_PROG;

		try
		{
			Process new_process = rt.exec (command);
			if (new_process.waitFor() != 0)
				return null;
			else
			{
				byte system_id [] = new byte [39];
				InputStream input = new_process.getInputStream();
				input.read(system_id);
				return new String (system_id);
			}
		}
		catch (IOException e)
		{
			DebugUtil.licenseDebugError("HM_License.get_system_id(): catch IOException", e);
			return null;
		}
		catch (InterruptedException e)
		{
			DebugUtil.licenseDebugError("HM_License.get_system_id(): catch InterruptedException", e);
			return null;
		}

	}

	/*
	 * @param system_id System id use to decrypt the encrypted string.
	 * @param encrypted_string Encrypted License string to decrypt.
	 * @param file_name File to read the encrypted string from.
	 * @return A decrypted byte array, or null if error.
	 *
	 * Return a decrypted string based on encrypted string and system_id input
	 * Enter either encrypted_string or file_name to decrypt from.
	 * If both parameters are entered, file_name takes precedence.
	 * If both parameters are null, a null byte array is returned.
	 */	
	public byte [] decrypt (String system_id, String encrypted_string, String file_name)
	{
		Runtime rt = Runtime.getRuntime();
		String command = null;

		if (file_name != null)
		{
			command = LICENSE_EXE_PATH + DECRYPT_PROG + 
			" -s " + system_id +
			" -f " + file_name;
		}
		else if (encrypted_string != null)
		{
			command = LICENSE_EXE_PATH + DECRYPT_PROG + 
			" -s " + system_id +
			" -l " + encrypted_string;
		}
		else return null;

		try
		{
			Process new_process = rt.exec (command);
			if (new_process.waitFor() != 0)
				return null;
			else
			{
				byte license_string [] = new byte [1024];
				InputStream input = new_process.getInputStream();
				input.read(license_string);
				return license_string;
			}
		}
		catch (IOException e)
		{
			DebugUtil.licenseDebugError("HM_License.decrypt(): catch ioexception", e);
			return null;
		}
		catch (InterruptedException e)
		{
			DebugUtil.licenseDebugError("HM_License.decrypt(): catch InterruptedException", e);
			return null;
		}
	}

	/*
	 * @param system_id System id use to decrypt the encrypted string.
	 * @param encrypted_string Encrypted License string to decrypt.
	 * @return The decrypted string
	 * Return a decrypted string based on encrypted string and system_id input
	 * This equivalent to calling decrypt (system_id, encrypted_string, null).
	 */	
	public String decrypt_from_string (String system_id, String encrypted_string)
	{
		byte decoded [] = decrypt (system_id, transform_encrypted_string(encrypted_string), null);
		return remove_change_line_signal(decoded);
	}

	/*
	 * @param system_id System id use to decrypt the encrypted string.
	 * @param file_name Name of the file to read from
	 * @return The decrypted string
	 * Return a decrypted string based on encrypted string in file and system_id input
	 * This equivalent to calling decrypt (system_id, null, file_name).
	 */	
	public String decrypt_from_file (String system_id, String file_name)
	{
		byte decoded [] = decrypt (system_id, null, file_name);
		return remove_change_line_signal(decoded);
	}
	
	/**
	 * Get rid of any extra characters (new line) before returning
	 * @param decrypted_string
	 * @return String : decrypted_string does not contain '\n'
	 */
	public String remove_change_line_signal (byte[] decrypted_string)
	{
		if (decrypted_string != null)
		{
			/* Get rid of any extra characters (new line) before returning */
			String tmp_string = new String (decrypted_string);
			int i = tmp_string.indexOf('\n');
			if (i != -1)
				return tmp_string.substring(0, i);
			else
				return tmp_string;
		}
		else return null;
	}

	/*
	 * @param encrypted_string Encrypted License string to transform
	 * @return Returns the transformed encrypted license string
	 * This function takes in an encrypted string (multiple-lines) and tranform all new-
	 * line character ('\n') into a dash ('-') so the decrypt_license C program can work 
	 * properly. Function do not change the original encrypted_string
	 */
	public String transform_encrypted_string (String encrypted_string)
	{
		if (encrypted_string != null)
			return encrypted_string.replace('\n','-');
		else return null;
	}

	/*
	 * Test code
	 */
//	public static void main (String args[])
//	{
//		if (args.length != 2)
//		{
//			System.err.println ("Usage: java HM_License system_id base64string");
//			System.exit (1);
//		}

//		HM_License hm_l = HM_License.getInstance();

//		System.out.println (hm_l.isVMWareSystem());

//		System.out.println (hm_l.transform_encrypted_string("3q2+78r++t6LvliANKmi4IBLLAHjcRE5eSzYY88OVGv0f9BebGMQQnrmFZG745qI\nBjfDhQ6FH1WvVrsWBokvhQ=="));

//		String system_id = hm_l.get_system_id ();
//		String outString = hm_l.decrypt_from_string (args[0], args[1]);

//		System.out.println(system_id);
//		System.out.println(outString);
//		System.out.println(outString.length());
//	}
	
}
