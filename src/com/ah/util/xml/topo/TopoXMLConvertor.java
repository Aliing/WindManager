package com.ah.util.xml.topo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.values.BooleanMsgPair;
import com.ah.util.xml.XmlDeclarationXStream;
import com.ah.util.xml.topo.bean.TopoNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter.UnknownFieldException;
import com.thoughtworks.xstream.io.xml.KXml2Driver;

public class TopoXMLConvertor {
    
    private static final Tracer LOG = new Tracer(TopoXMLConvertor.class.getSimpleName());
    
    private static final String XML_EXTENSION = "xml";

    private Set<String> imageNames = null;
    
    public TopoXMLConvertor() {
    }
    
    public TopoXMLConvertor(boolean collectMapNames) {
        if(collectMapNames) {
            imageNames = new HashSet<>();
        }
    }
    
    public BooleanMsgPair convert2XML(MapContainerNode mapNodeContainer, String destFilePath) {
        optResult = null;
        try {
            if(null == mapNodeContainer || StringUtils.isBlank(destFilePath)) {
                optResult = new BooleanMsgPair(false, MgrUtil.getUserMessage("error.topo.xml.param.invalid"));
            } else {
                TopoNode topoNode = new TopoNode().fromMapContainerNode(mapNodeContainer);
                generateXML(destFilePath, topoNode);
                
                if(null != imageNames) {
                    collectMapNamesFromNode(topoNode);
                }
                optResult = new BooleanMsgPair(true, MgrUtil.getUserMessage("info.topo.xml.convert.succ"));
            }
        } catch (Exception e) {
           handleException(destFilePath, e, MgrUtil.getUserMessage("error.topo.xml.export.unknow"));
        }
        
        return optResult;
    }

    public BooleanMsgPair convert2Obj(String srcFilePath, MapContainerNode mapNodeContainer) {
        optResult = null;
        if(StringUtils.isBlank(srcFilePath) || null == mapNodeContainer) {
            optResult = new BooleanMsgPair(false, MgrUtil.getUserMessage("error.topo.xml.param.invalid"));
        } else {
            TopoNode topoNode = null;
            File srcFile = new File(srcFilePath);
            if(srcFile.exists() && srcFile.isFile()) {
                topoNode = parseXML(srcFile);
                
                if(null != topoNode) {
                     topoNode.toMapContainerNode(mapNodeContainer);
                    optResult = new BooleanMsgPair(true, MgrUtil.getUserMessage("info.topo.xml.convert.succ"));
                }
            } else {
                optResult = new BooleanMsgPair(false, MgrUtil.getUserMessage("error.topo.xml.import.filepath.invalid"));
                LOG.error("convert2Obj", "The file path is invalid. path: " + srcFilePath);
            }
        }
        return optResult;
    }
    
