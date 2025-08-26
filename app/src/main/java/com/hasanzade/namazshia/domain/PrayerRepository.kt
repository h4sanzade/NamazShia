package com.hasanzade.namazshia.domain

import android.util.Log
import com.hasanzade.namazshia.location.LocationData
import com.hasanzade.namazshia.PrayerTimesCalculator
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

interface PrayerRepository {
    fun getPrayerTimes(locationData: LocationData, date: LocalDate = LocalDate.now()): Result<PrayerTimes>
    fun getPrayerTimesForOffset(locationData: LocationData, dayOffset: Int): Result<PrayerTimes>
}

@Singleton
class PrayerRepositoryImpl @Inject constructor() : PrayerRepository {

    private val calculator = PrayerTimesCalculator()

    override fun getPrayerTimes(locationData: LocationData, date: LocalDate): Result<PrayerTimes> {
        return try {
            Log.d("PrayerRepository", "Calculating prayer times for: ${locationData.city} (${locationData.latitude}, ${locationData.longitude}) on $date")

            val result = calculator.calculatePrayerTimes(
                latitude = locationData.latitude,
                longitude = locationData.longitude,
                date = date,
                timezone = 4
            )

            val prayerTimes = PrayerTimes(
                fajr = result.fajr,
                sunrise = result.sunrise,
                dhuhr = result.dhuhr,
                asr = result.asr,
                maghrib = result.maghrib,
                isha = result.isha,
                date = result.date
            )

            Log.d("PrayerRepository", "Successfully calculated prayer times: $prayerTimes")
            Result.success(prayerTimes)

        } catch (e: Exception) {
            Log.e("PrayerRepository", "Error calculating prayer times", e)
            Result.failure(e)
        }
    }

    override fun getPrayerTimesForOffset(locationData: LocationData, dayOffset: Int): Result<PrayerTimes> {
        val targetDate = LocalDate.now().plusDays(dayOffset.toLong())
        return getPrayerTimes(locationData, targetDate)
    }
}