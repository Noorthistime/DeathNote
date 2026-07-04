package com.example.deathnote.data.remote

import com.example.deathnote.domain.model.JournalEntry
import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.domain.model.Page
import com.example.deathnote.domain.model.Section
import com.example.deathnote.domain.repository.JournalRepository
import com.example.deathnote.domain.repository.NotebookRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val journalRepository: JournalRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun syncData() {
        val userId = auth.currentUser?.uid ?: return
        
        pushLocalToRemote(userId)
        pullRemoteToLocal(userId)
    }

    private suspend fun pushLocalToRemote(userId: String) {
        val userRef = firestore.collection("users").document(userId)

        // Sync Notebooks
        val notebooks = notebookRepository.getAllNotebooks().first()
        notebooks.forEach { notebook ->
            userRef.collection("notebooks").document(notebook.id).set(notebook)
        }

        // Sync Sections
        val sections = notebookRepository.getAllSections()
        sections.forEach { section ->
            userRef.collection("sections").document(section.id).set(section)
        }

        // Sync Pages
        val pages = notebookRepository.getAllPages()
        pages.forEach { page ->
            userRef.collection("pages").document(page.id).set(page)
        }

        // Sync Journal Entries
        val entries = journalRepository.getAllEntries().first()
        entries.forEach { entry ->
            userRef.collection("journal_entries").document(entry.id).set(entry)
        }
    }

    private suspend fun pullRemoteToLocal(userId: String) {
        val userRef = firestore.collection("users").document(userId)

        // Pull Notebooks
        val notebookSnapshots = userRef.collection("notebooks").get().await()
        notebookSnapshots.documents.forEach { doc ->
            val notebook = doc.toObject(Notebook::class.java)
            if (notebook != null) notebookRepository.syncNotebook(notebook)
        }

        // Pull Sections
        val sectionSnapshots = userRef.collection("sections").get().await()
        sectionSnapshots.documents.forEach { doc ->
            val section = doc.toObject(Section::class.java)
            if (section != null) notebookRepository.syncSection(section)
        }

        // Pull Pages
        val pageSnapshots = userRef.collection("pages").get().await()
        pageSnapshots.documents.forEach { doc ->
            val page = doc.toObject(Page::class.java)
            if (page != null) notebookRepository.syncPage(page)
        }

        // Pull Journal Entries
        val journalSnapshots = userRef.collection("journal_entries").get().await()
        journalSnapshots.documents.forEach { doc ->
            val entry = doc.toObject(JournalEntry::class.java)
            if (entry != null) journalRepository.syncEntry(entry)
        }
    }
}
