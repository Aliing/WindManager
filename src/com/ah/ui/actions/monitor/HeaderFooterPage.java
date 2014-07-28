package com.ah.ui.actions.monitor;

import java.util.TimeZone;

import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;
import java.awt.Color;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

public class HeaderFooterPage implements PdfPageEvent {

	private static final Tracer	log			= new Tracer(HeaderFooterPage.class);

	protected Image				image;
	protected Phrase			header;
	protected Phrase			footer;

	String						baseUrl		= System.getenv("HM_ROOT") + "/";

	Font						fontText	= null;

	public HeaderFooterPage() {
		String fontPath = System.getenv("HM_ROOT") + "/resources/fonts/";
		try {
			BaseFont bfTrebuc = BaseFont.createFont(fontPath + "trebuc.ttf", BaseFont.CP1252,
					BaseFont.EMBEDDED);
			fontText = new Font(bfTrebuc, 9, Font.NORMAL, Color.BLACK);
		} catch (Exception e) {
			log.error("HeaderFooterPage() exception", e);
		}
		init(null);
	}
	
	public HeaderFooterPage(TimeZone tz) {
		String fontPath = System.getenv("HM_ROOT") + "/resources/fonts/";
		try {
			BaseFont bfTrebuc = BaseFont.createFont(fontPath + "trebuc.ttf", BaseFont.CP1252,
					BaseFont.EMBEDDED);
			fontText = new Font(bfTrebuc, 9, Font.NORMAL, Color.BLACK);
		} catch (Exception e) {
			log.error("HeaderFooterPage() exception", e);
		}
		init(tz);
	}

	private void init(TimeZone tz) {
		try {
			String logoPath = baseUrl + "/images/company_logo.png";
			image = Image.getInstance(logoPath);
			if (tz==null) {
				header = new Phrase("Prepared time: "
						+ AhDateTimeUtil.getCurrentDate(AhDateTimeUtil.DEFAULT_DATE_TIME_FORMAT),
						fontText);
			} else {
				header = new Phrase("Prepared time: "
						+ AhDateTimeUtil.getCurrentDate(AhDateTimeUtil.DEFAULT_DATE_TIME_FORMAT, tz),
						fontText);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChapter(PdfWriter writer, Document document, float paragraphPosition,
			Paragraph title) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChapterEnd(PdfWriter writer, Document document, float paragraphPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		footer = new Phrase(String.valueOf(writer.getPageNumber()), fontText);

		PdfContentByte cb = writer.getDirectContent();
		if (document.getPageNumber() > 0) {
			try {
				image.setAbsolutePosition(document.left() + 0, document.top() + 11 + 7);
				image.scaleToFit(100, 37);
				cb.addImage(image);
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header, document.right(), document
					.top() + 11 + 9, 0);

			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.right(), document
					.bottom() - 10, 0);

			cb.setColorStroke(Color.BLACK);
			cb.setLineWidth(0.1f);
			cb.moveTo(document.left(), document.top() + 12);
			cb.lineTo(document.right(), document.top() + 12);
			cb.stroke();
		}

	}

	@Override
	public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {

	}

	@Override
	public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth,
			Paragraph title) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSectionEnd(PdfWriter writer, Document document, float paragraphPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		// TODO Auto-generated method stub

	}

}
