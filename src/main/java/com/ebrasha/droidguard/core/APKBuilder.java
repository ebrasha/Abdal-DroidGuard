/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : APKBuilder.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-15 09:23:47
 * Description  : APK Builder for reconstructing APK with proper structure
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
 * APK Builder for reconstructing APK with proper structure
 * This class handles APK reconstruction with proper binary manifest handling
 */
public class APKBuilder {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    private final Set<String> addedEntries = new HashSet<>();
    
    /**
     * Build APK from extracted directory
     */
    public boolean buildAPK(File originalAPK, Path extractedDir, Path outputAPK) {
        try {
            logger.info("Building APK from extracted directory...");
            logger.info("Original APK: " + originalAPK.getAbsolutePath());
            logger.info("Extracted directory: " + extractedDir.toString());
            logger.info("Output APK: " + outputAPK.toString());
            
            // Clear added entries
            addedEntries.clear();
            
            // Build APK with proper structure
            buildAPKWithStructure(originalAPK, extractedDir, outputAPK);
            
            logger.info("APK building completed successfully");
            return true;
            
        } catch (Exception e) {
            logger.error("APK building failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Build APK with proper structure
     */
    private void buildAPKWithStructure(File originalAPK, Path extractedDir, Path outputAPK) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(originalAPK));
             ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputAPK.toFile()))) {

            // 1) Copy originals unless overridden in extractedDir
            copyOriginalEntries(zis, zos, extractedDir); // keep resources.arsc, res/*, classes*.dex, etc.

            // 2) Add modified/new files (except manifest)
            addModifiedFiles(extractedDir, zos);

            // 3) Manifest last, copied as-is (binary)
            ensureProperManifest(extractedDir, zos);

            // 4) Add hardening markers under assets/ or META-INF/, still within this SAME build
            addHardeningMarkers(extractedDir, zos);
        }
    }
    
    /**
     * Copy original entries from APK
     */
    private void copyOriginalEntries(ZipInputStream zis, ZipOutputStream zos, Path extractedDir) throws Exception {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String name = entry.getName();
            
            // Skip manifest - we'll handle it separately
            if (name.equals("AndroidManifest.xml")) {
                zis.closeEntry();
                continue;
            }
            
            // Check if we have a modified version
            Path modifiedFile = extractedDir.resolve(name);
            if (Files.exists(modifiedFile) && Files.isRegularFile(modifiedFile)) {
                // Skip original, we'll add modified version later
                zis.closeEntry();
                continue;
            }
            
                // Copy original entry safely
                ZipEntry newEntry = new ZipEntry(name);
                newEntry.setMethod(entry.getMethod());
                newEntry.setTime(entry.getTime());
                
                // Only set CRC/size if they are valid (not -1)
                if (entry.getCrc() != -1) {
                    newEntry.setCrc(entry.getCrc());
                }
                if (entry.getSize() != -1) {
                    newEntry.setSize(entry.getSize());
                }
                if (entry.getCompressedSize() != -1) {
                    newEntry.setCompressedSize(entry.getCompressedSize());
                }
                
                zos.putNextEntry(newEntry);
                
                // Copy data
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = zis.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }
                
                zos.closeEntry();
                addedEntries.add(name);
                zis.closeEntry();
        }
    }
    
    /**
     * Add modified files from extracted directory
     */
    private void addModifiedFiles(Path extractedDir, ZipOutputStream zos) throws Exception {
        Files.walk(extractedDir)
            .filter(Files::isRegularFile)
            .forEach(path -> {
                try {
                    String entryName = extractedDir.relativize(path).toString().replace("\\", "/");
                    
                    // Skip manifest - we'll handle it separately
                    if (entryName.equals("AndroidManifest.xml")) {
                        return;
                    }
                    
                    // Skip if already added
                    if (addedEntries.contains(entryName)) {
                        return;
                    }
                    
                    // Only add if it's a modified file (like classes.dex)
                    if (isModifiedFile(entryName)) {
                        addFileAsStored(zos, path, entryName);
                        addedEntries.add(entryName);
                    }
                    
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
    
    /**
     * Ensure proper manifest
     */
    private void ensureProperManifest(Path extractedDir, ZipOutputStream zos) throws Exception {
        Path manifest = extractedDir.resolve("AndroidManifest.xml");
        if (!Files.exists(manifest)) {
            // Fail fast: never synthesize a fake binary manifest
            throw new IllegalStateException("Missing AndroidManifest.xml in extracted APK");
        }
        // Just write the existing (binary) manifest back into the zip.
        logger.info("Adding preserved binary AndroidManifest.xml");
        ZipEntry e = new ZipEntry("AndroidManifest.xml");
        zos.putNextEntry(e);
        Files.copy(manifest, zos);
        zos.closeEntry();
    }
    
    
    /**
     * Check if file is modified
     */
    private boolean isModifiedFile(String relativePath) {
        // Only classes.dex and other modified files
        return relativePath.startsWith("classes") && relativePath.endsWith(".dex");
    }
    
    /**
     * Add hardening markers
     */
    private void addHardeningMarkers(Path extractedDir, ZipOutputStream zos) throws Exception {
        // Add hardening markers to assets/
        String markerContent = "ABDAL_HARDENING_MARKER_" + System.currentTimeMillis();
        addTextAsStored(zos, markerContent, "assets/abdal_hardening.txt");
        
        // Add protection info
        String protectionInfo = "Protected by Abdal DroidGuard\nAuthor: Ebrahim Shafiei (EbraSha)\nEmail: Prof.Shafiei@Gmail.com";
        addTextAsStored(zos, protectionInfo, "META-INF/ABDAL_PROTECTION.txt");
        
        logger.info("Hardening markers added");
    }
    
    /**
     * Check if manifest is binary format
     */
    private boolean isBinaryManifest(Path manifestPath) {
        try {
            byte[] data = Files.readAllBytes(manifestPath);
            return isBinaryManifestBytes(data);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * New helper: detect binary XML by bytes (no fallback to fake binary).
     */
    private boolean isBinaryManifestBytes(byte[] data) {
        if (data.length < 4) return false;
        // Android AXML magic: 0x00080003 (little-endian => 03 00 08 00)
        return (data[0] & 0xFF) == 0x03 &&
               (data[1] & 0xFF) == 0x00 &&
               (data[2] & 0xFF) == 0x08 &&
               (data[3] & 0xFF) == 0x00;
    }
    
    /**
     * Create binary manifest from text manifest
     */
    private void createBinaryManifest(Path textManifest, ZipOutputStream zos) throws Exception {
        // Read text manifest
        String textContent = new String(Files.readAllBytes(textManifest), "UTF-8");
        
        // Create binary manifest
        byte[] binaryManifest = convertTextToBinaryManifest(textContent);
        
        // Add to APK
        ZipEntry manifestEntry = new ZipEntry("AndroidManifest.xml");
        zos.putNextEntry(manifestEntry);
        zos.write(binaryManifest);
        zos.closeEntry();
    }
    
    /**
     * Convert text manifest to binary manifest
     */
    private byte[] convertTextToBinaryManifest(String textContent) throws Exception {
        throw new UnsupportedOperationException("Use aapt2 to compile manifest. Hand-rolled binary XML is not supported.");
    }
    
    /**
     * Create minimal valid binary manifest
     */
    private byte[] createMinimalValidBinaryManifest() throws Exception {
        // Create a minimal but valid binary XML manifest
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Binary XML header (0x00080003)
        baos.write(0x00);
        baos.write(0x08);
        baos.write(0x00);
        baos.write(0x03);
        
        // File size (will be calculated later)
        int fileSize = 0;
        baos.write((fileSize >> 24) & 0xFF);
        baos.write((fileSize >> 16) & 0xFF);
        baos.write((fileSize >> 8) & 0xFF);
        baos.write(fileSize & 0xFF);
        
        // String pool header (0x00080001)
        baos.write(0x00);
        baos.write(0x08);
        baos.write(0x00);
        baos.write(0x01);
        
        // String pool size (will be calculated)
        int stringPoolSize = 0;
        baos.write((stringPoolSize >> 24) & 0xFF);
        baos.write((stringPoolSize >> 16) & 0xFF);
        baos.write((stringPoolSize >> 8) & 0xFF);
        baos.write(stringPoolSize & 0xFF);
        
        // String count (1)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x01);
        
        // Style count (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Flags (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // String data offset (0x20)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x20);
        
        // Style data offset (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // String data: "manifest"
        String manifestStr = "manifest";
        byte[] strBytes = manifestStr.getBytes("UTF-8");
        baos.write(strBytes);
        baos.write(0x00); // Null terminator
        
        // Resource map (0x00080080)
        baos.write(0x00);
        baos.write(0x08);
        baos.write(0x00);
        baos.write(0x80);
        
        // Resource map size (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Start tag (0x00080010)
        baos.write(0x00);
        baos.write(0x08);
        baos.write(0x00);
        baos.write(0x10);
        
        // Start tag size (0x14)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x14);
        
        // Line number (1)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x01);
        
        // Comment index (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Namespace URI index (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Name index (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Flags (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Attribute count (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Class attribute index (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // End tag (0x00080011)
        baos.write(0x00);
        baos.write(0x08);
        baos.write(0x00);
        baos.write(0x11);
        
        // End tag size (0x14)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x14);
        
        // Line number (1)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x01);
        
        // Comment index (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Namespace URI index (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        // Name index (0)
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        baos.write(0x00);
        
        return baos.toByteArray();
    }
    
    /**
     * Create minimal text manifest for testing
     */
    private void createMinimalTextManifest(ZipOutputStream zos) throws Exception {
        throw new UnsupportedOperationException("Minimal manifest creation disabled. Preserve original binary manifest.");
    }
    
    /**
     * Add protection files to APK
     */
    private void addProtectionFilesToAPK(Path extractedDir, Path outputAPK) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputAPK.toFile(), true))) {
            // Add hardening info
            ZipEntry hardeningEntry = new ZipEntry("META-INF/ABDAL_HARDENING.txt");
            zos.putNextEntry(hardeningEntry);
            
            String hardeningInfo = "ABDAL_DROIDGUARD_HARDENING\n" +
                                  "Timestamp: " + System.currentTimeMillis() + "\n" +
                                  "Version: 1.0.0\n" +
                                  "Author: Ebrahim Shafiei (EbraSha)\n" +
                                  "Email: Prof.Shafiei@Gmail.com\n" +
                                  "Hardening Features:\n" +
                                  "- Code Obfuscation\n" +
                                  "- Tamper Detection\n" +
                                  "- RASP Protection\n" +
                                  "- APK Signing\n" +
                                  "- APK Alignment\n" +
                                  "Generated by Abdal DroidGuard";
            
            zos.write(hardeningInfo.getBytes("UTF-8"));
            zos.closeEntry();
            
            logger.info("Protection files added to APK");
        }
    }
    
    /**
     * Add file as STORED with proper CRC calculation
     */
    private void addFileAsStored(ZipOutputStream zos, Path filePath, String entryName) throws IOException {
        byte[] data = Files.readAllBytes(filePath);
        addDataAsStored(zos, data, entryName);
    }
    
    /**
     * Add text as STORED with proper CRC calculation
     */
    private void addTextAsStored(ZipOutputStream zos, String text, String entryName) throws IOException {
        byte[] data = text.getBytes("UTF-8");
        addDataAsStored(zos, data, entryName);
    }
    
    /**
     * Add data as STORED with proper CRC calculation
     */
    private void addDataAsStored(ZipOutputStream zos, byte[] data, String entryName) throws IOException {
        CRC32 crc = new CRC32();
        crc.update(data);
        ZipEntry entry = new ZipEntry(entryName);
        entry.setMethod(ZipEntry.STORED);
        entry.setSize(data.length);
        entry.setCompressedSize(data.length);
        entry.setCrc(crc.getValue());
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
    
    /**
     * Get build statistics
     */
    public Map<String, String> getBuildStats() {
        Map<String, String> stats = new HashMap<>();
        stats.put("total_entries", String.valueOf(addedEntries.size()));
        stats.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return stats;
    }
    
    /**
     * Get added entries
     */
    public Set<String> getAddedEntries() {
        return new HashSet<>(addedEntries);
    }
}
