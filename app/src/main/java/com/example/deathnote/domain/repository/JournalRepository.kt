package com.example.deathnote.domain.repository

import com.example.deathnote.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntry>>
    suspend fun getEntryByDate(date: Long): JournalEntry?
    suspend fun saveEntry(entry: JournalEntry)
    suspend fun searchEntries(query: String): List<JournalEntry>
    suspend fun syncEntry(entry: JournalEntry)
}
