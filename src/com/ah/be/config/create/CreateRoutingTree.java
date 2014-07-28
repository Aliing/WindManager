package com.ah.be.config.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.RoutingProfileInt;
import com.ah.be.config.create.source.RoutingProfileInt.AadvertiseType;
import com.ah.bo.network.RoutingProfilePolicyRule;
import com.ah.util.Tracer;
import com.ah.xml.be.config.AhEnumAct;
import com.ah.xml.be.config.AhEnumActValue;
import com.ah.xml.be.config.AhNameQuoteProhibited;
import com.ah.xml.be.config.RouteRequestInterval;
import com.ah.xml.be.config.RoutingAdvertise;
import com.ah.xml.be.config.RoutingAuthMode;
import com.ah.xml.be.config.RoutingAuthModeValue;
import com.ah.xml.be.config.RoutingBgp;
import com.ah.xml.be.config.RoutingBgpKeepalive;
import com.ah.xml.be.config.RoutingBgpSystemNumber;
import com.ah.xml.be.config.RoutingInternalSubNetwork;
import com.ah.xml.be.config.RoutingMatchMap;
import com.ah.xml.be.config.RoutingMatchMapFrom;
import com.ah.xml.be.config.RoutingMatchMapIif;
import com.ah.xml.be.config.RoutingMatchMapUserProfile;
import com.ah.xml.be.config.RoutingObj;
import com.ah.xml.be.config.RoutingOspf;
import com.ah.xml.be.config.RoutingPolicy;
import com.ah.xml.be.config.RoutingPolicyId;
import com.ah.xml.be.config.RoutingPolicyIdMatchMap;
import com.ah.xml.be.config.RoutingProtocol;
import com.ah.xml.be.config.RoutingProtocolType;
import com.ah.xml.be.config.RoutingRipv2;
import com.ah.xml.be.config.RoutingRouteMap;
import com.ah.xml.be.config.RoutingRouteRequest;

public class CreateRoutingTree {
	private static final String ROUTING_MATCHMAP_FROM_ANY = "any";
	private static final String ROUTING_MATCHMAP_FROM_IPRANGE = "iprange ";
	private static final String ROUTING_MATCHMAP_FROM_NETWORK = "network ";

	private static final String ROUTING_MATCHMAP_TO_ANY = "any";
	private static final String ROUTING_MATCHMAP_TO_HOSTNAME = "hostname ";
	private static final String ROUTING_MATCHMAP_TO_IPRANGE = "iprange ";
	private static final String ROUTING_MATCHMAP_TO_NETWORK = "network ";
	private static final String ROUTING_MATCHMAP_TO_PRIVATE = "private";

	private static final Tracer log = new Tracer(
			CreateRoutingTree.class.getSimpleName());
	private RoutingProfileInt routingImpl;
	private GenerateXMLDebug oDebug;
	private HashMap<Short, String> wanIntfPool = new HashMap<>();

	private RoutingObj routingObj;

	private List<Object> routingChildList_1 = new ArrayList<Object>();
	private List<Object> routingChildList_2 = new ArrayList<Object>();

	public CreateRoutingTree(RoutingProfileInt routingImpl,
			GenerateXMLDebug oDebug) {
		this.routingImpl = routingImpl;
		this.oDebug = oDebug;
	}

	public void generate() throws Exception {
		if (routingImpl.isConfigRouting()) {
			routingObj = new RoutingObj();
			generateRoutingLevel_1();
		}
	}

	public RoutingObj getRoutingObj() {
		return this.routingObj;
	}

