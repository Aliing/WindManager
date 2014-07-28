/**
 *@App
 *@Overview
 */
(function($,AE){
	var Mod = AE.Mod,
		cfgs = AE.hm.configs.mdm,
		tools = AE.hm.tools,
		formCfg = AE.hm.configs.form,
		pfes = cfgs.profile,
		attrs = pfes.attrs,
		cla = pfes.cla;

	Mod.add('AE.hm.mdm.deviceInfo',{
		elems : {
			'#J-dataTable' : '_dataTableEl'		
		},
		events : {
			'click .mdm-set-item-title' : '_handleShowCon'		 
		},
		init : function(){
			this._initDataTable();	   
		},
		_handleShowCon : function(e){
			var t = $(e.target),type = t.attr(attrs.type),
				c = t.next(cla.mdmCon),f = type == 'off',
				name = t.attr('data-name');				 
			
			t[f ? 'addClass' : 'removeClass'](cla.mdmtitle);
			c[f ? 'show' : 'hide']();
			t.attr(attrs.type,pfes.state.swh[type]);
			// click location to trigger map init
			name =='location' && f && this._createMap();
		},
		_createMap : function(){
			var map = new AE.hm.widget.map();			 
			this._createMap = function(){};
		},
		_initDataTable : function(){
			this._dataTableEl.dataTable({
				sScrollY : '310px',
				iDisplayLength: '10000',
				bFilter : false,
				bLengthChange : false,
				bInfo : false
			});				 
		}
	});
})(jQuery,AE);
