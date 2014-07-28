;Aerohive.lang.chart = (function() {
	return {
		nodata: {
			nodevice: 'None of the devices in your VHM meet the filter criteria. Please change your filter.',
			nodata: 'There is currently no data available for the specified time range or data filter.'
		},
		port: {
			type: {
				0: 'Access',
				1: 'Trunk',
				2: 'WAN'
			},
			linkState: {
				0: 'Down',
				1: 'Up'
			},
			authState: {
				0: 'No data and no voice vlan success',
				1: 'Data vlan success',
				2: 'Voice vlan success',
				3: 'Data and voice vlan success'
			},
			linkProtocol: {
				0: ''
			},
			stp: {
				state: {
					0: 'Discarding',
					1: 'Listening',
					2: 'Learning',
					3: 'Forwarding',
					4: 'Blocking',
					5: 'Disabled'
				},
				mode: {
					0: 'STP',
					1: 'MSTP',
					2: 'RSTP'
				},
				role: {
					0: 'Master',
					1: 'Alternate',
					2: 'Root',
					3: 'Designated',
					4: 'Disabled',
					5: 'Backup'
				},
				enable: {
					0: 'Disabled',
					1: 'Enabled'
				}
			}
		},
		tip: {
			monitor: {
				timelimited: 'Drill-down details are not available for data older than 30 days.',
				timelimitedTbl: 'Drill-down details are not available for data older than 30 days.',
				viewDetail: 'Click the chart to see details.',
				viewDrillDown: 'Click the chart to start drill down.',
				doubleClickViewDrillDown: 'Double-click the chart to start drill down.',
				doubleClickViewDetail: 'Double-click the chart to see details.'
			}
		},
		login: {
			admin: {
				terminate: 'Terminate',
				terminateAll: 'Terminate All'
			}
		},
		device: {
			status: {
				group: {
					title: {
						name: 'Aerohive Devices'
					}
				}
			}
		},
		client: {
			status: {
				group: {
					title: 'Connected Clients'
				}
			}
		}
	}
})();