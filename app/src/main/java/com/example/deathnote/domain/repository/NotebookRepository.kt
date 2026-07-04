package com.example.deathnote.domain.repository

import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.domain.model.Page
import com.example.deathnote.domain.model.Section
import kotlinx.coroutines.flow.Flow

interface NotebookRepository {
    fun getAllNotebooks(): Flow<List<Notebook>>
    suspend fun addNotebook(name: String, isLocked: Boolean, passwordHash: String?)
    suspend fun updateNotebook(notebook: Notebook)
    suspend fun deleteNotebook(notebook: Notebook)
    suspend fun getNotebookById(notebookId: String): Notebook?
    fun getSectionsByNotebook(notebookId: String): Flow<List<Section>>
    suspend fun addSection(notebookId: String, name: String)
    suspend fun updateSection(section: Section)
    suspend fun deleteSection(section: Section)
    suspend fun getSectionById(sectionId: String): Section?
    fun getPagesBySection(sectionId: String): Flow<List<Page>>
    suspend fun addPage(sectionId: String, title: String, content: String)
    suspend fun updatePage(page: Page)
    suspend fun deletePage(page: Page)
    suspend fun getPageById(pageId: String): Page?
    suspend fun searchNotebooks(query: String): List<Notebook>
    suspend fun searchPages(query: String): List<Page>
    
    // Sync methods
    suspend fun syncNotebook(notebook: Notebook)
    suspend fun syncSection(section: Section)
    suspend fun syncPage(page: Page)
    suspend fun getAllSections(): List<Section>
    suspend fun getAllPages(): List<Page>
}
