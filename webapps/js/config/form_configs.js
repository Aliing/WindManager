/**
 *@Configs
 *@Overview
 */
(function($,AE){
	AE.namespace('AE.hm.configs.form');

	AE.hm.configs.form = {
		profile : {
			rules : {
				'displayName' : {
					required : true
				},
				'organization' : {
					required : true
				},
				'validTimeInfo.keepTime' : {
					/*required : true*/
					number: true,
					min: 0,
					max: 43200
				},
				'apnProfileInfos[0].apns[0].apn' : {
					required : true
				},
				'passcodeProfileInfos[0].maxPINAgeInDays' :{
					number: true,
					min :0,
					max: 730
				},
				'apnProfileInfos[0].apns[0].proxyPort' : {
					digits : true
				}
			},		  
			messages : {
				'validTimeInfo.keepTime': {
					number: '',
					min: 0,
					max: "The maximum lifetime value is 43200 minutes(30 days)."
				},
				'passcodeProfileInfos[0].maxPINAgeInDays' :{
					number: '',
					min : 0,
					max: 'Age is between 0 and 730 days.'
				}	   
			},
			onkeyup: false,
			//onsubmit:false,
			ignore : '.ignore',
			focusCleanup : true,
			errorElement : 'span',
			errorClass : 'form-error',
			errorPlacement : function(error,element){
				var name = element.attr('name');
				if(name == 'validTimeInfo.keepTime'){
					element.closest('td').append(error);
					return;
				}
				if(name = 'passcodeProfileInfos[0].maxPINAgeInDays'){
					element.closest('td').append(error);
					return;
				}
				element.after(error);
			}
		}
	};
})(jQuery,AE);
