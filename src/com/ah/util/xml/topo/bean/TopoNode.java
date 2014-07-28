package com.ah.util.xml.topo.bean;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Vertex;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "Node")
public class TopoNode {
    
    /** fields from {@link MapNode} **/
    public double x, y;
    public short severity;
    public String iconName;
    public String address;
    public Float latitude, longitude, centerLatitude, centerLongitude;
    public short centerZoom = -1;
    
    /** fields from {@link MapContainerNode} **/
    @XStreamAsAttribute
    public String mapName;
    public short mapType = 1;
    @XStreamAsAttribute
    @XStreamAlias(value = "type")
    public String mapTypeStr;
    
    public String background;
    public double width, height;
    public double actualWidth, actualHeight;
    public double apElevation;
    public double originX, originY;
    public double floorLoss = 15;
    public int mapOrder = 1;
    public short lengthUnit;
    public int environment;
    
    public List<Perimeter> perimeters= new ArrayList<Perimeter>();
    public List<Wall> walls = new ArrayList<Wall>();
    public List<PlannedDevice> devices = new ArrayList<PlannedDevice>();
    // sub nodes
    public List<TopoNode> children = new ArrayList<TopoNode>();
    
    /*------------------- Methods -------------------*/
    public TopoNode fromMapContainerNode(MapContainerNode node) {
        this.x = node.getX();
        this.y = node.getY();
        this.severity = node.getSeverity();
        this.iconName = node.getIconName();
        this.address = node.getAddress();
        this.latitude = node.getLatitude();
        this.longitude = node.getLongitude();
        this.centerLatitude = node.getCenterLatitude();
        this.centerLongitude = node.getCenterLongitude();
        this.centerZoom = node.getCenterZoom();

        this.mapName = node.getMapName();
        this.mapType = node.getMapType();
        this.mapTypeStr = getMapTypeStr(this.mapType);
        this.background = node.getBackground();
        this.width = node.getWidth();
        this.height = node.getHeight();
        this.actualWidth = node.getActualWidth();
        this.actualHeight = node.getActualHeight();
        this.apElevation = node.getApElevation();
        this.originX = node.getOriginX();
        this.originY = node.getOriginY();
        this.floorLoss = node.getFloorLoss();
        this.mapOrder = node.getMapOrder();
        this.lengthUnit = node.getLengthUnit();
        this.environment = node.getEnvironment();

        if(mapType != MapContainerNode.MAP_TYPE_BUILDING) { // the folder with non-zero actualHeight also can plan, Bug 26179
            perimeters.clear();
            for (Vertex vertex : node.getPerimeter()) {
                perimeters.add(new Perimeter(vertex.getId(), vertex.getType(), vertex.getX(), vertex.getY()));
            }
            walls.clear();
            for (com.ah.bo.monitor.Wall wall : node.getWalls()) {
                walls.add(new Wall(wall.getType(), wall.getX1(), wall.getY1(), wall.getX2(), wall
                        .getY2()));
            }
            devices.clear();
            for (PlannedAP plannedAp : node.getPlannedAPs()) {
                devices.add(new PlannedDevice(plannedAp));
            }
        }

        children.clear();
        for (MapNode mapNode : node.getChildNodes()) {
            if (!mapNode.isLeafNode()) {
                children.add(new TopoNode().fromMapContainerNode((MapContainerNode) mapNode));
            }
        }
        return this;
    }
    public MapContainerNode toMapContainerNode(MapContainerNode node) {
        if(null == node) {
            node = new MapContainerNode();
        }
        node .setX(this.x);
        node.setY(this.y);
        node.setSeverity(this.severity);
        node.setIconName(this.iconName);
        node.setAddress(this.address);
        node.setLatitude(this.latitude);
        node.setLongitude(this.longitude);
        node.setCenterLatitude(this.centerLatitude);
        node.setCenterLongitude(this.centerLongitude);
        node.setCenterZoom(this.centerZoom);

        node.setMapName(this.mapName);
        node.setMapType(this.mapType);
        node.setBackground(this.background);
        node.setWidth(this.width);
        node.setHeight(this.height);
        node.setActualWidth(this.actualWidth);
        node.setActualHeight(this.actualHeight);
        node.setApElevation(this.apElevation);
        node.setOriginX(this.originX);
        node.setOriginY(this.originY);
        node.setFloorLoss(this.floorLoss);
        node.setMapOrder(this.mapOrder);
        node.setLengthUnit(this.lengthUnit);
        node.setEnvironment(this.environment);

        if(mapType != MapContainerNode.MAP_TYPE_BUILDING) { // the folder with non-zero actualHeight also can plan, Bug 26179
            for(Perimeter perimeter : perimeters) {
                Vertex vertext = new Vertex();
                vertext.setId(perimeter.id);
                vertext.setType(perimeter.type);
                vertext.setX(perimeter.x);
                vertext.setY(perimeter.y);
                node.getPerimeter().add(vertext);
            }
            for (Wall wall : walls) {
                com.ah.bo.monitor.Wall walObj = new com.ah.bo.monitor.Wall();
                walObj.setType(wall.type);
                walObj.setX1(wall.x1);
                walObj.setX2(wall.x2);
                walObj.setY1(wall.y1);
                walObj.setY2(wall.y2);
                node.getWalls().add(walObj);
            }
            for(PlannedDevice device : devices) {
                node.getPlannedAPs().add(device.toPlannedAP());
            }
        }

        for(TopoNode child : children) {
            node.getChildNodes().add(child.toMapContainerNode(null));
        }
        return node;
    }
    private String getMapTypeStr(short type) {
        String str = "";
        switch (type) {
        case 1:
            str = "Map";
            break;
        case MapContainerNode.MAP_TYPE_BUILDING:
            str = "Building";
            break;
        case  MapContainerNode.MAP_TYPE_FLOOR:
            str = "Floor";
            break;
        case 99:
            str = "Folder";
            break;

        default:
            break;
        }
        return str;
    }
}
