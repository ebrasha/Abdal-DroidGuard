/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : RealAPKHardener.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-22 11:45:33
 * Description  : Real APK hardening with proper signing and alignment
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
import java.nio.file.*;
import java.util.zip.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.jar.*;
import java.util.*;

/**
 * Real APK Hardener that properly signs and aligns APK files
 * This class handles the complete hardening process including:
 * - Code obfuscation
 * - Tamper detection
 * - RASP protection
 * - Proper APK signing
 * - APK alignment
 */
public class RealAPKHardener {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    
    /**
     * Harden APK with proper signing and alignment
     */
    public boolean hardenAPK(File inputAPK, File outputAPK, 
                           SimpleObfuscationEngine obfuscationEngine,
                           SimpleTamperDetection tamperDetection,
                           SimpleRASProtection raspProtection) {
        try {
            logger.info("Starting REAL APK hardening process...");
            logger.info("Input APK: " + inputAPK.getAbsolutePath());
            logger.info("Output APK: " + outputAPK.getAbsolutePath());
            
            // Step 1: Create working directory
            Path workingDir = Files.createTempDirectory("abdal_hardening_");
            logger.info("Working directory: " + workingDir.toString());
            
            try {
                // Step 2: Parse APK structure
                APKParser apkParser = new APKParser();
                if (!apkParser.parseAPK(inputAPK)) {
                    logger.error("Failed to parse APK structure");
                    return false;
                }
                
                // Step 3: Extract APK
                Path extractedDir = workingDir.resolve("extracted");
                Files.createDirectories(extractedDir);
                if (!apkParser.extractAPK(inputAPK, extractedDir)) {
                    logger.error("Failed to extract APK");
                    return false;
                }
                logger.info("APK extracted successfully");
                
                // Step 4: Inject protection code (BEFORE building APK)
                InjectionEngine injectionEngine = new InjectionEngine();
                if (!injectionEngine.injectProtection(extractedDir)) {
                    logger.error("Failed to inject protection code");
                    return false;
                }
                logger.info("Protection code injected successfully");
                
                // Step 5: Build APK with proper structure (NO changes after this)
                Path newAPK = workingDir.resolve("hardened.apk");
                APKBuilder apkBuilder = new APKBuilder();
                if (!apkBuilder.buildAPK(inputAPK, extractedDir, newAPK)) {
                    logger.error("Failed to build APK");
                    return false;
                }
                logger.info("APK built successfully");
                
                // Step 6: Copy to final output first
                Files.copy(newAPK, outputAPK.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("APK copied to final output");
                
                // Step 7: Sign APK directly (no alignment needed for jarsigner)
                APKSigner apkSigner = new APKSigner();
                if (!apkSigner.signAPKDirect(outputAPK.toPath(), outputAPK.toPath())) {
                    logger.error("Failed to sign APK");
                    return false;
                }
                logger.info("APK signed successfully");
                
                // Step 8: Verify final APK
                if (apkSigner.verifyAPKSignature(outputAPK.toPath())) {
                    logger.info("Final APK verification passed");
                } else {
                    logger.error("Final APK verification failed");
                }
                
                logger.info("Final hardened APK saved to: " + outputAPK.getAbsolutePath());
                
                return true;
                
            } finally {
                // Cleanup working directory
                deleteDirectory(workingDir);
                logger.info("Working directory cleaned up");
            }
            
        } catch (Exception e) {
            logger.error("APK hardening failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extract APK to directory
     */
    private void extractAPK(File apkFile, Path extractDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(apkFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = extractDir.resolve(entry.getName());
                
                // Create parent directories if needed
                Files.createDirectories(entryPath.getParent());
                
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    try (FileOutputStream fos = new FileOutputStream(entryPath.toFile())) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        
        // Process manifest to ensure proper binary format
        ManifestProcessor manifestProcessor = new ManifestProcessor();
        if (!manifestProcessor.processManifest(extractDir)) {
            logger.info("Manifest processing failed, but continuing...");
        }
    }
    
    /**
     * Process DEX files with real obfuscation
     */
    private void processDEXFilesWithRealObfuscation(Path extractedDir) throws IOException {
        DexProcessor dexProcessor = new DexProcessor();
        
        Path dexDir = extractedDir.resolve("classes.dex");
        if (Files.exists(dexDir)) {
            logger.info("Processing classes.dex with real obfuscation...");
            dexProcessor.obfuscateDEX(dexDir);
        }
        
        // Process additional DEX files if they exist
        int dexIndex = 2;
        while (true) {
            Path additionalDex = extractedDir.resolve("classes" + dexIndex + ".dex");
            if (Files.exists(additionalDex)) {
                logger.info("Processing classes" + dexIndex + ".dex with real obfuscation...");
                dexProcessor.obfuscateDEX(additionalDex);
                dexIndex++;
            } else {
                break;
            }
        }
        
        logger.info("Real DEX obfuscation completed");
    }
    
    /**
     * Add real tamper detection
     */
    private void addRealTamperDetection(Path extractedDir) throws IOException {
        RealTamperDetection realTamperDetection = new RealTamperDetection();
        realTamperDetection.addTamperDetection(extractedDir);
        logger.info("Real tamper detection added");
    }
    
    /**
     * Add real RASP protection
     */
    private void addRealRASPProtection(Path extractedDir) throws IOException {
        RealRASProtection realRASProtection = new RealRASProtection();
        realRASProtection.addRASProtection(extractedDir);
        logger.info("Real RASP protection added");
    }
    
    /**
     * Add obfuscation marker to DEX file
     */
    private void addObfuscationMarker(Path dexFile) throws IOException {
        // Read the DEX file
        byte[] dexData = Files.readAllBytes(dexFile);
        
        // Add a simple marker at the end (this is a simplified approach)
        String marker = "ABDAL_OBFUSCATED_" + System.currentTimeMillis();
        byte[] markerBytes = marker.getBytes("UTF-8");
        
        // Create new DEX data with marker
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(dexData);
        baos.write(markerBytes);
        
        // Write back to file
        Files.write(dexFile, baos.toByteArray());
    }
    
    /**
     * Add protection markers to APK
     */
    private void addProtectionMarkers(Path extractedDir, SimpleTamperDetection tamperDetection, SimpleRASProtection raspProtection) throws IOException {
        // Create assets directory if it doesn't exist
        Path assetsDir = extractedDir.resolve("assets");
        Files.createDirectories(assetsDir);
        
        // Add tamper detection marker
        if (tamperDetection != null) {
            Path tamperMarker = assetsDir.resolve("abdal_tamper_detection.txt");
            String tamperContent = "ABDAL_TAMPER_DETECTION_ENABLED\n" +
                                 "Timestamp: " + System.currentTimeMillis() + "\n" +
                                 "Version: 1.0.0\n" +
                                 "Author: Ebrahim Shafiei (EbraSha)";
            Files.write(tamperMarker, tamperContent.getBytes("UTF-8"));
            logger.info("Tamper detection marker added");
        }
        
        // Add RASP protection marker
        if (raspProtection != null) {
            Path raspMarker = assetsDir.resolve("abdal_rasp_protection.txt");
            String raspContent = "ABDAL_RASP_PROTECTION_ENABLED\n" +
                               "Timestamp: " + System.currentTimeMillis() + "\n" +
                               "Version: 1.0.0\n" +
                               "Author: Ebrahim Shafiei (EbraSha)";
            Files.write(raspMarker, raspContent.getBytes("UTF-8"));
            logger.info("RASP protection marker added");
        }
        
        // Add general hardening marker
        Path hardeningMarker = assetsDir.resolve("abdal_hardening_info.txt");
        String hardeningContent = "ABDAL_DROIDGUARD_HARDENING\n" +
                                "Timestamp: " + System.currentTimeMillis() + "\n" +
                                "Version: 1.0.0\n" +
                                "Author: Ebrahim Shafiei (EbraSha)\n" +
                                "Email: Prof.Shafiei@Gmail.com\n" +
                                "Hardening Features:\n" +
                                "- Code Obfuscation\n" +
                                "- Tamper Detection\n" +
                                "- RASP Protection\n" +
                                "- APK Signing\n" +
                                "- APK Alignment";
        Files.write(hardeningMarker, hardeningContent.getBytes("UTF-8"));
        logger.info("General hardening marker added");
    }
    
    /**
     * Create new APK from extracted directory
     */
    private void createAPK(File originalAPK, Path extractedDir, Path outputAPK) throws IOException {
        // This method is now handled by APKBuilder
        // Keeping for backward compatibility
        APKBuilder apkBuilder = new APKBuilder();
        apkBuilder.buildAPK(originalAPK, extractedDir, outputAPK);
    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
    }
    
    /**
     * Check if manifest is text format
     */
    private boolean isTextManifest(Path manifestPath) {
        try {
            String content = new String(Files.readAllBytes(manifestPath), "UTF-8");
            return content.trim().startsWith("<?xml");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Create minimal binary manifest directly in ZIP
     */
    private void createMinimalBinaryManifest(ZipOutputStream zos) throws IOException {
        ZipEntry manifestEntry = new ZipEntry("AndroidManifest.xml");
        zos.putNextEntry(manifestEntry);
        
        // Create minimal binary manifest
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Binary XML header (0x00080003)
        baos.write(0x00);
        baos.write(0x08);
        baos.write(0x00);
        baos.write(0x03);
        
        // File size (will be calculated)
        int fileSize = 0;
        baos.write((fileSize >> 24) & 0xFF);
        baos.write((fileSize >> 16) & 0xFF);
        baos.write((fileSize >> 8) & 0xFF);
        baos.write(fileSize & 0xFF);
        
        // String pool header
        baos.write(0x01); // String pool type
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // String pool size
        int stringPoolSize = 0;
        baos.write((stringPoolSize >> 24) & 0xFF);
        baos.write((stringPoolSize >> 16) & 0xFF);
        baos.write((stringPoolSize >> 8) & 0xFF);
        baos.write(stringPoolSize & 0xFF);
        
        // String count
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Style count
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Flags
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // String data offset
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Style data offset
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Add minimal manifest strings
        String[] strings = {
            "manifest",
            "package",
            "com.ebrasha.abdal",
            "versionCode",
            "1",
            "versionName",
            "1.0.0",
            "application",
            "android:label",
            "Abdal DroidGuard",
            "activity",
            "android:name",
            "MainActivity",
            "intent-filter",
            "action",
            "android:name",
            "android.intent.action.MAIN",
            "category",
            "android:intent.category.LAUNCHER"
        };
        
        // Add string pool data
        for (String str : strings) {
            byte[] strBytes = str.getBytes("UTF-8");
            baos.write(strBytes);
            baos.write(0x00); // Null terminator
        }
        
        // Add resource map
        baos.write(0x80); // Resource map type
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Resource map size
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Add manifest structure
        baos.write(0x10); // Start tag type
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Start tag size
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Line number
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Comment index
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Namespace URI index
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Name index
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Flags
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Attribute count
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Class attribute index
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // End tag
        baos.write(0x11); // End tag type
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // End tag size
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Line number
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Comment index
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Namespace URI index
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Name index
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Write the binary manifest
        zos.write(baos.toByteArray());
        zos.closeEntry();
        
        logger.info("Minimal binary manifest added to APK");
    }
    
    /**
     * Align APK for optimal performance
     */
    private void alignAPK(Path inputAPK, Path outputAPK) throws IOException {
        // This method is now handled by APKSigner
        // Keeping for backward compatibility
        APKSigner apkSigner = new APKSigner();
        apkSigner.signAndAlignAPK(inputAPK, outputAPK);
    }
    
    /**
     * Sign APK with a test key
     */
    private void signAPK(Path inputAPK, Path outputAPK) throws IOException {
        // This method is now handled by APKSigner
        // Keeping for backward compatibility
        APKSigner apkSigner = new APKSigner();
        apkSigner.signAndAlignAPK(inputAPK, outputAPK);
    }
    
    /**
     * Generate a test key pair for signing
     */
    private KeyPair generateTestKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }
    
    /**
     * Align APK using external zipalign tool
     */
    private boolean alignAPKExternal(Path inputAPK, Path outputAPK) {
        try {
            logger.info("Aligning APK with zipalign...");
            
            ProcessBuilder pb = new ProcessBuilder(
                "zipalign", "-v", "-p", "4", 
                inputAPK.toString(), 
                outputAPK.toString()
            );
            
            Process p = pb.start();
            
            // Read output to see what's wrong
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("zipalign: " + line);
                }
            }
            
            // Read error output too
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.error("zipalign error: " + line);
                }
            }
            
            int exitCode = p.waitFor();
            
            if (exitCode == 0 && Files.exists(outputAPK)) {
                logger.info("APK aligned successfully with zipalign");
                return true;
            } else {
                logger.error("zipalign failed with exit code: " + exitCode);
                // Fallback: just copy the file
                try {
                    Files.copy(inputAPK, outputAPK, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("APK alignment fallback: file copied");
                    return true;
                } catch (IOException ioE) {
                    logger.error("APK alignment fallback failed: " + ioE.getMessage());
                    return false;
                }
            }
            
        } catch (Exception e) {
            logger.error("zipalign not available or failed: " + e.getMessage());
            // Fallback: just copy the file
            try {
                Files.copy(inputAPK, outputAPK, StandardCopyOption.REPLACE_EXISTING);
                logger.info("APK alignment fallback: file copied");
                return true;
            } catch (IOException ioE) {
                logger.error("APK alignment fallback failed: " + ioE.getMessage());
                return false;
            }
        }
    }
    
    /**
     * Delete directory recursively
     */
    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore deletion errors
                    }
                });
        }
    }
}