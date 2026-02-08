package com.konrad.movieseeker.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final String ENV_VAR_NAME = "TMDB_API_KEY";
    private static final String PROPERTY_FILE_NAME = "secrets.properties";
    private static final String PROPERTY_KEY_NAME = "tmdb.api.key";

    public static String getApiKey() {
        // 1. Try to get from Environment Variable (Priority)
        String apiKey = System.getenv(ENV_VAR_NAME);
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey;
        }

        // 2. Try to get from secrets.properties file (Fallback)
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + PROPERTY_FILE_NAME);
                return null;
            }

            Properties prop = new Properties();
            prop.load(input);
            apiKey = prop.getProperty(PROPERTY_KEY_NAME);

            if (apiKey != null && !apiKey.trim().isEmpty()) {
                return apiKey;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.err.println("API Key not found in Environment Variables or secrets.properties!");
        return null;
    }
}