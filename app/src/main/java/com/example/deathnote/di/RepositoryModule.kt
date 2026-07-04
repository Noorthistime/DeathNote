package com.example.deathnote.di

import com.example.deathnote.data.repository.AuthRepositoryImpl
import com.example.deathnote.data.repository.JournalRepositoryImpl
import com.example.deathnote.data.repository.NotebookRepositoryImpl
import com.example.deathnote.domain.repository.AuthRepository
import com.example.deathnote.domain.repository.JournalRepository
import com.example.deathnote.domain.repository.NotebookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindNotebookRepository(
        impl: NotebookRepositoryImpl
    ): NotebookRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        impl: JournalRepositoryImpl
    ): JournalRepository
}
