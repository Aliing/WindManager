/**
 *@filename		UserRegInfoForLs.java
 *@version
 *@author		Fiona
 *@createtime	2011-4-9 PM 03:43:17
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "USER_REG_INFO_FOR_LS")
public class UserRegInfoForLs implements HmBo
{

	private static final long	serialVersionUID	= 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length=DEFAULT_DESCRIPTION_LENGTH)
	private String company;

	@Column(length=256)
	private String addressLine1;

	@Column(length=256)
	private String addressLine2;

	@Column(length=DEFAULT_STRING_LENGTH)
	private String country;

	@Column(length=DEFAULT_STRING_LENGTH)
	private String postalCode;

	@Column(length=DEFAULT_DESCRIPTION_LENGTH)
	private String name;

	@Column(length=DEFAULT_STRING_LENGTH)
	private String telephone;

	@Column(length=DEFAULT_DESCRIPTION_LENGTH)
	private String email;
	
	private boolean activeBySelf;
	
	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return company;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public Timestamp getVersion()
	{
		return version;
	}

	@Override
	public void setVersion(Timestamp version)
	{
		this.version = version;
	}

	public String getCompany()
	{
		return company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}

	public String getAddressLine1()
	{
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2()
	{
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTelephone()
	{
		return telephone;
	}

	public void setTelephone(String telephone)
	{
		this.telephone = telephone;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public boolean isActiveBySelf()
	{
		return activeBySelf;
	}

	public void setActiveBySelf(boolean activeBySelf)
	{
		this.activeBySelf = activeBySelf;
	}
	
public static final String USER_COUNTRY_0 = "<Please Select Country>";
	
	public static final String USER_COUNTRY_1 = "United States";
	
	public static final String USER_COUNTRY_LAST = "Other";
	
	public static String[] USER_COUNTRY = new String[]{
		"Andorra",
		"United Arab Emirates",
		"Afghanistan",
		"Antigua and Barbuda",
		"Anguilla",
		"Albania",
		"Armenia",
		"Netherlands Antilles",
		"Angola",
		"Antarctica",
		"Argentina",
		"American Samoa",
		"Austria",
		"Australia",
		"Aruba",
		"Azerbaijan",
		"Bosnia and Herzegovina",
		"Barbados",
		"Bangladesh",
		"Belgium",
		"Burkina Faso",
		"Bulgaria",
		"Bahrain",
		"Burundi",
		"Benin",
		"Bermuda",
		"Brunei Darrussalam",
		"Bolivia",
		"Brazil",
		"Bahamas",
		"Bhutan",
		"Bouvet Island",
		"Botswana",
		"Belarus",
		"Belize",
		"Canada",
		"Cocos (keeling) Islands",
		"Congo, Democratic People's Republic",
		"Central African Republic",
		"Congo, Republic of",
		"Switzerland",
		"Cote d'Ivoire",
		"Cook Islands",
		"Chile",
		"Cameroon",
		"China",
		"Colombia",
		"Costa Rica",
		"Serbia and Montenegro",
		"Cuba",
		"Cap Verde",
		"Christmas Island",
		"Cyprus Island",
		"Czech Republic",
		"Germany",
		"Djibouti",
		"Denmark",
		"Dominica",
		"Dominican Republic",
		"Algeria",
		"Ecuador",
		"Estonia",
		"Egypt",
		"Western Sahara",
		"Eritrea",
		"Spain",
		"Ethiopia",
		"Finland",
		"Fiji",
		"Falkland Islands (Malvina)",
		"Micronesia, Federal State of",
		"Faroe Islands",
		"France",
		"Gabon",
		"United Kingdom (GB)",
		"Grenada",
		"Georgia",
		"French Guiana",
		"Guernsey",
		"Ghana",
		"Gibraltar",
		"Greenland",
		"Gambia",
		"Guinea",
		"Guadeloupe",
		"Equatorial Guinea",
		"Greece",
		"South Georgia",
		"Guatemala",
		"Guam",
		"Guinea-Bissau",
		"Guyana",
		"Hong Kong",
		"Heard and McDonald Islands",
		"Honduras",
		"Croatia/Hrvatska",
		"Haiti",
		"Hungary",
		"Indonesia",
		"Ireland",
		"Israel",
		"Isle of Man",
		"India",
		"British Indian Ocean Territory",
		"Iraq",
		"Iran (Islamic Republic of)",
		"Iceland",
		"Italy",
		"Jersey",
		"Jamaica",
		"Jordan",
		"Japan",
		"Kenya",
		"Kyrgyzstan",
		"Kiribati",
		"Comoros",
		"Saint Kitts and Nevis",
		"Korea, Democratic People's Republic",
		"Korea, Republic of",
		"Kuwait",
		"Cayman Islands",
		"Kazakhstan",
		"Lao People's Democratic Republic",
		"Lebanon",
		"Saint Lucia",
		"Liechtenstein",
		"Sri Lanka",
		"Liberia",
		"Lesotho",
		"Lithuania",
		"Luxembourgh",
		"Latvia",
		"Libyan Arab Jamahiriya",
		"Morocco",
		"Monaco",
		"Moldova, Republic of",
		"Madagascar",
		"Marshall Islands",
		"Macedonia",
		"Mali",
		"Myanmar",
		"Mongolia",
		"Macau",
		"Northern Mariana Islands",
		"Martinique",
		"Mauritania",
		"Montserrat",
		"Malta",
		"Mauritius",
		"Maldives",
		"malawi",
		"Mexico",
		"Malaysia",
		"Mozambique",
		"Namibia",
		"New Caledonia",
		"Niger",
		"Norfolk Island",
		"Nigeria",
		"Nicaragua",
		"Netherlands",
		"Norway",
		"Nepal",
		"Nauru",
		"Niue",
		"New Zealand",
		"Oman",
		"Panama",
		"Peru",
		"French Polynesia",
		"papua New Guinea",
		"Phillipines",
		"Pakistan",
		"Poland",
		"St. Pierre and Miquelon",
		"Pitcairn Island",
		"Puerto Rico",
		"Palestinian Territories",
		"Portugal",
		"Palau",
		"Paraguay",
		"Qatar",
		"Reunion Island",
		"Romania",
		"Russian Federation",
		"Rwanda",
		"Saudi Arabia",
		"Solomon Islands",
		"Seychelles",
		"Sudan",
		"Sweden",
		"Singapore",
		"St. Helena",
		"Slovenia",
		"Svalbard and Jan Mayen Islands",
		"Slovak Republic",
		"Sierra Leone",
		"San Marino",
		"Senegal",
		"Somalia",
		"Suriname",
		"Sao Tome and Principe",
		"El Salvador",
		"Syrian Arab Republic",
		"Swaziland",
		"Turks and Caicos Islands",
		"Chad",
		"French Southern Territories",
		"Togo",
		"Thailand",
		"Tajikistan",
		"Tokelau",
		"Turkmenistan",
		"Tunisia",
		"Tonga",
		"East Timor",
		"Turkey",
		"Trinidad and Tobago",
		"Tuvalu",
		"Taiwan",
		"Tanzania",
		"Ukraine",
		"Uganda",
		"US Minor Outlying Islands",
		"Uruguay",
		"Uzbekistan",
		"Holy See (City Vatican State)",
		"Saint Vincent and the Grenadines",
		"Venezuela",
		"Virgin Islands (British)",
		"Virgin Islands (USA)",
		"Vietnam",
		"Vanuatu",
		"Wallis and Futuna Islands",
		"Western Samoa",
		"Yemen",
		"Mayotte",
		"Yugoslavia",
		"South Africa",
		"Zambia",
		"Zimbabwe"
	};

}