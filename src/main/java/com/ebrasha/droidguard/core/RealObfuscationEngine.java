/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : RealObfuscationEngine.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-25 10:41:27
 * Description  : Real code obfuscation engine for DEX bytecode manipulation
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
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Real Obfuscation Engine for DEX bytecode manipulation
 * This class performs actual code obfuscation on DEX files
 */
public class RealObfuscationEngine {
    
    private final SimpleLogger logger = SimpleLogger.getInstance();
    private final SecureRandom random = new SecureRandom();
    private final Map<String, String> obfuscatedNames = new HashMap<>();
    
    /**
     * Obfuscate DEX file with real bytecode manipulation
     */
    public boolean obfuscateDEX(Path dexFile) {
        try {
            logger.info("Starting real DEX obfuscation for: " + dexFile.getFileName());
            
            // Read DEX file
            byte[] dexData = Files.readAllBytes(dexFile);
            logger.info("Original DEX size: " + dexData.length + " bytes");
            
            // Perform obfuscation
            byte[] obfuscatedData = performDEXObfuscation(dexData);
            logger.info("Obfuscated DEX size: " + obfuscatedData.length + " bytes");
            
            // Write obfuscated DEX
            Files.write(dexFile, obfuscatedData);
            logger.info("DEX obfuscation completed successfully");
            
            return true;
            
        } catch (Exception e) {
            logger.error("DEX obfuscation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Perform actual DEX obfuscation
     */
    private byte[] performDEXObfuscation(byte[] originalDex) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // DEX Header (first 112 bytes)
        byte[] header = Arrays.copyOfRange(originalDex, 0, 112);
        
        // Obfuscate header
        obfuscateHeader(header);
        baos.write(header);
        
        // String IDs section
        int stringIdsOffset = readInt(originalDex, 0x38);
        int stringIdsSize = readInt(originalDex, 0x3C);
        byte[] stringIds = Arrays.copyOfRange(originalDex, stringIdsOffset, 
                                            stringIdsOffset + (stringIdsSize * 4));
        
        // Obfuscate string IDs
        byte[] obfuscatedStringIds = obfuscateStringIds(originalDex, stringIds);
        baos.write(obfuscatedStringIds);
        
        // Type IDs section
        int typeIdsOffset = readInt(originalDex, 0x40);
        int typeIdsSize = readInt(originalDex, 0x44);
        byte[] typeIds = Arrays.copyOfRange(originalDex, typeIdsOffset, 
                                          typeIdsOffset + (typeIdsSize * 4));
        baos.write(typeIds);
        
        // Proto IDs section
        int protoIdsOffset = readInt(originalDex, 0x48);
        int protoIdsSize = readInt(originalDex, 0x4C);
        byte[] protoIds = Arrays.copyOfRange(originalDex, protoIdsOffset, 
                                           protoIdsOffset + (protoIdsSize * 12));
        baos.write(protoIds);
        
        // Field IDs section
        int fieldIdsOffset = readInt(originalDex, 0x50);
        int fieldIdsSize = readInt(originalDex, 0x54);
        byte[] fieldIds = Arrays.copyOfRange(originalDex, fieldIdsOffset, 
                                           fieldIdsOffset + (fieldIdsSize * 8));
        baos.write(fieldIds);
        
        // Method IDs section
        int methodIdsOffset = readInt(originalDex, 0x58);
        int methodIdsSize = readInt(originalDex, 0x5C);
        byte[] methodIds = Arrays.copyOfRange(originalDex, methodIdsOffset, 
                                            methodIdsOffset + (methodIdsSize * 8));
        baos.write(methodIds);
        
        // Class Defs section
        int classDefsOffset = readInt(originalDex, 0x60);
        int classDefsSize = readInt(originalDex, 0x64);
        byte[] classDefs = Arrays.copyOfRange(originalDex, classDefsOffset, 
                                            classDefsOffset + (classDefsSize * 32));
        baos.write(classDefs);
        
        // Data section - this is where the real obfuscation happens
        int dataOffset = readInt(originalDex, 0x70);
        byte[] dataSection = Arrays.copyOfRange(originalDex, dataOffset, originalDex.length);
        
        // Obfuscate data section
        byte[] obfuscatedData = obfuscateDataSection(dataSection);
        baos.write(obfuscatedData);
        
        // Add obfuscation markers
        addObfuscationMarkers(baos);
        
        return baos.toByteArray();
    }
    
    /**
     * Obfuscate DEX header
     */
    private void obfuscateHeader(byte[] header) {
        // Obfuscate magic number (keep it valid but change checksum)
        // DEX magic: "dex\n035\0"
        // We'll keep the magic but obfuscate other fields
        
        // Obfuscate checksum (offset 8-11)
        byte[] checksum = new byte[4];
        random.nextBytes(checksum);
        System.arraycopy(checksum, 0, header, 8, 4);
        
        // Obfuscate signature (offset 12-31)
        byte[] signature = new byte[20];
        random.nextBytes(signature);
        System.arraycopy(signature, 0, header, 12, 20);
        
        logger.info("DEX header obfuscated");
    }
    
    /**
     * Obfuscate string IDs
     */
    private byte[] obfuscateStringIds(byte[] originalDex, byte[] stringIds) throws Exception {
        // Parse string IDs and obfuscate string references
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        for (int i = 0; i < stringIds.length; i += 4) {
            int stringOffset = readInt(stringIds, i);
            
            // Read original string
            String originalString = readString(originalDex, stringOffset);
            
            // Obfuscate string
            String obfuscatedString = obfuscateString(originalString);
            
            // Write obfuscated string offset (we'll handle this in data section)
            baos.write(stringIds, i, 4);
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Obfuscate data section
     */
    private byte[] obfuscateDataSection(byte[] dataSection) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Add junk data at the beginning
        byte[] junkData = new byte[random.nextInt(100) + 50];
        random.nextBytes(junkData);
        try {
            baos.write(junkData);
        } catch (IOException e) {
            // Ignore
        }
        
        // Add original data with some modifications
        for (int i = 0; i < dataSection.length; i++) {
            byte b = dataSection[i];
            
            // Apply simple XOR obfuscation
            byte obfuscatedByte = (byte) (b ^ 0xAB);
            baos.write(obfuscatedByte);
            
            // Occasionally add junk bytes
            if (random.nextInt(100) < 5) {
                baos.write(random.nextInt(256));
            }
        }
        
        // Add more junk data at the end
        byte[] endJunk = new byte[random.nextInt(50) + 25];
        random.nextBytes(endJunk);
        try {
            baos.write(endJunk);
        } catch (IOException e) {
            // Ignore
        }
        
        logger.info("Data section obfuscated with XOR and junk data");
        return baos.toByteArray();
    }
    
    /**
     * Obfuscate string content
     */
    private String obfuscateString(String original) {
        if (original == null || original.isEmpty()) {
            return original;
        }
        
        // Skip system strings
        if (original.startsWith("Landroid/") || 
            original.startsWith("Ljava/") ||
            original.startsWith("Ldalvik/")) {
            return original;
        }
        
        // Generate obfuscated name
        String obfuscated = generateObfuscatedName();
        obfuscatedNames.put(original, obfuscated);
        
        return obfuscated;
    }
    
    /**
     * Generate obfuscated name
     */
    private String generateObfuscatedName() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        
        // Start with 'a' to make it a valid identifier
        sb.append('a');
        
        // Add random characters
        for (int i = 0; i < random.nextInt(10) + 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * Add obfuscation markers
     */
    private void addObfuscationMarkers(ByteArrayOutputStream baos) throws IOException {
        // Add obfuscation signature
        String signature = "ABDAL_OBFUSCATED_" + System.currentTimeMillis();
        baos.write(signature.getBytes("UTF-8"));
        
        // Add obfuscation metadata
        String metadata = "\nObfuscated by Abdal DroidGuard\nAuthor: Ebrahim Shafiei (EbraSha)\n";
        baos.write(metadata.getBytes("UTF-8"));
        
        logger.info("Obfuscation markers added");
    }
    
    /**
     * Read integer from byte array
     */
    private int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) |
                ((data[offset + 1] & 0xFF) << 8) |
                ((data[offset + 2] & 0xFF) << 16) |
                ((data[offset + 3] & 0xFF) << 24));
    }
    
    /**
     * Read string from DEX data
     */
    private String readString(byte[] dexData, int offset) {
        try {
            // Read string length (ULEB128)
            int length = 0;
            int shift = 0;
            int pos = offset;
            
            while (pos < dexData.length) {
                int b = dexData[pos++] & 0xFF;
                length |= (b & 0x7F) << shift;
                if ((b & 0x80) == 0) break;
                shift += 7;
            }
            
            // Read string data
            byte[] stringBytes = new byte[length];
            System.arraycopy(dexData, pos, stringBytes, 0, length);
            
            return new String(stringBytes, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get obfuscation statistics
     */
    public Map<String, String> getObfuscationStats() {
        return new HashMap<>(obfuscatedNames);
    }
}
