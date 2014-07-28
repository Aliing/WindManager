package com.ah.ui.actions.monitor;

import java.io.File;
import java.util.TimeZone;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.util.HmContextListener;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;
import java.awt.Color;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class NewHeaderFooterPage extends HeaderFooterPage {
	private static final Tracer	log			= new Tracer(NewHeaderFooterPage.class);
	
	private String logoPath;
	
	private HmDomain owner;
	private AhDashboard dashboard;
	
	private static final String baseUrl=HmContextListener.context.getRealPath(ReportSettingAction.reportRootDir);
					
	 
	public NewHeaderFooterPage()
	{
		super();
	}
	
	public NewHeaderFooterPage(TimeZone tz,HmDomain owner,AhDashboard dashboard)
	{
		super();
		init(tz,owner,dashboard);
	}
	
	private void init(TimeZone tz,HmDomain owner,AhDashboard dashboard) {
		try {
			setOwner(owner);
			setDashboard(dashboard);
			if(checkAndSetLogoPath())
			{
				image = Image.getInstance(logoPath);
			}
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
	public void onEndPage(PdfWriter writer, Document document) {
//		footer = new Phrase(String.valueOf(writer.getPageNumber()), fontText);
		String pdfFooter=String.valueOf(writer.getPageNumber());
		footer = new Phrase(pdfFooter, fontText);
		PdfContentByte cb = writer.getDirectContent();
		if (document.getPageNumber() > 0) {
			try {
				if(image!=null)
				{
					image.setAbsolutePosition(document.left() + 0, document.top() + 11 + 7);
					image.scaleToFit(100, 37);
					cb.addImage(image);
				}
				else
				{
					log.error("NewHeaderFooterPage", "Report logo image is not exsit!");
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header, document.right(), document
					.top() + 11 + 9, 0);
			
			if(dashboard!=null&&dashboard.getPdfHeader()!=null)
			{
				Phrase reportHeader= new Phrase(dashboard.getPdfHeader(), fontText);
				ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, reportHeader, document.right(), document
						.top() + 11, 0);
			}
			
			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.right(), document
					.bottom() - 10, 0);
			
			if(dashboard!=null&&dashboard.getPdfFooter()!=null)
			{
				Phrase reportFooter= new Phrase(dashboard.getPdfFooter(), fontText);
				ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, reportFooter, document.right(), document
						.bottom() - 10-9, 0);
			}

			cb.setColorStroke(Color.BLACK);
			cb.setLineWidth(0.1f);
			cb.moveTo(document.left(), document.top() + 8);
			cb.lineTo(document.right(), document.top() + 8);
			cb.stroke();
		}

	}
	
	private boolean checkAndSetLogoPath()
	{
		if(owner!=null&&owner.getDomainName()!=null&&!"".equals(owner.getDomainName()))
		{
			String ownerName=owner.getDomainName();
			File imageDir=new File(baseUrl
												+File.separator+ownerName
												+ File.separator+"vhm-report"
												+File.separator+"images");
			if(!imageDir.exists())
			{
				imageDir.mkdirs();
			}
			File logo=new File(imageDir.getAbsolutePath()+File.separator+ReportSettingAction.logoFileName);
			
			
			if(logo.exists())
			{
				setLogoPath(logo.getAbsolutePath());
				return true;
			}
		}
		else{
			log.error("NewHeaderFooterPage,checkAndSetLogoPath", "domain name is empty or null");
		}
		return false;
	}


	public String getLogoPath() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public AhDashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(AhDashboard dashboard) {
		this.dashboard = dashboard;
	}
	
}
