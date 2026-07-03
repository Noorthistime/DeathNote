package com.example.deathnote.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deathnote.domain.model.JournalEntry
import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.domain.model.Page
import com.example.deathnote.domain.repository.JournalRepository
import com.example.deathnote.domain.repository.NotebookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchTab {
    ALL, NOTEBOOKS, PAGES, JOURNAL
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(SearchTab.ALL)
    val selectedTab = _selectedTab.asStateFlow()

    private val _searchResults = MutableStateFlow<SearchResults>(SearchResults())
    val searchResults = _searchResults.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) {
            performSearch(query)
        } else {
            _searchResults.value = SearchResults()
        }
    }

    fun onTabSelected(tab: SearchTab) {
        _selectedTab.value = tab
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            val notebooksDeferred = async { notebookRepository.searchNotebooks(query) }
            val pagesDeferred = async { notebookRepository.searchPages(query) }
            val entriesDeferred = async { journalRepository.searchEntries(query) }
            
            _searchResults.value = SearchResults(
                notebooks = notebooksDeferred.await(),
                pages = pagesDeferred.await(),
                journalEntries = entriesDeferred.await()
            )
        }
    }
}

data class SearchResults(
    val notebooks: List<Notebook> = emptyList(),
    val pages: List<Page> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList()
)
