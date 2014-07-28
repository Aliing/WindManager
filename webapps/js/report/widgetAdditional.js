;(function() {
	var root = this;
	root.REPORT_ADDITIONAL = {
		COMMON_CONFIG: {
			pie: {
				legendPercentage: true
			}
		}//[[!single config end]]
		,"login administrator": {
			table: {
				postColumn: [
					{
						title: {
							text: Aerohive.lang.chart.login.admin.terminateAll,
							html: function(text, options) {
								return '<a data-addi-click="click" class="terminate-all-title" href="javascript:void(0);">' + text + '</a>';
							},
							click: function(event, chart, options){
								$.post("reports.action",
										{
											operation: "removeSession",
											removeSessionId: "AllClient",
											jsonMode: true,
											ignore: new Date().getTime()
										},
										function(data, textStatus) {
											if (data.rs) {
												root.location.href = "login.action";
											} else {
												chart.requestDataFunc();
											}
										}, 
										"json");
							}
						},
						data: {
							text: Aerohive.lang.chart.login.admin.terminate,
							html: function(text, options) {
								return '<a data-addi-click="click" href="javascript:void(0);">' + text + '</a>';
							},
							click: function(event, chart, options){
								if (options && options.bkValue) {
									$.post("reports.action",
										{
											operation: "removeSession",
											removeSessionId: options.bkValue,
											jsonMode: true,
											ignore: new Date().getTime()
										},
										function(data, textStatus) {
											if (data.rs && data.needLogin) {
												root.location.href = "login.action";
											} else {
												chart.requestDataFunc();
											}
										}, 
										"json");
								}
							}
						}
					}
				]
			}
		}//[[!single config end]]
		,"bandwidth application": {
			pie: {
				legendPercentage: false
			}
		}//[[!single config end]]
		,"watchlist bandwidth application": {
			pie: {
				forkey: {
					93: {
						legendPercentage: false
					}
				}
			}
		}//[[!single config end]]
		,"system information": {
			list: {
				group: {
					forkey: {
						1: [
							{
								name: Aerohive.lang.chart.device.status.group.title,
								metrics: ['network devices - unmanaged', 'network devices - alarmed', 'network devices - disconfigured', 'network devices - up', 'network devices - down']
							},
							{
								name: Aerohive.lang.chart.client.status.group.title,
								metrics: ['current active client count', 'max concurrent client devices']							
							}
						]
					}
				}
			}
		}//[[!single config end]]
		,"bandwidth provider(interface)": {
			column: {
				sortBy: {
					forkey: {
						46: {
							direction: "desc",
							data: 0 // number for certain index, or some string for certain method, e.g. "total", "max"...
						}
					}
				}
			}
		}
	};
	
	root.REPORT_ADDITIONAL.FUNCS = (function() {
		var self = this;
		return {
			_getCertainOption: function(proNames) {
				var objTmp = self,
					blnExist = true;
				for (var i = 0; i < proNames.length; i++) {
					if (proNames[i] in objTmp) {
						objTmp = objTmp[proNames[i]];
					} else {
						blnExist = false;
						break;
					}
				}
				if (blnExist) {
					return objTmp;
				}
			},
			getAddiOption: function() {
				var args = arguments,
					argLen = args.length;
				if (argLen < 1) {
					return;
				}
				var blnGetCommonIfNo = false,
					proPos = 0,
					proNames = [];
				if (args[0] === true || args[0] === false) {
					blnGetCommonIfNo = args[0];
					proPos = 1;
				}
				if (argLen > proPos) {
					for (var i = proPos; i < argLen; i++) {
						proNames.push(args[i]);
					}
				} else {
					return;
				}
				var result = this._getCertainOption(proNames);
				if (typeof result === 'undefined' && blnGetCommonIfNo) {
					proNames[0] = 'COMMON_CONFIG';
					result = this._getCertainOption(proNames);
				}
				return result;
			}
		};
	}).call(root.REPORT_ADDITIONAL);
}).call(this);

//console.log(REPORT_ADDITIONAL.FUNCS.getAddiOption('system information', 'list', 'group', 'forkey'));
//console.log(REPORT_ADDITIONAL.FUNCS.getAddiOption(true,'bandwidth application1', 'pie', 'legendPercentage'));