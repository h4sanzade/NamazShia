package com.hasanzade.namazshia

import java.time.*
import kotlin.math.*


class PrayerTimesCalculator(
    private val fajrAngle: Double = 16.0,
    private val ishaAngle: Double = 14.0,
    private val maghribAngle: Double = 4.0,
    private val asrShadowFactor: Int = 1,
    private val highLatRule: HighLatRule = HighLatRule.MIDDLE_OF_NIGHT
) {

    enum class HighLatRule { MIDDLE_OF_NIGHT, SEVENTH_OF_NIGHT, ANGLE_BASED }

    data class PrayerTimesResult(
        val fajr: LocalTime,
        val sunrise: LocalTime,
        val dhuhr: LocalTime,
        val asr: LocalTime,
        val maghrib: LocalTime,
        val isha: LocalTime,
        val date: LocalDate
    )

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        date: LocalDate = LocalDate.now(),
        timezone: Int = 4 // UTC+4 Bakı üçün
    ): PrayerTimesResult {

        val zoneId = ZoneOffset.ofHours(timezone)
        val offsetHours = timezone.toDouble()

        fun julianCentury(d: Double): Double {
            val JD = (date.toEpochDay() + 2440588) + d / 24.0 + 0.5
            return (JD - 2451545.0) / 36525.0
        }

        fun solarDeclinationEqTime(d: Double): Pair<Double, Double> {
            val T = julianCentury(d)
            val L0 = (280.46646 + 36000.76983 * T + 0.0003032 * T * T).normalize360()
            val M = 357.52911 + 35999.05029 * T - 0.0001537 * T * T
            val e = 0.016708634 - 0.000042037 * T - 0.0000001267 * T * T
            val C = (1.914602 - 0.004817 * T - 0.000014 * T * T) * sinD(M) +
                    (0.019993 - 0.000101 * T) * sinD(2 * M) +
                    0.000289 * sinD(3 * M)
            val trueLong = L0 + C
            val omega = 125.04 - 1934.136 * T
            val lambda = trueLong - 0.00569 - 0.00478 * sinD(omega)
            val epsilon0 = 23.439291 - 0.0130042 * T - 1.64e-7 * T * T + 5.04e-7 * T * T * T
            val epsilon = epsilon0 + 0.00256 * cosD(omega)
            val decl = asinD(sinD(epsilon) * sinD(lambda))

            val y = tanD(epsilon / 2).pow(2)
            val E = 4 * rad2deg(
                y * sin(2 * deg2rad(L0)) -
                        2 * e * sinD(M) +
                        4 * e * y * sinD(M) * cos(2 * deg2rad(L0)) -
                        0.5 * y * y * sin(4 * deg2rad(L0)) -
                        1.25 * e * e * sin(2 * deg2rad(M))
            )
            return decl to E
        }

        fun dhuhrTime(): Double {
            val (_, E) = solarDeclinationEqTime(12.0)
            return 12 + offsetHours - longitude / 15.0 - E / 60.0
        }

        fun timeForAltitude(altitude: Double, morning: Boolean): Double {
            var lo = if (morning) 0.0 else 12.0
            var hi = if (morning) 12.0 else 24.0
            repeat(40) {
                val mid = (lo + hi) / 2
                val (decl, E) = solarDeclinationEqTime(mid)
                val noon = 12 + offsetHours - longitude / 15.0 - E / 60.0
                val H = (mid - noon) * 15.0
                val alt = asinD(
                    sinD(latitude) * sinD(decl) + cosD(latitude) * cosD(decl) * cosD(H)
                )
                if (alt > altitude) {
                    if (morning) hi = mid else lo = mid
                } else {
                    if (morning) lo = mid else hi = mid
                }
            }
            return (lo + hi) / 2
        }

        val sunriseTime = timeForAltitude(-0.833, morning = true)
        val sunsetTime = timeForAltitude(-0.833, morning = false)
        val dhuhr = dhuhrTime()

        fun asrTime(): Double {
            fun targetAlt(d: Double): Double {
                val (decl, _) = solarDeclinationEqTime(d)
                val A = abs(latitude - decl)
                return -atanD(1.0 / (asrShadowFactor + tanD(A)))
            }

            var lo = dhuhr
            var hi = sunsetTime
            repeat(35) {
                val mid = (lo + hi) / 2
                val (decl, E) = solarDeclinationEqTime(mid)
                val noon = 12 + offsetHours - longitude / 15.0 - E / 60.0
                val H = (mid - noon) * 15.0
                val alt = asinD(sinD(latitude) * sinD(decl) + cosD(latitude) * cosD(decl) * cosD(H))
                val target = targetAlt(mid)
                if (alt > target) lo = mid else hi = mid
            }
            return (lo + hi) / 2
        }

        fun twilight(angle: Double, morning: Boolean): Double {
            val raw = timeForAltitude(-angle, morning)
            val night = (24 - (sunsetTime - sunriseTime))
            return when (highLatRule) {
                HighLatRule.MIDDLE_OF_NIGHT -> {
                    if (morning) maxOf(raw, sunriseTime - night / 2)
                    else minOf(raw, sunsetTime + night / 2)
                }
                HighLatRule.SEVENTH_OF_NIGHT -> {
                    if (morning) maxOf(raw, sunriseTime - night / 7)
                    else minOf(raw, sunsetTime + night / 7)
                }
                HighLatRule.ANGLE_BASED -> raw
            }
        }

        val fajrTime = twilight(fajrAngle, morning = true)
        val ishaTime = twilight(ishaAngle, morning = false)

        val maghribTime = sunsetTime + (maghribAngle / 15.0)
        val asr = asrTime()

        fun toLocalTime(hours: Double): LocalTime {
            val h = floor(hours).toInt()
            val m = floor((hours - h) * 60).toInt()
            val s = floor(((hours - h) * 60 - m) * 60).toInt()
            return LocalTime.of(((h % 24) + 24) % 24, m, s)
        }

        return PrayerTimesResult(
            fajr = toLocalTime(fajrTime),
            sunrise = toLocalTime(sunriseTime),
            dhuhr = toLocalTime(dhuhr),
            asr = toLocalTime(asr),
            maghrib = toLocalTime(maghribTime),
            isha = toLocalTime(ishaTime),
            date = date
        )
    }

    // Trigonometrik helper funksiyalar
    private fun Double.normalize360() = ((this % 360) + 360) % 360
    private fun sinD(d: Double) = sin(Math.toRadians(d))
    private fun cosD(d: Double) = cos(Math.toRadians(d))
    private fun tanD(d: Double) = tan(Math.toRadians(d))
    private fun asinD(x: Double) = Math.toDegrees(asin(x.coerceIn(-1.0, 1.0)))
    private fun acosD(x: Double) = Math.toDegrees(acos(x.coerceIn(-1.0, 1.0)))
    private fun atanD(x: Double) = Math.toDegrees(atan(x))
    private fun deg2rad(d: Double) = Math.toRadians(d)
    private fun rad2deg(r: Double) = Math.toDegrees(r)
}