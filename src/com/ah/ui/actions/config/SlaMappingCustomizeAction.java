package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.SlaMappingCustomize;
import com.ah.bo.wlan.SlaMappingCustomize.ClientPhyMode;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class SlaMappingCustomizeAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(
			SlaMappingCustomizeAction.class.getSimpleName());

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("init".equals(operation)) {
				log.info("execute", "operation:" + operation);
			} else if ("update".equals(operation)) {
				log.info("execute", "operation:" + operation);
				updateMappingTable();
				addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED,
						new SlaMappingCustomize().getLabel()));
			} else if ("reset".equals(operation)) {
				log.info("execute", "operation:" + operation);
				removeMappingTable(domainId);
				addActionMessage(MgrUtil.getUserMessage(OBJECT_RESETED,
						new SlaMappingCustomize().getLabel()));
			}
			initMappingTable();
			return SUCCESS;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_RADIO_PROFILE);
	}

	private void prepareDefaultValues() {
		high_11a_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11a_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11a,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11a_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11a_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11a_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11a,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11a_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);

		high_11b_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11b_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11b,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11b_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11b_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11b_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11b,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11b_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);

		high_11g_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11g_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11g,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11g_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11g_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11g_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11g,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11g_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);

		high_11n_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11n_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11n,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11n_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11n_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11n_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11n,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11n_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		
		//For 11ac
		high_11ac_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11ac_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11ac,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11ac_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11ac_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11ac_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11ac,
						SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
		high_11ac_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_HIGH);

		// medium
		medium_11a_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11a_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11a,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11a_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11a_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11a_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11a,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11a_usage_2 = SlaMappingCustomize
				.getDefaultSLABottomUsagePercent(ClientPhyMode._11a,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);

		medium_11b_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11b_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11b,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11b_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11b_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11b_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11b,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11b_usage_2 = SlaMappingCustomize
				.getDefaultSLABottomUsagePercent(ClientPhyMode._11b,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);

		medium_11g_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11g_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11g,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11g_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11g_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11g_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11g,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11g_usage_2 = SlaMappingCustomize
				.getDefaultSLABottomUsagePercent(ClientPhyMode._11g,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);

		medium_11n_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11n_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11n,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11n_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11n_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11n_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11n,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11n_usage_2 = SlaMappingCustomize
				.getDefaultSLABottomUsagePercent(ClientPhyMode._11n,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		
		//For 11ac
		medium_11ac_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11ac_success_1 = SlaMappingCustomize
				.getDefaultSLATopSuccessPercent(ClientPhyMode._11ac,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11ac_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11ac_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11ac_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11ac,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
		medium_11ac_usage_2 = SlaMappingCustomize
				.getDefaultSLABottomUsagePercent(ClientPhyMode._11ac,
						SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);

		// low
		low_11a_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11a_success_1 = SlaMappingCustomize.getDefaultSLATopSuccessPercent(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11a_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11a_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11a_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11a,
						SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11a_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11a, SlaMappingCustomize.SLA_THROUGHPUT_LOW);

		low_11b_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11b_success_1 = SlaMappingCustomize.getDefaultSLATopSuccessPercent(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11b_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11b_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11b_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11b,
						SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11b_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11b, SlaMappingCustomize.SLA_THROUGHPUT_LOW);

		low_11g_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11g_success_1 = SlaMappingCustomize.getDefaultSLATopSuccessPercent(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11g_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11g_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11g_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11g,
						SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11g_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11g, SlaMappingCustomize.SLA_THROUGHPUT_LOW);

		low_11n_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11n_success_1 = SlaMappingCustomize.getDefaultSLATopSuccessPercent(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11n_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11n_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11n_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11n,
						SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11n_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11n, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		
		//For 11ac
		low_11ac_rate_1 = SlaMappingCustomize.getDefaultSLATopRate(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11ac_success_1 = SlaMappingCustomize.getDefaultSLATopSuccessPercent(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11ac_usage_1 = SlaMappingCustomize.getDefaultSLATopUsagePercent(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11ac_rate_2 = SlaMappingCustomize.getDefaultSLABottomRate(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11ac_success_2 = SlaMappingCustomize
				.getDefaultSLABottomSuccessPercent(ClientPhyMode._11ac,
						SlaMappingCustomize.SLA_THROUGHPUT_LOW);
		low_11ac_usage_2 = SlaMappingCustomize.getDefaultSLABottomUsagePercent(
				ClientPhyMode._11ac, SlaMappingCustomize.SLA_THROUGHPUT_LOW);
	}

	private void initMappingTable() {
		prepareDefaultValues();

		List<SlaMappingCustomize> list = QueryUtil.executeQuery(SlaMappingCustomize.class, null,
				null, domainId);

		for (SlaMappingCustomize item : list) {
			short level = item.getLevel();
			short order = item.getItemOrder();
			ClientPhyMode mode = item.getPhymode();
			switch (level) {
			case SlaMappingCustomize.SLA_THROUGHPUT_HIGH:
				if (order == SlaMappingCustomize.ITEM_ORDER_BOTTOM) {
					if (ClientPhyMode._11a.equals(mode)) {
						high_11a_rate_2 = item.getRate();
						high_11a_success_2 = item.getSuccess();
						high_11a_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11b.equals(mode)) {
						high_11b_rate_2 = item.getRate();
						high_11b_success_2 = item.getSuccess();
						high_11b_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11g.equals(mode)) {
						high_11g_rate_2 = item.getRate();
						high_11g_success_2 = item.getSuccess();
						high_11g_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11n.equals(mode)) {
						high_11n_rate_2 = item.getRate();
						high_11n_success_2 = item.getSuccess();
						high_11n_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11ac.equals(mode)) {
						high_11ac_rate_2 = item.getRate();
						high_11ac_success_2 = item.getSuccess();
						high_11ac_usage_2 = item.getUsage();
					}
				} else if (order == SlaMappingCustomize.ITEM_ORDER_TOP) {
					if (ClientPhyMode._11a.equals(mode)) {
						high_11a_rate_1 = item.getRate();
						high_11a_success_1 = item.getSuccess();
						high_11a_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11b.equals(mode)) {
						high_11b_rate_1 = item.getRate();
						high_11b_success_1 = item.getSuccess();
						high_11b_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11g.equals(mode)) {
						high_11g_rate_1 = item.getRate();
						high_11g_success_1 = item.getSuccess();
						high_11g_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11n.equals(mode)) {
						high_11n_rate_1 = item.getRate();
						high_11n_success_1 = item.getSuccess();
						high_11n_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11ac.equals(mode)) {
						high_11ac_rate_1 = item.getRate();
						high_11ac_success_1 = item.getSuccess();
						high_11ac_usage_1 = item.getUsage();
					}
				}
				break;
			case SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM:
				if (order == SlaMappingCustomize.ITEM_ORDER_BOTTOM) {
					if (ClientPhyMode._11a.equals(mode)) {
						medium_11a_rate_2 = item.getRate();
						medium_11a_success_2 = item.getSuccess();
						medium_11a_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11b.equals(mode)) {
						medium_11b_rate_2 = item.getRate();
						medium_11b_success_2 = item.getSuccess();
						medium_11b_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11g.equals(mode)) {
						medium_11g_rate_2 = item.getRate();
						medium_11g_success_2 = item.getSuccess();
						medium_11g_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11n.equals(mode)) {
						medium_11n_rate_2 = item.getRate();
						medium_11n_success_2 = item.getSuccess();
						medium_11n_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11ac.equals(mode)) {
						medium_11ac_rate_2 = item.getRate();
						medium_11ac_success_2 = item.getSuccess();
						medium_11ac_usage_2 = item.getUsage();
					}
				} else if (order == SlaMappingCustomize.ITEM_ORDER_TOP) {
					if (ClientPhyMode._11a.equals(mode)) {
						medium_11a_rate_1 = item.getRate();
						medium_11a_success_1 = item.getSuccess();
						medium_11a_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11b.equals(mode)) {
						medium_11b_rate_1 = item.getRate();
						medium_11b_success_1 = item.getSuccess();
						medium_11b_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11g.equals(mode)) {
						medium_11g_rate_1 = item.getRate();
						medium_11g_success_1 = item.getSuccess();
						medium_11g_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11n.equals(mode)) {
						medium_11n_rate_1 = item.getRate();
						medium_11n_success_1 = item.getSuccess();
						medium_11n_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11ac.equals(mode)) {
						medium_11ac_rate_1 = item.getRate();
						medium_11ac_success_1 = item.getSuccess();
						medium_11ac_usage_1 = item.getUsage();
					}
				}
				break;
			case SlaMappingCustomize.SLA_THROUGHPUT_LOW:
				if (order == SlaMappingCustomize.ITEM_ORDER_BOTTOM) {
					if (ClientPhyMode._11a.equals(mode)) {
						low_11a_rate_2 = item.getRate();
						low_11a_success_2 = item.getSuccess();
						low_11a_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11b.equals(mode)) {
						low_11b_rate_2 = item.getRate();
						low_11b_success_2 = item.getSuccess();
						low_11b_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11g.equals(mode)) {
						low_11g_rate_2 = item.getRate();
						low_11g_success_2 = item.getSuccess();
						low_11g_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11n.equals(mode)) {
						low_11n_rate_2 = item.getRate();
						low_11n_success_2 = item.getSuccess();
						low_11n_usage_2 = item.getUsage();
					} else if (ClientPhyMode._11ac.equals(mode)) {
						low_11ac_rate_2 = item.getRate();
						low_11ac_success_2 = item.getSuccess();
						low_11ac_usage_2 = item.getUsage();
					}
				} else if (order == SlaMappingCustomize.ITEM_ORDER_TOP) {
					if (ClientPhyMode._11a.equals(mode)) {
						low_11a_rate_1 = item.getRate();
						low_11a_success_1 = item.getSuccess();
						low_11a_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11b.equals(mode)) {
						low_11b_rate_1 = item.getRate();
						low_11b_success_1 = item.getSuccess();
						low_11b_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11g.equals(mode)) {
						low_11g_rate_1 = item.getRate();
						low_11g_success_1 = item.getSuccess();
						low_11g_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11n.equals(mode)) {
						low_11n_rate_1 = item.getRate();
						low_11n_success_1 = item.getSuccess();
						low_11n_usage_1 = item.getUsage();
					} else if (ClientPhyMode._11ac.equals(mode)) {
						low_11ac_rate_1 = item.getRate();
						low_11ac_success_1 = item.getSuccess();
						low_11ac_usage_1 = item.getUsage();
					}
				}
				break;
			}
		}
	}

	private void updateMappingTable() throws Exception {
		removeMappingTable(domainId);
		List<SlaMappingCustomize> list = getCustomizeMappingData();
		if (null != list) {
			QueryUtil.bulkCreateBos(list);
		}
	}

	private void removeMappingTable(Long domainId) throws Exception {
		FilterParams filterParams = new FilterParams("owner.id", domainId);
		QueryUtil.removeBos(SlaMappingCustomize.class, filterParams);
	}

	private List<SlaMappingCustomize> getCustomizeMappingData() {
		List<SlaMappingCustomize> list = new ArrayList<SlaMappingCustomize>();
		if (null != high_11a_rate_1 && high_11a_success_1 > 0
				&& high_11a_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11a);
			sla.setRate(high_11a_rate_1);
			sla.setSuccess(high_11a_success_1);
			sla.setUsage(high_11a_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11a_rate_2 && high_11a_success_2 > 0
				&& high_11a_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11a);
			sla.setRate(high_11a_rate_2);
			sla.setSuccess(high_11a_success_2);
			sla.setUsage(high_11a_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11b_rate_1 && high_11b_success_1 > 0
				&& high_11b_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11b);
			sla.setRate(high_11b_rate_1);
			sla.setSuccess(high_11b_success_1);
			sla.setUsage(high_11b_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11b_rate_2 && high_11b_success_2 > 0
				&& high_11b_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11b);
			sla.setRate(high_11b_rate_2);
			sla.setSuccess(high_11b_success_2);
			sla.setUsage(high_11b_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11g_rate_1 && high_11g_success_1 > 0
				&& high_11g_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11g);
			sla.setRate(high_11g_rate_1);
			sla.setSuccess(high_11g_success_1);
			sla.setUsage(high_11g_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11g_rate_2 && high_11g_success_2 > 0
				&& high_11g_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11g);
			sla.setRate(high_11g_rate_2);
			sla.setSuccess(high_11g_success_2);
			sla.setUsage(high_11g_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11n_rate_1 && high_11n_success_1 > 0
				&& high_11n_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11n);
			sla.setRate(high_11n_rate_1);
			sla.setSuccess(high_11n_success_1);
			sla.setUsage(high_11n_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11n_rate_2 && high_11n_success_2 > 0
				&& high_11n_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11n);
			sla.setRate(high_11n_rate_2);
			sla.setSuccess(high_11n_success_2);
			sla.setUsage(high_11n_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11ac_rate_1 && high_11ac_success_1 > 0
				&& high_11ac_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11ac);
			sla.setRate(high_11ac_rate_1);
			sla.setSuccess(high_11ac_success_1);
			sla.setUsage(high_11ac_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != high_11ac_rate_2 && high_11ac_success_2 > 0
				&& high_11ac_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
			sla.setPhymode(ClientPhyMode._11ac);
			sla.setRate(high_11ac_rate_2);
			sla.setSuccess(high_11ac_success_2);
			sla.setUsage(high_11ac_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		// medium
		if (null != medium_11a_rate_1 && medium_11a_success_1 > 0
				&& medium_11a_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11a);
			sla.setRate(medium_11a_rate_1);
			sla.setSuccess(medium_11a_success_1);
			sla.setUsage(medium_11a_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11a_rate_2 && medium_11a_success_2 > 0
				&& medium_11a_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11a);
			sla.setRate(medium_11a_rate_2);
			sla.setSuccess(medium_11a_success_2);
			sla.setUsage(medium_11a_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11b_rate_1 && medium_11b_success_1 > 0
				&& medium_11b_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11b);
			sla.setRate(medium_11b_rate_1);
			sla.setSuccess(medium_11b_success_1);
			sla.setUsage(medium_11b_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11b_rate_2 && medium_11b_success_2 > 0
				&& medium_11b_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11b);
			sla.setRate(medium_11b_rate_2);
			sla.setSuccess(medium_11b_success_2);
			sla.setUsage(medium_11b_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11g_rate_1 && medium_11g_success_1 > 0
				&& medium_11g_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11g);
			sla.setRate(medium_11g_rate_1);
			sla.setSuccess(medium_11g_success_1);
			sla.setUsage(medium_11g_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11g_rate_2 && medium_11g_success_2 > 0
				&& medium_11g_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11g);
			sla.setRate(medium_11g_rate_2);
			sla.setSuccess(medium_11g_success_2);
			sla.setUsage(medium_11g_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11n_rate_1 && medium_11n_success_1 > 0
				&& medium_11n_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11n);
			sla.setRate(medium_11n_rate_1);
			sla.setSuccess(medium_11n_success_1);
			sla.setUsage(medium_11n_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11n_rate_2 && medium_11n_success_2 > 0
				&& medium_11n_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11n);
			sla.setRate(medium_11n_rate_2);
			sla.setSuccess(medium_11n_success_2);
			sla.setUsage(medium_11n_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		//For 11ac
		if (null != medium_11ac_rate_1 && medium_11ac_success_1 > 0
				&& medium_11ac_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11ac);
			sla.setRate(medium_11ac_rate_1);
			sla.setSuccess(medium_11ac_success_1);
			sla.setUsage(medium_11ac_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != medium_11ac_rate_2 && medium_11ac_success_2 > 0
				&& medium_11ac_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_MEDIUM);
			sla.setPhymode(ClientPhyMode._11ac);
			sla.setRate(medium_11ac_rate_2);
			sla.setSuccess(medium_11ac_success_2);
			sla.setUsage(medium_11ac_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		// low
		if (null != low_11a_rate_1 && low_11a_success_1 > 0
				&& low_11a_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11a);
			sla.setRate(low_11a_rate_1);
			sla.setSuccess(low_11a_success_1);
			sla.setUsage(low_11a_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11a_rate_2 && low_11a_success_2 > 0
				&& low_11a_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11a);
			sla.setRate(low_11a_rate_2);
			sla.setSuccess(low_11a_success_2);
			sla.setUsage(low_11a_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11b_rate_1 && low_11b_success_1 > 0
				&& low_11b_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11b);
			sla.setRate(low_11b_rate_1);
			sla.setSuccess(low_11b_success_1);
			sla.setUsage(low_11b_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11b_rate_2 && low_11b_success_2 > 0
				&& low_11b_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11b);
			sla.setRate(low_11b_rate_2);
			sla.setSuccess(low_11b_success_2);
			sla.setUsage(low_11b_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11g_rate_1 && low_11g_success_1 > 0
				&& low_11g_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11g);
			sla.setRate(low_11g_rate_1);
			sla.setSuccess(low_11g_success_1);
			sla.setUsage(low_11g_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11g_rate_2 && low_11g_success_2 > 0
				&& low_11g_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11g);
			sla.setRate(low_11g_rate_2);
			sla.setSuccess(low_11g_success_2);
			sla.setUsage(low_11g_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11n_rate_1 && low_11n_success_1 > 0
				&& low_11a_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11n);
			sla.setRate(low_11n_rate_1);
			sla.setSuccess(low_11n_success_1);
			sla.setUsage(low_11n_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11n_rate_2 && low_11n_success_2 > 0
				&& low_11n_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11n);
			sla.setRate(low_11n_rate_2);
			sla.setSuccess(low_11n_success_2);
			sla.setUsage(low_11n_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		
		//For 11ac
		if (null != low_11ac_rate_1 && low_11ac_success_1 > 0
				&& low_11a_usage_1 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11ac);
			sla.setRate(low_11ac_rate_1);
			sla.setSuccess(low_11ac_success_1);
			sla.setUsage(low_11ac_usage_1);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_TOP);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		if (null != low_11ac_rate_2 && low_11ac_success_2 > 0
				&& low_11ac_usage_2 > 0) {
			SlaMappingCustomize sla = new SlaMappingCustomize();
			sla.setLevel(SlaMappingCustomize.SLA_THROUGHPUT_LOW);
			sla.setPhymode(ClientPhyMode._11ac);
			sla.setRate(low_11ac_rate_2);
			sla.setSuccess(low_11ac_success_2);
			sla.setUsage(low_11ac_usage_2);
			sla.setItemOrder(SlaMappingCustomize.ITEM_ORDER_BOTTOM);
			sla.setOwner(getDomain());
			list.add(sla);
		}
		return list.isEmpty() ? null : list;
	}

	public List<TextItem> get_11aRates() {
		return SlaMappingCustomize.get11aRates();
	}

	public List<TextItem> get_11bRates() {
		return SlaMappingCustomize.get11bRates();
	}

	public List<TextItem> get_11gRates() {
		return SlaMappingCustomize.get11gRates();
	}

	public List<TextItem> get_11nRates() {
		return SlaMappingCustomize.get11nRates();
	}
	
	public List<TextItem> get_11acRates() {
		return SlaMappingCustomize.get11acRates();
	}

	public static String getRate(List<SlaMappingCustomize> list, ClientPhyMode mode,
			short slaThoughput, short order) {
		if (null != list) {
			for (SlaMappingCustomize sla : list) {
				if (mode.equals(sla.getPhymode())
						&& slaThoughput == sla.getLevel()
						&& order == sla.getItemOrder()) {
					return sla.getRate();
				}
			}
		}
		return null;
	}

	public static int getSuccess(List<SlaMappingCustomize> list, ClientPhyMode mode,
			short slaThoughput, short order) {
		if (null != list) {
			for (SlaMappingCustomize sla : list) {
				if (mode.equals(sla.getPhymode())
						&& slaThoughput == sla.getLevel()
						&& order == sla.getItemOrder()) {
					return sla.getSuccess();
				}
			}
		}
		return 0;
	}

	public static int getUsage(List<SlaMappingCustomize> list, ClientPhyMode mode,
			short slaThoughput, short order) {
		if (null != list) {
			for (SlaMappingCustomize sla : list) {
				if (mode.equals(sla.getPhymode())
						&& slaThoughput == sla.getLevel()
						&& order == sla.getItemOrder()) {
					return sla.getUsage();
				}
			}
		}
		return 0;
	}

	private String high_11a_rate_1;
	private int high_11a_success_1;
	private int high_11a_usage_1;
	private String high_11a_rate_2;
	private int high_11a_success_2;
	private int high_11a_usage_2;

	private String high_11b_rate_1;
	private int high_11b_success_1;
	private int high_11b_usage_1;
	private String high_11b_rate_2;
	private int high_11b_success_2;
	private int high_11b_usage_2;

	private String high_11g_rate_1;
	private int high_11g_success_1;
	private int high_11g_usage_1;
	private String high_11g_rate_2;
	private int high_11g_success_2;
	private int high_11g_usage_2;

	private String high_11n_rate_1;
	private int high_11n_success_1;
	private int high_11n_usage_1;
	private String high_11n_rate_2;
	private int high_11n_success_2;
	private int high_11n_usage_2;
	//For 11ac
	private String high_11ac_rate_1;
	private int high_11ac_success_1;
	private int high_11ac_usage_1;
	private String high_11ac_rate_2;
	private int high_11ac_success_2;
	private int high_11ac_usage_2;

	// medium
	private String medium_11a_rate_1;
	private int medium_11a_success_1;
	private int medium_11a_usage_1;
	private String medium_11a_rate_2;
	private int medium_11a_success_2;
	private int medium_11a_usage_2;

	private String medium_11b_rate_1;
	private int medium_11b_success_1;
	private int medium_11b_usage_1;
	private String medium_11b_rate_2;
	private int medium_11b_success_2;
	private int medium_11b_usage_2;

	private String medium_11g_rate_1;
	private int medium_11g_success_1;
	private int medium_11g_usage_1;
	private String medium_11g_rate_2;
	private int medium_11g_success_2;
	private int medium_11g_usage_2;

	private String medium_11n_rate_1;
	private int medium_11n_success_1;
	private int medium_11n_usage_1;
	private String medium_11n_rate_2;
	private int medium_11n_success_2;
	private int medium_11n_usage_2;
	//For 11ac
	private String medium_11ac_rate_1;
	private int medium_11ac_success_1;
	private int medium_11ac_usage_1;
	private String medium_11ac_rate_2;
	private int medium_11ac_success_2;
	private int medium_11ac_usage_2;

	// low
	private String low_11a_rate_1;
	private int low_11a_success_1;
	private int low_11a_usage_1;
	private String low_11a_rate_2;
	private int low_11a_success_2;
	private int low_11a_usage_2;

	private String low_11b_rate_1;
	private int low_11b_success_1;
	private int low_11b_usage_1;
	private String low_11b_rate_2;
	private int low_11b_success_2;
	private int low_11b_usage_2;

	private String low_11g_rate_1;
	private int low_11g_success_1;
	private int low_11g_usage_1;
	private String low_11g_rate_2;
	private int low_11g_success_2;
	private int low_11g_usage_2;

	private String low_11n_rate_1;
	private int low_11n_success_1;
	private int low_11n_usage_1;
	private String low_11n_rate_2;
	private int low_11n_success_2;
	private int low_11n_usage_2;
	//For 11ac
	private String low_11ac_rate_1;
	private int low_11ac_success_1;
	private int low_11ac_usage_1;
	private String low_11ac_rate_2;
	private int low_11ac_success_2;
	private int low_11ac_usage_2;

	public String getHigh_11a_rate_1() {
		return high_11a_rate_1;
	}

	public void setHigh_11a_rate_1(String high_11a_rate_1) {
		this.high_11a_rate_1 = high_11a_rate_1;
	}

	public int getHigh_11a_success_1() {
		return high_11a_success_1;
	}

	public void setHigh_11a_success_1(int high_11a_success_1) {
		this.high_11a_success_1 = high_11a_success_1;
	}

	public int getHigh_11a_usage_1() {
		return high_11a_usage_1;
	}

	public void setHigh_11a_usage_1(int high_11a_usage_1) {
		this.high_11a_usage_1 = high_11a_usage_1;
	}

	public String getHigh_11a_rate_2() {
		return high_11a_rate_2;
	}

	public void setHigh_11a_rate_2(String high_11a_rate_2) {
		this.high_11a_rate_2 = high_11a_rate_2;
	}

	public int getHigh_11a_success_2() {
		return high_11a_success_2;
	}

	public void setHigh_11a_success_2(int high_11a_success_2) {
		this.high_11a_success_2 = high_11a_success_2;
	}

	public int getHigh_11a_usage_2() {
		return high_11a_usage_2;
	}

	public void setHigh_11a_usage_2(int high_11a_usage_2) {
		this.high_11a_usage_2 = high_11a_usage_2;
	}

	public String getHigh_11b_rate_1() {
		return high_11b_rate_1;
	}

	public void setHigh_11b_rate_1(String high_11b_rate_1) {
		this.high_11b_rate_1 = high_11b_rate_1;
	}

	public int getHigh_11b_success_1() {
		return high_11b_success_1;
	}

	public void setHigh_11b_success_1(int high_11b_success_1) {
		this.high_11b_success_1 = high_11b_success_1;
	}

	public int getHigh_11b_usage_1() {
		return high_11b_usage_1;
	}

	public void setHigh_11b_usage_1(int high_11b_usage_1) {
		this.high_11b_usage_1 = high_11b_usage_1;
	}

	public String getHigh_11b_rate_2() {
		return high_11b_rate_2;
	}

	public void setHigh_11b_rate_2(String high_11b_rate_2) {
		this.high_11b_rate_2 = high_11b_rate_2;
	}

	public int getHigh_11b_success_2() {
		return high_11b_success_2;
	}

	public void setHigh_11b_success_2(int high_11b_success_2) {
		this.high_11b_success_2 = high_11b_success_2;
	}

	public int getHigh_11b_usage_2() {
		return high_11b_usage_2;
	}

	public void setHigh_11b_usage_2(int high_11b_usage_2) {
		this.high_11b_usage_2 = high_11b_usage_2;
	}

	public String getHigh_11g_rate_1() {
		return high_11g_rate_1;
	}

	public void setHigh_11g_rate_1(String high_11g_rate_1) {
		this.high_11g_rate_1 = high_11g_rate_1;
	}

	public int getHigh_11g_success_1() {
		return high_11g_success_1;
	}

	public void setHigh_11g_success_1(int high_11g_success_1) {
		this.high_11g_success_1 = high_11g_success_1;
	}

	public int getHigh_11g_usage_1() {
		return high_11g_usage_1;
	}

	public void setHigh_11g_usage_1(int high_11g_usage_1) {
		this.high_11g_usage_1 = high_11g_usage_1;
	}

	public String getHigh_11g_rate_2() {
		return high_11g_rate_2;
	}

	public void setHigh_11g_rate_2(String high_11g_rate_2) {
		this.high_11g_rate_2 = high_11g_rate_2;
	}

	public int getHigh_11g_success_2() {
		return high_11g_success_2;
	}

	public void setHigh_11g_success_2(int high_11g_success_2) {
		this.high_11g_success_2 = high_11g_success_2;
	}

	public int getHigh_11g_usage_2() {
		return high_11g_usage_2;
	}

	public void setHigh_11g_usage_2(int high_11g_usage_2) {
		this.high_11g_usage_2 = high_11g_usage_2;
	}

	public String getHigh_11n_rate_1() {
		return high_11n_rate_1;
	}

	public void setHigh_11n_rate_1(String high_11n_rate_1) {
		this.high_11n_rate_1 = high_11n_rate_1;
	}

	public int getHigh_11n_success_1() {
		return high_11n_success_1;
	}

	public void setHigh_11n_success_1(int high_11n_success_1) {
		this.high_11n_success_1 = high_11n_success_1;
	}

	public int getHigh_11n_usage_1() {
		return high_11n_usage_1;
	}

	public void setHigh_11n_usage_1(int high_11n_usage_1) {
		this.high_11n_usage_1 = high_11n_usage_1;
	}

	public String getHigh_11n_rate_2() {
		return high_11n_rate_2;
	}

	public void setHigh_11n_rate_2(String high_11n_rate_2) {
		this.high_11n_rate_2 = high_11n_rate_2;
	}

	public int getHigh_11n_success_2() {
		return high_11n_success_2;
	}

	public void setHigh_11n_success_2(int high_11n_success_2) {
		this.high_11n_success_2 = high_11n_success_2;
	}

	public int getHigh_11n_usage_2() {
		return high_11n_usage_2;
	}

	public void setHigh_11n_usage_2(int high_11n_usage_2) {
		this.high_11n_usage_2 = high_11n_usage_2;
	}

	public String getMedium_11a_rate_1() {
		return medium_11a_rate_1;
	}

	public void setMedium_11a_rate_1(String medium_11a_rate_1) {
		this.medium_11a_rate_1 = medium_11a_rate_1;
	}

	public int getMedium_11a_success_1() {
		return medium_11a_success_1;
	}

	public void setMedium_11a_success_1(int medium_11a_success_1) {
		this.medium_11a_success_1 = medium_11a_success_1;
	}

	public int getMedium_11a_usage_1() {
		return medium_11a_usage_1;
	}

	public void setMedium_11a_usage_1(int medium_11a_usage_1) {
		this.medium_11a_usage_1 = medium_11a_usage_1;
	}

	public String getMedium_11a_rate_2() {
		return medium_11a_rate_2;
	}

	public void setMedium_11a_rate_2(String medium_11a_rate_2) {
		this.medium_11a_rate_2 = medium_11a_rate_2;
	}

	public int getMedium_11a_success_2() {
		return medium_11a_success_2;
	}

	public void setMedium_11a_success_2(int medium_11a_success_2) {
		this.medium_11a_success_2 = medium_11a_success_2;
	}

	public int getMedium_11a_usage_2() {
		return medium_11a_usage_2;
	}

	public void setMedium_11a_usage_2(int medium_11a_usage_2) {
		this.medium_11a_usage_2 = medium_11a_usage_2;
	}

	public String getMedium_11b_rate_1() {
		return medium_11b_rate_1;
	}

	public void setMedium_11b_rate_1(String medium_11b_rate_1) {
		this.medium_11b_rate_1 = medium_11b_rate_1;
	}

	public int getMedium_11b_success_1() {
		return medium_11b_success_1;
	}

	public void setMedium_11b_success_1(int medium_11b_success_1) {
		this.medium_11b_success_1 = medium_11b_success_1;
	}

	public int getMedium_11b_usage_1() {
		return medium_11b_usage_1;
	}

	public void setMedium_11b_usage_1(int medium_11b_usage_1) {
		this.medium_11b_usage_1 = medium_11b_usage_1;
	}

	public String getMedium_11b_rate_2() {
		return medium_11b_rate_2;
	}

	public void setMedium_11b_rate_2(String medium_11b_rate_2) {
		this.medium_11b_rate_2 = medium_11b_rate_2;
	}

	public int getMedium_11b_success_2() {
		return medium_11b_success_2;
	}

	public void setMedium_11b_success_2(int medium_11b_success_2) {
		this.medium_11b_success_2 = medium_11b_success_2;
	}

	public int getMedium_11b_usage_2() {
		return medium_11b_usage_2;
	}

	public void setMedium_11b_usage_2(int medium_11b_usage_2) {
		this.medium_11b_usage_2 = medium_11b_usage_2;
	}

	public String getMedium_11g_rate_1() {
		return medium_11g_rate_1;
	}

	public void setMedium_11g_rate_1(String medium_11g_rate_1) {
		this.medium_11g_rate_1 = medium_11g_rate_1;
	}

	public int getMedium_11g_success_1() {
		return medium_11g_success_1;
	}

	public void setMedium_11g_success_1(int medium_11g_success_1) {
		this.medium_11g_success_1 = medium_11g_success_1;
	}

	public int getMedium_11g_usage_1() {
		return medium_11g_usage_1;
	}

	public void setMedium_11g_usage_1(int medium_11g_usage_1) {
		this.medium_11g_usage_1 = medium_11g_usage_1;
	}

	public String getMedium_11g_rate_2() {
		return medium_11g_rate_2;
	}

	public void setMedium_11g_rate_2(String medium_11g_rate_2) {
		this.medium_11g_rate_2 = medium_11g_rate_2;
	}

	public int getMedium_11g_success_2() {
		return medium_11g_success_2;
	}

	public void setMedium_11g_success_2(int medium_11g_success_2) {
		this.medium_11g_success_2 = medium_11g_success_2;
	}

	public int getMedium_11g_usage_2() {
		return medium_11g_usage_2;
	}

	public void setMedium_11g_usage_2(int medium_11g_usage_2) {
		this.medium_11g_usage_2 = medium_11g_usage_2;
	}

	public String getMedium_11n_rate_1() {
		return medium_11n_rate_1;
	}

	public void setMedium_11n_rate_1(String medium_11n_rate_1) {
		this.medium_11n_rate_1 = medium_11n_rate_1;
	}

	public int getMedium_11n_success_1() {
		return medium_11n_success_1;
	}

	public void setMedium_11n_success_1(int medium_11n_success_1) {
		this.medium_11n_success_1 = medium_11n_success_1;
	}

	public int getMedium_11n_usage_1() {
		return medium_11n_usage_1;
	}

	public void setMedium_11n_usage_1(int medium_11n_usage_1) {
		this.medium_11n_usage_1 = medium_11n_usage_1;
	}

	public String getMedium_11n_rate_2() {
		return medium_11n_rate_2;
	}

	public void setMedium_11n_rate_2(String medium_11n_rate_2) {
		this.medium_11n_rate_2 = medium_11n_rate_2;
	}

	public int getMedium_11n_success_2() {
		return medium_11n_success_2;
	}

	public void setMedium_11n_success_2(int medium_11n_success_2) {
		this.medium_11n_success_2 = medium_11n_success_2;
	}

	public int getMedium_11n_usage_2() {
		return medium_11n_usage_2;
	}

	public void setMedium_11n_usage_2(int medium_11n_usage_2) {
		this.medium_11n_usage_2 = medium_11n_usage_2;
	}

	public String getLow_11a_rate_1() {
		return low_11a_rate_1;
	}

	public void setLow_11a_rate_1(String low_11a_rate_1) {
		this.low_11a_rate_1 = low_11a_rate_1;
	}

	public int getLow_11a_success_1() {
		return low_11a_success_1;
	}

	public void setLow_11a_success_1(int low_11a_success_1) {
		this.low_11a_success_1 = low_11a_success_1;
	}

	public int getLow_11a_usage_1() {
		return low_11a_usage_1;
	}

	public void setLow_11a_usage_1(int low_11a_usage_1) {
		this.low_11a_usage_1 = low_11a_usage_1;
	}

	public String getLow_11a_rate_2() {
		return low_11a_rate_2;
	}

	public void setLow_11a_rate_2(String low_11a_rate_2) {
		this.low_11a_rate_2 = low_11a_rate_2;
	}

	public int getLow_11a_success_2() {
		return low_11a_success_2;
	}

	public void setLow_11a_success_2(int low_11a_success_2) {
		this.low_11a_success_2 = low_11a_success_2;
	}

	public int getLow_11a_usage_2() {
		return low_11a_usage_2;
	}

	public void setLow_11a_usage_2(int low_11a_usage_2) {
		this.low_11a_usage_2 = low_11a_usage_2;
	}

	public String getLow_11b_rate_1() {
		return low_11b_rate_1;
	}

	public void setLow_11b_rate_1(String low_11b_rate_1) {
		this.low_11b_rate_1 = low_11b_rate_1;
	}

	public int getLow_11b_success_1() {
		return low_11b_success_1;
	}

	public void setLow_11b_success_1(int low_11b_success_1) {
		this.low_11b_success_1 = low_11b_success_1;
	}

	public int getLow_11b_usage_1() {
		return low_11b_usage_1;
	}

	public void setLow_11b_usage_1(int low_11b_usage_1) {
		this.low_11b_usage_1 = low_11b_usage_1;
	}

	public String getLow_11b_rate_2() {
		return low_11b_rate_2;
	}

	public void setLow_11b_rate_2(String low_11b_rate_2) {
		this.low_11b_rate_2 = low_11b_rate_2;
	}

	public int getLow_11b_success_2() {
		return low_11b_success_2;
	}

	public void setLow_11b_success_2(int low_11b_success_2) {
		this.low_11b_success_2 = low_11b_success_2;
	}

	public int getLow_11b_usage_2() {
		return low_11b_usage_2;
	}

	public void setLow_11b_usage_2(int low_11b_usage_2) {
		this.low_11b_usage_2 = low_11b_usage_2;
	}

	public String getLow_11g_rate_1() {
		return low_11g_rate_1;
	}

	public void setLow_11g_rate_1(String low_11g_rate_1) {
		this.low_11g_rate_1 = low_11g_rate_1;
	}

	public int getLow_11g_success_1() {
		return low_11g_success_1;
	}

	public void setLow_11g_success_1(int low_11g_success_1) {
		this.low_11g_success_1 = low_11g_success_1;
	}

	public int getLow_11g_usage_1() {
		return low_11g_usage_1;
	}

	public void setLow_11g_usage_1(int low_11g_usage_1) {
		this.low_11g_usage_1 = low_11g_usage_1;
	}

	public String getLow_11g_rate_2() {
		return low_11g_rate_2;
	}

	public void setLow_11g_rate_2(String low_11g_rate_2) {
		this.low_11g_rate_2 = low_11g_rate_2;
	}

	public int getLow_11g_success_2() {
		return low_11g_success_2;
	}

	public void setLow_11g_success_2(int low_11g_success_2) {
		this.low_11g_success_2 = low_11g_success_2;
	}

	public int getLow_11g_usage_2() {
		return low_11g_usage_2;
	}

	public void setLow_11g_usage_2(int low_11g_usage_2) {
		this.low_11g_usage_2 = low_11g_usage_2;
	}

	public String getLow_11n_rate_1() {
		return low_11n_rate_1;
	}

	public void setLow_11n_rate_1(String low_11n_rate_1) {
		this.low_11n_rate_1 = low_11n_rate_1;
	}

	public int getLow_11n_success_1() {
		return low_11n_success_1;
	}

	public void setLow_11n_success_1(int low_11n_success_1) {
		this.low_11n_success_1 = low_11n_success_1;
	}

	public int getLow_11n_usage_1() {
		return low_11n_usage_1;
	}

	public void setLow_11n_usage_1(int low_11n_usage_1) {
		this.low_11n_usage_1 = low_11n_usage_1;
	}

	public String getLow_11n_rate_2() {
		return low_11n_rate_2;
	}

	public void setLow_11n_rate_2(String low_11n_rate_2) {
		this.low_11n_rate_2 = low_11n_rate_2;
	}

	public int getLow_11n_success_2() {
		return low_11n_success_2;
	}

	public void setLow_11n_success_2(int low_11n_success_2) {
		this.low_11n_success_2 = low_11n_success_2;
	}

	public int getLow_11n_usage_2() {
		return low_11n_usage_2;
	}

	public void setLow_11n_usage_2(int low_11n_usage_2) {
		this.low_11n_usage_2 = low_11n_usage_2;
	}

	public String getHigh_11ac_rate_1() {
		return high_11ac_rate_1;
	}

	public void setHigh_11ac_rate_1(String high_11ac_rate_1) {
		this.high_11ac_rate_1 = high_11ac_rate_1;
	}

	public int getHigh_11ac_success_1() {
		return high_11ac_success_1;
	}

	public void setHigh_11ac_success_1(int high_11ac_success_1) {
		this.high_11ac_success_1 = high_11ac_success_1;
	}

	public int getHigh_11ac_usage_1() {
		return high_11ac_usage_1;
	}

	public void setHigh_11ac_usage_1(int high_11ac_usage_1) {
		this.high_11ac_usage_1 = high_11ac_usage_1;
	}

	public String getHigh_11ac_rate_2() {
		return high_11ac_rate_2;
	}

	public void setHigh_11ac_rate_2(String high_11ac_rate_2) {
		this.high_11ac_rate_2 = high_11ac_rate_2;
	}

	public int getHigh_11ac_success_2() {
		return high_11ac_success_2;
	}

	public void setHigh_11ac_success_2(int high_11ac_success_2) {
		this.high_11ac_success_2 = high_11ac_success_2;
	}

	public int getHigh_11ac_usage_2() {
		return high_11ac_usage_2;
	}

	public void setHigh_11ac_usage_2(int high_11ac_usage_2) {
		this.high_11ac_usage_2 = high_11ac_usage_2;
	}

	public String getMedium_11ac_rate_1() {
		return medium_11ac_rate_1;
	}

	public void setMedium_11ac_rate_1(String medium_11ac_rate_1) {
		this.medium_11ac_rate_1 = medium_11ac_rate_1;
	}

	public int getMedium_11ac_success_1() {
		return medium_11ac_success_1;
	}

	public void setMedium_11ac_success_1(int medium_11ac_success_1) {
		this.medium_11ac_success_1 = medium_11ac_success_1;
	}

	public int getMedium_11ac_usage_1() {
		return medium_11ac_usage_1;
	}

	public void setMedium_11ac_usage_1(int medium_11ac_usage_1) {
		this.medium_11ac_usage_1 = medium_11ac_usage_1;
	}

	public String getMedium_11ac_rate_2() {
		return medium_11ac_rate_2;
	}

	public void setMedium_11ac_rate_2(String medium_11ac_rate_2) {
		this.medium_11ac_rate_2 = medium_11ac_rate_2;
	}

	public int getMedium_11ac_success_2() {
		return medium_11ac_success_2;
	}

	public void setMedium_11ac_success_2(int medium_11ac_success_2) {
		this.medium_11ac_success_2 = medium_11ac_success_2;
	}

	public int getMedium_11ac_usage_2() {
		return medium_11ac_usage_2;
	}

	public void setMedium_11ac_usage_2(int medium_11ac_usage_2) {
		this.medium_11ac_usage_2 = medium_11ac_usage_2;
	}

	public String getLow_11ac_rate_1() {
		return low_11ac_rate_1;
	}

	public void setLow_11ac_rate_1(String low_11ac_rate_1) {
		this.low_11ac_rate_1 = low_11ac_rate_1;
	}

	public int getLow_11ac_success_1() {
		return low_11ac_success_1;
	}

	public void setLow_11ac_success_1(int low_11ac_success_1) {
		this.low_11ac_success_1 = low_11ac_success_1;
	}

	public int getLow_11ac_usage_1() {
		return low_11ac_usage_1;
	}

	public void setLow_11ac_usage_1(int low_11ac_usage_1) {
		this.low_11ac_usage_1 = low_11ac_usage_1;
	}

	public String getLow_11ac_rate_2() {
		return low_11ac_rate_2;
	}

	public void setLow_11ac_rate_2(String low_11ac_rate_2) {
		this.low_11ac_rate_2 = low_11ac_rate_2;
	}

	public int getLow_11ac_success_2() {
		return low_11ac_success_2;
	}

	public void setLow_11ac_success_2(int low_11ac_success_2) {
		this.low_11ac_success_2 = low_11ac_success_2;
	}

	public int getLow_11ac_usage_2() {
		return low_11ac_usage_2;
	}

	public void setLow_11ac_usage_2(int low_11ac_usage_2) {
		this.low_11ac_usage_2 = low_11ac_usage_2;
	}

}