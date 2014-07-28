package com.ah.bo.monitor;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.ah.util.EnumItem;

/*
 * @author Chris Scheers
 */

@Embeddable
@SuppressWarnings("serial")
public class Wall implements Serializable {
	private short type;

	private double x1, y1, x2, y2;

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	public String toString() {
		String fmt = "(%1$.2f, %2$.2f) - (%3$.2f, %4$.2f)";
		return String.format(fmt, x1, y1, x2, y2);
	}

	public static short[] wallIds = new short[] { 1001, 1002, 1003, 1004, 1005,
			1006, 1007, 1010, 1009, 1008 };

	public static String[] wallTypes = new String[] { "Bookshelf (2 dB)",
			"Cubicle (1 dB)", "Dry Wall (3 dB)", "Brick Wall (10 dB)",
			"Concrete (12 dB)", "Elevator Shaft (30 dB)", "Thin Door (2 dB)",
			"Thick Door (6 dB)", "Thin Window (1 dB)", "Thick Window (3 dB)" };

	public static double[] wallWidth = new double[] { 0.35, 0.1, 0.1, 0.3, 0.5,
			0.2, 0.08, 0.12, 0.005, 0.015 };

	public static double[] wallAbsorption = new double[] { 5.7, 10, 30, 33, 24,
			150, 25, 50, 200, 200 };

	public static short getWallIndex(short type) {
		for (short i = 0; i < wallTypes.length; i++) {
			if (wallIds[i] == type) {
				return i;
			}
		}
		if (type < wallTypes.length) {
			return type;
		} else {
			return 0;
		}
	}

	public static EnumItem[] WALL_TYPES = new EnumItem[wallIds.length];

	static {
		for (int i = 0; i < wallIds.length; i++) {
			WALL_TYPES[i] = new EnumItem(wallIds[i], wallTypes[i]);
		}
	}
}
