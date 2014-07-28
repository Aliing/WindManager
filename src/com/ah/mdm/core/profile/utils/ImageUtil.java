package com.ah.mdm.core.profile.utils;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageUtil
{

	/**
	 * The method is to handle the image's width and height. the image is
	 * generated according to the new width and new height.
	 * 
	 * @param imageInputStream
	 *            image file inputstream
	 * @param nw
	 *            the new width
	 * @param nh
	 *            the new height
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] resizeImage(InputStream imageInputStream, int nw, int nh) throws IOException
	{
		AffineTransform transform = new AffineTransform();
		BufferedImage bufferedImage = ImageIO.read(imageInputStream);
		if (bufferedImage == null)
		{
			return null;
		}
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();

		double sx = (double) nw / w;
		double sy = (double) nh / h;

		transform.setToScale(sx, sy);

		BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
		bid.getGraphics().drawImage(bufferedImage.getScaledInstance(nw, nh, Image.SCALE_FAST), 0, 0, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bid, "png", baos);
		byte[] imageByte = baos.toByteArray();

		return imageByte;
	}

	public static byte[] resizeImage(byte[] oldImg, int w, int h) throws IOException
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(oldImg);
		byte[] newImg = resizeImage(stream, w, h);
		return newImg;
	}
}
