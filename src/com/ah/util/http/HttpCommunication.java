package com.ah.util.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.ah.util.NameValuePair;

public class HttpCommunication {

	private final Log log = LogFactory.getLog("commonlog.HttpCommunication");
	
	private String url;

	private final String host;
	
	private int    port = 443;

	private boolean useHttpsFlag = true;

	private boolean enableProxyFlag;
	private String proxyHost;
	private int proxyPort;
	private String proxyUsername;
	private String proxyPassword;

	private Scheme sch;

	private boolean isTargetNeedAuth;
	private String targetUsername;
	private String targetPassword;

	private final DefaultHttpClient httpclient = new DefaultHttpClient();

	public HttpCommunication(String url) {
		this.url = url;

		useHttpsFlag = url.startsWith("https://");
		if(useHttpsFlag)
			port = 443;
		else
			port = 80;
		
		
		int i1 = url.indexOf("://");
		int i2 = url.indexOf('/', i1 + 3);

		String urlhost = i2 == -1 ? url.substring(i1 + 3) : url.substring(i1 + 3, i2);
		int i3 = urlhost.indexOf(":");
		if(-1 != i3) {
			host = urlhost.substring(0,i3);
			port = Integer.parseInt(urlhost.substring(i3+1));
		}
		else
			host = urlhost;
		log.debug("host=" + host);

		try {
			initScheme();
		} catch (Exception e) {
			log.error("initHttpsTrustSetting exception", e);
		}
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
	}

