package com.example.deathnote.ui.notebook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.ui.security.SecuritySession
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirSurface
import com.example.deathnote.ui.theme.NoirTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookListScreen(
    onNotebookClick: (Notebook) -> Unit = {},
    onCreateNotebook: () -> Unit = {}
) {
    val viewModel: NotebookViewModel = hiltViewModel()
    val notebooks by viewModel.notebooks.collectAsStateWithLifecycle()
    var notebookToUnlock by remember { mutableStateOf<Notebook?>(null) }
    
    var notebookToEdit by remember { mutableStateOf<Notebook?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameText by remember { mutableStateOf("") }

    if (notebookToUnlock != null) {
        com.example.deathnote.ui.security.SecurityScreen(
            onAuthenticated = { passwordInput ->
                if (passwordInput == null || notebookToUnlock?.passwordHash?.trim() == passwordInput.trim()) {
                    val notebook = notebookToUnlock!!
                    SecuritySession.unlockedIds.add(notebook.id)
                    notebookToUnlock = null
                    onNotebookClick(notebook)
                }
            },
            onBack = { notebookToUnlock = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Folder, contentDescription = null, tint = NoirTextSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notebooks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Text("${notebooks.size} MATCHES", color = NoirTextSecondary, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(notebooks) { notebook ->
                    NotebookCard(
                        notebook = notebook,
                        onClick = { 
                            if (notebook.isLocked && !SecuritySession.unlockedIds.contains(notebook.id)) {
                                notebookToUnlock = notebook
                            } else {
                                onNotebookClick(notebook)
                            }
                        },
                        onRename = {
                            notebookToEdit = notebook
                            renameText = notebook.name
                            showRenameDialog = true
                        },
                        onDelete = {
                            viewModel.deleteNotebook(notebook)
                        }
                    )
                }
            }
        }
    }

    if (showRenameDialog && notebookToEdit != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Notebook", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("New Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (renameText.isNotBlank()) {
                        viewModel.updateNotebook(notebookToEdit!!.copy(name = renameText))
                        showRenameDialog = false
                    }
                }) { Text("SAVE", color = NoirPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("CANCEL") }
            },
            containerColor = NoirSurface
        )
    }
}

@Composable
fun NotebookCard(notebook: Notebook, onClick: (Notebook) -> Unit, onRename: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = { onClick(notebook) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NoirSurface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (notebook.isLocked) Icons.Default.Lock else Icons.Default.Folder,
                contentDescription = null,
                tint = if (notebook.isLocked) NoirPrimary else NoirTextSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notebook.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                // Text("Placeholder for dynamic count", style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = NoirTextSecondary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(NoirSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename", color = Color.White) },
                        onClick = {
                            showMenu = false
                            onRename()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = NoirPrimary) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
