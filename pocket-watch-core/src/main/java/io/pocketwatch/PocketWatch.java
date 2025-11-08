package io.pocketwatch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Core PocketWatch API - Minimal version for validation
 */
public final class PocketWatch {

	private final ZonedDateTime dateTime;

	private PocketWatch(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	
	
	/**
     * Parse date string with pattern
     * Returns null if parsing fails (for validation)
     */
	public static PocketWatch parse(String dateString, String pattern) {
	    if (dateString == null || dateString.isBlank()) {
	        return null;
	    }
	    
	    try {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	        
	        // Try LocalDate first (for date-only patterns)
	        try {
	            LocalDate localDate = java.time.LocalDate.parse(dateString, formatter);
	            ZonedDateTime zdt = localDate.atStartOfDay(ZoneId.systemDefault());
	            return new PocketWatch(zdt);
	        } catch (DateTimeParseException e) {
	            // Try LocalDateTime
	            try {
	                LocalDateTime localDateTime = java.time.LocalDateTime.parse(dateString, formatter);
	                ZonedDateTime zdt = localDateTime.atZone(java.time.ZoneId.systemDefault());
	                return new PocketWatch(zdt);
	            } catch (DateTimeParseException e2) {
	                // Try ZonedDateTime
	                ZonedDateTime zdt = ZonedDateTime.parse(dateString, formatter);
	                return new PocketWatch(zdt);
	            }
	        }
	    } catch (DateTimeParseException e) {
	        return null;  // Invalid date
	    }
	}
	
	
    public ZonedDateTime toZonedDateTime() {
        return dateTime;
    }
}
