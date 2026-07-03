package com.example.deathnote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.deathnote.ui.dashboard.DashboardScreen
import com.example.deathnote.ui.journal.JournalEntryScreen
import com.example.deathnote.ui.notebook.CreateNotebookScreen
import com.example.deathnote.ui.notebook.NotebookDetailScreen
import com.example.deathnote.ui.notebook.NotebookListScreen
import com.example.deathnote.ui.notebook.PageEditorScreen
import com.example.deathnote.ui.onboarding.OnboardingScreen
import com.example.deathnote.ui.search.SearchScreen
import com.example.deathnote.ui.security.SecurityScreen

@Composable
fun DeathNoteNavGraph() {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Route.Onboarding)
    
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry(Route.Onboarding) {
                OnboardingScreen(
                    onContinueAsGuest = { 
                        backStack.clear()
                        backStack.add(Route.Dashboard)
                    },
                    onSignInWithGoogle = {
                        backStack.clear()
                        backStack.add(Route.Dashboard)
                    }
                )
            }
            entry(Route.Dashboard) {
                DashboardScreen(
                    onNavigateToJournal = { timestamp ->
                        backStack.add(Route.JournalDetail(timestamp))
                    },
                    onNavigateToSearch = {
                        backStack.add(Route.Search)
                    },
                    onCreateNotebook = {
                        backStack.add(Route.CreateNotebook)
                    },
                    onNotebookClick = { notebook ->
                        backStack.add(Route.NotebookDetail(notebook.id))
                    }
                )
            }
            entry(Route.Notebooks) {
                NotebookListScreen(
                    onNotebookClick = { notebook ->
                        backStack.add(Route.NotebookDetail(notebook.id))
                    },
                    onCreateNotebook = {
                        backStack.add(Route.CreateNotebook)
                    }
                )
            }
            entry(Route.Search) {
                SearchScreen(
                    onBack = { 
                        backStack.removeLastOrNull()
                    },
                    onNotebookClick = { notebookId ->
                        backStack.add(Route.NotebookDetail(notebookId))
                    },
                    onPageClick = { pageId ->
                        backStack.add(Route.PageDetail(pageId))
                    },
                    onJournalClick = { timestamp ->
                        backStack.add(Route.JournalDetail(timestamp))
                    }
                )
            }
            entry(Route.CreateNotebook) {
                CreateNotebookScreen(
                    onDismiss = { backStack.removeLastOrNull() },
                    onCreated = { backStack.removeLastOrNull() }
                )
            }
            entry<Route.JournalDetail> { route ->
                JournalEntryScreen(
                    dateTimestamp = route.timestamp,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<Route.NotebookDetail> { route ->
                NotebookDetailScreen(
                    notebookId = route.notebookId,
                    onBack = { backStack.removeLastOrNull() },
                    onPageClick = { pageId ->
                        backStack.add(Route.PageDetail(pageId))
                    }
                )
            }
            entry<Route.PageDetail> { route ->
                PageEditorScreen(
                    pageId = route.pageId,
                    onBack = { backStack.removeLastOrNull() },
                    onNotebookClick = { notebookId ->
                        backStack.add(Route.NotebookDetail(notebookId))
                    },
                    onSectionClick = { _ ->
                        // Since we don't have the notebookId here easily without extra lookup,
                        // and we know Page was opened from NotebookDetail usually, 
                        // we'll just go back. If opened from Search, we should 
                        // ideally find the notebookId.
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
