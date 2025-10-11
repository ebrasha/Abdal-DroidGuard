/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : AndroidSDKConfig.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-10-10 19:30:00
 * Description  : Android SDK configuration manager for local and bundled SDK support
 * -------------------------------------------------------------------
 *
 * "Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming."
 * – Ebrahim Shafiei
 *
 **********************************************************************
 */

package com.ebrasha.droidguard.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Android SDK Configuration Manager
 * Handles local configuration and bundled SDK support
 */
public class AndroidSDKConfig {
    
    private static final String CONFIG_FILE = "android-sdk-config.properties";
    private static final String BUNDLED_SDK_DIR = "android-sdk";
    private static final String RELATIVE_SDK_DIR = "./android-sdk";
    private static final String PARENT_SDK_DIR = "../android-sdk";
    
    private final Properties config;
    private final SimpleLogger logger;
    private final String appDirectory;
    
    public AndroidSDKConfig() {
        this.config = new Properties();
        this.logger = SimpleLogger.getInstance();
        this.appDirectory = getApplicationDirectory();
        loadConfiguration();
    }
    
    /**
     * Get application directory
     */
    private String getApplicationDirectory() {
        try {
            // Try to get the directory where the JAR is located
            String jarPath = AndroidSDKConfig.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
            
            if (jarPath.endsWith(".jar")) {
                return new File(jarPath).getParent();
            } else {
                // Running from IDE or class files
                return System.getProperty("user.dir");
            }
        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }
    
    /**
     * Load configuration from properties file
     */
    private void loadConfiguration() {
        try {
            // Try to load from resources first
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (is != null) {
                    config.load(is);
                    logger.info("Loaded Android SDK configuration from resources");
                    return;
                }
            }
            
            // Try to load from application directory
            Path configPath = Paths.get(appDirectory, CONFIG_FILE);
            if (Files.exists(configPath)) {
                try (InputStream is = Files.newInputStream(configPath)) {
                    config.load(is);
                    logger.info("Loaded Android SDK configuration from: " + configPath);
                    return;
                }
            }
            
            logger.info("No Android SDK configuration file found, using defaults");
            
        } catch (Exception e) {
            logger.error("Failed to load Android SDK configuration: " + e.getMessage());
        }
    }
    
    /**
     * Get Android SDK path with priority order
     */
    public String getAndroidSDKPath() {
        // 1. Check explicit configuration
        String configuredPath = config.getProperty("android.sdk.path");
        if (configuredPath != null && !configuredPath.trim().isEmpty()) {
            String resolvedPath = resolvePath(configuredPath);
            if (resolvedPath != null && Files.exists(Paths.get(resolvedPath))) {
                logger.info("Using configured Android SDK path: " + resolvedPath);
                return resolvedPath;
            }
        }
        
        // 2. Check bundled SDK directories
        String[] bundledPaths = {
            Paths.get(appDirectory, BUNDLED_SDK_DIR).toString(),
            Paths.get(appDirectory, RELATIVE_SDK_DIR).toString(),
            Paths.get(appDirectory, PARENT_SDK_DIR).toString()
        };
        
        for (String bundledPath : bundledPaths) {
            if (Files.exists(Paths.get(bundledPath))) {
                logger.info("Using bundled Android SDK path: " + bundledPath);
                return bundledPath;
            }
        }
        
        // 3. Fallback to environment variables
        if (Boolean.parseBoolean(config.getProperty("android.fallback.to.env", "true"))) {
            String[] envVars = {"ANDROID_HOME", "ANDROID_SDK_ROOT", "ANDROID_SDK_HOME"};
            for (String envVar : envVars) {
                String envPath = System.getenv(envVar);
                if (envPath != null && Files.exists(Paths.get(envPath))) {
                    logger.info("Using environment variable " + envVar + ": " + envPath);
                    return envPath;
                }
            }
        }
        
        // 4. Check common default locations
        String[] defaultPaths = {
            System.getProperty("user.home") + "/AppData/Local/Android/Sdk", // Windows
            System.getProperty("user.home") + "/Library/Android/sdk", // macOS
            System.getProperty("user.home") + "/Android/Sdk", // Linux
            "/usr/local/android-sdk",
            "/opt/android-sdk"
        };
        
        for (String defaultPath : defaultPaths) {
            if (Files.exists(Paths.get(defaultPath))) {
                logger.info("Using default Android SDK path: " + defaultPath);
                return defaultPath;
            }
        }
        
        logger.warn("Android SDK not found in any configured or default locations");
        return null;
    }
    
