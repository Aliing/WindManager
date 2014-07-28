package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.DosParams.FrameType;
import com.ah.bo.network.DosParams.ScreeningType;
import com.ah.util.MgrUtil;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "DOS_PREVENTION")
@org.hibernate.annotations.Table(appliesTo = "DOS_PREVENTION", indexes = {
		@Index(name = "DOS_PREVENTION_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DosPrevention implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	public enum DosType {
		MAC, MAC_STATION, IP;
		public String getKey() {
			return name();
		}

		public String getValue() {
			return MgrUtil.getUserMessage(name());
		}
	}

	private DosType dosType;

	@Column(length = DEFAULT_STRING_LENGTH, unique = false, nullable = false)
	private String dosPreventionName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean defaultFlag;

	private boolean enabledSynCheck;

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "DOS_PREVENTION_DOS_PARAMS", joinColumns = @JoinColumn(name = "DOS_PREVENTION_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<String, DosParams> dosParamsMap = new HashMap<String, DosParams>();

	public Map<String, DosParams> getDosParamsMap() {
		return dosParamsMap;
	}

	public void setDosParamsMap(Map<String, DosParams> dosParamsMap) {
		this.dosParamsMap = dosParamsMap;
	}

	public DosParams getDosParams(FrameType frameType) {
		return dosParamsMap.get(frameType.name());
	}

	public DosParams getDosParams(ScreeningType screeningType) {
		return dosParamsMap.get(screeningType.name());
	}

	public String getDosPreventionName() {
		return dosPreventionName;
	}

	public void setDosPreventionName(String dosPreventionName) {
		this.dosPreventionName = dosPreventionName;
	}

	public DosPrevention() {
	}

	public DosPrevention(DosType dosType) {
		this.dosType = dosType;
	}

	public DosType getDosType() {
		return dosType;
	}

	public void setDosType(DosType dosType) {
		this.dosType = dosType;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return dosPreventionName;
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public boolean getEnabledSynCheck() {
		return enabledSynCheck;
	}

	public void setEnabledSynCheck(boolean enabledSynCheck) {
		this.enabledSynCheck = enabledSynCheck;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
    @Override
    public DosPrevention clone() {
       try {
           return (DosPrevention) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

}