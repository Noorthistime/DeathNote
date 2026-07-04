package com.example.deathnote.domain.model

data class JournalEntry(
    val id: String = "",
    val date: Long = 0,
    val wokeUpAt: String? = null,
    val content: String? = null,
    val felt: String? = null,
    val screenTime: String? = null,
    val sleptAt: String? = null,
    val sleptFor: String? = null,
    val moodPercentage: Int = 0
)
