/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : AuthorDisplay.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-20 14:33:26
 * Description  : Author information display module for runtime identification
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
 * Author display module that shows developer information during runtime
 * This ensures proper attribution and identification of the tool's creator
 */
public class AuthorDisplay {
    
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
     * Display a compact author signature
     */
    public static void displayAuthorSignature() {
        System.out.println("🔐 " + PROJECT_NAME + " v" + VERSION + " | Developed by " + AUTHOR_NAME);
        System.out.println("📧 Contact: " + AUTHOR_EMAIL);
    }
    
    /**
     * Get author information as a formatted string
     * @return Formatted author information
     */
    public static String getAuthorInfo() {
        return String.format(
            "Project: %s v%s\nDeveloper: %s\nEmail: %s\nQuote: \"%s\"\nRuntime: %s",
            PROJECT_NAME, VERSION, AUTHOR_NAME, AUTHOR_EMAIL, AUTHOR_QUOTE, getCurrentDateTime()
        );
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
     * Display author information in a minimal format
     */
    public static void displayMinimalInfo() {
        System.out.println("🔐 " + PROJECT_NAME + " | " + AUTHOR_NAME + " | " + AUTHOR_EMAIL);
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
