package io.pocketwatch.validator;

import static io.pocketwatch.annotations.constants.DatePattern.ISO_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import io.pocketwatch.annotations.DateRange;
import io.pocketwatch.annotations.FutureDate;
import io.pocketwatch.annotations.PastDate;
import io.pocketwatch.annotations.PlusDays;
import io.pocketwatch.annotations.ValidDate;
import io.pocketwatch.annotations.ValidTimeZone;

/**
 * Test for @ValidDate validation
 */
class PocketWatchValidatorTest {
    
    @Test
    void testValidDate_Success() {
        // Given
        TestEvent event = new TestEvent();
        event.startDate = "2025-11-04";
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
    }
    
    @Test
    void testValidDate_InvalidFormat() {
        // Given
        TestEvent event = new TestEvent();
        event.startDate = "invalid-date";
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("startDate", result.getErrors().get(0).getFieldName());
    }
    
    @Test
    void testValidDate_NullWhenRequired() {
        // Given
        TestEvent event = new TestEvent();
        event.startDate = null;
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
    }
    
    @Test
    void testPastDate_Valid() {
        // Given - date in the past
        TestEventWithPast event = new TestEventWithPast();
        event.birthDate = "2000-01-01";
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid());
    }

    @Test
    void testPastDate_Invalid_FutureDate() {
        // Given - date in the future
        TestEventWithPast event = new TestEventWithPast();
        event.birthDate = "2030-01-01";
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).getMessage().contains("must be in the past"));
    }

    @Test
    void testPastDate_Inclusive_Today() {
        // Given - today's date with inclusive=true
        TestEventWithPastInclusive event = new TestEventWithPastInclusive();
        event.registrationDate = java.time.LocalDate.now().toString();
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid()); // Should be valid with inclusive=true
    }
    
    @Test
    void testFutureDate_Valid() {
        // Given - date in the past
        TestEventWithFuture event = new TestEventWithFuture();
        event.eventDate = "2026-02-24";
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid());
    }
    
    @Test
    void testFutureDate_Inclusive_Today() {
        // Given - today's date with inclusive=true
    	TestEventWithFutureInclusive event = new TestEventWithFutureInclusive();
        event.eventDate = java.time.LocalDate.now().toString();
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid()); // Should be valid with inclusive=true
    }
    
    @Test
    void testFutureDate_Invalid() {
        // Given - today's date with inclusive=true
    	TestEventWithFutureInclusive event = new TestEventWithFutureInclusive();
        event.eventDate = java.time.LocalDate.now().minusDays(1).toString();
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertFalse(result.isValid()); 
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).getMessage().contains("must be in the future"));
    }
    
    
    @Test
    void testDateRange_valid() {
    	String checkInDate= LocalDate.now().plusDays(2).toString();
    	TestEventDateRange  event = new TestEventDateRange(checkInDate);
    	
    	 // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid());
    }
    
    
    @Test
    void testDateRange_Invalid() {
    	String checkInDate=LocalDate.now().toString();
    	TestEventDateRange  event = new TestEventDateRange(checkInDate);
    	
    	 // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        assertFalse(result.isValid()); 
        assertEquals(1, result.getErrors().size());
        assertTrue(result.hasErrors());
    }
    
    
    @Test
    void testDateRange_BeyondMaxDate() {
    	String checkInDate=LocalDate.now().plusDays(10).toString();
    	TestEventDateRange  event = new TestEventDateRange(checkInDate);
    	
    	 // When
        ValidationResult result = PocketWatchValidator.validate(event);
        System.out.println(result.getErrors().get(0).getMessage());
        assertFalse(result.isValid()); 
        assertEquals(1, result.getErrors().size());
        assertTrue(result.hasErrors());
    }
    
    
    @Test
    void testPlusDays_Valid() {
        // Given
        TestEventWithPlusDays event = new TestEventWithPlusDays();
        event.startDate = "2025-11-04";
        event.endDate = "2025-11-11";  // 7 days after
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid());
    }

    @Test
    void testPlusDays_Invalid() {
        // Given
        TestEventWithPlusDays event = new TestEventWithPlusDays();
        event.startDate = "2025-11-04";
        event.endDate = "2025-11-10";  // Only 6 days - wrong!
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    void testPlusDays_NegativeDays() {
        // Given - subtract days
        TestEventWithNegativeDays event = new TestEventWithNegativeDays();
        event.eventDate = "2025-11-11";
        event.bookingDeadline = "2025-11-04";  // 7 days before
        
        // When
        ValidationResult result = PocketWatchValidator.validate(event);
        
        // Then
        assertTrue(result.isValid());
    }
    
    
    @Test
    void testValidTimeZone() {
    	
    	TestEventWithValidTimeZone event = new TestEventWithValidTimeZone();
    	
    	event.timeZone = "Asia/Kolkata";
    	
    	ValidationResult result = PocketWatchValidator.validate(event);
    	
    	assertTrue(result.isValid());
    	
    }
    
    
    @Test
    void testValidTimeZoneWithNullValue() {
    	
    	TestEventWithValidTimeZone event = new TestEventWithValidTimeZone();
    	
    	event.timeZone = null;
    	
    	ValidationResult result = PocketWatchValidator.validate(event);
    	
    	assertFalse(result.isValid());
    	
    }
    
    @Test
    void testValidTimeZoneWithOffSetFormat() {
    	TestEventWithValidTimeZone event = new TestEventWithValidTimeZone();
    	
    	event.timeZone = "+05:30";
    	
    	ValidationResult result = PocketWatchValidator.validate(event);
    	
    	assertTrue(result.isValid());
    	
    }
    
    
    @Test
    void testInValidTimeZone() {
    	
    	TestEventWithValidTimeZone event = new TestEventWithValidTimeZone();
    	
    	event.timeZone = "IST";
    	
    	ValidationResult result = PocketWatchValidator.validate(event);
    	
    	assertFalse(result.isValid());
    	
    }
    
    
    static class TestEventWithValidTimeZone {
    	
    	@ValidTimeZone
    	String timeZone;
    }

    // Test classes
    static class TestEventWithPlusDays {
        @ValidDate(pattern = ISO_DATE)
        private String startDate;
        
        @ValidDate
        @PlusDays(from = "startDate", days = 7)
        private String endDate;
    }

    static class TestEventWithNegativeDays {
        @ValidDate
        private String eventDate;
        
        @ValidDate
        @PlusDays(from = "eventDate", days = -7)
        private String bookingDeadline;
    }
    
    
    record TestEventDateRange(
    		@DateRange(minDaysFromNow = 1, maxDaysFromNow = 8)
    		String checkInDate) {}
    
    
    static class TestEventWithFuture {
        @ValidDate
        @FutureDate(message = "Event date must be in the future")
        private String eventDate;
    }
    
    
    static class TestEventWithFutureInclusive {
        @ValidDate
        @FutureDate(inclusive = true)
        private String eventDate;
    }
    

    // Test classes
    static class TestEventWithPast {
        @ValidDate
        @PastDate(message = "Birth date must be in the past")
        private String birthDate;
    }

    static class TestEventWithPastInclusive {
        @ValidDate
        @PastDate(inclusive = true)
        private String registrationDate;
    }
    
    // Test class
    static class TestEvent {
        @ValidDate(pattern = ISO_DATE, message = "Start date is invalid")
        private String startDate;
        
        @DateRange(minDaysFromNow = 1, maxDaysFromNow = 5)
        private String checkInDate;
    }
}