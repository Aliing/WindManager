package com.ah.bo.report;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.VerticalAlignment;

import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.ui.actions.monitor.HeaderFooterPage;
import java.awt.Graphics2D;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class AhReportFreeChart {

	private static final String fontPath = FontFactory.TIMES_ROMAN;
	
    public static JFreeChart getPieChartImage() {
		org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);
       	DefaultPieDataset dataset = new DefaultPieDataset();
    	dataset.setValue("WPA/WPA2 (Personal)", 1);
    	dataset.setValue("Private PSK", 2);
    	dataset.setValue("WPA/WPA2 802.1X (Enterprise)", 3);
    	dataset.setValue("WEP", 4);
    	dataset.setValue("Open", 5);
    	JFreeChart chart =   ChartFactory.createPieChart(
    			"Station Access Security", dataset, true,
				true, false);
    	chart.setBackgroundPaint(Color.white); 

    	PiePlot pieplot = (PiePlot)chart.getPlot();
    	pieplot.setBackgroundPaint(Color.white);
		pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} : {2}"));
		pieplot.setLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
		pieplot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} : {1}"));
		chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.LEFT);
		chart.getLegend().setBorder(0,0,0,0);
		chart.getLegend().setVerticalAlignment(org.jfree.ui.VerticalAlignment.TOP);
		for (int i=0; i<AhReportProperties.SERIES_ALL_COLORS.length && i< dataset.getKeys().size(); i++) {
			pieplot.setSectionPaint(dataset.getKey(i), Color.getColor(AhReportProperties.SERIES_ALL_COLORS[i]));
		}
		return chart;
    }
    
    public static JFreeChart getLineChartImage() {
  		org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);
  		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
  		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		TimeSeries s1 = new TimeSeries("L&G European \n Index Trust");
		long time = System.currentTimeMillis();
		long time1 = time - 3600000 * 10;
		for (int i=0; i<10 ; i++) {
			s1.add(new Minute(new Date(time1 + i*3600000)), i*100 + i);
		}
		
		TimeSeries s2 = new TimeSeries("L&G China \n Index Trust");
		long time2 = time - 3600000 * 10;
		for (int i=0; i<10 ; i++) {
			s2.add(new Minute(new Date(time2 + i*3600000)), i*200 + i);
		}
  		
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);

		JFreeChart chart = ChartFactory.createTimeSeriesChart(
		           "Legal & General Unit Trust Prices",
		           "Date", "Price Per Unit",
		           dataset,
		           true,
		           true,
		           false
		       );
		chart.setBackgroundPaint(Color.white); 
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);
		
		for (int i=0; i<AhReportProperties.SERIES_ALL_COLORS.length && i< dataset.getSeriesCount(); i++) {
			plot.getRenderer().setSeriesPaint(i,Color.getColor(AhReportProperties.SERIES_ALL_COLORS[i]));
		}
		// X
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(sdf);
        axis.setLabelPaint(Color.BLACK);
        axis.setLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
        axis.setTickLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
		// Y
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelPaint(Color.BLACK);
		rangeAxis.setLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
		rangeAxis.setTickLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
		chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.LEFT);
		chart.getLegend().setBorder(0,0,0,0);
		chart.getLegend().setVerticalAlignment(VerticalAlignment.TOP);
        return chart;
      }
    
    public static JFreeChart getStackedBarChart(){
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    	dataset.addValue(100, "plan", "china");
    	dataset.addValue(200, "actu", "china");
//    	dataset.addValue(120, "plan", "japan");
//    	dataset.addValue(220, "actu", "japan");
//    	dataset.addValue(50, "plan", "US");
//    	dataset.addValue(20, "actu", "US");
    	
    	
        JFreeChart chart = ChartFactory.createStackedBarChart(
          "StackedBarChart", "Categary", "Value",
           dataset,
            PlotOrientation.HORIZONTAL,
            true, true, false);
        
        chart.setBackgroundPaint(Color.white); 
        CategoryPlot plot = (CategoryPlot)chart.getPlot();   
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.GRAY);
		plot.setRangeGridlinePaint(Color.GRAY);

		BarRenderer barrenderer = (BarRenderer)plot.getRenderer();
	    barrenderer.setMaximumBarWidth(0.2);
	    barrenderer.setShadowVisible(false);
		for (int i=0; i<AhReportProperties.SERIES_ALL_COLORS.length && i< dataset.getColumnKeys().size(); i++) {
			barrenderer.setSeriesPaint(i,Color.getColor(AhReportProperties.SERIES_ALL_COLORS[i]));
		}
		
		// X
		CategoryAxis axis = plot.getDomainAxis();
		axis.setLabelPaint(Color.BLACK);
        axis.setLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
        axis.setTickLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
		
    	// Y
 		ValueAxis rangeAxis = plot.getRangeAxis();
 		rangeAxis.setLabelPaint(Color.BLACK);
 		rangeAxis.setLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
 		rangeAxis.setTickLabelFont(new java.awt.Font(fontPath,Font.NORMAL,8));
        
		chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.LEFT);
		chart.getLegend().setBorder(0,0,0,0);
		chart.getLegend().setVerticalAlignment(VerticalAlignment.TOP);   
        
        return chart;
      }
    
	/**
	 * @param args -
	 * @throws DocumentException -
	 * @throws FileNotFoundException -
	 */
	public static void main(String[] args) throws FileNotFoundException, DocumentException {
		Document document = new Document(PageSize.A4,40,40,72,72);
		String currentDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ "home";
		File tmpFileDir = new File(currentDir);
		if (!tmpFileDir.exists()) {
			tmpFileDir.mkdirs();
		}
				
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream( "/home/tmp/currentReportData_test.pdf"));
		
		writer.setPageEvent(new HeaderFooterPage());
		document.open();
		
		JFreeChart chart = getPieChartImage();
		PdfTemplate tp = PdfTemplate.createTemplate(writer, document.right()-40, 200);
		//PdfGraphics2D g2d = new PdfGraphics2D(tp, document.right()-40, 200, true);
		Graphics2D g2d = tp.createGraphicsShapes(document.right()-40, 200);
		Rectangle2D r2d = new Rectangle2D.Double(0, 0, document.right()-40, 200);
		chart.draw(g2d, r2d);
		g2d.dispose();
		tp.sanityCheck();
		Image bbbb = Image.getInstance(tp);
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		PdfPCell cell = new PdfPCell();
		cell.setImage(bbbb);
		table.addCell(cell);
		document.add(table);
		
		chart = getLineChartImage();
		tp = PdfTemplate.createTemplate(writer, document.right()-40, 200);
		//g2d = new PdfGraphics2D(tp, document.right()-40, 200, true);
		g2d = tp.createGraphicsShapes(document.right()-40, 200);
		r2d = new Rectangle2D.Double(0, 0, document.right()-40, 200);
		chart.draw(g2d, r2d);
		g2d.dispose();
		tp.sanityCheck();
		bbbb = Image.getInstance(tp);
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		cell = new PdfPCell();
		cell.setImage(bbbb);
		table.addCell(cell);
		document.add(table);
		
		chart = getStackedBarChart();
		tp = PdfTemplate.createTemplate(writer, document.right()-40, 200);
		//g2d = new PdfGraphics2D(tp, document.right()-40, 200, true);
		g2d = tp.createGraphicsShapes(document.right()-40, 200);
		r2d = new Rectangle2D.Double(0, 0, document.right()-40, 200);
		chart.draw(g2d, r2d);
		g2d.dispose();
		tp.sanityCheck();
		bbbb = Image.getInstance(tp);
		table = new PdfPTable(1);
		table.setWidthPercentage(100);
		cell = new PdfPCell();
		cell.setImage(bbbb);
		table.addCell(cell);
		document.add(table);
		
		document.close();
	}

}