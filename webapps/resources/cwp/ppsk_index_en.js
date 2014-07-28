function fillData(){
var txt =[["Secure Internet Portal"],
["<strong>New users should fill out the form below to request a private preshared Key for accessing the secure wireless network.</strong>"],
["First Name*"],
["Last Name*"],
["Email*"],
["Phone"],
["Visiting*"],
["Comment"],
["It may take a moment for registration to complete (* required)."],
["Login"],
["Register"],
];  

document.getElementById("h1Title").innerHTML=txt[0];
document.getElementById("h2Title").innerHTML=txt[1];
fillPlaceHolder("field1",txt[2]);
fillPlaceHolder("field2",txt[3]);
fillPlaceHolder("field3",txt[4]);
fillPlaceHolder("opt_field1",txt[5]);
fillPlaceHolder("field4",txt[6]);
fillPlaceHolder("opt_field2",txt[7]);
document.getElementById("momentNote").innerHTML=txt[8];
document.title=txt[9];
document.getElementById("register_button_id").innerHTML=txt[10];
}

function fillPlaceHolder(fieldID,changeText){
	$("#"+fieldID).attr('placeholder',changeText);
}