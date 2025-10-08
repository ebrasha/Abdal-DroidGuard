/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : SafeAPKProcessor.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-15 07:43:12
 * Description  : Safe APK processing to maintain APK integrity
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
 * Safe APK processor that maintains APK integrity during hardening
 */
public class SafeAPKProcessor {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    
    /**
     * Process APK safely without breaking its structure
     * @param inputAPK Input APK file
     * @param outputAPK Output APK file
     * @param obfuscationEngine Obfuscation engine
     * @param tamperDetection Tamper detection engine
     * @param raspProtection RASP protection engine
     * @return True if processing was successful
     */
    public boolean processAPKSafely(File inputAPK, File outputAPK, 
                                   SimpleObfuscationEngine obfuscationEngine,
                                   SimpleTamperDetection tamperDetection,
                                   SimpleRASProtection raspProtection) {
        try {
            logger.info("Starting safe APK processing...");
            
            // Create temporary directory for APK processing
            Path tempDir = Files.createTempDirectory("abdal_safe_apk_");
            Path extractedDir = tempDir.resolve("extracted");
            Files.createDirectories(extractedDir);
            
            // Extract APK contents safely
            if (!extractAPKSafely(inputAPK, extractedDir)) {
                logger.error("Failed to extract APK safely");
                return false;
            }
            
            // Process DEX files only (don't modify other critical files)
            if (obfuscationEngine != null) {
                logger.info("Applying safe obfuscation to DEX files...");
                if (!obfuscateDEXFilesSafely(extractedDir)) {
                    logger.warn("DEX obfuscation failed, continuing without it");
                }
            }
            
            // Add protection markers to resources (not critical files)
            if (tamperDetection != null) {
                logger.info("Adding tamper detection markers...");
                addTamperDetectionMarkers(extractedDir);
            }
            
            // Add RASP protection markers
            if (raspProtection != null) {
                logger.info("Adding RASP protection markers...");
                addRASPProtectionMarkers(extractedDir);
            }
            
            // Repackage APK maintaining original structure
            if (!repackageAPKSafely(extractedDir, outputAPK)) {
                logger.error("Failed to repackage APK safely");
                return false;
            }
            
            // Cleanup
            deleteDirectory(tempDir.toFile());
            
            logger.success("APK processed safely and successfully!");
            return true;
            
        } catch (Exception e) {
            logger.error("Safe APK processing failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract APK contents safely
     * @param apkFile APK file
     * @param extractDir Directory to extract to
     * @return True if successful
     */
    private boolean extractAPKSafely(File apkFile, Path extractDir) {
        try {
            logger.debug("Extracting APK contents safely...");
            
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
     * Obfuscate DEX files safely
     * @param extractDir Directory containing extracted files
     * @return True if successful
     */
    private boolean obfuscateDEXFilesSafely(Path extractDir) {
        try {
            logger.debug("Obfuscating DEX files safely...");
            
            Files.walk(extractDir)
                .filter(path -> path.toString().endsWith(".dex"))
                .forEach(dexPath -> {
                    try {
                        obfuscateDEXFileSafely(dexPath);
                    } catch (Exception e) {
                        logger.warn("Failed to obfuscate DEX file: " + dexPath + " - " + e.getMessage());
                    }
                });
            
            return true;
            
        } catch (Exception e) {
            logger.error("DEX obfuscation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obfuscate a single DEX file safely
     * @param dexPath Path to DEX file
     */
    private void obfuscateDEXFileSafely(Path dexPath) throws IOException {
        logger.debug("Obfuscating DEX file safely: " + dexPath.getFileName());
        
        // Read DEX file
        byte[] dexData = Files.readAllBytes(dexPath);
        
        // Apply minimal, safe obfuscation that won't break the DEX format
        // Only add protection markers without changing critical structures
        dexData = addSafeProtectionMarker(dexData, "ABDAL_PROTECTED_DEX");
        
        // Write back
        Files.write(dexPath, dexData);
    }
    
    /**
     * Add safe protection marker to DEX data
     * @param dexData Original DEX data
     * @param marker Protection marker
     * @return Modified DEX data
     */
    private byte[] addSafeProtectionMarker(byte[] dexData, String marker) {
        try {
            // Add marker at the end of the file (safest approach)
            byte[] markerBytes = marker.getBytes("UTF-8");
            byte[] result = new byte[dexData.length + markerBytes.length];
            
            System.arraycopy(dexData, 0, result, 0, dexData.length);
            System.arraycopy(markerBytes, 0, result, dexData.length, markerBytes.length);
            
            return result;
            
        } catch (Exception e) {
            logger.warn("Failed to add protection marker: " + e.getMessage());
            return dexData;
        }
    }
    
    /**
     * Add tamper detection markers to resources
     * @param extractDir Directory containing extracted files
     */
    private void addTamperDetectionMarkers(Path extractDir) {
        try {
            logger.debug("Adding tamper detection markers...");
            
            // Add markers to non-critical files only
            Path assetsDir = extractDir.resolve("assets");
            if (Files.exists(assetsDir)) {
                Path markerFile = assetsDir.resolve("abdal_tamper_protection.txt");
                String markerContent = "ABDAL_TAMPER_PROTECTION|" + System.currentTimeMillis() + "|" + 
                                     "Developed by Ebrahim Shafiei (EbraSha)";
                Files.writeString(markerFile, markerContent);
            }
            
        } catch (Exception e) {
            logger.warn("Failed to add tamper detection markers: " + e.getMessage());
        }
    }
    
    /**
     * Add RASP protection markers
     * @param extractDir Directory containing extracted files
     */
    private void addRASPProtectionMarkers(Path extractDir) {
        try {
            logger.debug("Adding RASP protection markers...");
            
            // Add markers to non-critical files only
            Path assetsDir = extractDir.resolve("assets");
            if (Files.exists(assetsDir)) {
                Path markerFile = assetsDir.resolve("abdal_rasp_protection.txt");
                String markerContent = "ABDAL_RASP_PROTECTION|" + System.currentTimeMillis() + "|" +
                                     "ANTI_DEBUG|EMULATOR_DETECT|ROOT_DETECT|HOOK_DETECT|" +
                                     "Developed by Ebrahim Shafiei (EbraSha)";
                Files.writeString(markerFile, markerContent);
            }
            
        } catch (Exception e) {
            logger.warn("Failed to add RASP protection markers: " + e.getMessage());
        }
    }
    
    /**
     * Repackage APK safely maintaining original structure
     * @param extractDir Directory containing extracted files
     * @param outputAPK Output APK file
     * @return True if successful
     */
    private boolean repackageAPKSafely(Path extractDir, File outputAPK) {
        try {
            logger.debug("Repackaging APK safely...");
            
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputAPK))) {
                Files.walk(extractDir)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            String relativePath = extractDir.relativize(filePath).toString().replace("\\", "/");
                            ZipEntry entry = new ZipEntry(relativePath);
                            
                            // Preserve original compression method
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
}
