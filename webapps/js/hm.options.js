var hm = hm || {};
hm.options = hm.options || {};

hm.options.moveSelectedOptions = function(objSourceElement, objTargetElement, toSort, notMove1, notMove2, limitCount) {
    var test1 = hm.options.compile(notMove1);
    var test2 = hm.options.compile(notMove2);
    if(objSourceElement.length == 0 || objSourceElement.options[0].value == -1) {
    	warnDialog.cfg.setProperty('text', "No items found.");
		warnDialog.show();
    } else {
    	var j = 0;
    	for (var i = 0; i < objSourceElement.length; i++) {
	    	if(objSourceElement.options[i].selected) {
	    		j ++;
	    	}		
	    }
	    if(j == 0) {
	    	warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
	    } else {
	    	if(limitCount > 0 && (j + objTargetElement.length) > limitCount) {
		    	warnDialog.cfg.setProperty('text', "The selected items will overrun the limit "+limitCount+".");
				warnDialog.show();
		    } else {
			    hm.options.moveOptions(objSourceElement, objTargetElement, toSort, 
			        function(opt) {
			            return (opt.selected && !test1(opt.value) && !test2(opt.value) && opt.value != -1);
			        }
			    );
			 }
	    }
    }   
}

hm.options.moveAllOptions = function(objSourceElement, objTargetElement, toSort, notMove1, notMove2, limitCount) {
    var test1 = hm.options.compile(notMove1);
    var test2 = hm.options.compile(notMove2);
    if(objSourceElement.length == 0 || objSourceElement.options[0].value == -1) {
    	warnDialog.cfg.setProperty('text', "No items found.");
		warnDialog.show();
    } else {
    	if(limitCount > 0 && (objSourceElement.length + objTargetElement.length) > limitCount) {
	    	warnDialog.cfg.setProperty('text', "The selected items will overrun the limit "+limitCount+".");
			warnDialog.show();
	    } else {
		    hm.options.moveOptions(objSourceElement, objTargetElement, toSort, 
		        function(opt) {
		            return (!test1(opt.value) && !test2(opt.value) && opt.value != -1);
		        }
		    );
		}
    }  
}

hm.options.compile = function(ptn) {
    if (ptn != undefined) {
    	if (ptn == '' || !window.RegExp) {
            return function(val) { return val == ptn; }
        } else {
            var reg = new RegExp(ptn);
            return function (val) { 
                if (val == '') { // ignore empty option added by template 
                	return true;
                }
            	return reg.test(val);
            }
        }
    }
    return function(val) { return false; }
}    

hm.options.moveOptions = function(objSourceElement, objTargetElement, toSort, chooseFunc) {
    var aryTempSourceOptions = new Array();
    var aryTempTargetOptions = new Array();
    var x = 0;

    //looping through source element to find selected options
    for (var i = 0; i < objSourceElement.length; i++) {
        if (chooseFunc(objSourceElement.options[i])) {
            //need to move this option to target element
            if (objTargetElement.length == 1 && objTargetElement.options[0].value < 0) {
            	objTargetElement.length = 0;
            }
            var intTargetLen = objTargetElement.length++;
            objTargetElement.options[intTargetLen].text =   objSourceElement.options[i].text;
            objTargetElement.options[intTargetLen].value =  objSourceElement.options[i].value;
        }
        else {
            //storing options that stay to recreate select element
            var objTempValues = new Object();
            objTempValues.text = objSourceElement.options[i].text;
            objTempValues.value = objSourceElement.options[i].value;
            aryTempSourceOptions[x] = objTempValues;
            x++;
        }
    }

    //sorting and refilling target list
    for (var i = 0; i < objTargetElement.length; i++) {
        var objTempValues = new Object();
        objTempValues.text = objTargetElement.options[i].text;
        objTempValues.value = objTargetElement.options[i].value;
        aryTempTargetOptions[i] = objTempValues;
    }
    
    if (toSort) {
        aryTempTargetOptions.sort(hm.options.sortByText);
    }    
    
    for (var i = 0; i < objTargetElement.length; i++) {
	        objTargetElement.options[i].text = aryTempTargetOptions[i].text;
	        objTargetElement.options[i].value = aryTempTargetOptions[i].value;
	        objTargetElement.options[i].selected = false;
	    }   
    
    //resetting length of source
    objSourceElement.length = aryTempSourceOptions.length;
    //looping through temp array to recreate source select element
    for (var i = 0; i < aryTempSourceOptions.length; i++) {
        objSourceElement.options[i].text = aryTempSourceOptions[i].text;
        objSourceElement.options[i].value = aryTempSourceOptions[i].value;
        objSourceElement.options[i].selected = false;
    }
}

