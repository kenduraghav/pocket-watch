package io.pocketwatch.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.pocketwatch.annotations.DateRange;
import io.pocketwatch.annotations.FutureDate;
import io.pocketwatch.annotations.PastDate;
import io.pocketwatch.annotations.PlusDays;
import io.pocketwatch.annotations.ValidDate;
import io.pocketwatch.annotations.ValidTimeZone;
import io.pocketwatch.constants.DatePattern;

/**
 * Comprehensive tests for all validators
 */
class PocketWatchValidatorComprehensiveTest {
    
    // ========== @ValidDate TESTS ==========
    
    @Nested
    @DisplayName("@ValidDate Tests")
    class ValidDateTests {
        
        @Test
        @DisplayName("Should validate correct date format")
        void testValidDate_Success() {
            // Given
            TestModel model = new TestModel();
            model.isoDate = "2025-11-05";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
            assertEquals(0, result.getErrors().size());
        }
        
        @Test
        @DisplayName("Should fail for invalid date format")
        void testValidDate_InvalidFormat() {
            // Given
            TestModel model = new TestModel();
            model.isoDate = "invalid-date";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertEquals(1, result.getErrors().size());
            assertEquals("isoDate", result.getErrors().get(0).fieldName());
        }
        
        @Test
        @DisplayName("Should fail when required field is null")
        void testValidDate_RequiredNull() {
            // Given
            TestModel model = new TestModel();
            model.isoDate = null;
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertTrue(result.getErrors().get(0).message().contains("must be a valid date"));
        }
        
