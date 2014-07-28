;(function() {
var root = this;
Aerohive = root.Aerohive || {};
root.Aerohive = Aerohive;
Aerohive.TIMEZONE = {
	"Pacific/Midway": "(GMT-11:00) Pacific/Midway",
	"Pacific/Niue": "(GMT-11:00) Pacific/Niue",
	"HST": "(GMT-10:00) HST",
	"Pacific/Honolulu": "(GMT-10:00) Pacific/Honolulu",
	"Pacific/Johnston": "(GMT-10:00) Pacific/Johnston",
	"Pacific/Rarotonga": "(GMT-10:00) Pacific/Rarotonga",
	"Pacific/Tahiti": "(GMT-10:00) Pacific/Tahiti",
	"US/Hawaii": "(GMT-10:00) US/Hawaii",
	"Pacific/Marquesas": "(GMT-09:30) Pacific/Marquesas",
	"America/Anchorage": "(GMT-09:00) America/Anchorage",
	"America/Juneau": "(GMT-09:00) America/Juneau",
	"America/Nome": "(GMT-09:00) America/Nome",
	"America/Sitka": "(GMT-09:00) America/Sitka",
	"America/Yakutat": "(GMT-09:00) America/Yakutat",
	"Pacific/Gambier": "(GMT-09:00) Pacific/Gambier",
	"US/Alaska": "(GMT-09:00) US/Alaska",
	"America/Dawson": "(GMT-08:00) America/Dawson",
	"America/Ensenada": "(GMT-08:00) America/Ensenada",
	"America/Los_Angeles": "(GMT-08:00) America/Los_Angeles",
	"America/Metlakatla": "(GMT-08:00) America/Metlakatla",
	"America/Santa_Isabel": "(GMT-08:00) America/Santa_Isabel",
	"America/Tijuana": "(GMT-08:00) America/Tijuana",
	"America/Vancouver": "(GMT-08:00) America/Vancouver",
	"America/Whitehorse": "(GMT-08:00) America/Whitehorse",
	"Canada/Pacific": "(GMT-08:00) Canada/Pacific",
	"Canada/Yukon": "(GMT-08:00) Canada/Yukon",
	"Mexico/BajaNorte": "(GMT-08:00) Mexico/BajaNorte",
	"PST8PDT": "(GMT-08:00) PST8PDT",
	"Pacific/Pitcairn": "(GMT-08:00) Pacific/Pitcairn",
	"US/Pacific": "(GMT-08:00) US/Pacific",
	"America/Boise": "(GMT-07:00) America/Boise",
	"America/Cambridge_Bay": "(GMT-07:00) America/Cambridge_Bay",
	"America/Chihuahua": "(GMT-07:00) America/Chihuahua",
	"America/Creston": "(GMT-07:00) America/Creston",
	"America/Dawson_Creek": "(GMT-07:00) America/Dawson_Creek",
	"America/Denver": "(GMT-07:00) America/Denver",
	"America/Edmonton": "(GMT-07:00) America/Edmonton",
	"America/Hermosillo": "(GMT-07:00) America/Hermosillo",
	"America/Inuvik": "(GMT-07:00) America/Inuvik",
	"America/Mazatlan": "(GMT-07:00) America/Mazatlan",
	"America/Ojinaga": "(GMT-07:00) America/Ojinaga",
	"America/Phoenix": "(GMT-07:00) America/Phoenix",
	"America/Shiprock": "(GMT-07:00) America/Shiprock",
	"America/Yellowknife": "(GMT-07:00) America/Yellowknife",
	"Canada/Mountain": "(GMT-07:00) Canada/Mountain",
	"MST": "(GMT-07:00) MST",
	"MST7MDT": "(GMT-07:00) MST7MDT",
	"Mexico/BajaSur": "(GMT-07:00) Mexico/BajaSur",
	"Navajo": "(GMT-07:00) Navajo",
	"US/Arizona": "(GMT-07:00) US/Arizona",
	"US/Mountain": "(GMT-07:00) US/Mountain",
	"America/Belize": "(GMT-06:00) America/Belize",
	"America/Cancun": "(GMT-06:00) America/Cancun",
	"America/Chicago": "(GMT-06:00) America/Chicago",
	"America/Costa_Rica": "(GMT-06:00) America/Costa_Rica",
	"America/El_Salvador": "(GMT-06:00) America/El_Salvador",
	"America/Guatemala": "(GMT-06:00) America/Guatemala",
	"America/Indiana/Knox": "(GMT-06:00) America/Indiana/Knox",
	"America/Indiana/Tell_City": "(GMT-06:00) America/Indiana/Tell_City",
	"America/Knox_IN": "(GMT-06:00) America/Knox_IN",
	"America/Managua": "(GMT-06:00) America/Managua",
	"America/Matamoros": "(GMT-06:00) America/Matamoros",
	"America/Menominee": "(GMT-06:00) America/Menominee",
	"America/Merida": "(GMT-06:00) America/Merida",
	"America/Mexico_City": "(GMT-06:00) America/Mexico_City",
	"America/Monterrey": "(GMT-06:00) America/Monterrey",
	"America/North_Dakota/Beulah": "(GMT-06:00) America/North_Dakota/Beulah",
	"America/North_Dakota/Center": "(GMT-06:00) America/North_Dakota/Center",
	"America/North_Dakota/New_Salem": "(GMT-06:00) America/North_Dakota/New_Salem",
	"America/Rainy_River": "(GMT-06:00) America/Rainy_River",
	"America/Rankin_Inlet": "(GMT-06:00) America/Rankin_Inlet",
	"America/Regina": "(GMT-06:00) America/Regina",
	"America/Resolute": "(GMT-06:00) America/Resolute",
	"America/Swift_Current": "(GMT-06:00) America/Swift_Current",
	"America/Tegucigalpa": "(GMT-06:00) America/Tegucigalpa",
	"America/Winnipeg": "(GMT-06:00) America/Winnipeg",
	"CST6CDT": "(GMT-06:00) CST6CDT",
	"Canada/Central": "(GMT-06:00) Canada/Central",
	"Canada/East-Saskatchewan": "(GMT-06:00) Canada/East-Saskatchewan",
	"Canada/Saskatchewan": "(GMT-06:00) Canada/Saskatchewan",
	"Chile/EasterIsland": "(GMT-06:00) Chile/EasterIsland",
	"Mexico/General": "(GMT-06:00) Mexico/General",
	"Pacific/Easter": "(GMT-06:00) Pacific/Easter",
	"Pacific/Galapagos": "(GMT-06:00) Pacific/Galapagos",
	"US/Central": "(GMT-06:00) US/Central",
	"US/Indiana-Starke": "(GMT-06:00) US/Indiana-Starke",
	"America/Atikokan": "(GMT-05:00) America/Atikokan",
	"America/Bogota": "(GMT-05:00) America/Bogota",
	"America/Cayman": "(GMT-05:00) America/Cayman",
	"America/Coral_Harbour": "(GMT-05:00) America/Coral_Harbour",
	"America/Detroit": "(GMT-05:00) America/Detroit",
	"America/Fort_Wayne": "(GMT-05:00) America/Fort_Wayne",
	"America/Grand_Turk": "(GMT-05:00) America/Grand_Turk",
	"America/Havana": "(GMT-05:00) America/Havana",
	"America/Indiana/Indianapolis": "(GMT-05:00) America/Indiana/Indianapolis",
	"America/Indiana/Marengo": "(GMT-05:00) America/Indiana/Marengo",
	"America/Indiana/Petersburg": "(GMT-05:00) America/Indiana/Petersburg",
	"America/Indiana/Vevay": "(GMT-05:00) America/Indiana/Vevay",
	"America/Indiana/Vincennes": "(GMT-05:00) America/Indiana/Vincennes",
	"America/Indiana/Winamac": "(GMT-05:00) America/Indiana/Winamac",
	"America/Indianapolis": "(GMT-05:00) America/Indianapolis",
	"America/Iqaluit": "(GMT-05:00) America/Iqaluit",
	"America/Jamaica": "(GMT-05:00) America/Jamaica",
	"America/Kentucky/Louisville": "(GMT-05:00) America/Kentucky/Louisville",
	"America/Kentucky/Monticello": "(GMT-05:00) America/Kentucky/Monticello",
	"America/Lima": "(GMT-05:00) America/Lima",
	"America/Louisville": "(GMT-05:00) America/Louisville",
	"America/Montreal": "(GMT-05:00) America/Montreal",
	"America/Nassau": "(GMT-05:00) America/Nassau",
	"America/New_York": "(GMT-05:00) America/New_York",
	"America/Nipigon": "(GMT-05:00) America/Nipigon",
	"America/Panama": "(GMT-05:00) America/Panama",
	"America/Pangnirtung": "(GMT-05:00) America/Pangnirtung",
	"America/Port-au-Prince": "(GMT-05:00) America/Port-au-Prince",
	"America/Thunder_Bay": "(GMT-05:00) America/Thunder_Bay",
	"America/Toronto": "(GMT-05:00) America/Toronto",
	"Canada/Eastern": "(GMT-05:00) Canada/Eastern",
	"Cuba": "(GMT-05:00) Cuba",
	"EST": "(GMT-05:00) EST",
	"EST5EDT": "(GMT-05:00) EST5EDT",
	"Jamaica": "(GMT-05:00) Jamaica",
	"US/East-Indiana": "(GMT-05:00) US/East-Indiana",
	"US/Eastern": "(GMT-05:00) US/Eastern",
	"US/Michigan": "(GMT-05:00) US/Michigan",
	"America/Caracas": "(GMT-04:30) America/Caracas",
	"America/Anguilla": "(GMT-04:00) America/Anguilla",
	"America/Antigua": "(GMT-04:00) America/Antigua",
	"America/Aruba": "(GMT-04:00) America/Aruba",
	"America/Asuncion": "(GMT-04:00) America/Asuncion",
	"America/Barbados": "(GMT-04:00) America/Barbados",
	"America/Blanc-Sablon": "(GMT-04:00) America/Blanc-Sablon",
	"America/Boa_Vista": "(GMT-04:00) America/Boa_Vista",
	"America/Campo_Grande": "(GMT-04:00) America/Campo_Grande",
	"America/Cuiaba": "(GMT-04:00) America/Cuiaba",
	"America/Curacao": "(GMT-04:00) America/Curacao",
	"America/Dominica": "(GMT-04:00) America/Dominica",
	"America/Eirunepe": "(GMT-04:00) America/Eirunepe",
	"America/Glace_Bay": "(GMT-04:00) America/Glace_Bay",
	"America/Goose_Bay": "(GMT-04:00) America/Goose_Bay",
	"America/Grenada": "(GMT-04:00) America/Grenada",
	"America/Guadeloupe": "(GMT-04:00) America/Guadeloupe",
	"America/Guyana": "(GMT-04:00) America/Guyana",
	"America/Halifax": "(GMT-04:00) America/Halifax",
	"America/Kralendijk": "(GMT-04:00) America/Kralendijk",
	"America/La_Paz": "(GMT-04:00) America/La_Paz",
	"America/Lower_Princes": "(GMT-04:00) America/Lower_Princes",
	"America/Manaus": "(GMT-04:00) America/Manaus",
	"America/Marigot": "(GMT-04:00) America/Marigot",
	"America/Martinique": "(GMT-04:00) America/Martinique",
	"America/Moncton": "(GMT-04:00) America/Moncton",
	"America/Montserrat": "(GMT-04:00) America/Montserrat",
	"America/Port_of_Spain": "(GMT-04:00) America/Port_of_Spain",
	"America/Porto_Acre": "(GMT-04:00) America/Porto_Acre",
	"America/Porto_Velho": "(GMT-04:00) America/Porto_Velho",
	"America/Puerto_Rico": "(GMT-04:00) America/Puerto_Rico",
	"America/Rio_Branco": "(GMT-04:00) America/Rio_Branco",
	"America/Santiago": "(GMT-04:00) America/Santiago",
	"America/Santo_Domingo": "(GMT-04:00) America/Santo_Domingo",
	"America/St_Barthelemy": "(GMT-04:00) America/St_Barthelemy",
	"America/St_Kitts": "(GMT-04:00) America/St_Kitts",
	"America/St_Lucia": "(GMT-04:00) America/St_Lucia",
	"America/St_Thomas": "(GMT-04:00) America/St_Thomas",
	"America/St_Vincent": "(GMT-04:00) America/St_Vincent",
	"America/Thule": "(GMT-04:00) America/Thule",
	"America/Tortola": "(GMT-04:00) America/Tortola",
	"America/Virgin": "(GMT-04:00) America/Virgin",
	"Antarctica/Palmer": "(GMT-04:00) Antarctica/Palmer",
	"Atlantic/Bermuda": "(GMT-04:00) Atlantic/Bermuda",
	"Brazil/Acre": "(GMT-04:00) Brazil/Acre",
	"Brazil/West": "(GMT-04:00) Brazil/West",
	"Canada/Atlantic": "(GMT-04:00) Canada/Atlantic",
	"Chile/Continental": "(GMT-04:00) Chile/Continental",
	"America/St_Johns": "(GMT-03:30) America/St_Johns",
	"Canada/Newfoundland": "(GMT-03:30) Canada/Newfoundland",
	"America/Araguaina": "(GMT-03:00) America/Araguaina",
	"America/Argentina/Buenos_Aires": "(GMT-03:00) America/Argentina/Buenos_Aires",
	"America/Argentina/Catamarca": "(GMT-03:00) America/Argentina/Catamarca",
	"America/Argentina/ComodRivadavia": "(GMT-03:00) America/Argentina/ComodRivadavia",
	"America/Argentina/Cordoba": "(GMT-03:00) America/Argentina/Cordoba",
	"America/Argentina/Jujuy": "(GMT-03:00) America/Argentina/Jujuy",
	"America/Argentina/La_Rioja": "(GMT-03:00) America/Argentina/La_Rioja",
	"America/Argentina/Mendoza": "(GMT-03:00) America/Argentina/Mendoza",
	"America/Argentina/Rio_Gallegos": "(GMT-03:00) America/Argentina/Rio_Gallegos",
	"America/Argentina/Salta": "(GMT-03:00) America/Argentina/Salta",
	"America/Argentina/San_Juan": "(GMT-03:00) America/Argentina/San_Juan",
	"America/Argentina/Tucuman": "(GMT-03:00) America/Argentina/Tucuman",
	"America/Argentina/Ushuaia": "(GMT-03:00) America/Argentina/Ushuaia",
	"America/Bahia": "(GMT-03:00) America/Bahia",
	"America/Belem": "(GMT-03:00) America/Belem",
	"America/Buenos_Aires": "(GMT-03:00) America/Buenos_Aires",
	"America/Catamarca": "(GMT-03:00) America/Catamarca",
	"America/Cayenne": "(GMT-03:00) America/Cayenne",
	"America/Cordoba": "(GMT-03:00) America/Cordoba",
	"America/Fortaleza": "(GMT-03:00) America/Fortaleza",
	"America/Godthab": "(GMT-03:00) America/Godthab",
	"America/Jujuy": "(GMT-03:00) America/Jujuy",
	"America/Maceio": "(GMT-03:00) America/Maceio",
	"America/Mendoza": "(GMT-03:00) America/Mendoza",
	"America/Miquelon": "(GMT-03:00) America/Miquelon",
	"America/Montevideo": "(GMT-03:00) America/Montevideo",
	"America/Recife": "(GMT-03:00) America/Recife",
	"America/Rosario": "(GMT-03:00) America/Rosario",
	"America/Santarem": "(GMT-03:00) America/Santarem",
	"America/Sao_Paulo": "(GMT-03:00) America/Sao_Paulo",
	"Atlantic/Stanley": "(GMT-03:00) Atlantic/Stanley",
	"Brazil/East": "(GMT-03:00) Brazil/East",
	"America/Noronha": "(GMT-02:00) America/Noronha",
	"Brazil/DeNoronha": "(GMT-02:00) Brazil/DeNoronha",
	"America/Scoresbysund": "(GMT-01:00) America/Scoresbysund",
	"Atlantic/Azores": "(GMT-01:00) Atlantic/Azores",
	"Africa/Abidjan": "(GMT) Africa/Abidjan",
	"Africa/Accra": "(GMT) Africa/Accra",
	"Africa/Bamako": "(GMT) Africa/Bamako",
	"Africa/Banjul": "(GMT) Africa/Banjul",
	"Africa/Bissau": "(GMT) Africa/Bissau",
	"Africa/Casablanca": "(GMT) Africa/Casablanca",
	"Africa/Conakry": "(GMT) Africa/Conakry",
	"Africa/Dakar": "(GMT) Africa/Dakar",
	"Africa/El_Aaiun": "(GMT) Africa/El_Aaiun",
	"Africa/Freetown": "(GMT) Africa/Freetown",
	"Africa/Lome": "(GMT) Africa/Lome",
	"Africa/Monrovia": "(GMT) Africa/Monrovia",
	"Africa/Nouakchott": "(GMT) Africa/Nouakchott",
	"Africa/Ouagadougou": "(GMT) Africa/Ouagadougou",
	"Africa/Sao_Tome": "(GMT) Africa/Sao_Tome",
	"Africa/Timbuktu": "(GMT) Africa/Timbuktu",
	"America/Danmarkshavn": "(GMT) America/Danmarkshavn",
	"Atlantic/Canary": "(GMT) Atlantic/Canary",
	"Atlantic/Faeroe": "(GMT) Atlantic/Faeroe",
	"Atlantic/Faroe": "(GMT) Atlantic/Faroe",
	"Atlantic/Madeira": "(GMT) Atlantic/Madeira",
	"Atlantic/Reykjavik": "(GMT) Atlantic/Reykjavik",
	"Atlantic/St_Helena": "(GMT) Atlantic/St_Helena",
	"Eire": "(GMT) Eire",
	"Etc/Greenwich": "(GMT) Etc/Greenwich",
	"Etc/UCT": "(GMT) Etc/UCT",
	"Etc/UTC": "(GMT) Etc/UTC",
	"Etc/Universal": "(GMT) Etc/Universal",
	"Etc/Zulu": "(GMT) Etc/Zulu",
	"Europe/Belfast": "(GMT) Europe/Belfast",
	"Europe/Dublin": "(GMT) Europe/Dublin",
	"Europe/Guernsey": "(GMT) Europe/Guernsey",
	"Europe/Isle_of_Man": "(GMT) Europe/Isle_of_Man",
	"Europe/Jersey": "(GMT) Europe/Jersey",
	"Europe/Lisbon": "(GMT) Europe/Lisbon",
	"Europe/London": "(GMT) Europe/London",
	"GB": "(GMT) GB",
	"GB-Eire": "(GMT) GB-Eire",
	"GMT": "(GMT) GMT",
	"GMT0": "(GMT) GMT0",
	"Greenwich": "(GMT) Greenwich",
	"Iceland": "(GMT) Iceland",
	"Portugal": "(GMT) Portugal",
	"UCT": "(GMT) UCT",
	"UTC": "(GMT) UTC",
	"Universal": "(GMT) Universal",
	"WET": "(GMT) WET",
	"Zulu": "(GMT) Zulu",
	"Africa/Algiers": "(GMT+01:00) Africa/Algiers",
	"Africa/Bangui": "(GMT+01:00) Africa/Bangui",
	"Africa/Brazzaville": "(GMT+01:00) Africa/Brazzaville",
	"Africa/Ceuta": "(GMT+01:00) Africa/Ceuta",
	"Africa/Douala": "(GMT+01:00) Africa/Douala",
	"Africa/Kinshasa": "(GMT+01:00) Africa/Kinshasa",
	"Africa/Lagos": "(GMT+01:00) Africa/Lagos",
	"Africa/Libreville": "(GMT+01:00) Africa/Libreville",
	"Africa/Luanda": "(GMT+01:00) Africa/Luanda",
	"Africa/Malabo": "(GMT+01:00) Africa/Malabo",
	"Africa/Ndjamena": "(GMT+01:00) Africa/Ndjamena",
	"Africa/Niamey": "(GMT+01:00) Africa/Niamey",
	"Africa/Porto-Novo": "(GMT+01:00) Africa/Porto-Novo",
	"Africa/Tunis": "(GMT+01:00) Africa/Tunis",
	"Africa/Windhoek": "(GMT+01:00) Africa/Windhoek",
	"Arctic/Longyearbyen": "(GMT+01:00) Arctic/Longyearbyen",
	"Atlantic/Jan_Mayen": "(GMT+01:00) Atlantic/Jan_Mayen",
	"CET": "(GMT+01:00) CET",
	"Europe/Amsterdam": "(GMT+01:00) Europe/Amsterdam",
	"Europe/Andorra": "(GMT+01:00) Europe/Andorra",
	"Europe/Belgrade": "(GMT+01:00) Europe/Belgrade",
	"Europe/Berlin": "(GMT+01:00) Europe/Berlin",
	"Europe/Bratislava": "(GMT+01:00) Europe/Bratislava",
	"Europe/Brussels": "(GMT+01:00) Europe/Brussels",
	"Europe/Budapest": "(GMT+01:00) Europe/Budapest",
	"Europe/Copenhagen": "(GMT+01:00) Europe/Copenhagen",
	"Europe/Gibraltar": "(GMT+01:00) Europe/Gibraltar",
	"Europe/Ljubljana": "(GMT+01:00) Europe/Ljubljana",
	"Europe/Luxembourg": "(GMT+01:00) Europe/Luxembourg",
	"Europe/Madrid": "(GMT+01:00) Europe/Madrid",
	"Europe/Malta": "(GMT+01:00) Europe/Malta",
	"Europe/Monaco": "(GMT+01:00) Europe/Monaco",
	"Europe/Oslo": "(GMT+01:00) Europe/Oslo",
	"Europe/Paris": "(GMT+01:00) Europe/Paris",
	"Europe/Podgorica": "(GMT+01:00) Europe/Podgorica",
	"Europe/Prague": "(GMT+01:00) Europe/Prague",
	"Europe/Rome": "(GMT+01:00) Europe/Rome",
	"Europe/San_Marino": "(GMT+01:00) Europe/San_Marino",
	"Europe/Sarajevo": "(GMT+01:00) Europe/Sarajevo",
	"Europe/Skopje": "(GMT+01:00) Europe/Skopje",
	"Europe/Stockholm": "(GMT+01:00) Europe/Stockholm",
	"Europe/Tirane": "(GMT+01:00) Europe/Tirane",
	"Europe/Vaduz": "(GMT+01:00) Europe/Vaduz",
	"Europe/Vatican": "(GMT+01:00) Europe/Vatican",
	"Europe/Vienna": "(GMT+01:00) Europe/Vienna",
	"Europe/Warsaw": "(GMT+01:00) Europe/Warsaw",
	"Europe/Zagreb": "(GMT+01:00) Europe/Zagreb",
	"Europe/Zurich": "(GMT+01:00) Europe/Zurich",
	"MET": "(GMT+01:00) MET",
	"Poland": "(GMT+01:00) Poland",
	"Africa/Cairo": "(GMT+02:00) Africa/Cairo",
	"Africa/Johannesburg": "(GMT+02:00) Africa/Johannesburg",
	"Africa/Maseru": "(GMT+02:00) Africa/Maseru",
	"Africa/Mbabane": "(GMT+02:00) Africa/Mbabane",
	"Africa/Tripoli": "(GMT+02:00) Africa/Tripoli",
	"Asia/Amman": "(GMT+02:00) Asia/Amman",
	"Asia/Beirut": "(GMT+02:00) Asia/Beirut",
	"Asia/Damascus": "(GMT+02:00) Asia/Damascus",
	"Asia/Gaza": "(GMT+02:00) Asia/Gaza",
	"Asia/Hebron": "(GMT+02:00) Asia/Hebron",
	"Asia/Istanbul": "(GMT+02:00) Asia/Istanbul",
	"Asia/Jerusalem": "(GMT+02:00) Asia/Jerusalem",
	"Asia/Nicosia": "(GMT+02:00) Asia/Nicosia",
	"Asia/Tel_Aviv": "(GMT+02:00) Asia/Tel_Aviv",
	"EET": "(GMT+02:00) EET",
	"Egypt": "(GMT+02:00) Egypt",
	"Europe/Athens": "(GMT+02:00) Europe/Athens",
	"Europe/Bucharest": "(GMT+02:00) Europe/Bucharest",
	"Europe/Chisinau": "(GMT+02:00) Europe/Chisinau",
	"Europe/Helsinki": "(GMT+02:00) Europe/Helsinki",
	"Europe/Istanbul": "(GMT+02:00) Europe/Istanbul",
	"Europe/Kiev": "(GMT+02:00) Europe/Kiev",
	"Europe/Mariehamn": "(GMT+02:00) Europe/Mariehamn",
	"Europe/Nicosia": "(GMT+02:00) Europe/Nicosia",
	"Europe/Riga": "(GMT+02:00) Europe/Riga",
	"Europe/Simferopol": "(GMT+02:00) Europe/Simferopol",
	"Europe/Sofia": "(GMT+02:00) Europe/Sofia",
	"Europe/Tallinn": "(GMT+02:00) Europe/Tallinn",
	"Europe/Tiraspol": "(GMT+02:00) Europe/Tiraspol",
	"Europe/Uzhgorod": "(GMT+02:00) Europe/Uzhgorod",
	"Europe/Vilnius": "(GMT+02:00) Europe/Vilnius",
	"Europe/Zaporozhye": "(GMT+02:00) Europe/Zaporozhye",
	"Israel": "(GMT+02:00) Israel",
	"Libya": "(GMT+02:00) Libya",
	"Turkey": "(GMT+02:00) Turkey",
	"Africa/Addis_Ababa": "(GMT+03:00) Africa/Addis_Ababa",
	"Africa/Asmara": "(GMT+03:00) Africa/Asmara",
	"Africa/Asmera": "(GMT+03:00) Africa/Asmera",
	"Africa/Dar_es_Salaam": "(GMT+03:00) Africa/Dar_es_Salaam",
	"Africa/Djibouti": "(GMT+03:00) Africa/Djibouti",
	"Africa/Juba": "(GMT+03:00) Africa/Juba",
	"Africa/Kampala": "(GMT+03:00) Africa/Kampala",
	"Africa/Khartoum": "(GMT+03:00) Africa/Khartoum",
	"Africa/Mogadishu": "(GMT+03:00) Africa/Mogadishu",
	"Africa/Nairobi": "(GMT+03:00) Africa/Nairobi",
	"Asia/Aden": "(GMT+03:00) Asia/Aden",
	"Asia/Baghdad": "(GMT+03:00) Asia/Baghdad",
	"Asia/Bahrain": "(GMT+03:00) Asia/Bahrain",
	"Asia/Kuwait": "(GMT+03:00) Asia/Kuwait",
	"Asia/Qatar": "(GMT+03:00) Asia/Qatar",
	"Asia/Riyadh": "(GMT+03:00) Asia/Riyadh",
	"Europe/Kaliningrad": "(GMT+03:00) Europe/Kaliningrad",
	"Europe/Minsk": "(GMT+03:00) Europe/Minsk",
	"Indian/Antananarivo": "(GMT+03:00) Indian/Antananarivo",
	"Indian/Comoro": "(GMT+03:00) Indian/Comoro",
	"Indian/Mayotte": "(GMT+03:00) Indian/Mayotte",
	"Asia/Baku": "(GMT+04:00) Asia/Baku",
	"Asia/Dubai": "(GMT+04:00) Asia/Dubai",
	"Asia/Muscat": "(GMT+04:00) Asia/Muscat",
	"Asia/Tbilisi": "(GMT+04:00) Asia/Tbilisi",
	"Asia/Yerevan": "(GMT+04:00) Asia/Yerevan",
	"Europe/Moscow": "(GMT+04:00) Europe/Moscow",
	"Indian/Mahe": "(GMT+04:00) Indian/Mahe",
	"Indian/Mauritius": "(GMT+04:00) Indian/Mauritius",
	"Indian/Reunion": "(GMT+04:00) Indian/Reunion",
	"W-SU": "(GMT+04:00) W-SU",
	"Asia/Kabul": "(GMT+04:30) Asia/Kabul",
	"Antarctica/Mawson": "(GMT+05:00) Antarctica/Mawson",
	"Asia/Ashgabat": "(GMT+05:00) Asia/Ashgabat",
	"Asia/Ashkhabad": "(GMT+05:00) Asia/Ashkhabad",
	"Asia/Dushanbe": "(GMT+05:00) Asia/Dushanbe",
	"Asia/Karachi": "(GMT+05:00) Asia/Karachi",
	"Asia/Samarkand": "(GMT+05:00) Asia/Samarkand",
	"Asia/Tashkent": "(GMT+05:00) Asia/Tashkent",
	"Indian/Kerguelen": "(GMT+05:00) Indian/Kerguelen",
	"Indian/Maldives": "(GMT+05:00) Indian/Maldives",
	"Asia/Calcutta": "(GMT+05:30) Asia/Calcutta",
	"Asia/Colombo": "(GMT+05:30) Asia/Colombo",
	"Asia/Kolkata": "(GMT+05:30) Asia/Kolkata",
	"Asia/Kathmandu": "(GMT+05:45) Asia/Kathmandu",
	"Asia/Katmandu": "(GMT+05:45) Asia/Katmandu",
	"Asia/Almaty": "(GMT+06:00) Asia/Almaty",
	"Asia/Bishkek": "(GMT+06:00) Asia/Bishkek",
	"Asia/Dacca": "(GMT+06:00) Asia/Dacca",
	"Asia/Dhaka": "(GMT+06:00) Asia/Dhaka",
	"Asia/Thimbu": "(GMT+06:00) Asia/Thimbu",
	"Asia/Thimphu": "(GMT+06:00) Asia/Thimphu",
	"Asia/Yekaterinburg": "(GMT+06:00) Asia/Yekaterinburg",
	"Indian/Chagos": "(GMT+06:00) Indian/Chagos",
	"Asia/Rangoon": "(GMT+06:30) Asia/Rangoon",
	"Indian/Cocos": "(GMT+06:30) Indian/Cocos",
	"Antarctica/Davis": "(GMT+07:00) Antarctica/Davis",
	"Asia/Bangkok": "(GMT+07:00) Asia/Bangkok",
	"Asia/Ho_Chi_Minh": "(GMT+07:00) Asia/Ho_Chi_Minh",
	"Asia/Jakarta": "(GMT+07:00) Asia/Jakarta",
	"Asia/Novokuznetsk": "(GMT+07:00) Asia/Novokuznetsk",
	"Asia/Novosibirsk": "(GMT+07:00) Asia/Novosibirsk",
	"Asia/Omsk": "(GMT+07:00) Asia/Omsk",
	"Asia/Phnom_Penh": "(GMT+07:00) Asia/Phnom_Penh",
	"Asia/Saigon": "(GMT+07:00) Asia/Saigon",
	"Asia/Vientiane": "(GMT+07:00) Asia/Vientiane",
	"Indian/Christmas": "(GMT+07:00) Indian/Christmas",
	"Antarctica/Casey": "(GMT+08:00) Antarctica/Casey",
	"Asia/Brunei": "(GMT+08:00) Asia/Brunei",
	"Asia/Chongqing": "(GMT+08:00) Asia/Chongqing",
	"Asia/Chungking": "(GMT+08:00) Asia/Chungking",
	"Asia/Harbin": "(GMT+08:00) Asia/Harbin",
	"Asia/Hong_Kong": "(GMT+08:00) Asia/Hong_Kong",
	"Asia/Kashgar": "(GMT+08:00) Asia/Kashgar",
	"Asia/Krasnoyarsk": "(GMT+08:00) Asia/Krasnoyarsk",
	"Asia/Kuala_Lumpur": "(GMT+08:00) Asia/Kuala_Lumpur",
	"Asia/Kuching": "(GMT+08:00) Asia/Kuching",
	"Asia/Macao": "(GMT+08:00) Asia/Macao",
	"Asia/Macau": "(GMT+08:00) Asia/Macau",
	"Asia/Manila": "(GMT+08:00) Asia/Manila",
	"Asia/Shanghai": "(GMT+08:00) Asia/Shanghai",
	"Asia/Singapore": "(GMT+08:00) Asia/Singapore",
	"Asia/Taipei": "(GMT+08:00) Asia/Taipei",
	"Asia/Ulaanbaatar": "(GMT+08:00) Asia/Ulaanbaatar",
	"Asia/Ulan_Bator": "(GMT+08:00) Asia/Ulan_Bator",
	"Asia/Urumqi": "(GMT+08:00) Asia/Urumqi",
	"Australia/Perth": "(GMT+08:00) Australia/Perth",
	"Hongkong": "(GMT+08:00) Hongkong",
	"PRC": "(GMT+08:00) PRC",
	"Singapore": "(GMT+08:00) Singapore",
	"Asia/Irkutsk": "(GMT+09:00) Asia/Irkutsk",
	"Asia/Pyongyang": "(GMT+09:00) Asia/Pyongyang",
	"Asia/Seoul": "(GMT+09:00) Asia/Seoul",
	"Asia/Tokyo": "(GMT+09:00) Asia/Tokyo",
	"Japan": "(GMT+09:00) Japan",
	"Pacific/Palau": "(GMT+09:00) Pacific/Palau",
	"ROK": "(GMT+09:00) ROK",
	"Australia/Adelaide": "(GMT+09:30) Australia/Adelaide",
	"Australia/Broken_Hill": "(GMT+09:30) Australia/Broken_Hill",
	"Australia/Darwin": "(GMT+09:30) Australia/Darwin",
	"Australia/North": "(GMT+09:30) Australia/North",
	"Australia/South": "(GMT+09:30) Australia/South",
	"Australia/Yancowinna": "(GMT+09:30) Australia/Yancowinna",
	"Antarctica/DumontDUrville": "(GMT+10:00) Antarctica/DumontDUrville",
	"Asia/Yakutsk": "(GMT+10:00) Asia/Yakutsk",
	"Australia/ACT": "(GMT+10:00) Australia/ACT",
	"Australia/Brisbane": "(GMT+10:00) Australia/Brisbane",
	"Australia/Canberra": "(GMT+10:00) Australia/Canberra",
	"Australia/Currie": "(GMT+10:00) Australia/Currie",
	"Australia/Hobart": "(GMT+10:00) Australia/Hobart",
	"Australia/Lindeman": "(GMT+10:00) Australia/Lindeman",
	"Australia/Melbourne": "(GMT+10:00) Australia/Melbourne",
	"Australia/NSW": "(GMT+10:00) Australia/NSW",
	"Australia/Queensland": "(GMT+10:00) Australia/Queensland",
	"Australia/Sydney": "(GMT+10:00) Australia/Sydney",
	"Australia/Tasmania": "(GMT+10:00) Australia/Tasmania",
	"Australia/Victoria": "(GMT+10:00) Australia/Victoria",
	"Pacific/Port_Moresby": "(GMT+10:00) Pacific/Port_Moresby",
	"Pacific/Truk": "(GMT+10:00) Pacific/Truk",
	"Pacific/Yap": "(GMT+10:00) Pacific/Yap",
	"Australia/LHI": "(GMT+10:30) Australia/LHI",
	"Australia/Lord_Howe": "(GMT+10:30) Australia/Lord_Howe",
	"Asia/Vladivostok": "(GMT+11:00) Asia/Vladivostok",
	"Pacific/Efate": "(GMT+11:00) Pacific/Efate",
	"Pacific/Kosrae": "(GMT+11:00) Pacific/Kosrae",
	"Pacific/Ponape": "(GMT+11:00) Pacific/Ponape",
	"Pacific/Norfolk": "(GMT+11:30) Pacific/Norfolk",
	"Antarctica/McMurdo": "(GMT+12:00) Antarctica/McMurdo",
	"Antarctica/South_Pole": "(GMT+12:00) Antarctica/South_Pole",
	"Asia/Anadyr": "(GMT+12:00) Asia/Anadyr",
	"Asia/Kamchatka": "(GMT+12:00) Asia/Kamchatka",
	"Asia/Magadan": "(GMT+12:00) Asia/Magadan",
	"Kwajalein": "(GMT+12:00) Kwajalein",
	"NZ": "(GMT+12:00) NZ",
	"Pacific/Auckland": "(GMT+12:00) Pacific/Auckland",
	"Pacific/Fiji": "(GMT+12:00) Pacific/Fiji",
	"Pacific/Funafuti": "(GMT+12:00) Pacific/Funafuti",
	"Pacific/Kwajalein": "(GMT+12:00) Pacific/Kwajalein",
	"Pacific/Majuro": "(GMT+12:00) Pacific/Majuro",
	"Pacific/Tarawa": "(GMT+12:00) Pacific/Tarawa",
	"Pacific/Wake": "(GMT+12:00) Pacific/Wake",
	"Pacific/Wallis": "(GMT+12:00) Pacific/Wallis",
	"NZ-CHAT": "(GMT+12:45) NZ-CHAT",
	"Pacific/Chatham": "(GMT+12:45) Pacific/Chatham"
};
Aerohive.TIMEZONE_COUNTRY = {
	"Canada": {
		"text": "Canada",
		"zones": ["America/Atikokan","America/Blanc-Sablon","America/Cambridge_Bay","America/Creston","America/Dawson","America/Dawson_Creek","America/Edmonton","America/Glace_Bay","America/Goose_Bay","America/Halifax","America/Inuvik","America/Iqaluit","America/Moncton","America/Montreal","America/Nipigon","America/Pangnirtung","America/Rainy_River","America/Rankin_Inlet","America/Regina","America/Resolute","America/St_Johns","America/Swift_Current","America/Thunder_Bay","America/Toronto","America/Vancouver","America/Whitehorse","America/Winnipeg","America/Yellowknife"]
	},
	"Guinea-Bissau": {
		"text": "Guinea-Bissau",
		"zones": ["Africa/Bissau"]
	},
	"Lithuania": {
		"text": "Lithuania",
		"zones": ["Europe/Vilnius"]
	},
	"Cambodia": {
		"text": "Cambodia",
		"zones": ["Asia/Phnom_Penh"]
	},
	"Ethiopia": {
		"text": "Ethiopia",
		"zones": ["Africa/Addis_Ababa"]
	},
	"Aruba": {
		"text": "Aruba",
		"zones": ["America/Aruba"]
	},
	"Swaziland": {
		"text": "Swaziland",
		"zones": ["Africa/Mbabane"]
	},
	"Palestine": {
		"text": "Palestine",
		"zones": ["Asia/Gaza","Asia/Hebron"]
	},
	"Argentina": {
		"text": "Argentina",
		"zones": ["America/Argentina/Buenos_Aires","America/Argentina/Catamarca","America/Argentina/Cordoba","America/Argentina/Jujuy","America/Argentina/La_Rioja","America/Argentina/Mendoza","America/Argentina/Rio_Gallegos","America/Argentina/Salta","America/Argentina/San_Juan","America/Argentina/Tucuman","America/Argentina/Ushuaia"]
	},
	"Bolivia": {
		"text": "Bolivia",
		"zones": ["America/La_Paz"]
	},
	"Cameroon": {
		"text": "Cameroon",
		"zones": ["Africa/Douala"]
	},
	"Burkina Faso": {
		"text": "Burkina Faso",
		"zones": ["Africa/Ouagadougou"]
	},
	"Turkmenistan": {
		"text": "Turkmenistan",
		"zones": ["Asia/Ashgabat"]
	},
	"Ghana": {
		"text": "Ghana",
		"zones": ["Africa/Accra"]
	},
	"Korea (North)": {
		"text": "Korea (North)",
		"zones": ["Asia/Pyongyang"]
	},
	"Saudi Arabia": {
		"text": "Saudi Arabia",
		"zones": ["Asia/Riyadh"]
	},
	"Japan": {
		"text": "Japan",
		"zones": ["Asia/Tokyo"]
	},
	"Wallis & Futuna": {
		"text": "Wallis & Futuna",
		"zones": ["Pacific/Wallis"]
	},
	"Cocos (Keeling) Islands": {
		"text": "Cocos (Keeling) Islands",
		"zones": ["Indian/Cocos"]
	},
	"Pitcairn": {
		"text": "Pitcairn",
		"zones": ["Pacific/Pitcairn"]
	},
	"Guatemala": {
		"text": "Guatemala",
		"zones": ["America/Guatemala"]
	},
	"Kuwait": {
		"text": "Kuwait",
		"zones": ["Asia/Kuwait"]
	},
	"Jordan": {
		"text": "Jordan",
		"zones": ["Asia/Amman"]
	},
	"Dominica": {
		"text": "Dominica",
		"zones": ["America/Dominica"]
	},
	"Liberia": {
		"text": "Liberia",
		"zones": ["Africa/Monrovia"]
	},
	"St Vincent": {
		"text": "St Vincent",
		"zones": ["America/St_Vincent"]
	},
	"Maldives": {
		"text": "Maldives",
		"zones": ["Indian/Maldives"]
	},
	"Jamaica": {
		"text": "Jamaica",
		"zones": ["America/Jamaica"]
	},
	"Trinidad & Tobago": {
		"text": "Trinidad & Tobago",
		"zones": ["America/Port_of_Spain"]
	},
	"Tanzania": {
		"text": "Tanzania",
		"zones": ["Africa/Dar_es_Salaam"]
	},
	"Martinique": {
		"text": "Martinique",
		"zones": ["America/Martinique"]
	},
	"Aaland Islands": {
		"text": "Aaland Islands",
		"zones": ["Europe/Mariehamn"]
	},
	"Albania": {
		"text": "Albania",
		"zones": ["Europe/Tirane"]
	},
	"Gabon": {
		"text": "Gabon",
		"zones": ["Africa/Libreville"]
	},
	"Niue": {
		"text": "Niue",
		"zones": ["Pacific/Niue"]
	},
	"Monaco": {
		"text": "Monaco",
		"zones": ["Europe/Monaco"]
	},
	"New Zealand": {
		"text": "New Zealand",
		"zones": ["Pacific/Auckland","Pacific/Chatham"]
	},
	"Yemen": {
		"text": "Yemen",
		"zones": ["Asia/Aden"]
	},
	"Britain (UK)": {
		"text": "Britain (UK)",
		"zones": ["Europe/London"]
	},
	"Jersey": {
		"text": "Jersey",
		"zones": ["Europe/Jersey"]
	},
	"Bahamas": {
		"text": "Bahamas",
		"zones": ["America/Nassau"]
	},
	"Greenland": {
		"text": "Greenland",
		"zones": ["America/Danmarkshavn","America/Godthab","America/Scoresbysund","America/Thule"]
	},
	"French Southern & Antarctic Lands": {
		"text": "French Southern & Antarctic Lands",
		"zones": ["Indian/Kerguelen"]
	},
	"Macau": {
		"text": "Macau",
		"zones": ["Asia/Macau"]
	},
	"Norfolk Island": {
		"text": "Norfolk Island",
		"zones": ["Pacific/Norfolk"]
	},
	"India": {
		"text": "India",
		"zones": ["Asia/Kolkata"]
	},
	"Azerbaijan": {
		"text": "Azerbaijan",
		"zones": ["Asia/Baku"]
	},
	"Lesotho": {
		"text": "Lesotho",
		"zones": ["Africa/Maseru"]
	},
	"Iraq": {
		"text": "Iraq",
		"zones": ["Asia/Baghdad"]
	},
	"Kenya": {
		"text": "Kenya",
		"zones": ["Africa/Nairobi"]
	},
	"Tajikistan": {
		"text": "Tajikistan",
		"zones": ["Asia/Dushanbe"]
	},
	"Turkey": {
		"text": "Turkey",
		"zones": ["Europe/Istanbul"]
	},
	"Afghanistan": {
		"text": "Afghanistan",
		"zones": ["Asia/Kabul"]
	},
	"Bangladesh": {
		"text": "Bangladesh",
		"zones": ["Asia/Dhaka"]
	},
	"Mauritania": {
		"text": "Mauritania",
		"zones": ["Africa/Nouakchott"]
	},
	"Palau": {
		"text": "Palau",
		"zones": ["Pacific/Palau"]
	},
	"San Marino": {
		"text": "San Marino",
		"zones": ["Europe/San_Marino"]
	},
	"Mongolia": {
		"text": "Mongolia",
		"zones": ["Asia/Ulaanbaatar"]
	},
	"France": {
		"text": "France",
		"zones": ["Europe/Paris"]
	},
	"Turks & Caicos Is": {
		"text": "Turks & Caicos Is",
		"zones": ["America/Grand_Turk"]
	},
	"Bermuda": {
		"text": "Bermuda",
		"zones": ["Atlantic/Bermuda"]
	},
	"Namibia": {
		"text": "Namibia",
		"zones": ["Africa/Windhoek"]
	},
	"Somalia": {
		"text": "Somalia",
		"zones": ["Africa/Mogadishu"]
	},
	"Laos": {
		"text": "Laos",
		"zones": ["Asia/Vientiane"]
	},
	"Seychelles": {
		"text": "Seychelles",
		"zones": ["Indian/Mahe"]
	},
	"Norway": {
		"text": "Norway",
		"zones": ["Europe/Oslo"]
	},
	"Cote d'Ivoire": {
		"text": "Cote d'Ivoire",
		"zones": ["Africa/Abidjan"]
	},
	"Cook Islands": {
		"text": "Cook Islands",
		"zones": ["Pacific/Rarotonga"]
	},
	"Benin": {
		"text": "Benin",
		"zones": ["Africa/Porto-Novo"]
	},
	"Libya": {
		"text": "Libya",
		"zones": ["Africa/Tripoli"]
	},
	"Cuba": {
		"text": "Cuba",
		"zones": ["America/Havana"]
	},
	"Korea (South)": {
		"text": "Korea (South)",
		"zones": ["Asia/Seoul"]
	},
	"Montenegro": {
		"text": "Montenegro",
		"zones": ["Europe/Podgorica"]
	},
	"Togo": {
		"text": "Togo",
		"zones": ["Africa/Lome"]
	},
	"China": {
		"text": "China",
		"zones": ["Asia/Chongqing","Asia/Harbin","Asia/Kashgar","Asia/Shanghai","Asia/Urumqi"]
	},
	"Armenia": {
		"text": "Armenia",
		"zones": ["Asia/Yerevan"]
	},
	"Dominican Republic": {
		"text": "Dominican Republic",
		"zones": ["America/Santo_Domingo"]
	},
	"French Polynesia": {
		"text": "French Polynesia",
		"zones": ["Pacific/Gambier","Pacific/Marquesas","Pacific/Tahiti"]
	},
	"Ukraine": {
		"text": "Ukraine",
		"zones": ["Europe/Kiev","Europe/Simferopol","Europe/Uzhgorod","Europe/Zaporozhye"]
	},
	"Bahrain": {
		"text": "Bahrain",
		"zones": ["Asia/Bahrain"]
	},
	"Qatar": {
		"text": "Qatar",
		"zones": ["Asia/Qatar"]
	},
	"Western Sahara": {
		"text": "Western Sahara",
		"zones": ["Africa/El_Aaiun"]
	},
	"Finland": {
		"text": "Finland",
		"zones": ["Europe/Helsinki"]
	},
	"Virgin Islands (UK)": {
		"text": "Virgin Islands (UK)",
		"zones": ["America/Tortola"]
	},
	"Mauritius": {
		"text": "Mauritius",
		"zones": ["Indian/Mauritius"]
	},
	"Liechtenstein": {
		"text": "Liechtenstein",
		"zones": ["Europe/Vaduz"]
	},
	"Belarus": {
		"text": "Belarus",
		"zones": ["Europe/Minsk"]
	},
	"Mali": {
		"text": "Mali",
		"zones": ["Africa/Bamako"]
	},
	"Vatican City": {
		"text": "Vatican City",
		"zones": ["Europe/Vatican"]
	},
	"Russia": {
		"text": "Russia",
		"zones": ["Asia/Anadyr","Asia/Irkutsk","Asia/Kamchatka","Asia/Krasnoyarsk","Asia/Magadan","Asia/Novokuznetsk","Asia/Novosibirsk","Asia/Omsk","Asia/Vladivostok","Asia/Yakutsk","Asia/Yekaterinburg","Europe/Kaliningrad","Europe/Moscow"]
	},
	"Bulgaria": {
		"text": "Bulgaria",
		"zones": ["Europe/Sofia"]
	},
	"United States": {
		"text": "United States",
		"zones": ["America/Anchorage","America/Boise","America/Chicago","America/Denver","America/Detroit","America/Indiana/Indianapolis","America/Indiana/Knox","America/Indiana/Marengo","America/Indiana/Petersburg","America/Indiana/Tell_City","America/Indiana/Vevay","America/Indiana/Vincennes","America/Indiana/Winamac","America/Juneau","America/Kentucky/Louisville","America/Kentucky/Monticello","America/Los_Angeles","America/Menominee","America/Metlakatla","America/New_York","America/Nome","America/North_Dakota/Beulah","America/North_Dakota/Center","America/North_Dakota/New_Salem","America/Phoenix","America/Shiprock","America/Sitka","America/Yakutat","Pacific/Honolulu"]
	},
	"Romania": {
		"text": "Romania",
		"zones": ["Europe/Bucharest"]
	},
	"Angola": {
		"text": "Angola",
		"zones": ["Africa/Luanda"]
	},
	"Cayman Islands": {
		"text": "Cayman Islands",
		"zones": ["America/Cayman"]
	},
	"South Africa": {
		"text": "South Africa",
		"zones": ["Africa/Johannesburg"]
	},
	"Cyprus": {
		"text": "Cyprus",
		"zones": ["Asia/Nicosia"]
	},
	"Sweden": {
		"text": "Sweden",
		"zones": ["Europe/Stockholm"]
	},
	"Peru": {
		"text": "Peru",
		"zones": ["America/Lima"]
	},
	"Antigua & Barbuda": {
		"text": "Antigua & Barbuda",
		"zones": ["America/Antigua"]
	},
	"Malaysia": {
		"text": "Malaysia",
		"zones": ["Asia/Kuala_Lumpur","Asia/Kuching"]
	},
	"Austria": {
		"text": "Austria",
		"zones": ["Europe/Vienna"]
	},
	"Vietnam": {
		"text": "Vietnam",
		"zones": ["Asia/Ho_Chi_Minh"]
	},
	"Uganda": {
		"text": "Uganda",
		"zones": ["Africa/Kampala"]
	},
	"Hungary": {
		"text": "Hungary",
		"zones": ["Europe/Budapest"]
	},
	"Niger": {
		"text": "Niger",
		"zones": ["Africa/Niamey"]
	},
	"Brazil": {
		"text": "Brazil",
		"zones": ["America/Araguaina","America/Bahia","America/Belem","America/Boa_Vista","America/Campo_Grande","America/Cuiaba","America/Eirunepe","America/Fortaleza","America/Maceio","America/Manaus","America/Noronha","America/Porto_Velho","America/Recife","America/Rio_Branco","America/Santarem","America/Sao_Paulo"]
	},
	"Falkland Islands": {
		"text": "Falkland Islands",
		"zones": ["Atlantic/Stanley"]
	},
	"Faroe Islands": {
		"text": "Faroe Islands",
		"zones": ["Atlantic/Faroe"]
	},
	"Guinea": {
		"text": "Guinea",
		"zones": ["Africa/Conakry"]
	},
	"Panama": {
		"text": "Panama",
		"zones": ["America/Panama"]
	},
	"Costa Rica": {
		"text": "Costa Rica",
		"zones": ["America/Costa_Rica"]
	},
	"Luxembourg": {
		"text": "Luxembourg",
		"zones": ["Europe/Luxembourg"]
	},
	"Andorra": {
		"text": "Andorra",
		"zones": ["Europe/Andorra"]
	},
	"Chad": {
		"text": "Chad",
		"zones": ["Africa/Ndjamena"]
	},
	"Gibraltar": {
		"text": "Gibraltar",
		"zones": ["Europe/Gibraltar"]
	},
	"Ireland": {
		"text": "Ireland",
		"zones": ["Europe/Dublin"]
	},
	"Pakistan": {
		"text": "Pakistan",
		"zones": ["Asia/Karachi"]
	},
	"Italy": {
		"text": "Italy",
		"zones": ["Europe/Rome"]
	},
	"Nigeria": {
		"text": "Nigeria",
		"zones": ["Africa/Lagos"]
	},
	"Ecuador": {
		"text": "Ecuador",
		"zones": ["Pacific/Galapagos"]
	},
	"Czech Republic": {
		"text": "Czech Republic",
		"zones": ["Europe/Prague"]
	},
	"Brunei": {
		"text": "Brunei",
		"zones": ["Asia/Brunei"]
	},
	"Australia": {
		"text": "Australia",
		"zones": ["Australia/Adelaide","Australia/Brisbane","Australia/Broken_Hill","Australia/Currie","Australia/Darwin","Australia/Hobart","Australia/Lindeman","Australia/Lord_Howe","Australia/Melbourne","Australia/Perth","Australia/Sydney"]
	},
	"Algeria": {
		"text": "Algeria",
		"zones": ["Africa/Algiers"]
	},
	"Slovenia": {
		"text": "Slovenia",
		"zones": ["Europe/Ljubljana"]
	},
	"El Salvador": {
		"text": "El Salvador",
		"zones": ["America/El_Salvador"]
	},
	"Tuvalu": {
		"text": "Tuvalu",
		"zones": ["Pacific/Funafuti"]
	},
	"St Barthelemy": {
		"text": "St Barthelemy",
		"zones": ["America/St_Barthelemy"]
	},
	"Marshall Islands": {
		"text": "Marshall Islands",
		"zones": ["Pacific/Kwajalein","Pacific/Majuro"]
	},
	"Chile": {
		"text": "Chile",
		"zones": ["America/Santiago","Pacific/Easter"]
	},
	"Puerto Rico": {
		"text": "Puerto Rico",
		"zones": ["America/Puerto_Rico"]
	},
	"Belgium": {
		"text": "Belgium",
		"zones": ["Europe/Brussels"]
	},
	"Kiribati": {
		"text": "Kiribati",
		"zones": ["Pacific/Tarawa"]
	},
	"Haiti": {
		"text": "Haiti",
		"zones": ["America/Port-au-Prince"]
	},
	"Belize": {
		"text": "Belize",
		"zones": ["America/Belize"]
	},
	"Hong Kong": {
		"text": "Hong Kong",
		"zones": ["Asia/Hong_Kong"]
	},
	"Congo (Dem. Rep.)": {
		"text": "Congo (Dem. Rep.)",
		"zones": ["Africa/Kinshasa"]
	},
	"Georgia": {
		"text": "Georgia",
		"zones": ["Asia/Tbilisi"]
	},
	"Svalbard & Jan Mayen": {
		"text": "Svalbard & Jan Mayen",
		"zones": ["Arctic/Longyearbyen"]
	},
	"Gambia": {
		"text": "Gambia",
		"zones": ["Africa/Banjul"]
	},
	"Philippines": {
		"text": "Philippines",
		"zones": ["Asia/Manila"]
	},
	"Moldova": {
		"text": "Moldova",
		"zones": ["Europe/Chisinau"]
	},
	"Morocco": {
		"text": "Morocco",
		"zones": ["Africa/Casablanca"]
	},
	"Croatia": {
		"text": "Croatia",
		"zones": ["Europe/Zagreb"]
	},
	"Malta": {
		"text": "Malta",
		"zones": ["Europe/Malta"]
	},
	"Guernsey": {
		"text": "Guernsey",
		"zones": ["Europe/Guernsey"]
	},
	"Thailand": {
		"text": "Thailand",
		"zones": ["Asia/Bangkok"]
	},
	"Switzerland": {
		"text": "Switzerland",
		"zones": ["Europe/Zurich"]
	},
	"Grenada": {
		"text": "Grenada",
		"zones": ["America/Grenada"]
	},
	"Isle of Man": {
		"text": "Isle of Man",
		"zones": ["Europe/Isle_of_Man"]
	},
	"Portugal": {
		"text": "Portugal",
		"zones": ["Atlantic/Azores","Atlantic/Madeira","Europe/Lisbon"]
	},
	"Estonia": {
		"text": "Estonia",
		"zones": ["Europe/Tallinn"]
	},
	"Uruguay": {
		"text": "Uruguay",
		"zones": ["America/Montevideo"]
	},
	"Mexico": {
		"text": "Mexico",
		"zones": ["America/Cancun","America/Chihuahua","America/Hermosillo","America/Matamoros","America/Mazatlan","America/Merida","America/Mexico_City","America/Monterrey","America/Ojinaga","America/Santa_Isabel","America/Tijuana"]
	},
	"Lebanon": {
		"text": "Lebanon",
		"zones": ["Asia/Beirut"]
	},
	"St Pierre & Miquelon": {
		"text": "St Pierre & Miquelon",
		"zones": ["America/Miquelon"]
	},
	"Sierra Leone": {
		"text": "Sierra Leone",
		"zones": ["Africa/Freetown"]
	},
	"Uzbekistan": {
		"text": "Uzbekistan",
		"zones": ["Asia/Samarkand","Asia/Tashkent"]
	},
	"Tunisia": {
		"text": "Tunisia",
		"zones": ["Africa/Tunis"]
	},
	"Djibouti": {
		"text": "Djibouti",
		"zones": ["Africa/Djibouti"]
	},
	"Spain": {
		"text": "Spain",
		"zones": ["Africa/Ceuta","Atlantic/Canary","Europe/Madrid"]
	},
	"Colombia": {
		"text": "Colombia",
		"zones": ["America/Bogota"]
	},
	"Reunion": {
		"text": "Reunion",
		"zones": ["Indian/Reunion"]
	},
	"Slovakia": {
		"text": "Slovakia",
		"zones": ["Europe/Bratislava"]
	},
	"Taiwan": {
		"text": "Taiwan",
		"zones": ["Asia/Taipei"]
	},
	"Fiji": {
		"text": "Fiji",
		"zones": ["Pacific/Fiji"]
	},
	"Barbados": {
		"text": "Barbados",
		"zones": ["America/Barbados"]
	},
	"Virgin Islands (US)": {
		"text": "Virgin Islands (US)",
		"zones": ["America/St_Thomas"]
	},
	"Madagascar": {
		"text": "Madagascar",
		"zones": ["Indian/Antananarivo"]
	},
	"Congo (Rep.)": {
		"text": "Congo (Rep.)",
		"zones": ["Africa/Brazzaville"]
	},
	"Curacao": {
		"text": "Curacao",
		"zones": ["America/Curacao"]
	},
	"Bhutan": {
		"text": "Bhutan",
		"zones": ["Asia/Thimphu"]
	},
	"Sudan": {
		"text": "Sudan",
		"zones": ["Africa/Khartoum"]
	},
	"Nepal": {
		"text": "Nepal",
		"zones": ["Asia/Kathmandu"]
	},
	"St Martin (French part)": {
		"text": "St Martin (French part)",
		"zones": ["America/Marigot"]
	},
	"Micronesia": {
		"text": "Micronesia",
		"zones": ["Pacific/Kosrae"]
	},
	"St Kitts & Nevis": {
		"text": "St Kitts & Nevis",
		"zones": ["America/St_Kitts"]
	},
	"Bosnia & Herzegovina": {
		"text": "Bosnia & Herzegovina",
		"zones": ["Europe/Sarajevo"]
	},
	"Netherlands": {
		"text": "Netherlands",
		"zones": ["Europe/Amsterdam"]
	},
	"Anguilla": {
		"text": "Anguilla",
		"zones": ["America/Anguilla"]
	},
	"Venezuela": {
		"text": "Venezuela",
		"zones": ["America/Caracas"]
	},
	"Israel": {
		"text": "Israel",
		"zones": ["Asia/Jerusalem"]
	},
	"Myanmar (Burma)": {
		"text": "Myanmar (Burma)",
		"zones": ["Asia/Rangoon"]
	},
	"Central African Rep.": {
		"text": "Central African Rep.",
		"zones": ["Africa/Bangui"]
	},
	"Iceland": {
		"text": "Iceland",
		"zones": ["Atlantic/Reykjavik"]
	},
	"Senegal": {
		"text": "Senegal",
		"zones": ["Africa/Dakar"]
	},
	"Papua New Guinea": {
		"text": "Papua New Guinea",
		"zones": ["Pacific/Port_Moresby"]
	},
	"Sao Tome & Principe": {
		"text": "Sao Tome & Principe",
		"zones": ["Africa/Sao_Tome"]
	},
	"Germany": {
		"text": "Germany",
		"zones": ["Europe/Berlin"]
	},
	"Vanuatu": {
		"text": "Vanuatu",
		"zones": ["Pacific/Efate"]
	},
	"Denmark": {
		"text": "Denmark",
		"zones": ["Europe/Copenhagen"]
	},
	"Kazakhstan": {
		"text": "Kazakhstan",
		"zones": ["Asia/Almaty"]
	},
	"Poland": {
		"text": "Poland",
		"zones": ["Europe/Warsaw"]
	},
	"Eritrea": {
		"text": "Eritrea",
		"zones": ["Africa/Asmara"]
	},
	"Kyrgyzstan": {
		"text": "Kyrgyzstan",
		"zones": ["Asia/Bishkek"]
	},
	"St Helena": {
		"text": "St Helena",
		"zones": ["Atlantic/St_Helena"]
	},
	"Mayotte": {
		"text": "Mayotte",
		"zones": ["Indian/Mayotte"]
	},
	"British Indian Ocean Territory": {
		"text": "British Indian Ocean Territory",
		"zones": ["Indian/Chagos"]
	},
	"Montserrat": {
		"text": "Montserrat",
		"zones": ["America/Montserrat"]
	},
	"Macedonia": {
		"text": "Macedonia",
		"zones": ["Europe/Skopje"]
	},
	"St Lucia": {
		"text": "St Lucia",
		"zones": ["America/St_Lucia"]
	},
	"Paraguay": {
		"text": "Paraguay",
		"zones": ["America/Asuncion"]
	},
	"Latvia": {
		"text": "Latvia",
		"zones": ["Europe/Riga"]
	},
	"Guyana": {
		"text": "Guyana",
		"zones": ["America/Guyana"]
	},
	"Syria": {
		"text": "Syria",
		"zones": ["Asia/Damascus"]
	},
	"Guadeloupe": {
		"text": "Guadeloupe",
		"zones": ["America/Guadeloupe"]
	},
	"Honduras": {
		"text": "Honduras",
		"zones": ["America/Tegucigalpa"]
	},
	"Equatorial Guinea": {
		"text": "Equatorial Guinea",
		"zones": ["Africa/Malabo"]
	},
	"Egypt": {
		"text": "Egypt",
		"zones": ["Africa/Cairo"]
	},
	"Nicaragua": {
		"text": "Nicaragua",
		"zones": ["America/Managua"]
	},
	"Singapore": {
		"text": "Singapore",
		"zones": ["Asia/Singapore"]
	},
	"Serbia": {
		"text": "Serbia",
		"zones": ["Europe/Belgrade"]
	},
	"Comoros": {
		"text": "Comoros",
		"zones": ["Indian/Comoro"]
	},
	"Antarctica": {
		"text": "Antarctica",
		"zones": ["Antarctica/Casey","Antarctica/Davis","Antarctica/DumontDUrville","Antarctica/Mawson","Antarctica/McMurdo","Antarctica/Palmer","Antarctica/South_Pole"]
	},
	"Christmas Island": {
		"text": "Christmas Island",
		"zones": ["Indian/Christmas"]
	},
	"US minor outlying islands": {
		"text": "US minor outlying islands",
		"zones": ["Pacific/Johnston","Pacific/Midway","Pacific/Wake"]
	},
	"Greece": {
		"text": "Greece",
		"zones": ["Europe/Athens"]
	},
	"Sri Lanka": {
		"text": "Sri Lanka",
		"zones": ["Asia/Colombo"]
	},
	"French Guiana": {
		"text": "French Guiana",
		"zones": ["America/Cayenne"]
	},
	"Oman": {
		"text": "Oman",
		"zones": ["Asia/Muscat"]
	},
	"United Arab Emirates": {
		"text": "United Arab Emirates",
		"zones": ["Asia/Dubai"]
	},
	"Indonesia": {
		"text": "Indonesia",
		"zones": ["Asia/Jakarta"]
	},
	"Bonaire, St Eustatius & Saba": {
		"text": "Bonaire, St Eustatius & Saba",
		"zones": ["America/Kralendijk"]
	},
	"St Maarten (Dutch part)": {
		"text": "St Maarten (Dutch part)",
		"zones": ["America/Lower_Princes"]
	},
	"South Sudan": {
		"text": "South Sudan",
		"zones": ["Africa/Juba"]
	}
};
})();