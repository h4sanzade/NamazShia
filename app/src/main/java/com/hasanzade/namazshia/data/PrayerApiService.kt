package com.hasanzade.namazshia.data

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PrayerApiService {
    @GET("pray/all")
    suspend fun getPrayerTimes(
        @Query("city") city: String,
        @Header("authorization") apiKey: String = "apikey 0BkHILtMEPzYJMVg6pJ31f:1u4BXhl6HSCelrZNgaAkLY",
        @Header("content-type") contentType: String = "application/json"
    ): PrayerTimesResponse
}
