/*
main function: sortTable(theadTableId, tableId, tbodyId, startRow, colIndex, colDataType, sTd)
theadTableId: table thead id; tableId: the sort table id; tbodyId: the table tbody id;
startRow: the start row index; colIndex: sort column index;
colDataType: sort column data type; sTd: sort column self.
external js, call jquery.min.js
external style, call tableSort.css
*/

//count: sorted style counter
var count=0;
//convert tool, will convert field type into table column type which can sort, like：String,int,float 
function convert(sValue, colDataType) {
    switch (colDataType) {
        case "int":
            return parseInt(sValue);
        case "float":
            return parseFloat(sValue);
        default:
            return sValue.toString();
    }
}

//sort function
function sortTable(theadTableId, tableId, tbodyId, startRow, colIndex, colDataType, sTd) {
	var oTheadTable = document.getElementById(theadTableId);
    var oTable = document.getElementById(tableId);
	var oTableBody = document.getElementById(tbodyId);
	var tableRows = oTableBody.rows;
    var tableDataRows = new Array;
    var tableDataHiddenRows = new Array;
    var tableThs = oTheadTable.tBodies[0].rows;
    var ths = tableThs[0].getElementsByTagName("th");
    for(var i = 1; i < ths.length; i++){
    	$(ths[i]).attr("onclick","javascript:void(0);");
    }
    //push all columns into array
    for (var i = startRow; i < tableRows.length; i++) {
    	if(tableRows[i].style.display == ""){
    		tableDataRows.push(tableRows[i]);
    	}else{
    		tableDataHiddenRows.push(tableRows[i]);
    	}
    }
    //shell sort array
    if (oTable.sortCol == colIndex) {
    	var rTemp;
    	if(count % 2 == 0){
    		var j, i, v, h=1, s=3, k,n = tableDataRows.length;
    	    while(h < n){
    	    	h=s*h+1;
    	    }
    		while(h > 1) {
    			h=(h-1)/s;
    	      	for (k=0; k<h; k++){
    				for (i=k+h,j=i; i<n; i+=h, j=i) {
    	          		v = tableDataRows[i];
    	          		var jValue,vValue;
    	          		
    					while(true){
    						if ((j-=h) >= 0){
    							if(colDataType == 'float'){
    								jValue = convert($(tableDataRows[j].cells[colIndex]).attr("realValue"), colDataType);
    	    		          		vValue = convert($(v.cells[colIndex]).attr("realValue"), colDataType);
    								if (jValue < vValue) {
    									tableDataRows[j+h]=tableDataRows[j];
    				                }else{
    				                	break;
    				                }
    							}else if(colIndex != 0){
    								jValue = convert($(tableDataRows[j].cells[colIndex]).text().trim().toUpperCase(), colDataType);
    	    		          		vValue = convert($(v.cells[colIndex]).text().trim().toUpperCase(), colDataType);
    								if (jValue < vValue) {
    									tableDataRows[j+h]=tableDataRows[j];
    				                }else{
    				                	break;
    				                }
    							}
    						}else{
    							break;
    						}
    					}
    					tableDataRows[j+h]=v;
    	        	}
    	      	}
    		}
    	}else{
    		var j, i, v, h=1, s=3, k,n = tableDataRows.length;
    	    while(h < n){
    	    	h=s*h+1;
    	    }
    		while(h > 1) {
    			h=(h-1)/s;
    	      	for (k=0; k<h; k++){
    				for (i=k+h,j=i; i<n; i+=h, j=i) {
    	          		v = tableDataRows[i];
    	          		var jValue,vValue;
    	          		
    					while(true){
    						if ((j-=h) >= 0){
    							if(colDataType == 'float'){
    								jValue = convert($(tableDataRows[j].cells[colIndex]).attr("realValue"), colDataType);
    	    		          		vValue = convert($(v.cells[colIndex]).attr("realValue"), colDataType);
    								if (jValue > vValue) {
    									tableDataRows[j+h]=tableDataRows[j];
    				                }else{
    				                	break;
    				                }
    							}else if(colIndex != 0){
    								jValue = convert($(tableDataRows[j].cells[colIndex]).text().trim().toUpperCase(), colDataType);
    	    		          		vValue = convert($(v.cells[colIndex]).text().trim().toUpperCase(), colDataType);
    								if (jValue > vValue) {
    									tableDataRows[j+h]=tableDataRows[j];
    				                }else{
    				                	break;
    				                }
    							}
    						}else{
    							break;
    						}
    					}
    					tableDataRows[j+h]=v;
    	        	}
    	      	}
    		}
    	}
	    
        count++;
    } else {
    	var j, i, v, h=1, s=3, k,n = tableDataRows.length;
	    while(h < n){
	    	h=s*h+1;
	    }
		while(h > 1) {
			h=(h-1)/s;
	      	for (k=0; k<h; k++){
				for (i=k+h,j=i; i<n; i+=h, j=i) {
	          		v = tableDataRows[i];
	          		var jValue,vValue;
	          		
					while(true){
						if ((j-=h) >= 0){
							if(colDataType == 'float'){
								jValue = convert($(tableDataRows[j].cells[colIndex]).attr("realValue"), colDataType);
				          		vValue = convert($(v.cells[colIndex]).attr("realValue"), colDataType);
								if (jValue > vValue) {
									tableDataRows[j+h]=tableDataRows[j];
				                }else{
				                	break;
				                }
							}else if(colIndex != 0){
								jValue = convert($(tableDataRows[j].cells[colIndex]).text().trim().toUpperCase(), colDataType);
				          		vValue = convert($(v.cells[colIndex]).text().trim().toUpperCase(), colDataType);
								if (jValue > vValue) {
									tableDataRows[j+h]=tableDataRows[j];
				                }else{
				                	break;
				                }
							}
						}else{
							break;
						}
					}
					tableDataRows[j+h]=v;
	        	}
	      	}
		}
        count=0;
    }
    //create new table
    oTable.removeChild(oTableBody);
	var otbody = document.createElement("tbody");
	otbody.id = tbodyId;
	for (var i = 0; i < tableDataRows.length; i++) {
    	otbody.appendChild(tableDataRows[i]);
    }
	if(tableDataHiddenRows.length > 0){
		for (var i = 0; i < tableDataHiddenRows.length; i++) {
	    	otbody.appendChild(tableDataHiddenRows[i]);
	    }
	}
    oTable.appendChild(otbody);
    //show column sorted image
    if(oTable.sortCol != "undefined"){
    	 $(oTheadTable.tBodies[0].rows[0].cells[oTable.sortCol]).removeClass("Asc");
    	 $(oTheadTable.tBodies[0].rows[0].cells[oTable.sortCol]).removeClass("Desc");
         $(oTheadTable.tBodies[0].rows[0].cells[oTable.sortCol]).addClass("SortNone");
    }
    if (count % 2 == 0) {
        if (count != 0) {
            $(oTheadTable.tBodies[0].rows[0].cells[sTd.cellIndex]).removeClass("Desc");
        }
        $(oTheadTable.tBodies[0].rows[0].cells[sTd.cellIndex]).removeClass("SortNone");
        $(oTheadTable.tBodies[0].rows[0].cells[sTd.cellIndex]).addClass("Asc");
    }
    else {
    	$(oTheadTable.tBodies[0].rows[0].cells[sTd.cellIndex]).removeClass("SortNone");
        $(oTheadTable.tBodies[0].rows[0].cells[sTd.cellIndex]).removeClass("Asc");
        $(oTheadTable.tBodies[0].rows[0].cells[sTd.cellIndex]).addClass("Desc");
    }

    //record the last sorted column index
    oTable.sortCol = colIndex;
    //record the sort finished
    for(var i = 1; i < ths.length; i++){
    	if(colDataType == 'float'){
    		$(ths[i]).attr("onclick","sortTable('"+theadTableId+"', '"+tableId+"', '"+tbodyId+"', "+startRow+", "+i+", 'float', this)");
    	}else{
    		$(ths[i]).attr("onclick","sortTable('"+theadTableId+"', '"+tableId+"', '"+tbodyId+"', "+startRow+", "+i+", '', this)");
    	}
    }
} 
