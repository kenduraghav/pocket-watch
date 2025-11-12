package io.pocketwatch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for PocketWatch core API
 */
class PocketWatchTest {
    
    // ========== PARSING TESTS ==========
    
    @Nested
    @DisplayName("Parsing Tests")
    class ParsingTests {
        
        @Test
        @DisplayName("Should parse valid ISO date (yyyy-MM-dd)")
        void testParse_ValidIsoDate() {
            // Given
            String dateString = "2025-11-05";
            
            // When
            PocketWatch result = PocketWatch.parse(dateString);
            
            // Then
            assertNotNull(result);
            assertEquals(2025, result.getYear());
            assertEquals(11, result.getMonth());
            assertEquals(5, result.getDay());
        }
        
        @Test
        @DisplayName("Should parse with custom pattern")
        void testParse_CustomPattern() {
            // Given
            String dateString = "05/11/2025";
            String pattern = "dd/MM/yyyy";
            
            // When
            PocketWatch result = PocketWatch.parse(dateString, pattern);
            
            // Then
            assertEquals(5, result.getDay());
            assertEquals(11, result.getMonth());
            assertEquals(2025, result.getYear());
        }
        
        @Test
        @DisplayName("Should parse US date format")
        void testParse_UsFormat() {
            // Given
            String dateString = "11/05/2025";
            String pattern = "MM/dd/yyyy";
            
            // When
            PocketWatch result = PocketWatch.parse(dateString, pattern);
            
            // Then
            assertEquals(11, result.getMonth());
            assertEquals(5, result.getDay());
        }
        
        @Test
        @DisplayName("Should parse date-time with pattern")
        void testParse_DateTime() {
            // Given
            String dateString = "2025-11-05 14:30:45";
            String pattern = "yyyy-MM-dd HH:mm:ss";
            
            // When
            PocketWatch result = PocketWatch.parse(dateString, pattern);
            
            // Then
            assertEquals(14, result.getHour());
            assertEquals(30, result.getMinute());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid date")
        void testParse_InvalidDate() {
            // Given
            String invalidDate = "not-a-date";
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> PocketWatch.parse(invalidDate));
        }
        
        @Test
        @DisplayName("Should throw exception for null date")
        void testParse_NullDate() {
            assertThrows(IllegalArgumentException.class, 
                () -> PocketWatch.parse(null));
        }
        
        @Test
        @DisplayName("Should throw exception for blank date")
        void testParse_BlankDate() {
            assertThrows(IllegalArgumentException.class, 
                () -> PocketWatch.parse("   "));
        }
        
        @Test
        @DisplayName("Should throw exception for wrong pattern")
        void testParse_WrongPattern() {
            // Given
            String dateString = "2025-11-05";
            String wrongPattern = "dd/MM/yyyy";
            
            // When & Then
            assertThrows(IllegalArgumentException.class, 
                () -> PocketWatch.parse(dateString, wrongPattern));
        }
    }
    
    // ========== FACTORY METHODS TESTS ==========
    
    @Nested
    @DisplayName("Factory Methods Tests")
    class FactoryMethodsTests {
        
        @Test
        @DisplayName("Should create PocketWatch from now()")
        void testNow() {
            // When
            PocketWatch result = PocketWatch.now();
            
            // Then
            assertNotNull(result);
            // Should be within 1 second of current time
            long diff = Math.abs(result.toEpochMilli() - System.currentTimeMillis());
            assertTrue(diff < 1000);
        }
        
        @Test
        @DisplayName("Should create PocketWatch in specific timezone")
        void testNowIn() {
            // When
            PocketWatch ny = PocketWatch.nowIn("America/New_York");
            PocketWatch tokyo = PocketWatch.nowIn("Asia/Tokyo");
            
            // Then
            assertNotNull(ny);
            assertNotNull(tokyo);
            // Same instant, different local times
            assertEquals(ny.toEpochMilli(), tokyo.toEpochMilli(), 1000);
        }
        
        @Test
        @DisplayName("Should create from ZonedDateTime")
        void testOf_ZonedDateTime() {
            // Given
            ZonedDateTime zdt = ZonedDateTime.of(2025, 11, 5, 14, 30, 0, 0, 
                ZoneId.systemDefault());
            
            // When
            PocketWatch result = PocketWatch.of(zdt);
            
            // Then
            assertEquals(2025, result.getYear());
            assertEquals(11, result.getMonth());
            assertEquals(5, result.getDay());
            assertEquals(14, result.getHour());
            assertEquals(30, result.getMinute());
        }
        
