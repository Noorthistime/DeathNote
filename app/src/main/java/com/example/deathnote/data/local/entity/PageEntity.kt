package com.example.deathnote.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = SectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sectionId"])]
)
data class PageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sectionId: String,
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis()
)
