package com.hasanzade.namazshia.domain

import android.util.Log
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
            Log.d("PrayerRepository", "Fetching prayer times for city: $city")

            val response = apiService.getPrayerTimes(city)
            Log.d("PrayerRepository", "API Response: success=${response.success}, result size=${response.result.size}")

            if (response.success && response.result.isNotEmpty()) {
                val dto = response.result.first()
                Log.d("PrayerRepository", "First result DTO: $dto")

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

                Log.d("PrayerRepository", "Created PrayerTimes: $prayerTimes")
                Result.success(prayerTimes)
            } else {
                val errorMsg = "No prayer times found. Success: ${response.success}, Results: ${response.result.size}"
                Log.e("PrayerRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("PrayerRepository", "Error fetching prayer times", e)
            Result.failure(e)
        }
    }
}