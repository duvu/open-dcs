package com.vd5.config;

import com.vd5.dcs2.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author beou on 10/1/18 01:09
 */
public class Config {
    private final Properties2 properties = new Properties2();

    private boolean useEnvironmentVariables;

    public Config() throws IOException {
        properties.load("default.properties");
        useEnvironmentVariables = Boolean.parseBoolean(System.getenv("CONFIG_USE_ENVIRONMENT_VARIABLES"))
                || Boolean.parseBoolean(properties.getProperty("config.useEnvironmentVariables"));
    }

    public boolean hasKey(String key) {
        return useEnvironmentVariables && System.getenv().containsKey(getEnvironmentVariableName(key))
                || properties.containsKey(key);
    }

    public String getString(String key) {
        if (useEnvironmentVariables) {
            String value = System.getenv(getEnvironmentVariableName(key));
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return hasKey(key) ? getString(key) : defaultValue;
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    public int getInteger(String key, int defaultValue) {
        return hasKey(key) ? Integer.parseInt(getString(key)) : defaultValue;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return hasKey(key) ? Long.parseLong(getString(key)) : defaultValue;
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public double getDouble(String key, double defaultValue) {
        return hasKey(key) ? Double.parseDouble(getString(key)) : defaultValue;
    }

    public static String getEnvironmentVariableName(String key) {
        return key.replaceAll("\\.", "_").replaceAll("(\\p{Lu})", "_$1").toUpperCase();
    }

    public void setString(String key, String value) {
        properties.put(key, value);
    }

}
