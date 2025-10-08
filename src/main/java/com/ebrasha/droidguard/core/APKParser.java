/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : APKParser.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-12 13:28:55
 * Description  : APK Parser for extracting and analyzing APK structure
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
import java.util.*;

/**
 * APK Parser for extracting and analyzing APK structure
 * This class handles APK parsing, extraction, and structure analysis
 */
public class APKParser {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    private final Map<String, APKEntry> entries = new HashMap<>();
    private final List<String> dexFiles = new ArrayList<>();
    private final List<String> nativeLibraries = new ArrayList<>();
    private final List<String> resources = new ArrayList<>();
    private String manifestPath = null;
    
    /**
     * APK Entry information
     */
    public static class APKEntry {
        public final String name;
        public final long size;
        public final long compressedSize;
        public final long crc;
        public final int method;
        public final long time;
        public final boolean isDirectory;
        
        public APKEntry(String name, long size, long compressedSize, long crc, 
                       int method, long time, boolean isDirectory) {
            this.name = name;
            this.size = size;
            this.compressedSize = compressedSize;
            this.crc = crc;
            this.method = method;
            this.time = time;
            this.isDirectory = isDirectory;
        }
    }
    
    /**
     * Parse APK file and extract structure information
     */
    public boolean parseAPK(File apkFile) {
        try {
            logger.info("Parsing APK: " + apkFile.getAbsolutePath());
            
            entries.clear();
            dexFiles.clear();
            nativeLibraries.clear();
            resources.clear();
            manifestPath = null;
            
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(apkFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String name = entry.getName();
                    
                    // Create APK entry
                    APKEntry apkEntry = new APKEntry(
                        name,
                        entry.getSize(),
                        entry.getCompressedSize(),
                        entry.getCrc(),
                        entry.getMethod(),
                        entry.getTime(),
                        entry.isDirectory()
                    );
                    
                    entries.put(name, apkEntry);
                    
                    // Categorize entries
                    if (name.equals("AndroidManifest.xml")) {
                        manifestPath = name;
                        logger.info("Found AndroidManifest.xml");
                    } else if (name.endsWith(".dex")) {
                        dexFiles.add(name);
                        logger.info("Found DEX file: " + name);
                    } else if (name.startsWith("lib/") && (name.endsWith(".so") || name.endsWith(".a"))) {
                        nativeLibraries.add(name);
                        logger.info("Found native library: " + name);
                    } else if (name.startsWith("res/") || name.startsWith("assets/") || 
                              name.startsWith("META-INF/") || name.endsWith(".arsc")) {
                        resources.add(name);
                    }
                    
                    zis.closeEntry();
                }
            }
            
            logger.info("APK parsing completed:");
            logger.info("  Total entries: " + entries.size());
            logger.info("  DEX files: " + dexFiles.size());
            logger.info("  Native libraries: " + nativeLibraries.size());
            logger.info("  Resources: " + resources.size());
            logger.info("  Manifest: " + (manifestPath != null ? "Found" : "Not found"));
            
            return true;
            
        } catch (Exception e) {
            logger.error("APK parsing failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract APK to directory
     */
    public boolean extractAPK(File apkFile, Path extractDir) {
        try {
            logger.info("Extracting APK to: " + extractDir.toString());
            
            Files.createDirectories(extractDir);
            
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
            
            logger.info("APK extraction completed");
            return true;
            
        } catch (Exception e) {
            logger.error("APK extraction failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get APK entry information
     */
    public APKEntry getEntry(String name) {
        return entries.get(name);
    }
    
    /**
     * Get all APK entries
     */
    public Map<String, APKEntry> getAllEntries() {
        return new HashMap<>(entries);
    }
    
    /**
     * Get DEX files
     */
    public List<String> getDexFiles() {
        return new ArrayList<>(dexFiles);
    }
    
    /**
     * Get native libraries
     */
    public List<String> getNativeLibraries() {
        return new ArrayList<>(nativeLibraries);
    }
    
    /**
     * Get resources
     */
    public List<String> getResources() {
        return new ArrayList<>(resources);
    }
    
    /**
     * Get manifest path
     */
    public String getManifestPath() {
        return manifestPath;
    }
    
    /**
     * Check if APK has specific entry
     */
    public boolean hasEntry(String name) {
        return entries.containsKey(name);
    }
    
    /**
     * Get APK statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_entries", entries.size());
        stats.put("dex_files", dexFiles.size());
        stats.put("native_libraries", nativeLibraries.size());
        stats.put("resources", resources.size());
        stats.put("has_manifest", manifestPath != null);
        
        long totalSize = 0;
        long totalCompressedSize = 0;
        for (APKEntry entry : entries.values()) {
            if (!entry.isDirectory) {
                totalSize += entry.size;
                totalCompressedSize += entry.compressedSize;
            }
        }
        
        stats.put("total_size", totalSize);
        stats.put("total_compressed_size", totalCompressedSize);
        stats.put("compression_ratio", totalSize > 0 ? (double) totalCompressedSize / totalSize : 0.0);
        
        return stats;
    }
    
    /**
     * Validate APK structure
     */
    public boolean validateAPK() {
        // Check for required files
        if (manifestPath == null) {
            logger.error("AndroidManifest.xml not found");
            return false;
        }
        
        if (dexFiles.isEmpty()) {
            logger.error("No DEX files found");
            return false;
        }
        
        // Check for classes.dex
        if (!dexFiles.contains("classes.dex")) {
            logger.error("classes.dex not found");
            return false;
        }
        
        logger.info("APK structure validation passed");
        return true;
    }
    
    /**
     * Get APK entry names
     */
    public Set<String> getEntryNames() {
        return new HashSet<>(entries.keySet());
    }
    
    /**
     * Check if entry is a directory
     */
    public boolean isDirectory(String name) {
        APKEntry entry = entries.get(name);
        return entry != null && entry.isDirectory;
    }
    
    /**
     * Get entry size
     */
    public long getEntrySize(String name) {
        APKEntry entry = entries.get(name);
        return entry != null ? entry.size : -1;
    }
}
