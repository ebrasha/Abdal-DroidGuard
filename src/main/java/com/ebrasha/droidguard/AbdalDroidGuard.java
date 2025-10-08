/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : AbdalDroidGuard.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-23 11:38:15
 * Description  : Main application class for Abdal DroidGuard - Advanced Android Application Hardening Tool
 * -------------------------------------------------------------------
 *
 * "Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming."
 * – Ebrahim Shafiei
 *
 **********************************************************************
 */

package com.ebrasha.droidguard;

import com.ebrasha.droidguard.core.AuthorDisplay;
import com.ebrasha.droidguard.core.ObfuscationEngine;
import com.ebrasha.droidguard.core.TamperDetection;
import com.ebrasha.droidguard.core.RASProtection;
import com.ebrasha.droidguard.utils.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Main application class for Abdal DroidGuard
 * Advanced Android Application Hardening Tool for JVM and DEX bytecode protection
 */
@Command(name = "abdal-droidguard", 
         mixinStandardHelpOptions = true, 
         version = "1.0.0",
         description = "Advanced Android Application Hardening Tool for JVM and DEX Bytecode Protection")
public class AbdalDroidGuard implements Callable<Integer> {

    @Parameters(index = "0", description = "Path to the Android application file (APK/JAR)")
    private File inputFile;

    @Option(names = {"-o", "--output"}, description = "Output file path")
    private File outputFile;

    @Option(names = {"--obfuscate"}, description = "Enable code obfuscation")
    private boolean enableObfuscation = false;

    @Option(names = {"--tamper-detect"}, description = "Enable tamper detection")
    private boolean enableTamperDetection = false;

    @Option(names = {"--rasp"}, description = "Enable Runtime Application Self-Protection")
    private boolean enableRASP = false;

    @Option(names = {"--all"}, description = "Enable all protection features")
    private boolean enableAll = false;

    @Option(names = {"--verbose", "-v"}, description = "Enable verbose logging")
    private boolean verbose = false;

    @Option(names = {"--config"}, description = "Configuration file path")
    private File configFile;

    private final Logger logger = Logger.getInstance();

    public static void main(String[] args) {
        // Display author information at startup
        AuthorDisplay.displayAuthorInfo();
        
        int exitCode = new CommandLine(new AbdalDroidGuard()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try {
            // Initialize logger
            logger.setVerbose(verbose);
            
            // Display banner
            displayBanner();
            
            // Validate input
            if (!validateInput()) {
                return 1;
            }

            // Set default output if not provided
            if (outputFile == null) {
                outputFile = new File(inputFile.getParent(), 
                    inputFile.getName().replaceAll("\\.(apk|jar)$", "_hardened.$1"));
            }

            // Determine which features to enable
            boolean obfuscate = enableObfuscation || enableAll;
            boolean tamperDetect = enableTamperDetection || enableAll;
            boolean rasp = enableRASP || enableAll;

            logger.info("Starting Abdal DroidGuard hardening process...");
            logger.info("Input file: " + inputFile.getAbsolutePath());
            logger.info("Output file: " + outputFile.getAbsolutePath());
            logger.info("Features enabled:");
            logger.info("  - Obfuscation: " + obfuscate);
            logger.info("  - Tamper Detection: " + tamperDetect);
            logger.info("  - RASP Protection: " + rasp);

            // Initialize protection engines
            ObfuscationEngine obfuscationEngine = null;
            TamperDetection tamperDetection = null;
            RASProtection raspProtection = null;

            if (obfuscate) {
                logger.info("Initializing obfuscation engine...");
                obfuscationEngine = new ObfuscationEngine();
            }

            if (tamperDetect) {
                logger.info("Initializing tamper detection...");
                tamperDetection = new TamperDetection();
            }

            if (rasp) {
                logger.info("Initializing RASP protection...");
                raspProtection = new RASProtection();
            }

            // Process the application
            boolean success = processApplication(obfuscationEngine, tamperDetection, raspProtection);

            if (success) {
                logger.info("Hardening process completed successfully!");
                logger.info("Hardened application saved to: " + outputFile.getAbsolutePath());
                return 0;
            } else {
                logger.error("Hardening process failed!");
                return 1;
            }

        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    private void displayBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Abdal DroidGuard v1.0.0                  ║");
        System.out.println("║              Advanced Android Hardening Tool                ║");
        System.out.println("║                                                              ║");
        System.out.println("║  🔒 Code Obfuscation    🛡️  Tamper Detection               ║");
        System.out.println("║  🚀 RASP Protection     ⚡ JVM/DEX Bytecode Security        ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Developed by: Ebrahim Shafiei (EbraSha)                    ║");
        System.out.println("║  Email: Prof.Shafiei@Gmail.com                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private boolean validateInput() {
        if (inputFile == null) {
            logger.error("Input file is required!");
            return false;
        }

        if (!inputFile.exists()) {
            logger.error("Input file does not exist: " + inputFile.getAbsolutePath());
            return false;
        }

        if (!inputFile.isFile()) {
            logger.error("Input path is not a file: " + inputFile.getAbsolutePath());
            return false;
        }

        String fileName = inputFile.getName().toLowerCase();
        if (!fileName.endsWith(".apk") && !fileName.endsWith(".jar")) {
            logger.error("Input file must be an APK or JAR file!");
            return false;
        }

        return true;
    }

    private boolean processApplication(ObfuscationEngine obfuscationEngine, 
                                     TamperDetection tamperDetection, 
                                     RASProtection raspProtection) {
        try {
            // Create a copy of the input file as starting point
            File workingFile = new File(outputFile.getParent(), "temp_" + outputFile.getName());
            java.nio.file.Files.copy(inputFile.toPath(), workingFile.toPath());

            // Apply obfuscation if enabled
            if (obfuscationEngine != null) {
                logger.info("Applying code obfuscation...");
                if (!obfuscationEngine.processFile(workingFile, inputFile.getName().endsWith(".apk"))) {
                    logger.error("Obfuscation failed!");
                    return false;
                }
            }

            // Apply tamper detection if enabled
            if (tamperDetection != null) {
                logger.info("Adding tamper detection...");
                if (!tamperDetection.addTamperDetection(workingFile)) {
                    logger.error("Tamper detection setup failed!");
                    return false;
                }
            }

            // Apply RASP protection if enabled
            if (raspProtection != null) {
                logger.info("Adding RASP protection...");
                if (!raspProtection.addRASProtection(workingFile)) {
                    logger.error("RASP protection setup failed!");
                    return false;
                }
            }

            // Move final file to output location
            if (workingFile.renameTo(outputFile)) {
                logger.info("Final hardened application created successfully!");
                return true;
            } else {
                logger.error("Failed to create final output file!");
                return false;
            }

        } catch (Exception e) {
            logger.error("Error during application processing: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
