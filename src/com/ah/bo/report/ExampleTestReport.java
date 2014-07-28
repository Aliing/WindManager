package com.ah.bo.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ah.bo.report.annotation.AhReportConfig;

@AhReportConfig(id=AhReportProperties.REPORT_EXAMPLE_FOR_TEST_LINEAR)
public class ExampleTestReport extends AhAbstractReport {
	private static final Long SERIES_JANE = 1L;
	private static final Long SERIES_JOHN = 2L;
	private static final Long SERIES_JOE = 3L;
	private static final Long SERIES_JANET = 4L;

	@Override
	public void init() {
		this.setReportTitle("Test Report");
		super.setReportSubTitle("Period: 2012-2-28 19:00:00 - 2012-2-28 20:00:00");
		super.setReportSummary("This is some summary for the example report chart.This is some summary for the example report chart."+
										"This is some summary for the example report chart.This is some summary for the example report chart.");
	}

	@Override
	protected void doCalculate() throws Exception {
		prepareCategories();
		prepareDataSeries();
	}
	
	private void prepareCategories() {
		List<Object> categories = new ArrayList<Object>();
		categories.add("Apples");
		categories.add("Oranges");
		categories.add("Pears");
		categories.add("Grapes");
		categories.add("Bananas");
		this.setCategories(categories);
	}
	
	private void prepareDataSeries() throws Exception {
		int seriesCount = (int)(Math.random()*10)%4 + 2;
		String groupName = "total_grp";
		
		if (seriesCount > 0) {
			AhSeries seriesJane = new AhLinearSeries();
			seriesJane.setId(SERIES_JANE);
			seriesJane.setName("Jane");
			seriesJane.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesJane.setObjectData(Arrays.asList(new Object[]{2, 5, 6, 2, 1}));
			seriesJane.addSummary("Total: 16");
			seriesJane.addSummary("Average: 3.2");
			seriesJane.setStackGroup(groupName);
			this.addSeries(seriesJane);
		}
		
		if (seriesCount > 1) {
			AhSeries seriesJohn = new AhLinearSeries();
			seriesJohn.setId(SERIES_JOHN);
			seriesJohn.setName("John");
			seriesJohn.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			//seriesJohn.setData(Arrays.asList(new Integer[]{5, 3, 4, 7, 2}));
			seriesJohn.addData(5);
			seriesJohn.addData(3);
			seriesJohn.addData(4);
			seriesJohn.addData(5);
			seriesJohn.addData(2);
			seriesJohn.addSummary("Total: 21");
			seriesJohn.setStackGroup(groupName);
			this.addSeries(seriesJohn);
		}
		
		if (seriesCount > 2) {
			AhSeries seriesJoe = new AhLinearSeries();
			seriesJoe.setId(SERIES_JOE);
			seriesJoe.setName("Joe");
			seriesJoe.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesJoe.setObjectData(Arrays.asList(new Object[]{3, 4, 4, 2, 5}));
			seriesJoe.addSummary("Average: 3.6");
			seriesJoe.setStackGroup(groupName);
			this.addSeries(seriesJoe);
		}
		
		if (seriesCount > 3) {
			AhSeries seriesJanet = new AhLinearSeries();
			seriesJanet.setId(SERIES_JANET);
			seriesJanet.setName("Janet");
			seriesJanet.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesJanet.setObjectData(Arrays.asList(new Object[]{3, 0, 4, 4, 3}));
			seriesJanet.addSummary("It's a mark only.");
			seriesJanet.setStackGroup(groupName);
			this.addSeries(seriesJanet);
		}
	}

}
