package com.example.deathnote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.deathnote.data.local.dao.JournalDao
import com.example.deathnote.data.local.dao.NotebookDao
import com.example.deathnote.data.local.entity.JournalEntryEntity
import com.example.deathnote.data.local.entity.NotebookEntity
import com.example.deathnote.data.local.entity.PageEntity
import com.example.deathnote.data.local.entity.SectionEntity

@Database(
    entities = [
        NotebookEntity::class,
        SectionEntity::class,
        PageEntity::class,
        JournalEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notebookDao(): NotebookDao
    abstract fun journalDao(): JournalDao
}
