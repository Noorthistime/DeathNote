package com.example.deathnote.data.mapper

import com.example.deathnote.data.local.entity.NotebookEntity
import com.example.deathnote.data.local.entity.PageEntity
import com.example.deathnote.data.local.entity.SectionEntity
import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.domain.model.Page
import com.example.deathnote.domain.model.Section

fun NotebookEntity.toDomain(): Notebook {
    return Notebook(
        id = id,
        userId = userId,
        name = name,
        isLocked = isLocked,
        passwordHash = passwordHash,
        createdAt = createdAt
    )
}

fun SectionEntity.toDomain(): Section {
    return Section(
        id = id,
        notebookId = notebookId,
        name = name
    )
}

fun PageEntity.toDomain(): Page {
    return Page(
        id = id,
        sectionId = sectionId,
        title = title,
        content = content,
        updatedAt = updatedAt
    )
}
