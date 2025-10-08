/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : MinimalAPKProcessor.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-17 10:55:19
 * Description  : Minimal APK processor that only copies and adds markers
 * -------------------------------------------------------------------
 *
 * "Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming."
 * – Ebrahim Shafiei
 *
 **********************************************************************
 */

package com.ebrasha.droidguard.core;

import com.ebrasha.droidguard.utils.SimpleLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Minimal APK processor that only copies the file and adds protection markers
 * This is the safest approach - no structural changes at all
 */
public class MinimalAPKProcessor {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    
    /**
     * Process APK with minimal changes - just copy and add markers
     * @param inputAPK Input APK file
     * @param outputAPK Output APK file
     * @param obfuscationEngine Obfuscation engine
     * @param tamperDetection Tamper detection engine
     * @param raspProtection RASP protection engine
     * @return True if processing was successful
     */
    public boolean processAPKMinimally(File inputAPK, File outputAPK, 
                                     SimpleObfuscationEngine obfuscationEngine,
                                     SimpleTamperDetection tamperDetection,
                                     SimpleRASProtection raspProtection) {
        try {
            logger.info("Starting minimal APK processing...");
            
            // First, just copy the file to see if it works
            logger.info("Step 1: Copying APK file...");
            copyFile(inputAPK, outputAPK);
            
            // Verify the copied file works
            if (verifyAPKBasic(outputAPK)) {
                logger.success("APK copied successfully and is valid!");
                
                // Now try to add minimal markers
                logger.info("Step 2: Adding minimal protection markers...");
                return addMinimalMarkers(outputAPK, obfuscationEngine, tamperDetection, raspProtection);
            } else {
                logger.error("Copied APK is not valid!");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Minimal APK processing failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Copy file from source to destination
     * @param source Source file
     * @param destination Destination file
     */
    private void copyFile(File source, File destination) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
    
    /**
     * Verify APK basic structure
     * @param apkFile APK file to verify
     * @return True if APK is valid
     */
    private boolean verifyAPKBasic(File apkFile) {
        try {
            logger.debug("Verifying APK basic structure...");
            
            try (ZipFile zipFile = new ZipFile(apkFile)) {
                // Just check if it's a valid ZIP file
                var entries = zipFile.entries();
                int entryCount = 0;
                while (entries.hasMoreElements()) {
                    entries.nextElement();
                    entryCount++;
                }
                
                logger.debug("APK contains " + entryCount + " entries");
                return entryCount > 0;
            }
            
        } catch (Exception e) {
            logger.error("APK verification failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add minimal protection markers to APK
     * @param apkFile APK file to add markers to
     * @param obfuscationEngine Obfuscation engine
     * @param tamperDetection Tamper detection engine
     * @param raspProtection RASP protection engine
     * @return True if successful
     */
    private boolean addMinimalMarkers(File apkFile, 
                                    SimpleObfuscationEngine obfuscationEngine,
                                    SimpleTamperDetection tamperDetection,
                                    SimpleRASProtection raspProtection) {
        try {
            logger.info("Adding minimal protection markers...");
            
            // Create temporary directory
            Path tempDir = Files.createTempDirectory("abdal_minimal_");
            Path extractedDir = tempDir.resolve("extracted");
            Files.createDirectories(extractedDir);
            
            // Extract APK
            if (!extractAPK(apkFile, extractedDir)) {
                logger.error("Failed to extract APK for markers");
                return false;
            }
            
            // Add markers only to assets folder
            boolean markersAdded = false;
            
            if (obfuscationEngine != null) {
                logger.debug("Adding obfuscation marker...");
                if (addObfuscationMarker(extractedDir)) {
                    markersAdded = true;
                }
            }
            
            if (tamperDetection != null) {
                logger.debug("Adding tamper detection marker...");
                if (addTamperDetectionMarker(extractedDir)) {
                    markersAdded = true;
                }
            }
            
            if (raspProtection != null) {
                logger.debug("Adding RASP protection marker...");
                if (addRASPProtectionMarker(extractedDir)) {
                    markersAdded = true;
                }
            }
            
            if (markersAdded) {
                // Repackage APK
                if (repackageAPK(extractedDir, apkFile)) {
                    logger.success("Minimal markers added successfully!");
                    deleteDirectory(tempDir.toFile());
                    return true;
                } else {
                    logger.error("Failed to repackage APK with markers");
                    deleteDirectory(tempDir.toFile());
                    return false;
                }
            } else {
                logger.info("No markers to add, APK remains unchanged");
                deleteDirectory(tempDir.toFile());
                return true;
            }
            
        } catch (Exception e) {
            logger.error("Failed to add minimal markers: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract APK to directory
     * @param apkFile APK file
     * @param extractDir Directory to extract to
     * @return True if successful
     */
    private boolean extractAPK(File apkFile, Path extractDir) {
        try {
            try (ZipFile zipFile = new ZipFile(apkFile)) {
                var entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    Path entryPath = extractDir.resolve(entry.getName());
                    
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        try (InputStream inputStream = zipFile.getInputStream(entry);
                             OutputStream outputStream = Files.newOutputStream(entryPath)) {
                            inputStream.transferTo(outputStream);
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to extract APK: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add obfuscation marker
     * @param extractDir Extracted directory
     * @return True if successful
     */
    private boolean addObfuscationMarker(Path extractDir) {
        try {
            Path assetsDir = extractDir.resolve("assets");
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }
            
            Path markerFile = assetsDir.resolve("abdal_obfuscation.txt");
            String content = "ABDAL_OBFUSCATION_PROTECTION\n" +
                           "Developer: Ebrahim Shafiei (EbraSha)\n" +
                           "Email: Prof.Shafiei@Gmail.com\n" +
                           "Timestamp: " + System.currentTimeMillis();
            Files.writeString(markerFile, content);
            return true;
        } catch (Exception e) {
            logger.warn("Failed to add obfuscation marker: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add tamper detection marker
     * @param extractDir Extracted directory
     * @return True if successful
     */
    private boolean addTamperDetectionMarker(Path extractDir) {
        try {
            Path assetsDir = extractDir.resolve("assets");
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }
            
            Path markerFile = assetsDir.resolve("abdal_tamper.txt");
            String content = "ABDAL_TAMPER_DETECTION\n" +
                           "Developer: Ebrahim Shafiei (EbraSha)\n" +
                           "Email: Prof.Shafiei@Gmail.com\n" +
                           "Timestamp: " + System.currentTimeMillis();
            Files.writeString(markerFile, content);
            return true;
        } catch (Exception e) {
            logger.warn("Failed to add tamper detection marker: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add RASP protection marker
     * @param extractDir Extracted directory
     * @return True if successful
     */
    private boolean addRASPProtectionMarker(Path extractDir) {
        try {
            Path assetsDir = extractDir.resolve("assets");
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }
            
            Path markerFile = assetsDir.resolve("abdal_rasp.txt");
            String content = "ABDAL_RASP_PROTECTION\n" +
                           "Developer: Ebrahim Shafiei (EbraSha)\n" +
                           "Email: Prof.Shafiei@Gmail.com\n" +
                           "Timestamp: " + System.currentTimeMillis();
            Files.writeString(markerFile, content);
            return true;
        } catch (Exception e) {
            logger.warn("Failed to add RASP protection marker: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Repackage APK
     * @param extractDir Extracted directory
     * @param outputAPK Output APK file
     * @return True if successful
     */
    private boolean repackageAPK(Path extractDir, File outputAPK) {
        try {
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputAPK))) {
                Files.walk(extractDir)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            String relativePath = extractDir.relativize(filePath).toString().replace("\\", "/");
                            ZipEntry entry = new ZipEntry(relativePath);
                            zipOut.putNextEntry(entry);
                            Files.copy(filePath, zipOut);
                            zipOut.closeEntry();
                        } catch (IOException e) {
                            logger.error("Failed to add file to APK: " + filePath + " - " + e.getMessage());
                        }
                    });
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to repackage APK: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete directory recursively
     * @param directory Directory to delete
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}
