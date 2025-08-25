package com.hasanzade.namazshia.domain

import com.hasanzade.namazshia.data.PrayerApiService
import javax.inject.Inject
import javax.inject.Singleton

interface PrayerRepository {
    suspend fun getPrayerTimes(city: String): Result<PrayerTimes>
}

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val apiService: PrayerApiService
) : PrayerRepository {

    override suspend fun getPrayerTimes(city: String): Result<PrayerTimes> {
        return try {
            val response = apiService.getPrayerTimes(city)
            if (response.success && response.result.isNotEmpty()) {
                val dto = response.result.first()
                val prayerTimes = PrayerTimes(
                    fajr = dto.timeFajr,
                    sunrise = dto.timeSunrise,
                    dhuhr = dto.timeDhuhr,
                    sunset = dto.timeSunset,
                    maghrib = dto.timeMaghrib,
                    midnight = dto.timeMidnight,
                    date = dto.date,
                    dateReadable = dto.dateReadable
                )
                Result.success(prayerTimes)
            } else {
                Result.failure(Exception("No prayer times found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
