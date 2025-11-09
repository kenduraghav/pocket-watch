package io.pocketwatch.constants;

/**
 * Common date/time pattern constants for use with validation annotations.
 * 
 * These patterns follow Java DateTimeFormatter syntax.
 * 
 * @author Raghav 
 * @since 1.0.0
 */
public final class DatePattern {
    
    private DatePattern() {
        throw new AssertionError("Cannot instantiate constants class");
    }
    
    // ========== ISO Patterns ==========
    
    /**
     * ISO date format: 2025-11-04
     */
    public static final String ISO_DATE = "yyyy-MM-dd";
    
    /**
     * ISO date-time format: 2025-11-04T14:30:00
     */
    public static final String ISO_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
    
    /**
     * ISO date-time with timezone: 2025-11-04T14:30:00+05:30
     */
    public static final String ISO_ZONED_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ssXXX";
    
    // ========== US Patterns ==========
    
    /**
     * US date format: 11/04/2025
     */
    public static final String US_DATE = "MM/dd/yyyy";
    
    /**
     * US date-time format: 11/04/2025 02:30 PM
     */
    public static final String US_DATE_TIME = "MM/dd/yyyy hh:mm a";
    
    /**
     * US date-time with seconds: 11/04/2025 02:30:45 PM
     */
    public static final String US_DATE_TIME_SECONDS = "MM/dd/yyyy hh:mm:ss a";
    
    // ========== EU Patterns ==========
    
    /**
     * EU/UK date format: 04/11/2025
     */
    public static final String EU_DATE = "dd/MM/yyyy";
    
    /**
     * EU date-time format: 04/11/2025 14:30
     */
    public static final String EU_DATE_TIME = "dd/MM/yyyy HH:mm";
    
    /**
     * EU date-time with seconds: 04/11/2025 14:30:45
     */
    public static final String EU_DATE_TIME_SECONDS = "dd/MM/yyyy HH:mm:ss";
    
    // ========== Common Patterns ==========
    
    /**
     * Simple date: 04-Nov-2025
     */
    public static final String SIMPLE_DATE = "dd-MMM-yyyy";
    
    /**
     * Long date: Monday, November 04, 2025
     */
    public static final String LONG_DATE = "EEEE, MMMM dd, yyyy";
    
    /**
     * Medium date: Nov 04, 2025
     */
    public static final String MEDIUM_DATE = "MMM dd, yyyy";
    
    /**
     * Short date with dashes: 04-11-2025
     */
    public static final String SHORT_DATE_DASH = "dd-MM-yyyy";
    
    /**
     * Short date with dots: 04.11.2025
     */
    public static final String SHORT_DATE_DOT = "dd.MM.yyyy";
    
    // ========== Time-Only Patterns ==========
    
    /**
     * 24-hour time: 14:30
     */
    public static final String TIME_24H = "HH:mm";
    
    /**
     * 24-hour time with seconds: 14:30:45
     */
    public static final String TIME_24H_SECONDS = "HH:mm:ss";
    
    /**
     * 12-hour time: 02:30 PM
     */
    public static final String TIME_12H = "hh:mm a";
    
    /**
     * 12-hour time with seconds: 02:30:45 PM
     */
    public static final String TIME_12H_SECONDS = "hh:mm:ss a";
    
    // ========== Database/Timestamp Patterns ==========
    
    /**
     * Database timestamp: 2025-11-04 14:30:45
     */
    public static final String DB_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * Compact timestamp: 20251104143045
     */
    public static final String COMPACT_TIMESTAMP = "yyyyMMddHHmmss";
    
    /**
     * Unix-friendly timestamp: 2025-11-04_14-30-45
     */
    public static final String UNIX_TIMESTAMP = "yyyy-MM-dd_HH-mm-ss";
    
    // ========== Custom/Special Patterns ==========
    
    /**
     * Year and month only: 2025-11
     */
    public static final String YEAR_MONTH = "yyyy-MM";
    
    /**
     * Month and day only: Nov 04
     */
    public static final String MONTH_DAY = "MMM dd";
    
    /**
     * Year only: 2025
     */
    public static final String YEAR = "yyyy";
}