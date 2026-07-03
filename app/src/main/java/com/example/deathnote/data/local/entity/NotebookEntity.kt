package com.example.deathnote.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notebooks")
data class NotebookEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String?,
    val name: String,
    val isLocked: Boolean = false,
    val passwordHash: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
