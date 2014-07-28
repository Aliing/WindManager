<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="ah" uri="/ah-tags"%>
<%@page import="com.ah.bo.wlan.SsidProfile"%>
<script>
var KEY_MGMT_OPEN = <%=SsidProfile.KEY_MGMT_OPEN%>;
var KEY_MGMT_WPA2_PSK = <%=SsidProfile.KEY_MGMT_WPA2_PSK%>;
var KEY_MGMT_WPA_PSK = <%=SsidProfile.KEY_MGMT_WPA_PSK%>;
var KEY_MGMT_AUTO_WPA_OR_WPA2_PSK = <%=SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK%>;
var KEY_MGMT_WEP_PSK = <%=SsidProfile.KEY_MGMT_WEP_PSK%>;
var KEY_MGMT_DYNAMIC_WEP = <%=SsidProfile.KEY_MGMT_DYNAMIC_WEP%>;
var ACCESS_MODE_PSK = <%=SsidProfile.ACCESS_MODE_PSK%>;
var formName = 'wifiClinetPerferredSsid';
var changeDNAdminText = false;

function changeAccessMode(accessMode){   
    if (accessMode==1) {
        var mgmtKey = document.getElementById("mgmtKey");
        mgmtKey.length=0;
        mgmtKey.length=3;
        mgmtKey.options[0].value=KEY_MGMT_AUTO_WPA_OR_WPA2_PSK;
        mgmtKey.options[0].text='Auto-(WPA or WPA2)-PSK';
        mgmtKey.options[1].value=KEY_MGMT_WPA_PSK;
        mgmtKey.options[1].text='WPA-PSK (WPA Personal)';
        mgmtKey.options[2].value=KEY_MGMT_WPA2_PSK;
        mgmtKey.options[2].text='WPA2-PSK (WPA2 Personal)';
        mgmtKey.options[2].selected=true;
        mgmtKey.value=mgmtKey.options[2].value;
        mgmtKey.text=mgmtKey.options[2].text;
        show_tt(KEY_MGMT_WPA2_PSK);
        init_value();
    } else if (accessMode==4) {
        document.getElementById("hideKeyManagement").style.display="none";
        document.getElementById("hideAuthMethord").style.display="none";
        var mgmtKey = document.getElementById("mgmtKey");
        mgmtKey.length=0;
        mgmtKey.length=2;
        mgmtKey.options[0].value=KEY_MGMT_WEP_PSK;
        mgmtKey.options[0].text='WEP';
        mgmtKey.options[0].selected=true;
        mgmtKey.value=mgmtKey.options[0].value;
        mgmtKey.text=mgmtKey.options[0].text;
        mgmtKey.options[1].value=KEY_MGMT_DYNAMIC_WEP;
        mgmtKey.options[1].text='WEP 802.1X';
        show_tt(KEY_MGMT_WEP_PSK);
        init_value();
    } else if (accessMode==5) {
        document.getElementById("hideKeyManagement").style.display="none";
        document.getElementById("hideAuthMethord").style.display="none";
        var mgmtKey = document.getElementById("mgmtKey");
        mgmtKey.length=0;
        mgmtKey.length=1;
        mgmtKey.options[0].value=KEY_MGMT_OPEN;
        mgmtKey.options[0].text='Open';
        mgmtKey.options[0].selected=true;
        mgmtKey.value=mgmtKey.options[0].value;
        mgmtKey.text=mgmtKey.options[0].text; 
        show_tt(KEY_MGMT_OPEN);
        init_value();
    }
}

function show_fourth(selectValue)
{
   var mgmtKey = document.getElementById("mgmtKey").value;
   if (mgmtKey == KEY_MGMT_WEP_PSK) {
       if (selectValue=="3") {
          init_pass_value();
          document.getElementById("hideFourth").style.display="block";
          document.getElementById("hideFourth_one").style.display="block";
          document.getElementById("hideFourth_two").style.display="none";
          document.getElementById("hideFifth").style.display="none";
          document.getElementById("hideFifth_one").style.display="none";
          document.getElementById("hideFifth_two").style.display="none";
        }
       
       if (selectValue=="4") {
           init_pass_value();
           document.getElementById("hideFourth").style.display="none";
           document.getElementById("hideFourth_one").style.display="none";
           document.getElementById("hideFourth_two").style.display="none";
           document.getElementById("hideFifth").style.display="block";
           document.getElementById("hideFifth_one").style.display="block";
           document.getElementById("hideFifth_two").style.display="none";
         }
    }
}

function show_keyType5(expid1)
{
  var hideFifth_one = document.getElementById("hideFifth_one");
  var hideFifth_two = document.getElementById("hideFifth_two");
  hideFifth_one.style.display="none";
  hideFifth_two.style.display="none";
  init_pass_value();
  if (expid1=="0") {
      hideFifth_one.style.display="block";
  }
  if (expid1=="1") {
      document.getElementById("keyType5").value='1';
      hideFifth_two.style.display="block";
  }

}

