package vce.nhs.pomodolock.utils

import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

class CompassHelper {
    // Define the interface for the caller to receive the loaded timetable
    interface TimetableLoadListener {
        fun onTimetableLoaded(timetableData: String)
        fun onTimetableLoadFailed(errorMessage: String)
    }

    companion object {
        // Method to load the timetable from the provided URL
        fun loadTimetable(icsUrl: String, listener: TimetableLoadListener) {
            val client = OkHttpClient()
            val request = Request.Builder().url(icsUrl).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    listener.onTimetableLoadFailed("Failed to download the timetable.")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            response.body?.let { body ->
                                val timetableData = body.string()
                                listener.onTimetableLoaded(timetableData)
                            }
                        } catch (e: IOException) {
                            listener.onTimetableLoadFailed("Error reading timetable data.")
                        }
                    } else {
                        listener.onTimetableLoadFailed("Failed to download the timetable.")
                    }
                }
            })
        }

        // Method to load the timetable for the given date
        fun loadTimetableForDate(icsUrl: String, date: LocalDate, timezone: Int, listener: TimetableLoadListener) {
            loadTimetable(icsUrl, object : TimetableLoadListener {
                override fun onTimetableLoaded(timetableData: String) {
                    val filteredData = filterEventsForDate(timetableData, date, timezone)
                    listener.onTimetableLoaded(filteredData)
                }

                override fun onTimetableLoadFailed(errorMessage: String) {
                    listener.onTimetableLoadFailed(errorMessage)
                }
            })
        }
    }
}

// Helper method to check if an event is for the specified date
fun isEventForDate(eventData: String, date: LocalDate, timeZoneOffset: Int): Boolean {
    try
    {
        val startTag = "DTSTART:"
        val startIndex = eventData.indexOf(startTag) + startTag.length
        val endIndex = eventData.indexOf("T", startIndex)
        val datePart = eventData.substring(startIndex, endIndex + 3)

        // Extract the year, month, day, and hour from the datePart
        val year = datePart.substring(0, 4).toInt()
        val month = datePart.substring(4, 6).toInt()
        val day = datePart.substring(6, 8).toInt()
        val hour = datePart.substring(9, 11).toInt()

        val eventDateTime =
            LocalDateTime.of(year, month, day, hour, 0) // Assuming the time is in UTC
        val eventDateTimeWithOffset = eventDateTime.plusHours(timeZoneOffset.toLong())

        return eventDateTimeWithOffset.toLocalDate() == date
    }

    // If there's no time data
    catch (e: java.lang.StringIndexOutOfBoundsException) {
        return false
    }
}

// Method to filter the events for a specific date
fun filterEventsForDate(icsData: String, date: LocalDate, timezone: Int): String {
    val filteredEvents = mutableListOf<String>()
    val lines = icsData.lines()

    var isInsideEvent = false
    val currentEvent = StringBuilder()

    for (line in lines) {
        if (line.startsWith("BEGIN:VEVENT")) {
            isInsideEvent = true
            currentEvent.clear()
        } else if (line.startsWith("END:VEVENT")) {
            isInsideEvent = false
            currentEvent.append(line).append("\n")
            if (isEventForDate(currentEvent.toString(), date, timezone)) {
                filteredEvents.add(currentEvent.toString())
            }
        }

        if (isInsideEvent) {
            currentEvent.append(line).append("\n")
        }
    }
    return filteredEvents.joinToString(separator = "")
}

