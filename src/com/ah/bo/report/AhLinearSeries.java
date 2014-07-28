package com.ah.bo.report;


/**
 * 
 * @date Mar 2, 2012
 * @author wx
 *
 * used for: common series used except datetime type of series
 */
public class AhLinearSeries extends AhSeries {
	
	@Override
	public void addData(Object value) {
		if (value == null) {
			return;
		}
		super.addData(value);
	}
	
	/**
	 * only use this method while you really need to mark a point with name&value.
	 * if you have set <b>categories</b>, you should use <b>addData(Object value)</b>
	 */
	public void addData(Object name, Object value) {
		super.addData(name, value);
	}
}
