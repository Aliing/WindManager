package com.ah.be.common.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ah.util.Tracer;

public class XMLFileReadWriter 
{
	private static final Tracer log = new Tracer(XMLFileReadWriter.class.getSimpleName());
	
	public static Document createDocument() 
	{
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );

        @SuppressWarnings("unused")
		Element author1 = root.addElement( "author" )
            .addAttribute( "name", "James" )
            .addAttribute( "location", "UK" )
            .addText( "James Strachan" );

        @SuppressWarnings("unused")
		Element author2 = root.addElement( "author" )
            .addAttribute( "name", "Bob" )
            .addAttribute( "location", "US" )
            .addText( "Bob McWhirter" );

        return document;
    }
	public static Document parser(URL url) throws DocumentException
	{
        SAXReader reader = new SAXReader();
        return reader.read(url);
    }
    public static Document parser(String url)  
    {
    	if(url==null || url.trim().equals(""))
    		return null;
    	Document document=null;
    	try
    	{
            SAXReader reader = new SAXReader();
            document = reader.read(url);
    	}
    	catch(DocumentException e)
    	{
    		e.printStackTrace();
    	}
        return document;
    }

    public static Document parser(File file) throws DocumentException {
        SAXReader reader = new SAXReader();

        return reader.read(file);
    }

	public static List<?> readXML2List(String url)
    {
    	List<?> lst=null;
    	if(url==null || url.trim().equals(""))
    		return null;
    	try
    	{
    		Document doc=parser(url);
    		if(doc!=null)
    			lst=readXML2List(doc);    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return lst;
    }
    public static List<?> readXML2List(Document doc)
    {
    	List<?> lst=null;
    	if(doc==null)
    		return null;
    	try
    	{
    		lst=doc.getRootElement().elements();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return lst;
    }
    public static void write(Document document,String url) throws IOException 
    {
        // lets write to a file
    	if(url==null || url.trim().equals(""))
    		return;
    	OutputFormat format=new OutputFormat();
    	format.setEncoding("ISO-8859-1");
    	write(document,url,format);
    }
    public static void write(Document document,String url,OutputFormat format) throws IOException 
    {
        // lets write to a file
    	if(url==null || url.trim().equals(""))
    		return;
        XMLWriter writer = new XMLWriter(
            new FileWriter( url ),format
        );
        writer.write( document );
        writer.close();
    }
    
    public static boolean isValidXMLFile(File file) {
    	try {
    		SAXReader reader = new SAXReader();
    		reader.read(file);
    		return true;
    	} catch (Exception e) {
    		log.error("A bad format XML file has been submitted.");
    		return false;
    	}
    }
}
