package com.ah.bo.wlan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.TextItem;

@Entity
@Table(name = "SLA_MAPPING_CUSTOMIZE")
@org.hibernate.annotations.Table(appliesTo = "SLA_MAPPING_CUSTOMIZE", indexes = {
		@Index(name = "SLA_MAPPING_CUSTOMIZE_OWNER", columnNames = { "OWNER" })
		})
public class SlaMappingCustomize implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	private static final String RATE_UNIT = "Mbps";
	public static final String _11A_RATE_6 = "6";
	public static final String _11A_RATE_9 = "9";
	public static final String _11A_RATE_12 = "12";
	public static final String _11A_RATE_18 = "18";
	public static final String _11A_RATE_24 = "24";
	public static final String _11A_RATE_36 = "36";
	public static final String _11A_RATE_48 = "48";
	public static final String _11A_RATE_54 = "54";

	public static List<TextItem> get11aRates() {
		List<TextItem> list = new ArrayList<TextItem>();
		TextItem item = new TextItem(_11A_RATE_6, _11A_RATE_6+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11A_RATE_9, _11A_RATE_9+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11A_RATE_12, _11A_RATE_12+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11A_RATE_18, _11A_RATE_18+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11A_RATE_24, _11A_RATE_24+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11A_RATE_36, _11A_RATE_36+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11A_RATE_48, _11A_RATE_48+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11A_RATE_54, _11A_RATE_54+RATE_UNIT);
		list.add(item);
		return list;
	}

	public static final String _11B_RATE_1 = "1";
	public static final String _11B_RATE_2 = "2";
	public static final String _11B_RATE_5_5 = "5.5";
	public static final String _11B_RATE_11 = "11";

	public static List<TextItem> get11bRates() {
		List<TextItem> list = new ArrayList<TextItem>();
		TextItem item = new TextItem(_11B_RATE_1, _11B_RATE_1+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11B_RATE_2, _11B_RATE_2+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11B_RATE_5_5, _11B_RATE_5_5+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11B_RATE_11, _11B_RATE_11+RATE_UNIT);
		list.add(item);
		return list;
	}

	public static final String _11G_RATE_1 = "1";
	public static final String _11G_RATE_2 = "2";
	public static final String _11G_RATE_5_5 = "5.5";
	public static final String _11G_RATE_11 = "11";
	public static final String _11G_RATE_6 = "6";
	public static final String _11G_RATE_9 = "9";
	public static final String _11G_RATE_12 = "12";
	public static final String _11G_RATE_18 = "18";
	public static final String _11G_RATE_24 = "24";
	public static final String _11G_RATE_36 = "36";
	public static final String _11G_RATE_48 = "48";
	public static final String _11G_RATE_54 = "54";

	public static List<TextItem> get11gRates() {
		List<TextItem> list = new ArrayList<TextItem>();
		TextItem item = new TextItem(_11G_RATE_1, _11G_RATE_1+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_2, _11G_RATE_2+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_5_5, _11G_RATE_5_5+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_11, _11G_RATE_11+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_6, _11G_RATE_6+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_9, _11G_RATE_9+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_12, _11G_RATE_12+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_18, _11G_RATE_18+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_24, _11G_RATE_24+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_36, _11G_RATE_36+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_48, _11G_RATE_48+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11G_RATE_54, _11G_RATE_54+RATE_UNIT);
		list.add(item);
		return list;
	}

	public static final String _11N_RATE_6 = "6";
	public static final String _11N_RATE_9 = "9";
	public static final String _11N_RATE_12 = "12";
	public static final String _11N_RATE_18 = "18";
	public static final String _11N_RATE_24 = "24";
	public static final String _11N_RATE_36 = "36";
	public static final String _11N_RATE_48 = "48";
	public static final String _11N_RATE_54 = "54";
	public static final String _11N_RATE_MCS0 = "mcs0";
	public static final String _11N_RATE_MCS1 = "mcs1";
	public static final String _11N_RATE_MCS2 = "mcs2";
	public static final String _11N_RATE_MCS3 = "mcs3";
	public static final String _11N_RATE_MCS4 = "mcs4";
	public static final String _11N_RATE_MCS5 = "mcs5";
	public static final String _11N_RATE_MCS6 = "mcs6";
	public static final String _11N_RATE_MCS7 = "mcs7";
	public static final String _11N_RATE_MCS8 = "mcs8";
	public static final String _11N_RATE_MCS9 = "mcs9";
	public static final String _11N_RATE_MCS10 = "mcs10";
	public static final String _11N_RATE_MCS11 = "mcs11";
	public static final String _11N_RATE_MCS12 = "mcs12";
	public static final String _11N_RATE_MCS13 = "mcs13";
	public static final String _11N_RATE_MCS14 = "mcs14";
	public static final String _11N_RATE_MCS15 = "mcs15";
	public static final String _11N_RATE_MCS16 = "mcs16";
	public static final String _11N_RATE_MCS17 = "mcs17";
	public static final String _11N_RATE_MCS18 = "mcs18";
	public static final String _11N_RATE_MCS19 = "mcs19";
	public static final String _11N_RATE_MCS20 = "mcs20";
	public static final String _11N_RATE_MCS21 = "mcs21";
	public static final String _11N_RATE_MCS22 = "mcs22";
	public static final String _11N_RATE_MCS23 = "mcs23";
	public static final String _11AC_RATE_MCS01 = "mcs0/1";
	public static final String _11AC_RATE_MCS11 = "mcs1/1";
	public static final String _11AC_RATE_MCS21 = "mcs2/1";
	public static final String _11AC_RATE_MCS31 = "mcs3/1";
	public static final String _11AC_RATE_MCS41 = "mcs4/1";
	public static final String _11AC_RATE_MCS51 = "mcs5/1";
	public static final String _11AC_RATE_MCS61 = "mcs6/1";
	public static final String _11AC_RATE_MCS71 = "mcs7/1";
	public static final String _11AC_RATE_MCS81 = "mcs8/1";
	public static final String _11AC_RATE_MCS91 = "mcs9/1";
	public static final String _11AC_RATE_MCS02 = "mcs0/2";
	public static final String _11AC_RATE_MCS12 = "mcs1/2";
	public static final String _11AC_RATE_MCS22 = "mcs2/2";
	public static final String _11AC_RATE_MCS32 = "mcs3/2";
	public static final String _11AC_RATE_MCS42 = "mcs4/2";
	public static final String _11AC_RATE_MCS52 = "mcs5/2";
	public static final String _11AC_RATE_MCS62 = "mcs6/2";
	public static final String _11AC_RATE_MCS72 = "mcs7/2";
	public static final String _11AC_RATE_MCS82 = "mcs8/2";
	public static final String _11AC_RATE_MCS92 = "mcs9/2";
	public static final String _11AC_RATE_MCS03 = "mcs0/3";
	public static final String _11AC_RATE_MCS13 = "mcs1/3";
	public static final String _11AC_RATE_MCS23 = "mcs2/3";
	public static final String _11AC_RATE_MCS33 = "mcs3/3";
	public static final String _11AC_RATE_MCS43 = "mcs4/3";
	public static final String _11AC_RATE_MCS53 = "mcs5/3";
	public static final String _11AC_RATE_MCS63 = "mcs6/3";
	public static final String _11AC_RATE_MCS73 = "mcs7/3";
	public static final String _11AC_RATE_MCS83 = "mcs8/3";
	public static final String _11AC_RATE_MCS93 = "mcs9/3";

	public static List<TextItem> get11nRates() {
		List<TextItem> list = new ArrayList<TextItem>();
		TextItem item = new TextItem(_11N_RATE_6, _11N_RATE_6+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_9, _11N_RATE_9+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_12, _11N_RATE_12+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_18, _11N_RATE_18+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_24, _11N_RATE_24+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_36, _11N_RATE_36+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_48, _11N_RATE_48+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_54, _11N_RATE_54+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS0, _11N_RATE_MCS0);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS1, _11N_RATE_MCS1);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS2, _11N_RATE_MCS2);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS3, _11N_RATE_MCS3);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS4, _11N_RATE_MCS4);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS5, _11N_RATE_MCS5);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS6, _11N_RATE_MCS6);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS7, _11N_RATE_MCS7);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS8, _11N_RATE_MCS8);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS9, _11N_RATE_MCS9);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS10, _11N_RATE_MCS10);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS11, _11N_RATE_MCS11);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS12, _11N_RATE_MCS12);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS13, _11N_RATE_MCS13);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS14, _11N_RATE_MCS14);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS15, _11N_RATE_MCS15);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS16, _11N_RATE_MCS16);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS17, _11N_RATE_MCS17);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS18, _11N_RATE_MCS18);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS19, _11N_RATE_MCS19);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS20, _11N_RATE_MCS20);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS21, _11N_RATE_MCS21);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS22, _11N_RATE_MCS22);
		list.add(item);
		item = new TextItem(_11N_RATE_MCS23, _11N_RATE_MCS23);
		list.add(item);
		return list;
	}
	
	public static List<TextItem> get11acRates() {
		List<TextItem> list = new ArrayList<TextItem>();
		TextItem item = new TextItem(_11N_RATE_6, _11N_RATE_6+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_9, _11N_RATE_9+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_12, _11N_RATE_12+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_18, _11N_RATE_18+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_24, _11N_RATE_24+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_36, _11N_RATE_36+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_48, _11N_RATE_48+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11N_RATE_54, _11N_RATE_54+RATE_UNIT);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS01, _11AC_RATE_MCS01);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS11, _11AC_RATE_MCS11);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS21, _11AC_RATE_MCS21);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS31, _11AC_RATE_MCS31);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS41, _11AC_RATE_MCS41);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS51, _11AC_RATE_MCS51);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS61, _11AC_RATE_MCS61);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS71, _11AC_RATE_MCS71);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS81, _11AC_RATE_MCS81);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS91, _11AC_RATE_MCS91);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS02, _11AC_RATE_MCS02);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS12, _11AC_RATE_MCS12);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS22, _11AC_RATE_MCS22);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS32, _11AC_RATE_MCS32);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS42, _11AC_RATE_MCS42);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS52, _11AC_RATE_MCS52);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS62, _11AC_RATE_MCS62);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS72, _11AC_RATE_MCS72);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS82, _11AC_RATE_MCS82);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS92, _11AC_RATE_MCS92);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS03, _11AC_RATE_MCS03);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS13, _11AC_RATE_MCS13);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS23, _11AC_RATE_MCS23);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS33, _11AC_RATE_MCS33);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS43, _11AC_RATE_MCS43);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS53, _11AC_RATE_MCS53);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS63, _11AC_RATE_MCS63);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS73, _11AC_RATE_MCS73);
		list.add(item);		
		item = new TextItem(_11AC_RATE_MCS83, _11AC_RATE_MCS83);
		list.add(item);
		item = new TextItem(_11AC_RATE_MCS93, _11AC_RATE_MCS93);
		list.add(item);		
		return list;
	}

	public static final short SLA_THROUGHPUT_HIGH = 1;
	public static final short SLA_THROUGHPUT_MEDIUM = 2;
	public static final short SLA_THROUGHPUT_LOW = 3;

	private short level = SLA_THROUGHPUT_MEDIUM;

	public enum ClientPhyMode {
		_11a, _11b, _11g, _11n, _11ac
	}

	@Enumerated(EnumType.STRING)
	private ClientPhyMode phymode;

	public static final short ITEM_ORDER_TOP = 1;
	public static final short ITEM_ORDER_BOTTOM = 2;

	private short itemOrder = ITEM_ORDER_TOP;

	private String rate;
	private int success;
	private int usage;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public short getLevel() {
		return level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	public ClientPhyMode getPhymode() {
		return phymode;
	}

	public void setPhymode(ClientPhyMode phymode) {
		this.phymode = phymode;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public short getItemOrder() {
		return itemOrder;
	}

	public void setItemOrder(short itemOrder) {
		this.itemOrder = itemOrder;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	@Override
	public String getLabel() {
		return "SLA Mapping Table";
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

	@Transient
	public static String getDefaultSLATopRate(ClientPhyMode mode,
			short slaThoughput) {
		switch (mode) {
		case _11a:
		case _11g:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return "48";
			case SLA_THROUGHPUT_LOW:
				return "36";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return "36";
			}
		case _11b:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return "11";
			case SLA_THROUGHPUT_LOW:
				return "11";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return "11";
			}
		case _11ac:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return _11AC_RATE_MCS42;
			case SLA_THROUGHPUT_LOW:
				return "36";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return _11AC_RATE_MCS22;
			}
		case _11n:
		default:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return "mcs14";
			case SLA_THROUGHPUT_LOW:
				return "36";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return "mcs12";
			}
		}
	}

	@Transient
	public static String getDefaultSLABottomRate(ClientPhyMode mode,
			short slaThoughput) {
		switch (mode) {
		case _11a:
		case _11g:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return "36";
			case SLA_THROUGHPUT_LOW:
				return "24";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return "24";
			}
		case _11b:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return "5.5";
			case SLA_THROUGHPUT_LOW:
				return "5.5";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return "5.5";
			}
		case _11ac:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return _11AC_RATE_MCS32;
			case SLA_THROUGHPUT_LOW:
				return "24";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return "54";
			}
		case _11n:
		default:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return "mcs13";
			case SLA_THROUGHPUT_LOW:
				return "24";
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return "54";
			}
		}
	}

	@Transient
	public static int getDefaultSLATopSuccessPercent(ClientPhyMode mode,
			short slaThoughput) {
		switch (mode) {
		case _11a:
		case _11g:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return 70;
			case SLA_THROUGHPUT_LOW:
				return 60;
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 70;
			}
		case _11b:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return 70;
			case SLA_THROUGHPUT_LOW:
				return 50;
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 60;
			}
		case _11ac:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return 70;
			case SLA_THROUGHPUT_LOW:
				return 60;
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 80;
			}
		case _11n:
		default:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return 70;
			case SLA_THROUGHPUT_LOW:
				return 60;
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 80;
			}
		}
	}

	@Transient
	public static int getDefaultSLABottomSuccessPercent(ClientPhyMode mode,
			short slaThoughput) {
		switch (mode) {
		case _11a:
		case _11g:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return 80;
			case SLA_THROUGHPUT_LOW:
				return 70;
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 80;
			}
		case _11b:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return 80;
			case SLA_THROUGHPUT_LOW:
				return 60;
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 70;
			}
		case _11ac:
		case _11n:
		default:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
				return 80;
			case SLA_THROUGHPUT_LOW:
				return 70;
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 70;
			}
		}
	}

	@Transient
	public static int getDefaultSLATopUsagePercent(ClientPhyMode mode,
			short slaThoughput) {
		switch (mode) {
		case _11a:
		case _11g:
		case _11b:
		case _11n:
		case _11ac:
		default:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
			case SLA_THROUGHPUT_LOW:
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 50;
			}
		}
	}

	@Transient
	public static int getDefaultSLABottomUsagePercent(ClientPhyMode mode,
			short slaThoughput) {
		switch (mode) {
		case _11a:
		case _11g:
		case _11b:
		case _11n:
		case _11ac:
		default:
			switch (slaThoughput) {
			case SLA_THROUGHPUT_HIGH:
			case SLA_THROUGHPUT_LOW:
			case SLA_THROUGHPUT_MEDIUM:
			default:
				return 50;
			}
		}
	}

}