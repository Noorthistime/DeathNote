package com.example.deathnote.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Dashboard : Route

    @Serializable
    data object Notebooks : Route

    @Serializable
    data class NotebookDetail(val notebookId: String) : Route

    @Serializable
    data class PageDetail(val pageId: String) : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data class JournalDetail(val timestamp: Long) : Route

    @Serializable
    data object CreateNotebook : Route
}