        @Test
        @DisplayName("Should create from LocalDate")
        void testOf_LocalDate() {
            // Given
            LocalDate date = LocalDate.of(2025, 11, 5);
            
            // When
            PocketWatch result = PocketWatch.of(date);
            
            // Then
            assertEquals(2025, result.getYear());
            assertEquals(11, result.getMonth());
            assertEquals(5, result.getDay());
        }
        
        @Test
        @DisplayName("Should create from LocalDateTime")
        void testOf_LocalDateTime() {
            // Given
            LocalDateTime dateTime = LocalDateTime.of(2025, 11, 5, 14, 30);
            
            // When
            PocketWatch result = PocketWatch.of(dateTime);
            
            // Then
            assertEquals(2025, result.getYear());
            assertEquals(14, result.getHour());
        }
        
        @Test
        @DisplayName("Should throw exception for null ZonedDateTime")
        void testOf_NullZonedDateTime() {
            assertThrows(IllegalArgumentException.class, 
                () -> PocketWatch.of((ZonedDateTime) null));
        }
        
        @Test
        @DisplayName("Should throw exception for null LocalDate")
        void testOf_NullLocalDate() {
            assertThrows(IllegalArgumentException.class, 
                () -> PocketWatch.of((LocalDate) null));
        }
    }
    
    // ========== CONVERSION TESTS ==========
    
    @Nested
    @DisplayName("Conversion Tests")
    class ConversionTests {
        
        @Test
        @DisplayName("Should convert to LocalDate")
        void testToLocalDate() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When
            LocalDate result = pw.toLocalDate();
            
            // Then
            assertEquals(LocalDate.of(2025, 11, 5), result);
        }
        
