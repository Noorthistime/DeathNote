package com.example.deathnote.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deathnote.domain.model.JournalEntry
import com.example.deathnote.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: JournalRepository
) : ViewModel() {

    private val _currentEntry = MutableStateFlow<JournalEntry?>(null)
    val currentEntry: StateFlow<JournalEntry?> = _currentEntry.asStateFlow()

    val allEntries: StateFlow<List<JournalEntry>> = repository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun normalizeDate(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun loadEntryForDate(timestamp: Long) {
        val normalizedDate = normalizeDate(timestamp)
        viewModelScope.launch {
            val entry = repository.getEntryByDate(normalizedDate) ?: JournalEntry(
                id = UUID.randomUUID().toString(),
                date = normalizedDate,
                wokeUpAt = null,
                content = null,
                felt = null,
                screenTime = null,
                sleptAt = null,
                sleptFor = null,
                moodPercentage = 0
            )
            _currentEntry.value = entry
        }
    }

    fun updateEntry(
        wokeUpAt: String? = null,
        content: String? = null,
        felt: String? = null,
        screenTime: String? = null,
        sleptAt: String? = null,
        sleptFor: String? = null
    ) {
        _currentEntry.value?.let { entry ->
            val updated = entry.copy(
                wokeUpAt = wokeUpAt ?: entry.wokeUpAt,
                content = content ?: entry.content,
                felt = felt ?: entry.felt,
                screenTime = screenTime ?: entry.screenTime,
                sleptAt = sleptAt ?: entry.sleptAt,
                sleptFor = sleptFor ?: entry.sleptFor
            )
            _currentEntry.value = updated
            viewModelScope.launch {
                repository.saveEntry(updated)
            }
        }
    }

    fun calculateSleptFor(currentDate: Long, currentWokeUp: String, wokeUpAmPm: String) {
        viewModelScope.launch {
            // Requirement 1: Take "slept at" of previous date
            val prevDate = normalizeDate(currentDate - 24 * 60 * 60 * 1000)
            val prevEntry = repository.getEntryByDate(prevDate)
            val prevSleptAt = prevEntry?.sleptAt
            
            val wokeUpClean = currentWokeUp.replace(" AM", "").replace(" PM", "").filter { it.isDigit() }
            val sleptAtClean = prevSleptAt?.replace(" AM", "")?.replace(" PM", "")?.filter { it.isDigit() } ?: ""

            if (wokeUpClean.isEmpty() || sleptAtClean.isEmpty()) {
                updateEntry(sleptFor = "")
                return@launch
            }

            try {
                fun toMinutes(time: String, providedAmPm: String? = null): Int {
                    // Remove any existing AM/PM to avoid double-processing
                    val cleanTime = time.replace("AM", "", ignoreCase = true)
                        .replace("PM", "", ignoreCase = true)
                        .trim()
                    
                    if (cleanTime.isEmpty() || cleanTime == ":") return 0
                    
                    val parts = cleanTime.split(":")
                    var h = if (parts[0].isNotBlank()) parts[0].trim().toInt() else 0
                    val m = if (parts.size > 1 && parts[1].isNotBlank()) {
                        parts[1].trim().filter { it.isDigit() }.toInt()
                    } else 0
                    
                    // Priority: 1. Suffix in string, 2. provided argument, 3. Default PM (for sleep) or AM (for wake)
                    val amPm = if (time.contains("PM", ignoreCase = true)) "PM"
                              else if (time.contains("AM", ignoreCase = true)) "AM"
                              else providedAmPm ?: "PM"

                    if (amPm == "PM" && h != 12) h += 12
                    if (amPm == "AM" && h == 12) h = 0
                    return h * 60 + m
                }

                val start = toMinutes(prevSleptAt!!) // Uses its own suffix if present
                val end = toMinutes(currentWokeUp, wokeUpAmPm)

                // The logic: (Woke Up Time) - (Previous Day's Slept At)
                // If it's negative, it means it crossed midnight (e.g., 11 PM to 7 AM)
                var diff = end - start
                if (diff < 0) {
                    diff += 1440
                }
                
                val h = diff / 60
                val m = diff % 60
                
                updateEntry(sleptFor = String.format(Locale.getDefault(), "%d:%02d", h, m))
            } catch (e: Exception) {
                // Ignore invalid input
            }
        }
    }
}
