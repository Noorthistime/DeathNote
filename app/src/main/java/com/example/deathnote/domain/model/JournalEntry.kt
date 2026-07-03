package com.example.deathnote.domain.model

data class JournalEntry(
    val id: String,
    val date: Long,
    val wokeUpAt: String?,
    val content: String?,
    val felt: String?,
    val screenTime: String?,
    val sleptAt: String?,
    val sleptFor: String?,
    val moodPercentage: Int
)
