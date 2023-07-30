package vce.nhs.pomodolock.fragments.CompassFragment

import android.util.Log
import java.lang.Math.abs
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


data class CompassModel(
    val summary: String,
    val teacher: String,
    val location: String,
    val startTime: String,
    val endTime: String
)

fun parseTimetableData(timetableData: String, timezone: Int): List<CompassModel> {
    val compassModels = mutableListOf<CompassModel>()

    val eventRegex = """BEGIN:VEVENT\s+(.+?)\s+END:VEVENT""".toRegex(RegexOption.DOT_MATCHES_ALL)
    val fieldsRegex = """(DTSTART|DTEND|SUMMARY|DESCRIPTION|LOCATION):(.*)""".toRegex(RegexOption.MULTILINE)

    val zoneIdUtcCorrected = if (timezone >= 0) {
        ZoneId.of("UTC+$timezone")
    } else {
        ZoneId.of("UTC-${abs(timezone)}")
    }

    for (match in eventRegex.findAll(timetableData)) {
        val eventData = match.groupValues[1]
        val eventFields = fieldsRegex.findAll(eventData.trimIndent())

        var summary = ""
        var teacher = ""
        var location = ""
        var startTime = ""
        var endTime = ""

        for (fieldMatch in eventFields) {
            val fieldName = fieldMatch.groupValues[1]
            val fieldValue = fieldMatch.groupValues[2].trim()

            when (fieldName) {
                "DTSTART" -> startTime = fieldValue
                "DTEND" -> endTime = fieldValue
                "SUMMARY" -> summary = fieldValue
                "DESCRIPTION" -> teacher = fieldValue
                "LOCATION" -> location = fieldValue
            }
        }

        // Convert date and time strings to LocalDateTime (UTC+0 assumed)
        val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")
        val startTimeFormatted = try {
            LocalDateTime.parse(startTime, timeFormatter)
        } catch (e: Exception) {
            Log.e("TimetableParser", "Error parsing start time: ${e.message}")
            continue // Skip this event if there's an error parsing the start time
        }
        val endTimeFormatted = try {
            LocalDateTime.parse(endTime, timeFormatter)
        } catch (e: Exception) {
            Log.e("TimetableParser", "Error parsing end time: ${e.message}")
            continue // Skip this event if there's an error parsing the end time
        }

        // Convert LocalDateTime to ZonedDateTime with your local time zone (UTC+9)
        val startTimeZoned = startTimeFormatted.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(zoneIdUtcCorrected)
        val endTimeZoned = endTimeFormatted.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(zoneIdUtcCorrected)

        // Format the time strings as desired (hh:mm)
        val startTimeString = startTimeZoned.format(DateTimeFormatter.ofPattern("HH:mm"))
        val endTimeString = endTimeZoned.format(DateTimeFormatter.ofPattern("HH:mm"))

        // Create the CompassModel object with formatted time strings and extracted "RML"
        val compassModel = CompassModel(summary, teacher, location, startTimeString, endTimeString)
        compassModels.add(compassModel)

        Log.d("TimetableParser", "Parsed event: Summary=$summary, Teacher=$teacher, Location=$location, Start=$startTimeString, End=$endTimeString")
    }

    // Sort the compassModels list by start time
    compassModels.sortBy { it.startTime }

    return compassModels
}