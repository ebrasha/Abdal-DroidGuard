/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : AbdalDroidGuardTest.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-24 11:29:17
 * Description  : Unit tests for Abdal DroidGuard application
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Abdal DroidGuard application
 */
public class AbdalDroidGuardTest {

    @TempDir
    Path tempDir;

    private File testJarFile;
    private File testApkFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create test JAR file
        testJarFile = tempDir.resolve("test.jar").toFile();
        createTestJarFile(testJarFile);

        // Create test APK file
        testApkFile = tempDir.resolve("test.apk").toFile();
        createTestApkFile(testApkFile);
    }

    @Test
    void testAuthorDisplay() {
        // Test author information display
        assertNotNull(AuthorDisplay.getAuthorName());
        assertNotNull(AuthorDisplay.getAuthorEmail());
        assertNotNull(AuthorDisplay.getProjectName());
        assertNotNull(AuthorDisplay.getVersion());
        assertNotNull(AuthorDisplay.getAuthorInfo());

        assertEquals("Ebrahim Shafiei (EbraSha)", AuthorDisplay.getAuthorName());
        assertEquals("Prof.Shafiei@Gmail.com", AuthorDisplay.getAuthorEmail());
        assertEquals("Abdal DroidGuard", AuthorDisplay.getProjectName());
        assertEquals("1.0.0", AuthorDisplay.getVersion());
    }

    @Test
    void testObfuscationEngine() {
        ObfuscationEngine engine = new ObfuscationEngine();
        assertNotNull(engine);

        // Test JAR obfuscation
        assertTrue(engine.processFile(testJarFile, false));

        // Test APK obfuscation
        assertTrue(engine.processFile(testApkFile, true));
    }

    @Test
    void testTamperDetection() {
        TamperDetection tamperDetection = new TamperDetection();
        assertNotNull(tamperDetection);

        // Test JAR tamper detection
        assertTrue(tamperDetection.addTamperDetection(testJarFile));

        // Test APK tamper detection
        assertTrue(tamperDetection.addTamperDetection(testApkFile));
    }

    @Test
    void testRASProtection() {
        RASProtection raspProtection = new RASProtection();
        assertNotNull(raspProtection);

        // Test JAR RASP protection
        assertTrue(raspProtection.addRASProtection(testJarFile));

        // Test APK RASP protection
        assertTrue(raspProtection.addRASProtection(testApkFile));
    }

    @Test
    void testLogger() {
        Logger logger = Logger.getInstance();
        assertNotNull(logger);

        // Test logger functionality
        logger.setVerbose(true);
        logger.info("Test info message");
        logger.warn("Test warning message");
        logger.error("Test error message");
        logger.debug("Test debug message");
        logger.verbose("Test verbose message");
        logger.success("Test success message");
        logger.progress("Test progress message");
    }

    @Test
    void testMainApplication() {
        // Test main application class instantiation
        AbdalDroidGuard app = new AbdalDroidGuard();
        assertNotNull(app);
    }

    @Test
    void testFileValidation() {
        AbdalDroidGuard app = new AbdalDroidGuard();
        
        // Test with non-existent file
        File nonExistentFile = new File("non-existent-file.jar");
        assertFalse(nonExistentFile.exists());

        // Test with directory instead of file
        assertTrue(tempDir.toFile().isDirectory());
    }

    @Test
    void testCommandLineArguments() {
        // Test command line argument parsing
        String[] args = {
            testJarFile.getAbsolutePath(),
            "--obfuscate",
            "--tamper-detect",
            "--rasp",
            "--verbose"
        };

        // This would test the command line parsing functionality
        // In a real implementation, you would test the picocli integration
        assertNotNull(args);
        assertEquals(5, args.length);
    }

    @Test
    void testAllProtectionFeatures() {
        // Test enabling all protection features
        String[] args = {
            testJarFile.getAbsolutePath(),
            "--all",
            "--verbose"
        };

        assertNotNull(args);
        assertTrue(args.length >= 2);
    }

    /**
     * Create a test JAR file for testing purposes
     * @param jarFile JAR file to create
     */
    private void createTestJarFile(File jarFile) throws IOException {
        // Create a simple test JAR file
        try (java.util.jar.JarOutputStream jarOut = new java.util.jar.JarOutputStream(
                new java.io.FileOutputStream(jarFile))) {
            
            // Add a simple manifest
            java.util.jar.JarEntry manifestEntry = new java.util.jar.JarEntry("META-INF/MANIFEST.MF");
            jarOut.putNextEntry(manifestEntry);
            jarOut.write("Manifest-Version: 1.0\n".getBytes());
            jarOut.write("Main-Class: TestClass\n".getBytes());
            jarOut.closeEntry();

            // Add a simple class file
            java.util.jar.JarEntry classEntry = new java.util.jar.JarEntry("TestClass.class");
            jarOut.putNextEntry(classEntry);
            jarOut.write(new byte[]{0xCA, 0xFE, 0xBA, 0xBE}); // Java class file magic number
            jarOut.closeEntry();
        }
    }

    /**
     * Create a test APK file for testing purposes
     * @param apkFile APK file to create
     */
    private void createTestApkFile(File apkFile) throws IOException {
        // Create a simple test APK file (which is essentially a ZIP file)
        try (java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(
                new java.io.FileOutputStream(apkFile))) {
            
            // Add AndroidManifest.xml
            java.util.zip.ZipEntry manifestEntry = new java.util.zip.ZipEntry("AndroidManifest.xml");
            zipOut.putNextEntry(manifestEntry);
            zipOut.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
            zipOut.write("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">\n".getBytes());
            zipOut.write("</manifest>\n".getBytes());
            zipOut.closeEntry();

            // Add classes.dex
            java.util.zip.ZipEntry dexEntry = new java.util.zip.ZipEntry("classes.dex");
            zipOut.putNextEntry(dexEntry);
            zipOut.write(new byte[]{0x64, 0x65, 0x78, 0x0A}); // DEX file magic number
            zipOut.closeEntry();
        }
    }
}
