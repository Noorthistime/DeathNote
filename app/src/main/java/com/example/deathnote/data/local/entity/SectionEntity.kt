package com.example.deathnote.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = NotebookEntity::class,
            parentColumns = ["id"],
            childColumns = ["notebookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["notebookId"])]
)
data class SectionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val notebookId: String,
    val name: String
)
