package com.ah.bo;

import java.sql.Timestamp;

/*
 * @author Chris Scheers
 */

public interface HmBo extends HmBoBase {

	// Unique ID used by paging
	Long getId();

	//for domain clone
	void setId(Long id);

	// For versioning
	Timestamp getVersion();

	void setVersion(Timestamp version);

	// For multi page selection
	boolean isSelected();

	//for clone use
	void setSelected(boolean selected);

}