    /**
     * Get build tools versions in order of preference
     */
    public List<String> getBuildToolsVersions() {
        String versions = config.getProperty("android.build.tools.versions", 
            "36.1.0,34.0.0,33.0.2,33.0.1,33.0.0,32.0.0,31.0.0,30.0.3");
        
        return Arrays.stream(versions.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }
    
    /**
     * Get specific tool path
     */
    public String getToolPath(String toolName) {
        // Check explicit configuration first
        String configuredPath = config.getProperty("android." + toolName + ".path");
        if (configuredPath != null && !configuredPath.trim().isEmpty()) {
            String resolvedPath = resolvePath(configuredPath);
            if (resolvedPath != null && Files.exists(Paths.get(resolvedPath))) {
                return resolvedPath;
            }
        }
        
        // Auto-detect from SDK path
        String sdkPath = getAndroidSDKPath();
        if (sdkPath == null) {
            return null;
        }
        
        List<String> versions = getBuildToolsVersions();
        for (String version : versions) {
            String toolPath = sdkPath + "/build-tools/" + version + "/" + toolName;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                if (toolName.equals("apksigner")) {
                    toolPath += ".bat";
                } else {
                    toolPath += ".exe";
                }
            }
            
            if (Files.exists(Paths.get(toolPath))) {
                return toolPath;
            }
        }
        
        return null;
    }
    
    /**
     * Resolve relative paths
     */
    private String resolvePath(String path) {
        if (path.startsWith("./") || path.startsWith("../")) {
            return Paths.get(appDirectory, path).normalize().toString();
        }
        return path;
    }
    
    /**
     * Check if verbose logging is enabled
     */
    public boolean isVerboseLoggingEnabled() {
        return Boolean.parseBoolean(config.getProperty("android.sdk.verbose.logging", "false"));
    }
    
    /**
     * Get all configuration as map
     */
    public Map<String, String> getAllConfig() {
        Map<String, String> configMap = new HashMap<>();
        for (String key : config.stringPropertyNames()) {
            configMap.put(key, config.getProperty(key));
        }
        return configMap;
    }
    
    /**
     * Create sample configuration file
     */
    public void createSampleConfig() {
        try {
            Path configPath = Paths.get(appDirectory, CONFIG_FILE);
            
            String sampleConfig = 
                "# Android SDK Configuration for Abdal DroidGuard\n" +
                "# Uncomment and modify the paths below as needed\n\n" +
                "# Primary Android SDK path\n" +
                "# android.sdk.path=C:\\\\Users\\\\YourName\\\\AppData\\\\Local\\\\Android\\\\Sdk\n" +
                "# android.sdk.path=./android-sdk\n\n" +
                "# Build tools version preference\n" +
                "android.build.tools.versions=36.1.0,34.0.0,33.0.2,33.0.1,33.0.0,32.0.0,31.0.0,30.0.3\n\n" +
                "# Specific tool paths (optional)\n" +
                "# android.apksigner.path=C:\\\\path\\\\to\\\\apksigner.bat\n" +
                "# android.zipalign.path=C:\\\\path\\\\to\\\\zipalign.exe\n\n" +
                "# Fallback to environment variables\n" +
                "android.fallback.to.env=true\n\n" +
                "# Verbose logging\n" +
                "android.sdk.verbose.logging=false\n";
            
            Files.write(configPath, sampleConfig.getBytes("UTF-8"));
            logger.info("Created sample configuration file: " + configPath);
            
        } catch (Exception e) {
            logger.error("Failed to create sample configuration: " + e.getMessage());
        }
    }
}
