package com.hasanzade.namazshia.domain

import android.os.Build
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

data class PrayerTimes(
    val fajr: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime,
    val date: LocalDate
) {
    fun getAllPrayers(): List<Prayer> {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return listOf(
            Prayer("Fajr", fajr.format(timeFormatter)),
            Prayer("Sunrise", sunrise.format(timeFormatter)),
            Prayer("Dhuhr", dhuhr.format(timeFormatter)),
            Prayer("Asr", asr.format(timeFormatter)),
            Prayer("Maghrib", maghrib.format(timeFormatter)),
            Prayer("Isha", isha.format(timeFormatter))
        )
    }
}

data class Prayer(
    val name: String,
    val time: String
)

data class LocationInfo(
    val city: String,
    val latitude: Double = 40.4093,
    val longitude: Double = 49.8671
)

data class DateInfo(
    val gregorianDate: String,
    val hijriDate: String,
    val dayOffset: Int = 0
)