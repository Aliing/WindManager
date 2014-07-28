package com.ah.util.xml.topo.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "perimeter")
public class Perimeter {
    public Perimeter() {
    }
    public Perimeter(int id, short type, double x, double y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
    }
    public int id;
    public short type;
    public double x, y;
}