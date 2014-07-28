REPORT_LOCALIZATION =
[
  {
    "-1":
    {
      prompt: "select axis...",	//  latest
      display: "",
      defaultChart: ""
    }//,
  },
  {
    "audited operation":
    {
      prompt: "Audit Logs",	//  latest
      display: "Operation",
      defaultChart: "table",
      defaultYaxis:"time",
      specifyType:1,
      yaxis:  {
      	"time": 
  			{
  				prompt: "Operation Time",
  				display: "Operation Time",
  				breakdown: false,
  				drilldown:0,
  				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
  				dataType: "time"
  			},
  		"adminitrator": 
  			{
  				prompt: "Operator Name",
  				display: "Admin Name",
  				breakdown: false,
  				drilldown:0,
  				validate: "by"
  			},
  		"IP": 
  			{
  				prompt: "Operator IP",
  				display: "Admin IP",
  				breakdown: false,
  				drilldown:0,
  				validate: "from"
  			},
  		"result": 
  			{
  				prompt: "Operation Result",
  				display: "Result",
  				breakdown: false,
  				drilldown:0,
  				validate: "from"
  			}
      }
    }
  },
  {
    "login administrator":
    {
      prompt: "Admin Login Information",	//  latest
      display: "Admin Name",
      defaultChart: "table",
      defaultYaxis:"admin name",
      specifyType:1,
      homeonly:true,
      yaxis:  {
  		"time": 
  			{
  				prompt: "Time",
  				display: "Time",
  				breakdown: false,
  				drilldown:0,
  				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
  				dataType: "time"
  			},
  		"IP": 
  			{
  				prompt: "Admin IP",
  				display: "Admin IP",
  				breakdown: false,
  				drilldown:0,
  				validate: "from"
  			},
  		"seconds": 
  			{
  				prompt: "Session Time",
				display: "Session Time",
				breakdown: false,
				unit:"SECOND",
				drilldown:0,
				validate: 1000
  			}
      }
    }
  },
  {
    "bandwidth consuming OS/type":
    {
      prompt: "Data Usage Distribution by Client Device Type",	//  top
      display: "Client Type",
      defaultChart: "pie",
      defaultYaxis:"bytes",
      specifyType:1,
      yaxis:{
		  "total distinct client devices": 
			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				breakdown: true,
				drilldown:5,
				valueInt: true,
				validate: 1000
			},
		  "total distinct client users": 
			{
				prompt: "Total Number of Client Users",
				display: "Users",
				breakdown: true,
				drilldown:5,
				valueInt: true,
				validate: 1000
			},		
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"percent usage": 
  			{
  				prompt: "percent usage",
  				display: "percent usage",
  				breakdown: true,
  				drilldown:1,
  				unit:"%",
  				validate: 1
  			},  			
  		"inbound 2.4 GHz bytes": 
  			{
  				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			}
      	}
    }
  },
  {
    "bandwidth consuming vendor":
    {
      prompt: "Data Usage Distribution by Client Device Vendor",	//  top
      display: "Client Vendor",
      defaultChart: "pie",
      defaultYaxis:"bytes",
      specifyType:1,
      yaxis:{
      	"total distinct client devices": 
  			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
  				breakdown: true,
  				drilldown:5,
  				valueInt: true,
  				validate: 1000
  			},
  		"bytes":
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 2.4 GHz bytes": 
  			{
				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			}
      }
    }
  },
  {
    "watchlist bandwidth application":
    {
      prompt: "watchlist Applications by Data Usage",	//  top
      display: "Application",
      defaultChart: "table",
      defaultYaxis:"bytes",
      specifyType:1,
      monitorType: 'app',
      yaxis:{
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			}
      }
    }
  },
  {
    "bandwidth application":
    {
      prompt: "Top Applications by Data Usage",	//  top
      display: "Application",
      defaultChart: "table",
      defaultYaxis:"bytes",
      specifyType:1,
      monitorType: 'app',
      yaxis:{
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 2.4 GHz bytes": 
  			{
				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound wired bytes":
  			{
  				prompt: "Inbound Wired Byte Usage",
  				display: "Inbound Wired",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound wired bytes":
  			{
  				prompt: "Outbound Wired Byte Usage",
  				display: "Outbound Wired",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  	  	"top user":
  			{
  				prompt: "Top UserName by Byte Usage",
  				display: "Top User",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth UserName"
  			},
  		"top SSId": 
  			{
  				prompt: "Top SSID by Byte Usage",
  				display: "Top SSID",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth SSId"
  			},
  		"top network device": 
  			{
  				prompt: "Top Aerohive Device by Byte Usage",
  				display: "Top Aerohive Device",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth network device"
  			},
  		"top client device": 
  			{
  				prompt: "MAC Address of Top Client Device by Byte Usage",
  				display: "Top Client MAC",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client MAC address"
  			},
  		"top client host": 
  			{
  				prompt: "Host Name of Top Client Device by Byte Usage",
  				display: "Top Client Name",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client host name"
  			},
  		"client devices": 
  			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"network devices": 
  			{
  				prompt: "Total Number of Aerohive Devices",
  				display: "Aerohive Devices",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"last time": 
  			{
  				prompt: "The Last Time the Application Was Seen",
  				display: "Last Time",
  				breakdown: false,
  				drilldown:0,
  				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
  				dataType: "time"
  			},
  		"users": 
  			{
  				prompt: "Total Number of Users",
  				display: "Users",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"group": 
  			{
  				prompt: "Application Group",
  				display: "App Group",
  				breakdown: false,
  				drilldown:0,
  				validate: "App Group Name"
  			},
  		"percent usage": 
  			{
  				prompt: "Percent Usage",
  				display: "Percent Usage",
  				breakdown: false,
  				unit:"%",
  				drilldown:0,
  				validate: 1
  			}
      	}
    }
  },
  {
    "watchlist bandwidth application by usage":
    {
      prompt: "Top Applications by Data Usage",	//  top
      display: "Application",
      defaultChart: "table",
      defaultYaxis:"bytes",
      specifyType:1,
      monitorType: 'app',
      yaxis:{
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"users": 
  			{
  				prompt: "Total Number of Users",
  				display: "Users",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"percent usage": 
  			{
  				prompt: "Percent Usage",
  				display: "Percent Usage",
  				breakdown: false,
  				unit:"%",
  				drilldown:0,
  				validate: 1
  			}
      	}
    }
  },
  {
    "bandwidth application by usage":
    {
      prompt: "Top Applications by Data Usage",	//  top
      display: "Application",
      defaultChart: "table",
      defaultYaxis:"bytes",
      specifyType:1,
      monitorType: 'app',
      yaxis:{
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 2.4 GHz bytes": 
  			{
				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound wired bytes":
  			{
  				prompt: "Inbound Wired Byte Usage",
  				display: "Inbound Wired",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound wired bytes":
  			{
  				prompt: "Outbound Wired Byte Usage",
  				display: "Outbound Wired",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  	  	"top user":
  			{
  				prompt: "Top UserName by Byte Usage",
  				display: "Top User",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth UserName"
  			},
  		"top SSId": 
  			{
  				prompt: "Top SSID by Byte Usage",
  				display: "Top SSID",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth SSId"
  			},
  		"top network device": 
  			{
  				prompt: "Top Aerohive Device by Byte Usage",
  				display: "Top Aerohive Device",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth network device"
  			},
  		"top client device": 
  			{
  				prompt: "MAC Address of Top Client Device by Byte Usage",
  				display: "Top Client MAC",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client MAC address"
  			},
  		"top client host": 
  			{
  				prompt: "Host Name of Top Client Device by Byte Usage",
  				display: "Top Client Name",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client host name"
  			},
  		"client devices": 
  			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"network devices": 
  			{
  				prompt: "Total Number of Aerohive Devices",
  				display: "Aerohive Devices",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"last time": 
  			{
  				prompt: "The Last Time the Application Was Seen",
  				display: "Last Time",
  				breakdown: false,
  				drilldown:0,
  				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
  				dataType: "time"
  			},
  		"users": 
  			{
  				prompt: "Total Number of Users",
  				display: "Users",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"group": 
  			{
  				prompt: "Application Group",
  				display: "App Group",
  				breakdown: false,
  				drilldown:0,
  				validate: "App Group Name"
  			},
  		"percent usage": 
  			{
  				prompt: "Percent Usage",
  				display: "Percent Usage",
  				breakdown: false,
  				unit:"%",
  				drilldown:0,
  				validate: 1
  			}
      	}
    }
  },
  {
    "watchlist bandwidth application by clients":
    {
      prompt: "Top Applications by Data Usage",	//  top
      display: "Application",
      defaultChart: "table",
      defaultYaxis:"bytes",
      specifyType:1,
      monitorType: 'app',
      yaxis:{
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"client devices": 
  			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			}
      	}
    }
  },
  {
    "bandwidth application by clients":
    {
      prompt: "Top Applications by Data Usage",	//  top
      display: "Application",
      defaultChart: "table",
      defaultYaxis:"bytes",
      specifyType:1,
      monitorType: 'app',
      yaxis:{
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 2.4 GHz bytes": 
  			{
				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound wired bytes":
  			{
  				prompt: "Inbound Wired Byte Usage",
  				display: "Inbound Wired",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound wired bytes":
  			{
  				prompt: "Outbound Wired Byte Usage",
  				display: "Outbound Wired",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  	  	"top user":
  			{
  				prompt: "Top UserName by Byte Usage",
  				display: "Top User",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth UserName"
  			},
  		"top SSId": 
  			{
  				prompt: "Top SSID by Byte Usage",
  				display: "Top SSID",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth SSId"
  			},
  		"top network device": 
  			{
  				prompt: "Top Aerohive Device by Byte Usage",
  				display: "Top Aerohive Device",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth network device"
  			},
  		"top client device": 
  			{
  				prompt: "MAC Address of Top Client Device by Byte Usage",
  				display: "Top Client MAC",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client MAC address"
  			},
  		"top client host": 
  			{
  				prompt: "Host Name of Top Client Device by Byte Usage",
  				display: "Top Client Name",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client host name"
  			},
  		"client devices": 
  			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"network devices": 
  			{
  				prompt: "Total Number of Aerohive Devices",
  				display: "Aerohive Devices",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"last time": 
  			{
  				prompt: "The Last Time the Application Was Seen",
  				display: "Last Time",
  				breakdown: false,
  				drilldown:0,
  				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
  				dataType: "time"
  			},
  		"users": 
  			{
  				prompt: "Total Number of Users",
  				display: "Users",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"group": 
  			{
  				prompt: "Application Group",
  				display: "App Group",
  				breakdown: false,
  				drilldown:0,
  				validate: "App Group Name"
  			},
  		"percent usage": 
  			{
  				prompt: "Percent Usage",
  				display: "Percent Usage",
  				breakdown: false,
  				unit:"%",
  				drilldown:0,
  				validate: 1
  			}
      	}
    }
  },
  {
    "bandwidth user":
    {
      prompt: "Top Users by Data Usage",	//  top
      display: "User",
      defaultChart: "table",
      defaultYaxis: "bytes",
      specifyType:1,
      monitorType: 'user',
      yaxis:{
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 2.4 GHz bytes": 
  			{
			prompt: "Inbound 2.4 GHz Byte Usage",
				display: "Inbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		"inbound 5 GHz bytes": 
			{
				prompt: "Inbound 5 GHz Byte Usage",
				display: "Inbound 5 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		"outbound 2.4 GHz bytes": 
			{
				prompt: "Outbound 2.4 GHz Byte Usage",
				display: "Outbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		"outbound 5 GHz bytes": 
			{
				prompt: "Outbound 5 GHz Byte Usage",
				display: "Outbound 5 GHz",
  				breakdown: true,
  				drilldown:1,
  				unit:"B",
  				validate: 1000
  			},
  		"top application":
  			{
  				prompt: "Top Application by Data Usage",
				display: "Top App",
				breakdown: true,
				drilldown:1,
				validate: 1
  			},
  		"last client MAC":
  			{
	  			prompt: "The Last Client Device Used",
				display: "Last Client",
				breakdown: true,
				drilldown:1,
				validate: "latest client device MAC used"
  			}
      }
    }
  },
  {
    "bandwidth consumer":
    {
      prompt: "Top Client Devices by Data Usage",	//  top
      display: "name/MAC",
      defaultChart: "horizontal",
      defaultYaxis: "bytes",
      specifyType:1,
      monitorType: 'client',
      yaxis:{
    	  "MAC":{
    		  prompt: "Client Device MAC Address",
			  display: "MAC",
			  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "Client device Media Access Control address"
    	  },
    	  "OS/type":{
    		  prompt: "Client Device Type",
			  display: "Client Type",
			  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "Client device OS/type"
    	  },
    	  "vendor":{
    		  prompt: "Client Device Vendor",
			  display: "vendor",
			  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "Client device vendor"
    	  },
    	  "UserNames":{
    		  prompt: "UserName(s)",
			  display: "User(s)",
			  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "User names delimited by /"
    	  },
    	  "bytes": 
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "inbound 2.4 GHz bytes": 
			{
				prompt: "Inbound 2.4 GHz Byte Usage",
				display: "Inbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		"inbound 5 GHz bytes": 
			{
				prompt: "Inbound 5 GHz Byte Usage",
				display: "Inbound 5 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		"outbound 2.4 GHz bytes": 
			{
				prompt: "Outbound 2.4 GHz Byte Usage",
				display: "Outbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		"outbound 5 GHz bytes": 
			{
				prompt: "Outbound 5 GHz Byte Usage",
				display: "Outbound 5 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			}
      }
    }//,
  },
  {
    "bandwidth consuming device":
    {
      prompt: "bandwidth consuming device",	//  top
      display: "device name",
      defaultChart: "horizontal",
      defaultYaxis: "bytes",
      yaxis:{
    	  "bytes": 
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
			"inbound 2.4 GHz bytes": 
			{
				prompt: "Inbound 2.4 GHz Byte Usage",
				display: "Inbound 2.4 GHz",
				unit:"B",
				validate: 1000
			},
			"inbound 5 GHz bytes": 
			{
				prompt: "Inbound 5 GHz Byte Usage",
				display: "Inbound 5 GHz",
				unit:"B",
				validate: 1000
			},
			"outbound 2.4 GHz bytes": 
			{
				prompt: "Outbound 2.4 GHz Byte Usage",
				display: "Outbound 2.4 GHz",
				unit:"B",
				validate: 1000
			},
			"outbound 5 GHz bytes": 
			{
				prompt: "Outbound 5 GHz Byte Usage",
				display: "Outbound 5 GHz",
				unit:"B",
				validate: 1000
			},
			"inbound wired bytes": 
			{
				prompt: "inbound wired Byte Usage",
				display: "inbound wired",
				unit:"B",
				validate: 1000
			},
			"outbound wired bytes": 
			{
				prompt: "Outbound wired Byte Usage",
				display: "Outbound wired",
				unit:"B",
				validate: 1000
			},
			"total distinct 2.4 GHz client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
			},
			"total distinct 5 GHz client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
			},
			"total distinct wireless client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
			},
			"total distinct wired client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
			},
			"total distinct sum client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
			}
      	}
    }//,
  },
  {
    "client w/ the most airtime usage":
    {
      prompt: "Top Client Devices by Airtime Usag",	//  top
      display: "Client Device",
      defaultChart: "table",
      defaultYaxis: "total airtime %",
      specifyType:1,
      monitorType: 'client',
      yaxis:{
    	  "total airtime %":{
    		  prompt: "Airtime Usage %",
			  display: "Airtime Usage %",
			  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "transmitting airtime %":{
    		  prompt: "Tx Airtime Usage %",
			  display: "Tx Airtime Usage %",
			  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "receive airtime %":{
    		  prompt: "Rx Airtime Usage %",
			  display: "Rx Airtime Usage %",
			  breakdown: true,
			  drilldown: 0,
			  validate: 1
    	  }
      }
    }//,
  },
  {
    "client w/ association failures":
    {
      prompt: "Top Client Devices by Association Failures",	//  top
      display: "Client Device",
      defaultChart: "table",
      defaultYaxis: "association failures",
      specifyType:1,
      monitorType: 'client',
      yaxis:{
    	  "MAC":{
    		  prompt: "MAC Address",
			  display: "MAC",
			  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "Client device Media Access Control address"
    	  },
    	  "association failures":{
    		  prompt: "Association Failures",
			  display: "Association Failures",
			  breakdown: true,
			  drilldown: 1,
			  valueInt: true,
			  validate: 1000
    	  }
      }
    }//,
  },
  {
    "client status":
    {
      prompt: "Client Status",	//  top
      display: "Client Status",
      defaultChart: "list",
      defaultYaxis: "association failures",
      specifyType:1,
      monitorType: 'client',
      yaxis:{
    	  "current active client count":{
    		  prompt: "Current Active Client Count",
			  display: "Current Active Client Count",
			  breakdown: false,
			  drilldown:1,
			  valueInt: true,
			  validate: 1000
    	  },
    	  "max concurrent client devices":{
    		  prompt: "Max Concurrent Client Devices",
			  display: "Max Concurrent Client Devices",
			  breakdown: false,
			  drilldown: 1,
			  valueInt: true,
			  validate: 1000
    	  }
      }
    }//,
  },
  {
	"active clients by user profile":
  	{
	    prompt: "Client Devices by User Profile",	//  by
	    display: "User Profile Attribute",
	    defaultChart: "pie",
	    defaultYaxis: "total distinct client devices",
	    specifyType:1,
	    yaxis:{
	  	  "total distinct client devices":{
					prompt: "Total Number of Client Devices",
					display: "Clients",
	  		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
	  		  validate: 1000
	  	  }
	    }
	  }//,
	}, 
  {
    "client device radio mode":
    {
      prompt: "Client Radio Mode Distribution",	//  by
      display: "Radio Mode",
      defaultChart: "pie",
      defaultYaxis: "total distinct client devices",
      specifyType:1,
      yaxis:{
    	  "total distinct client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
			  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  }
      }
    }//,
  },
  {
    "client device OS/type":
    {
      prompt: "Distribution by Client Device Type",	//  by
      display: "Client Type",
      defaultChart: "pie",
      defaultYaxis: "total distinct client devices",
      specifyType:1,
      yaxis:{
    	  "total distinct client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  }
      }
    }//,
  },
  {
    "client device vendor":
    {
      prompt: "Client Vendor Distribution",	//  by
      display: "Client Device Vendor",
      defaultChart: "pie",
      defaultYaxis: "total distinct client devices",
      specifyType:1,
      yaxis:{
    	  "total distinct client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  }
      }
      
    }//,
  },
  {
    "total client devices server(s) (port)":
    {
      prompt: "Top Aerohive Devices (Aggregations/Ports) by Total Clients",	//  top
      display: "Aerohive Device(s) (Port)",
      defaultChart: "horizontal",
      defaultYaxis: "total distinct client devices",
      specifyType:1,
      yaxis:{
    	  "MAC":{
    		  prompt: "MAC Address if Aerohive Device",
    		  display: "MAC",
			  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "MAC"
    	  },
    	  "total distinct client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  }
    	  
      }
    }//,
  },
  {
    "concurrent client devices server(s) (port)":
    {
      prompt: "Top Aerohive Devices by Maximum Concurrent Clients",	//  top
      display: "Aerohive Device(s) (Port)",
      defaultChart: "horizontal",
      defaultYaxis: "maximum concurrent client devices",
      specifyType:1,
      yaxis:{
    	  "MAC":{
    		  prompt: "MAC Address if Aerohive Device",
    		  display:"MAC",
    		  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "MAC"
    	  },
    	  "maximum concurrent client devices":{
    		  prompt:"Maximum Number of Concurrent Client Devices",
    		  display:"Concurrent Clients",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "maximum concurrent client devices 2.4 GHz":{
    		  prompt:"Maximum Number of Concurrent Client Devices 2.4 GHz",
    		  display:"Concurrent Clients 2.4 GHz",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "maximum concurrent client devices 5 GHz":{
    		  prompt:"Maximum Number of Concurrent Client Devices 5 GHz",
    		  display:"Concurrent Clients 5 GHz",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "maximum concurrent client devices wired":{
    		  prompt:"Maximum Number of Concurrent Client Devices wired",
    		  display:"Concurrent Clients wired",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  }
      }
    }//,
  },
  {
    "aggregation concurrent client devices server(s)":
    {
      prompt: "Top Aerohive Devices by Maximum Concurrent Clients",	//  top
      display: "Aerohive Device(s)",
      defaultChart: "pie",
      defaultYaxis: "maximum concurrent client devices",
      specifyType:1,
      yaxis:{
    	  "maximum concurrent client devices":{
    		  prompt:"Maximum Number of Concurrent Client Devices",
    		  display:"Concurrent Clients",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "maximum concurrent client devices wireless":{
    		  prompt:"Maximum Number of Concurrent Client Devices wireless",
    		  display:"Concurrent Clients",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "maximum concurrent client devices 2.4 GHz":{
    		  prompt:"Maximum Number of Concurrent Client Devices 2.4 GHz",
    		  display:"Concurrent Clients 2.4 GHz",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "maximum concurrent client devices 5 GHz":{
    		  prompt:"Maximum Number of Concurrent Client Devices 5 GHz",
    		  display:"Concurrent Clients 5 GHz",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "maximum concurrent client devices wired":{
    		  prompt:"Maximum Number of Concurrent Client Devices Wired",
    		  display:"Concurrent Clients Wired",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  }
      }
    }//,
  },
  {
    "HiveOS version":
    {
      prompt: "HiveOS Version Distribution",	//  by
      display: "HiveOS Version",
      defaultChart: "pie",
      defaultYaxis: "total network devices",
      specifyType:1,
      yaxis:{
    	  "total network devices":{
				prompt: "Total Number of Aerohive Devices",
				display: "Aerohive Devices",
    		  breakdown: true,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  }
      }
    }//,
  },
  {
    "configuration compliance":
    {
      prompt: "Aerohive Device Configuration Compliance",	//  by
      display: "Configuration Compliance",
      defaultChart: "pie",
      defaultYaxis: "total network devices",
      specifyType:1,
      yaxis:{
    	  "total network devices":{
    		  	prompt: "Total Number of Aerohive Devices",
				display: "Aerohive Devices",
				breakdown: true,
				drilldown: 5,
				valueInt: true,
				validate: 1000,
				data_key_convert: function(value) {
					var result = 1;
					if (value === 'Weak') {
						result = 1;
					} else if (value === 'Acceptable') {
						result = 2;
					} else if (value === 'Strong') {
						result = 3;
					}
					return result;
				}
    	  }
      }
    }//,
  },
  {
    "system information":
    {
      prompt: "System Information",
      display: "Software Version",
      defaultChart: "list",
      defaultYaxis: "network devices - up",
      specifyType:1,
      yaxis:{
    	  "network devices - up":{
			  prompt: "Number of Aerohive Devices with Management Up",
			  display: "Number of Aerohive Devices with Management Up",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "network devices - down":{
			  prompt: "Number of Aerohive Devices with Management Down",
			  display: "Number of Aerohive Devices with Management Down",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "network devices - unmanaged":{
			  prompt: "Number of New Aerohive Devices",
			  display: "Number of New Aerohive Devices",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "network devices - alarmed":{
			  prompt: "Number of Aerohive Devices with Alarm Conditions",
			  display: "Number of Aerohive Devices with Alarm Conditions",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "network devices - disconfigured":{
			  prompt: "Number of Aerohive Devices with Outdated Configurations",
			  display: "Number of Aerohive Devices with Outdated Configurations",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "current active client count":{
    		  prompt: "Current Active Client Count",
			  display: "Current Active Client Count",
			  breakdown: false,
			  drilldown:1,
			  valueInt: true,
			  validate: 1000
    	  },
    	  "max concurrent client devices":{
    		  prompt: "Max Concurrent Client Devices",
			  display: "Max Concurrent Client Devices",
			  breakdown: false,
			  drilldown: 1,
			  valueInt: true,
			  validate: 1000
    	  },
    	  "network devices - friendly":{
			  prompt: "Number of Friendly APs",
			  display: "Number of Friendly APs",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "network devices - rogue":{
			  prompt: "Number of Rogue APs",
			  display: "Number of Rogue APs",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "clients - active":{
			  prompt: "Number of Currently Active Clients",
			  display: "Number of Currently Active Clients",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "clients - maximum":{
			  prompt: "Maximum Number of Clients",
			  display: "Maximum Number of Clients",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "clients - net rogue":{
			  prompt: "Number of Rogue Clients in Net",
			  display: "Number of Rogue Clients in Net",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "clients - map rogue":{
			  prompt: "Number of Rogue Clients on Map",
			  display: "Number of Rogue Clients on Map",
    		  breakdown: false,
			  drilldown: 5,
			  valueInt: true,
    		  validate: 1000
    	  },
    	  "OnPremise - host(s)":{
			  prompt: "HiveManager Host Name(s)",
			  display: "Host Name(s)",
    		  breakdown: false,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "HiveManager OnPremise Host Name(s)"
    	  },
    	  "OnPremise - build":{
			  prompt: "HiveManager Build Time",
			  display: "Build Time",
    		  breakdown: false,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "yyyy-MM-dd'T'HH:mm:ss"
    	  },
    	  "OnPremise - model":{
			  prompt: "HiveManager Model Number",
			  display: "Model Number",
    		  breakdown: false,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "HiveManager(s) OnPremise Model Number"
    	  },
    	  "OnPremise - SN":{
			  prompt: "HiveManager Serial Number",
			  display: "Serial Number",
    		  breakdown: true,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "HiveManager(s) OnPremise Serial Number"
    	  },
    	  "OnPremise - up":{
			  prompt: "HiveManager Up Seconds",
			  display: "System Uptime",
    		  breakdown: true,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "HiveManager(s) OnPremise System UpTime"
    	  },
    	  "OnPremise - HA":{
			  prompt: "HiveManager HA Status",
			  display: "HA Status",
    		  breakdown: false,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "HiveManager OnPremise HA Status"
    	  },
    	  "OnPremise - port(s)":{
			  prompt: "HiveManager Mgt Port(s)",
			  display: "MGT Port",
    		  breakdown: false,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "HiveManager OnPremise Mgt Port(s)"
    	  },
    	  "OnPremise - LAN(s)":{
			  prompt: "HiveManager LAN Port(s)",
			  display: "LAN Port",
    		  breakdown: false,
			  drilldown: 5,
			  homeonly:true,
    		  validate: "HiveManager OnPremise LAN Port(s)"
    	  }
      }
    }//,
  },
  {
    "bandwidth ports":
    {
      prompt: "Port Bandwidth",
      display: "Port Bandwidth",
      defaultChart: "column",
      defaultYaxis: "bytes",
      specifyType:1,
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			}
      }
    }//,
  },

  {
    "bandwidth provider":
    {
      prompt: "Top Aerohive Devices (Aggregations/Ports) by Data Usage",	//  top
      display: "Aerohive Device(s) (Port)",
      defaultChart: "horizontal",
      defaultYaxis: "bytes",
      specifyType:1,
      yaxis:{
    	  "MAC":{
    		  prompt: "MAC Address if Aerohive Device",
    		  display:"MAC",
    		  readonly: true,
			  validate: "MAC"
    	  },
    	  "total distinct client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
    	  },
    	  "total distinct 2.4 GHz client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
    	  },
    	  "total distinct 5 GHz client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
    	  },
    	  "total distinct wireless client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
    	  },
    	  "total distinct wired client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
    	  },
    	  "total distinct sum client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
				valueInt: true,
				validate: 1000
    	  },
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "inbound 2.4 GHz bytes": 
			{
				prompt: "Inbound 2.4 GHz Byte Usage",
				display: "Inbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "inbound 5 GHz bytes":
			{
				prompt: "Inbound 5 GHz Byte Usage",
				display: "Inbound 5 GHz",
				breakdown: true,
				drilldown:1,
				validate: 1000
			},
		  "inbound wired bytes":
			{
				prompt: "Inbound Wired Byte Usage",
				display: "Inbound Wired",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "outbound 2.4 GHz bytes": 
			{
				prompt: "Outbound 2.4 GHz Byte Usage",
				display: "Outbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "outbound 5 GHz bytes": 
			{
				prompt: "Outbound 5 GHz Byte Usage",
				display: "Outbound 5 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "outbound wired bytes":
			{
				prompt: "Outbound Wired Byte Usage",
				display: "Outbound Wired",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			}
      }
    }//,
  },
  {
    "bandwidth provider(interface)":
    {
      prompt: "Top Aerohive Devices (Aggregations/Ports) by Data Usage",	//  top
      display: "Aerohive Device(s) (Port)",
      defaultChart: "horizontal",
      defaultYaxis: "bytes",
      specifyType:1,
      yaxis:{
    	  "MAC":{
    		  prompt: "MAC Address if Aerohive Device",
    		  display:"MAC",
    		  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "MAC"
    	  },
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "inbound 2.4 GHz bytes": 
			{
				prompt: "Inbound 2.4 GHz Byte Usage",
				display: "Inbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "inbound 5 GHz bytes":
			{
				prompt: "Inbound 5 GHz Byte Usage",
				display: "Inbound 5 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "inbound wired bytes":
			{
				prompt: "Inbound Wired Byte Usage",
				display: "Inbound Wired",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "outbound 2.4 GHz bytes": 
			{
				prompt: "Outbound 2.4 GHz Byte Usage",
				display: "Outbound 2.4 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "outbound 5 GHz bytes": 
			{
				prompt: "Outbound 5 GHz Byte Usage",
				display: "Outbound 5 GHz",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "outbound wired bytes":
			{
				prompt: "Outbound Wired Byte Usage",
				display: "Outbound Wired",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			}
      }
    }//,
  },
  {
    "network device(s) (port) alarm level":
    {
      prompt: "Aerohive Device(s) Alarm Distribution",	//  by
      display: "Alarm",
      defaultChart: "pie",
      defaultYaxis: "alarms",
      specifyType:1,
      yaxis:{
    	  "alarms":{
    		  prompt: "Total Number of Alarms",
    		  display:"Alarms",
    		  breakdown: true,
			  drilldown:5,
			  valueInt: true,
			  validate: 1000,
			  presentation: {
				  databased: {
					  "1": {
						  text: "Cleared",
						  color: "#00FF00"
					  },
					  "3": {
						  text: "Minor",
						  color: "#FFFF00"
					  },
					  "4": {
						  text: "Major",
						  color: "#FFA500"
					  },
					  "5": {
						  text: "Critical",
						  color: "#FF0000"
					  }
				  }
			  }
    	  }
      }
    }//,
  },
  {
    "network device (port) w/ errors":
    {
      prompt: "Top Aerohive Devices (Interfaces) by Error %",	//  top
      display: "Aerohive Device (Interface)",
      defaultChart: "horizontal",
      defaultYaxis: "error %",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "MAC":{
    		  prompt: "Aerohive Device MAC Address",
    		  display:"MAC",
    		  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "MAC"
    	  },
    	  "error %":{
    		  prompt:"Error %",
    		  display:"Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz CRC error %":{
    		  prompt:"2.4 GHz CRC Error %",
    		  display:"2.4 GHz CRC Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz transmitting error %":{
    		  prompt:"2.4 GHz Transmitting Drop %",
    		  display:"2.4 GHz Tx Drop %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz receiving error %":{
    		  prompt:"2.4 GHz Receiving Error %",
    		  display:"2.4 GHz Rx Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "5 GHz CRC error %":{
    		  prompt:"5 GHz CRC Error %",
    		  display:"5 GHz CRC Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "5 GHz transmitting error %":{
    		  prompt:"5 GHz Transmitting Drop %",
    		  display:"5 GHz Tx Drop %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "5 GHz receiving error %":{
    		  prompt:"5 GHz Receiving Error %",
    		  display:"5 GHz Rx Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  }
      }
    }//,
  },
  {
    "most error for device":
    {
      prompt: "Devices by CRC Error %",	//  top
      display: "Device",
      defaultChart: "table",
      defaultYaxis: "error %",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "error %":{
    		  prompt:"Error %",
    		  display:"Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz CRC error %":{
    		  prompt:"2.4 GHz CRC Error %",
    		  display:"2.4 GHz CRC Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "5 GHz CRC error %":{
    		  prompt:"5 GHz CRC Error %",
    		  display:"5 GHz CRC Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  }
      }
    }//,
  },
  
  {
    "most error for interface":
    {
      prompt: "Devices by Error %",	//  top
      display: "Device",
      defaultChart: "table",
      defaultYaxis: "CRC Error Rate %",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "CRC Error Rate %":{
    		  prompt:"CRC Error Rate %",
    		  display:"CRC Error Rate %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "Tx Retry Rate %":{
    		  prompt:"Tx Retry Rate %",
    		  display:"Tx Retry Rate %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "Rx Retry Rate %":{
    		  prompt:"Rx Retry Rate %",
    		  display:"Rx Retry Rate %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "Interface":{
    		  prompt:"Interface Name",
    		  display:"Interface Name",
    		  breakdown: false,
			  drilldown:0,
			  validate: "Interface Name"
    	  },
    	  "Report Time":{
    		  prompt:"Report Time",
    		  display:"Report Time",
    		  breakdown: false,
			  drilldown:0,
			  validate: "yyyy-MM-dd'T'HH:mm:ss",
			  dataType: "time"
    	  },
    	  "Collection Period":{
    		  prompt:"Collection Period (seconds)",
    		  display:"Collection Period (seconds)",
    		  breakdown: false,
			  drilldown:0,
			  unit:"SECOND",
			  validate: 1000
    	  }
      }
    }//,
  },
  {
    "Error Types by Percentage for the Top 20 AP":
    {
      prompt: "Top Aerohive Devices by Error %",	//  top
      display: "Aerohive Device",
      defaultChart: "horizontal",
      defaultYaxis: "error %",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "error %":{
    		  prompt:"Error %",
    		  display:"Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz CRC error %":{
    		  prompt:"2.4 GHz CRC Error %",
    		  display:"2.4 GHz CRC Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "2.4 GHz transmitting error %":{
    		  prompt:"2.4 GHz Transmitting Error %",
    		  display:"2.4 GHz Tx Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "2.4 GHz receiving retry %":{
    		  prompt:"2.4 GHz Receiving Retry %",
    		  display:"2.4 GHz Rx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "2.4 GHz transmitting retry %":{
    		  prompt:"2.4 GHz transmitting Retry %",
    		  display:"2.4 GHz Tx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "5 GHz CRC error %":{
    		  prompt:"5 GHz CRC Error %",
    		  display:"5 GHz CRC Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  },
    	  "5 GHz transmitting error %":{
    		  prompt:"5 GHz Transmitting Error %",
    		  display:"5 GHz Tx Error %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  },
    	  "5 GHz receiving retry %":{
    		  prompt:"5 GHz Receiving Retry %",
    		  display:"5 GHz Rx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  },
    	  "5 GHz transmitting retry %":{
    		  prompt:"5 GHz transmitting Retry %",
    		  display:"5 GHz Tx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  }
      }
    }//,
  },
  {
    "devices with the most clients":
    {
      prompt: "Devices with the Most Clients",	//  top
      display: "Device",
      defaultChart: "table",
      defaultYaxis: "maximum concurrent client devices",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "maximum concurrent client devices":{
    		  prompt:"Concurrent Number of Client Devices",
    		  display:"Clients",
    		  breakdown: false,
			  drilldown:0,
			  valueInt: true,
			  validate: 1000
    	  },
    	  "total distinct client devices":{
    		  prompt:"total distinct client devices",
    		  display:"Clients",
    		  breakdown: false,
			  drilldown:0,
			  valueInt: true,
			  validate: 1000
    	  },
    	  "topology":{
    		  prompt:"Topology Name",
    		  display:"Topology Name",
    		  breakdown: false,
			  drilldown:0,
			  validate: "Topology Name"
    	  }
      }
    }//,
  },
  {
    "network device (port) w/ retries":
    {
      prompt: "Top Aerohive Devices (Interfaces) by Retry %",	//  top
      display: "Aerohive Device (Interface)",
      defaultChart: "horizontal",
      defaultYaxis: "retry %",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "MAC":{
    		  prompt: "Aerohive Device MAC Address",
    		  display:"MAC",
    		  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "MAC"
    	  },
    	  "retry %":{
    		  prompt:"Retry %",
    		  display:"Retry %",
			  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz transmitting retry %":{
    		  prompt:"2.4 GHz Transmitting Retry %",
    		  display:"2.4 GHz Tx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "2.4 GHz receiving retry %":{
    		  prompt:"2.4 GHz Receiving Retry %",
    		  display:"2.4 GHz Rx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "5 GHz transmitting retry %":{
    		  prompt:"5 GHz Transmitting Retry %",
    		  display:"5 GHz Tx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  },
    	  "5 GHz receiving retry %":{
    		  prompt:"5 GHz Receiving Retry %",
    		  display:"5 GHz Rx Retry %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  }
      }
    }//,
  },
  {
    "network device (interface) of Channel Utilization":
    {
      prompt: "Top Aerohive Devices (Interfaces) by Channel Utilization",	//  top
      display: "Aerohive Device (Interface)",
      defaultChart: "horizontal",
      defaultYaxis: "Channel Utilization %",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "MAC":{
    		  prompt: "MAC Address",
    		  display:"MAC",
    		  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "MAC"
    	  },
    	  "Channel Utilization %":{
    		  prompt:"Channel Utilization %",
    		  display:"Channel Utilization %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz interference %":{
    		  prompt:"2.4 GHz Interference %",
    		  display:"2.4 GHz Interference %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "2.4 GHz transmitting %":{
    		  prompt:"2.4 GHz Transmitting %",
    		  display:"2.4 GHz Tx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "2.4 GHz receiving %":{
    		  prompt:"2.4 GHz Receiving %",
    		  display:"2.4 GHz Rx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "5 GHz interference %":{
    		  prompt:"5 GHz Interference %",
    		  display:"5 GHz Interference %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  },
    	  "5 GHz transmitting %":{
    		  prompt:"5 GHz Transmitting %",
    		  display:"5 GHz Tx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  },
    	  "5 GHz receiving %":{
    		  prompt:"5 GHz Receiving %",
    		  display:"5 GHz Rx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  }
      }
    }//,
  },
  {
    "network device (interface) of AirTime utilization":
    {
      prompt: "Top Aerohive Devices (Interfaces) by AirTime Utilization",	//  top
      display: "Aerohive Device (Interface)",
      defaultChart: "horizontal",
      defaultYaxis: "AirTime utilization %",
      specifyType:1,
      monitorType: 'device',
      yaxis:{
    	  "MAC":{
    		  prompt: "MAC Address",
    		  display:"MAC",
    		  readonly: true,
			  breakdown: false,
			  drilldown:0,
			  validate: "MAC"
    	  },
    	  "AirTime utilization %":{
    		  prompt:"AirTime Utilization %",
    		  display:"AirTime Utilization %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1
    	  },
    	  "2.4 GHz transmitting %":{
    		  prompt:"2.4 GHz Transmitting %",
    		  display:"2.4 GHz Tx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "2.4 GHz receiving %":{
    		  prompt:"2.4 GHz Receiving %",
    		  display:"2.4 GHz Rx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedRedSetColor
			  }
    	  },
    	  "5 GHz transmitting %":{
    		  prompt:"5 GHz Transmitting %",
    		  display:"5 GHz Tx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  },
    	  "5 GHz receiving %":{
    		  prompt:"5 GHz Receiving %",
    		  display:"5 GHz Rx %",
    		  breakdown: false,
			  drilldown:0,
			  validate: 1,
			  presentation: {
				  colorSelector: Aerohive.presentation.chart.color.GENERATOR_HELPER.getADefinedBlueSetColor
			  }
    	  }
      }
    }//,
  },
  {
    "time":
    {
      prompt: "Over Time",	//  over
      display: "Time",
      defaultChart: "line vertical",
      specifyType:1,
      overtime: true,
      yaxis:{
    	  "bytes":
			{
				prompt: "Network Data Usage (bytes)",
				display: "Data Usage",
				breakdown: true,
				drilldown: 3,
				unit:"B",
				validate: 1000
			},
			"client bytes":
			{
				prompt: "Network Data Usage (bytes)",
				display: "Data Usage",
				breakdown: true,
				drilldown: 3,
				unit:"B",
				validate: 1000
			},	
			"total bandwidth client BPS":
			{
				prompt: "Total Data Usage BPS",
				display: "Data Usage",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
    	  "inbound 2.4 GHz BPS":
			{
				prompt: "Inbound 2.4 GHz BPS",
				display: "Inbound 2.4 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
		  "inbound 5 GHz BPS":
			{
				prompt: "Inbound 5 GHz BPS",
				display: "Inbound 5 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
		  "inbound wired BPS":
			{
				prompt: "Inbound Wired BPS",
				display: "Inbound Wired",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
		  "outbound 2.4 GHz BPS": 
			{
				prompt: "Outbound 2.4 GHz BPS",
				display: "Outbound 2.4 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
		  "outbound 5 GHz BPS": 
			{
				prompt: "Outbound 5 GHz BPS",
				display: "Outbound 5 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
		  "outbound wired BPS":
			{
				prompt: "Outbound Wired BPS",
				display: "Outbound Wired",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
    	  "inbound 2.4 GHz PPS":
			{
				prompt: "Inbound 2.4 GHz PPS",
				display: "Inbound 2.4 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "inbound 5 GHz PPS":
			{
				prompt: "Inbound 5 GHz PPS",
				display: "Inbound 5 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "inbound wired PPS":
			{
				prompt: "Inbound Wired PPS",
				display: "Inbound Wired",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "outbound 2.4 GHz PPS": 
			{
				prompt: "Outbound 2.4 GHz PPS",
				display: "Outbound 2.4 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "outbound 5 GHz PPS": 
			{
				prompt: "Outbound 5 GHz PPS",
				display: "Outbound 5 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "outbound wired PPS":
			{
				prompt: "Outbound Wired PPS",
				display: "Outbound Wired",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "inbound broadcast 2.4 GHz PPS": 
			{
				prompt: "Inbound Broadcast 2.4 GHz PPS",
				display: "Inbound Broadcast 2.4 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "inbound broadcast 5 GHz PPS": 
			{
				prompt: "Inbound Broadcast 5 GHz PPS",
				display: "Inbound Broadcast 5 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "inbound broadcast wired PPS": 
			{
				prompt: "Inbound Broadcast Wired PPS",
				display: "Inbound Broadcast Wired",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "outbound broadcast 2.4 GHz PPS": 
			{
				prompt: "Outbound Broadcast 2.4 GHz PPS",
				display: "Outbound Broadcast 2.4 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "outbound broadcast 5 GHz PPS": 
			{
				prompt: "Outbound Broadcast 5 GHz PPS",
				display: "Outbound Broadcast 5 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
		  "outbound broadcast wired PPS": 
			{
				prompt: "Outbound Broadcast Wired PPS",
				display: "Outbound Broadcast Wired",
				breakdown: true,
				drilldown: 3,
				unit:"PPS",
				validate: 1000
			},
			"top applications BPS":
			{
				prompt: "Top Applications by BPS",
				display: "Bandwidth",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
			"top applications BPS (2.4)":
			{
				prompt: "Top Applications by BPS (2.4 GHz Distribution)",
				display: "2.4 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
			"top applications BPS (5)":
			{
				prompt: "Top Applications by BPS (5 GHz Distribution)",
				display: "5 GHz",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
			"top applications BPS (wired)":
			{
				prompt: "Top Applications by BPS (Wired Distribution)",
				display: "Wired",
				breakdown: true,
				drilldown: 3,
				unit:"BPS",
				validate: 1000
			},
			"total distinct client devices": 
			{
				prompt: "Total Number of Client Devices",
				display: "Total Clients",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
			"total distinct 2.4 GHz client devices": 
			{
				prompt: "total distinct 2.4 GHz client devices",
				display: "Total Clients",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
			"total distinct 5 GHz client devices": 
			{
				prompt: "total distinct 5 GHz client devices",
				display: "Total Clients",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
			"total distinct wired client devices": 
			{
				prompt: "total distinct wired client devices",
				display: "Total Clients",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
			"maximum concurrent client devices": 
			{
	    		  prompt:"Maximum Number of Concurrent Client Devices",
	    		  display:"Maximum Concurrent Clients",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
	    	"maximum concurrent client devices wireless":{
	    		  prompt:"Maximum Number of Concurrent Client Devices wireless",
	    		  display:"Concurrent Clients",
	    		  breakdown: true,
				  drilldown: 5,
				  valueInt: true,
	    		  validate: 1000
	    	  },
			"maximum concurrent client devices 2.4 GHz": 
			{
	    		  prompt:"Maximum Number of Concurrent Client Devices 2.4 GHz",
	    		  display:"Maximum Concurrent Clients 2.4 GHz",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
			"maximum concurrent client devices 5 GHz": 
			{
	    		  prompt:"Maximum Number of Concurrent Client Devices 5 GHz",
	    		  display:"Maximum Concurrent Clients 5 GHz",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
			"maximum concurrent client devices wired": 
			{
	    		  prompt:"Maximum Number of Concurrent Client Devices Wired",
	    		  display:"Maximum Concurrent Clients Wired",
				breakdown: true,
				drilldown: 7,
				valueInt: true,
				validate: 1000
			},
			"all noncompliant clients %": 
			{
				prompt: "Noncompliant Client Devices %",
				display: "Noncompliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"all warning clients %": 
			{
				prompt: "Warning Client Devices %",
				display: "Warning Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FFFF00"
				}
			},
			"all compliant clients %": 
			{
				prompt: "Compliant Client Devices %",
				display: "Compliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"throughput noncompliant clients %": 
			{
				prompt: "Throughput Noncompliant Client Devices %",
				display: "Throughput Noncompliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"throughput warning clients %": 
			{
				prompt: "Throughput Warning Client Devices %",
				display: "Throughput Warning Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FFFF00"
				}
			},
			"throughput compliant clients %": 
			{
				prompt: "Throughput Compliant Client Devices %",
				display: "Throughput Compliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"AirTime noncompliant clients %": 
			{
				prompt: "AirTime Noncompliant Client Devices %",
				display: "AirTime Noncompliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"AirTime Compliant clients %": 
			{
				prompt: "AirTime Compliant Client Devices %",
				display: "AirTime Compliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"health noncompliant clients %": 
			{
				prompt: "Health Noncompliant Client Devices %",
				display: "Health Noncompliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"health warning clients %": 
			{
				prompt: "Health Warning Client Devices %",
				display: "Health Warning Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FFFF00"
				}
			},
			"health compliant clients %": 
			{
				prompt: "Health Compliant Client Devices %",
				display: "Health Compliant Clients %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"all noncompliant providers %": 
			{
				prompt: "Noncompliant Aerohive Devices %",
				display: "Noncompliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"all warning providers %": 
			{
				prompt: "Warning Aerohive Devices %",
				display: "Warning Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FFFF00"
				}
			},
			"all compliant providers %": 
			{
				prompt: "Compliant Aerohive Devices %",
				display: "Compliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"throughput noncompliant providers %": 
			{
				prompt: "Throughput Noncompliant Aerohive Devices %",
				display: "Throughput Noncompliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"throughput warning providers %": 
			{
				prompt: "Throughput Warning Aerohive Devices %",
				display: "Throughput Warning Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FFFF00"
				}
			},
			"throughput compliant providers %": 
			{
				prompt: "Throughput Compliant Aerohive Devices %",
				display: "Throughput Compliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"AirTime noncompliant providers %": 
			{
				prompt: "AirTime Noncompliant Aerohive Devices %",
				display: "AirTime Noncompliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"AirTime compliant providers %": 
			{
				prompt: "AirTime Compliant Aerohive Devices %",
				display: "AirTime Compliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"Rx Drop noncompliant providers %": 
			{
				prompt: "Rx Drop Noncompliant Aerohive Devices %",
				display: "Rx Drop Noncompliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"Rx Drop compliant providers %": 
			{
				prompt: "Rx Drop Compliant Aerohive Devices %",
				display: "Rx Drop Compliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"Tx Drop noncompliant providers %": 
			{
				prompt: "Tx Drop Noncompliant Aerohive Devices %",
				display: "Tx Drop Noncompliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"Tx Drop compliant providers %": 
			{
				prompt: "Tx Drop Compliant Aerohive Devices %",
				display: "Tx Drop Compliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"Tx Retry noncompliant providers %": 
			{
				prompt: "Tx Retry Noncompliant Aerohive Devices %",
				display: "Tx Retry Noncompliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"Tx Retry compliant providers %": 
			{
				prompt: "Tx Retry Compliant Aerohive Devices %",
				display: "Tx Retry Compliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"CRC noncompliant providers %": 
			{
				prompt: "CRC Noncompliant Aerohive Devices %",
				display: "CRC Noncompliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#FF0000"
				}
			},
			"CRC compliant providers %": 
			{
				prompt: "CRC Compliant Aerohive Devices %",
				display: "CRC Compliant Aerohive Devices %",
				breakdown: true,
				drilldown: 7,
				validate: 1,
				presentation: {
					color: "#006600"
				}
			},
			"dropped % @ 2.4 GHz to receive": 
			{
				prompt: "2.4 GHz Receiving Dropped % by SoftWare",
				display: "2.4 GHz Rx Dropped % by SW",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"dropped % @ 5 GHz to receive": 
			{
				prompt: "5 GHz Receiving Dropped % by SoftWare",
				display: "5 GHz Rx Dropped % by SW",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"dropped % @ 2.4 GHz to transmit": 
			{
				prompt: "2.4 GHz Transmitting Dropped % by SoftWare",
				display: "2.4 GHz Tx Dropped % by SW",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"dropped % @ 5 GHz to transmit": 
			{
				prompt: "5 GHz Transmitting Dropped % by SoftWare",
				display: "5 GHz Tx Dropped % by SW",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"dropped % by 2.4 GHz to transmit": 
			{
				prompt: "2.4 GHz Transmitting Dropped % by HardWare",
				display: "2.4 GHz Tx Dropped % by HW",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"dropped % by 5 GHz to transmit": 
			{
				prompt: "5 GHz Transmitting Dropped % by HardWare",
				display: "5 GHz Tx Dropped % by HW",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"average CPU %": 
			{
				prompt: "Average CPU %",
				display: "Average CPU %",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"maximum CPU %": 
			{
				prompt: "Maximum CPU %",
				display: "Maximum CPU %",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"average memory %": 
			{
				prompt: "Average Memory %",
				display: "Average Memory %",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"maximum memory %": 
			{
				prompt: "Maximum Memory %",
				display: "Maximum Memory %",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"up %": 
			{
				prompt: "Aerohive Devices Up %",
				display: "Aerohive Devices Up %",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"2.4 GHz transmitting AirTime %":
			{
				prompt: "2.4 GHz Transmitting AirTime %",
				display: "2.4 GHz Tx",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"2.4 GHz receiving AirTime %": 
			{
				prompt: "2.4 GHz Receiving AirTime %",
				display: "2.4 GHz Rx",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"5 GHz transmitting AirTime %": 
			{
				prompt: "5 GHz Transmitting AirTime %",
				display: "5 GHz Tx",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"5 GHz receiving AirTime %": 
			{
				prompt: "5 GHz Receiving AirTime %",
				display: "5 GHz Rx",
				breakdown: true,
				drilldown: 3,
				validate: 1
			},
			"HiveManager(s) OnPremise CPU %": 
			{
				prompt: "HiveManager(s) CPU %",
				display: "HiveManager(s) CPU %",
				breakdown: false,
				drilldown: 3,
				validate: 1,
				homeonly:true
			},
			"HiveManager(s) OnPremise memory %": 
			{
				prompt: "HiveManager(s) Memory %",
				display: "HiveManager(s) Memory %",
				breakdown: false,
				drilldown: 3,
				validate: 1,
				homeonly:true
			}
      }
    }//,
  },
  {
    "time for client device":
    {
      prompt: "Over Time",	//  over
      display: "Time",
      defaultChart: "line vertical",
      specifyType:2,
      monitorType: 'client',
      overtime: true,
      yaxis:{
    	  "Received Signal Strength Indication":{
    		  prompt: "Received Signal Strength Indication",
    		  display:"RSSI",
    		  breakdown: true,
    		  drilldown: 3,
    		  validate: 1
    	  },
    	  "Signal-to-Noise Ratio":{
    		  prompt:"Signal-to-Noise Ratio",
    		  display:"SNR",
    		  breakdown: true,
    		  drilldown: 3,
    		  validate: 1
    	  },
    	  "average transmitting BPS":{
    		  prompt:"Average Transmitting BPS",
    		  display:"Average Tx BPS",
    		  breakdown: true,
    		  drilldown: 3,
    		  unit:"BPS",
    		  validate: 1000
    	  },
    	  "average receiving BPS":{
    		  prompt:"Average Receiving BPS",
    		  display:"Average Rx BPS",
    		  breakdown: true,
    		  drilldown: 3,
    		  unit:"BPS",
    		  validate: 1000
    	  },
    	  "transmitting AirTime %":{
    		  prompt:"Transmitting AirTime %",
    		  display:"Tx AirTime %",
    		  breakdown: true,
    		  drilldown: 3,
    		  validate: 1
    	  },
    	  "receiving AirTime %":{
    		  prompt:"Receiving AirTime %",
    		  display:"Rx AirTime %",
    		  breakdown: true,
    		  drilldown: 3,
    		  validate: 1
    	  }
      }
    }//,
  },
  {
    "watchlist time for bandwidth application":
    {
      prompt: "time for top N bandwidth application",	//  
      display: "Time",
      defaultChart: "line vertical",
      specifyType:1,
      overtime: true,
      yaxis:{
    	  "bytes":
			{
				prompt: "bytes",
				display: "Usage",
				unit:"B",
				validate: 1000
			}
      }
    }//,
  },
  {
    "time for top N bandwidth application":
    {
      prompt: "time for top N bandwidth application",	//  
      display: "Time",
      defaultChart: "line vertical",
      specifyType:1,
      overtime: true,
      yaxis:{
    	  "bytes":
			{
				prompt: "bytes",
				display: "Usage",
				unit:"B",
				validate: 1000
			}
      }
    }//,
  },
  {
    "interface over time":
    {
      prompt: "interface over time",	//  
      display: "Time",
      defaultChart: "line vertical",
      specifyType:1,
      overtime: true,
      yaxis:{
    	  "2.4 GHz receiving %":
			{
				prompt: "receiving channel %",
				display: "receiving channel %",
				validate: 1
			},
			"5 GHz receiving %":
			{
				prompt: "receiving channel %",
				display: "receiving channel %",
				validate: 1
			},
			"2.4 GHz transmitting %":
			{
				prompt: "transmitting channel %",
				display: "transmitting channel %",
				validate: 1
			},
			"5 GHz transmitting %":
			{
				prompt: "transmitting channel %",
				display: "transmitting channel %",
				validate: 1
			},
			"2.4 GHz interference %":
			{
				prompt: "interference channel %",
				display: "interference channel %",
				validate: 1
			},
			"5 GHz interference %":
			{
				prompt: "interference channel %",
				display: "interference channel %",
				validate: 1
			},
			"2.4 GHz transmitting AirTime %":
			{
				prompt: "transmitting AirTime %",
				display: "transmitting AirTime %",
				validate: 1
			},
			"5 GHz transmitting AirTime %":
			{
				prompt: "transmitting AirTime %",
				display: "transmitting AirTime %",
				validate: 1
			},
			"2.4 GHz receiving AirTime %":
			{
				prompt: "receiving AirTime %",
				display: "receiving AirTime %",
				validate: 1
			},
			"5 GHz receiving AirTime %":
			{
				prompt: "receiving AirTime %",
				display: "receiving AirTime %",
				validate: 1
			},
			"2.4 GHz CRC error %":
			{
				prompt: "CRC error %",
				display: "CRC error %",
				validate: 1
			},
			"5 GHz CRC error %":
			{
				prompt: "CRC error %",
				display: "CRC error %",
				validate: 1
			},
			"2.4 GHz transmitting retry %":
			{
				prompt: "transmitting retry %",
				display: "transmitting retry %",
				validate: 1
			},
			"5 GHz transmitting retry %":
			{
				prompt: "transmitting retry %",
				display: "transmitting retry %",
				validate: 1
			},
			"2.4 GHz transmitting error %":
			{
				prompt: "transmitting error %",
				display: "transmitting error %",
				validate: 1
			},
			"5 GHz transmitting error %":
			{
				prompt: "transmitting error %",
				display: "transmitting error %",
				validate: 1
			}
      }
    }//,
  },
  {
    "switch port over time":
    {
      prompt: "switch port over time",	//  
      display: "Time",
      defaultChart: "line vertical",
      specifyType:1,
      overtime: true,
      yaxis:{
    	   "inbound BPS":
			{
				prompt: "inbound BPS",
				display: "inbound BPS",
				unit:"BPS",
				validate: 1000
			},
			"outbound BPS":
			{
				prompt: "outbound BPS",
				display: "outbound BPS",
				unit:"BPS",
				validate: 1000
			},
			"total BPS":
			{
				prompt: "total BPS",
				display: "total BPS",
				unit:"BPS",
				validate: 1000
			}
      }
    }//,
  },
  {
    "client device information":
    {
      prompt: "Client Device Information",	//  over
      display: "Name",
      defaultChart: "table",
      specifyType:2,
      monitorType: 'client',
      overtime: false,
      yaxis:{
    	  "1st time seen ever":{
    		  prompt: "1st Time This Client Device Was Ever Seen",
    		  display:"1st Time",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
    		  dataType: "time"
    	  },
    	  "comment":{
    		  prompt:"Comment",
    		  display:"Comment",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "last comment"
    	  },
    	  "IP4":{
    		  prompt:"IP4 Address",
    		  display:"IP4",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "IP4 address"
    	  },
    	  "OS/type":{
    		  prompt:"Type",
    		  display:"Type",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "OS/type"
    	  },
    	  "vendor":{
    		  prompt:"Vendor",
    		  display:"Vendor",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "vendor"
    	  },
    	  "UserName":{
    		  prompt:"UserName",
    		  display:"User",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "last UserName"
    	  },
    	  "location watched":{
    		  prompt:"Being Watched for Location or Not",
    		  display:"Location Watched",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: false
    	  },
    	  "monitored":{
    		  prompt:"Being Monitored or Not",
    		  display:"Monitored",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: false
    	  }
      }
    }//,
  },
  {
    "SSId seen":
    {
      prompt: "SSIDs This Client Device Was Seen",	//  over
      display: "SSID",
      defaultChart: "table",
      specifyType:2,
      overtime: false,
      yaxis:{
    	  "last time":{
    		  prompt: "Last Time This Client Device Was Seen by This SSID",
    		  display:"Last Time",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
    		  dataType: "time"
    	  },
    	  "last network device":{
    		  prompt:"Last Aerohive Device Name This Client Device Was Seen by This SSID",
    		  display:"Last Aerohive Device Name",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "Network device name, or MAC address if anonymous"
    	  },
    	  "network device MAC":{
    		  prompt:"Last Aerohive Device MAC Address This Client Device Was Seen by This SSID",
    		  display:"Last Aerohive Device MAC",
    		  breakdown: false,
    		  drilldown: 0,
    		  validate: "Network device Media Access Control address"
    	  }
      }
    }//,
  },
  {
    "time for user":
    {
      prompt: "Over Time",	//  over
      display: "Time",
      defaultChart: "line vertical",
      specifyType:3,
      monitorType: 'user',
      overtime: true,
      yaxis:{
    	  "inbound 2.4 GHz BPS": 
			{
				prompt: "Inbound 2.4 GHz BPS",
				display: "Inbound 2.4 GHz",
				breakdown: true,
	    		drilldown: 3,
	    		unit:"BPS",
				validate: 1000
			},
		  "inbound 5 GHz BPS": 
			{
				prompt: "Inbound 5 GHz BPS",
				display: "Inbound 5 GHz",
				breakdown: true,
	    		drilldown: 3,
	    		unit:"BPS",
				validate: 1000
			},
		  	/*"inbound wired BPS":
  			{
  				prompt: "Inbound Wired BPS",
  				display: "Inbound Wired",
  				breakdown: true,
  				drilldown:1,
  				validate: 1000
  			},  */
		  "outbound 2.4 GHz BPS": 
			{
				prompt: "Outbound 2.4 GHz BPS",
				display: "Outbound 2.4 GHz",
				breakdown: true,
	    		drilldown: 3,
	    		unit:"BPS",
				validate: 1000
			},
		  "outbound 5 GHz BPS": 
			{
				prompt: "Outbound 5 GHz BPS",
				display: "Outbound 5 GHz",
				breakdown: true,
	    		drilldown: 3,
	    		unit:"BPS",
				validate: 1000
			},
  		/*"outbound wired BPS": 
  			{
  				prompt: "Outbound Wired BPS",
  				display: "Outbound Wired",
  				breakdown: true,
  				drilldown:1,
  				validate: 1000
  			},  */
			"total distinct client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
			   breakdown: true,
	    	   drilldown: 7,
	    	   valueInt: true,
			   validate: 1000
		    },
		   "network devices":{
 				prompt: "Total Number of Aerohive Devices",
  				display: "Aerohive Devices",
			   breakdown: true,
	    	   drilldown: 7,
	    	   valueInt: true,
			   validate: 1000
		   },
		   "SSIDs":{
			   prompt:"Total Number of SSIDs",
			   display:"SSIDs",
			   breakdown: true,
	    	   drilldown: 7,
	    	   valueInt: true,
			   validate: 1000
		   },
		   "applications":{
			   prompt:"Total Number of Applications",
			   display:"Applications",
			   breakdown: true,
	    	   drilldown: 7,
	    	   valueInt: true,
			   validate: 1000
		   }
      }
    }//,
  },
  {
    "user information":
    {
      prompt: "User Information",	//  over
      display: "Comment",
      defaultChart: "list",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
    	  "1st time seen ever": 
			{
				prompt: "1st Time This User Was Ever Seen",
				display: "1st Time",
				breakdown: false,
	    		drilldown: 0,
				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
				dataType: "time"
			},
		  "User Profile": 
			{
				prompt: "User Profile",
				display: "User Profile",
				breakdown: false,
	    		drilldown: 0,
				validate: "last User Profile name"
			},
		  "eMail": 
			{
				prompt: "Email",
				display: "Email",
				breakdown: false,
	    		drilldown: 0,
				validate: "last eMail address"
			}
      	}
    }//,
  },
  {
    "device used":
    {
      prompt: "Devices Used",	//  over
      display: "Name",
      defaultChart: "table",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
    	  "MAC": 
			{
				prompt: "MAC Address",
				display: "MAC",
				breakdown: false,
	    		drilldown: 0,
				validate: "MAC"
			},
		  "last time": 
			{
				prompt: "Last Time Used",
				display: "Last Time",
				breakdown: false,
	    		drilldown: 0,
				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
				dataType: "time"
			},
		  "last network device":{
			  	prompt: "Last Aerohive Device Name This Client Device Was Seen",
				display: "Last Aerohive Device Name",
				breakdown: false,
	    		drilldown: 0,
				validate: "Network device name, or MAC address if anonymous"
		  },
		  "network device MAC":{
			  prompt: "Last Aerohive Device MAC Address This Client Device Was Seen",
				display: "Last Aerohive Device MAC",
				breakdown: false,
	    		drilldown: 0,
				validate: "Network device Media Access Control address"
		  },
		  "OS/type": 
			{
				prompt: "Type",
				display: "Type",
				breakdown: false,
	    		drilldown: 0,
				validate: "Client device OS/type"
			},
			"vendor": 
			{
				prompt: "Vendor",
				display: "Vendor",
				breakdown: false,
	    		drilldown: 0,
				validate: "Client device vendor"
			}
      	}
    }//,
  },
  {
    "SSId authenticated":
    {
      prompt: "SSIDs This User Was Authenticated",	//  over
      display: "SSID",
      defaultChart: "table",
      defaultYaxis: "bytes",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
    	  "last time": 
			{
				prompt: "Last Time This User Was Authenticated against This SSID",
				display: "Last Time",
				breakdown: false,
	    		drilldown: 0,
				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
				dataType: "time"
			},
		  "last network device": 
			{
				prompt: "Last Aerohive Device Name This User Was Authenticated against This SSID",
				display: "Last Aerohive Device Name",
				breakdown: false,
	    		drilldown: 0,
				validate: "Network device name, or MAC address if anonymous"
			},
		  "network device MAC": 
			{
				prompt: "Last Aerohive Device MAC Address This User Was Authenticated against This SSID",
				display: "Last Aerohive Device MAC",
				breakdown: false,
	    		drilldown: 0,
				validate: "Network device Media Access Control address"
			},
		  "authentication": 
			{
				prompt: "Authentication Method",
				display: "Authentication Method",
				breakdown: false,
	    		drilldown: 0,
				validate: "authentication method"
			}
      	}
    }//,
  },
  {
    "ssid association list for client":
    {
      prompt: "ssid association list for client",	//  over
      display: "SSID",
      defaultChart: "table",
      defaultYaxis: "bytes",
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
			"ssid":
			{
				prompt: "SSID",
				display: "SSID",
				validate: "SSID"
			},
			"total distinct client users": 
			{
				prompt: "Total Number of Client Users",
				display: "Users",
				valueInt: true,
				validate: 1000
			}
      	}
    }//,
  },
  {
    "ssid association list for client by app":
    {
      prompt: "ssid association list for client",	//  over
      display: "SSID",
      defaultChart: "table",
      defaultYaxis: "bytes",
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
			"ssid":
			{
				prompt: "SSID",
				display: "SSID",
				validate: "SSID"
			},
			"total distinct client users": 
			{
				prompt: "Total Number of Client Users",
				display: "Users",
				valueInt: true,
				validate: 1000
			}
      	}
    }//,
  },
  {
    "device association list for client":
    {
      prompt: "device association list for client",	//  over
      display: "device name",
      defaultChart: "table",
      defaultYaxis: "device mac",
      yaxis:{
    	   "device name":
			{
				prompt: "device name",
				display: "device name",
				validate: "device name"
			},
			"device mac":
			{
				prompt: "device mac",
				display: "device mac",
				validate: "device mac"
			},
			"device location": 
			{
				prompt: "device location",
				display: "device location",
				validate: "device location"
			},
		   "association time": 
			{
				prompt: "association Time",
				display: "association Time",
				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
				dataType: "time"
			}
      	}
    }//,
  },
  {
    "device association list for client by app":
    {
      prompt: "device association list for client",	//  over
      display: "device name",
      defaultChart: "table",
      defaultYaxis: "device mac",
      yaxis:{
    	   "device name":
			{
				prompt: "device name",
				display: "device name",
				validate: "device name"
			},
			"device mac":
			{
				prompt: "device mac",
				display: "device mac",
				validate: "device mac"
			},
			"device location": 
			{
				prompt: "device location",
				display: "device location",
				validate: "device location"
			},
		   "association time": 
			{
				prompt: "association Time",
				display: "association Time",
				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
				dataType: "time"
			}
      	}
    }//,
  },
  {
    "device association list":
    {
      prompt: "device association",	//  over
      display: "Device Name",
      defaultChart: "table",
      defaultYaxis: "device mac",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
		  "device mac": 
			{
				prompt: "Aerohive Device MAC Address",
				display: "Aerohive Device MAC",
				breakdown: false,
	    		drilldown: 0,
				validate: "Network device Media Access Control address"
			},
		  "device location": 
			{
				prompt: "Aerohive Device Location",
				display: "Aerohive Device Location",
				breakdown: false,
	    		drilldown: 0,
				validate: "Network device Location"
			},
		  "association time": 
			{
				prompt: "Association Time",
				display: "Association Time",
				breakdown: false,
	    		drilldown: 0,
				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
				dataType: "time"
			}
      	}
    }//,
  },
  {
    "client list":
    {
      prompt: "client list",	//  over
      display: "client list",
      defaultChart: "table",
      defaultYaxis: "bytes",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "client host name": 
			{
				prompt: "client host name",
				display: "client host name",
				breakdown: false,
	    		drilldown: 0,
				validate: "client host name"
			},
		  "OS/type": 
			{
				prompt: "OS/type",
				display: "OS/type",
				breakdown: false,
	    		drilldown: 0,
				validate: "OS/type"
			},
		  "device name": 
			{
				prompt: "device name",
				display: "device name",
				breakdown: false,
	    		drilldown: 0,
				validate: "device name"
			},
		  "device location": 
			{
				prompt: "device location",
				display: "device location",
				breakdown: false,
	    		drilldown: 0,
				validate: "device location"
			},
		  "ssid": 
			{
				prompt: "ssid name",
				display: "ssid name",
				breakdown: false,
	    		drilldown: 0,
				validate: "ssid name"
			},
		  "authentication": 
			{
				prompt: "Authentication Method",
				display: "Authentication Method",
				breakdown: false,
	    		drilldown: 0,
				validate: "authentication method"
			}
      	}
    }//,
  },
  {
    "client usage by application":
    {
      prompt: "HostName",	//  over
      display: "Application",
      defaultChart: "table",
      defaultYaxis: "bytes",
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
		  "clientmac":
			{
				prompt: "client MAC",
				display: "client MAC",
				validate: "client MAC"
			},			
		  "OS/type": 
			{
				prompt: "OS/type",
				display: "OS/type",
				validate: "OS/type"
			},
		  "last ap associated": 
			{
				prompt: "device name",
				display: "device name",
				validate: "device name"
			},
	  	  	"users": 
  			{
  				prompt: "Total Number of Users",
  				display: "Users",
  				valueInt: true,
  				validate: 1000
  			}
      	}
    }
  },
  {
    "top 20 client by application usage":
    {
      prompt: "HostName",
      display: "Application",
      defaultChart: "table",
      defaultYaxis: "bytes",
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
		  "clientmac":
			{
				prompt: "client MAC",
				display: "client MAC",
				validate: "client MAC"
			}
      	}
    }
  },
  {
    "top 20 application for client device":
    {
      prompt: "Application",
      display: "Application",
      defaultChart: "table",
      defaultYaxis: "bytes",
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
			"percent usage": 
  			{
  				prompt: "Percent Usage",
  				display: "Percent Usage",
  				unit:"%",
  				validate: 1
  			}
      	}
    }
  },
  
  {
    "client detail":
    {
      prompt: "client detail",	//  over
      display: "client detail",
      defaultChart: "list",
      defaultYaxis: "bytes",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "client MAC":
			{
				prompt: "client MAC",
				display: "client MAC",
				breakdown: false,
				drilldown:1,
				validate: "client MAC"
			},			
		  "client host name": 
			{
				prompt: "client host name",
				display: "client host name",
				breakdown: false,
	    		drilldown: 0,
				validate: "client host name"
			},
		  "OS/type": 
			{
				prompt: "OS/type",
				display: "OS/type",
				breakdown: false,
	    		drilldown: 0,
				validate: "OS/type"
			},
		  "last ap associated": 
			{
				prompt: "device name",
				display: "device name",
				breakdown: false,
	    		drilldown: 0,
				validate: "device name"
			},
		  "location": 
			{
				prompt: "device location",
				display: "device location",
				breakdown: false,
	    		drilldown: 0,
				validate: "device location"
			}
      	}
    }//,
  },
  {
    "client detail by app":
    {
      prompt: "client detail",	//  over
      display: "client detail",
      defaultChart: "list",
      defaultYaxis: "bytes",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
    	  "bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:1,
				unit:"B",
				validate: 1000
			},
		  "client MAC":
			{
				prompt: "client MAC",
				display: "client MAC",
				breakdown: false,
				drilldown:1,
				validate: "client MAC"
			},			
		  "client host name": 
			{
				prompt: "client host name",
				display: "client host name",
				breakdown: false,
	    		drilldown: 0,
				validate: "client host name"
			},
		  "OS/type": 
			{
				prompt: "OS/type",
				display: "OS/type",
				breakdown: false,
	    		drilldown: 0,
				validate: "OS/type"
			},
		  "last ap associated": 
			{
				prompt: "device name",
				display: "device name",
				breakdown: false,
	    		drilldown: 0,
				validate: "device name"
			},
		  "location": 
			{
				prompt: "device location",
				display: "device location",
				breakdown: false,
	    		drilldown: 0,
				validate: "device location"
			}
      	}
    }//,
  },
  {
    "switch port detail":
    {
      prompt: "switch port detail",	//  over
      display: "switch port detail",
      defaultChart: "list",
      defaultYaxis: "MAC",
      yaxis:{
    	  "MAC":
			{
				prompt: "MAC",
				display: "MAC",
				validate: "MAC"
			},
		  "portName":
			{
				prompt: "portName",
				display: "portName",
				validate: "portName"
			},			
		  "portType": 
			{
				prompt: "portType",
				display: "portType",
				validate: "portType",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.type)
				}
			},
		  "portChannel": 
			{
				prompt: "portChannel",
				display: "portChannel",
				validate: "portChannel"
			},
		  "voiceVLANs": 
			{
				prompt: "voiceVLANs",
				display: "voiceVLANs",
				validate: "voiceVLANs",
				valueInt: true
			},
		  "dataVLANs": 
			{
				prompt: "dataVLANs",
				display: "dataVLANs",
				validate: "dataVLANs",
				valueInt: true
			},
		  "state": 
			{
				prompt: "Link State",
				display: "state",
				validate: "state",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.linkState)
				}
			},
			"lineProtocol": 
			{
				prompt: "lineProtocol",
				display: "lineProtocol",
				validate: "lineProtocol",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.linkProtocol)
				}
			},
			"authenticationState": 
			{
				prompt: "authenticationState",
				display: "authenticationState",
				validate: "authenticationState",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.authState)
				}
			},
			"STPMode": 
			{
				prompt: "STPMode",
				display: "STPMode",
				validate: "STPMode",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.stp.mode)
				}
			},
			"STPRole": 
			{
				prompt: "STPRole",
				display: "STPRole",
				validate: "STPRole",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.stp.role)
				}
			},
			"STPState": 
			{
				prompt: "STPState",
				display: "STPState",
				validate: "STPState",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.stp.state)
				}
			},
			"STPEnable": 
			{
				prompt: "STPEnable",
				display: "STPEnable",
				validate: "STPEnable",
				valueInt: true,
				presentation: {
					databased: Aerohive.lang.encapPresentationText(Aerohive.lang.chart.port.stp.enable)
				}
			}
      	}
    }//,
  },
  {
    "bandwidth application breakdown":
    {
      prompt: "Bandwidth Application",	//  over
      display: "Application",
      defaultChart: "table",
      defaultYaxis: "bytes",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
      	"bytes":
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  	  	"users": 
  			{
  				prompt: "Total Number of Users",
  				display: "Users",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			}
      	}
    }
  },
  {
    "bandwidth of all application":
    {
      prompt: "bandwidth of all application",	//  over
      display: "Application",
      defaultChart: "table",
      defaultYaxis: "bytes",
      yaxis:{
      	"bytes":
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				unit:"B",
  				validate: 1000
  			},
      	"day bytes":
	      	{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
      	"week bytes":
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				unit:"B",
				validate: 1000
			},
		"percent usage": 
			{
				prompt: "percent usage",
				display: "percent usage",
				unit:"%",
				validate: 1
			}, 
	  	"group": 
  			{
  				prompt: "Application Group",
  				display: "App Group",
  				validate: "App Group Name"
  			}
      	}
    }
  },
  {
    "bandwidth application used":
    {
      prompt: "Top Used Applications by Data Usage",	//  over
      display: "Application",
      defaultChart: "table",
      defaultYaxis: "bytes",
      specifyType:3,
      monitorType: 'user',
      yaxis:{
      	"bytes":
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 2.4 GHz bytes": 
  			{
  				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound wired bytes": 
  			{
  				prompt: "Inbound Wired Byte Usage",
  				display: "Inbound Wired",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound wired bytes": 
  			{
  				prompt: "Outbound Wired Byte Usage",
  				display: "Outbound Wired",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"top SSId": 
  			{
  				prompt: "Top SSID by Data Usage",
  				display: "Top SSID",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth SSId"
  			},
  		"top network device": 
  			{
  				prompt: "Top Aerohive Device by Data Usage",
  				display: "Top Aerohive Device",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth network device"
  			},
  		"top client device": 
  			{
  				prompt: "Top Client Device MAC Assress by Data Usage",
  				display: "Top Client Device MAC",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client MAC address"
  			},
  		"top client host": 
  			{
  				prompt: "Top Client Device Name by Data Usage",
  				display: "Top Client Device Name",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client host name"
  			},
  		"client devices": 
  			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
  				breakdown: true,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"network devices": 
  			{
  				prompt: "Total Number of Aerohive Devices",
  				display: "Aerohive Devices",
  				breakdown: true,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"last time": 
  			{
  				prompt: "Last Time Used",
  				display: "Last Time",
  				breakdown: false,
  				drilldown:0,
  				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
  				dataType: "time"
  			},
  		"group": 
  			{
  				prompt: "Application Group",
  				display: "App Group",
  				breakdown: false,
  				drilldown:0,
  				validate: "App Group Name"
  			}
      	}
    }
  },
  {
    "application information":
    {
      prompt: "Application Information",	//  top
      display: "Last Time",
      defaultChart: "list",
      defaultYaxis:"bytes",
      specifyType:4,
      monitorType: 'app',
      yaxis:{
    	  "application name": 
			{
				prompt: "application name",
				display: "application name",
				breakdown: true,
				drilldown:0,
				validate: "application name"
			},
    	  "bytes": 
			{
				prompt: "Byte Usage",
				display: "Data Usage",
				breakdown: true,
				drilldown:0,
				unit:"B",
				validate: 1000
			},
  		"inbound 2.4 GHz bytes": 
  			{
  				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound wired bytes": 
  			{
  				prompt: "Inbound Wired Byte Usage",
  				display: "Inbound Wired",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound wired bytes": 
  			{
  				prompt: "Outbound Wired Byte Usage",
  				display: "Outbound Wired",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"top user":
  			{
  				prompt: "Top UserName by Data Usage",
  				display: "Top User",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth UserName"
  			},
  		"top SSId": 
  			{
  				prompt: "Top SSID by Data Usage",
  				display: "Top SSID",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth SSId"
  			},
  		"top network device": 
  			{
  				prompt: "Top Aerohive Device by Data Usage",
  				display: "Top Aerohive Device",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth network device"
  			},
  		"top client device": 
  			{
  				prompt: "Top Client Device MAC Address by Data Usage",
  				display: "Top Client Device MAC",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client MAC address"
  			},
  		"top client host": 
  			{
  				prompt: "Top Client Device Name by Data Usage",
  				display: "Top Client Device Name",
  				breakdown: false,
  				drilldown:0,
  				validate: "top bandwidth client host name"
  			},
  		"client devices": 
  			{
				prompt: "Total Number of Client Devices",
				display: "Clients",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"network devices": 
  			{
				prompt: "Total Number of Aerohive Devices",
  				display: "Aerohive Devices",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"users": 
  			{
  				prompt: "Total Number of Users",
  				display: "Users",
  				breakdown: false,
  				drilldown:0,
  				valueInt: true,
  				validate: 1000
  			},
  		"group": 
  			{
  				prompt: "Application Group",
  				display: "App Group",
  				breakdown: false,
  				drilldown:0,
  				validate: "App Group Name"
  			},
		"application description": 
			{
				prompt: "application description",
				display: "application description",
				breakdown: true,
				drilldown:0,
				validate: "application description"
			}
      	}
    }
  },
  {
    "bandwidth user applied":
    {
      prompt: "Top Users by Data Usage",	//  top
      display: "User",
      defaultChart: "table",
      defaultYaxis: "bytes",
      specifyType:4,
      monitorType: 'app',
      yaxis:{
	  "user name": 
		{
			prompt: "user name",
			display: "user name",
			breakdown: false,
			drilldown:0,
			validate: "user name"
		},
      	"bytes": 
  			{
  				prompt: "Byte Usage",
  				display: "Data Usage",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 2.4 GHz bytes": 
  			{
  				prompt: "Inbound 2.4 GHz Byte Usage",
  				display: "Inbound 2.4 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"inbound 5 GHz bytes": 
  			{
  				prompt: "Inbound 5 GHz Byte Usage",
  				display: "Inbound 5 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 2.4 GHz bytes": 
  			{
  				prompt: "Outbound 2.4 GHz Byte Usage",
  				display: "Outbound 2.4 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"outbound 5 GHz bytes": 
  			{
  				prompt: "Outbound 5 GHz Byte Usage",
  				display: "Outbound 5 GHz",
  				breakdown: false,
  				drilldown:0,
  				unit:"B",
  				validate: 1000
  			},
  		"user profile names":
  			{
  			prompt: "user profile names",
			display: "user profile names",
			breakdown: true,
			drilldown:1,
			validate: "user profile names"
			},
	  	"client device names":
  			{
  			prompt: "client device names",
			display: "client device names",
			breakdown: true,
			drilldown:1,
			validate: "client device names"
			},
		 "applications":
  			{
  			prompt: "Total Number of Applications",
			display: "Total Number of Applications",
			breakdown: true,
			drilldown:1,
			valueInt: true,
			validate: 1000
			},
		 "last ap":
  			{
  			prompt: "recent AP asssociated",
			display: "recent AP asssociated",
			breakdown: true,
			drilldown:1,
			validate: "apName"
			},
		 "ssid names":
  			{
  			prompt: "ssid names",
			display: "ssid names",
			breakdown: true,
			drilldown:1,
			validate: "ssid names"
			},
		"last time": 
  			{
  				prompt: "Last Time",
  				display: "Last Time",
  				breakdown: false,
  				drilldown:0,
  				validate: "yyyy-MM-dd'T'HH:mm:ssZ of local Time Zone",
  				dataType: "time"
  			}
      }
    }
  },
  {
    "time for application":
    {
      prompt: "Over Time",	//  over
      display: "Time",
      defaultChart: "line vertical",
      overtime: true,
      yaxis:{
    	  "bytes": 
			{
				prompt: "Usage",
				display: "Usage",
		    	unit:"B",
				validate: 1000
			},
    	  "inbound 2.4 GHz BPS": 
			{
				prompt: "Inbound 2.4 GHz BPS",
				display: "Inbound 2.4 GHz",
		    	unit:"BPS",
				validate: 1000
			},
		  "inbound 5 GHz BPS": 
			{
				prompt: "Inbound 5 GHz BPS",
				display: "Inbound 5 GHz",
		    	unit:"BPS",
				validate: 1000
			},
		  "outbound 2.4 GHz BPS": 
			{
				prompt: "Outbound 2.4 GHz BPS",
				display: "Outbound 2.4 GHz",
		    	unit:"BPS",
				validate: 1000
			},
		  "outbound 5 GHz BPS": 
			{
				prompt: "Outbound 5 GHz BPS",
				display: "Outbound 5 GHz",
		    	unit:"BPS",
				validate: 1000
			},
		   "total distinct client devices":{
				prompt: "Total Number of Client Devices",
				display: "Clients",
			   valueInt: true,
			   validate: 1000
		   },
		   "network devices":{
				prompt: "Total Number of Aerohive Devices",
  				display: "Aerohive Devices",
			   valueInt: true,
			   validate: 1000
		   },
		   "vLANs":{
			   prompt:"Total Number of vLANs",
			   display:"vLANs",
			   breakdown: true,
			   drilldown: 7,
			   valueInt: true,
			   validate: 1000
		   },
		   "users":{
			   prompt:"Total Number of Users",
			   display:"Users",
			   breakdown: true,
			   drilldown: 7,
			   valueInt: true,
			   validate: 1000
		   }
      }
    }//,
  }
];