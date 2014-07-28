package com.ah.be.common;

import java.lang.management.ManagementFactory;

public class HmSystemInfoUtil {

	/**
	 * Returns the operating system name.
	 * 
	 * @return the operating system name.
	 */
	public static String getOsName() {
		return ManagementFactory.getOperatingSystemMXBean().getName();
	}

	/**
	 * Returns the operating system architecture.
	 * 
	 * @return the operating system architecture.
	 */
	public static String getOsArch() {
		return ManagementFactory.getOperatingSystemMXBean().getArch();
	}

	/**
	 * Returns the operating system version.
	 * 
	 * @return the operating system version.
	 */
	public static String getOsVersion() {
		return ManagementFactory.getOperatingSystemMXBean().getVersion();
	}

	/**
	 * Returns the number of processors available to the Java virtual machine.
	 * This value may change during a particular invocation of the virtual
	 * machine.
	 * 
	 * @return the number of processors available to the virtual machine; never
	 *         smaller than one.
	 */
	public static int getAvailableProcessors() {
		return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	}

	/**
	 * Returns the system load average for the last minute. The system load
	 * average is the sum of the number of runnable entities queued to the
	 * available processors and the number of runnable entities running on the
	 * available processors averaged over a period of time. The way in which the
	 * load average is calculated is operating system specific but is typically
	 * a damped time-dependent average. If the load average is not available, a
	 * negative value is returned. This method is designed to provide a hint
	 * about the system load and may be queried frequently. The load average may
	 * be unavailable on some platform where it is expensive to implement this
	 * method.
	 * 
	 * @return the system load average; or a negative value if not available.
	 */
	public static double getSystemLoadAverage() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}

	/**
	 * Returns the name representing the running Java virtual machine. The
	 * returned name string can be any arbitrary string and a Java virtual
	 * machine implementation can choose to embed platform-specific useful
	 * information in the returned name string. Each running virtual machine
	 * could have a different name.
	 * 
	 * @return the name representing the running Java virtual machine.
	 */
	public static String getRunningJvmName() {
		return ManagementFactory.getRuntimeMXBean().getName();
	}

	/**
	 * Returns the Java virtual machine implementation name.
	 * 
	 * @return the Java virtual machine implementation name.
	 */
	public static String getJvmName() {
		return ManagementFactory.getRuntimeMXBean().getVmName();
	}

	/**
	 * Returns the Java virtual machine implementation version.
	 * 
	 * @return the Java virtual machine implementation version.
	 */
	public static String getJvmVersion() {
		return ManagementFactory.getRuntimeMXBean().getVmVersion();
	}

	/**
	 * Returns the Java class path that is used by the system class loader to
	 * search for class files. Multiple paths in the Java class path are
	 * separated by the path separator character of the platform of the Java
	 * virtual machine being monitored.
	 * 
	 * @return the Java class path.
	 */
	public static String getClassPath() {
		return ManagementFactory.getRuntimeMXBean().getClassPath();
	}

	/**
	 * Returns the Java library path. Multiple paths in the Java library path
	 * are separated by the path separator character of the platform of the Java
	 * virtual machine being monitored.
	 * 
	 * @return the Java library path.
	 */
	public static String getLibraryPath() {
		return ManagementFactory.getRuntimeMXBean().getLibraryPath();
	}

	/**
	 * Returns the boot class path that is used by the bootstrap class loader to
	 * search for class files. Multiple paths in the boot class path are
	 * separated by the path separator character of the platform on which the
	 * Java virtual machine is running. A Java virtual machine implementation
	 * may not support the boot class path mechanism for the bootstrap class
	 * loader to search for class files. The isBootClassPathSupported() method
	 * can be used to determine if the Java virtual machine supports this
	 * method.
	 * 
	 * @return the boot class path.
	 */
	public static String getBootClassPath() {
		return ManagementFactory.getRuntimeMXBean().getBootClassPath();
	}

	/**
	 * Returns the uptime of the Java virtual machine in milliseconds.
	 * 
	 * @return uptime of the Java virtual machine in milliseconds.
	 */
	public static long getJvmUptime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}

	/**
	 * Returns the start time of the Java virtual machine in milliseconds. This
	 * method returns the approximate time when the Java virtual machine
	 * started.
	 * 
	 * @return start time of the Java virtual machine in milliseconds.
	 */
	public static long getJvmStartTime() {
		return ManagementFactory.getRuntimeMXBean().getStartTime();
	}

}