    /*--------------------------- private methods ---------------------------*/
    private void generateXML(String destFilePath, TopoNode topoNode) {
        FileOutputStream fs = null;
        Writer writer = null;
        final String errorMsg = "Error when create the xml file.";
        try {
            final boolean isXMLExtension = FilenameUtils.isExtension(destFilePath, XML_EXTENSION);
            if(isXMLExtension) {
                // create a file
                final File file = new File(destFilePath);
                
                // delete the files 2 hours ago under folder
                deleteFiles(file, 2);
                
                FileUtils.touch(file);
                
                fs = new FileOutputStream(destFilePath);
                writer = new OutputStreamWriter(fs, "UTF-8");
                
                XStream xStream = new XmlDeclarationXStream(new KXml2Driver());
                xStream.autodetectAnnotations(true); // enable annotations
                xStream.toXML(topoNode, writer);
            } else {
                optResult = new BooleanMsgPair(false, MgrUtil.getUserMessage("error.topo.xml.export.fileextension"));
            }
        } catch (FileNotFoundException e) {
            handleException(destFilePath, e, errorMsg);
        } catch (IOException e) {
            handleException(destFilePath, e, errorMsg);
        } catch (Exception e) {
            handleException(destFilePath, e, errorMsg);
        } finally {
            if(null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.error("Error to close writer", e);
                }
            }
            if(null != fs) {
                try {
                    fs.close();
                } catch (IOException e) {
                    LOG.error("Error to close OutputStream", e);
                }
            }
        }
    }

    private void deleteFiles(final File file, final int hours) {
        final File parentFile = file.getParentFile();
        if(parentFile.exists() && parentFile.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(parentFile, new IOFileFilter() {
                
                @Override
                public boolean accept(File file) {
                    final Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY - hours));
                    return FileUtils.isFileOlder(file, calendar.getTime());
                }
                
                @Override
                public boolean accept(File dir, String name) {
                    return false;
                }}, null);

            for (File deleteFile : files) {
                FileUtils.deleteQuietly(deleteFile);
            }
        }
    }

    private void handleException(String destFilePath, Exception e, String errorMsg) {
        if(new File(destFilePath).exists()) {
            FileUtils.deleteQuietly(new File(destFilePath));
        }
        LOG.error(errorMsg, e);
        optResult = new BooleanMsgPair(false, errorMsg);
    }

    private TopoNode parseXML(File srcFile) {
        TopoNode topoNode = null;
        String srcFilePath = srcFile.getPath();
        final String errorMsg = "Error when create the xml file.";
        try {
            FileInputStream fs = new FileInputStream(srcFile);
            InputStreamReader reader = new InputStreamReader(fs, "UTF-8");
            
            XStream xStream = new XStream(new KXml2Driver());
            xStream.autodetectAnnotations(true);
            xStream.alias("Node", TopoNode.class);
            topoNode = (TopoNode) xStream.fromXML(reader);
            LOG.debug("fromXML = " + ReflectionToStringBuilder.toString(topoNode));
            
        } catch (FileNotFoundException e) {
            handleException(
                    srcFilePath,
                    e,
                    errorMsg
                            + " "
                            + MgrUtil.getUserMessage("error.topo.xml.import.file.noexist",
                                    srcFilePath));
        } catch (UnsupportedEncodingException e) {
            handleException(
                    srcFilePath,
                    e,
                    errorMsg
                            + " "
                            + MgrUtil.getUserMessage(
                                    "error.topo.xml.import.file.encoding.unsupported", srcFilePath));
        } catch (UnknownFieldException e) {
            handleException(
                    srcFilePath,
                    e,
                    errorMsg
                            + " "
                            + MgrUtil.getUserMessage(
                                    "error.topo.xml.import.file.content.unsupported", srcFilePath));
        } catch (Exception e) {
            handleException(srcFilePath, e, errorMsg);
        }
        return topoNode;
    }
    
    private void collectMapNamesFromNode(TopoNode topoNode) {
        if(StringUtils.isNotEmpty(topoNode.background)) {
            imageNames.add(topoNode.background);
        }
        for (TopoNode child : topoNode.children) {
            collectMapNamesFromNode(child);
        }
    }

    /*--------------------------- private field ---------------------------*/
    private BooleanMsgPair optResult;
    
    private final QueryBo lazyBoLoader = new QueryBo() {
        @Override
        public Collection<HmBo> load(HmBo bo) {
            if (bo == null) {
                return null;
            }
            
            if (bo instanceof MapContainerNode) {
                MapContainerNode mapContainerNode = (MapContainerNode) bo;
                if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
                    
                    for (MapNode mapNode : mapContainerNode.getChildNodes()) {
                        if (mapNode.isLeafNode()) {
                            continue;
                        }
                        MapContainerNode floor = (MapContainerNode) mapNode;
                        floor.getChildNodes().size();
                        BoMgmt.getPlannedApMgmt().loadPlannedAPs(floor,
                                floor.getChildNodes());
                        floor.getPerimeter().size();
                        floor.getWalls().size();
                        floor.getPlannedAPs().size();                        
                    }
                } else if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
                    for (MapNode child : mapContainerNode.getChildNodes()) {
                        if (child.isLeafNode()) {
                            ((MapLeafNode) child).getHiveAp().getHiveApModel();
                        }
                    }
                    mapContainerNode.getPerimeter().size();
                    mapContainerNode.getWalls().size();
                    mapContainerNode.getPlannedAPs().size();
                    
                    MapContainerNode building = (mapContainerNode).getParentMap();
                    for (MapNode mapNode : building.getChildNodes()) {
                        if (mapNode.isLeafNode()) {
                            continue; // There should not be any leaf nodes anyway
                        }
                        MapContainerNode floor = (MapContainerNode) mapNode;
                        floor.getChildNodes().size();
                        floor.getPlannedAPs().size();
                    }
                } else {
                    for (MapNode child : mapContainerNode.getChildNodes()) {
                        if (child.isLeafNode()) {
                            ((MapLeafNode) child).getHiveAp().getHiveApModel();
                        } else {
                            load(child);
                        }
                    }
                    mapContainerNode.getPerimeter().size();
                    mapContainerNode.getWalls().size();
                    mapContainerNode.getPlannedAPs().size();
                }
            }
            return null;
        }
    };

    /*--------------------------- Getter/Setter ---------------------------*/
    public QueryBo getLazyBoLoader() {
        return lazyBoLoader;
    }

    public Set<String> getBackgroundImageNames() {
        return imageNames;
    }
}
