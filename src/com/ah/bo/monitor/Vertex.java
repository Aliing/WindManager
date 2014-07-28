package com.ah.bo.monitor;

import java.io.Serializable;

import javax.persistence.Embeddable;

/*
 * @author Chris Scheers
 */

@Embeddable
@SuppressWarnings("serial")
public class Vertex implements Serializable {
	private int id;

	private short type;

	private double x, y;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
