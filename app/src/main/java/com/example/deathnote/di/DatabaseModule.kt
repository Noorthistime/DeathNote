package com.example.deathnote.di

import android.content.Context
import androidx.room.Room
import com.example.deathnote.data.local.AppDatabase
import com.example.deathnote.data.local.dao.JournalDao
import com.example.deathnote.data.local.dao.NotebookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "deathnote_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideNotebookDao(db: AppDatabase): NotebookDao = db.notebookDao()

    @Provides
    fun provideJournalDao(db: AppDatabase): JournalDao = db.journalDao()
}
