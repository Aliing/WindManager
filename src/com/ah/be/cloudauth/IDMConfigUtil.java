package com.ah.be.cloudauth;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.ah.be.cloudauth.annotation.ConfigProp;
import com.ah.util.Tracer;

public class IDMConfigUtil {

    private static final Tracer LOG = new Tracer(IDMConfigUtil.class.getSimpleName());
    private static final String BETA_PREFIX = "beta.";
    private static final String CONFIG_PROPERTIES_PATH = System.getenv("HM_ROOT")
            + "/WEB-INF/classes/resources/idmConfig.properties";

    private static IDMConfig standarConfig;
    private static IDMConfig betaConfig;

    static {
        // static block
        loadProperties2();
    }

    public static IDMConfig getRadSecConfig(boolean useBetaConfig) {
        LOG.info("getRadSecConfig", "get the beta config: " + useBetaConfig);
        return useBetaConfig ? getBetaRadSecConfig() : getStandRadSecConfig();
    }

    /*----------------------private _method---------------------------------*/
    private static IDMConfig getStandRadSecConfig() {
        LOG.info("getStandRadSecConfig", "get the standard config: " + standarConfig);
        return standarConfig;
    }
    
    private static IDMConfig getBetaRadSecConfig() {
        LOG.info("getBetaRadSecConfig", "get the beta config: " + betaConfig);
        return betaConfig;
    }
    
    @Deprecated
    private static void loadProperties() {
        try(FileInputStream in = new FileInputStream(CONFIG_PROPERTIES_PATH)) {
            Properties properties = new Properties();
            properties.load(in);

            standarConfig = initConfig(properties, null);
            betaConfig = initConfig(properties, BETA_PREFIX);

        } catch (IOException | IllegalArgumentException | IllegalAccessException e) {
            LOG.error("loadProperties", "Error when load IDM configuration.", e);
        }
    }
    
    /**
     * Should we need to support auto reload?<br>
     * Then should we just access the property directly instead of set the value to bean;<br>
     * Or use to Watch Service API (new in JDK7, {@link http://docs.oracle.com/javase/tutorial/essential/io/notification.html});
     * Example as http://howtodoinjava.com/2012/10/10/auto-reload-of-configuration-when-any-change-happen/<br>
     * Or use the VFS API from Apache Commons ({@link http://commons.apache.org/proper/commons-vfs/}).
     * 
     * @author Yunzhi Lin
     * - Time: Apr 15, 2013 7:49:40 PM
     */
    private static void loadProperties2() {
        try {
            PropertiesConfiguration configuration = new PropertiesConfiguration(CONFIG_PROPERTIES_PATH);
            //configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
            
            // convert
            Properties properties = ConfigurationConverter.getProperties(configuration);
            
            standarConfig = initConfig(properties , null);
            betaConfig = initConfig(properties, BETA_PREFIX);
            
        } catch (IllegalArgumentException | IllegalAccessException | ConfigurationException e) {
            LOG.error("loadProperties", "Error when load IDM configuration.", e);
        }
    }

    private static IDMConfig initConfig(Properties properties, String prefix)
            throws IllegalAccessException {
        IDMConfig config = new IDMConfig();
        for (Field field : IDMConfig.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProp.class)) {
                field.setAccessible(true);
                ConfigProp notation = field.getAnnotation(ConfigProp.class);
                String key = notation.name();
                field.set(config, properties.getProperty(null == prefix ? key : prefix + key));
            }
        }
        return config;
    }
}
