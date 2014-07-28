package com.ah.ws.rest.client.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.util.Tracer;
import com.ah.util.http.XTrustProvider;
import com.ah.ws.rest.client.auth.BasicAuthFilter;
import com.ah.ws.rest.models.ExceptionModel;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

public abstract class BaseUtils implements ModuleConstant {
	
	private static final Tracer log = new Tracer(BaseUtils.class.getSimpleName());

	private final static String SECRETKEY_FILE = "/etc/secretkey";

	private final static String REST_SECRETKEY_APPEND = ".rest.key";

	private final static String DEFAULT_SECRETKEY = "aerohive_default_secretkey";

	private final static String DEFAULT_SCHEME = "https://";

	public final static Map<String, String> REST_PATHS = new HashMap<String, String>();

	private static Map<String, ApacheHttpClient[]> httpClients = new HashMap<String, ApacheHttpClient[]>();

	private static Map<String, BasicAuthFilter[]> filters = new HashMap<String, BasicAuthFilter[]>();

	private static Properties keys = new Properties();

	private String secretkey = DEFAULT_SECRETKEY;

	private String id;

	private String module;

	private String key;

	private byte constructType;

	private String role = "";

	private byte[] skey;

	static {
		// Trust all certificates
		XTrustProvider.install();

		// Initialize all secrect keys
		InputStream fis = null;
		try {
			fis = new FileInputStream(SECRETKEY_FILE);

			keys.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}

		// Initialize all api base path
		REST_PATHS.put(CLIENT_MODULE_REDIRECTOR, NmsUtil.getRedirectorServiceURL());
		
		REST_PATHS.put(CLIENT_MODULE_PORTAL, NmsUtil.getPortalServiceURL());
		
		log.warn("init rest api, portal url is "+REST_PATHS.get(CLIENT_MODULE_PORTAL));

		String licserverUrl = DEFAULT_SCHEME + LicenseServerSetting.DEFAULT_LICENSE_SERVER_URL;
		LicenseServerSetting lserverInfo = HmBeActivationUtil.getLicenseServerInfo();
		if (lserverInfo != null)
			licserverUrl = DEFAULT_SCHEME + lserverInfo.getLserverUrl();

		REST_PATHS.put(CLIENT_MODULE_LICENSESERVER, licserverUrl);
	}

	public BaseUtils(String id, String module, byte constructType) {
		this.id = StringUtils.isBlank(id) ? "" : id;
		this.module = module;
		this.constructType = constructType;

		this.key = this.module + this.id;

		if (!httpClients.containsKey(this.key)) {
			ApacheHttpClient[] clients = new ApacheHttpClient[ALL];
			clients[constructType] = ApacheHttpClient.create();
			httpClients.put(this.key, clients);
		} else {
			ApacheHttpClient[] clients = httpClients.get(this.key);
			if (clients[constructType] == null)
				clients[constructType] = ApacheHttpClient.create();
		}

		String prefix = this.module + REST_SECRETKEY_APPEND;
		if (StringUtils.isBlank(this.id)) {
			secretkey = keys.getProperty(prefix, DEFAULT_SECRETKEY);
			if (constructType == NONE) {
				for (Object obj : keys.keySet()) {
					if (obj.toString().startsWith(prefix + ".")) {
						role = obj.toString().substring((prefix + ".").length());
						secretkey = keys.getProperty(obj.toString(), DEFAULT_SECRETKEY);
						break;
					}
				}
			}
		} else {
			secretkey = keys.getProperty(prefix + "." + this.id, DEFAULT_SECRETKEY);
		}
		if (!filters.containsKey(this.key)) {
			BasicAuthFilter[] fts = new BasicAuthFilter[ALL];
			fts[constructType] = new BasicAuthFilter("", secretkey);
			filters.put(this.key, fts);
		} else {
			BasicAuthFilter[] fts = filters.get(this.key);
			if (fts[constructType] == null)
				fts[constructType] = new BasicAuthFilter("", secretkey);
		}
	}

	public BaseUtils(String module, byte constructType) {
		this(null, module, constructType);
	}

	protected boolean isMatchType(byte constructType) {
		return this.constructType == constructType;
	}

	public WebResource getRawResource(String role, byte[] secretkey, String uri) {
		ApacheHttpClient client = ApacheHttpClient.create();
		BasicAuthFilter filter = new BasicAuthFilter(role, secretkey);
		client.addFilter(filter);

		return client.resource(getUri().path(uri).build());
	}

	protected ApacheHttpClient getHttpClient() {
		if (httpClients.containsKey(this.key)
				&& filters.containsKey(this.key)) {
			BasicAuthFilter cf = filters.get(this.key)[constructType];
			cf.generateAuthentication(role == null ? "" : role, skey == null ? secretkey.getBytes(BasicAuthFilter.CHARACTER_SET) : skey );

			ApacheHttpClient client = httpClients.get(this.key)[constructType];
			if (!client.isFilterPreset(cf))
				client.addFilter(cf);
//			client.addFilter(new LoggingFilter()); // for print out request and response data 
			
			// fix CFD-301 begin
			ApacheHttpClient apaHttpClient = httpClients.get(this.key)[constructType];
			apaHttpClient.setConnectTimeout(1000*60);
			apaHttpClient.setReadTimeout(1000*60);
			// fix CFD-301 end
			return apaHttpClient;
		}
		return null;
	}

	protected UriBuilder getUri() {
		return UriBuilder.fromUri(REST_PATHS.get(this.module));
	}

	protected String getRole() {
		return role;
	}

	protected void setRole(String role) {
		this.role = role;
	}

	protected byte[] getSkey() {
		return skey;
	}

	protected void setSkey(byte[] skey) {
		this.skey = skey;
	}

	protected ExceptionModel getException(ClientResponse cr) {
		return cr.getEntity(ExceptionModel.class);
	}
	
	/**
	 * Refresh portal url for communication after change
	 *
	 */
	public static void refreshPortalUrlForRestApi() {
		REST_PATHS.put(CLIENT_MODULE_PORTAL, NmsUtil.getPortalServiceURL());
		log.warn("refreshPortalUrlForRestApi, portal url is "+REST_PATHS.get(CLIENT_MODULE_PORTAL));
	}
}
