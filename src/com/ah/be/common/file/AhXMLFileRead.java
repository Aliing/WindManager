package com.ah.be.common.file;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AhXMLFileRead extends DefaultHandler {

	public final List<String[]>	m_lst_xmlFile	= new ArrayList<String[]>();

	private String[]			m_str_rowData;
	private List<String>		m_lst_rowData;
	public String[]				m_str_colName;
	private List<String>		m_lst_colName;
	private boolean				blnFindFlg;
	private int					intRowCount;
	private int					inttmpCount;

	public void startDocument() throws SAXException
	{
	}

	public void endDocument() throws SAXException
	{
	}

	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attributes) throws SAXException
	{
		if (localName.equals("row"))
		{
			if (!blnFindFlg)
			{
				intRowCount = 0;
				m_lst_rowData = new ArrayList<String>();
				m_lst_colName = new ArrayList<String>();
			}
			else
			{
				m_str_rowData = new String[intRowCount];
			}
			inttmpCount = 0;
		}

		if (localName.equals("field"))
		{
			if (!blnFindFlg)
			{
				intRowCount++;
				for (int i = 0; i < attributes.getLength(); i++)
				{
					if (attributes.getLocalName(i).equals("name"))
					{
						m_lst_colName.add(attributes.getValue(i));
					}
					if (attributes.getLocalName(i).equals("value"))
					{
						m_lst_rowData.add(attributes.getValue(i));
					}
				}
			}
			else
			{
				for (int i = 0; i < attributes.getLength(); i++)
				{
					if (attributes.getLocalName(i).equals("value"))
					{
						m_str_rowData[inttmpCount] = attributes.getValue(i);
					}
				}
				inttmpCount++;
			}
		}
	}

	public void endElement(String uri, String localName, String qName)
		throws SAXException
	{
		if (localName.equals("row"))
		{
			if (!blnFindFlg)
			{
				m_str_rowData = new String[intRowCount];
				m_str_colName = new String[intRowCount];
				for (int i = 0; i < intRowCount; i++)
				{
					m_str_rowData[i] = m_lst_rowData.get(i);
					m_str_colName[i] = m_lst_colName.get(i);
				}
			}
			m_lst_xmlFile.add(m_str_rowData);
			blnFindFlg = true;
		}
	}

	public void characters(char[] ch, int start, int length)
		throws SAXException
	{
	}

}