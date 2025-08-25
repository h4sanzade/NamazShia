package com.hasanzade.namazshia.data

import kotlinx.serialization.Serializable

@Serializable
data class PrayerTimesResponse(
    val success: Boolean,
    val result: List<PrayerTimeDto>
)

@Serializable
data class PrayerTimeDto(
    val timeSunrise: String = "",
    val timeFajr: String = "",
    val timeDhuhr: String = "",
    val timeSunset: String = "",
    val timeMaghrib: String = "",
    val timeMidnight: String = "",
    val date: String = "",
    val dateReadable: String = ""
)