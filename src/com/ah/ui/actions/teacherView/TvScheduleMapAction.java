package com.ah.ui.actions.teacherView;

/*
 * @author Chris Scheers
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.teacherView.TvScheduleMap;
import com.ah.bo.teacherView.TvScheduleMapPeriodTime;
import com.ah.bo.teacherView.TvScheduleMapWeekDay;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;

public class TvScheduleMapAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		try {
			if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				saveGuiData();
				if (getDataSource().getLstPeriod().size()==0 || getDataSource().getLstWeek().size()==0) {
					addActionError(MgrUtil.getUserMessage("action.error.period.cannot.be.blank"));
					return INPUT;
				}
				if ("create".equals(operation)) {
					id=createBo(dataSource);
					setSessionDataSource(findBoById(TvScheduleMap.class,id,this));
					return INPUT;
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				if (dataSource == null) {
					return INPUT;
				}
				saveGuiData();
				if (getDataSource().getLstPeriod().size()==0 || getDataSource().getLstWeek().size()==0) {
					addActionError(MgrUtil.getUserMessage("action.error.period.cannot.be.blank"));
					return INPUT;
				}
				if ("update".equals(operation)) {
					setSessionDataSource(updateBo(dataSource));
					return INPUT;
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("addPeriod".equals(operation)) {
				if (dataSource == null) {
					return INPUT;
				} else {
					addSinglePeriod();
					return INPUT;
				}
			} else if ("addWeek".equals(operation)) {
				if (dataSource == null) {
					return INPUT;
				} else {
					addSingleWeek();
					return INPUT;
				}
			} else if ("removePeriod".equals(operation)
					|| "removePeriodNone".equals(operation)) {
					hideCreateItem = "removePeriodNone".equals(operation) ? "" : "none";
					hideNewButton = "removePeriodNone".equals(operation) ? "none" : "";
				if (dataSource == null) {
					return INPUT;
				} else {
					removeSelectedPeriod();
					return INPUT;
				}
			} else if ("removeWeek".equals(operation)
					|| "removeWeekNone".equals(operation)) {
					hideCreateItem2 = "removeWeekNone".equals(operation) ? "" : "none";
					hideNewButton2 = "removeWeekNone".equals(operation) ? "none" : "";
				if (dataSource == null) {
					return INPUT;
				} else {
					removeSelectedWeek();
					return INPUT;
				}
			} else {
				List<?> lstRet = QueryUtil.executeQuery(TvScheduleMap.class, null, null, getDomainId());
				if (lstRet.size()>0) {
					setSessionDataSource(findBoById(TvScheduleMap.class, ((TvScheduleMap)lstRet.get(0)).getId(),this)); 
					setId(getDataSource().getId());
				} else {
					setSessionDataSource(new TvScheduleMap()); 
				}
				return INPUT;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_TV_SCHEDULEMAP);
		setDataSource(TvScheduleMap.class);
	}
	public TvScheduleMap getDataSource() {
		return (TvScheduleMap) dataSource;
	}

	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'", "\\'");
	}

	protected void saveGuiData() throws Exception{

	}

//	private String addWeekDay="Monday";
	private String addSHour="";
	private String addSMin="";
	private String addEHour="";
	private String addEMin="";

	private Collection<String> scheduleIndices;
	private Collection<String> weekIndices;
	private int addSection=1;
	private String addSymbol="A";
	
	private boolean addSun;
	private boolean addMon;
	private boolean addTue;
	private boolean addWed;
	private boolean addThu;
	private boolean addFri;
	private boolean addSat;
	private boolean addMonFri;
	
	private String arrayWeekSun[];
	private String arrayWeekMon[];
	private String arrayWeekTue[];
	private String arrayWeekWed[];
	private String arrayWeekThu[];
	private String arrayWeekSat[];
	
	private String arraySMin[];
	private String arraySHour[];
	private String arrayEMin[];
	private String arrayEHour[];
	
	private String hideNewButton = "";
	private String hideNewButton2 = "";
	private String hideCreateItem = "none";
	private String hideCreateItem2 = "none";
	public String getHideCreateItem()
	{
		return hideCreateItem;
	}

	public String getHideNewButton()
	{
		return hideNewButton;
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return "";
		}
		return "disabled";
	}
	
	public int getGridCount() {
		return getDataSource().getLstPeriod().size() == 0 ? 3 : 0;
	}
	
	public int getGridWeekCount() {
		return getDataSource().getLstWeek().size() == 0 ? 3 : 0;
	}

	private static final DecimalFormat df = new DecimalFormat("00");
	public List<TextItem> getLstHour(){
		List<TextItem> retLst = new ArrayList<TextItem>();
		for(int i=0; i<24; i++){
			retLst.add(new TextItem(df.format(i),df.format(i)));
		}
		return retLst;
	}
	
	public List<TextItem> getLstMin(){
		List<TextItem> retLst = new ArrayList<TextItem>();
		for(int i=0; i<60; i=i+5){
			retLst.add(new TextItem(df.format(i),df.format(i)));
		}
		return retLst;
	}
	
	protected String getWeekDayString(){
		StringBuffer temBuf = new StringBuffer(7);
		temBuf.append(addSun?"1":"0");
		temBuf.append(addMon?"1":"0");
		temBuf.append(addTue?"1":"0");
		temBuf.append(addWed?"1":"0");
		temBuf.append(addThu?"1":"0");
		temBuf.append(addFri?"1":"0");
		temBuf.append(addSat?"1":"0");
		return temBuf.toString();
	}
	
	protected boolean addSinglePeriod() throws Exception {
		for(TvScheduleMapPeriodTime single : getDataSource().getLstPeriod()){
			if (single.getSection()==addSection){
				addActionError(MgrUtil.getUserMessage("action.error.add.single.period"));
				return false;
			}
			if ((single.getStartTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getStartTime().compareTo(addEHour + ":" + addEMin)<=0)
				|| (single.getEndTime().compareTo(addSHour + ":" + addSMin)>=0 && single.getEndTime().compareTo(addEHour + ":" + addEMin)<=0)
				|| ((addSHour + ":" + addSMin).compareTo(single.getStartTime())>=0 && (addSHour + ":" + addSMin).compareTo(single.getEndTime())<=0)
				|| ((addEHour + ":" + addEMin).compareTo(single.getStartTime())>=0 && (addEHour + ":" + addEMin).compareTo(single.getEndTime())<=0)){
				hideCreateItem = "";
				hideNewButton = "none";
				addActionError(MgrUtil.getUserMessage("action.error.add.time.period"));
				return false;
			}
		}

		TvScheduleMapPeriodTime tvSche=new TvScheduleMapPeriodTime();
		tvSche.setSection(addSection);
		tvSche.setStartTime(addSHour + ":" + addSMin);
		tvSche.setEndTime(addEHour + ":" + addEMin);
		getDataSource().getLstPeriod().add(tvSche);
		addSHour="";
		addSMin="";
		addEHour="";
		addEMin="";
		addSection=1;
		Collections.sort(getDataSource().getLstPeriod(), new Comparator<TvScheduleMapPeriodTime>() {
			@Override
			public int compare(TvScheduleMapPeriodTime o1, TvScheduleMapPeriodTime o2) {
				return o1.getSection()-o2.getSection();
			}
		});
		return true;
	}
	
	protected boolean addSingleWeek() throws Exception {
		String weekDayString = getWeekDayString();
		for(TvScheduleMapWeekDay single : getDataSource().getLstWeek()){
			if (single.getSymbol().equalsIgnoreCase(addSymbol.trim())){
				addActionError(MgrUtil.getUserMessage("action.error.add.single.week"));
				return false;
			}
		}

		TvScheduleMapWeekDay tvSche=new TvScheduleMapWeekDay();
		tvSche.setSymbol(addSymbol.trim().toUpperCase());
		tvSche.setWeekday(weekDayString);
		getDataSource().getLstWeek().add(tvSche);
		addSun=false;
		addMon=false;
		addTue=false;
		addWed=false;
		addThu=false;
		addFri=false;
		addSat=false;
		addMonFri=false;
		addSymbol="A";
		Collections.sort(getDataSource().getLstWeek(), new Comparator<TvScheduleMapWeekDay>() {
			@Override
			public int compare(TvScheduleMapWeekDay o1, TvScheduleMapWeekDay o2) {
				if (o2.getSymbol().length()==o1.getSymbol().length()) {
					return o1.getSymbol().compareTo(o2.getSymbol());
				} else {
					return o1.getSymbol().length()-o2.getSymbol().length();
				}
			}
		});
		return true;
	}
	
	protected void removeSelectedPeriod() {
		if (scheduleIndices != null) {
			Collection<TvScheduleMapPeriodTime> removeList = new Vector<TvScheduleMapPeriodTime>();
			for (String serviceIndex : scheduleIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getLstPeriod().size()) {
						removeList.add(getDataSource().getLstPeriod().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getLstPeriod().removeAll(removeList);
		}
	}
	
	protected void removeSelectedWeek() {
		if (weekIndices != null) {
			Collection<TvScheduleMapWeekDay> removeList = new Vector<TvScheduleMapWeekDay>();
			for (String serviceIndex : weekIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getLstWeek().size()) {
						removeList.add(getDataSource().getLstWeek().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getLstWeek().removeAll(removeList);
		}
	}
	
	
	public String getShowMaxPeriodNote(){
		if (getDataSource()!=null && getDataSource().getLstPeriod()!=null && getDataSource().getLstPeriod().size()>=8){
			return "";
		}
		return "none";
	}
	
	public String getShowMaxWeekNote(){
		if (getDataSource()!=null && getDataSource().getLstWeek()!=null && getDataSource().getLstPeriod().size()>=50){
			return "";
		}
		return "none";
	}
	
	public String getApplyUpdateDisabled() {
		if ("".equals(getWriteDisabled()) && getShowMaxPeriodNote().equals("none")) {
			return "";
		}
		return "disabled";
	}
	
	public String getApplyUpdateDisabled2() {
		if ("".equals(getWriteDisabled()) && getShowMaxWeekNote().equals("none")) {
			return "";
		}
		return "disabled";
	}
	
	/**
	 * @param addSHour the addSHour to set
	 */
	public void setAddSHour(String addSHour) {
		this.addSHour = addSHour;
	}

	/**
	 * @param addSMin the addSMin to set
	 */
	public void setAddSMin(String addSMin) {
		this.addSMin = addSMin;
	}

	/**
	 * @param addEHour the addEHour to set
	 */
	public void setAddEHour(String addEHour) {
		this.addEHour = addEHour;
	}

	/**
	 * @param addEMin the addEMin to set
	 */
	public void setAddEMin(String addEMin) {
		this.addEMin = addEMin;
	}


	/**
	 * @param scheduleIndices the scheduleIndices to set
	 */
	public void setScheduleIndices(Collection<String> scheduleIndices) {
		this.scheduleIndices = scheduleIndices;
	}

	/**
	 * @return the addSHour
	 */
	public String getAddSHour() {
		return addSHour;
	}

	/**
	 * @return the addSMin
	 */
	public String getAddSMin() {
		return addSMin;
	}

	/**
	 * @return the addEHour
	 */
	public String getAddEHour() {
		return addEHour;
	}

	/**
	 * @return the addEMin
	 */
	public String getAddEMin() {
		return addEMin;
	}


	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof TvScheduleMap) {
			dataSource = bo;
			if (getDataSource().getLstPeriod() != null) {
				getDataSource().getLstPeriod().size();
			}
			if (getDataSource().getLstWeek() != null) {
				getDataSource().getLstWeek().size();
			}
		}
		return null;
	}

	/**
	 * @return the addSun
	 */
	public boolean isAddSun() {
		return addSun;
	}

	/**
	 * @param addSun the addSun to set
	 */
	public void setAddSun(boolean addSun) {
		this.addSun = addSun;
	}

	/**
	 * @return the addMon
	 */
	public boolean isAddMon() {
		return addMon;
	}

	/**
	 * @param addMon the addMon to set
	 */
	public void setAddMon(boolean addMon) {
		this.addMon = addMon;
	}

	/**
	 * @return the addTue
	 */
	public boolean isAddTue() {
		return addTue;
	}

	/**
	 * @param addTus the addTue to set
	 */
	public void setAddTue(boolean addTue) {
		this.addTue = addTue;
	}

	/**
	 * @return the addWed
	 */
	public boolean isAddWed() {
		return addWed;
	}

	/**
	 * @param addWed the addWed to set
	 */
	public void setAddWed(boolean addWed) {
		this.addWed = addWed;
	}

	/**
	 * @return the addThu
	 */
	public boolean isAddThu() {
		return addThu;
	}

	/**
	 * @param addThu the addThu to set
	 */
	public void setAddThu(boolean addThu) {
		this.addThu = addThu;
	}

	/**
	 * @return the addFri
	 */
	public boolean isAddFri() {
		return addFri;
	}

	/**
	 * @param addFri the addFri to set
	 */
	public void setAddFri(boolean addFri) {
		this.addFri = addFri;
	}

	/**
	 * @return the addSat
	 */
	public boolean isAddSat() {
		return addSat;
	}

	/**
	 * @param addSat the addSat to set
	 */
	public void setAddSat(boolean addSat) {
		this.addSat = addSat;
	}

	/**
	 * @return the addMonFri
	 */
	public boolean isAddMonFri() {
		return addMonFri;
	}

	/**
	 * @param addMonFri the addMonFri to set
	 */
	public void setAddMonFri(boolean addMonFri) {
		this.addMonFri = addMonFri;
	}

	/**
	 * @return the hideNewButton2
	 */
	public String getHideNewButton2() {
		return hideNewButton2;
	}

	/**
	 * @return the hideCreateItem2
	 */
	public String getHideCreateItem2() {
		return hideCreateItem2;
	}

	/**
	 * @return the addSection
	 */
	public int getAddSection() {
		return addSection;
	}

	/**
	 * @param addSection the addSection to set
	 */
	public void setAddSection(int addSection) {
		this.addSection = addSection;
	}

	/**
	 * @return the addSymbol
	 */
	public String getAddSymbol() {
		return addSymbol;
	}

	/**
	 * @param addSymbol the addSymbol to set
	 */
	public void setAddSymbol(String addSymbol) {
		this.addSymbol = addSymbol;
	}

	/**
	 * @param weekIndices the weekIndices to set
	 */
	public void setWeekIndices(Collection<String> weekIndices) {
		this.weekIndices = weekIndices;
	}

	/**
	 * @return the arrayWeekSun
	 */
	public String[] getArrayWeekSun() {
		return arrayWeekSun;
	}

	/**
	 * @param arrayWeekSun the arrayWeekSun to set
	 */
	public void setArrayWeekSun(String[] arrayWeekSun) {
		this.arrayWeekSun = arrayWeekSun;
	}

	/**
	 * @return the arrayWeekMon
	 */
	public String[] getArrayWeekMon() {
		return arrayWeekMon;
	}

	/**
	 * @param arrayWeekMon the arrayWeekMon to set
	 */
	public void setArrayWeekMon(String[] arrayWeekMon) {
		this.arrayWeekMon = arrayWeekMon;
	}

	/**
	 * @return the arrayWeekTue
	 */
	public String[] getArrayWeekTue() {
		return arrayWeekTue;
	}

	/**
	 * @param arrayWeekTue the arrayWeekTue to set
	 */
	public void setArrayWeekTue(String[] arrayWeekTue) {
		this.arrayWeekTue = arrayWeekTue;
	}

	/**
	 * @return the arrayWeekWed
	 */
	public String[] getArrayWeekWed() {
		return arrayWeekWed;
	}

	/**
	 * @param arrayWeekWed the arrayWeekWed to set
	 */
	public void setArrayWeekWed(String[] arrayWeekWed) {
		this.arrayWeekWed = arrayWeekWed;
	}

	/**
	 * @return the arrayWeekThu
	 */
	public String[] getArrayWeekThu() {
		return arrayWeekThu;
	}

	/**
	 * @param arrayWeekThu the arrayWeekThu to set
	 */
	public void setArrayWeekThu(String[] arrayWeekThu) {
		this.arrayWeekThu = arrayWeekThu;
	}

	/**
	 * @return the arrayWeekSat
	 */
	public String[] getArrayWeekSat() {
		return arrayWeekSat;
	}

	/**
	 * @param arrayWeekSat the arrayWeekSat to set
	 */
	public void setArrayWeekSat(String[] arrayWeekSat) {
		this.arrayWeekSat = arrayWeekSat;
	}

	/**
	 * @return the arraySMin
	 */
	public String[] getArraySMin() {
		return arraySMin;
	}

	/**
	 * @param arraySMin the arraySMin to set
	 */
	public void setArraySMin(String[] arraySMin) {
		this.arraySMin = arraySMin;
	}

	/**
	 * @return the arraySHour
	 */
	public String[] getArraySHour() {
		return arraySHour;
	}

	/**
	 * @param arraySHour the arraySHour to set
	 */
	public void setArraySHour(String[] arraySHour) {
		this.arraySHour = arraySHour;
	}

	/**
	 * @return the arrayEMin
	 */
	public String[] getArrayEMin() {
		return arrayEMin;
	}

	/**
	 * @param arrayEMin the arrayEMin to set
	 */
	public void setArrayEMin(String[] arrayEMin) {
		this.arrayEMin = arrayEMin;
	}

	/**
	 * @return the arrayEHour
	 */
	public String[] getArrayEHour() {
		return arrayEHour;
	}

	/**
	 * @param arrayEHour the arrayEHour to set
	 */
	public void setArrayEHour(String[] arrayEHour) {
		this.arrayEHour = arrayEHour;
	}

}