package com.beperfectsalon.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Salon working hours and slot generation used by the booking flow. */
object SalonHours {
    const val OPEN_HOUR_24 = 10 // 10:00 AM
    const val CLOSE_HOUR_24 = 20 // 8:00 PM
    const val SLOT_INTERVAL_MINUTES = 30

    private val slotFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val dateDisplayFormat: SimpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

    /** All bookable slots in a day, e.g. "10:00 AM", "10:30 AM", ... up to (but excluding) close. */
    fun allSlotsForDay(): List<String> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, OPEN_HOUR_24)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val slots = mutableListOf<String>()
        while (calendar.get(Calendar.HOUR_OF_DAY) < CLOSE_HOUR_24) {
            slots += slotFormat.format(calendar.time)
            calendar.add(Calendar.MINUTE, SLOT_INTERVAL_MINUTES)
        }
        return slots
    }

    /** The next [count] calendar days (today included) as yyyy-MM-dd strings, for date pickers. */
    fun nextDays(count: Int = 14): List<String> {
        val calendar = Calendar.getInstance()
        return (0 until count).map {
            val date = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            date
        }
    }

    /** True if [date] (yyyy-MM-dd) + [slot] ("hh:mm a") is still in the future relative to now. */
    fun isSlotInFuture(date: String, slot: String, now: Date = Date()): Boolean {
        val combined = "$date $slot"
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)
        val slotDate = runCatching { format.parse(combined) }.getOrNull() ?: return true
        return slotDate.after(now)
    }
}
