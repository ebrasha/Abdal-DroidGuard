/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : SimpleAuthorDisplay.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-24 13:45:22
 * Description  : Simplified author information display module
 * -------------------------------------------------------------------
 *
 * "Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming."
 * – Ebrahim Shafiei
 *
 **********************************************************************
 */

package com.ebrasha.droidguard.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simplified author display module without external dependencies
 */
public class SimpleAuthorDisplay {
    
    private static final String AUTHOR_NAME = "Ebrahim Shafiei (EbraSha)";
    private static final String AUTHOR_EMAIL = "Prof.Shafiei@Gmail.com";
    private static final String AUTHOR_QUOTE = "Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming.";
    private static final String PROJECT_NAME = "Abdal DroidGuard";
    private static final String VERSION = "1.0.0";
    
    /**
     * Display comprehensive author information at application startup
     */
    public static void displayAuthorInfo() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DEVELOPER INFORMATION                    ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                              ║");
        System.out.println("║  🛠️  Project: " + String.format("%-45s", PROJECT_NAME) + " ║");
        System.out.println("║  📦 Version: " + String.format("%-45s", VERSION) + " ║");
        System.out.println("║                                                              ║");
        System.out.println("║  👨‍💻 Developer: " + String.format("%-42s", AUTHOR_NAME) + " ║");
        System.out.println("║  📧 Email: " + String.format("%-46s", AUTHOR_EMAIL) + " ║");
        System.out.println("║                                                              ║");
        System.out.println("║  💬 Quote: " + String.format("%-45s", "\"" + AUTHOR_QUOTE + "\"") + " ║");
        System.out.println("║                                                              ║");
        System.out.println("║  🕒 Runtime: " + String.format("%-44s", getCurrentDateTime()) + " ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Get the current date and time in a formatted string
     * @return Current date and time
     */
    private static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    /**
     * Get author name
     * @return Author name
     */
    public static String getAuthorName() {
        return AUTHOR_NAME;
    }
    
    /**
     * Get author email
     * @return Author email
     */
    public static String getAuthorEmail() {
        return AUTHOR_EMAIL;
    }
    
    /**
     * Get project name
     * @return Project name
     */
    public static String getProjectName() {
        return PROJECT_NAME;
    }
    
    /**
     * Get version
     * @return Version string
     */
    public static String getVersion() {
        return VERSION;
    }
}
