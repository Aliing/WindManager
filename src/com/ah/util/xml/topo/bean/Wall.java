package com.ah.util.xml.topo.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "wall")
public class Wall {
    public Wall() {
    }
    public Wall(short type, double x1, double y1, double x2, double y2) {
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    public short type;
    public double x1, y1, x2, y2;
}