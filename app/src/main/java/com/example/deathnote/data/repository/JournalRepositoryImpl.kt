package com.example.deathnote.data.repository

import com.example.deathnote.data.local.dao.JournalDao
import com.example.deathnote.data.mapper.toDomain
import com.example.deathnote.data.mapper.toEntity
import com.example.deathnote.domain.model.JournalEntry
import com.example.deathnote.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val dao: JournalDao
) : JournalRepository {

    override fun getAllEntries(): Flow<List<JournalEntry>> {
        return dao.getAllEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEntryByDate(date: Long): JournalEntry? {
        return dao.getEntryByDate(date)?.toDomain()
    }

    override suspend fun saveEntry(entry: JournalEntry) {
        dao.insertEntry(entry.toEntity())
    }

    override suspend fun searchEntries(query: String): List<JournalEntry> {
        return dao.searchEntries(query).map { it.toDomain() }
    }

    override suspend fun syncEntry(entry: JournalEntry) {
        dao.insertEntry(entry.toEntity())
    }
}
