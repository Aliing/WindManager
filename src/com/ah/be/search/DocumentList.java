/**
 * @filename			IndexOutputStream.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ah.util.Tracer;

/**
 * store index documents
 * 
 * first, store documents in buffer of memory. if the buffer exceeds 
 * the max size, dump the buffer to file
 */
public class DocumentList implements Serializable {

	private static final long	serialVersionUID	= 1L;
	
	public static final Tracer		log = new Tracer(DocumentList.class.getSimpleName());

	/*
	 * the max size of buffer list in memory.
	 */
	public static final int BUFFER_MAX_SIZE = 4096;
	
	public static final int FILE_MAX_SIZE = 200000;
	
	public static final int FILE_ARRAY_SIZE = 16;
	
	private String[] files;
	
	private List<IDocument> documents;
	
	/*
	 * an index of current active file
	 */
	private int currentFileIndex;
	
	/*
	 * a counter of documents in current active file
	 */
	private int dumpedInCurrentFile ;

	/*
	 * type of the document
	 * two types: Alarm/Event; Page content
	 */
	private short type;
	
	public DocumentList(short type) {
		documents = new LinkedList<IDocument>();
		files = new String[FILE_ARRAY_SIZE];
		this.type = type;
	}

	/**
	 * getter of documents
	 * @return the documents
	 */
	public List<IDocument> getDocuments() {
		return documents;
	}

	/**
	 * setter of documents
	 * @param documents the documents to set
	 */
	public void setDocuments(List<IDocument> documents) {
		this.documents = documents;
	}
	
	/**
	 * getter of type
	 * @return the type
	 */
	public short getType() {
		return type;
	}

	/**
	 * setter of type
	 * @param type the type to set
	 */
	public void setType(short type) {
		this.type = type;
	}

	/**
	 * add document to list
	 * 
	 * @param document		document to be added
	 * @param fileRow		index of file row
	 * @param key			key in the index map
	 * @author Joseph Chen
	 */
	public void addDocument(IDocument document, int fileRow, String key) {
		if(documents.size() >= BUFFER_MAX_SIZE) {
			/*
			 * dump into file
			 */
			
			if(dumpedInCurrentFile * BUFFER_MAX_SIZE >= FILE_MAX_SIZE) {
				/*
				 * get a new file
				 */
				int suffix = 0;
				
				for(; suffix<FILE_ARRAY_SIZE; suffix++) {
					if(files[suffix] == null) {
						break;
					}
				}
				
				currentFileIndex = suffix;
				dumpedInCurrentFile = 0;
			}
			
			if(files[currentFileIndex] == null) {
				files[currentFileIndex] = getFilePath(fileRow, key, currentFileIndex);
			}
			
			IndexUtil.save(documents, files[currentFileIndex]);
			documents.clear();
			dumpedInCurrentFile++;
		}
		
		/*
		 * to check the existence of a document will take too much time(about 10 times) 
		 * joseph chen
		 * 05/21/2009
		 */
//		if(!documents.contains(document)) {
			documents.add(document);
//		}
	}
	
	private String getFilePath(int fileRow, String key, int suffix) {
		StringBuffer buffer = new StringBuffer(SearchEngine.SEARCH_RESOURCES_PATH);
		buffer.append(fileRow).append(File.separator);
		
		File file = new File(buffer.toString());
		
		if(!file.exists()) {
			file.mkdir();
		}
		
		buffer.append(key).append("_").append(suffix);
		
		switch(this.type) {
		case SearchEngine.INDEX_TYPE_DATABASE:
			buffer.append(".bin");
			break;
		case SearchEngine.INDEX_TYPE_PAGE:
			buffer.append(".page");
			break;
		default:
			break;
		}
		
		return buffer.toString();
	}
	
	/**
	 * remove document with given tableId  and ceiling BO id.
	 * document whose BO id is lower than the ceiling will be removed.
	 * 
	 * @param tableId -
	 * @param ceiling -
	 * @return -
	 * @author Joseph Chen
	 */
	public int removeDocument(int tableId, long ceiling) {
		int removedCount = 0;
		
		/*
		 * remove in memory
		 */
		Iterator<IDocument> it = documents.iterator();
		
		while(it.hasNext()) {
			Document doc = (Document)it.next();
			
			if(doc.getTableId() == tableId 
					&& doc.getBoId() < ceiling) {
				it.remove();
				removedCount++;
			}
		}
		
		/*
		 * remove in file
		 */
		if(!hasDocumentInFile()) { // no documents in files
			return removedCount;
		}
		
		for(int i=0; i<FILE_ARRAY_SIZE; i++) {
			if(files[i] == null) {
				continue;
			}
			
			List<Object> list = IndexUtil.read(files[i]);
			
			if(list == null) {
				continue;
			}
			
			int removedInFile = 0;
			
			Iterator<Object> itList = list.iterator();
			
			while(itList.hasNext()) {
				List<Document> docList = (List<Document>) itList.next();
				Iterator<Document> itDoc = docList.iterator();
				
				while(itDoc.hasNext()) {
					Document doc = itDoc.next();
					
					if(doc.getTableId() == tableId 
							&& doc.getBoId() < ceiling) {
						itDoc.remove();
						removedCount++;
						removedInFile++;
					}
				}
				
				if(docList.isEmpty()) {
					itList.remove();
				}
			}
			
			if(removedInFile <= 0) {
				continue;
			}
			
			/*
			 * update file 
			 */
			// remove the old file
			File file = new File(files[i]);
			file.delete();
			
			if(list.isEmpty()) { // no document list
				log.info("file " + files[i] + " has been deleted.");
				files[i] = null;
			} else {
				// save the left into file
				for(Object obj : list) {
					IndexUtil.save(obj, files[i]);
				}
			}
		}
		
		return removedCount;
	}
	
	/**
	 * check if there is document in the list
	 * 
	 * @return -
	 * @author Joseph Chen
	 */
	public boolean hasDocument() {
		return documents.size() > 0 || hasDocumentInFile();
	}
	
	/**
	 * check if there is document in file
	 * 
	 * @return -
	 * @author Joseph Chen
	 */
	public boolean hasDocumentInFile() {
		for(int i=0; i<FILE_ARRAY_SIZE; i++) {
			if(files[i] != null) {
				return true;
			}
		}
		
		return false;
	}
	
	public String[] getFiles() {
		return this.files;
	}

}