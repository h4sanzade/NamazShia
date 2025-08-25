package com.hasanzade.namazshia.domain

data class PrayerTimes(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val sunset: String,
    val maghrib: String,
    val midnight: String,
    val date: String,
    val dateReadable: String
) {
    fun getAllPrayers(): List<Prayer> = listOf(
        Prayer("Fajr", fajr),
        Prayer("Sunrise", sunrise),
        Prayer("Dhuhr", dhuhr),
        Prayer("Sunset", sunset),
        Prayer("Maghrib", maghrib),
        Prayer("Midnight", midnight)
    )
}

data class Prayer(
    val name: String,
    val time: String
)

data class LocationInfo(
    val city: String,
    val latitude: Double = 41.0082,  // Istanbul coordinates
    val longitude: Double = 28.9784
)

data class DateInfo(
    val gregorianDate: String,
    val hijriDate: String,
    val dayOffset: Int = 0
)