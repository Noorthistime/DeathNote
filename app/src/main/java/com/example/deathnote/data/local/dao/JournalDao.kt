package com.example.deathnote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deathnote.data.local.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    suspend fun getEntryByDate(date: Long): JournalEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity)

    @Update
    suspend fun updateEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: JournalEntryEntity)

    @Query("SELECT * FROM journal_entries WHERE content LIKE '%' || :query || '%' OR felt LIKE '%' || :query || '%'")
    suspend fun searchEntries(query: String): List<JournalEntryEntity>
}
