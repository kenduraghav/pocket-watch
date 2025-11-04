package io.pocketwatch.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.pocketwatch.annotations.DatePattern;
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
    
    // Test class
    static class TestEvent {
        @ValidDate(pattern = DatePattern.ISO_DATE, message = "Start date is invalid")
        private String startDate;
    }
}