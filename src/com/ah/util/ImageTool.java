package com.ah.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.image4j.codec.ico.ICODecoder;

/**
 * 
 * Description: get the image width and height
 * ImageTool.java Create on Jul 1, 2013 10:04:25 PM
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2013 Aerohive Networks Inc. All Rights Reserved.
 */
public class ImageTool {
	/**
	 * 
	 * Description: get the width of image
	 * Date:Jul 1, 2013
	 * @author Shaohua Zhou
	 * @param file image
	 * @return int width
	 */
	public static int getImageWidth(File file){
		InputStream is = null;
		BufferedImage src = null;
		int ret = -1; 
		try{
			is = new FileInputStream(file);
			src = javax.imageio.ImageIO.read(is);
			ret = src.getWidth();
			is.close();
		}catch(Exception e){
			e.printStackTrace();
			return ret;
		}
		return ret;
	}
	/**
	 * Description: get the height of image
	 * Date:Jul 1, 2013
	 * @author Shaohua Zhou
	 * @param file image
	 * @return int height
	 */
	public static int getImageHeight(File file){
		InputStream is = null;
		BufferedImage src = null;
		int ret = -1;
		try{
			is = new FileInputStream(file);
			src = javax.imageio.ImageIO.read(is);
			ret = src.getHeight();
			is.close();
		} catch(Exception e){
			e.printStackTrace();
			return ret;
		}
		return ret;
	}
	/**
	 * Description: get the icon image file width
	 * Date:Jul 27, 2013
	 * @author Shaohua Zhou
	 * @param icoFile
	 * @return int
	 */
	public static int getIconImageWidth(File icoFile){
		int ret = -1;
		try {
			List<BufferedImage> list = ICODecoder.read(icoFile);
			BufferedImage src = null;
			if(null != list && !list.isEmpty()){
				src = list.get(0);
				ret = src.getWidth();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ret;
		}
		return ret;
	}
	/**
	 * Description: get the icon image file height
	 * Date:Jul 27, 2013
	 * @author Shaohua Zhou
	 * @param icoFile
	 * @return
	 * @return int
	 */
	public static int getIconImageHeight(File icoFile){
		int ret = -1;
		try {
			List<BufferedImage> list = ICODecoder.read(icoFile);
			BufferedImage src = null;
			if(null != list && !list.isEmpty()){
				src = list.get(0);
				ret = src.getHeight();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ret;
		}
		return ret;
	}
	
}
