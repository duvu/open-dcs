package com.vd5.dcs2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * @author beou on 10/1/18 01:09
 */
public class Config {
    private final Properties properties = new Properties();

    private boolean useEnvironmentVariables;

    public void load(String file) throws IOException {
        try {
            Properties mainProperties = new Properties();
            InputStream is = getInputStream(file);

            if (is != null) {
                mainProperties.load(getInputStream(file));
            } else {
                Log.info("Notable to load " + file);
            }

            InputStream is2 = getInputStream("default.conf");
            if (is2 != null) {
                properties.load(is2);
            } else {
                Log.info("Notable to load default.conf");
            }

            properties.putAll(mainProperties); // override defaults

            useEnvironmentVariables = Boolean.parseBoolean(System.getenv("CONFIG_USE_ENVIRONMENT_VARIABLES"))
                    || Boolean.parseBoolean(properties.getProperty("config.useEnvironmentVariables"));
        } catch (InvalidPropertiesFormatException e) {
            throw new RuntimeException("Configuration file is not a valid XML document", e);
        }
    }

    private InputStream getInputStream(String file) {
        Log.info("Loading file: " + file);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);;

        if (inputStream == null) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return inputStream;
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
