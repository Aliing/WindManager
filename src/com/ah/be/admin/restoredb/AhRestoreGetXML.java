/**
 *@filename	AhRestoreGetXML.java
 *@version
 *@author		Fisher
 *@createtime	2007-05-11 10:35:27
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.admin.restoredb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ah.be.common.file.AhXMLFileRead;
import com.ah.be.common.file.XMLFileReadWriter;


/**
 * @author Fisher
 * @version V1.0.0.0
 */
public class AhRestoreGetXML
{

	private List<String[]>		m_lst_xmlFile		= new ArrayList<String[]>();
	private String[]			m_str_rowData;
	private String[]			m_str_colName;
	private final Map<String,Integer>	m_map_Column		= new Hashtable<String,Integer>();
	private int					m_int_rowCount;
	private int					m_int_colCount;
	private boolean				m_bln_readFlg		= false;
	private static String		STR_XMLFILE_PATH	= AhRestoreDBTools.HM_XML_TABLE_PATH;

	public AhRestoreGetXML()
	{
		STR_XMLFILE_PATH = AhRestoreDBTools.HM_XML_TABLE_PATH;		
	}
	
	public void convertXMLfile(String strTableName){
		BufferedReader source=null;
		BufferedWriter target=null;
		boolean blnFileFlg = true;
		String strFileName;
		String strFileNameCopy;
		int intTableCount = 1;
		File fi = new File(STR_XMLFILE_PATH + strTableName + ".xml");
		if (fi.exists()) {
			strFileName = STR_XMLFILE_PATH + strTableName + ".xml";
			strFileNameCopy = STR_XMLFILE_PATH + "convert_" + strTableName + ".xml";
			try {
				String temp=null;
	        	source = new BufferedReader(new FileReader(strFileName));
	        	target = new BufferedWriter(new FileWriter(strFileNameCopy));
	        	while((temp=source.readLine())!=null) {
	        		target.write(temp.replace("&#", "null"));
	        		target.newLine();
	        		target.flush();
	        	}
	        	while (blnFileFlg) {
					strFileName = STR_XMLFILE_PATH + strTableName + "_"
						+ intTableCount + ".xml";
					strFileNameCopy = STR_XMLFILE_PATH + "convert_" + strTableName + "_"
					+ intTableCount++ + ".xml";
					File fi1 = new File(strFileName);
					if (!fi1.exists()) {
						blnFileFlg = false;
					} else {
						temp=null;
			        	source = new BufferedReader(new FileReader(strFileName));
			        	target = new BufferedWriter(new FileWriter(strFileNameCopy));
			        	while((temp=source.readLine())!=null) {
			        		target.write(temp.replace("&#", "null"));
			        		target.newLine();
			        		target.flush();
			        	}
					}
				}
			} catch (Exception e){
				e.printStackTrace();
			} finally{
				try {
					if (source!=null){
						source.close();
					}
					if (target!=null) {
						target.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean readXMLFile(String strTableName)
	{
		if(AhRestoreDBTools.isNewFrameData)
		{
			strTableName = strTableName.toLowerCase();
		}

		Vector<String> vctFile = new Vector<String>();
		Vector<String> vctFileTmp = new Vector<String>();
		m_lst_xmlFile		= new ArrayList<String[]>();//feeling 2007-11-09 add

		boolean blnFileFlg = true;
		String strFileName;
		int intTableCount = 1;

		File fi = new File(STR_XMLFILE_PATH + strTableName + ".xml");
		if (fi.exists())
		{
			strFileName = STR_XMLFILE_PATH + strTableName + ".xml";
			vctFileTmp.add(strFileName);

			while (blnFileFlg)
			{
				strFileName = STR_XMLFILE_PATH + strTableName + "_"
					+ intTableCount++ + ".xml";
				File fi1 = new File(strFileName);
				if (!fi1.exists())
				{
					//AhRestoreDBTools.logRestoreMsg(fi1.getName()+ " is not exists");
					
					blnFileFlg = false;
				}
				else
				{
					vctFileTmp.add(strFileName);
				}
			}

//			if (vctFileTmp.size() > 3)
//			{
//				vctFile.add(vctFileTmp.get(vctFileTmp.size() - 3));
//				vctFile.add(vctFileTmp.get(vctFileTmp.size() - 2));
//				vctFile.add(vctFileTmp.get(vctFileTmp.size() - 1));
//			}
//			else
//			{
				vctFile = vctFileTmp;
//			}
		}
		else
		{
			AhRestoreDBTools.logRestoreMsg(fi.getName()+ " is not exists");

			return false;
		}

		for (int fcnt = 0; fcnt < vctFile.size(); fcnt++)
		{
			// System.out.println("test-1-fcnt:" + fcnt);

		//	XMLFileReadWriter xmlfile = new XMLFileReadWriter();
			Document doc = XMLFileReadWriter.parser(vctFile.get(fcnt));

			if (null == doc)
			{
				AhRestoreDBTools.logRestoreMsg("SAXReader parser "+fi.getName() + " file error.");
				return false;
			}
			// System.out.println("test-2-fcnt:" + fcnt);

			Element root = doc.getRootElement();
			List<?> rootLst = root.elements();

			for (int i = 0; i < rootLst.size(); i++)
			{
				List<?> rowlst = ((Element) rootLst.get(i)).elements();
				m_str_rowData = new String[rowlst.size()];

				if (i == 0 && fcnt == 0)
				{
					m_str_colName = new String[rowlst.size()];
				}

				// System.out.println("test-3-fcnt:" + fcnt);

				for (int j = 0; j < rowlst.size(); j++)
				{
					Element elm = (Element) rowlst.get(j);
					m_str_rowData[j] = elm.attributeValue("value");
					if (i == 0 && fcnt == 0)
					{
						m_str_colName[j] = elm.attributeValue("name");
						m_map_Column.put(elm.attributeValue("name").toLowerCase(), j);
					}
				}
				// System.out.println("test-4-fcnt:" + fcnt);
				m_lst_xmlFile.add(m_str_rowData);
			}
			root.clearContent();
			root = null;
			doc.clearContent();
			doc = null;
		//	xmlfile = null;
			System.gc();
			// System.out.println("test-5-fcnt:" + fcnt);
		}

		m_int_rowCount = m_lst_xmlFile.size();
		if (m_str_colName == null)
		{
			m_int_colCount = 0;
		}
		else
		{
			m_int_colCount = m_str_colName.length;
		}
		
		m_int_colCount = m_map_Column.size();
		m_bln_readFlg = true;

		return true;
	}
	
	public static boolean checkXMLFileExist(String tableName)
	{
		if(AhRestoreDBTools.isNewFrameData)
		{
			tableName = tableName.toLowerCase();
		}

		File file = new File(STR_XMLFILE_PATH + tableName + ".xml");
		return file.exists();
	}
	

	public boolean readXMLOneFile(String strTableName)
	{
		if(AhRestoreDBTools.isNewFrameData)
		{
			strTableName = strTableName.toLowerCase();
		}

		File fi = new File(STR_XMLFILE_PATH + strTableName + ".xml");
		if (!fi.exists())
		{
//			AhRestoreDBTools.logRestoreMsg("AhRestoreGetXML.readXMLOneFile() file not exists. file name = "+ fi.getName());
			return false;
		}

		try
		{
			XMLReader xr = XMLReaderFactory.createXMLReader();
			AhXMLFileRead fileRead = new AhXMLFileRead();
			xr.setContentHandler(fileRead);
			xr.parse(new InputSource(new FileReader(STR_XMLFILE_PATH
				+ strTableName + ".xml")));

			m_lst_xmlFile = fileRead.m_lst_xmlFile;
			m_str_colName = fileRead.m_str_colName;
			if(fileRead.m_str_colName != null) {
				for(int i = 0; i < fileRead.m_str_colName.length; i++) {
					m_map_Column.put(fileRead.m_str_colName[i].toLowerCase(), i);
				}
			}
		}
		catch (Exception ex)
		{
			AhRestoreDBTools.logRestoreMsg("AhRestoreGetXML.readXMLOneFile() catch exception", ex);

			return false;
		}

		m_int_rowCount = m_lst_xmlFile.size();
		if (m_str_colName == null)
		{
			m_int_colCount = 0;
		}
		else
		{
			m_int_colCount = m_str_colName.length;
		}
		m_int_colCount = m_map_Column.size();
		m_bln_readFlg = true;

		return true;
	}

	public boolean checkColExist(final String strColName)
		throws AhRestoreException
	{
		if (!m_bln_readFlg)
		{
			throw new AhRestoreException("checkColExist():file have not read. ColName:" + strColName);
		}
//		for (int i = 0; i < m_int_colCount; i++)
//		{
//			if (m_str_colName[i].equalsIgnoreCase(strColName))
//			{
//				return true;
//			}
//		}
		return null != m_map_Column.get(strColName.toLowerCase());
	}

	public int getRowCount() throws AhRestoreException
	{
		if (!m_bln_readFlg)
		{
			throw new AhRestoreException("getRowCount():file have not read");
		}
		return m_int_rowCount;
	}

	public int getColCount() throws AhRestoreException
	{
		if (!m_bln_readFlg)
		{
			throw new AhRestoreException("getColCount():file have not read");
		}
		return m_int_colCount;
	}

	public String[] getRow(final int row) throws AhRestoreException
	{
		if (!m_bln_readFlg || row >= m_int_rowCount || row < 0)
		{
			throw new AhRestoreException(
				"getRow(" + row +"):file have not read or input param row is wrong");
		}
		return m_lst_xmlFile.get(row);
	}

	public String[] getRow(final AhRestoreGetParamDTO dataParamDTO)
		throws AhRestoreException,
		AhRestoreColNotExistException
	{
		if (!m_bln_readFlg || dataParamDTO.getSize() == 0)
		{
			throw new AhRestoreException(
				"getRow(AhRestoreGetParamDTO):file have not read or input param is wrong");
		}

		boolean blnFindFlg;
		String[] strParamValue = new String[dataParamDTO.getSize()];
		int[] intParamIndex = new int[dataParamDTO.getSize()];
		int i = 0;

		for (int j = 0; j < m_int_colCount; j++)
		{
			for (int k = 0; k < dataParamDTO.getSize(); k++)
			{
				if (m_str_colName[j].equalsIgnoreCase(dataParamDTO.getFiledName(k)))
				{
					intParamIndex[i] = j;
					strParamValue[i] = dataParamDTO.getFiledValue(k);
					i++;
				}
			}
		}

		if (i < dataParamDTO.getSize())
		{
			throw new AhRestoreColNotExistException(
				"getRow(AhRestoreGetParamDTO):col does not exist or can not find row");
		}

		for (int index = 0; index < m_int_rowCount; index++)
		{
			blnFindFlg = true;
			for (int k = 0; k < i; k++)
			{
				if (!m_lst_xmlFile.get(index)[intParamIndex[k]]
					.equalsIgnoreCase(strParamValue[k]))
				{
					blnFindFlg = false;
					break;
				}
			}
			if (blnFindFlg)
			{
				return m_lst_xmlFile.get(index);
			}
		}

		return new String[0];
	}

	public String[][] getRows(final AhRestoreGetParamDTO dataParamDTO)
		throws AhRestoreException,
		AhRestoreColNotExistException
	{
		if (!m_bln_readFlg || dataParamDTO.getSize() == 0)
		{
			throw new AhRestoreException(
				"getUserPro():file have not read or input param is wrong");
		}

		String[][] strRetRows;
		Vector<String[]> vctRetRows = new Vector<String[]>();
		boolean blnFindFlg;
		String[] strParamValue = new String[dataParamDTO.getSize()];
		int[] intParamIndex = new int[dataParamDTO.getSize()];
		int i = 0;

		for (int j = 0; j < m_int_colCount; j++)
		{
			for (int k = 0; k < dataParamDTO.getSize(); k++)
			{
				if (m_str_colName[j].equalsIgnoreCase(dataParamDTO.getFiledName(k)))
				{
					intParamIndex[i] = j;
					strParamValue[i] = dataParamDTO.getFiledValue(k);
					i++;
				}
			}
		}

		for (int index = 0; index < m_int_rowCount; index++)
		{
			blnFindFlg = true;
			for (int k = 0; k < i; k++)
			{
				if (!m_lst_xmlFile.get(index)[intParamIndex[k]]
					.equalsIgnoreCase(strParamValue[k]))
				{
					blnFindFlg = false;
					break;
				}
			}
			if (blnFindFlg)
			{
				vctRetRows.add(m_lst_xmlFile.get(index));
			}
		}

		strRetRows = new String[vctRetRows.size()][];
		for (int retCount = 0; retCount < vctRetRows.size(); retCount++)
		{
			strRetRows[retCount] = vctRetRows.get(retCount);
		}

		return strRetRows;
	}

	public Vector<Map<String, String>> getUserPro(
		final AhRestoreGetParamDTO dataParamDTO,
		final String strKeyColNm,
		final String strValColNm)
		throws AhRestoreException,
		AhRestoreColNotExistException
	{
		if (!m_bln_readFlg || dataParamDTO.getSize() == 0)
		{
			throw new AhRestoreException(
				"getUserPro():file have not read or input param is wrong");
		}

		boolean blnFindFlg;
		String[] strParamValue = new String[dataParamDTO.getSize()];
		int[] intParamIndex = new int[dataParamDTO.getSize()];
		int i = 0;
		int intKeyColIndex = -1;
		int intValColIndex = -1;

		Vector<Map<String, String>> vctReturn = new Vector<Map<String, String>>();

		for (int j = 0; j < m_int_colCount; j++)
		{
			for (int k = 0; k < dataParamDTO.getSize(); k++)
			{
				if (m_str_colName[j].equalsIgnoreCase(dataParamDTO.getFiledName(k)))
				{
					intParamIndex[i] = j;
					strParamValue[i] = dataParamDTO.getFiledValue(k);
					i++;
				}
			}
			if (m_str_colName[j].equalsIgnoreCase(strKeyColNm))
			{
				intKeyColIndex = j;
			}
			if (m_str_colName[j].equalsIgnoreCase(strValColNm))
			{
				intValColIndex = j;
			}
		}

		if (intKeyColIndex == -1 || intValColIndex == -1)
		{
			throw new AhRestoreColNotExistException(
				"getUserPro:col does not exist");
		}

		for (int index = 0; index < m_int_rowCount; index++)
		{
			blnFindFlg = true;
			for (int k = 0; k < i; k++)
			{
				if (!m_lst_xmlFile.get(index)[intParamIndex[k]]
					.equalsIgnoreCase(strParamValue[k]))
				{
					blnFindFlg = false;
					break;
				}
			}
			if (blnFindFlg)
			{
				Map<String, String> map = new HashMap<String, String>();
				map.put(strKeyColNm, m_lst_xmlFile.get(index)[intKeyColIndex]);
				map.put(strValColNm, m_lst_xmlFile.get(index)[intValColIndex]);
				vctReturn.add(map);
			}
		}

		return vctReturn;
	}

	public String getColVal(final int row, final String colName)
		throws AhRestoreException,
		AhRestoreColNotExistException
	{
		int col;

		if (!m_bln_readFlg || row >= m_int_rowCount || row < 0)
		{
			throw new AhRestoreException(
				"getColVal(" + row +","+ colName + "):file have not read or input param row is wrong");
		}

//		for (int i = 0; i < m_int_colCount; i++)
//		{
//			if (m_str_colName[i].equalsIgnoreCase(colName))
//			{
//				col = i;
//				break;
//			}
//		}
//		if (col == -1)
//		{
//			throw new AhRestoreColNotExistException(
//					"getColVal(rowIndex,colName):col does not exist");
//		}
		Integer value = m_map_Column.get(colName.toLowerCase());
		// col do not exist
		if(value == null)
		{
			throw new AhRestoreColNotExistException(
				"getColVal(rowIndex,colName):col does not exist");
		}
		col = value;
		return m_lst_xmlFile.get(row)[col];

	}

	public String getColVal(final String[] rowRec, final String colName)
		throws AhRestoreException,
		AhRestoreColNotExistException
	{
		int col = -1;

		if (!m_bln_readFlg || rowRec == null
			|| rowRec.length < m_int_colCount)
		{
			throw new AhRestoreException(
				"getColVal(rowRec,colName):file have not read or input param row is wrong");
		}

		for (int i = 0; i < m_int_colCount; i++)
		{
			if (m_str_colName[i].equalsIgnoreCase(colName))
			{
				col = i;
				break;
			}
		}
		// col does not exist
		if (col == -1)
		{
			throw new AhRestoreColNotExistException(
				"getColVal(string[],string):col does not exist");
		}
		return rowRec[col];
	}

	public List<String[]> getM_lst_xmlFile() {
		return m_lst_xmlFile;
	}

	public String[] getM_str_colName() {
		return m_str_colName;
	}

}