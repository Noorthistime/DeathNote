package com.example.deathnote.ui.notebook

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirSurface
import com.example.deathnote.ui.theme.NoirTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotebookScreen(
    onDismiss: () -> Unit = {},
    onCreated: () -> Unit = {}
) {
    val viewModel: NotebookViewModel = viewModel()
    var name by remember { mutableStateOf("") }
    var isLocked by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var isSyncActive by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ROOT > NEW_INSTANCE", style = MaterialTheme.typography.labelSmall) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Text(
                "INITIALIZE NEW NOTEBOOK",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "create a new notebook.",
                style = MaterialTheme.typography.bodyLarge,
                color = NoirTextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text("NOTEBOOK NAME", style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Untitled_01", color = NoirTextSecondary.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = NoirTextSecondary.copy(alpha = 0.3f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            ToggleOption(
                icon = Icons.Default.Lock,
                title = "ENCRYPTED",
                subtitle = "Zero-knowledge local storage.",
                isActive = isLocked,
                onToggle = { isLocked = it }
            )

            if (isLocked) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("ENCRYPTION PASSWORD", style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Required for access", color = NoirTextSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    visualTransformation = VisualTransformation.None, // Placeholder if I can't find the exact import
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedBorderColor = NoirTextSecondary.copy(alpha = 0.3f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ToggleOption(
                icon = Icons.Default.Sync,
                title = "SYNC_ACTIVE",
                subtitle = "Cloud backup across nodes.",
                isActive = isSyncActive,
                onToggle = { isSyncActive = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.createNotebook(
                        name.ifEmpty { "Untitled_01" },
                        isLocked,
                        if (isLocked) password else null
                    )
                    onCreated()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("CREATE  →", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ToggleOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        onClick = { onToggle(!isActive) },
        color = NoirSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black, MaterialTheme.shapes.small)
                    .border(1.dp, NoirTextSecondary.copy(alpha = 0.2f), MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (isActive) MaterialTheme.colorScheme.primary else Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
            }
        }
    }
}