	private void generateRoutingLevel_1() throws Exception {

		/** element: <routing>.<protocol> */
		if (routingImpl.isConfigRoutingProtocol()) {
			RoutingProtocol protocolObj = new RoutingProtocol();
			routingChildList_1.add(protocolObj);
			routingObj.setProtocol(protocolObj);
		}

		/** element: <routing>.<route-request> */
		if (routingImpl.isConfigRoutingRequest()) {
			RoutingRouteRequest requestObj = new RoutingRouteRequest();
			routingChildList_1.add(requestObj);
			routingObj.setRouteRequest(requestObj);
		}

		/** element: <routing>.<internal-sub-network> */
		for (int index = 0; index < routingImpl.getRoutingSubNetworkSize(); index++) {
			if (routingImpl.isConfigRoutingSubNetwork(index)) {
				routingObj.getInternalSubNetwork().add(
						createRoutingInternalSubNetwork(routingImpl
								.getRoutingSubNetworkValue(index).trim(),
								routingImpl.isRoutingSubNetworkTunnel(index)));
			}
		}

		int ruleSize = routingImpl.getPolicyRuleSize();
		for (int i = 0; i < ruleSize; i++) {
			int destinationType = routingImpl.getDestinationType(i);
			// String destinationName = routingImpl.getDestinationName(i);
			String destinationValue = routingImpl.getDestinationValue(i);

			int sourceType = routingImpl.getSourceType(i);
			String sourceName = routingImpl.getSourceName(i);
			List<String> sourceValue = routingImpl.getSourceValue(i);

			String out1 = routingImpl.getOut(i, 0);
			String out2 = routingImpl.getOut(i, 1);

			// if route-map is empty, don't generate CLI
			if (out1 == null && out2 == null)
				continue;

			// it is suggested to use same name for match-map and route-map
			RoutingRouteMap rmap = prepareRoutingRouteMap(sourceName);
			if (out1 != null)
				rmap.getVia().add(
						CLICommonFunc.createAhName(out1));
			if (out2 != null)
				rmap.getVia().add(
						CLICommonFunc.createAhName(out2));

			AhNameQuoteProhibited matchmap_to = new AhNameQuoteProhibited();
			switch (destinationType) {
			case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_ANY:/* any */{
				matchmap_to.setName(ROUTING_MATCHMAP_TO_ANY);
				matchmap_to.setQuoteProhibited(AhEnumAct.YES);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_IPRANGE:/* ip-range */{
				matchmap_to.setName(ROUTING_MATCHMAP_TO_IPRANGE
						+ prepareIpRange(destinationValue));
				matchmap_to.setQuoteProhibited(AhEnumAct.YES);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_NETWORK:/* network */{
				matchmap_to.setName(ROUTING_MATCHMAP_TO_NETWORK
						+ destinationValue);
				matchmap_to.setQuoteProhibited(AhEnumAct.YES);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_HOSTNAME:/* hostname */{
				matchmap_to.setName(ROUTING_MATCHMAP_TO_HOSTNAME
						+ destinationValue);
				matchmap_to.setQuoteProhibited(AhEnumAct.YES);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_DESTINTION_PRIVATE:/* private */{
				matchmap_to.setName(ROUTING_MATCHMAP_TO_PRIVATE);
				matchmap_to.setQuoteProhibited(AhEnumAct.YES);
				break;
			}
			}

			List<RoutingMatchMap> mmaps = new ArrayList<>();
			switch (sourceType) {
			case RoutingProfilePolicyRule.MATCHMAP_SOURCE_ANY:/* any */{
				RoutingMatchMap mmap = prepareRoutingMatchMap(sourceName);
				RoutingMatchMapFrom from = new RoutingMatchMapFrom();
				from.setName(ROUTING_MATCHMAP_FROM_ANY);
				from.setQuoteProhibited(AhEnumAct.YES);
				from.setTo(matchmap_to);
				mmap.setFrom(from);
				mmaps.add(mmap);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_SOURCE_IPRANGE:/* ip-range */{
				RoutingMatchMap mmap = prepareRoutingMatchMap(sourceName);
				RoutingMatchMapFrom from = new RoutingMatchMapFrom();
				from.setName(ROUTING_MATCHMAP_FROM_IPRANGE
						+ prepareIpRange(sourceValue.get(0)));
				from.setQuoteProhibited(AhEnumAct.YES);
				from.setTo(matchmap_to);
				mmap.setFrom(from);
				mmaps.add(mmap);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_SOURCE_NETWORK:/* network */{
				RoutingMatchMap mmap = prepareRoutingMatchMap(sourceName);
				RoutingMatchMapFrom from = new RoutingMatchMapFrom();
				from.setName(ROUTING_MATCHMAP_FROM_NETWORK + sourceValue.get(0));
				from.setQuoteProhibited(AhEnumAct.YES);
				from.setTo(matchmap_to);
				mmap.setFrom(from);
				mmaps.add(mmap);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_SOURCE_INTERFACE:/* iif */{
				if (sourceValue == null || sourceValue.size() == 0)
					break;
				RoutingMatchMap mmap = prepareRoutingMatchMap(sourceName);
				RoutingMatchMapIif iif = new RoutingMatchMapIif();
				iif.setName(sourceValue.get(0));
				iif.setQuoteProhibited(AhEnumAct.YES);
				iif.setTo(matchmap_to);
				mmap.setIif(iif);
				mmaps.add(mmap);
				break;
			}
			case RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE:/*
																	 * user-profile
																	 */{
				for (String upName : sourceValue) {
					RoutingMatchMap mmap = prepareRoutingMatchMap(sourceName);
					RoutingMatchMapUserProfile up = new RoutingMatchMapUserProfile();
					up.setName(upName);
					up.setQuoteProhibited(AhEnumAct.YES);
					up.setTo(matchmap_to);
					mmap.setUserProfile(up);
					mmaps.add(mmap);
				}
				if (mmaps.size() > 0)
					mmaps.get(0).setName(sourceName);
				break;
			}
			}

			// if match-map is empty, don't generate CLI
			if (mmaps.size() == 0) {
				continue;
			}

			/** element: <routing>.<match-map> */
			routingObj.getMatchMap().addAll(mmaps);

			/** element: <routing>.<route-map> */
			routingObj.getRouteMap().add(rmap);

			for (RoutingMatchMap mmap : mmaps) {
				RoutingPolicyIdMatchMap idMatchmap = new RoutingPolicyIdMatchMap();
				idMatchmap.setName(mmap.getName());
				idMatchmap.setRouteMap(CLICommonFunc.createAhName(rmap
						.getName()));

				RoutingPolicyId policyId = new RoutingPolicyId();
				policyId.setName(String
						.valueOf(routingObj.getPolicy().size() + 1));
				policyId.setMatchMap(idMatchmap);

				RoutingPolicy policy = new RoutingPolicy();
				policy.setName(idMatchmap.getName());
				/** attribute: operation */
				policy.setOperation(CLICommonFunc
						.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				policy.setId(policyId);

				/** element: <routing>.<policy> */
				routingObj.getPolicy().add(policy);
			}
		}

		generateRoutingLevel_2();
	}

	private void generateRoutingLevel_2() throws Exception {
		/**
		 * <routing>.<protocol> RoutingProtocol <routing>.<route-request>
		 * RoutingRouteRequest
		 */
		for (Object childObj : routingChildList_1) {

			/** element: <routing>.<protocol> */
			if (childObj instanceof RoutingProtocol) {
				RoutingProtocol protocolObj = (RoutingProtocol) childObj;

				/** element: <routing>.<protocol>.<enable> */
				protocolObj.setEnable(CLICommonFunc.getAhOnlyAct(routingImpl
						.isProtocolEnable()));

				/** element: <routing>.<protocol>.<type> */
				if (routingImpl.isConfigProtocolType()) {
					RoutingProtocolType typeObj = new RoutingProtocolType();
					routingChildList_2.add(typeObj);
					protocolObj.setType(typeObj);
				}

				/** element: <routing>.<protocol>.<ripv2> */
				if (routingImpl.isConfigProtocolRipv2()) {
					RoutingRipv2 ripObj = new RoutingRipv2();
					routingChildList_2.add(ripObj);
					protocolObj.setRipv2(ripObj);
				}

				/** element: <routing>.<protocol>.<ospf> */
				if (routingImpl.isConfigProtocolOspf()) {
					RoutingOspf ospfObj = new RoutingOspf();
					routingChildList_2.add(ospfObj);
					protocolObj.setOspf(ospfObj);
				}

				/** element: <routing>.<protocol>.<bgp> */
				if (routingImpl.isConfigProtocolBgp()) {
					RoutingBgp bgpObj = new RoutingBgp();
					routingChildList_2.add(bgpObj);
					protocolObj.setBgp(bgpObj);
				}
			}

			/** element: <routing>.<route-request> */
			if (childObj instanceof RoutingRouteRequest) {
				RoutingRouteRequest requestObj = (RoutingRouteRequest) childObj;

				/** element: <routing>.<route-request>.<enable> */
				requestObj.setEnable(CLICommonFunc.getAhOnlyAct(routingImpl
						.isEnableRouteRequest()));

				/** element: <routing>.<route-request>.<interval> */
				Object[][] intervalParm = {
						{ CLICommonFunc.ATTRIBUTE_VALUE,
								routingImpl.getRouteInterval() },
						{ CLICommonFunc.ATTRIBUTE_OPERATION,
								CLICommonFunc.getYesDefault() } };
				requestObj.setInterval((RouteRequestInterval) CLICommonFunc
						.createObjectWithName(RouteRequestInterval.class,
								intervalParm));
			}
		}
		routingChildList_1.clear();
		generateRoutingLevel_3();
	}

	private void generateRoutingLevel_3() throws Exception {
		/**
		 * <routing>.<protocol>.<type> RoutingProtocolType
		 * <routing>.<protocol>.<ripv2> RoutingRipv2 <routing>.<protocol>.<ospf>
		 * RoutingOspf <routing>.<protocol>.<bgp> RoutingBgp
		 */
		for (Object childObj : routingChildList_2) {

			/** element: <routing>.<protocol>.<type> */
			if (childObj instanceof RoutingProtocolType) {
				RoutingProtocolType typeObj = (RoutingProtocolType) childObj;

				/** attribute: operation */
				typeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc
						.getYesDefault()));

				/** attribute: value */
				typeObj.setValue(routingImpl.getProtocolTypeValue());
			}

			/** element: <routing>.<protocol>.<ripv2> */
			if (childObj instanceof RoutingRipv2) {
				RoutingRipv2 ripv2Obj = (RoutingRipv2) childObj;

				/** element: <routing>.<protocol>.<ripv2>.<auth-mode> */
				if (routingImpl.isConfigMd5Key()) {
					ripv2Obj.setAuthMode(this.createRoutingAuthMode(routingImpl
							.getMd5Key()));
				}

				/** element: <routing>.<protocol>.<ripv2>.<advertise> */
				ripv2Obj.setAdvertise(createRoutingAdvertise(routingImpl
						.getAadvertise()));
			}

			/** element: <routing>.<protocol>.<ospf> */
			if (childObj instanceof RoutingOspf) {
				RoutingOspf ospfObj = (RoutingOspf) childObj;

				/** element: <routing>.<protocol>.<ospf>.<advertise> */
				ospfObj.setAdvertise(createRoutingAdvertise(routingImpl
						.getAadvertise()));

				/** element: <routing>.<protocol>.<ospf>.<area> */
				if (routingImpl.isConfigArea()) {
					ospfObj.setArea(CLICommonFunc.createAhStringActObj(
							routingImpl.getArea(),
							CLICommonFunc.getYesDefault()));
				}

				/** element: <routing>.<protocol>.<ospf>.<router-id> */
				if (routingImpl.isConfigRouterId()) {
					ospfObj.setRouterId(CLICommonFunc.createAhStringActObj(
							routingImpl.getRouterId(),
							CLICommonFunc.getYesDefault()));
				}

				/** element: <routing>.<protocol>.<ospf>.<auth-mode> */
				if (routingImpl.isConfigMd5Key()) {
					ospfObj.setAuthMode(this.createRoutingAuthMode(routingImpl
							.getMd5Key()));
				}
			}

			/** element: <routing>.<protocol>.<bgp> */
			if (childObj instanceof RoutingBgp) {
				RoutingBgp bgpObj = (RoutingBgp) childObj;

				/** element: <routing>.<protocol>.<bgp>.<keepalive> */
				if (routingImpl.isConfigKeepalive()) {
					Object[][] aliveParm = {
							{ CLICommonFunc.ATTRIBUTE_VALUE,
									routingImpl.getKeepaliveValue() },
							{ CLICommonFunc.ATTRIBUTE_OPERATION,
									CLICommonFunc.getYesDefault() } };
					bgpObj.setKeepalive((RoutingBgpKeepalive) CLICommonFunc
							.createObjectWithName(RoutingBgpKeepalive.class,
									aliveParm));
				}

				/** element: <routing>.<protocol>.<bgp>.<systemNumber> */
				if (routingImpl.isConfigSystemNumber()) {
					Object[][] systemParm = {
							{ CLICommonFunc.ATTRIBUTE_VALUE,
									routingImpl.getSystemNumber() },
							{ CLICommonFunc.ATTRIBUTE_OPERATION,
									CLICommonFunc.getYesDefault() } };
					bgpObj.setSystemNumber((RoutingBgpSystemNumber) CLICommonFunc
							.createObjectWithName(RoutingBgpSystemNumber.class,
									systemParm));
				}

				/** element: <routing>.<protocol>.<bgp>.<router-id> */
				if (routingImpl.isConfigRouterId()) {
					bgpObj.setRouterId(CLICommonFunc.createAhStringActObj(
							routingImpl.getRouterId(),
							CLICommonFunc.getYesDefault()));
				}

				/** element: <routing>.<protocol>.<bgp>.<auth-mode> */
				if (routingImpl.isConfigMd5Key()) {
					bgpObj.setAuthMode(this.createRoutingAuthMode(routingImpl
							.getMd5Key()));
				}

				/** element: <routing>.<protocol>.<bgp>.<neighbor> */
				for (int index = 0; index < routingImpl.getNeighborSize(); index++) {
					bgpObj.getNeighbor().add(
							CLICommonFunc.createAhNameActValue(
									routingImpl.getNeighborValue(index),
									CLICommonFunc.getYesDefault()));
				}
			}
		}
		routingChildList_2.clear();
	}

	private RoutingInternalSubNetwork createRoutingInternalSubNetwork(
			String name, boolean isTunnel) {
		if (name == null || "".equals(name)) {
			return null;
		}
		RoutingInternalSubNetwork subNetwork = new RoutingInternalSubNetwork();

		/** attribute: name */
		subNetwork.setName(name);

		/** attribute: operation */
		subNetwork.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc
				.getYesDefault()));

		/** element: <tunnel-dist-only> */
		if (isTunnel) {
			subNetwork.setTunnelDistOnly(CLICommonFunc
					.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}

		return subNetwork;
	}

	private RoutingAuthMode createRoutingAuthMode(String key)
			throws IOException {
		if (key == null || "".equals(key)) {
			return null;
		}
		RoutingAuthMode authModeObj = new RoutingAuthMode();

		/** attribute: value */
		authModeObj.setValue(RoutingAuthModeValue.MD_5);

		/** attribute: operation */
		authModeObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc
				.getYesDefault()));

		/** element: <key> */
		authModeObj.setKey(CLICommonFunc.createAhEncryptedString(key));

		return authModeObj;
	}

	private RoutingAdvertise createRoutingAdvertise(AadvertiseType type) {
		if (type == AadvertiseType.none) {
			return null;
		}
		RoutingAdvertise advertiseObj = new RoutingAdvertise();

		if (type == AadvertiseType.eth0 || type == AadvertiseType.both) {
			advertiseObj.setEth0(CLICommonFunc.getAhOnlyAct(CLICommonFunc
					.getYesDefault()));
		}

		if (type == AadvertiseType.eth1 || type == AadvertiseType.both) {
			advertiseObj.setEth1(CLICommonFunc.getAhOnlyAct(CLICommonFunc
					.getYesDefault()));
		}

		return advertiseObj;
	}

	private String prepareIpRange(String iprange) throws Exception {
		if (iprange != null) {
			iprange = iprange.replace('~', ' ');
			iprange = iprange.replace('-', ' ');
		}

		return iprange != null ? iprange : "";
	}

	private RoutingMatchMap prepareRoutingMatchMap(String name) {
		RoutingMatchMap matchMap = new RoutingMatchMap();
		matchMap.setName(name);
		matchMap.setOperation(AhEnumActValue.YES_WITH_VALUE);

		return matchMap;
	}

	private RoutingRouteMap prepareRoutingRouteMap(String name) {
		RoutingRouteMap routeMap = new RoutingRouteMap();
		routeMap.setName(name);
		routeMap.setOperation(AhEnumActValue.YES_WITH_VALUE);

		return routeMap;
	}
}
