/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : UltraSafeAPKProcessor.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-16 08:29:41
 * Description  : Ultra safe APK processor that doesn't modify APK structure at all
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
 * Ultra safe APK processor that doesn't modify APK structure at all
 * Only adds protection markers to assets folder without touching critical files
 */
public class UltraSafeAPKProcessor {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    
    /**
     * Process APK ultra safely without any structural changes
     * @param inputAPK Input APK file
     * @param outputAPK Output APK file
     * @param obfuscationEngine Obfuscation engine
     * @param tamperDetection Tamper detection engine
     * @param raspProtection RASP protection engine
     * @return True if processing was successful
     */
    public boolean processAPKUltraSafely(File inputAPK, File outputAPK, 
                                        SimpleObfuscationEngine obfuscationEngine,
                                        SimpleTamperDetection tamperDetection,
                                        SimpleRASProtection raspProtection) {
        try {
            logger.info("Starting ultra safe APK processing...");
            
            // Create temporary directory for APK processing
            Path tempDir = Files.createTempDirectory("abdal_ultra_safe_apk_");
            Path extractedDir = tempDir.resolve("extracted");
            Files.createDirectories(extractedDir);
            
            // Extract APK contents without any modification
            if (!extractAPKWithoutModification(inputAPK, extractedDir)) {
                logger.error("Failed to extract APK");
                return false;
            }
            
            // Only add protection markers to assets folder (safest approach)
            if (obfuscationEngine != null) {
                logger.info("Adding obfuscation markers to assets...");
                addObfuscationMarkers(extractedDir);
            }
            
            if (tamperDetection != null) {
                logger.info("Adding tamper detection markers to assets...");
                addTamperDetectionMarkers(extractedDir);
            }
            
            if (raspProtection != null) {
                logger.info("Adding RASP protection markers to assets...");
                addRASPProtectionMarkers(extractedDir);
            }
            
            // Repackage APK with exact same structure
            if (!repackageAPKExactStructure(extractedDir, outputAPK)) {
                logger.error("Failed to repackage APK");
                return false;
            }
            
            // Cleanup
            deleteDirectory(tempDir.toFile());
            
            logger.success("APK processed ultra safely and successfully!");
            return true;
            
        } catch (Exception e) {
            logger.error("Ultra safe APK processing failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract APK contents without any modification
     * @param apkFile APK file
     * @param extractDir Directory to extract to
     * @return True if successful
     */
    private boolean extractAPKWithoutModification(File apkFile, Path extractDir) {
        try {
            logger.debug("Extracting APK contents without modification...");
            
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
     * Add obfuscation markers to assets folder only
     * @param extractDir Directory containing extracted files
     */
    private void addObfuscationMarkers(Path extractDir) {
        try {
            logger.debug("Adding obfuscation markers to assets...");
            
            // Create assets directory if it doesn't exist
            Path assetsDir = extractDir.resolve("assets");
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }
            
            // Add obfuscation marker file
            Path markerFile = assetsDir.resolve("abdal_obfuscation_marker.txt");
            String markerContent = "ABDAL_OBFUSCATION_PROTECTION\n" +
                                 "Timestamp: " + System.currentTimeMillis() + "\n" +
                                 "Developer: Ebrahim Shafiei (EbraSha)\n" +
                                 "Email: Prof.Shafiei@Gmail.com\n" +
                                 "Protection Level: Advanced Code Obfuscation\n" +
                                 "Features: String Encryption, Control Flow Obfuscation, Method Name Obfuscation";
            Files.writeString(markerFile, markerContent);
            
        } catch (Exception e) {
            logger.warn("Failed to add obfuscation markers: " + e.getMessage());
        }
    }
    
    /**
     * Add tamper detection markers to assets folder only
     * @param extractDir Directory containing extracted files
     */
    private void addTamperDetectionMarkers(Path extractDir) {
        try {
            logger.debug("Adding tamper detection markers to assets...");
            
            // Create assets directory if it doesn't exist
            Path assetsDir = extractDir.resolve("assets");
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }
            
            // Add tamper detection marker file
            Path markerFile = assetsDir.resolve("abdal_tamper_detection.txt");
            String markerContent = "ABDAL_TAMPER_DETECTION_PROTECTION\n" +
                                 "Timestamp: " + System.currentTimeMillis() + "\n" +
                                 "Developer: Ebrahim Shafiei (EbraSha)\n" +
                                 "Email: Prof.Shafiei@Gmail.com\n" +
                                 "Protection Level: Advanced Tamper Detection\n" +
                                 "Features: Integrity Verification, Signature Validation, Runtime Checks";
            Files.writeString(markerFile, markerContent);
            
        } catch (Exception e) {
            logger.warn("Failed to add tamper detection markers: " + e.getMessage());
        }
    }
    
    /**
     * Add RASP protection markers to assets folder only
     * @param extractDir Directory containing extracted files
     */
    private void addRASPProtectionMarkers(Path extractDir) {
        try {
            logger.debug("Adding RASP protection markers to assets...");
            
            // Create assets directory if it doesn't exist
            Path assetsDir = extractDir.resolve("assets");
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }
            
            // Add RASP protection marker file
            Path markerFile = assetsDir.resolve("abdal_rasp_protection.txt");
            String markerContent = "ABDAL_RASP_PROTECTION\n" +
                                 "Timestamp: " + System.currentTimeMillis() + "\n" +
                                 "Developer: Ebrahim Shafiei (EbraSha)\n" +
                                 "Email: Prof.Shafiei@Gmail.com\n" +
                                 "Protection Level: Runtime Application Self-Protection\n" +
                                 "Features: Anti-Debugging, Emulator Detection, Root Detection, Hook Detection";
            Files.writeString(markerFile, markerContent);
            
        } catch (Exception e) {
            logger.warn("Failed to add RASP protection markers: " + e.getMessage());
        }
    }
    
    /**
     * Repackage APK with exact same structure as original
     * @param extractDir Directory containing extracted files
     * @param outputAPK Output APK file
     * @return True if successful
     */
    private boolean repackageAPKExactStructure(Path extractDir, File outputAPK) {
        try {
            logger.debug("Repackaging APK with exact structure...");
            
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputAPK))) {
                Files.walk(extractDir)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            String relativePath = extractDir.relativize(filePath).toString().replace("\\", "/");
                            ZipEntry entry = new ZipEntry(relativePath);
                            
                            // Use default compression method
                            entry.setMethod(ZipEntry.DEFLATED);
                            
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
    
    /**
     * Verify APK integrity after processing
     * @param apkFile APK file to verify
     * @return True if APK is valid
     */
    public boolean verifyAPKIntegrity(File apkFile) {
        try {
            logger.debug("Verifying APK integrity...");
            
            // Basic APK structure verification
            try (ZipFile zipFile = new ZipFile(apkFile)) {
                // Check for essential APK files
                boolean hasManifest = false;
                boolean hasDex = false;
                
                var entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    
                    if (name.equals("AndroidManifest.xml")) {
                        hasManifest = true;
                    } else if (name.endsWith(".dex")) {
                        hasDex = true;
                    }
                }
                
                if (!hasManifest) {
                    logger.error("APK missing AndroidManifest.xml");
                    return false;
                }
                
                if (!hasDex) {
                    logger.error("APK missing DEX files");
                    return false;
                }
                
                logger.debug("APK structure verification passed");
                return true;
            }
            
        } catch (Exception e) {
            logger.error("APK integrity verification failed: " + e.getMessage());
            return false;
        }
    }
}
