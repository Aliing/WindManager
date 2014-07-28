/**
 *@Pluins 
 *@Overview
 *@ google map loader
 */
(function($,AE){
	AE.namespace('AE.hm.widget.map');

	AE.hm.widget.map = function(options,url){
		this.url = url || 'https://maps.googleapis.com/maps/api/js?key=AIzaSyCj_3hqAFVcAzUDACgwJY_g46Lt95u4OMo&sensor=false';
		
		this.defaultConfigs = {
			latitude : 30.289609,
			longitude : 120.117945,
			scaleControl: true,
		    zoom: 16,
			panControl: true,
			streetViewControl: false,
			zoomControl: true,
			myMapId : 'J-map'
		};
		this.config = $.extend(this.defaultConfigs,options||{});
		this.init();
	};

	$.extend(AE.hm.widget.map.prototype,{
		init : function(){
			//(window.google && window.google.maps) ? this._rendMap() : this._loadMap();		   
			window.google && window.google.maps && this._rendMap();
		},
		_rendMap : function(){
			var cfg = this.config, 
				google = window.google,
				center = new google.maps.LatLng(cfg.latitude,cfg.longitude),
				o = {
					center : center,
					mapTypeId : google.maps.MapTypeId.ROADMAP,
					zoomControlOptions : {
						style: google.maps.ZoomControlStyle.LARGE
					}
				},
				infowindow = new google.maps.InfoWindow(),
				map = new google.maps.Map(document.getElementById(cfg.myMapId),$.extend(cfg,o)),
				maker = new google.maps.Marker({
					position : center,
					map : map
				});	   

				google.maps.event.addListener(maker, 'click', function(event) {
					infowindow.open(map, maker);
			 	});
		},
		_loadMap : function(){
			$.getScript(this.url,$.proxy(this._rendMap,this));		   
		}
	});
})(jQuery,AE);
