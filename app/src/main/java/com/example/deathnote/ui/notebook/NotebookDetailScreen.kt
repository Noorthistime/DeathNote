package com.example.deathnote.ui.notebook

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.deathnote.domain.model.Section
import com.example.deathnote.domain.model.Page
import com.example.deathnote.ui.security.SecuritySession
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirSurface
import com.example.deathnote.ui.theme.NoirTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookDetailScreen(
    notebookId: String,
    onBack: () -> Unit = {},
    onPageClick: (String) -> Unit = {}
) {
    val viewModel: NotebookViewModel = hiltViewModel()
    val sections by viewModel.sections.collectAsStateWithLifecycle()
    val notebook by viewModel.selectedNotebook.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
    
    var showAddSectionDialog by remember { mutableStateOf(false) }
    var newSectionName by remember { mutableStateOf("") }
    
    var sectionToEdit by remember { mutableStateOf<Section?>(null) }
    var showEditSectionDialog by remember { mutableStateOf(false) }
    var editSectionName by remember { mutableStateOf("") }
    
    var sessionUnlocked by remember(notebookId) { 
        mutableStateOf(SecuritySession.unlockedIds.contains(notebookId)) 
    }

    LaunchedEffect(notebookId) {
        viewModel.selectNotebook(notebookId)
    }

    val currentNotebook = notebook
    val isReady = currentNotebook != null && currentNotebook.id == notebookId

    if (!isReady) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
    } else if (currentNotebook!!.isLocked && !sessionUnlocked) {
        com.example.deathnote.ui.security.SecurityScreen(
            onAuthenticated = { passwordInput ->
                if (passwordInput == null || currentNotebook.passwordHash?.trim() == passwordInput.trim()) {
                    SecuritySession.unlockedIds.add(notebookId)
                    sessionUnlocked = true
                }
            },
            onBack = onBack
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = NoirSurface,
                    drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "SECTION_INDICES",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NoirTextSecondary,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn {
                        items(sections) { section ->
                            NavigationDrawerItem(
                                label = { Text(section.name.uppercase()) },
                                selected = false,
                                onClick = { 
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    unselectedTextColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { showAddSectionDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("NEW SECTION")
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Open Sections")
                            }
                        },
                        actions = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { 
                            showAddSectionDialog = true
                        },
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Default.LibraryAdd, contentDescription = "Add Section")
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    Text(notebook?.name ?: "", style = MaterialTheme.typography.displayLarge, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = sections,
                            key = { it.id }
                        ) { section ->
                            SectionWithPages(
                                section = section,
                                viewModel = viewModel,
                                onRenameSection = {
                                    sectionToEdit = section
                                    editSectionName = section.name
                                    showEditSectionDialog = true
                                },
                                onDeleteSection = {
                                    viewModel.deleteSection(section)
                                },
                                onPageClick = { page ->
                                    onPageClick(page.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAddSectionDialog) {
            AlertDialog(
                onDismissRequest = { showAddSectionDialog = false },
                title = { Text("Initialize New Section", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = newSectionName,
                        onValueChange = { newSectionName = it },
                        label = { Text("Section Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newSectionName.isNotBlank()) {
                            viewModel.addSection(newSectionName)
                            newSectionName = ""
                            showAddSectionDialog = false
                        }
                    }) { Text("CREATE", color = NoirPrimary) }
                },
                dismissButton = {
                    TextButton(onClick = { showAddSectionDialog = false }) { Text("CANCEL") }
                },
                containerColor = NoirSurface
            )
        }

        if (showEditSectionDialog && sectionToEdit != null) {
            AlertDialog(
                onDismissRequest = { showEditSectionDialog = false },
                title = { Text("Rename Section", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = editSectionName,
                        onValueChange = { editSectionName = it },
                        label = { Text("New Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (editSectionName.isNotBlank()) {
                            viewModel.updateSection(sectionToEdit!!.copy(name = editSectionName))
                            showEditSectionDialog = false
                        }
                    }) { Text("SAVE", color = NoirPrimary) }
                },
                dismissButton = {
                    TextButton(onClick = { showEditSectionDialog = false }) { Text("CANCEL") }
                },
                containerColor = NoirSurface
            )
        }
    }
}

@Composable
fun SectionWithPages(
    section: Section,
    viewModel: NotebookViewModel,
    onRenameSection: () -> Unit,
    onDeleteSection: () -> Unit,
    onPageClick: (Page) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val pagesFlow = remember(section.id) { viewModel.getPagesForSection(section.id) }
    val pages by pagesFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    
    var showMenu by remember { mutableStateOf(false) }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(containerColor = NoirSurface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(section.name, style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${pages.size} PAGES", style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
                }
                IconButton(onClick = { 
                    viewModel.addPage(section.id, "Page - ${pages.size + 1}")
                    expanded = true
                }) {
                    Icon(Icons.Default.NoteAdd, contentDescription = "Quick Add Page", tint = NoirPrimary)
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
                            text = { Text("Add Page", color = Color.White) },
                            onClick = {
                                showMenu = false
                                viewModel.addPage(section.id, "Page - ${pages.size + 1}")
                                expanded = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rename Section", color = Color.White) },
                            onClick = {
                                showMenu = false
                                onRenameSection()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Section", color = NoirPrimary) },
                            onClick = {
                                showMenu = false
                                onDeleteSection()
                            }
                        )
                    }
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = NoirPrimary
                )
            }
        }
        
        if (expanded) {
            Column(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .drawBehind {
                        drawLine(
                            color = NoirTextSecondary.copy(alpha = 0.3f),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
            ) {
                pages.forEach { page ->
                    PageItem(
                        page = page,
                        onClick = { onPageClick(page) },
                        onDelete = {
                            viewModel.deletePage(page)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PageItem(
    page: Page,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Horizontal connector line
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(1.dp)
                .background(NoirTextSecondary.copy(alpha = 0.3f))
        )
        
        Surface(
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            color = NoirSurface.copy(alpha = 0.4f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, NoirTextSecondary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Description, 
                    contentDescription = null, 
                    tint = NoirTextSecondary, 
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    page.title, 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = Color.White, 
                    modifier = Modifier.weight(1f)
                )
                
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = NoirTextSecondary, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(NoirSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Page", color = NoirPrimary) },
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
}
