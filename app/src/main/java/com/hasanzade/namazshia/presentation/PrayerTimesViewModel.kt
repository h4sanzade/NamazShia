package com.hasanzade.namazshia.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasanzade.namazshia.domain.DateInfo
import com.hasanzade.namazshia.domain.LocationInfo
import com.hasanzade.namazshia.domain.PrayerRepository
import com.hasanzade.namazshia.domain.PrayerTimes
import com.hasanzade.namazshia.location.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerTimesUiState())
    val uiState: StateFlow<PrayerTimesUiState> = _uiState.asStateFlow()

    init {
        checkLocationAndLoadPrayerTimes()
    }

    private fun checkLocationAndLoadPrayerTimes() {
        viewModelScope.launch {
            if (locationRepository.hasLocationPermission()) {
                val location = locationRepository.getCurrentLocation()
                if (location != null) {
                    loadPrayerTimes(location.city)
                    _uiState.value = _uiState.value.copy(
                        locationInfo = LocationInfo(
                            city = location.city.capitalize(),
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    // Fallback to default city
                    loadPrayerTimes("istanbul")
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
        loadPrayerTimes("istanbul") // Default city
    }

    fun loadPrayerTimes(city: String = "istanbul") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            prayerRepository.getPrayerTimes(city).fold(
                onSuccess = { prayerTimes ->
                    _uiState.value = _uiState.value.copy(
                        prayerTimes = prayerTimes,
                        dateInfo = createDateInfo(prayerTimes),
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

    fun navigateToNextDay() {
        val currentOffset = _uiState.value.dateInfo.dayOffset
        updateDateOffset(currentOffset + 1)
    }

    fun navigateToPreviousDay() {
        val currentOffset = _uiState.value.dateInfo.dayOffset
        updateDateOffset(currentOffset - 1)
    }

    private fun updateDateOffset(offset: Int) {
        val currentDateInfo = _uiState.value.dateInfo
        val updatedDateInfo = currentDateInfo.copy(
            dayOffset = offset,
            gregorianDate = calculateGregorianDate(offset),
            hijriDate = calculateHijriDate(offset)
        )
        _uiState.value = _uiState.value.copy(dateInfo = updatedDateInfo)
    }

    private fun createDateInfo(prayerTimes: PrayerTimes): DateInfo {
        return DateInfo(
            gregorianDate = prayerTimes.dateReadable.ifEmpty { calculateGregorianDate(0) },
            hijriDate = "Rabi' I 2, 1447",
            dayOffset = 0
        )
    }

    private fun calculateGregorianDate(offset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun calculateHijriDate(offset: Int): String {
        return "Rabi' I ${2 + offset}, 1447"
    }
}

data class PrayerTimesUiState(
    val prayerTimes: PrayerTimes? = null,
    val locationInfo: LocationInfo = LocationInfo("Istanbul"),
    val dateInfo: DateInfo = DateInfo("", ""),
    val isLoading: Boolean = false,
    val error: String? = null,
    val needsLocationPermission: Boolean = false
)