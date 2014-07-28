package com.ah.util.coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.ah.be.app.DebugUtil;

/**
 * support compression API
 *@filename		AhCompressByte.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-6-11 10:20:07
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class AhCompressByte
{
	/**
	 * threshold value for compress
	 */
	public static final int	THRESHOLD	= 1200;

	/**
	 * Answer a byte array compressed in the DEFLATER format from bytes.
	 * 
	 * @param bytes
	 *            a byte array
	 * @return byte[] compressed bytes
	 * @throws IOException
	 */
	public static byte[] compress(byte[] bytes)
	{
		// Create the compressor with highest level of compression
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);

		// Give the compressor the data to compress
		compressor.setInput(bytes);
		compressor.finish();

		// Create an expandable byte array to hold the compressed data.
		// You cannot use an array that's the same size as the orginal because
		// there is no guarantee that the compressed data will be smaller than
		// the uncompressed data.
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);

		// Compress the data
		byte[] buf = new byte[1024];
		while (!compressor.finished())
		{
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		try
		{
			bos.close();
		}
		catch (IOException e)
		{
		}

		// Get the compressed data
		byte[] compressedData = bos.toByteArray();
		return compressedData;
	}

	/**
	 * Answer a byte array that has been decompressed from the DEFLATER format.
	 * 
	 * @param bytes
	 *            a byte array
	 * @return byte[] compressed bytes
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] bytes)
	{
		// Create the decompressor and give it the data to compress
		Inflater decompressor = new Inflater();
		decompressor.setInput(bytes);

		// Create an expandable byte array to hold the decompressed data
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);

		// Decompress the data
		byte[] buf = new byte[1024];

		try
		{
			while (!decompressor.finished())
			{
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
				// deal with dead circulation
				if(count == 0){
					DebugUtil.commonDebugWarn(
							"AhCompressByte.uncompress() illegal data");
					break;
				}
			}
		}
		catch (DataFormatException e)
		{
			DebugUtil.commonDebugWarn(
				"AhCompressByte.uncompress() catch DataFormatException.", e);
		}
		finally
		{
			try
			{
				bos.close();
			}
			catch (IOException e)
			{
			}
		}

		// Get the decompressed data
		byte[] decompressedData = bos.toByteArray();
		return decompressedData;
	}
}
