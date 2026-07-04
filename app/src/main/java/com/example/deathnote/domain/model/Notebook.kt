package com.example.deathnote.domain.model

data class Notebook(
    val id: String = "",
    val userId: String? = null,
    val name: String = "",
    val isLocked: Boolean = false,
    val passwordHash: String? = null,
    val createdAt: Long = 0
)

data class Section(
    val id: String = "",
    val notebookId: String = "",
    val name: String = ""
)

data class Page(
    val id: String = "",
    val sectionId: String = "",
    val title: String = "",
    val content: String = "",
    val updatedAt: Long = 0
)
