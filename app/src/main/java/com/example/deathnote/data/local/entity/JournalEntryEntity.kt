package com.example.deathnote.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: Long, // Timestamp for the date
    val wokeUpAt: String? = null,
    val content: String? = null, // "The Day" field
    val felt: String? = null,
    val screenTime: String? = null,
    val sleptAt: String? = null,
    val sleptFor: String? = null, // Added based on image_5.png
    val moodPercentage: Int = 0 // Based on "Mood 72%" in image_4.png
)
