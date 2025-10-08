/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : SimpleTamperDetection.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-13 18:52:33
 * Description  : Simplified tamper detection without external dependencies
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
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Simplified tamper detection without external dependencies
 * Provides basic integrity verification functionality
 */
public class SimpleTamperDetection {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    private final Map<String, String> fileHashes = new HashMap<>();
    
    /**
     * Add tamper detection to the application
     * @param inputFile Input application file
     * @return True if tamper detection was successfully added
     */
    public boolean addTamperDetection(File inputFile) {
        try {
            logger.info("Adding simplified tamper detection to: " + inputFile.getName());
            
            if (inputFile.getName().toLowerCase().endsWith(".apk")) {
                return addTamperDetectionToAPK(inputFile);
            } else if (inputFile.getName().toLowerCase().endsWith(".jar")) {
                return addTamperDetectionToJAR(inputFile);
            } else {
                logger.error("Unsupported file format for tamper detection");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Tamper detection setup failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add tamper detection to APK file
     * @param apkFile APK file
     * @return True if successful
     */
    private boolean addTamperDetectionToAPK(File apkFile) {
        try {
            logger.progress("Adding tamper detection to APK...");
            
            // Calculate file hash
            String fileHash = calculateFileHash(apkFile);
            fileHashes.put(apkFile.getName(), fileHash);
            
            // Add integrity marker
            byte[] originalData = readFile(apkFile);
            byte[] protectedData = addIntegrityMarker(originalData, fileHash, "ABDAL_TAMPER_PROTECTED_APK");
            
            writeFile(apkFile, protectedData);
            
            logger.success("APK tamper detection added successfully!");
            logger.debug("File hash: " + fileHash);
            return true;
            
        } catch (Exception e) {
            logger.error("APK tamper detection failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add tamper detection to JAR file
     * @param jarFile JAR file
     * @return True if successful
     */
    private boolean addTamperDetectionToJAR(File jarFile) {
        try {
            logger.progress("Adding tamper detection to JAR...");
            
            // Calculate file hash
            String fileHash = calculateFileHash(jarFile);
            fileHashes.put(jarFile.getName(), fileHash);
            
            // Add integrity marker
            byte[] originalData = readFile(jarFile);
            byte[] protectedData = addIntegrityMarker(originalData, fileHash, "ABDAL_TAMPER_PROTECTED_JAR");
            
            writeFile(jarFile, protectedData);
            
            logger.success("JAR tamper detection added successfully!");
            logger.debug("File hash: " + fileHash);
            return true;
            
        } catch (Exception e) {
            logger.error("JAR tamper detection failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calculate SHA-256 hash of a file
     * @param file File to calculate hash for
     * @return SHA-256 hash as hex string
     */
    private String calculateFileHash(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
    
    /**
     * Add integrity marker to file data
     * @param originalData Original file data
     * @param hash File hash
     * @param marker Protection marker
     * @return Modified file data
     */
    private byte[] addIntegrityMarker(byte[] originalData, String hash, String marker) {
        try {
            // Create integrity data
            String integrityData = marker + "|" + hash + "|" + System.currentTimeMillis();
            byte[] integrityBytes = integrityData.getBytes("UTF-8");
            
            // Append integrity data to file
            byte[] result = new byte[originalData.length + integrityBytes.length];
            System.arraycopy(originalData, 0, result, 0, originalData.length);
            System.arraycopy(integrityBytes, 0, result, originalData.length, integrityBytes.length);
            
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to add integrity marker: " + e.getMessage());
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
     * Verify file integrity
     * @param file File to verify
     * @return True if file integrity is valid
     */
    public boolean verifyIntegrity(File file) {
        try {
            String currentHash = calculateFileHash(file);
            String expectedHash = fileHashes.get(file.getName());
            
            if (expectedHash == null) {
                logger.warn("No expected hash found for file: " + file.getName());
                return false;
            }
            
            boolean isValid = currentHash.equals(expectedHash);
            if (!isValid) {
                logger.error("File integrity check failed for: " + file.getName());
                logger.debug("Expected: " + expectedHash);
                logger.debug("Current: " + currentHash);
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("Integrity verification failed: " + e.getMessage());
            return false;
        }
    }
}
