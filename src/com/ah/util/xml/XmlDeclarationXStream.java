package com.ah.util.xml;

import java.io.OutputStream;
import java.io.Writer;

import com.ah.util.Tracer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class XmlDeclarationXStream extends XStream {

    private static final Tracer LOG = new Tracer(XmlDeclarationXStream.class.getSimpleName());

    private String version;

    private String ecoding;

    public XmlDeclarationXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        this("1.0", "UTF-8", hierarchicalStreamDriver);
    }
    
    public XmlDeclarationXStream() {
        this("1.0", "UTF-8", new XppDriver());
    }

    public XmlDeclarationXStream(String version, String ecoding, HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(hierarchicalStreamDriver);
        
        this.version = version;
        this.ecoding = ecoding;
    }

    public String getDeclaration() {
        return "<?xml version=\"" + this.version + "\" encoding=\"" + this.ecoding + "\"?>"
                + System.getProperty("line.separator");
    }

    @Override
    public void toXML(Object arg0, OutputStream arg1) {
        try {
            String dec = this.getDeclaration();
            byte[] bytesOfDec = dec.getBytes(this.ecoding);
            arg1.write(bytesOfDec);
        } catch (Exception e) {
            LOG.error("Error when write the declaration to XML", e);
            return;
        }

        super.toXML(arg0, arg1);

    }

    @Override
    public void toXML(Object arg0, Writer arg1) {
        try {
            arg1.append(getDeclaration());
        } catch (Exception e) {
            LOG.error("Error when write the declaration to XML", e);
            return;
        }

        super.toXML(arg0, arg1);
    }
}