	private void initProxy() {
		if (enableProxyFlag) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

			if (proxyUsername != null) {
				httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
						new UsernamePasswordCredentials(proxyUsername, proxyPassword));
			}
		}
	}

	private void initAuthentication() {

	}

	private void initScheme() throws Exception {
		if (useHttpsFlag) {
			TrustManager easyTrustManager = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);

			SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams httpParams = new SyncBasicHttpParams();
			sf.createSocket(httpParams);

			sch = new Scheme("https", port, sf);
		} else {
			sch = new Scheme("http", port, PlainSocketFactory.getSocketFactory());
		}
	}

	public String getUrlString() {
		return url;
	}

	public void setUrlString(String urlString) {
		this.url = urlString;
	}

	public boolean isEnableProxyFlag() {
		return enableProxyFlag;
	}

	public void setEnableProxyFlag(boolean enableProxyFlag) {
		this.enableProxyFlag = enableProxyFlag;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getTargetUsername() {
		return targetUsername;
	}

	public void setTargetUsername(String targetUsername) {
		this.targetUsername = targetUsername;
	}

	public String getTargetPassword() {
		return targetPassword;
	}

	public void setTargetPassword(String targetPassword) {
		this.targetPassword = targetPassword;
	}
	
	public boolean testForConnecting() throws Exception {
		HttpGet httpget = new HttpGet(url);
		try {
			initProxy();
			initAuthentication();
			if (isTargetNeedAuth) {
				httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
						new UsernamePasswordCredentials(targetUsername, targetPassword));
			}
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			HttpContext localContext = new BasicHttpContext();
			HttpResponse response = httpclient.execute(httpget, localContext);
			
			AuthState proxyAuthState = (AuthState) localContext.getAttribute(ClientContext.PROXY_AUTH_STATE);
			log.info("Proxy auth scheme: " + proxyAuthState.getAuthScheme());
			log.info("Proxy auth credentials: " + proxyAuthState.getCredentials());

			AuthState targetAuthState = (AuthState) localContext.getAttribute(ClientContext.TARGET_AUTH_STATE);
			log.info("Target auth scheme: " + targetAuthState.getAuthScheme());
			log.info("Target auth credentials: " + targetAuthState.getCredentials());
			return response.getStatusLine().getStatusCode() == 200;
		} catch (Exception e) {
			throw e;
		} finally {
			httpget.abort();
		}
	}

	public byte[] sendAndReceive(final byte[] buffer) throws Exception {
		initProxy();
		initAuthentication();
		HttpPost httppost;
		if (isTargetNeedAuth) {
			httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
					new UsernamePasswordCredentials(targetUsername, targetPassword));
		}
		httppost = new HttpPost(url);

		ContentProducer cp = new ContentProducer() {
			public void writeTo(OutputStream outstream) throws IOException {
				outstream.write(buffer);
			}
		};
		HttpEntity reqEntity = new EntityTemplate(cp);
		httppost.setEntity(reqEntity);

		log.debug("executing request " + httppost.getRequestLine());
		HttpContext localContext = new BasicHttpContext();

		HttpResponse response = httpclient.execute(httppost, localContext);

		AuthState proxyAuthState = (AuthState) localContext.getAttribute(ClientContext.PROXY_AUTH_STATE);
		log.info("Proxy auth scheme: " + proxyAuthState.getAuthScheme());
		log.info("Proxy auth credentials: " + proxyAuthState.getCredentials());

		AuthState targetAuthState = (AuthState) localContext.getAttribute(ClientContext.TARGET_AUTH_STATE);
		log.info("Target auth scheme: " + targetAuthState.getAuthScheme());
		log.info("Target auth credentials: " + targetAuthState.getCredentials());

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception(response.getStatusLine().toString());
		}

		HttpEntity resEntity = response.getEntity();

		if (resEntity != null) {
			ByteBuffer recvBB = ByteBuffer.allocate(8192);
			InputStream in = resEntity.getContent();

			byte[] tmp = new byte[1024];

			int len;
			//int totalLen = 0;
			while ((len = in.read(tmp)) != -1) {
				recvBB.put(tmp, 0, len);
			//	totalLen += len;
			}

			recvBB.flip();

			byte[] rrr = new byte[recvBB.limit()];
			recvBB.get(rrr);
			return rrr;
		}
		return null;
	}

	public boolean isTargetNeedAuth() {
		return isTargetNeedAuth;
	}

	public void setTargetNeedAuth(boolean isTargetNeedAuth) {
		this.isTargetNeedAuth = isTargetNeedAuth;
	}

	public byte[] sendFileToLS(NameValuePair[] nvs, String fileName, File file) throws Exception {
		initProxy();
		initAuthentication();

		HttpPost httppost;
		if (isTargetNeedAuth) {
			httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
					new UsernamePasswordCredentials(targetUsername, targetPassword));
		}
		httppost = new HttpPost(url);

		FileBody bin = new FileBody(file);

		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		reqEntity.addPart(fileName, bin);
		for (NameValuePair nv : nvs) {
			reqEntity.addPart(nv.getName(), new StringBody(nv.getValue()));
		}

		httppost.setEntity(reqEntity);

		log.info("executing request " + httppost.getRequestLine());
		HttpContext localContext = new BasicHttpContext();

		HttpResponse response = httpclient.execute(httppost, localContext);

		AuthState proxyAuthState = (AuthState) localContext.getAttribute(ClientContext.PROXY_AUTH_STATE);
		log.info("Proxy auth scheme: " + proxyAuthState.getAuthScheme());
		log.info("Proxy auth credentials: " + proxyAuthState.getCredentials());

		AuthState targetAuthState = (AuthState) localContext.getAttribute(ClientContext.TARGET_AUTH_STATE);
		log.info("Target auth scheme: " + targetAuthState.getAuthScheme());
		log.info("Target auth credentials: " + targetAuthState.getCredentials());

		int statusCode = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();

		log.info("Response status code: " + statusCode);
		
		if (resEntity != null) {
			log.info("Response content length: " + resEntity.getContentLength());
			log.info("Chunked?: " + resEntity.isChunked());
			
			return EntityUtils.toByteArray(resEntity);

			/* - 
			ByteBuffer recvBB = ByteBuffer.allocate(8192);
			InputStream in = resEntity.getContent();

			byte[] tmp = new byte[1024];

			int len;
			int totalLen = 0;
			while ((len = in.read(tmp)) != -1) {
				recvBB.put(tmp, 0, len);
				totalLen += len;
			}

			recvBB.flip();

			byte[] rrr = new byte[recvBB.limit()];
			recvBB.get(rrr);
			return rrr;*/
		}else{
			log.error("no response entity found...");
			return null;
		}
	}
	
	public HttpResponse uploadFile(NameValuePair[] nvs, File file) throws Exception {
		initProxy();
		initAuthentication();

		HttpPost httppost;
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
					new UsernamePasswordCredentials(targetUsername, targetPassword));
		httppost = new HttpPost(url);
		
		FileBody bin = new FileBody(file);

		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		reqEntity.addPart("bin", bin);
		if(nvs != null) {
			for (NameValuePair nv : nvs) {
				reqEntity.addPart(nv.getName(), new StringBody(nv.getValue()));
			}
		}

		httppost.setEntity(reqEntity);

		log.debug("executing request " + httppost.getRequestLine());
		HttpContext localContext = new BasicHttpContext();

		HttpResponse response = httpclient.execute(httppost, localContext);

		AuthState proxyAuthState = (AuthState) localContext.getAttribute(ClientContext.PROXY_AUTH_STATE);
		log.debug("Proxy auth scheme: " + proxyAuthState.getAuthScheme());
		log.debug("Proxy auth credentials: " + proxyAuthState.getCredentials());

		AuthState targetAuthState = (AuthState) localContext.getAttribute(ClientContext.TARGET_AUTH_STATE);
		log.debug("Target auth scheme: " + targetAuthState.getAuthScheme());
		log.debug("Target auth credentials: " + targetAuthState.getCredentials());
		
		response.getEntity().getContent().close();
		
		return response;
	}
	
    public HttpEntity sendParams(final List<org.apache.http.NameValuePair> parameters)
            throws Exception {
        initProxy();
        initAuthentication();
        HttpPost httppost;
        if (isTargetNeedAuth) {
            if (useHttpsFlag) {
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
                        new UsernamePasswordCredentials(targetUsername, targetPassword));
            } else {
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
                        new UsernamePasswordCredentials(targetUsername, targetPassword));
            }
        }
        httppost = new HttpPost(url);

        HttpEntity reqEntity = new UrlEncodedFormEntity(parameters);
        httppost.setEntity(reqEntity);

        log.debug("executing request " + httppost.getRequestLine());
        HttpContext localContext = new BasicHttpContext();

        HttpResponse response = httpclient.execute(httppost, localContext);

        AuthState proxyAuthState = (AuthState) localContext
                .getAttribute(ClientContext.PROXY_AUTH_STATE);
        log.info("Proxy auth scheme: " + proxyAuthState.getAuthScheme());
        log.info("Proxy auth credentials: " + proxyAuthState.getCredentials());

        AuthState targetAuthState = (AuthState) localContext
                .getAttribute(ClientContext.TARGET_AUTH_STATE);
        log.info("Target auth scheme: " + targetAuthState.getAuthScheme());
        log.info("Target auth credentials: " + targetAuthState.getCredentials());

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception(response.getStatusLine().toString());
        }

		return response.getEntity();
    }
    
    public HttpEntity sendRequestByGet(List<NameValuePair> params) throws Exception {
        initProxy();
        initAuthentication();
        HttpGet httpget;
        if (isTargetNeedAuth) {
            if (useHttpsFlag) {
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
                        new UsernamePasswordCredentials(targetUsername, targetPassword));
            } else {
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port),
                        new UsernamePasswordCredentials(targetUsername, targetPassword));
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            NameValuePair pair = params.get(i);
            if(i == 0) {
                builder.append("?");
            } else {
                builder.append("&");
            }
            builder.append(pair.getName() + "=" + pair.getValue());
        }
        httpget = new HttpGet(url + builder.toString());

        log.debug("executing request " + httpget.getRequestLine());
        HttpContext localContext = new BasicHttpContext();

        HttpResponse response = httpclient.execute(httpget, localContext);

        AuthState proxyAuthState = (AuthState) localContext
                .getAttribute(ClientContext.PROXY_AUTH_STATE);
        log.info("Proxy auth scheme: " + proxyAuthState.getAuthScheme());
        log.info("Proxy auth credentials: " + proxyAuthState.getCredentials());

        AuthState targetAuthState = (AuthState) localContext
                .getAttribute(ClientContext.TARGET_AUTH_STATE);
        log.info("Target auth scheme: " + targetAuthState.getAuthScheme());
        log.info("Target auth credentials: " + targetAuthState.getCredentials());

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception(response.getStatusLine().toString());
        }

        HttpEntity resEntity = response.getEntity();

        return resEntity;

    }
}