function show_tt(expid1)
{
  var hideThird = document.getElementById("hideThird");
  var hideThird_one = document.getElementById("hideThird_one");
  var hideThird_two = document.getElementById("hideThird_two");
  
  var hideFourth = document.getElementById("hideFourth");
  var hideFourth_one = document.getElementById("hideFourth_one");
  var hideFourth_two = document.getElementById("hideFourth_two");
  var hideKeyManagement =  document.getElementById("hideKeyManagement");
  
  var hideFifth = document.getElementById("hideFifth");
  var hideFifth_one = document.getElementById("hideFifth_one");
  var hideFifth_two = document.getElementById("hideFifth_two");
  
  hideThird.style.display="none";
  hideThird_one.style.display="none";
  hideThird_two.style.display="none";
  
  hideFourth.style.display="none";
  hideFourth_one.style.display="none";
  hideFourth_two.style.display="none";
  
  hideFifth.style.display="none";
  hideFifth_one.style.display="none";
  hideFifth_two.style.display="none";

  var enc = document.getElementById("enc");
  var aut = document.getElementById("aut");
  document.getElementById("hideAuthMethord").style.display="none";
  if (expid1==KEY_MGMT_OPEN) {
      enc.length=0;
      enc.length=1;
      enc.options[0].value='0';
      enc.options[0].text='NONE';

      aut.length=0;
      aut.length=1;
      aut.options[0].value='0';
      aut.options[0].text='OPEN';
  }
  
  if ( expid1==KEY_MGMT_WPA2_PSK || expid1==KEY_MGMT_WPA_PSK) {

      enc.length=0;
      enc.length=2;
      enc.options[0].value='1';
      enc.options[0].text='CCMP (AES)';
      enc.options[1].value='2';
      enc.options[1].text='TKIP';

      if (expid1==KEY_MGMT_WPA2_PSK || expid1==KEY_MGMT_WPA_PSK) {
          hideThird.style.display="block";
          if (Get("keyType3").value=='0') {
              hideThird_one.style.display="block";
          } else {
              hideThird_two.style.display="block";
          }
          
          aut.length=0;
          aut.length=1;
          aut.options[0].value='0';
          aut.options[0].text='OPEN';
      }
  }
  
  if (expid1==KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
      hideThird.style.display="block";
      if (Get("keyType3").value=='0') {
          hideThird_one.style.display="block";
      } else {
          hideThird_two.style.display="block";
      }

      enc.length=0;
      enc.length=1;
      enc.options[0].value='5';
      enc.options[0].text='Auto-TKIP or CCMP (AES)';

      aut.length=0;
      aut.length=1;
      aut.options[0].value='0';
      aut.options[0].text='OPEN';
  }

  if (expid1==KEY_MGMT_WPA2_PSK) {
	  hideKeyManagement.style.display="block";
      hideThird.style.display="block";
      if (Get("keyType3").value=='0') {
          hideThird_one.style.display="block";
      } else {
          hideThird_two.style.display="block";
      }
      
      enc.length=0;
      enc.length=2;
      enc.options[0].value='1';
      enc.options[0].text='CCMP (AES)';
      enc.options[1].value='2';
      enc.options[1].text='TKIP';
      
      aut.length=0;
      aut.length=1;
      aut.options[0].value='0';
      aut.options[0].text='OPEN';
  }
  
  if (expid1==KEY_MGMT_WEP_PSK) {     
	  hideKeyManagement.style.display="block";
      hideFourth.style.display="block";
      document.getElementById("hideAuthMethord").style.display="block";
      if (Get("keyType4").value=='0') {
          hideFourth_one.style.display="block";
      } else {
          hideFourth_two.style.display="block";
      }
      
      enc.length=0;
      enc.length=2;
      enc.options[0].value='3';
      enc.options[0].text='WEP 104';
      enc.options[1].value='4';
      enc.options[1].text='WEP 40';

      aut.length=0;
      aut.length=2;
      aut.options[0].value='0';
      aut.options[0].text='OPEN';
      aut.options[1].value='2';
      aut.options[1].text='SHARED';
  }
  
  if (expid1==KEY_MGMT_DYNAMIC_WEP) {         
      enc.length=0;
      enc.length=2;
      enc.options[0].value='3';
      enc.options[0].text='WEP 104';
      enc.options[1].value='4';
      enc.options[1].text='WEP 40';

      aut.length=0;
      aut.length=1;
      aut.options[0].value='1';
      aut.options[0].text='EAP(802.1X)';
  }

}

function show_keyType3(expid1)
{
  var hideThird_one = document.getElementById("hideThird_one");
  var hideThird_two = document.getElementById("hideThird_two");
  hideThird_one.style.display="none";
  hideThird_two.style.display="none";
  init_pass_value();
  if (expid1=="0") {
      hideThird_one.style.display="block";
  }
  if (expid1=="1") {
      document.getElementById("keyType3").value='1';
      hideThird_two.style.display="block";
  }

}

function show_keyType4(expid1)
{
  var hideFourth_one = document.getElementById("hideFourth_one");
  var hideFourth_two = document.getElementById("hideFourth_two");
  hideFourth_one.style.display="none";
  hideFourth_two.style.display="none";
  init_pass_value();
  if (expid1=="0") {
      hideFourth_one.style.display="block";
  }
  if (expid1=="1") {
      document.getElementById("keyType4").value='1';
      hideFourth_two.style.display="block";
  }

}

function init_value()
{
  document.getElementById("keyType3").value='0';
  document.getElementById("keyType4").value='0';
  
}
function init_pass_value()
  {
      document.getElementById("keyType3").value='0';
      document.getElementById("keyType4").value='0';
      
      document.getElementById("firstKeyValue0").value='';
      document.getElementById("firstKeyValue0_1").value='';
      
      document.getElementById("firstKeyValue0_text").value='';
      document.getElementById("firstKeyValue0_1_text").value='';
      
      document.getElementById("firstConfirmValue0").value='';     
      document.getElementById("firstConfirmValue0_1").value='';  
      
      document.getElementById("firstConfirmValue0_text").value='';
      document.getElementById("firstConfirmValue0_1_text").value='';
            
      document.getElementById("firstKeyValue1").value='';      
      document.getElementById("firstKeyValue1_1").value='';
      
      document.getElementById("firstKeyValue1_text").value='';
      document.getElementById("firstKeyValue1_1_text").value='';
      
      document.getElementById("firstConfirmValue1").value='';
      document.getElementById("firstConfirmValue1_1").value='';
      
      document.getElementById("firstConfirmValue1_text").value='';              
      document.getElementById("firstConfirmValue1_1_text").value='';
      
      document.getElementById("firstKeyValue2").value='';      
      document.getElementById("firstKeyValue2_1").value='';
      
      document.getElementById("firstKeyValue2_text").value='';
      document.getElementById("firstKeyValue2_1_text").value='';
      
      document.getElementById("firstConfirmValue2").value='';
      document.getElementById("firstConfirmValue2_1").value='';
      
      document.getElementById("firstConfirmValue2_text").value='';              
      document.getElementById("firstConfirmValue2_1_text").value='';
  }
  
