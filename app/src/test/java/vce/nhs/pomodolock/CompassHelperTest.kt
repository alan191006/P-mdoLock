package vce.nhs.pomodolock

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import vce.nhs.pomodolock.utils.filterEventsForDate
import vce.nhs.pomodolock.utils.isEventForDate
import java.time.LocalDate

internal class CompassHelperTest {

    @Test
    fun testIsEventForDate() {
        // Test with a valid event for the specified date and timezone offset
        val eventData1 = "DTSTART:20231006T15:00:00"
        val date1 = LocalDate.of(2023, 10, 6)
        val timeZoneOffset1 = 3
        Assertions.assertTrue(isEventForDate(eventData1, date1, timeZoneOffset1))

        // Test with a valid event for the specified date and a different timezone offset
        val eventData2 = "DTSTART:20231006T18:00:00"
        val date2 = LocalDate.of(2023, 10, 6)
        val timeZoneOffset2 = 0
        Assertions.assertTrue(isEventForDate(eventData2, date2, timeZoneOffset2))

        // Test with an event that is not for the specified date
        val eventData3 = "DTSTART:20231005T15:00:00"
        val date3 = LocalDate.of(2023, 10, 6)
        val timeZoneOffset3 = 3
        Assertions.assertFalse(isEventForDate(eventData3, date3, timeZoneOffset3))

        // Test with an event that has no date information
        val eventData5 = "DESCRIPTION:No date information"
        val date5 = LocalDate.of(2023, 10, 6)
        val timeZoneOffset5 = 0
        Assertions.assertFalse(isEventForDate(eventData5, date5, timeZoneOffset5))
    }

    @Test
    fun testFilterEventsForDate() {
        // Sample ICS data containing events for two different dates and a different timezone offset
        val icsData = """
            BEGIN:VEVENT
            DTSTART:20231006T15:00:00
            DTEND:20231006T16:00:00
            SUMMARY:Event 1 for October 6
            END:VEVENT
            BEGIN:VEVENT
            DTSTART:20231006T18:00:00
            DTEND:20231006T19:00:00
            SUMMARY:Event 2 for October 6
            END:VEVENT
            BEGIN:VEVENT
            DTSTART:20231007T14:00:00
            DTEND:20231007T15:00:00
            SUMMARY:Event for October 7
            END:VEVENT
        """.trimIndent()

        val date = LocalDate.of(2023, 10, 6)
        val timezone = 0

        val filteredEvents = filterEventsForDate(icsData, date, timezone)

        // Verify that the filteredEvents only contains events for the specified date
        Assertions.assertTrue(filteredEvents.contains(
            "BEGIN:VEVENT\nDTSTART:20231006T15:00:00\nDTEND:20231006T16:00:00\nSUMMARY:Event 1 for October 6\nEND:VEVENT"))
        Assertions.assertTrue(filteredEvents.contains(
            "BEGIN:VEVENT\nDTSTART:20231006T18:00:00\nDTEND:20231006T19:00:00\nSUMMARY:Event 2 for October 6\nEND:VEVENT"))

        // Verify that the filteredEvents does not contain events for other dates
        Assertions.assertFalse(filteredEvents.contains(
            "BEGIN:VEVENT\nDTSTART:20231007T14:00:00\nDTEND:20231007T15:00:00\nSUMMARY:Event for October 7\nEND:VEVENT"))
    }
}