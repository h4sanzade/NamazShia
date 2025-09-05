package com.hasanzade.namazshia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasanzade.namazshia.data.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val quranRepository: QuranRepository
) : ViewModel() {

    private val _state = MutableStateFlow<QuranScreenState>(QuranScreenState.Loading)
    val state: StateFlow<QuranScreenState> = _state.asStateFlow()

    private val _surahs = MutableStateFlow<List<Surah>>(emptyList())
    val surahs: StateFlow<List<Surah>> = _surahs.asStateFlow()

    private val _ayahs = MutableStateFlow<List<Ayah>>(emptyList())
    val ayahs: StateFlow<List<Ayah>> = _ayahs.asStateFlow()

    private val _translations = MutableStateFlow<List<QuranTranslation>>(emptyList())
    val translations: StateFlow<List<QuranTranslation>> = _translations.asStateFlow()

    private val _searchResults = MutableStateFlow<List<QuranSearchResult>>(emptyList())
    val searchResults: StateFlow<List<QuranSearchResult>> = _searchResults.asStateFlow()

    private val _isLoadingDetail = MutableStateFlow(false)
    val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()

    fun loadSurahs() {
        viewModelScope.launch {
            try {
                _state.value = QuranScreenState.Loading

                quranRepository.getSurahList().fold(
                    onSuccess = { apiSurahs ->
                        val surahList = apiSurahs.map { apiSurah ->
                            Surah(
                                number = apiSurah.number,
                                name = apiSurah.name,
                                englishName = apiSurah.englishName,
                                englishNameTranslation = apiSurah.englishNameTranslation,
                                numberOfAyahs = apiSurah.numberOfAyahs,
                                revelationType = apiSurah.revelationType
                            )
                        }
                        _surahs.value = surahList
                        _state.value = QuranScreenState.SurahList
                    },
                    onFailure = { error ->
                        _state.value = QuranScreenState.Error("Failed to load Surahs: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = QuranScreenState.Error("Network error: ${e.message}")
            }
        }
    }

    fun loadSurahDetail(surah: Surah) {
        viewModelScope.launch {
            try {
                _isLoadingDetail.value = true
                _state.value = QuranScreenState.SurahDetail(surah)

                var arabicAyahs: List<Ayah> = emptyList()
                var translationAyahs: List<QuranTranslation> = emptyList()

                quranRepository.getSurahArabicText(surah.number).fold(
                    onSuccess = { apiAyahs ->
                        arabicAyahs = apiAyahs.map { apiAyah ->
                            Ayah(
                                number = apiAyah.number,
                                text = apiAyah.text,
                                numberInSurah = apiAyah.numberInSurah,
                                juz = apiAyah.juz ?: 1,
                                manzil = apiAyah.manzil ?: 1,
                                page = apiAyah.page ?: 1,
                                ruku = apiAyah.ruku ?: 1,
                                hizbQuarter = apiAyah.hizbQuarter ?: 1,
                                sajda = apiAyah.sajda ?: false
                            )
                        }
                        _ayahs.value = arabicAyahs
                    },
                    onFailure = { error ->
                        println("Arabic text API error: ${error.message}")
                        _ayahs.value = emptyList()
                    }
                )

                quranRepository.getSurahTranslation(surah.number).fold(
                    onSuccess = { apiTranslations ->
                        translationAyahs = apiTranslations.mapIndexed { index, apiTranslation ->
                            QuranTranslation(
                                ayah = apiTranslation.number,
                                text = apiTranslation.text
                            )
                        }
                        _translations.value = translationAyahs
                    },
                    onFailure = { error ->
                        println("Translation API error: ${error.message}")
                        _translations.value = emptyList()
                    }
                )

                _isLoadingDetail.value = false

            } catch (e: Exception) {
                _isLoadingDetail.value = false
                _state.value = QuranScreenState.Error("Failed to load Surah details: ${e.message}")
            }
        }
    }

    fun searchQuran(query: String) {
        viewModelScope.launch {
            try {
                _state.value = QuranScreenState.Loading

                val searchResults = searchThroughApi(query)
                _searchResults.value = searchResults

                _state.value = QuranScreenState.SearchResults(query, searchResults)

            } catch (e: Exception) {
                _state.value = QuranScreenState.Error("Search failed: ${e.message}")
            }
        }
    }

    fun navigateToSurahList() {
        _state.value = QuranScreenState.SurahList
    }

    fun navigateToAyah(searchResult: QuranSearchResult) {
        viewModelScope.launch {
            val surah = _surahs.value.find { it.number == searchResult.surahNumber }
            if (surah != null) {
                loadSurahDetail(surah)
            }
        }
    }

    private suspend fun searchThroughApi(query: String): List<QuranSearchResult> {
        val results = mutableListOf<QuranSearchResult>()
        val queryLower = query.lowercase()

        for (surahNumber in 1..10) {
            try {
                val surah = _surahs.value.find { it.number == surahNumber }
                if (surah != null) {
                    var arabicTexts: List<String> = emptyList()
                    var translationTexts: List<String> = emptyList()

                    quranRepository.getSurahArabicText(surahNumber).fold(
                        onSuccess = { apiAyahs ->
                            arabicTexts = apiAyahs.map { it.text }
                        },
                        onFailure = {
                            println("Arabic API failed for search in surah $surahNumber")
                        }
                    )

                    quranRepository.getSurahTranslation(surahNumber).fold(
                        onSuccess = { apiTranslations ->
                            translationTexts = apiTranslations.map { it.text }

                            translationTexts.forEachIndexed { index, translation ->
                                if (translation.lowercase().contains(queryLower)) {
                                    val arabicText = arabicTexts.getOrNull(index) ?: ""
                                    results.add(
                                        QuranSearchResult(
                                            surahNumber = surahNumber,
                                            surahName = surah.englishName,
                                            ayahNumber = index + 1,
                                            arabicText = arabicText,
                                            translation = translation
                                        )
                                    )
                                }
                            }
                        },
                        onFailure = {
                            println("Translation API failed for search in surah $surahNumber")
                        }
                    )
                }
            } catch (e: Exception) {
                println("Search error for surah $surahNumber: ${e.message}")
                continue
            }

            if (results.size >= 15) break
        }

        return results
    }
}