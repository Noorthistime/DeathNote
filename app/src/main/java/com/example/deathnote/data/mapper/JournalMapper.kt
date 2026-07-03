package com.example.deathnote.data.mapper

import com.example.deathnote.data.local.entity.JournalEntryEntity
import com.example.deathnote.domain.model.JournalEntry

fun JournalEntryEntity.toDomain(): JournalEntry {
    return JournalEntry(
        id = id,
        date = date,
        wokeUpAt = wokeUpAt,
        content = content,
        felt = felt,
        screenTime = screenTime,
        sleptAt = sleptAt,
        sleptFor = sleptFor,
        moodPercentage = moodPercentage
    )
}

fun JournalEntry.toEntity(): JournalEntryEntity {
    return JournalEntryEntity(
        id = id,
        date = date,
        wokeUpAt = wokeUpAt,
        content = content,
        felt = felt,
        screenTime = screenTime,
        sleptAt = sleptAt,
        sleptFor = sleptFor,
        moodPercentage = moodPercentage
    )
}
