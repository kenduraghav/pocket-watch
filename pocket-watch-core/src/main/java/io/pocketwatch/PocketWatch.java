package io.pocketwatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import io.pocketwatch.constants.DatePattern;

/**
 * Core PocketWatch API - Minimal version for validation
 */
public final class PocketWatch {

	private final ZonedDateTime dateTime;

	// Private constructor - use factory methods
	private PocketWatch(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Parse date string with given pattern
	 * 
	 * @param dateString the date string to parse
	 * @param pattern    the pattern (e.g., "yyyy-MM-dd")
	 * @return PocketWatch instance
	 * @return null if parsing fails
	 */
	public static PocketWatch parse(String dateString, String pattern) {
		if (dateString == null || dateString.isBlank()) {
			throw new IllegalArgumentException("Date string cannot be null or blank");
		}

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

			 // Try ZonedDateTime first (most complete)
	        try {
	            ZonedDateTime zdt = ZonedDateTime.parse(dateString, formatter);
	            return new PocketWatch(zdt);
	        } catch (DateTimeParseException e1) {
	            // Try LocalDateTime (has time but no zone)
	            try {
	                LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
	                ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
	                return new PocketWatch(zdt);
	            } catch (DateTimeParseException e2) {
	                // Try LocalDate (date only)
	                LocalDate localDate = LocalDate.parse(dateString, formatter);
	                ZonedDateTime zdt = localDate.atStartOfDay(ZoneId.systemDefault());
	                return new PocketWatch(zdt);
	            }
	        }
		} catch (DateTimeParseException e) {
			 throw new IllegalArgumentException("Cannot parse '" + dateString + 
		                "' with pattern '" + pattern + "'", e);
		}
	}
	
	
	public static Optional<PocketWatch> tryParse(String date, String pattern) {
	    try {
	        return Optional.of(parse(date, pattern));
	    } catch (IllegalArgumentException e) {
	        return Optional.empty();
	    }
	}
	
	
	/**
	 * Parse date string with default ISO date pattern (yyyy-MM-dd)
	 */
	public static PocketWatch parse(String dateString) {
		return parse(dateString, DatePattern.ISO_DATE);
	}
	
	
	/**
     * Get current date/time
     */
    public static PocketWatch now() {
        return new PocketWatch(ZonedDateTime.now());
    }
    
    /**
     * Get current date/time in specific timezone
     */
    public static PocketWatch nowIn(String zoneId) {
        return new PocketWatch(ZonedDateTime.now(ZoneId.of(zoneId)));
    }
    
