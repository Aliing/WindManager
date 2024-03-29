package com.ah.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JodaTimeZone {

	/* key: locale, value: zone */
	private static Map<String, String> m_hm_list = new LinkedHashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("Pacific/Apia", "-11:00");
			put("Pacific/Midway", "-11:00");
			put("Pacific/Niue", "-11:00");
			put("Pacific/Pago_Pago", "-11:00");
			put("America/Adak", "-10:00");
			put("HST", "-10:00");
			put("Pacific/Fakaofo", "-10:00");
			put("Pacific/Honolulu", "-10:00");
			put("Pacific/Johnston", "-10:00");
			put("Pacific/Rarotonga", "-10:00");
			put("Pacific/Tahiti", "-10:00");
			put("Pacific/Marquesas", "-09:30");
			put("America/Anchorage", "-09:00");
			put("America/Juneau", "-09:00");
			put("America/Nome", "-09:00");
			put("America/Yakutat", "-09:00");
			put("Pacific/Gambier", "-09:00");
			put("America/Dawson", "-08:00");
			put("America/Los_Angeles", "-08:00");
			put("America/Santa_Isabel", "-08:00");
			put("America/Tijuana", "-08:00");
			put("America/Vancouver", "-08:00");
			put("America/Whitehorse", "-08:00");
			put("PST8PDT", "-08:00");
			put("Pacific/Pitcairn", "-08:00");
			put("America/Boise", "-07:00");
			put("America/Cambridge_Bay", "-07:00");
			put("America/Chihuahua", "-07:00");
			put("America/Dawson_Creek", "-07:00");
			put("America/Denver", "-07:00");
			put("America/Edmonton", "-07:00");
			put("America/Hermosillo", "-07:00");
			put("America/Inuvik", "-07:00");
			put("America/Mazatlan", "-07:00");
			put("America/Ojinaga", "-07:00");
			put("America/Phoenix", "-07:00");
			put("America/Yellowknife", "-07:00");
			put("MST", "-07:00");
			put("MST7MDT", "-07:00");
			put("America/Bahia_Banderas", "-06:00");
			put("America/Belize", "-06:00");
			put("America/Cancun", "-06:00");
			put("America/Chicago", "-06:00");
			put("America/Costa_Rica", "-06:00");
			put("America/El_Salvador", "-06:00");
			put("America/Guatemala", "-06:00");
			put("America/Indiana/Knox", "-06:00");
			put("America/Indiana/Tell_City", "-06:00");
			put("America/Managua", "-06:00");
			put("America/Matamoros", "-06:00");
			put("America/Menominee", "-06:00");
			put("America/Merida", "-06:00");
			put("America/Mexico_City", "-06:00");
			put("America/Monterrey", "-06:00");
			put("America/North_Dakota/Center", "-06:00");
			put("America/North_Dakota/New_Salem", "-06:00");
			put("America/Rainy_River", "-06:00");
			put("America/Rankin_Inlet", "-06:00");
			put("America/Regina", "-06:00");
			put("America/Swift_Current", "-06:00");
			put("America/Tegucigalpa", "-06:00");
			put("America/Winnipeg", "-06:00");
			put("CST6CDT", "-06:00");
			put("Pacific/Easter", "-06:00");
			put("Pacific/Galapagos", "-06:00");
			put("America/Atikokan", "-05:00");
			put("America/Bogota", "-05:00");
			put("America/Cayman", "-05:00");
			put("America/Detroit", "-05:00");
			put("America/Grand_Turk", "-05:00");
			put("America/Guayaquil", "-05:00");
			put("America/Havana", "-05:00");
			put("America/Indiana/Indianapolis", "-05:00");
			put("America/Indiana/Marengo", "-05:00");
			put("America/Indiana/Petersburg", "-05:00");
			put("America/Indiana/Vevay", "-05:00");
			put("America/Indiana/Vincennes", "-05:00");
			put("America/Indiana/Winamac", "-05:00");
			put("America/Iqaluit", "-05:00");
			put("America/Jamaica", "-05:00");
			put("America/Kentucky/Louisville", "-05:00");
			put("America/Kentucky/Monticello", "-05:00");
			put("America/Lima", "-05:00");
			put("America/Montreal", "-05:00");
			put("America/Nassau", "-05:00");
			put("America/New_York", "-05:00");
			put("America/Nipigon", "-05:00");
			put("America/Panama", "-05:00");
			put("America/Pangnirtung", "-05:00");
			put("America/Port-au-Prince", "-05:00");
			put("America/Resolute", "-05:00");
			put("America/Thunder_Bay", "-05:00");
			put("America/Toronto", "-05:00");
			put("EST", "-05:00");
			put("EST5EDT", "-05:00");
			put("America/Caracas", "-04:30");
			put("America/Anguilla", "-04:00");
			put("America/Antigua", "-04:00");
			put("America/Argentina/San_Luis", "-03:00");
			put("America/Aruba", "-04:00");
			put("America/Asuncion", "-04:00");
			put("America/Barbados", "-04:00");
			put("America/Blanc-Sablon", "-04:00");
			put("America/Boa_Vista", "-04:00");
			put("America/Campo_Grande", "-04:00");
			put("America/Cuiaba", "-04:00");
			put("America/Curacao", "-04:00");
			put("America/Dominica", "-04:00");
			put("America/Eirunepe", "-04:00");
			put("America/Glace_Bay", "-04:00");
			put("America/Goose_Bay", "-04:00");
			put("America/Grenada", "-04:00");
			put("America/Guadeloupe", "-04:00");
			put("America/Guyana", "-04:00");
			put("America/Halifax", "-04:00");
			put("America/La_Paz", "-04:00");
			put("America/Manaus", "-04:00");
			put("America/Martinique", "-04:00");
			put("America/Moncton", "-04:00");
			put("America/Montserrat", "-04:00");
			put("America/Port_of_Spain", "-04:00");
			put("America/Porto_Velho", "-04:00");
			put("America/Puerto_Rico", "-04:00");
			put("America/Rio_Branco", "-04:00");
			put("America/Santiago", "-04:00");
			put("America/Santo_Domingo", "-04:00");
			put("America/St_Kitts", "-04:00");
			put("America/St_Lucia", "-04:00");
			put("America/St_Thomas", "-04:00");
			put("America/St_Vincent", "-04:00");
			put("America/Thule", "-04:00");
			put("America/Tortola", "-04:00");
			put("Antarctica/Palmer", "-04:00");
			put("Atlantic/Bermuda", "-04:00");
			put("Atlantic/Stanley", "-04:00");
			put("America/St_Johns", "-03:30");
			put("America/Araguaina", "-03:00");
			put("America/Argentina/Buenos_Aires", "-03:00");
			put("America/Argentina/Catamarca", "-03:00");
			put("America/Argentina/Cordoba", "-03:00");
			put("America/Argentina/Jujuy", "-03:00");
			put("America/Argentina/La_Rioja", "-03:00");
			put("America/Argentina/Mendoza", "-03:00");
			put("America/Argentina/Rio_Gallegos", "-03:00");
			put("America/Argentina/Salta", "-03:00");
			put("America/Argentina/San_Juan", "-03:00");
			put("America/Argentina/Tucuman", "-03:00");
			put("America/Argentina/Ushuaia", "-03:00");
			put("America/Bahia", "-03:00");
			put("America/Belem", "-03:00");
			put("America/Cayenne", "-03:00");
			put("America/Fortaleza", "-03:00");
			put("America/Godthab", "-03:00");
			put("America/Maceio", "-03:00");
			put("America/Miquelon", "-03:00");
			put("America/Montevideo", "-03:00");
			put("America/Paramaribo", "-03:00");
			put("America/Recife", "-03:00");
			put("America/Santarem", "-03:00");
			put("America/Sao_Paulo", "-03:00");
			put("Antarctica/Rothera", "-03:00");
			put("America/Noronha", "-02:00");
			put("Atlantic/South_Georgia", "-02:00");
			put("America/Scoresbysund", "-01:00");
			put("Atlantic/Azores", "-01:00");
			put("Atlantic/Cape_Verde", "-01:00");
			put("Africa/Abidjan", "+00:00");
			put("Africa/Accra", "+00:00");
			put("Africa/Bamako", "+00:00");
			put("Africa/Banjul", "+00:00");
			put("Africa/Bissau", "+00:00");
			put("Africa/Casablanca", "+00:00");
			put("Africa/Conakry", "+00:00");
			put("Africa/Dakar", "+00:00");
			put("Africa/El_Aaiun", "+00:00");
			put("Africa/Freetown", "+00:00");
			put("Africa/Lome", "+00:00");
			put("Africa/Monrovia", "+00:00");
			put("Africa/Nouakchott", "+00:00");
			put("Africa/Ouagadougou", "+00:00");
			put("Africa/Sao_Tome", "+00:00");
			put("America/Danmarkshavn", "+00:00");
			put("Atlantic/Canary", "+00:00");
			put("Atlantic/Faroe", "+00:00");
			put("Atlantic/Madeira", "+00:00");
			put("Atlantic/Reykjavik", "+00:00");
			put("Atlantic/St_Helena", "+00:00");
			put("Europe/Dublin", "+00:00");
			put("Europe/Lisbon", "+00:00");
			put("Europe/London", "+00:00");
			put("UTC", "+00:00");
			put("WET", "+00:00");
			put("Africa/Algiers", "+01:00");
			put("Africa/Bangui", "+01:00");
			put("Africa/Brazzaville", "+01:00");
			put("Africa/Ceuta", "+01:00");
			put("Africa/Douala", "+01:00");
			put("Africa/Kinshasa", "+01:00");
			put("Africa/Lagos", "+01:00");
			put("Africa/Libreville", "+01:00");
			put("Africa/Luanda", "+01:00");
			put("Africa/Malabo", "+01:00");
			put("Africa/Ndjamena", "+01:00");
			put("Africa/Niamey", "+01:00");
			put("Africa/Porto-Novo", "+01:00");
			put("Africa/Tunis", "+01:00");
			put("Africa/Windhoek", "+01:00");
			put("CET", "+01:00");
			put("Europe/Amsterdam", "+01:00");
			put("Europe/Andorra", "+01:00");
			put("Europe/Belgrade", "+01:00");
			put("Europe/Berlin", "+01:00");
			put("Europe/Brussels", "+01:00");
			put("Europe/Budapest", "+01:00");
			put("Europe/Copenhagen", "+01:00");
			put("Europe/Gibraltar", "+01:00");
			put("Europe/Luxembourg", "+01:00");
			put("Europe/Madrid", "+01:00");
			put("Europe/Malta", "+01:00");
			put("Europe/Monaco", "+01:00");
			put("Europe/Oslo", "+01:00");
			put("Europe/Paris", "+01:00");
			put("Europe/Prague", "+01:00");
			put("Europe/Rome", "+01:00");
			put("Europe/Stockholm", "+01:00");
			put("Europe/Tirane", "+01:00");
			put("Europe/Vaduz", "+01:00");
			put("Europe/Vienna", "+01:00");
			put("Europe/Warsaw", "+01:00");
			put("Europe/Zurich", "+01:00");
			put("MET", "+01:00");
			put("Africa/Blantyre", "+02:00");
			put("Africa/Bujumbura", "+02:00");
			put("Africa/Cairo", "+02:00");
			put("Africa/Gaborone", "+02:00");
			put("Africa/Harare", "+02:00");
			put("Africa/Johannesburg", "+02:00");
			put("Africa/Kigali", "+02:00");
			put("Africa/Lubumbashi", "+02:00");
			put("Africa/Lusaka", "+02:00");
			put("Africa/Maputo", "+02:00");
			put("Africa/Maseru", "+02:00");
			put("Africa/Mbabane", "+02:00");
			put("Africa/Tripoli", "+02:00");
			put("Asia/Amman", "+02:00");
			put("Asia/Beirut", "+02:00");
			put("Asia/Damascus", "+02:00");
			put("Asia/Gaza", "+02:00");
			put("Asia/Jerusalem", "+02:00");
			put("Asia/Nicosia", "+02:00");
			put("EET", "+02:00");
			put("Europe/Athens", "+02:00");
			put("Europe/Bucharest", "+02:00");
			put("Europe/Chisinau", "+02:00");
			put("Europe/Helsinki", "+02:00");
			put("Europe/Istanbul", "+02:00");
			put("Europe/Kaliningrad", "+02:00");
			put("Europe/Kiev", "+02:00");
			put("Europe/Minsk", "+02:00");
			put("Europe/Riga", "+02:00");
			put("Europe/Simferopol", "+02:00");
			put("Europe/Sofia", "+02:00");
			put("Europe/Tallinn", "+02:00");
			put("Europe/Uzhgorod", "+02:00");
			put("Europe/Vilnius", "+02:00");
			put("Europe/Zaporozhye", "+02:00");
			put("Africa/Addis_Ababa", "+03:00");
			put("Africa/Asmara", "+03:00");
			put("Africa/Dar_es_Salaam", "+03:00");
			put("Africa/Djibouti", "+03:00");
			put("Africa/Kampala", "+03:00");
			put("Africa/Khartoum", "+03:00");
			put("Africa/Mogadishu", "+03:00");
			put("Africa/Nairobi", "+03:00");
			put("Antarctica/Syowa", "+03:00");
			put("Asia/Aden", "+03:00");
			put("Asia/Baghdad", "+03:00");
			put("Asia/Bahrain", "+03:00");
			put("Asia/Kuwait", "+03:00");
			put("Asia/Qatar", "+03:00");
			put("Asia/Riyadh", "+03:00");
			put("Europe/Moscow", "+03:00");
			put("Europe/Samara", "+03:00");
			put("Europe/Volgograd", "+03:00");
			put("Indian/Antananarivo", "+03:00");
			put("Indian/Comoro", "+03:00");
			put("Indian/Mayotte", "+03:00");
			put("Asia/Tehran", "+03:30");
			put("Asia/Baku", "+04:00");
			put("Asia/Dubai", "+04:00");
			put("Asia/Muscat", "+04:00");
			put("Asia/Tbilisi", "+04:00");
			put("Asia/Yerevan", "+04:00");
			put("Indian/Mahe", "+04:00");
			put("Indian/Mauritius", "+04:00");
			put("Indian/Reunion", "+04:00");
			put("Asia/Kabul", "+04:30");
			put("Antarctica/Mawson", "+05:00");
			put("Asia/Aqtau", "+05:00");
			put("Asia/Aqtobe", "+05:00");
			put("Asia/Ashgabat", "+05:00");
			put("Asia/Dushanbe", "+05:00");
			put("Asia/Karachi", "+05:00");
			put("Asia/Oral", "+05:00");
			put("Asia/Samarkand", "+05:00");
			put("Asia/Tashkent", "+05:00");
			put("Asia/Yekaterinburg", "+05:00");
			put("Indian/Kerguelen", "+05:00");
			put("Indian/Maldives", "+05:00");
			put("Asia/Colombo", "+05:30");
			put("Asia/Kolkata", "+05:30");
			put("Asia/Kathmandu", "+05:45");
			put("Antarctica/Vostok", "+06:00");
			put("Asia/Almaty", "+06:00");
			put("Asia/Bishkek", "+06:00");
			put("Asia/Dhaka", "+06:00");
			put("Asia/Novokuznetsk", "+06:00");
			put("Asia/Novosibirsk", "+06:00");
			put("Asia/Omsk", "+06:00");
			put("Asia/Qyzylorda", "+06:00");
			put("Asia/Thimphu", "+06:00");
			put("Indian/Chagos", "+06:00");
			put("Asia/Rangoon", "+06:30");
			put("Indian/Cocos", "+06:30");
			put("Antarctica/Davis", "+07:00");
			put("Asia/Bangkok", "+07:00");
			put("Asia/Ho_Chi_Minh", "+07:00");
			put("Asia/Hovd", "+07:00");
			put("Asia/Jakarta", "+07:00");
			put("Asia/Krasnoyarsk", "+07:00");
			put("Asia/Phnom_Penh", "+07:00");
			put("Asia/Pontianak", "+07:00");
			put("Asia/Vientiane", "+07:00");
			put("Indian/Christmas", "+07:00");
			put("Antarctica/Casey", "+08:00");
			put("Asia/Brunei", "+08:00");
			put("Asia/Choibalsan", "+08:00");
			put("Asia/Chongqing", "+08:00");
			put("Asia/Harbin", "+08:00");
			put("Asia/Hong_Kong", "+08:00");
			put("Asia/Irkutsk", "+08:00");
			put("Asia/Kashgar", "+08:00");
			put("Asia/Kuala_Lumpur", "+08:00");
			put("Asia/Kuching", "+08:00");
			put("Asia/Macau", "+08:00");
			put("Asia/Makassar", "+08:00");
			put("Asia/Manila", "+08:00");
			put("Asia/Shanghai", "+08:00");
			put("Asia/Singapore", "+08:00");
			put("Asia/Taipei", "+08:00");
			put("Asia/Ulaanbaatar", "+08:00");
			put("Asia/Urumqi", "+08:00");
			put("Australia/Perth", "+08:00");
			put("Australia/Eucla", "+08:45");
			put("Asia/Dili", "+09:00");
			put("Asia/Jayapura", "+09:00");
			put("Asia/Pyongyang", "+09:00");
			put("Asia/Seoul", "+09:00");
			put("Asia/Tokyo", "+09:00");
			put("Asia/Yakutsk", "+09:00");
			put("Pacific/Palau", "+09:00");
			put("Australia/Adelaide", "+09:30");
			put("Australia/Broken_Hill", "+09:30");
			put("Australia/Darwin", "+09:30");
			put("Antarctica/DumontDUrville", "+10:00");
			put("Asia/Sakhalin", "+10:00");
			put("Asia/Vladivostok", "+10:00");
			put("Australia/Brisbane", "+10:00");
			put("Australia/Currie", "+10:00");
			put("Australia/Hobart", "+10:00");
			put("Australia/Lindeman", "+10:00");
			put("Australia/Melbourne", "+10:00");
			put("Australia/Sydney", "+10:00");
			put("Pacific/Chuuk", "+10:00");
			put("Pacific/Guam", "+10:00");
			put("Pacific/Port_Moresby", "+10:00");
			put("Pacific/Saipan", "+10:00");
			put("Australia/Lord_Howe", "+10:30");
			put("Antarctica/Macquarie", "+11:00");
			put("Asia/Anadyr", "+11:00");
			put("Asia/Kamchatka", "+11:00");
			put("Asia/Magadan", "+11:00");
			put("Pacific/Efate", "+11:00");
			put("Pacific/Guadalcanal", "+11:00");
			put("Pacific/Kosrae", "+11:00");
			put("Pacific/Noumea", "+11:00");
			put("Pacific/Pohnpei", "+11:00");
			put("Pacific/Norfolk", "+11:30");
			put("Antarctica/McMurdo", "+12:00");
			put("Pacific/Auckland", "+12:00");
			put("Pacific/Fiji", "+12:00");
			put("Pacific/Funafuti", "+12:00");
			put("Pacific/Kwajalein", "+12:00");
			put("Pacific/Majuro", "+12:00");
			put("Pacific/Nauru", "+12:00");
			put("Pacific/Tarawa", "+12:00");
			put("Pacific/Wake", "+12:00");
			put("Pacific/Wallis", "+12:00");
			put("Pacific/Chatham", "+12:45");
			put("Pacific/Enderbury", "+13:00");
			put("Pacific/Tongatapu", "+13:00");
			put("Pacific/Kiritimati", "+14:00");
		}
	};

	public static List<TextItem> getJodaTimeZoneList() {
		List<TextItem> list = new ArrayList<TextItem>(m_hm_list.size());
		for (String locale : m_hm_list.keySet()) {
			String zone = m_hm_list.get(locale);
			list.add(new TextItem(zone, "(" + zone + ") " + locale));
		}
		return list;
	}

	public static List<TextItem> getJodaTimeLocaleList() {
		List<TextItem> list = new ArrayList<TextItem>(m_hm_list.size());
		for (String locale : m_hm_list.keySet()) {
			String zone = m_hm_list.get(locale);
			list.add(new TextItem(locale, "(" + zone + ") " + locale));
		}
		return list;
	}
}
