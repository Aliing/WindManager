package com.ah.be.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class ValiDateImage {
	private ByteArrayInputStream image;
	private String str;
	private char mapTable[]={  
			'A','B','C','D','E','F',  
			'G','H','J','K','M','P',  
			'Q','R','S','T','U','V',  
			'W','X','Y','Z','2','3',  
			'4','5','6','7','8','9'};
	private ValiDateImage() {
		init();
	}

	public static ValiDateImage Instance() {
		return new ValiDateImage();
	}

	public ByteArrayInputStream getImage() {
		return this.image;
	}

	public String getString() {
		return this.str;
	}

	private void init() {

		int width = 85, height = 20;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		Random random = new Random();

		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);

		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));

		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		String sRand = "";
		for (int i = 0; i < 4; i++) {
			
			String rand = String.valueOf(mapTable[random.nextInt(30)]);
			sRand += rand;

			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random
					.nextInt(110)));

			g.drawString(rand, 13 * i + 6, 16);
		}

		this.str = sRand;

		g.dispose();
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ImageOutputStream imageOut = ImageIO.createImageOutputStream(output);
			ImageIO.write(image, "JPEG", imageOut);
			imageOut.close();
			input = new ByteArrayInputStream(output.toByteArray());
		} catch (Exception e) {
			System.out.println("generate code picture error!" + e.toString());
		}

		this.image = input;
	}

	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}