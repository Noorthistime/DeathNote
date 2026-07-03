package com.example.deathnote.ui.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.domain.model.Section
import com.example.deathnote.domain.model.Page
import com.example.deathnote.domain.repository.NotebookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    val repository: NotebookRepository
) : ViewModel() {

    val notebooks: StateFlow<List<Notebook>> = repository.getAllNotebooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedNotebookId = MutableStateFlow<String?>(null)
    val selectedNotebookId = _selectedNotebookId.asStateFlow()

    val selectedNotebook: StateFlow<Notebook?> = combine(notebooks, _selectedNotebookId) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sections: StateFlow<List<Section>> = _selectedNotebookId
        .flatMapLatest { id ->
            if (id != null) repository.getSectionsByNotebook(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createNotebook(name: String, isLocked: Boolean, password: String?) {
        viewModelScope.launch {
            repository.addNotebook(name, isLocked, password)
        }
    }

    fun updateNotebook(notebook: Notebook) {
        viewModelScope.launch {
            repository.updateNotebook(notebook)
        }
    }

    fun deleteNotebook(notebook: Notebook) {
        viewModelScope.launch {
            repository.deleteNotebook(notebook)
        }
    }

    fun selectNotebook(id: String) {
        _selectedNotebookId.value = id
    }

    fun addSection(name: String) {
        val notebookId = _selectedNotebookId.value ?: return
        viewModelScope.launch {
            repository.addSection(notebookId, name)
        }
    }

    fun updateSection(section: Section) {
        viewModelScope.launch {
            repository.updateSection(section)
        }
    }

    fun deleteSection(section: Section) {
        viewModelScope.launch {
            repository.deleteSection(section)
        }
    }

    fun getPagesForSection(sectionId: String): Flow<List<Page>> {
        return repository.getPagesBySection(sectionId)
    }

    fun addPage(sectionId: String, title: String) {
        viewModelScope.launch {
            repository.addPage(sectionId, title, "")
        }
    }

    fun updatePage(page: Page) {
        viewModelScope.launch {
            repository.updatePage(page)
        }
    }

    fun deletePage(page: Page) {
        viewModelScope.launch {
            repository.deletePage(page)
        }
    }
}
