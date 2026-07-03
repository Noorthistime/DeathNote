package com.example.deathnote.data.repository

import com.example.deathnote.data.local.dao.NotebookDao
import com.example.deathnote.data.local.entity.NotebookEntity
import com.example.deathnote.data.local.entity.PageEntity
import com.example.deathnote.data.local.entity.SectionEntity
import com.example.deathnote.data.mapper.toDomain
import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.domain.model.Page
import com.example.deathnote.domain.model.Section
import com.example.deathnote.domain.repository.NotebookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotebookRepositoryImpl @Inject constructor(
    private val dao: NotebookDao
) : NotebookRepository {

    override fun getAllNotebooks(): Flow<List<Notebook>> {
        return dao.getAllNotebooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addNotebook(name: String, isLocked: Boolean, passwordHash: String?) {
        dao.insertNotebook(
            NotebookEntity(
                userId = null, // Guest mode for now
                name = name,
                isLocked = isLocked,
                passwordHash = passwordHash
            )
        )
    }

    override suspend fun updateNotebook(notebook: Notebook) {
        dao.updateNotebook(NotebookEntity(
            id = notebook.id,
            userId = notebook.userId,
            name = notebook.name,
            isLocked = notebook.isLocked,
            passwordHash = notebook.passwordHash,
            createdAt = notebook.createdAt
        ))
    }

    override suspend fun deleteNotebook(notebook: Notebook) {
        dao.deleteNotebook(NotebookEntity(
            id = notebook.id,
            userId = notebook.userId,
            name = notebook.name,
            isLocked = notebook.isLocked,
            passwordHash = notebook.passwordHash,
            createdAt = notebook.createdAt
        ))
    }

    override suspend fun getNotebookById(notebookId: String): Notebook? {
        return dao.getNotebookById(notebookId)?.toDomain()
    }

    override fun getSectionsByNotebook(notebookId: String): Flow<List<Section>> {
        return dao.getSectionsByNotebook(notebookId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addSection(notebookId: String, name: String) {
        dao.insertSection(SectionEntity(notebookId = notebookId, name = name))
    }

    override suspend fun updateSection(section: Section) {
        dao.updateSection(SectionEntity(id = section.id, notebookId = section.notebookId, name = section.name))
    }

    override suspend fun deleteSection(section: Section) {
        dao.deleteSection(SectionEntity(id = section.id, notebookId = section.notebookId, name = section.name))
    }

    override suspend fun getSectionById(sectionId: String): Section? {
        return dao.getSectionById(sectionId)?.toDomain()
    }

    override fun getPagesBySection(sectionId: String): Flow<List<Page>> {
        return dao.getPagesBySection(sectionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addPage(sectionId: String, title: String, content: String) {
        dao.insertPage(PageEntity(sectionId = sectionId, title = title, content = content))
    }

    override suspend fun updatePage(page: Page) {
        dao.updatePage(PageEntity(
            id = page.id,
            sectionId = page.sectionId,
            title = page.title,
            content = page.content,
            updatedAt = page.updatedAt
        ))
    }

    override suspend fun deletePage(page: Page) {
        dao.deletePage(PageEntity(
            id = page.id,
            sectionId = page.sectionId,
            title = page.title,
            content = page.content,
            updatedAt = page.updatedAt
        ))
    }

    override suspend fun getPageById(pageId: String): Page? {
        return dao.getPageById(pageId)?.toDomain()
    }

    override suspend fun searchNotebooks(query: String): List<Notebook> {
        return dao.searchNotebooks(query).map { it.toDomain() }
    }

    override suspend fun searchPages(query: String): List<Page> {
        return dao.searchPages(query).map { it.toDomain() }
    }
}
