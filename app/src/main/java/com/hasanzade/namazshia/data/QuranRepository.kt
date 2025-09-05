package com.hasanzade.namazshia.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepository @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val baseUrl = "https://api.alquran.cloud/v1"
    private val TAG = "QuranRepository"

    // API Response models
    @Serializable
    data class ApiResponse<T>(
        val code: Int,
        val status: String,
        val data: T
    )

    @Serializable
    data class MetaData(
        val surahs: SurahsData
    )

    @Serializable
    data class SurahsData(
        val count: Int,
        val references: List<SurahInfo>
    )

    @Serializable
    data class SurahInfo(
        val number: Int,
        val name: String,
        val englishName: String,
        val englishNameTranslation: String,
        val numberOfAyahs: Int,
        val revelationType: String
    )

    @Serializable
    data class SurahData(
        val number: Int,
        val name: String,
        val englishName: String,
        val englishNameTranslation: String,
        val numberOfAyahs: Int,
        val revelationType: String,
        val ayahs: List<AyahInfo>
    )

    @Serializable
    data class AyahInfo(
        val number: Int,
        val text: String,
        val numberInSurah: Int,
        val juz: Int? = null,
        val manzil: Int? = null,
        val page: Int? = null,
        val ruku: Int? = null,
        val hizbQuarter: Int? = null,
        val sajda: Boolean? = null
    )

    // Domain models
    data class AlQuranSurah(
        val number: Int,
        val name: String,
        val englishName: String,
        val englishNameTranslation: String,
        val numberOfAyahs: Int,
        val revelationType: String
    )

    data class AlQuranAyah(
        val number: Int,
        val text: String,
        val numberInSurah: Int,
        val juz: Int? = null,
        val manzil: Int? = null,
        val page: Int? = null,
        val ruku: Int? = null,
        val hizbQuarter: Int? = null,
        val sajda: Boolean? = null
    )

    suspend fun getSurahList(): Result<List<AlQuranSurah>> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Loading surah list from API...")

            val url = "$baseUrl/meta"
            val response = makeApiCall(url)
            val apiResponse = json.decodeFromString<ApiResponse<MetaData>>(response)

            if (apiResponse.code == 200) {
                val surahs = apiResponse.data.surahs.references.map { surahInfo ->
                    AlQuranSurah(
                        number = surahInfo.number,
                        name = surahInfo.name,
                        englishName = surahInfo.englishName,
                        englishNameTranslation = surahInfo.englishNameTranslation,
                        numberOfAyahs = surahInfo.numberOfAyahs,
                        revelationType = surahInfo.revelationType
                    )
                }
                Log.d(TAG, "Successfully loaded ${surahs.size} surahs")
                Result.success(surahs)
            } else {
                Log.e(TAG, "API returned error: ${apiResponse.status}")
                Result.failure(Exception("API returned error: ${apiResponse.status}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load surahs: ${e.message}", e)
            val fallbackSurahs = getFallbackSurahs()
            Log.d(TAG, "Using fallback data: ${fallbackSurahs.size} surahs")
            Result.success(fallbackSurahs)
        }
    }

    suspend fun getSurahArabicText(surahNumber: Int): Result<List<AlQuranAyah>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Log.d(TAG, "Loading Arabic text for surah $surahNumber...")

                val url = "$baseUrl/surah/$surahNumber/quran-uthmani"
                val response = makeApiCall(url)
                val apiResponse = json.decodeFromString<ApiResponse<SurahData>>(response)

                if (apiResponse.code == 200) {
                    val ayahs = apiResponse.data.ayahs.map { ayahInfo ->
                        AlQuranAyah(
                            number = ayahInfo.number,
                            text = ayahInfo.text,
                            numberInSurah = ayahInfo.numberInSurah,
                            juz = ayahInfo.juz,
                            manzil = ayahInfo.manzil,
                            page = ayahInfo.page,
                            ruku = ayahInfo.ruku,
                            hizbQuarter = ayahInfo.hizbQuarter,
                            sajda = ayahInfo.sajda
                        )
                    }
                    Log.d(TAG, "Successfully loaded ${ayahs.size} Arabic ayahs")
                    Result.success(ayahs)
                } else {
                    Log.e(TAG, "Arabic text API error: ${apiResponse.status}")
                    val fallbackAyahs = getFallbackArabicText(surahNumber)
                    Result.success(fallbackAyahs)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load Arabic text: ${e.message}", e)
                val fallbackAyahs = getFallbackArabicText(surahNumber)
                Result.success(fallbackAyahs)
            }
        }

    suspend fun getSurahTranslation(surahNumber: Int): Result<List<AlQuranAyah>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Log.d(TAG, "Loading translation for surah $surahNumber...")

                val url = "$baseUrl/surah/$surahNumber/en.sahih"
                val response = makeApiCall(url)
                val apiResponse = json.decodeFromString<ApiResponse<SurahData>>(response)

                if (apiResponse.code == 200) {
                    val translations = apiResponse.data.ayahs.map { ayahInfo ->
                        AlQuranAyah(
                            number = ayahInfo.number,
                            text = ayahInfo.text,
                            numberInSurah = ayahInfo.numberInSurah,
                            juz = ayahInfo.juz,
                            manzil = ayahInfo.manzil,
                            page = ayahInfo.page,
                            ruku = ayahInfo.ruku,
                            hizbQuarter = ayahInfo.hizbQuarter,
                            sajda = ayahInfo.sajda
                        )
                    }
                    Log.d(TAG, "Successfully loaded ${translations.size} translations")
                    Result.success(translations)
                } else {
                    Log.e(TAG, "Translation API error: ${apiResponse.status}")
                    val fallbackTranslations = getFallbackTranslation(surahNumber)
                    Result.success(fallbackTranslations)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load translation: ${e.message}", e)
                val fallbackTranslations = getFallbackTranslation(surahNumber)
                Result.success(fallbackTranslations)
            }
        }

    private fun makeApiCall(url: String): String {
        Log.d(TAG, "Making API call to: $url")
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 30000
                readTimeout = 30000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "NamazShia/1.0")
            }

            val responseCode = connection.responseCode
            Log.d(TAG, "HTTP Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG, "Response received successfully")
                response
            } else {
                val errorMessage = "HTTP $responseCode: ${connection.responseMessage}"
                Log.e(TAG, "HTTP Error: $errorMessage")
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error: ${e.message}", e)
            throw Exception("Network error: ${e.message}")
        } finally {
            try {
                connection.disconnect()
            } catch (e: Exception) {
                Log.w(TAG, "Error disconnecting: ${e.message}")
            }
        }
    }

    private fun getFallbackSurahs(): List<AlQuranSurah> {
        return listOf(
            AlQuranSurah(1, "سُورَةُ ٱلْفَاتِحَةِ", "Al-Faatiha", "The Opening", 7, "Meccan"),
            AlQuranSurah(2, "سُورَةُ البَقَرَةِ", "Al-Baqara", "The Cow", 286, "Medinan"),
            AlQuranSurah(3, "سُورَةُ آلِ عِمْرَانَ", "Aal-i-Imraan", "The Family of Imraan", 200, "Medinan"),
            AlQuranSurah(112, "سُورَةُ الإِخْلاصِ", "Al-Ikhlaas", "Sincerity", 4, "Meccan"),
            AlQuranSurah(113, "سُورَةُ الفَلَقِ", "Al-Falaq", "The Dawn", 5, "Meccan"),
            AlQuranSurah(114, "سُورَةُ النَّاسِ", "An-Naas", "Mankind", 6, "Meccan")
        )
    }

    private fun getFallbackArabicText(surahNumber: Int): List<AlQuranAyah> {
        return when (surahNumber) {
            1 -> listOf(
                AlQuranAyah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", 1),
                AlQuranAyah(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", 2),
                AlQuranAyah(3, "الرَّحْمَٰنِ الرَّحِيمِ", 3),
                AlQuranAyah(4, "مَالِكِ يَوْمِ الدِّينِ", 4),
                AlQuranAyah(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", 5),
                AlQuranAyah(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", 6),
                AlQuranAyah(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", 7)
            )
            112 -> listOf(
                AlQuranAyah(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", 1),
                AlQuranAyah(2, "اللَّهُ الصَّمَدُ", 2),
                AlQuranAyah(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", 3),
                AlQuranAyah(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", 4)
            )
            else -> listOf(
                AlQuranAyah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", 1)
            )
        }
    }

    private fun getFallbackTranslation(surahNumber: Int): List<AlQuranAyah> {
        return when (surahNumber) {
            1 -> listOf(
                AlQuranAyah(1, "In the name of Allah, the Entirely Merciful, the Especially Merciful.", 1),
                AlQuranAyah(2, "[All] praise is [due] to Allah, Lord of the worlds -", 2),
                AlQuranAyah(3, "The Entirely Merciful, the Especially Merciful,", 3),
                AlQuranAyah(4, "Sovereign of the Day of Recompense.", 4),
                AlQuranAyah(5, "It is You we worship and You we ask for help.", 5),
                AlQuranAyah(6, "Guide us to the straight path -", 6),
                AlQuranAyah(7, "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", 7)
            )
            112 -> listOf(
                AlQuranAyah(1, "Say, \"He is Allah, [who is] One,", 1),
                AlQuranAyah(2, "Allah, the Eternal Refuge.", 2),
                AlQuranAyah(3, "He neither begets nor is born,", 3),
                AlQuranAyah(4, "Nor is there to Him any equivalent.\"", 4)
            )
            else -> listOf(
                AlQuranAyah(1, "In the name of Allah, the Entirely Merciful, the Especially Merciful.", 1)
            )
        }
    }
}