package com.ah.util.compress.tar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import com.ah.util.Tracer;

public class TarArchive {

    private static final Tracer LOG = new Tracer(TarArchive.class.getSimpleName());

    private final static int BUFFER = 2048;

    private final static String BASE_PATH = "";
    private final static String SEPERATOR = "/";
    public final static String SUFFIX_TAR = ".tar";
    public final static String SUFFIX_ZIP = ".gz";
    public final static String SUFFIX_TAR_ZIP = ".tar.gz";

    public boolean create(String srcPath, String destPath) {
        try (FileOutputStream myOutputStream = new FileOutputStream(destPath);
                TarArchiveOutputStream tarOut = new TarArchiveOutputStream(myOutputStream)) {
            File file = new File(srcPath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    tarDir(srcPath, tarOut, BASE_PATH);
                } else if (file.isFile()) {
                    tarFile(srcPath, tarOut, BASE_PATH);
                }
                LOG.debug("compress file:"+srcPath +" to " + destPath + " successfully.");
                return true;
            }

        } catch (IOException e) {
            LOG.error("Error occurs when compress " + srcPath + " to " + destPath, e);
        }
        return false;
    }

    public boolean extract(String srcPath, String destPath) {
        try (FileInputStream myInputStream = new FileInputStream(srcPath);
                TarArchiveInputStream tarInput = new TarArchiveInputStream(myInputStream)) {
            File file = new File(srcPath);
            if (file.exists() && file.isFile()) {
                deTarFile(destPath, tarInput);
                LOG.debug("decompress file:"+srcPath +" to " + destPath + " successfully.");
                return true;
            }
        } catch (IOException e) {
            LOG.error("Error occurs when decompress " + srcPath + " to " + destPath, e);
        }
        return false;
    }

    // ----------------Private methods------------------//
    private void deTarFile(String destPath, TarArchiveInputStream tarInput) throws IOException {
        File folder = new File(destPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        TarArchiveEntry entry = null;
        while ((entry = tarInput.getNextTarEntry()) != null) {
            String filePath = folder.getAbsolutePath() + File.separator + entry.getName();
            File file = new File(filePath);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (file.isDirectory()) {
                file.mkdir();
            } else {
                try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(
                        file))) {
                    int count;
                    byte data[] = new byte[BUFFER];
                    while ((count = tarInput.read(data, 0, BUFFER)) != -1) {
                        output.write(data, 0, count);
                    }
                } catch (IOException e) {
                    LOG.error("Error occurs when untar the file: " + file.getName() + "to " + destPath, e);
                }
            }
        }
    }

    private void tarDir(String srcPath, TarArchiveOutputStream tarOut, String basePath)
            throws IOException {
        File folder = new File(srcPath);
        File[] files = folder.listFiles();
        final String dirPath = basePath + folder.getName() + SEPERATOR;
        if (null == files) {
            TarArchiveEntry entry = new TarArchiveEntry(dirPath);
            tarOut.putArchiveEntry(entry);
            tarOut.closeArchiveEntry();
        } else {
            for (File file : files) {
                if (file.isDirectory()) {
                    // pass the folder to file
                    tarDir(file.getAbsolutePath(), tarOut, basePath + file.getName() + SEPERATOR);
                } else {
                    // use the BasePath to ignore the fist directory
                    tarFile(file.getAbsolutePath(), tarOut, basePath);
                }
            }
        }
    }

    private void tarFile(String srcPath, TarArchiveOutputStream tarOut, String basePath)
            throws IOException {
        LOG.debug("start tar file=" + srcPath);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcPath))) {
            File file = new File(srcPath);
            LOG.debug("|__tar parent file=" + file.getParentFile().getName());
            TarArchiveEntry entry = new TarArchiveEntry(basePath + file.getName());
            entry.setSize(file.length());
            tarOut.putArchiveEntry(entry);

            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                tarOut.write(data, 0, count);
            }

            tarOut.flush();
            tarOut.closeArchiveEntry();
        } catch (Exception e) {
            LOG.error("Error occurs when tar the file: " + srcPath, e);
        }
    }
    
    private void compressTarFileToGzip(String targzipFilePath){
    	try {
			FileOutputStream gzFile=new FileOutputStream(targzipFilePath + SUFFIX_ZIP);   
			GZIPOutputStream gzout=new GZIPOutputStream(gzFile);   
			FileInputStream tarin=new FileInputStream(targzipFilePath);  
			int len;
			byte data[] = new byte[BUFFER];
			
			while ((len=tarin.read(data)) != -1)   
			{   
			    gzout.write(data,0,len);   
			}
			
			gzout.close();   
			gzFile.close();   
			tarin.close();
		} catch (Exception e) {
			 LOG.error("Error occurs when gzip the tar file: " + targzipFilePath, e);
		} 
    }
    
    private void deleteTarFile(String targzipFilePath){
    	File file = new File(targzipFilePath);  
        if (file.isFile() && file.exists()) {  
            file.delete();  
        }  
    }
    
    
    public void createTarZip(String srcPath, String destPath){
    	create(srcPath, destPath);
    	compressTarFileToGzip(destPath);
    	deleteTarFile(destPath);
    }
}
