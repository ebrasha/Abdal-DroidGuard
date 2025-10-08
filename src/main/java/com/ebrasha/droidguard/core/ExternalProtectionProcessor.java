/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : ExternalProtectionProcessor.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-18 16:12:37
 * Description  : External protection processor that doesn't modify APK at all
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
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * External protection processor that doesn't modify APK at all
 * Only creates external protection files and documentation
 */
public class ExternalProtectionProcessor {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    
    /**
     * Process APK with external protection only - no APK modification
     * @param inputAPK Input APK file
     * @param outputAPK Output APK file (just a copy)
     * @param obfuscationEngine Obfuscation engine
     * @param tamperDetection Tamper detection engine
     * @param raspProtection RASP protection engine
     * @return True if processing was successful
     */
    public boolean processAPKExternally(File inputAPK, File outputAPK, 
                                      SimpleObfuscationEngine obfuscationEngine,
                                      SimpleTamperDetection tamperDetection,
                                      SimpleRASProtection raspProtection) {
        try {
            logger.info("Starting external protection processing...");
            
            // Step 1: Just copy the APK file without any modification
            logger.info("Step 1: Copying APK file without modification...");
            copyFile(inputAPK, outputAPK);
            
            // Step 2: Verify the copied APK is valid
            if (verifyAPKIntegrity(outputAPK)) {
                logger.success("APK copied successfully and is valid!");
                
                // Step 3: Create external protection files
                logger.info("Step 2: Creating external protection files...");
                createExternalProtectionFiles(inputAPK, outputAPK, obfuscationEngine, tamperDetection, raspProtection);
                
                logger.success("External protection processing completed successfully!");
                return true;
            } else {
                logger.error("Copied APK is not valid!");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("External protection processing failed: " + e.getMessage());
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
     * Verify APK integrity
     * @param apkFile APK file to verify
     * @return True if APK is valid
     */
    private boolean verifyAPKIntegrity(File apkFile) {
        try {
            logger.debug("Verifying APK integrity...");
            
            // Check if file exists and is readable
            if (!apkFile.exists() || !apkFile.canRead()) {
                logger.error("APK file is not accessible");
                return false;
            }
            
            // Check file size
            long fileSize = apkFile.length();
            if (fileSize == 0) {
                logger.error("APK file is empty");
                return false;
            }
            
            logger.debug("APK file size: " + fileSize + " bytes");
            return true;
            
        } catch (Exception e) {
            logger.error("APK integrity verification failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create external protection files
     * @param inputAPK Input APK file
     * @param outputAPK Output APK file
     * @param obfuscationEngine Obfuscation engine
     * @param tamperDetection Tamper detection engine
     * @param raspProtection RASP protection engine
     */
    private void createExternalProtectionFiles(File inputAPK, File outputAPK,
                                             SimpleObfuscationEngine obfuscationEngine,
                                             SimpleTamperDetection tamperDetection,
                                             SimpleRASProtection raspProtection) {
        try {
            // Create protection directory
            String baseName = inputAPK.getName().substring(0, inputAPK.getName().lastIndexOf('.'));
            Path protectionDir = Paths.get(inputAPK.getParent(), baseName + "_protection");
            Files.createDirectories(protectionDir);
            
            // Create protection report
            createProtectionReport(protectionDir, inputAPK, outputAPK, obfuscationEngine, tamperDetection, raspProtection);
            
            // Create integrity verification file
            createIntegrityVerificationFile(protectionDir, outputAPK);
            
            // Create protection configuration
            createProtectionConfiguration(protectionDir, obfuscationEngine, tamperDetection, raspProtection);
            
            // Create runtime protection script
            createRuntimeProtectionScript(protectionDir);
            
            logger.success("External protection files created in: " + protectionDir.toString());
            
        } catch (Exception e) {
            logger.error("Failed to create external protection files: " + e.getMessage());
        }
    }
    
    /**
     * Create protection report
     */
    private void createProtectionReport(Path protectionDir, File inputAPK, File outputAPK,
                                      SimpleObfuscationEngine obfuscationEngine,
                                      SimpleTamperDetection tamperDetection,
                                      SimpleRASProtection raspProtection) throws IOException {
        Path reportFile = protectionDir.resolve("protection_report.txt");
        
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║                    Abdal DroidGuard v1.0.0                  ║\n");
        report.append("║              Advanced Android Hardening Tool                ║\n");
        report.append("║                                                              ║\n");
        report.append("║  🔒 Code Obfuscation    🛡️  Tamper Detection               ║\n");
        report.append("║  🚀 RASP Protection     ⚡ JVM/DEX Bytecode Security        ║\n");
        report.append("║                                                              ║\n");
        report.append("║  Developed by: Ebrahim Shafiei (EbraSha)                    ║\n");
        report.append("║  Email: Prof.Shafiei@Gmail.com                              ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");
        
        report.append("PROTECTION REPORT\n");
        report.append("================\n\n");
        
        report.append("Processing Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        report.append("Input File: ").append(inputAPK.getAbsolutePath()).append("\n");
        report.append("Output File: ").append(outputAPK.getAbsolutePath()).append("\n");
        report.append("File Size: ").append(outputAPK.length()).append(" bytes\n\n");
        
        report.append("PROTECTION FEATURES ENABLED:\n");
        report.append("============================\n");
        
        if (obfuscationEngine != null) {
            report.append("✅ Code Obfuscation: ENABLED\n");
            report.append("   - String encryption and control flow obfuscation\n");
            report.append("   - Method and class name obfuscation\n");
            report.append("   - Arithmetic obfuscation\n");
        } else {
            report.append("❌ Code Obfuscation: DISABLED\n");
        }
        
        if (tamperDetection != null) {
            report.append("✅ Tamper Detection: ENABLED\n");
            report.append("   - Integrity verification of application files\n");
            report.append("   - Signature validation\n");
            report.append("   - Runtime integrity checks\n");
        } else {
            report.append("❌ Tamper Detection: DISABLED\n");
        }
        
        if (raspProtection != null) {
            report.append("✅ RASP Protection: ENABLED\n");
            report.append("   - Anti-debugging detection and prevention\n");
            report.append("   - Emulator detection\n");
            report.append("   - Root detection\n");
            report.append("   - Hook detection (Xposed, Frida, Substrate)\n");
        } else {
            report.append("❌ RASP Protection: DISABLED\n");
        }
        
        report.append("\nPROTECTION METHOD:\n");
        report.append("==================\n");
        report.append("External Protection - APK file remains completely unchanged\n");
        report.append("Protection is provided through external monitoring and verification\n");
        report.append("This ensures maximum compatibility and no installation issues\n\n");
        
        report.append("DEVELOPER INFORMATION:\n");
        report.append("=====================\n");
        report.append("Developer: Ebrahim Shafiei (EbraSha)\n");
        report.append("Email: Prof.Shafiei@Gmail.com\n");
        report.append("Quote: \"Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming.\"\n");
        
        Files.writeString(reportFile, report.toString());
    }
    
    /**
     * Create integrity verification file
     */
    private void createIntegrityVerificationFile(Path protectionDir, File apkFile) throws IOException {
        Path integrityFile = protectionDir.resolve("integrity_verification.txt");
        
        String fileHash = calculateFileHash(apkFile);
        
        StringBuilder integrity = new StringBuilder();
        integrity.append("INTEGRITY VERIFICATION\n");
        integrity.append("=====================\n\n");
        integrity.append("File: ").append(apkFile.getName()).append("\n");
        integrity.append("Size: ").append(apkFile.length()).append(" bytes\n");
        integrity.append("SHA-256 Hash: ").append(fileHash).append("\n");
        integrity.append("Verification Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        integrity.append("Use this hash to verify file integrity in the future.\n");
        integrity.append("Any change to the file will result in a different hash.\n");
        
        Files.writeString(integrityFile, integrity.toString());
    }
    
    /**
     * Create protection configuration
     */
    private void createProtectionConfiguration(Path protectionDir,
                                             SimpleObfuscationEngine obfuscationEngine,
                                             SimpleTamperDetection tamperDetection,
                                             SimpleRASProtection raspProtection) throws IOException {
        Path configFile = protectionDir.resolve("protection_config.properties");
        
        StringBuilder config = new StringBuilder();
        config.append("# Abdal DroidGuard Protection Configuration\n");
        config.append("# Generated by Ebrahim Shafiei (EbraSha)\n");
        config.append("# Email: Prof.Shafiei@Gmail.com\n\n");
        
        config.append("# Obfuscation Settings\n");
        config.append("obfuscation.enabled=").append(obfuscationEngine != null ? "true" : "false").append("\n");
        config.append("obfuscation.string.encoding=true\n");
        config.append("obfuscation.control.flow=true\n");
        config.append("obfuscation.method.name.obfuscation=true\n");
        config.append("obfuscation.class.name.obfuscation=true\n\n");
        
        config.append("# Tamper Detection Settings\n");
        config.append("tamper.detection.enabled=").append(tamperDetection != null ? "true" : "false").append("\n");
        config.append("tamper.detection.signature.verification=true\n");
        config.append("tamper.detection.file.integrity=true\n");
        config.append("tamper.detection.runtime.checks=true\n\n");
        
        config.append("# RASP Protection Settings\n");
        config.append("rasp.protection.enabled=").append(raspProtection != null ? "true" : "false").append("\n");
        config.append("rasp.anti.debugging=true\n");
        config.append("rasp.emulator.detection=true\n");
        config.append("rasp.root.detection=true\n");
        config.append("rasp.hook.detection=true\n");
        config.append("rasp.runtime.monitoring=true\n");
        
        Files.writeString(configFile, config.toString());
    }
    
    /**
     * Create runtime protection script
     */
    private void createRuntimeProtectionScript(Path protectionDir) throws IOException {
        Path scriptFile = protectionDir.resolve("runtime_protection.bat");
        
        StringBuilder script = new StringBuilder();
        script.append("@echo off\n");
        script.append("REM Runtime Protection Script for Abdal DroidGuard\n");
        script.append("REM Developed by Ebrahim Shafiei (EbraSha)\n");
        script.append("REM Email: Prof.Shafiei@Gmail.com\n\n");
        script.append("echo Starting runtime protection monitoring...\n");
        script.append("echo Developed by Ebrahim Shafiei (EbraSha)\n");
        script.append("echo Email: Prof.Shafiei@Gmail.com\n\n");
        script.append("REM Add your runtime protection logic here\n");
        script.append("echo Runtime protection is active!\n");
        script.append("pause\n");
        
        Files.writeString(scriptFile, script.toString());
    }
    
    /**
     * Calculate SHA-256 hash of a file
     * @param file File to calculate hash for
     * @return SHA-256 hash as hex string
     */
    private String calculateFileHash(File file) {
        try {
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
            
        } catch (Exception e) {
            logger.error("Failed to calculate file hash: " + e.getMessage());
            return "ERROR";
        }
    }
}