function submitAction(operation) {
	if(operation == 'cancel' + '<s:property value="lstForward"/>'){
        showProcessing();
        document.forms[formName].operation.value = operation;
        document.forms[formName].submit();
	}else if (operation == 'create'+'<s:property value="lstForward"/>'
            || operation == 'update' + '<s:property value="lstForward"/>'){
		        if (validate(operation)) {
	                showProcessing();
	                document.forms[formName].operation.value = operation;
	                document.forms[formName].submit();
	            }
	}
}

function submitActionJson(operation) {
	if (operation=='cancel') {
		parent.closeIFrameDialog();
		return false;
	}
	if (validate(operation)) {
		var url =  "<s:url action='wifiClinetPerferredSsid' includeParams='none' />" +
		"?jsonMode=true"+
		"&ignore="+new Date().getTime();
		document.forms["wifiClinetPerferredSsid"].operation.value = operation;
		YAHOO.util.Connect.setForm(document.getElementById("wifiClinetPerferredSsid"));
		var transaction = YAHOO.util.Connect.asyncRequest('POST', url, {success : succWifiPreferredSsid, failure : resultDoNothing, timeout: 60000}, null);	
	}
}

var resultDoNothing = function(o) {
//	alert("failed.");
};

var succWifiPreferredSsid = function (o) {
	eval("var details = " + o.responseText);
	if (details.result) {
		var leftOptions_preferredSsids = parent.Get("leftOptions_preferredSsids");
		if($(leftOptions_preferredSsids).find(" option[value='"+details.id+"']").length ==0)
			hm.util.insertSelectValue(details.id, details.ssidname, leftOptions_preferredSsids, false, false);
		parent.closeIFrameDialog();
	} else {
		hm.util.displayJsonErrorNote(details.m);
	}
}

function validate(operation){
   if (!validateSsid()) {
        return false;
    }
   
   if (!validateGeneral()) {
       return false;
   }
   
   return true;
}

