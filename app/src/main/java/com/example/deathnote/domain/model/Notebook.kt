package com.example.deathnote.domain.model

data class Notebook(
    val id: String,
    val userId: String?,
    val name: String,
    val isLocked: Boolean,
    val passwordHash: String?,
    val createdAt: Long
)

data class Section(
    val id: String,
    val notebookId: String,
    val name: String
)

data class Page(
    val id: String,
    val sectionId: String,
    val title: String,
    val content: String,
    val updatedAt: Long
)
