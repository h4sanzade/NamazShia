package com.hasanzade.namazshia.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasanzade.namazshia.domain.DateInfo
import com.hasanzade.namazshia.domain.LocationInfo
import com.hasanzade.namazshia.domain.PrayerRepository
import com.hasanzade.namazshia.domain.PrayerTimes
import com.hasanzade.namazshia.location.LocationData
import com.hasanzade.namazshia.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerTimesUiState())
    val uiState: StateFlow<PrayerTimesUiState> = _uiState.asStateFlow()

    private var currentLocationData: LocationData? = null
    private var currentDayOffset = 0

    init {
        checkLocationAndLoadPrayerTimes()
    }

    private fun checkLocationAndLoadPrayerTimes() {
        viewModelScope.launch {
            if (locationRepository.hasLocationPermission()) {
                val location = locationRepository.getCurrentLocation()
                if (location != null) {
                    currentLocationData = location
                    loadPrayerTimes(location)
                    _uiState.value = _uiState.value.copy(
                        locationInfo = LocationInfo(
                            city = location.city.capitalize(),
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    val defaultLocation = LocationData(40.4093, 49.8671, "baku")
                    currentLocationData = defaultLocation
                    loadPrayerTimes(defaultLocation)
                    _uiState.value = _uiState.value.copy(
                        locationInfo = LocationInfo("Baku", 40.4093, 49.8671)
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(needsLocationPermission = true)
            }
        }
    }

    fun onLocationPermissionGranted() {
        _uiState.value = _uiState.value.copy(needsLocationPermission = false)
        checkLocationAndLoadPrayerTimes()
    }

    fun onLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(needsLocationPermission = false)
        val defaultLocation = LocationData(40.4093, 49.8671, "baku")
        currentLocationData = defaultLocation
        loadPrayerTimes(defaultLocation)
        _uiState.value = _uiState.value.copy(
            locationInfo = LocationInfo("Baku", 40.4093, 49.8671)
        )
    }

    private fun loadPrayerTimes(locationData: LocationData) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            prayerRepository.getPrayerTimesForOffset(locationData, currentDayOffset).fold(
                onSuccess = { prayerTimes ->
                    _uiState.value = _uiState.value.copy(
                        prayerTimes = prayerTimes,
                        dateInfo = createDateInfo(currentDayOffset),
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Unknown error occurred",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun loadPrayerTimes() {
        currentLocationData?.let { locationData ->
            loadPrayerTimes(locationData)
        }
    }

    fun navigateToNextDay() {
        currentDayOffset += 1
        currentLocationData?.let { locationData ->
            loadPrayerTimes(locationData)
        }
    }

    fun navigateToPreviousDay() {
        currentDayOffset -= 1
        currentLocationData?.let { locationData ->
            loadPrayerTimes(locationData)
        }
    }

    private fun createDateInfo(dayOffset: Int): DateInfo {
        val targetDate = LocalDate.now().plusDays(dayOffset.toLong())
        val gregorianFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault())

        return DateInfo(
            gregorianDate = targetDate.format(gregorianFormatter),
            hijriDate = calculateHijriDate(dayOffset),
            dayOffset = dayOffset
        )
    }

    private fun calculateHijriDate(offset: Int): String {

        val baseDay = 15
        val newDay = baseDay + offset
        return "Rabi' al-Awwal $newDay, 1446"
    }
}

data class PrayerTimesUiState(
    val prayerTimes: PrayerTimes? = null,
    val locationInfo: LocationInfo = LocationInfo("Baku"),
    val dateInfo: DateInfo = DateInfo("", ""),
    val isLoading: Boolean = false,
    val error: String? = null,
    val needsLocationPermission: Boolean = false
)