    /**
     * Create PocketWatch from ZonedDateTime
     */
    public static PocketWatch of(ZonedDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("ZonedDateTime cannot be null");
        }
        return new PocketWatch(dateTime);
    }
    
    
    /**
     * Create PocketWatch from LocalDate (uses system default timezone)
     */
    public static PocketWatch of(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("LocalDate cannot be null");
        }
        return new PocketWatch(date.atStartOfDay(ZoneId.systemDefault()));
    }
    
    /**
     * Create PocketWatch from LocalDateTime (uses system default timezone)
     */
    public static PocketWatch of(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("LocalDateTime cannot be null");
        }
        return new PocketWatch(dateTime.atZone(ZoneId.systemDefault()));
    }
    
    /**
     * Convert to LocalDate (date only, no time)
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }
    
    /**
     * Convert to LocalDateTime (date + time, no timezone)
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime.toLocalDateTime();
    }
    
    /**
     * Convert to ZonedDateTime (date + time + timezone)
     */
    public ZonedDateTime toZonedDateTime() {
        return dateTime;
    }
    
    /**
     * Convert to Instant (UTC timestamp)
     */
    public Instant toInstant() {
        return dateTime.toInstant();
    }
    
    /**
     * Convert to epoch milliseconds
     */
    public long toEpochMilli() {
        return dateTime.toInstant().toEpochMilli();
    }
    
    // ========== OPERATIONS (Fluent - Returns PocketWatch) ==========
    
    /**
     * Add days (immutable - returns new instance)
     */
    public PocketWatch plusDays(int days) {
        return new PocketWatch(dateTime.plusDays(days));
    }
    
    /**
     * Add hours
     */
    public PocketWatch plusHours(int hours) {
        return new PocketWatch(dateTime.plusHours(hours));
    }
    
    /**
     * Add minutes
     */
    public PocketWatch plusMinutes(int minutes) {
        return new PocketWatch(dateTime.plusMinutes(minutes));
    }
    
    /**
     * Add months
     */
    public PocketWatch plusMonths(int months) {
        return new PocketWatch(dateTime.plusMonths(months));
    }
    
    /**
     * Add years
     */
    public PocketWatch plusYears(int years) {
        return new PocketWatch(dateTime.plusYears(years));
    }
    
    /**
     * Subtract days
     */
    public PocketWatch minusDays(int days) {
        return new PocketWatch(dateTime.minusDays(days));
    }
    
    /**
     * Subtract hours
     */
    public PocketWatch minusHours(int hours) {
        return new PocketWatch(dateTime.minusHours(hours));
    }
    
    // ========== TIMEZONE OPERATIONS (Fluent) ==========
    
    /**
     * Convert to different timezone (keeps same instant in time)
     */
    public PocketWatch toZone(String zoneId) {
        return new PocketWatch(
            dateTime.withZoneSameInstant(ZoneId.of(zoneId))
        );
    }
    
    /**
     * Convert to different timezone
     */
    public PocketWatch toZone(ZoneId zoneId) {
        return new PocketWatch(
            dateTime.withZoneSameInstant(zoneId)
        );
    }
    
    // ========== FORMATTING ==========
    
    /**
     * Format with custom pattern
     */
    public String format(String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Format with DateTimeFormatter
     */
    public String format(DateTimeFormatter formatter) {
        return dateTime.format(formatter);
    }
    
    /**
     * Format as ISO string (yyyy-MM-dd'T'HH:mm:ssXXX)
     */
    public String toIsoString() {
        return dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
    
    /**
     * Format as ISO date only (yyyy-MM-dd)
     */
    public String toIsoDate() {
        return dateTime.toLocalDate().format(DateTimeFormatter.ISO_DATE);
    }
    
    // ========== COMPARISONS ==========
    
    /**
     * Check if this date is before another
     */
    public boolean isBefore(PocketWatch other) {
        return this.dateTime.isBefore(other.dateTime);
    }
    
    /**
     * Check if this date is after another
     */
    public boolean isAfter(PocketWatch other) {
        return this.dateTime.isAfter(other.dateTime);
    }
    
    /**
     * Check if dates are equal (same instant)
     */
    public boolean isEqual(PocketWatch other) {
        return this.dateTime.isEqual(other.dateTime);
    }
    
    /**
     * Calculate days between this and another date
     */
    public long daysUntil(PocketWatch other) {
        return ChronoUnit.DAYS.between(this.dateTime, other.dateTime);
    }
    
    /**
     * Calculate hours between this and another date
     */
    public long hoursUntil(PocketWatch other) {
        return ChronoUnit.HOURS.between(this.dateTime, other.dateTime);
    }
    
    // ========== ACCESSORS ==========
    
    public int getYear() {
        return dateTime.getYear();
    }
    
    public int getMonth() {
        return dateTime.getMonthValue();
    }
    
    public int getDay() {
        return dateTime.getDayOfMonth();
    }
    
    public int getHour() {
        return dateTime.getHour();
    }
    
    public int getMinute() {
        return dateTime.getMinute();
    }
    
    // ========== OBJECT METHODS ==========
    
    @Override
    public String toString() {
        return dateTime.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PocketWatch other)) return false;
        return dateTime.equals(other.dateTime);
    }
    
    @Override
    public int hashCode() {
        return dateTime.hashCode();
    }
}
