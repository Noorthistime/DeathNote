package com.example.deathnote.ui.notebook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deathnote.domain.model.Page
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirSurface
import com.example.deathnote.ui.theme.NoirTextPrimary
import com.example.deathnote.ui.theme.NoirTextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageEditorScreen(
    pageId: String,
    onBack: () -> Unit = {},
    onNotebookClick: (String) -> Unit = {},
    onSectionClick: (String) -> Unit = {}
) {
    val viewModel: NotebookViewModel = hiltViewModel()
    var page by remember { mutableStateOf<Page?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    var sectionName by remember { mutableStateOf("") }
    var notebookName by remember { mutableStateOf("") }
    var notebookId by remember { mutableStateOf("") }
    var sectionId by remember { mutableStateOf("") }

    LaunchedEffect(pageId) {
        viewModel.repository.getPageById(pageId)?.let { p ->
            page = p
            title = p.title
            content = p.content
            sectionId = p.sectionId
            
            viewModel.repository.getSectionById(p.sectionId)?.let { s ->
                sectionName = s.name
                notebookId = s.notebookId
                viewModel.repository.getNotebookById(s.notebookId)?.let { n ->
                    notebookName = n.name
                }
            }
        }
    }

    // Auto-save logic
    LaunchedEffect(title, content) {
        page?.let { existingPage ->
            if (existingPage.title != title || existingPage.content != content) {
                // Debounce save to avoid too many DB writes
                delay(1000)
                viewModel.updatePage(existingPage.copy(title = title, content = content))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (notebookName.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(NoirSurface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = notebookName.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = NoirPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onNotebookClick(notebookId) }
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = NoirTextSecondary,
                                modifier = Modifier.size(16.dp).padding(horizontal = 4.dp)
                            )
                            Text(
                                text = sectionName.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.clickable { onSectionClick(sectionId) }
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 24.dp)
        ) {
            // Page Title Header
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                cursorBrush = SolidColor(NoirPrimary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            "Untitled Page", 
                            color = NoirTextSecondary.copy(alpha = 0.5f), 
                            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Body Content
            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = TextStyle(
                    color = NoirTextPrimary,
                    fontSize = 18.sp,
                    lineHeight = 28.sp
                ),
                cursorBrush = SolidColor(NoirPrimary),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text("Start writing here...", color = NoirTextSecondary)
                    }
                    innerTextField()
                }
            )
        }
    }
}