function validateSsid() {
    var inputElement = document.getElementById(formName + "_dataSource_ssid");
    var message = hm.util.validateSsid(inputElement.value, '<s:text name="config.ssid.head.ssid" />');
    if (message != null) {
        hm.util.reportFieldError(inputElement, message);
        inputElement.focus();
        return false;
    }
    if (inputElement.value.length > 32) {
          hm.util.reportFieldError(inputElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.head.ssid" /></s:param><s:param><s:text name="config.ssid.ssidName_range" /></s:param></s:text>');
          inputElement.focus();
          return false;
    }
    
    var desInputEle = document.getElementById(formName + "_dataSource_comment");
    if (desInputEle.value.length > 64) {
          hm.util.reportFieldError(desInputEle, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.description" /></s:param><s:param><s:text name="config.ssid.description_range" /></s:param></s:text>');
          desInputEle.focus();
          return false;
    }
    return true;
}

function validateGeneral() {
    var mgmtKey = document.getElementById("mgmtKey").value;
    
    if (mgmtKey == KEY_MGMT_WPA2_PSK
            || mgmtKey == KEY_MGMT_WPA_PSK
            || mgmtKey == KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
	        if (document.getElementById("keyType3").value == '0') {
	            if(!validateKeyConfirmValue("firstKeyValue0","firstConfirmValue0","chkToggleDisplay0")) {
	                return false;
	            }
	        }
	        if (document.getElementById("keyType3").value == '1') {
	            if(!validateKeyConfirmValue("firstKeyValue0_1","firstConfirmValue0_1","chkToggleDisplay0_1")) {
	                return false;
	            }
	        }
     }

    if (mgmtKey == KEY_MGMT_WEP_PSK ) {
        if (document.getElementById("enc").value == '3') {
             if (document.getElementById("keyType4").value == '0') {                
                if(!validateKeyConfirmValueFour("firstKeyValue1" , "firstConfirmValue1" , "1","0","chkToggleDisplay1_1")) {
                    return false;
                }
             }

             if (document.getElementById("keyType4").value == '1') {
                if(!validateKeyConfirmValueFour("firstKeyValue1_1" , "firstConfirmValue1_1" , "1","0","chkToggleDisplay1_1_1")) {
                    return false;
                }
             }
        }
        if (document.getElementById("enc").value == '4') {
            if (document.getElementById("keyType5").value == '0') {  
                if(!validateKeyConfirmValueFour("firstKeyValue2" , "firstConfirmValue2" , "1","0","chkToggleDisplay2_1")) {
                    return false;
                }
             }

             if (document.getElementById("keyType5").value == '1') {
                if(!validateKeyConfirmValueFour("firstKeyValue2_1" , "firstConfirmValue2_1" , "1","0","chkToggleDisplay2_1_1")) {
                    return false;
                }
             }
        }
    }
    return true;
}

function validateKeyConfirmValue(elementKey,elementConfirm,checkBoxId) {
    var keyElement;
    var confirmElement;
    if (document.getElementById(checkBoxId).checked) {
      keyElement = document.getElementById(elementKey);
      confirmElement = document.getElementById(elementConfirm);
    } else {
      keyElement = document.getElementById(elementKey+ "_text");
      confirmElement = document.getElementById(elementConfirm+ "_text");
    }
    
    if (keyElement.value.length ==0) {
         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue" /></s:param></s:text>');
         keyElement.focus();
         return false;
	}
	
	if (confirmElement.value.length == 0) {
	     hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue" /></s:param></s:text>');
	     confirmElement.focus();
	     return false;
	}
	
    if (keyElement.name=="firstKeyValue0")
    {
        if (keyElement.value.length < 8) {
              hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
              keyElement.focus();
              return false;
        }

        if (confirmElement.value.length < 8) {
              hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range" /></s:param></s:text>');
              confirmElement.focus();
              return false;
        }
    }

    if (keyElement.name=="firstKeyValue0_1")
    {
        if (keyElement.value.length < 64) {
              hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range_1" /></s:param></s:text>');
              keyElement.focus();
              return false;
        }
        
        var message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue" />');

        if (message != null) {
              hm.util.reportFieldError(keyElement, message);
              keyElement.focus();
              return false;
        }

        if (confirmElement.value.length < 64) {
              hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range_1" /></s:param></s:text>');
              confirmElement.focus();
              return false;
        }
        
        var message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue" />');

        if (message != null) {
              hm.util.reportFieldError(confirmElement, message);
              confirmElement.focus();
              return false;
        }
    }

    if (keyElement.value != confirmElement.value) {
          hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue" /></s:param></s:text>');
          keyElement.focus();
          return false;
    }

    return true;
}    

function validateKeyConfirmValueFour(elementKey,elementConfirm,defaultKeyIndexValue,checkBlank,checkBoxId) {
    var keyElement;
    var confirmElement;
    if (document.getElementById(checkBoxId).checked) {
      keyElement = document.getElementById(elementKey);
      confirmElement = document.getElementById(elementConfirm);
    } else {
      keyElement = document.getElementById(elementKey+ "_text");
      confirmElement = document.getElementById(elementConfirm+ "_text");
    }
    
    if (keyElement.value.length ==0) {
         hm.util.reportFieldError(keyElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.keyValue" /></s:param></s:text>');
         keyElement.focus();
         return false;
    }
    
    if (confirmElement.value.length == 0) {
         hm.util.reportFieldError(confirmElement, '<s:text name="error.requiredField"><s:param><s:text name="config.ssid.confirmValue" /></s:param></s:text>');
         confirmElement.focus();
         return false;
    }
    
    if (keyElement.name=="firstKeyValue2")
    {
        if (keyElement.value.length < 5) {
              hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
              keyElement.focus();
              return false;
        }

        if (confirmElement.value.length < 5) {
              hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range2" /></s:param></s:text>');
              confirmElement.focus();
              return false;
        }
    }
    
    if (keyElement.name=="firstKeyValue1")
    {
        if (keyElement.value.length < 13) {
              hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
              keyElement.focus();
              return false;
        }

        if (confirmElement.value.length < 13) {
              hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range1" /></s:param></s:text>');
              confirmElement.focus();
              return false;
        }
    }

    if (keyElement.name=="firstKeyValue1_1")
    {
        if (keyElement.value.length < 26) {
              hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
              keyElement.focus();
              return false;
        }
        
        var message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue" />');

        if (message != null) {
              hm.util.reportFieldError(keyElement, message);
              keyElement.focus();
              return false;
        }

        if (confirmElement.value.length < 26) {
              hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range1_1" /></s:param></s:text>');
              confirmElement.focus();
              return false;
        }
        
        var message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue" />');

        if (message != null) {
              hm.util.reportFieldError(confirmElement, message);
              confirmElement.focus();
              return false;
        }
    }
    
    if (keyElement.name=="firstKeyValue2_1")
    {
        if (keyElement.value.length < 10) {
              hm.util.reportFieldError(keyElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.keyValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
              keyElement.focus();
              return false;
        }
        
        var message = hm.util.validateHexRange(keyElement.value, '<s:text name="config.ssid.keyValue" />');

        if (message != null) {
              hm.util.reportFieldError(keyElement, message);
              keyElement.focus();
              return false;
        }

        if (confirmElement.value.length < 10) {
              hm.util.reportFieldError(confirmElement, '<s:text name="error.keyValueRange"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue_range2_1" /></s:param></s:text>');
              confirmElement.focus();
              return false;
        }
        
        var message = hm.util.validateHexRange(confirmElement.value, '<s:text name="config.ssid.confirmValue" />');

        if (message != null) {
              hm.util.reportFieldError(confirmElement, message);
              confirmElement.focus();
              return false;
        }
    }

    if (keyElement.value != confirmElement.value) {
          hm.util.reportFieldError(confirmElement, '<s:text name="error.notEqual"><s:param><s:text name="config.ssid.confirmValue" /></s:param><s:param><s:text name="config.ssid.keyValue" /></s:param></s:text>');
          keyElement.focus();
          return false;
    }

    return true;
}

function insertPageContext() {
    <s:if test="%{lstTitle!=null && lstTitle.size>1}">
        document.writeln('<td class="crumb" nowrap>');
        <s:iterator value="lstTitle">
            document.writeln(" <s:property/> ");
        </s:iterator>
        document.writeln('</td>');
    </s:if>
    <s:else>
        document.writeln('<td class="crumb" nowrap><a href="<s:url action="wifiClinetPerferredSsid" includeParams="none"/>"><s:property value="%{selectedL2Feature.description}" /></a> &gt; ');
        <s:if test="%{dataSource.id == null}">
            document.writeln('New </td>');
        </s:if>
        <s:else>
            <s:if test="%{dataSource.defaultFlag}">
                document.writeln('Default Value \'<s:property value="changedSsidName" />\'</td>');
            </s:if>
            <s:else>
                document.writeln('Edit \'<s:property value="changedSsidName" />\'</td>');
            </s:else>
        </s:else>
    </s:else>
}
</script>
<div id="content">
    <s:form action="wifiClinetPerferredSsid">
		<s:if test="%{jsonMode == true}">
			<s:hidden name="id" />
			<s:hidden name="operation" />
			<s:hidden name="jsonMode" />
			<div class="topFixedTitle">
				<table border="0" cellspacing="0" cellpadding="0"  width="100%">
					<tr>
						<td align="left">
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td><img src="<s:url value="/images/hm_v2/profile/hm-icon-ssid-2-4-big.png" includeParams="none"/>"
									width="40" height="40" alt="" class="dblk" />
								</td>
								<s:if test="%{dataSource.id == null}">
								<td class="dialogPanelTitle"><s:text name="config.title.wfcmssid"/></td>
								</s:if>
								<s:else>
								<td class="dialogPanelTitle"><s:text name="config.title.wfcmssid.edit"/></td>
								</s:else>
								<td style="padding-left:10px;">
									<a href="javascript:void(0);" onclick="parent.openHelpLinkPage('<s:property value="helpLink" />');" >
										<img src="<s:url value="/images/hm_v2/profile/HM-icon-help-button.png" includeParams="none"/>"
											alt="" class="dblk"/>
									</a>
								</td>
							</tr>
						</table>
						</td>
						<td align="right">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="npcButton"><a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('cancel')" title="<s:text name="config.v2.select.user.profile.popup.cancel"/>"><span><s:text name="config.v2.select.user.profile.popup.cancel"/></span></a></td>
									<td width="20px">&nbsp;</td>
									<td class="npcButton">
									<s:if test="%{dataSource.id == null}">
										<s:if test="%{writeDisabled == 'disabled'}">
											&nbsp;</td>
										</s:if>
										<s:else>
											<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('create');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
										</s:else>
									</s:if>
									<s:else>
										<s:if test="%{updateDisabled == 'disabled'}">
											&nbsp;</td>
										</s:if>
										<s:else>
											<a href="javascript:void(0);" class="btCurrent" onclick="submitActionJson('update');" title="<s:text name="button.update"/>"><span><s:text name="button.update"/></span></a></td>
										</s:else>
									</s:else>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</s:if>
		<s:if test="%{jsonMode==false}">
	        <table width="100%" border="0" cellspacing="0" cellpadding="0">
		        <tr>
		            <td><tiles:insertDefinition name="context" /></td>
		        </tr>
		        <tr>
		            <td class="buttons">
			            <table border="0" cellspacing="0" cellpadding="0">
			                <tr>
			                    <s:if test="%{dataSource.id == null}">
			                        <td><input type="button" name="ignore" value="<s:text name="button.create"/>"
			                            class="button" onClick="submitAction('create<s:property value="lstForward"/>');"
			                            <s:property value="writeDisabled" />></td>
			                    </s:if>
			                    <s:else>
			                        <td><input type="button" name="ignore" value="<s:text name="button.update"/>"
			                            class="button" id="updateSSIDButton"
			                            onClick="submitAction('update<s:property value="lstForward"/>');"
			                            <s:property value="updateDisabled" />></td>
			                    </s:else>
			                    <s:if test="%{lstForward == null || lstForward == ''}">
			                        <td><input type="button" name="cancel" value="Cancel"
			                            class="button" id="cancelSSIDButton"
			                            onClick="submitAction('cancel');">
			                        </td>
			                    </s:if>
			                    <s:else>
			                        <td><input type="button" name="cancel" value="Cancel"
			                            class="button"
			                            onClick="submitAction('cancel<s:property value="lstForward"/>');">
			                        </td>
			                    </s:else>
			                 </tr>
			            </table>
		            </td>
		        </tr>
	        </table>
	    </s:if>
	   	<s:if test="%{jsonMode==true}">
	   		<table style="padding-top:60px;" width="100%" border="0" cellspacing="0" cellpadding="0">
	    </s:if>
	    <s:if test="%{jsonMode==false}">
	    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	    </s:if>
	        <tr>
	            <td><tiles:insertDefinition name="notes" /></td>
	        </tr>
            <tr>
                 <td style="padding-top: 5px;">	   
                    <table class="editBox" border="0" cellspacing="0" cellpadding="0" width="720px" id="ssidEditTable">
                        <tr>
                            <td>
	                            <table border="0" cellspacing="0" cellpadding="0" width="100%">
	                                <tr>
	                                    <td height="4"></td>
	                                </tr>
	                                <tr>
	                                    <td class="labelT1">
	                                        <s:text name="config.ssid.head.ssid" /><font color="red"><s:text name="*"/></font>
	                                    </td>
	                                    <td><s:textfield name="dataSource.ssid" size="24"
	                                        onkeypress="return hm.util.keyPressPermit(event,'ssid');"
	                                        maxlength="32"/>&nbsp;
	                                       <s:text name="config.ssid.ssidName_range" />
	                                     </td>
	                                 </tr>
	                                <tr style="display:<s:property value="hideSsid"/>">
	                                    <td class="labelT1"><s:text name="config.ssid.description" /></td>
	                                    <td><s:textfield name="dataSource.comment" size="50"
	                                         maxlength="64" />&nbsp;<s:text
	                                        name="config.ssid.description_range" /></td>
	                                </tr>
				                 </table>
                            </td>
                        </tr>
                        <tr>
                            <td height="3"></td>
                        </tr>
                        <tr>
                            <td style="padding: 4px 4px 4px 4px;"> 
                                <fieldset><legend><s:text name="config.ssid.accessSecurity" /></legend>
                                    <table cellspacing="0" cellpadding="0" border="0" width="100%">
	                                     <tr> 
	                                        <td style="padding:0 2px 5px 6px">
	                                            <table cellspacing="0" cellpadding="0" border="0" width="100%">
													<tr>
														<td>
														   <table cellspacing="0" cellpadding="0" border="0" width="100%">
														      <tr>
			                                                    <td align="left"  style="padding-left: 80px">
			                                                        <table cellspacing="0" cellpadding="0" border="0">
			                                                            <tr>
			                                                                <td width="190px"><s:radio label="Gender" id="accessMode"
			                                                                            name="dataSource.accessMode"
			                                                                            list="#{1:'WPA/WPA2 PSK (Personal)'}"
			                                                                            value="%{dataSource.accessMode}"
			                                                                            onclick="changeAccessMode(1);" /></td>
			                                                            </tr>
							                                             <tr>
			                                                                <td colspan="1" nowrap="nowrap" width="100%">
			                                                                    <table  cellspacing="0" cellpadding="0" border="0" width="100%">
			                                                                        <tr>
			                                                                            <td>
			                                                                                <table  cellspacing="0" cellpadding="0" border="0" width="100%">
			                                                                                    <tr>
			                                                                                        <td class="sepLine">
			                                                                                            <img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
			                                                                                        </td>
			                                                                                    </tr>
			                                                                                </table>
			                                                                            </td>
			                                                                            <td width="45px" align="center">
			                                                                                <table  cellspacing="0" cellpadding="0" border="0">
			                                                                                    <tr>
			                                                                                        <td>
			                                                                                            <font style="font-size: 10px; color:#474646">Secure</font>
			                                                                                        </td>
			                                                                                    </tr>
			                                                                                </table>
			                                                                            </td>
			                                                                            <td>
			                                                                                <table  cellspacing="0" cellpadding="0" border="0" width="100%">
			                                                                                    <tr>
			                                                                                        <td class="sepLine">
			                                                                                            <img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
			                                                                                        </td>
			                                                                                    </tr>
			                                                                                </table>
			                                                                            </td>
			                                                                        </tr>
			                                                                    </table>
			                                                                </td>
			                                                            </tr>
			                                                        </table>
			                                                      </td>
				                                              </tr>
														   </table>
														 </td>
														 <td align="right" width="140px" style="padding-right: 80px">
	                                                        <table cellspacing="0" cellpadding="0" border="0" width="100%">
	                                                            <tr>
	                                                                <td width="60px" align="left"><s:radio label="Gender" id="accessMode"
	                                                                            name="dataSource.accessMode"
	                                                                            list="#{4:'WEP'}"
	                                                                            value="%{dataSource.accessMode}"
	                                                                            onclick="changeAccessMode(4);" /></td>
	                                                                <td width="60px" align="center"><s:radio label="Gender" id="accessMode"
	                                                                            name="dataSource.accessMode"
	                                                                            list="#{5:'Open'}"
	                                                                            value="%{dataSource.accessMode}"
	                                                                            onclick="changeAccessMode(5);" /></td>
	                                                            </tr>
	                                                            <tr>
	                                                                <td colspan="2" nowrap="nowrap" width="100%">
	                                                                    <table  cellspacing="0" cellpadding="0" border="0" width="100%">
	                                                                        <tr>
	                                                                            <td>
	                                                                                <table  cellspacing="0" cellpadding="0" border="0" width="100%">
	                                                                                    <tr>
	                                                                                        <td class="sepLine">
	                                                                                            <img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
	                                                                                        </td>
	                                                                                    </tr>
	                                                                                </table>
	                                                                            </td>
	                                                                            <td width="60px" align="center">
	                                                                                <table  cellspacing="0" cellpadding="0" border="0">
	                                                                                    <tr>
	                                                                                        <td>
	                                                                                            <font style="font-size: 10px; color:#474646">Not Secure</font>
	                                                                                        </td>
	                                                                                    </tr>
	                                                                                </table>
	                                                                            </td>
	                                                                            <td>
	                                                                                <table  cellspacing="0" cellpadding="0" border="0" width="100%">
	                                                                                    <tr>
	                                                                                        <td class="sepLine">
	                                                                                            <img src="<s:url value="/images/spacer.gif"/>" height="1" class="dblk" />
	                                                                                        </td>
	                                                                                    </tr>
	                                                                                </table>
	                                                                            </td>
	                                                                        </tr>
	                                                                    </table>
	                                                                </td>
	                                                            </tr>
	                                                        </table>
	                                                    </td>
												    </tr>
	                                            </table>
	                                        </td>   
	                                    </tr>
	                                    
					                     <tr>
				                           <td>
				                               <div style="display:<s:property value="hideKeyManagement"/>" id="hideKeyManagement">
				                               <table border="0" cellspacing="0" cellpadding="0" width="100%">
				                                   <tr>
				                                       <td class="labelT1" width="150px"><s:text
				                                           name="config.ssid.keyManagement" /></td>
				                                       <td><s:select id="mgmtKey" name="dataSource.mgmtKey"
				                                           list="%{enumKeyMgmt}" listKey="key" listValue="value"
				                                           value="dataSource.mgmtKey"
				                                           onchange="show_tt(this.options[this.selectedIndex].value);"
				                                           cssStyle="width: 280px;" /></td>
				                                   </tr>
				
				                                   <tr>
				                                       <td class="labelT1"><s:text
				                                           name="config.ssid.encriptionMethord" /></td>
				                                       <td><s:if test="%{dataSource.mgmtKey == 0}">
				                                           <s:select id="enc" name="dataSource.encryption"
				                                               list="#{'0':'NONE'}" value="dataSource.encryption"
				                                               onchange="show_fourth(this.options[this.selectedIndex].value);"
				                                               cssStyle="width: 280px;" />
				                                       </s:if> <s:elseif
				                                           test="%{dataSource.mgmtKey == 1 || dataSource.mgmtKey == 2 || dataSource.mgmtKey == 3 || dataSource.mgmtKey == 4}">
				                                           <s:select id="enc" name="dataSource.encryption"
				                                               list="#{'1':'CCMP (AES)', '2':'TKIP'}"
				                                               value="dataSource.encryption"
				                                               onchange="show_fourth(this.options[this.selectedIndex].value);"
				                                               cssStyle="width: 280px;" />
				                                       </s:elseif> <s:elseif
				                                           test="%{dataSource.mgmtKey == 5 || dataSource.mgmtKey == 6}">
				                                           <s:select id="enc" name="dataSource.encryption"
				                                               list="#{'5':'Auto-TKIP or CCMP (AES)'}"
				                                               value="dataSource.encryption"
				                                               onchange="show_fourth(this.options[this.selectedIndex].value);"
				                                               cssStyle="width: 280px;" />
				                                       </s:elseif> <s:else>
				                                           <s:select id="enc" name="dataSource.encryption"
				                                               list="#{'3':'WEP 104', '4':'WEP 40'}"
				                                               value="dataSource.encryption"
				                                               onchange="show_fourth(this.options[this.selectedIndex].value);"
				                                               cssStyle="width: 280px;" />
				                                       </s:else></td>
				                                   </tr>
				                               </table>
				                               </div>
				                           </td>
				                       </tr>
				                       
				                       <tr>
	                                       <td>
	                                            <div style="display:<s:property value="hideAuthMethord"/>" id="hideAuthMethord">
	                                            <table border="0" cellspacing="0" cellpadding="0" width="100%">
	                                                <tr>
	                                                    <td class="labelT1" width="150px"><s:text
	                                                        name="config.ssid.authenticationMethord" />
	                                                    </td>
	                                                    <td><s:if
	                                                        test="%{dataSource.mgmtKey == 0 || dataSource.mgmtKey == 2 || dataSource.mgmtKey == 4 || dataSource.mgmtKey == 6}">
	                                                        <s:select id="aut" name="dataSource.authentication"
	                                                            list="#{'0':'OPEN'}" value="dataSource.authentication"
	                                                            cssStyle="width: 280px;" />
	                                                    </s:if> <s:elseif
	                                                        test="%{dataSource.mgmtKey == 1 || dataSource.mgmtKey == 3 || dataSource.mgmtKey == 5 || dataSource.mgmtKey == 8}">
	                                                        <s:select id="aut" name="dataSource.authentication"
	                                                            list="#{'1':'EAP (802.1X)'}" value="dataSource.authentication"
	                                                            cssStyle="width: 280px;" />
	                                                    </s:elseif> <s:else>
	                                                        <s:select id="aut" name="dataSource.authentication"
	                                                            list="#{'0':'OPEN', '2':'SHARED'}"
	                                                            value="dataSource.authentication" cssStyle="width: 280px;" />
	                                                    </s:else></td>
	                                                    
	                                                </tr>
	                                            </table>
	                                            </div>
	                                        </td>
	                                    </tr>   
	                                    
	                                   <tr>
	                                        <td>
	                                            <div style="display:<s:property value="hideThird"/>"
	                                                id="hideThird">
	                                                <table border="0" cellspacing="0" cellpadding="0" width="100%">
	                                                    <tr>
	                                                        <td class="labelT1" width="150px"><s:text
	                                                            name="config.ssid.keyType" /></td>
	                                                        <td><s:select id="keyType3" name="keyType3"
	                                                            list="#{'0':'ASCII Key', '1':'Hex Key'}"
	                                                            onchange="show_keyType3(this.options[this.selectedIndex].value);"
	                                                            value="keyType3" cssStyle="width:280px;" /></td>
	                                                    </tr>
	                                                </table>
	                                            </div>
                                                <div style="display:<s:property value="hideThird_one"/>" id="hideThird_one">
                                                    <table border="0" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td class="labelT1" width="150px"><s:text
                                                                name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstKeyValue0" name="firstKeyValue0"
                                                                size="50" maxlength="63" showPassword="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
                                                                <s:textfield id="firstKeyValue0_text" name="firstKeyValue0"
                                                                size="50" maxlength="63" cssStyle="display:none" disabled="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'ssid');"/></td>
                                                            <td>&nbsp;<s:text name="config.ssid.keyValue_range" /></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstConfirmValue0" showPassword="true"
                                                                name="firstConfirmValue0" value="%{firstKeyValue0}" size="50"
                                                                maxlength="63" 
                                                                onkeypress="return hm.util.keyPressPermit(event,'ssid');"/>
                                                                <s:textfield id="firstConfirmValue0_text"
                                                                name="firstConfirmValue0" value="%{firstKeyValue0}" size="50"
                                                                maxlength="63" cssStyle="display:none" disabled="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'ssid');"/></td>
                                                            <td>
                                                                <table border="0" cellspacing="0" cellpadding="0">
                                                                    <tr>
                                                                        <td>
                                                                            <s:checkbox id="chkToggleDisplay0" name="ignore" value="true" disabled="%{writeDisable4Struts}"
                                                                                onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue0','firstConfirmValue0'],['firstKeyValue0_text','firstConfirmValue0_text']);" />
                                                                        </td>
                                                                        <td>
                                                                            <s:text name="admin.user.obscurePassword" />
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </div>
                                                <div style="display:<s:property value="hideThird_two"/>"  id="hideThird_two">
                                                    <table border="0" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td class="labelT1" width="150px"><s:text
                                                                name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstKeyValue0_1" showPassword="true"
                                                                name="firstKeyValue0_1" size="50" maxlength="64"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" />
                                                                <s:textfield id="firstKeyValue0_1_text" cssStyle="display:none" disabled="true"
                                                                name="firstKeyValue0" size="50" maxlength="64"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
                                                            <td>&nbsp;<s:text name="config.ssid.keyValue_range_1" /></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstConfirmValue0_1" showPassword="true"
                                                                name="firstConfirmValue0_1" value="%{firstKeyValue0_1}"
                                                                size="50" maxlength="64"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" />
                                                                <s:textfield id="firstConfirmValue0_1_text"
                                                                name="firstConfirmValue0" value="%{firstKeyValue0_1}"
                                                                size="50" maxlength="64" cssStyle="display:none" disabled="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
                                                            <td>
                                                                <table border="0" cellspacing="0" cellpadding="0">
                                                                    <tr>
                                                                        <td>
                                                                            <s:checkbox id="chkToggleDisplay0_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
                                                                                onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue0_1','firstConfirmValue0_1'],['firstKeyValue0_1_text','firstConfirmValue0_1_text']);" />
                                                                        </td>
                                                                        <td>
                                                                            <s:text name="admin.user.obscurePassword" />
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </div>
                                            
                                            <div style="display:<s:property value="hideFourth"/>"
                                                id="hideFourth">
                                                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                                    <tr>
                                                        <td class="labelT1" width="150px"><s:text
                                                            name="config.ssid.keyType" /></td>
                                                        <td><s:select id="keyType4" name="keyType4"
                                                            list="#{'0':'ASCII Key', '1':'Hex Key'}" value="keyType4"
                                                            onchange="show_keyType4(this.options[this.selectedIndex].value);"
                                                            cssStyle="width:280px;" /></td>
                                                    </tr>
                                                </table>
                                            </div>
                                                <div style="display:<s:property value="hideFourth_one"/>" id="hideFourth_one">
                                                    <table border="0" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td class="labelT1" width="150px"><s:text
                                                                name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstKeyValue1" name="firstKeyValue1"
                                                                size="50" maxlength="13" showPassword="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/>
                                                                <s:textfield id="firstKeyValue1_text" name="firstKeyValue1"
                                                                size="50" maxlength="13" cssStyle="display:none" disabled="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
                                                            <td>&nbsp;<s:text name="config.ssid.keyValue_range1" /></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstConfirmValue1" showPassword="true"
                                                                name="firstConfirmValue1" value="%{firstKeyValue1}" size="50"
                                                                maxlength="13" 
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/>
                                                                <s:textfield id="firstConfirmValue1_text" cssStyle="display:none" disabled="true"
                                                                name="firstConfirmValue1" value="%{firstKeyValue1}" size="50"
                                                                maxlength="13" 
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
                                                            <td>
                                                                <table border="0" cellspacing="0" cellpadding="0">
                                                                    <tr>
                                                                        <td>
                                                                            <s:checkbox id="chkToggleDisplay1_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
                                                                                onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue1','firstConfirmValue1'],['firstKeyValue1_text','firstConfirmValue1_text']);" />
                                                                        </td>
                                                                        <td>
                                                                            <s:text name="admin.user.obscurePassword" />
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>                                                        
                                                    </table>
                                                </div>
                                                <div style="display:<s:property value="hideFourth_two"/>" id="hideFourth_two">
                                                    <table border="0" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td class="labelT1" width="150px"><s:text
                                                                name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstKeyValue1_1" showPassword="true"
                                                                name="firstKeyValue1_1" size="50" maxlength="26"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" />
                                                                <s:textfield id="firstKeyValue1_1_text" cssStyle="display:none" disabled="true"
                                                                name="firstKeyValue1_1" size="50" maxlength="26"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
                                                            <td>&nbsp;<s:text name="config.ssid.keyValue_range1_1" /></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstConfirmValue1_1" showPassword="true"
                                                                name="firstConfirmValue1_1" value="%{firstKeyValue1_1}"
                                                                size="50" maxlength="26"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" />
                                                                <s:textfield id="firstConfirmValue1_1_text" cssStyle="display:none" disabled="true"
                                                                name="firstConfirmValue1_1" value="%{firstKeyValue1_1}"
                                                                size="50" maxlength="26"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
                                                            <td>
                                                                <table border="0" cellspacing="0" cellpadding="0">
                                                                    <tr>
                                                                        <td>
                                                                            <s:checkbox id="chkToggleDisplay1_1_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
                                                                                onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue1_1','firstConfirmValue1_1'],['firstKeyValue1_1_text','firstConfirmValue1_1_text']);" />
                                                                        </td>
                                                                        <td>
                                                                            <s:text name="admin.user.obscurePassword" />
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>                                                                                                            
                                                    </table>
                                                </div> 
                                                                                         
	                                            <div style="display:<s:property value="hideFifth"/>" id="hideFifth">
	                                                <table border="0" cellspacing="0" cellpadding="0" width="100%">
	                                                    <tr>
	                                                        <td class="labelT1" width="150px"><s:text
	                                                            name="config.ssid.keyType" /></td>
	                                                        <td><s:select id="keyType5" name="keyType5"
	                                                            list="#{'0':'ASCII Key', '1':'Hex Key'}" value="keyType5"
	                                                            onchange="show_keyType5(this.options[this.selectedIndex].value);"
	                                                            cssStyle="width:280px;" /></td>
	                                                    </tr>
	                                                </table>
	                                            </div>
                                                             
                                                <div style="display:<s:property value="hideFifth_one"/>"
                                                    id="hideFifth_one">
                                                    <table border="0" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td class="labelT1" width="150px"><s:text
                                                                name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstKeyValue2" name="firstKeyValue2"
                                                                size="50" maxlength="5" showPassword="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/>
                                                                <s:textfield id="firstKeyValue2_text" name="firstKeyValue2"
                                                                size="50" maxlength="5" cssStyle="display:none" disabled="true"
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
                                                            <td>&nbsp;<s:text name="config.ssid.keyValue_range2" /></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstConfirmValue2" showPassword="true"
                                                                name="firstConfirmValue2" value="%{firstKeyValue2}" size="50"
                                                                maxlength="5" 
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/>
                                                                <s:textfield id="firstConfirmValue2_text" cssStyle="display:none" disabled="true"
                                                                name="firstConfirmValue2" value="%{firstKeyValue2}" size="50"
                                                                maxlength="5" 
                                                                onkeypress="return hm.util.keyPressPermit(event,'password');"/></td>
                                                            <td>
                                                                <table border="0" cellspacing="0" cellpadding="0">
                                                                    <tr>
                                                                        <td>
                                                                            <s:checkbox id="chkToggleDisplay2_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
                                                                                onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue2','firstConfirmValue2'],['firstKeyValue2_text','firstConfirmValue2_text']);" />
                                                                        </td>
                                                                        <td>
                                                                            <s:text name="admin.user.obscurePassword" />
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                 </div>   
                                                <div style="display:<s:property value="hideFifth_two"/>"  id="hideFifth_two">
                                                    <table border="0" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td class="labelT1" width="150px"><s:text
                                                                name="config.ssid.keyValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstKeyValue2_1" showPassword="true"
                                                                name="firstKeyValue2_1" size="50" maxlength="10"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" />
                                                                <s:textfield id="firstKeyValue2_1_text" cssStyle="display:none" disabled="true"
                                                                name="firstKeyValue2_1" size="50" maxlength="10"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
                                                            <td>&nbsp;<s:text name="config.ssid.keyValue_range2_1" /></td>
                                                        </tr>
                                                        <tr>
                                                            <td class="labelT1"><s:text name="config.ssid.confirmValue" /><font color="red"><s:text name="*"/></font></td>
                                                            <td><s:password id="firstConfirmValue2_1" showPassword="true"
                                                                name="firstConfirmValue2_1" value="%{firstKeyValue2_1}"
                                                                size="50" maxlength="10"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" />
                                                                <s:textfield id="firstConfirmValue2_1_text" cssStyle="display:none" disabled="true"
                                                                name="firstConfirmValue2_1" value="%{firstKeyValue2_1}"
                                                                size="50" maxlength="10"
                                                                onkeypress="return hm.util.keyPressPermit(event,'hex');" /></td>
                                                            <td>
                                                                <table border="0" cellspacing="0" cellpadding="0">
                                                                    <tr>
                                                                        <td>
                                                                            <s:checkbox id="chkToggleDisplay2_1_1" name="ignore" value="true" disabled="%{writeDisable4Struts}"
                                                                                onclick="hm.util.toggleObscurePassword(this.checked,['firstKeyValue2_1','firstConfirmValue2_1'],['firstKeyValue2_1_text','firstConfirmValue2_1_text']);" />
                                                                        </td>
                                                                        <td>
                                                                            <s:text name="admin.user.obscurePassword" />
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                     </table>
                                                 </div>                                                                                                                                       
	                                        </td>
	                                    </tr>	                                                                    
                                    </table>
                                </fieldset>
                            </td>
                        </tr>                     
                    </table>
                </td>                
            </tr>    
        </table>        
    </s:form>
</div>
