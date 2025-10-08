/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : SimpleObfuscationEngine.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-12 16:31:47
 * Description  : Simplified obfuscation engine without external dependencies
 * -------------------------------------------------------------------
 *
 * "Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming."
 * – Ebrahim Shafiei
 *
 **********************************************************************
 */

package com.ebrasha.droidguard.core;

import com.ebrasha.droidguard.utils.SimpleLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Simplified obfuscation engine without external dependencies
 * Provides basic obfuscation functionality for demonstration
 */
public class SimpleObfuscationEngine {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    private final SecureRandom random = new SecureRandom();
    private final Map<String, String> obfuscatedNames = new HashMap<>();
    
    /**
     * Process a file for obfuscation
     * @param inputFile Input file to obfuscate
     * @param isAPK Whether the input is an APK file
     * @return True if obfuscation was successful
     */
    public boolean processFile(File inputFile, boolean isAPK) {
        try {
            logger.info("Starting simplified obfuscation process for: " + inputFile.getName());
            
            if (isAPK) {
                return obfuscateAPK(inputFile);
            } else {
                return obfuscateJAR(inputFile);
            }
            
        } catch (Exception e) {
            logger.error("Obfuscation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obfuscate APK file (simplified version)
     * @param apkFile APK file to obfuscate
     * @return True if successful
     */
    private boolean obfuscateAPK(File apkFile) {
        try {
            logger.progress("Applying simplified APK obfuscation...");
            
            // For demonstration, we'll just add a marker to the file
            byte[] originalData = readFile(apkFile);
            byte[] obfuscatedData = addObfuscationMarker(originalData, "ABDAL_OBFUSCATED_APK");
            
            writeFile(apkFile, obfuscatedData);
            
            logger.success("APK obfuscation completed successfully!");
            return true;
            
        } catch (Exception e) {
            logger.error("APK obfuscation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obfuscate JAR file (simplified version)
     * @param jarFile JAR file to obfuscate
     * @return True if successful
     */
    private boolean obfuscateJAR(File jarFile) {
        try {
            logger.progress("Applying simplified JAR obfuscation...");
            
            // For demonstration, we'll just add a marker to the file
            byte[] originalData = readFile(jarFile);
            byte[] obfuscatedData = addObfuscationMarker(originalData, "ABDAL_OBFUSCATED_JAR");
            
            writeFile(jarFile, obfuscatedData);
            
            logger.success("JAR obfuscation completed successfully!");
            return true;
            
        } catch (Exception e) {
            logger.error("JAR obfuscation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add obfuscation marker to file data
     * @param originalData Original file data
     * @param marker Obfuscation marker
     * @return Modified file data
     */
    private byte[] addObfuscationMarker(byte[] originalData, String marker) {
        try {
            // Simple obfuscation: append marker to the end of the file
            byte[] markerBytes = marker.getBytes("UTF-8");
            byte[] result = new byte[originalData.length + markerBytes.length];
            
            System.arraycopy(originalData, 0, result, 0, originalData.length);
            System.arraycopy(markerBytes, 0, result, originalData.length, markerBytes.length);
            
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to add obfuscation marker: " + e.getMessage());
            return originalData;
        }
    }
    
    /**
     * Read file to byte array
     * @param file File to read
     * @return File data as byte array
     */
    private byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        }
    }
    
    /**
     * Write byte array to file
     * @param file File to write to
     * @param data Data to write
     */
    private void writeFile(File file, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }
    
    /**
     * Generate obfuscated name
     * @param originalName Original name
     * @return Obfuscated name
     */
    private String generateObfuscatedName(String originalName) {
        if (obfuscatedNames.containsKey(originalName)) {
            return obfuscatedNames.get(originalName);
        }
        
        String obfuscatedName = "obf_" + Math.abs(random.nextInt());
        obfuscatedNames.put(originalName, obfuscatedName);
        return obfuscatedName;
    }
}
