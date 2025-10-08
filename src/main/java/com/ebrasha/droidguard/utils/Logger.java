/*
 **********************************************************************
 * -------------------------------------------------------------------
 * Project Name : Abdal DroidGuard
 * File Name    : Logger.java
 * Author       : Ebrahim Shafiei (EbraSha)
 * Email        : Prof.Shafiei@Gmail.com
 * Created On   : 2025-08-19 12:41:53
 * Description  : Custom logging utility for Abdal DroidGuard application
 * -------------------------------------------------------------------
 *
 * "Coding is an engaging and beloved hobby for me. I passionately and insatiably pursue knowledge in cybersecurity and programming."
 * – Ebrahim Shafiei
 *
 **********************************************************************
 */

package com.ebrasha.droidguard.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom logging utility for Abdal DroidGuard
 * Provides formatted logging with different levels and verbose mode support
 */
public class Logger {
    
    private static Logger instance;
    private boolean verbose = false;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private Logger() {
        // Private constructor for singleton pattern
    }
    
    /**
     * Get singleton instance of Logger
     * @return Logger instance
     */
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    /**
     * Set verbose mode
     * @param verbose True to enable verbose logging
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    /**
     * Log info message
     * @param message Message to log
     */
    public void info(String message) {
        log("INFO", message, false);
    }
    
    /**
     * Log warning message
     * @param message Message to log
     */
    public void warn(String message) {
        log("WARN", message, false);
    }
    
    /**
     * Log error message
     * @param message Message to log
     */
    public void error(String message) {
        log("ERROR", message, false);
    }
    
    /**
     * Log debug message (only in verbose mode)
     * @param message Message to log
     */
    public void debug(String message) {
        if (verbose) {
            log("DEBUG", message, false);
        }
    }
    
    /**
     * Log verbose message (only in verbose mode)
     * @param message Message to log
     */
    public void verbose(String message) {
        if (verbose) {
            log("VERBOSE", message, false);
        }
    }
    
    /**
     * Log success message
     * @param message Message to log
     */
    public void success(String message) {
        log("SUCCESS", message, true);
    }
    
    /**
     * Log progress message
     * @param message Message to log
     */
    public void progress(String message) {
        log("PROGRESS", message, true);
    }
    
    /**
     * Internal logging method
     * @param level Log level
     * @param message Message to log
     * @param useEmoji Whether to use emoji prefix
     */
    private void log(String level, String message, boolean useEmoji) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        String prefix = getPrefix(level, useEmoji);
        System.out.println(String.format("[%s] %s %s", timestamp, prefix, message));
    }
    
    /**
     * Get prefix for log level
     * @param level Log level
     * @param useEmoji Whether to use emoji
     * @return Formatted prefix
     */
    private String getPrefix(String level, boolean useEmoji) {
        if (useEmoji) {
            switch (level) {
                case "SUCCESS":
                    return "✅";
                case "PROGRESS":
                    return "🔄";
                case "INFO":
                    return "ℹ️";
                case "WARN":
                    return "⚠️";
                case "ERROR":
                    return "❌";
                case "DEBUG":
                    return "🐛";
                case "VERBOSE":
                    return "🔍";
                default:
                    return "📝";
            }
        } else {
            return String.format("[%s]", level);
        }
    }
    
    /**
     * Log a separator line
     */
    public void separator() {
        System.out.println("─".repeat(60));
    }
    
    /**
     * Log a header
     * @param title Header title
     */
    public void header(String title) {
        System.out.println();
        System.out.println("═".repeat(60));
        System.out.println("  " + title);
        System.out.println("═".repeat(60));
    }
    
    /**
     * Log a subheader
     * @param title Subheader title
     */
    public void subheader(String title) {
        System.out.println();
        System.out.println("─ " + title + " ─");
    }
}
