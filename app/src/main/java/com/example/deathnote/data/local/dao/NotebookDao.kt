package com.example.deathnote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deathnote.data.local.entity.NotebookEntity
import com.example.deathnote.data.local.entity.PageEntity
import com.example.deathnote.data.local.entity.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    // Notebooks
    @Query("SELECT * FROM notebooks ORDER BY createdAt DESC")
    fun getAllNotebooks(): Flow<List<NotebookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: NotebookEntity)

    @Update
    suspend fun updateNotebook(notebook: NotebookEntity)

    @Query("SELECT * FROM notebooks WHERE id = :notebookId")
    suspend fun getNotebookById(notebookId: String): NotebookEntity?

    @Delete
    suspend fun deleteNotebook(notebook: NotebookEntity)

    // Sections
    @Query("SELECT * FROM sections WHERE notebookId = :notebookId")
    fun getSectionsByNotebook(notebookId: String): Flow<List<SectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: SectionEntity)

    @Update
    suspend fun updateSection(section: SectionEntity)

    @Query("SELECT * FROM sections WHERE id = :sectionId")
    suspend fun getSectionById(sectionId: String): SectionEntity?

    @Delete
    suspend fun deleteSection(section: SectionEntity)

    // Pages
    @Query("SELECT * FROM pages WHERE sectionId = :sectionId ORDER BY updatedAt DESC")
    fun getPagesBySection(sectionId: String): Flow<List<PageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity)

    @Update
    suspend fun updatePage(page: PageEntity)

    @Delete
    suspend fun deletePage(page: PageEntity)

    @Query("SELECT * FROM pages WHERE id = :pageId")
    suspend fun getPageById(pageId: String): PageEntity?

    // Search
    @Query("SELECT * FROM notebooks WHERE name LIKE '%' || :query || '%'")
    suspend fun searchNotebooks(query: String): List<NotebookEntity>

    @Query("SELECT * FROM sections WHERE name LIKE '%' || :query || '%'")
    suspend fun searchSections(query: String): List<SectionEntity>

    @Query("SELECT * FROM pages WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    suspend fun searchPages(query: String): List<PageEntity>
}
