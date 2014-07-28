package com.ah.util.devices.impl;
import com.ah.be.parameter.constant.parser.AhDeviceProductNameParser.DeviceProductType;
import com.ah.util.devices.ValueTypes;

public enum Device {
	// all devices
	ALL(null),

	// devices definition
	AP20(DeviceProductType.hiveap20),
	AP28(DeviceProductType.hiveap28),
	AP110(DeviceProductType.hiveap110),
	AP120(DeviceProductType.hiveap120),
	AP170(DeviceProductType.hiveap170),
	AP320(DeviceProductType.hiveap320),
	AP330(DeviceProductType.hiveap330),
	AP340(DeviceProductType.hiveap340),
	AP350(DeviceProductType.hiveap350),
	AP370(DeviceProductType.hiveap370),
	AP380(DeviceProductType.hiveap380),
	AP390(DeviceProductType.hiveap390),
	AP230(DeviceProductType.hiveap230),
	AP121(DeviceProductType.hiveap121),
	AP141(DeviceProductType.hiveap141),
	CVG(DeviceProductType.cvg),
	CVGAPPLIANCE(DeviceProductType.cvgappliance),
	BR100(DeviceProductType.br100),
	BR200(DeviceProductType.br200),
	BR200_WP(DeviceProductType.br200_wp),
	BR200_LTE_VZ(DeviceProductType.br200_lte_vz),
	SR24(DeviceProductType.sr24),
	SR2124P(DeviceProductType.sr2124p),
	SR2024P(DeviceProductType.sr2024p),
	SR2148P(DeviceProductType.sr2148p),
	SR48(DeviceProductType.sr48),

	// properties
	MODEL(ValueTypes.String),
	MODEL_NUM(ValueTypes.String),
	NAME(ValueTypes.String),
	SHORT_NAME(ValueTypes.String),
	IS_11n(ValueTypes.Boolean),
	IS_11ac(ValueTypes.Boolean),
	IS_OUTDOOR(ValueTypes.Boolean),
	SUPPORTED_WIFI0(ValueTypes.Boolean),
	SUPPORTED_WIFI1(ValueTypes.Boolean),
	SUPPORTED_LOCATE(ValueTypes.Boolean),
	SUPPORTED_LED(ValueTypes.Boolean),
	SUPPORTED_TEMPAlARM(ValueTypes.Boolean),
	SUPPORTED_FANSAlARM(ValueTypes.Boolean),
	SUPPORTED_UPDATE_IMAGE(ValueTypes.Boolean),
	SUPPORTED_IMPORT_CSV(ValueTypes.Boolean),
	SUPPORTED_SPECTRUMANALYSIS(ValueTypes.Boolean),
	SUPPORTED_HIVEOS_VERSIONS(ValueTypes.Enum, ValueTypes.String),
	ETH_PORTS(ValueTypes.Enum, ValueTypes.String),
	BONJOUR_PRIORITY(ValueTypes.String),
	SUPPORTED_BONJOUR(ValueTypes.Boolean),
	IS_SINGLERADIO(ValueTypes.Boolean),
	IS_DUALBAND(ValueTypes.Boolean),
	SUPPORTED_PSE_ETH(ValueTypes.Enum, ValueTypes.String),
	SUPPORTED_USB(ValueTypes.Boolean),
	SUPPORTED_HM_EXPRESS(ValueTypes.Boolean),
	SUPPORTED_STUDENTMANAGER(ValueTypes.Boolean);

	private Object[] typeTree;

	Device(Object type, Object... typeTree){
		this.typeTree = new Object[typeTree.length + 1];
		this.typeTree[0] = type;
		for (int i = 0; i < typeTree.length; i++) {
			this.typeTree[i + 1] = typeTree[i];
		}
	}

	public Object[] getTypeTree() {
		return typeTree;
	}
}