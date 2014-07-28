/**
 *@Tools
 *@Overview functions
 */
(function($,AE){
	AE.namespace('AE.hm.tools');

	AE.hm.tools = {
		common : {
		 	rms : function(str){
				return str.replace(/\s+|-/g,'');			   
			}		 
		}
	}
})(jQuery,AE);