hm.options.sortByText = function(a, b) {
    if (a.text < b.text) {return -1}
    if (a.text > b.text) {return 1}
    return 0;
}

hm.options.selectAllOptionsExceptSome = function(objTargetElement, type, ptn) {
    var test = hm.options.compile(ptn);
    for (var i = 0; i < objTargetElement.length; i++) {
        var opt = objTargetElement.options[i];
        if ((type == 'key' && !test(opt.value)) ||
              (type == 'text' && !test(opt.text))) {
            opt.selected = true;
        } else {
            opt.selected = false;
        }    
    }
    return false;
}

hm.options.selectAllOptions = function(objTargetElementId) {
	var objTargetElement = document.getElementById(objTargetElementId);
	if(!objTargetElement){
		return false;
	}
    for (var i = 0; i < objTargetElement.length; i++) {
        if (objTargetElement.options[i].value != '') {
            objTargetElement.options[i].selected = true;    
        }    
    }
    return false;
}

hm.options.moveOptionUp = function(objTargetElement, type, ptn) {
	
	if(objTargetElement.length == 0 || objTargetElement.options[0].value == -1) {
    	warnDialog.cfg.setProperty('text', "No items found.");
		warnDialog.show();
		return;
    }
    
	var test = hm.options.compile(ptn);
	var selectCount = 0;
	for (i=0; i<objTargetElement.length; i++) {
		if (objTargetElement[i].selected) {
			var v;
			if (i != 0 && !objTargetElement[i-1].selected) {
		    	if (type == 'key') {
		    		v = objTargetElement[i-1].value
		    	}
		    	else {
		    		v = objTargetElement[i-1].text;
		    	}
				if (!test(v)) {
					hm.options.swapOptions(objTargetElement,i,i-1);
				}
		    }
		    selectCount++;
		}
	}
	
	if(selectCount == 0) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	}
}

hm.options.moveOptionDown = function(objTargetElement, type, ptn) {
	
	if(objTargetElement.length == 0 || objTargetElement.options[0].value == -1) {
    	warnDialog.cfg.setProperty('text', "No items found.");
		warnDialog.show();
		return;
    }
    
	var test = hm.options.compile(ptn);
	var selectCount = 0;
	
	for (i=(objTargetElement.length-1); i>= 0; i--) {
		if (objTargetElement[i].selected) {
			var v;
			if ((i != (objTargetElement.length-1)) && !objTargetElement[i+1].selected) {
		    	if (type == 'key') {
		    		v = objTargetElement[i].value
		    	}
		    	else {
		    		v = objTargetElement[i].text;
		    	}
				if (!test(v)) {
					hm.options.swapOptions(objTargetElement,i,i+1);
				}
		    }
			
				selectCount++;
		}
	}
	
	if(selectCount == 0) {
		warnDialog.cfg.setProperty('text', "Please select at least one item.");
		warnDialog.show();
	}
}

hm.options.swapOptions = function(objTargetElement, first, second) {
	var opt = objTargetElement.options;
	var temp = new Option(opt[first].text, opt[first].value, opt[first].defaultSelected, opt[first].selected);
	var temp2= new Option(opt[second].text, opt[second].value, opt[second].defaultSelected, opt[second].selected);
	opt[first] = temp2;
	opt[second] = temp;
}

hm.options.moveSelectedItems = function(objSourceElement, objTargetElement, selectObjValue, toSort, notMove1, notMove2, limitCount) {
    var test1 = hm.options.compile(notMove1);
    var test2 = hm.options.compile(notMove2);
    if(selectObjValue.length == 0) {
		return;
    } else {
    	var j = 0;
    	for(var k=0; k<selectObjValue.length; k++) {
	    	for (var i = 0; i < objSourceElement.length; i++) {
		    	if(objSourceElement.options[i].value==selectObjValue[k]) {
		    		objSourceElement.options[i].selected=true;
		    		j ++;
		    	}		
		    }
    	}
	    if(j == 0) {
	    	warnDialog.cfg.setProperty('text', "Please select at least one item.");
			warnDialog.show();
	    } else {
	    	if(limitCount > 0 && (j + objTargetElement.length) > limitCount) {
		    	warnDialog.cfg.setProperty('text', "The selected items will overrun the limit "+limitCount+".");
				warnDialog.show();
		    } else {
			    hm.options.moveOptions(objSourceElement, objTargetElement, toSort, 
			        function(opt) {
			            return (opt.selected && !test1(opt.value) && !test2(opt.value) && opt.value != -1);
			        }
			    );
			 }
	    }
    }   
}
