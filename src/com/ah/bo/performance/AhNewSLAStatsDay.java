package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "ah_new_sla_stats_day")
@org.hibernate.annotations.Table(appliesTo = "ah_new_sla_stats_day", indexes = {
		@Index(name = "IDX_NEWSLA_DAY_TIMESTAMP", columnNames = {"timeStamp","APMAC","OWNER"}),
		@Index(name = "IDX_NEWSLA_DAY_OWNER", columnNames = {"OWNER"})
		})
public class AhNewSLAStatsDay implements AhNewSLAStatsInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	private String 		apMac;

	private String 		apName;
	private int			apTotal_Red;
	private int			apTotal_Yellow;
	private int			apSla_Red;
	private int			apSla_Yellow;
	private int			apAirTime_Red;
	private int			apCrcError_Red;
	private int			apRetry_Red;
	private int			apTxDrop_Red;
	private int			apRxDrop_Red;

	private int			clientTotal_Red;
	private int			clientTotal_Yellow;
	private int			clientSla_Red;
	private int			clientSla_Yellow;
	private int			clientAirTime_Red;
	private int			clientScore_Red;
	private int			clientScore_Yellow;

	private int			clientCount;
	private long		timeStamp;
	
	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Version
	private Timestamp		version;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return "ah_new_sla_stats_day";
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getApTotal_Red() {
		return apTotal_Red;
	}

	public void setApTotal_Red(int apTotalRed) {
		apTotal_Red = apTotalRed;
	}

	public int getApTotal_Yellow() {
		return apTotal_Yellow;
	}

	public void setApTotal_Yellow(int apTotalYellow) {
		apTotal_Yellow = apTotalYellow;
	}

	public int getApSla_Red() {
		return apSla_Red;
	}

	public void setApSla_Red(int apSlaRed) {
		apSla_Red = apSlaRed;
	}

	public int getApSla_Yellow() {
		return apSla_Yellow;
	}

	public void setApSla_Yellow(int apSlaYellow) {
		apSla_Yellow = apSlaYellow;
	}

	public int getApAirTime_Red() {
		return apAirTime_Red;
	}

	public void setApAirTime_Red(int apAirTimeRed) {
		apAirTime_Red = apAirTimeRed;
	}

	public int getApCrcError_Red() {
		return apCrcError_Red;
	}

	public void setApCrcError_Red(int apCrcErrorRed) {
		apCrcError_Red = apCrcErrorRed;
	}

	public int getApRetry_Red() {
		return apRetry_Red;
	}

	public void setApRetry_Red(int apRetryRed) {
		apRetry_Red = apRetryRed;
	}

	public int getApTxDrop_Red() {
		return apTxDrop_Red;
	}

	public void setApTxDrop_Red(int apTxDropRed) {
		apTxDrop_Red = apTxDropRed;
	}

	public int getApRxDrop_Red() {
		return apRxDrop_Red;
	}

	public void setApRxDrop_Red(int apRxDropRed) {
		apRxDrop_Red = apRxDropRed;
	}

	public int getClientTotal_Red() {
		return clientTotal_Red;
	}

	public void setClientTotal_Red(int clientTotalRed) {
		clientTotal_Red = clientTotalRed;
	}

	public int getClientTotal_Yellow() {
		return clientTotal_Yellow;
	}

	public void setClientTotal_Yellow(int clientTotalYellow) {
		clientTotal_Yellow = clientTotalYellow;
	}

	public int getClientSla_Red() {
		return clientSla_Red;
	}

	public void setClientSla_Red(int clientSlaRed) {
		clientSla_Red = clientSlaRed;
	}

	public int getClientSla_Yellow() {
		return clientSla_Yellow;
	}

	public void setClientSla_Yellow(int clientSlaYellow) {
		clientSla_Yellow = clientSlaYellow;
	}

	public int getClientAirTime_Red() {
		return clientAirTime_Red;
	}

	public void setClientAirTime_Red(int clientAirTimeRed) {
		clientAirTime_Red = clientAirTimeRed;
	}

	public int getClientScore_Red() {
		return clientScore_Red;
	}

	public void setClientScore_Red(int clientScoreRed) {
		clientScore_Red = clientScoreRed;
	}

	public int getClientScore_Yellow() {
		return clientScore_Yellow;
	}

	public void setClientScore_Yellow(int clientScoreYellow) {
		clientScore_Yellow = clientScoreYellow;
	}
	
	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

}