        @Test
        @DisplayName("Should fail when required field is blank")
        void testValidDate_RequiredBlank() {
            // Given
            TestModel model = new TestModel();
            model.isoDate = "   ";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should pass when non-required field is null")
        void testValidDate_NotRequiredNull() {
            // Given
            TestModelOptional model = new TestModelOptional();
            model.optionalDate = null;
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should validate custom pattern")
        void testValidDate_CustomPattern() {
            // Given
            TestModelCustomPattern model = new TestModelCustomPattern();
            model.usDate = "11/05/2025";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail when pattern doesn't match")
        void testValidDate_PatternMismatch() {
            // Given
            TestModelCustomPattern model = new TestModelCustomPattern();
            model.usDate = "2025-11-05"; // ISO format, not US
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        static class TestModel {
            @ValidDate
            private String isoDate;
        }
        
        static class TestModelOptional {
            @ValidDate(required = false)
            private String optionalDate;
        }
        
        static class TestModelCustomPattern {
            @ValidDate(pattern = DatePattern.US_DATE)
            private String usDate;
        }
    }
    
    // ========== @PastDate TESTS ==========
    
    @Nested
    @DisplayName("@PastDate Tests")
    class PastDateTests {
        
        @Test
        @DisplayName("Should validate past date")
        void testPastDate_Valid() {
            // Given
            TestModel model = new TestModel();
            model.birthDate = "2000-01-01";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail for future date")
        void testPastDate_FutureDate() {
            // Given
            TestModel model = new TestModel();
            model.birthDate = "2030-01-01";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertTrue(result.getErrors().get(0).message().contains("must be in the past"));
        }
        
        @Test
        @DisplayName("Should fail for today when inclusive=false")
        void testPastDate_TodayNotInclusive() {
            // Given
            TestModel model = new TestModel();
            model.birthDate = LocalDate.now().toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should pass for today when inclusive=true")
        void testPastDate_TodayInclusive() {
            // Given
            TestModelInclusive model = new TestModelInclusive();
            model.registrationDate = LocalDate.now().toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should skip validation if field is null")
        void testPastDate_Null() {
            // Given
            TestModel model = new TestModel();
            model.birthDate = null;
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            // Only @ValidDate should fail, not @PastDate
            assertEquals(1, result.getErrors().size());
        }
        
        static class TestModel {
            @ValidDate
            @PastDate
            private String birthDate;
        }
        
        static class TestModelInclusive {
            @ValidDate
            @PastDate(inclusive = true)
            private String registrationDate;
        }
    }
    
    // ========== @FutureDate TESTS ==========
    
    @Nested
    @DisplayName("@FutureDate Tests")
    class FutureDateTests {
        
        @Test
        @DisplayName("Should validate future date")
        void testFutureDate_Valid() {
            // Given
            TestModel model = new TestModel();
            model.eventDate = LocalDate.now().plusDays(7).toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail for past date")
        void testFutureDate_PastDate() {
            // Given
            TestModel model = new TestModel();
            model.eventDate = "2020-01-01";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertTrue(result.getErrors().get(0).message().contains("must be in the future"));
        }
        
        @Test
        @DisplayName("Should fail for today when inclusive=false")
        void testFutureDate_TodayNotInclusive() {
            // Given
            TestModel model = new TestModel();
            model.eventDate = LocalDate.now().toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should pass for today when inclusive=true")
        void testFutureDate_TodayInclusive() {
            // Given
            TestModelInclusive model = new TestModelInclusive();
            model.appointmentDate = LocalDate.now().toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        static class TestModel {
            @ValidDate
            @FutureDate
            private String eventDate;
        }
        
        static class TestModelInclusive {
            @ValidDate
            @FutureDate(inclusive = true)
            private String appointmentDate;
        }
    }
    
    // ========== @DateRange TESTS ==========
    
    @Nested
    @DisplayName("@DateRange Tests")
    class DateRangeTests {
        
        @Test
        @DisplayName("Should validate date within range")
        void testDateRange_Valid() {
            // Given
            TestModel model = new TestModel();
            model.bookingDate = LocalDate.now().plusDays(10).toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail when date is before minimum")
        void testDateRange_BeforeMin() {
            // Given
            TestModel model = new TestModel();
            model.bookingDate = LocalDate.now().toString(); // Today, but min is tomorrow
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail when date is after maximum")
        void testDateRange_AfterMax() {
            // Given
            TestModel model = new TestModel();
            model.bookingDate = LocalDate.now().plusDays(400).toString(); // > 365 days
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should validate on boundary dates")
        void testDateRange_Boundaries() {
            // Given - min boundary
            TestModel model1 = new TestModel();
            model1.bookingDate = LocalDate.now().plusDays(1).toString();
            
            // When
            ValidationResult result1 = PocketWatchValidator.validate(model1);
            
            // Then
            assertTrue(result1.isValid());
            
            // Given - max boundary
            TestModel model2 = new TestModel();
            model2.bookingDate = LocalDate.now().plusDays(365).toString();
            
            // When
            ValidationResult result2 = PocketWatchValidator.validate(model2);
            
            // Then
            assertTrue(result2.isValid());
        }
        
        @Test
        @DisplayName("Should handle negative minDaysFromNow")
        void testDateRange_NegativeMin() {
            // Given
            TestModelPast model = new TestModelPast();
            model.feedbackDate = LocalDate.now().minusDays(15).toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        static class TestModel {
            @ValidDate
            @DateRange(minDaysFromNow = 1, maxDaysFromNow = 365)
            private String bookingDate;
        }
        
        static class TestModelPast {
            @ValidDate
            @DateRange(minDaysFromNow = -30, maxDaysFromNow = 0)
            private String feedbackDate;
        }
    }
    
    // ========== @PlusDays TESTS ==========
    
    @Nested
    @DisplayName("@PlusDays Tests")
    class PlusDaysTests {
        
        @Test
        @DisplayName("Should validate correct calculation")
        void testPlusDays_Valid() {
            // Given
            TestModel model = new TestModel();
            model.startDate = "2025-11-05";
            model.endDate = "2025-11-12"; // +7 days
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail when calculation is wrong")
        void testPlusDays_Invalid() {
            // Given
            TestModel model = new TestModel();
            model.startDate = "2025-11-05";
            model.endDate = "2025-11-10"; // Only +5 days, should be +7
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should handle negative days (subtraction)")
        void testPlusDays_Negative() {
            // Given
            TestModelNegative model = new TestModelNegative();
            model.eventDate = "2025-11-12";
            model.bookingDeadline = "2025-11-05"; // -7 days
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should skip when source field is null")
        void testPlusDays_SourceNull() {
            // Given
            TestModel model = new TestModel();
            model.startDate = null;
            model.endDate = "2025-11-12";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            // Should only fail @ValidDate on startDate, not @PlusDays
            assertEquals(1, result.getErrors().size());
            assertEquals("startDate", result.getErrors().get(0).fieldName());
        }
        
        @Test
        @DisplayName("Should fail when source field doesn't exist")
        void testPlusDays_SourceNotFound() {
            // Given
            TestModelBadSource model = new TestModelBadSource();
            model.endDate = "2025-11-12";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertTrue(result.getErrors().get(0).message().contains("not found"));
        }
        
        static class TestModel {
            @ValidDate
            private String startDate;
            
            @ValidDate
            @PlusDays(from = "startDate", days = 7)
            private String endDate;
        }
        
        static class TestModelNegative {
            @ValidDate
            private String eventDate;
            
            @ValidDate
            @PlusDays(from = "eventDate", days = -7)
            private String bookingDeadline;
        }
        
        static class TestModelBadSource {
            @ValidDate
            @PlusDays(from = "nonExistentField", days = 7)
            private String endDate;
        }
    }
    
    // ========== @ValidTimeZone TESTS ==========
    
    @Nested
    @DisplayName("@ValidTimeZone Tests")
    class ValidTimeZoneTests {
        
        @Test
        @DisplayName("Should validate valid timezone")
        void testValidTimeZone_Valid() {
            // Given
            TestModel model = new TestModel();
            model.timezone = "America/New_York";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should validate UTC")
        void testValidTimeZone_UTC() {
            // Given
            TestModel model = new TestModel();
            model.timezone = "UTC";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should validate Asian timezone")
        void testValidTimeZone_Asia() {
            // Given
            TestModel model = new TestModel();
            model.timezone = "Asia/Kolkata";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail for invalid timezone")
        void testValidTimeZone_Invalid() {
            // Given
            TestModel model = new TestModel();
            model.timezone = "InvalidZone";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail for ambiguous timezone")
        void testValidTimeZone_Ambiguous() {
            // Given
            TestModel model = new TestModel();
            model.timezone = "IST"; // Ambiguous
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail when required and null")
        void testValidTimeZone_RequiredNull() {
            // Given
            TestModel model = new TestModel();
            model.timezone = null;
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
        }
        
        @Test
        @DisplayName("Should pass when not required and null")
        void testValidTimeZone_NotRequiredNull() {
            // Given
            TestModelOptional model = new TestModelOptional();
            model.timezone = null;
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        static class TestModel {
            @ValidTimeZone
            private String timezone;
        }
        
        static class TestModelOptional {
            @ValidTimeZone(required = false)
            private String timezone;
        }
    }
    
    // ========== MULTIPLE ANNOTATIONS TESTS ==========
    
    @Nested
    @DisplayName("Multiple Annotations Tests")
    class MultipleAnnotationsTests {
        
        @Test
        @DisplayName("Should validate multiple annotations on same field")
        void testMultipleAnnotations_AllPass() {
            // Given
            TestModel model = new TestModel();
            model.eventDate = LocalDate.now().plusDays(30).toString();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should fail multiple validations")
        void testMultipleAnnotations_MultipleFail() {
            // Given
            TestModel model = new TestModel();
            model.eventDate = "2020-01-01"; // Past date, out of range
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertTrue(result.getErrors().size() >= 2); // @FutureDate + @DateRange
        }
        
        @Test
        @DisplayName("Should validate multiple fields")
        void testMultipleFields_AllValid() {
            // Given
            TestModelMultipleFields model = new TestModelMultipleFields();
            model.startDate = LocalDate.now().plusDays(10).toString();
            model.endDate = LocalDate.now().plusDays(17).toString();
            model.timezone = "America/New_York";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should collect errors from multiple fields")
        void testMultipleFields_MultipleErrors() {
            // Given
            TestModelMultipleFields model = new TestModelMultipleFields();
            model.startDate = "invalid";
            model.endDate = "2020-01-01";
            model.timezone = "BadZone";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertEquals(2, result.getErrors().size());
        }
        
        @Test
        @DisplayName("Should collect errors from multiple fields given valid start date ")
        void testMultipleFields_withValidStartDate_MultipleErrors() {
            // Given
            TestModelMultipleFields model = new TestModelMultipleFields();
            model.startDate = "2025-11-11";
            model.endDate = "2020-01-01";
            model.timezone = "BadZone";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertFalse(result.isValid());
            assertEquals(4, result.getErrors().size());
        }
        
        static class TestModel {
            @ValidDate
            @FutureDate
            @DateRange(minDaysFromNow = 7, maxDaysFromNow = 90)
            private String eventDate;
        }
        
        static class TestModelMultipleFields {
            @ValidDate
            @FutureDate
            @DateRange(minDaysFromNow = 1, maxDaysFromNow = 365)
            private String startDate;
            
            @ValidDate
            @PlusDays(from = "startDate", days = 7)
            private String endDate;
            
            @ValidTimeZone
            private String timezone;
        }
    }
    
    // ========== EDGE CASES ==========
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle object with no annotations")
        void testNoAnnotations() {
            // Given
            TestModelNoAnnotations model = new TestModelNoAnnotations();
            model.someField = "value";
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        @Test
        @DisplayName("Should throw exception for null object")
        void testNullObject() {
            assertThrows(IllegalArgumentException.class, 
                () -> PocketWatchValidator.validate(null));
        }
        
        @Test
        @DisplayName("Should handle empty object")
        void testEmptyObject() {
            // Given
            TestModelEmpty model = new TestModelEmpty();
            
            // When
            ValidationResult result = PocketWatchValidator.validate(model);
            
            // Then
            assertTrue(result.isValid());
        }
        
        static class TestModelNoAnnotations {
            private String someField;
        }
        
        static class TestModelEmpty {
            // No fields
        }
    }
}