        @Test
        @DisplayName("Should convert to LocalDateTime")
        void testToLocalDateTime() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05 14:30:00", 
                "yyyy-MM-dd HH:mm:ss");
            
            // When
            LocalDateTime result = pw.toLocalDateTime();
            
            // Then
            assertEquals(2025, result.getYear());
            assertEquals(14, result.getHour());
            assertEquals(30, result.getMinute());
        }
        
        @Test
        @DisplayName("Should convert to ZonedDateTime")
        void testToZonedDateTime() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When
            ZonedDateTime result = pw.toZonedDateTime();
            
            // Then
            assertNotNull(result);
            assertEquals(2025, result.getYear());
            assertEquals(11, result.getMonthValue());
        }
        
        @Test
        @DisplayName("Should convert to Instant")
        void testToInstant() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When
            Instant result = pw.toInstant();
            
            // Then
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should convert to epoch milliseconds")
        void testToEpochMilli() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When
            long result = pw.toEpochMilli();
            
            // Then
            assertTrue(result > 0);
            // Should be in year 2025
            assertTrue(result > 1735689600000L); // Jan 1, 2025
        }
    }
    
    // ========== OPERATIONS TESTS ==========
    
    @Nested
    @DisplayName("Operations Tests")
    class OperationsTests {
        
        @Test
        @DisplayName("Should add days")
        void testPlusDays() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05");
            
            // When
            PocketWatch result = original.plusDays(7);
            
            // Then
            assertEquals(12, result.getDay());
            assertNotEquals(original, result); // Immutability
        }
        
        @Test
        @DisplayName("Should add days across month boundary")
        void testPlusDays_CrossMonth() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-28");
            
            // When
            PocketWatch result = original.plusDays(5);
            
            // Then
            assertEquals(12, result.getMonth()); // December
            assertEquals(3, result.getDay());
        }
        
        @Test
        @DisplayName("Should add hours")
        void testPlusHours() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05 10:00:00", 
                "yyyy-MM-dd HH:mm:ss");
            
            // When
            PocketWatch result = original.plusHours(5);
            
            // Then
            assertEquals(15, result.getHour());
        }
        
        @Test
        @DisplayName("Should add minutes")
        void testPlusMinutes() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05 10:30:00", 
                "yyyy-MM-dd HH:mm:ss");
            
            // When
            PocketWatch result = original.plusMinutes(45);
            
            // Then
            assertEquals(11, result.getHour());
            assertEquals(15, result.getMinute());
        }
        
        @Test
        @DisplayName("Should add months")
        void testPlusMonths() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05");
            
            // When
            PocketWatch result = original.plusMonths(2);
            
            // Then
            assertEquals(2026, result.getYear());
            assertEquals(1, result.getMonth()); // January
        }
        
        @Test
        @DisplayName("Should add years")
        void testPlusYears() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05");
            
            // When
            PocketWatch result = original.plusYears(3);
            
            // Then
            assertEquals(2028, result.getYear());
        }
        
        @Test
        @DisplayName("Should subtract days")
        void testMinusDays() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05");
            
            // When
            PocketWatch result = original.minusDays(3);
            
            // Then
            assertEquals(2, result.getDay());
        }
        
        @Test
        @DisplayName("Should subtract hours")
        void testMinusHours() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05 10:00:00", 
                "yyyy-MM-dd HH:mm:ss");
            
            // When
            PocketWatch result = original.minusHours(3);
            
            // Then
            assertEquals(7, result.getHour());
        }
        
        @Test
        @DisplayName("Should chain operations")
        void testMethodChaining() {
            // Given
            PocketWatch start = PocketWatch.parse("2025-11-05");
            
            // When
            PocketWatch result = start
                .plusDays(7)
                .plusHours(2)
                .plusMinutes(30);
            
            // Then
            assertEquals(12, result.getDay());
            assertEquals(2, result.getHour());
            assertEquals(30, result.getMinute());
        }
        
        @Test
        @DisplayName("Operations should be immutable")
        void testImmutability() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05");
            
            // When
            PocketWatch modified = original.plusDays(7);
            
            // Then
            assertNotEquals(original, modified);
            assertEquals(5, original.getDay()); // Original unchanged
            assertEquals(12, modified.getDay());
        }
    }
    
    // ========== TIMEZONE TESTS ==========
    
    @Nested
    @DisplayName("Timezone Tests")
    class TimezoneTests {
        
        @Test
        @DisplayName("Should convert to different timezone")
        void testToZone_String() {
            // Given
            PocketWatch utc = PocketWatch.parse("2025-11-05T10:00:00Z", 
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
            
            // When
            PocketWatch ny = utc.toZone("America/New_York");
            
            // Then
            assertEquals(utc.toEpochMilli(), ny.toEpochMilli()); // Same instant
        }
        
        @Test
        @DisplayName("Should convert to timezone with ZoneId")
        void testToZone_ZoneId() {
            // Given
            PocketWatch original = PocketWatch.parse("2025-11-05T10:00:00Z", 
                "yyyy-MM-dd'T'HH:mm:ss'Z'");
            
            // When
            PocketWatch result = original.toZone(ZoneId.of("Europe/London"));
            
            // Then
            assertNotNull(result);
            assertEquals(original.toEpochMilli(), result.toEpochMilli());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid timezone")
        void testToZone_Invalid() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When & Then
            assertThrows(Exception.class, 
                () -> pw.toZone("Invalid/Timezone"));
        }
    }
    
    // ========== FORMATTING TESTS ==========
    
    @Nested
    @DisplayName("Formatting Tests")
    class FormattingTests {
        
        @Test
        @DisplayName("Should format with custom pattern")
        void testFormat_CustomPattern() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When
            String result = pw.format("dd-MMM-yyyy");
            
            // Then
            assertEquals("05-Nov-2025", result);
        }
        
        @Test
        @DisplayName("Should format to ISO string")
        void testToIsoString() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When
            String result = pw.toIsoString();
            
            // Then
            assertTrue(result.startsWith("2025-11-05"));
        }
        
        @Test
        @DisplayName("Should format to ISO date only")
        void testToIsoDate() {
            // Given
            PocketWatch pw = PocketWatch.parse("2025-11-05");
            
            // When
            String result = pw.toIsoDate();
            
            // Then
            assertEquals("2025-11-05", result);
        }
    }
    
    // ========== COMPARISON TESTS ==========
    
    @Nested
    @DisplayName("Comparison Tests")
    class ComparisonTests {
        
        @Test
        @DisplayName("Should check if before")
        void testIsBefore() {
            // Given
            PocketWatch date1 = PocketWatch.parse("2025-11-05");
            PocketWatch date2 = PocketWatch.parse("2025-11-10");
            
            // When & Then
            assertTrue(date1.isBefore(date2));
            assertFalse(date2.isBefore(date1));
        }
        
        @Test
        @DisplayName("Should check if after")
        void testIsAfter() {
            // Given
            PocketWatch date1 = PocketWatch.parse("2025-11-10");
            PocketWatch date2 = PocketWatch.parse("2025-11-05");
            
            // When & Then
            assertTrue(date1.isAfter(date2));
            assertFalse(date2.isAfter(date1));
        }
        
        @Test
        @DisplayName("Should check if equal")
        void testIsEqual() {
            // Given
            PocketWatch date1 = PocketWatch.parse("2025-11-05");
            PocketWatch date2 = PocketWatch.parse("2025-11-05");
            
            // When & Then
            assertTrue(date1.isEqual(date2));
        }
        
        @Test
        @DisplayName("Should calculate days until")
        void testDaysUntil() {
            // Given
            PocketWatch start = PocketWatch.parse("2025-11-05");
            PocketWatch end = PocketWatch.parse("2025-11-12");
            
            // When
            long days = start.daysUntil(end);
            
            // Then
            assertEquals(7, days);
        }
        
        @Test
        @DisplayName("Should calculate negative days for past dates")
        void testDaysUntil_Negative() {
            // Given
            PocketWatch start = PocketWatch.parse("2025-11-12");
            PocketWatch end = PocketWatch.parse("2025-11-05");
            
            // When
            long days = start.daysUntil(end);
            
            // Then
            assertEquals(-7, days);
        }
        
        @Test
        @DisplayName("Should calculate hours until")
        void testHoursUntil() {
            // Given
            PocketWatch start = PocketWatch.parse("2025-11-05 10:00:00", 
                "yyyy-MM-dd HH:mm:ss");
            PocketWatch end = PocketWatch.parse("2025-11-05 15:00:00", 
                "yyyy-MM-dd HH:mm:ss");
            
            // When
            long hours = start.hoursUntil(end);
            
            // Then
            assertEquals(5, hours);
        }
    }
    
    // ========== EDGE CASES ==========
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle leap year")
        void testLeapYear() {
            // Given
            PocketWatch date = PocketWatch.parse("2024-02-29");
            
            // When
            PocketWatch nextDay = date.plusDays(1);
            
            // Then
            assertEquals(3, nextDay.getMonth()); // March
            assertEquals(1, nextDay.getDay());
        }
        
        @Test
        @DisplayName("Should handle year boundary")
        void testYearBoundary() {
            // Given
            PocketWatch date = PocketWatch.parse("2025-12-31");
            
            // When
            PocketWatch nextDay = date.plusDays(1);
            
            // Then
            assertEquals(2026, nextDay.getYear());
            assertEquals(1, nextDay.getMonth());
            assertEquals(1, nextDay.getDay());
        }
        
        @Test
        @DisplayName("Should handle large day additions")
        void testLargeDayAddition() {
            // Given
            PocketWatch date = PocketWatch.parse("2025-11-05");
            
            // When
            PocketWatch result = date.plusDays(1000);
            
            // Then
            assertNotNull(result);
            assertTrue(result.isAfter(date));
        }
    }
    
    // ========== OBJECT METHOD TESTS ==========
    
    @Nested
    @DisplayName("Object Methods Tests")
    class ObjectMethodsTests {
        
        @Test
        @DisplayName("Should implement equals correctly")
        void testEquals() {
            // Given
            PocketWatch date1 = PocketWatch.parse("2025-11-05");
            PocketWatch date2 = PocketWatch.parse("2025-11-05");
            PocketWatch date3 = PocketWatch.parse("2025-11-06");
            
            // When & Then
            assertEquals(date1, date2);
            assertNotEquals(date1, date3);
        }
        
        @Test
        @DisplayName("Should implement hashCode correctly")
        void testHashCode() {
            // Given
            PocketWatch date1 = PocketWatch.parse("2025-11-05");
            PocketWatch date2 = PocketWatch.parse("2025-11-05");
            
            // When & Then
            assertEquals(date1.hashCode(), date2.hashCode());
        }
        
        @Test
        @DisplayName("Should implement toString")
        void testToString() {
            // Given
            PocketWatch date = PocketWatch.parse("2025-11-05");
            
            // When
            String result = date.toString();
            
            // Then
            assertNotNull(result);
            assertTrue(result.contains("2025"));
        }
    }
}