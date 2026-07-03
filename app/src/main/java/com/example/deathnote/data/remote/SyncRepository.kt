package com.example.deathnote.data.remote

import com.example.deathnote.domain.repository.JournalRepository
import com.example.deathnote.domain.repository.NotebookRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val notebookRepository: NotebookRepository,
    private val journalRepository: JournalRepository
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun syncData() {
        val userId = auth.currentUser?.uid ?: return
        
        // Sync Notebooks
        val notebooks = notebookRepository.getAllNotebooks().first()
        notebooks.forEach { notebook ->
            firestore.collection("users").document(userId)
                .collection("notebooks").document(notebook.id)
                .set(notebook)
        }

        // Sync Journal Entries
        val entries = journalRepository.getAllEntries().first()
        entries.forEach { entry ->
            firestore.collection("users").document(userId)
                .collection("journal_entries").document(entry.id)
                .set(entry)
        }
    }
}
