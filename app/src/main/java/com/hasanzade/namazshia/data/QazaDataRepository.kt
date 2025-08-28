package com.hasanzade.namazshia.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QazaDataRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("qaza_data", Context.MODE_PRIVATE)

    // Qaza prayer counts
    fun saveQazaPrayerCount(prayerName: String, count: Int) {
        sharedPreferences.edit()
            .putInt("qaza_$prayerName", count)
            .apply()
    }

    fun getQazaPrayerCount(prayerName: String): Int {
        return sharedPreferences.getInt("qaza_$prayerName", 0)
    }

    // Safar prayer counts
    fun saveSafarPrayerCount(prayerName: String, count: Int) {
        sharedPreferences.edit()
            .putInt("safar_$prayerName", count)
            .apply()
    }

    fun getSafarPrayerCount(prayerName: String): Int {
        return sharedPreferences.getInt("safar_$prayerName", 0)
    }

    // Tasbeeh state
    fun saveTasbeehState(currentPhase: Int, currentCount: Int, isCompleted: Boolean) {
        sharedPreferences.edit()
            .putInt("tasbeeh_phase", currentPhase)
            .putInt("tasbeeh_count", currentCount)
            .putBoolean("tasbeeh_completed", isCompleted)
            .apply()
    }

    fun getTasbeehState(): Triple<Int, Int, Boolean> {
        val phase = sharedPreferences.getInt("tasbeeh_phase", 0)
        val count = sharedPreferences.getInt("tasbeeh_count", 0)
        val completed = sharedPreferences.getBoolean("tasbeeh_completed", false)
        return Triple(phase, count, completed)
    }

    // Reset all qaza data
    fun resetAllQazaData() {
        val editor = sharedPreferences.edit()

        // Reset all qaza prayers
        val qazaPrayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha", "Ramadan")
        qazaPrayers.forEach { prayer ->
            editor.putInt("qaza_$prayer", 0)
        }

        // Reset all safar prayers
        val safarPrayers = listOf("Safar Dhuhr", "Safar Asr", "Safar Isha")
        safarPrayers.forEach { prayer ->
            editor.putInt("safar_$prayer", 0)
        }

        editor.apply()
    }

    // Reset tasbeeh data
    fun resetTasbeehData() {
        sharedPreferences.edit()
            .putInt("tasbeeh_phase", 0)
            .putInt("tasbeeh_count", 0)
            .putBoolean("tasbeeh_completed", false)
            .apply()
    }

    // Get all qaza prayers with their counts
    fun getAllQazaCounts(): Map<String, Int> {
        val qazaPrayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha", "Ramadan")
        return qazaPrayers.associateWith { getQazaPrayerCount(it) }
    }

    // Get all safar prayers with their counts
    fun getAllSafarCounts(): Map<String, Int> {
        val safarPrayers = listOf("Safar Dhuhr", "Safar Asr", "Safar Isha")
        return safarPrayers.associateWith { getSafarPrayerCount(it) }
    }

    // Auto calculation data
    fun saveAutoCalculationData(
        gender: String,
        birthDate: String?,
        prayerStartDate: String?
    ) {
        sharedPreferences.edit()
            .putString("auto_calc_gender", gender)
            .putString("auto_calc_birth_date", birthDate)
            .putString("auto_calc_prayer_start_date", prayerStartDate)
            .apply()
    }

    fun getAutoCalculationData(): Triple<String, String?, String?> {
        val gender = sharedPreferences.getString("auto_calc_gender", "Male") ?: "Male"
        val birthDate = sharedPreferences.getString("auto_calc_birth_date", null)
        val prayerStartDate = sharedPreferences.getString("auto_calc_prayer_start_date", null)
        return Triple(gender, birthDate, prayerStartDate)
    }

    // Prayer times user preferences
    fun saveLocationPreference(city: String, latitude: Double, longitude: Double) {
        sharedPreferences.edit()
            .putString("user_city", city)
            .putFloat("user_latitude", latitude.toFloat())
            .putFloat("user_longitude", longitude.toFloat())
            .apply()
    }

    fun getLocationPreference(): Triple<String?, Double?, Double?> {
        val city = sharedPreferences.getString("user_city", null)
        val latitude = if (sharedPreferences.contains("user_latitude")) {
            sharedPreferences.getFloat("user_latitude", 0f).toDouble()
        } else null
        val longitude = if (sharedPreferences.contains("user_longitude")) {
            sharedPreferences.getFloat("user_longitude", 0f).toDouble()
        } else null
        return Triple(city, latitude, longitude)
    }

    // Clear all app data (for complete reset)
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
}