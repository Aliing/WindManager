package com.ah.be.ga;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.ah.util.Tracer;

public class GAConfigUtil {
    
    private static final Tracer LOG = new Tracer(GAConfigUtil.class.getSimpleName());
    private static final String CONFIG_PROPERTIES_PATH = System.getenv("HM_ROOT")
            + "/WEB-INF/classes/resources/ga.properties";
    
    private static PropertiesConfiguration configuration = null;
    
    private final static String BETA_PREFIX = "beta.";
    private final static String PRODUCT_PREFIX = "";

    static {
        try {
            configuration = new PropertiesConfiguration(CONFIG_PROPERTIES_PATH);
            configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            LOG.error("loadProperties", "Error when load GA configuration.", e);
        }
    }

    public static synchronized String getProperty(final String key) {
        //return (String) configuration.getProperty(key);
        return configuration.getString(key);
    }
    
    public static String getACCPURLRoot(boolean beta) {
        return getProperty((beta ? BETA_PREFIX : PRODUCT_PREFIX) + "ga.acpp.url.root");
    }
    
    public static String getACCPCheckServiceAPI(boolean beta) {
        return getProperty((beta ? BETA_PREFIX : PRODUCT_PREFIX) + "ga.acpp.check.service.api");
    }
    
    public static String getACCPWebAccessURL(boolean beta) {
        return getProperty((beta ? BETA_PREFIX : PRODUCT_PREFIX) + "ga.acpp.url.web.root");
    }
}
