package io.pocketwatch.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.pocketwatch.annotations.DatePattern;
import io.pocketwatch.annotations.PastDate;
import io.pocketwatch.annotations.ValidDate;

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

    // Test classes
    static class TestEventWithPast {
        @ValidDate(pattern = DatePattern.ISO_DATE)
        @PastDate(message = "Birth date must be in the past")
        private String birthDate;
    }

    static class TestEventWithPastInclusive {
        @ValidDate(pattern = DatePattern.ISO_DATE)
        @PastDate(inclusive = true)
        private String registrationDate;
    }
    
    // Test class
    static class TestEvent {
        @ValidDate(pattern = DatePattern.ISO_DATE, message = "Start date is invalid")
        private String startDate;
    }
}