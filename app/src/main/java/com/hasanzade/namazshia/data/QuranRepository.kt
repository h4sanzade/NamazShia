package com.hasanzade.namazshia.data

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

    @Serializable
    data class AlQuranResponse(
        val code: Int,
        val status: String,
        val data: AlQuranData
    )

    @Serializable
    data class AlQuranData(
        val surahs: List<AlQuranSurah>? = null,
        val ayahs: List<AlQuranAyah>? = null
    )

    @Serializable
    data class AlQuranSurah(
        val number: Int,
        val name: String,
        val englishName: String,
        val englishNameTranslation: String,
        val numberOfAyahs: Int,
        val revelationType: String
    )

    @Serializable
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

    @Singleton
    class QuranRepository @Inject constructor() {

        companion object {
            private const val BASE_URL = "https://api.alquran.cloud/v1"
        }

        private val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        suspend fun getSurahList(): Result<List<AlQuranSurah>> = withContext(Dispatchers.IO) {
            return@withContext try {
                val response = makeApiCall("$BASE_URL/meta")
                val alQuranResponse = json.decodeFromString<AlQuranResponse>(response)
                val surahs = alQuranResponse.data.surahs
                    ?: throw Exception("No surahs found in API response")
                Result.success(surahs)
            } catch (e: Exception) {
                Result.failure(Exception("Failed to fetch surahs: ${e.message}"))
            }
        }

        suspend fun getSurahArabicText(surahNumber: Int): Result<List<AlQuranAyah>> =
            withContext(Dispatchers.IO) {
                return@withContext try {
                    val response = makeApiCall("$BASE_URL/surah/$surahNumber")
                    val alQuranResponse = json.decodeFromString<AlQuranResponse>(response)
                    val ayahs = alQuranResponse.data.ayahs
                        ?: throw Exception("No ayahs found in API response")
                    Result.success(ayahs)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to fetch Arabic text: ${e.message}"))
                }
            }

        suspend fun getSurahTranslation(surahNumber: Int): Result<List<AlQuranAyah>> =
            withContext(Dispatchers.IO) {
                return@withContext try {
                    val response = makeApiCall("$BASE_URL/surah/$surahNumber/en.sahih")
                    val alQuranResponse = json.decodeFromString<AlQuranResponse>(response)
                    val ayahs = alQuranResponse.data.ayahs
                        ?: throw Exception("No translation found in API response")
                    Result.success(ayahs)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to fetch translation: ${e.message}"))
                }
            }

        private fun makeApiCall(url: String): String {
            val connection = URL(url).openConnection() as HttpURLConnection
            return try {
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 25000
                    readTimeout = 25000
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "NamazShia/1.0")
                    setRequestProperty("Cache-Control", "no-cache")
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    throw Exception("HTTP $responseCode: ${connection.responseMessage}")
                }
            } catch (e: Exception) {
                throw Exception("Network error: ${e.message}")
            } finally {
                try {
                    connection.disconnect()
                } catch (e: Exception) {
                    // Ignore disconnect errors
                }
            }
        }
    }
    }