package com.example.deathnote.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirSurface
import com.example.deathnote.ui.theme.NoirTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit = {},
    onNotebookClick: (String) -> Unit = {},
    onPageClick: (String) -> Unit = {},
    onJournalClick: (Long) -> Unit = {}
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search", style = MaterialTheme.typography.titleLarge, color = Color.White) },
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
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Search Notebooks, Pages, or Journal...", color = NoirTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NoirTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = NoirSurface,
                    focusedContainerColor = NoirSurface,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchFilterChip("All", selectedTab == SearchTab.ALL) {
                    viewModel.onTabSelected(SearchTab.ALL)
                }
                SearchFilterChip("Notebooks", selectedTab == SearchTab.NOTEBOOKS) {
                    viewModel.onTabSelected(SearchTab.NOTEBOOKS)
                }
                SearchFilterChip("Pages", selectedTab == SearchTab.PAGES) {
                    viewModel.onTabSelected(SearchTab.PAGES)
                }
                SearchFilterChip("Journal", selectedTab == SearchTab.JOURNAL) {
                    viewModel.onTabSelected(SearchTab.JOURNAL)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if ((selectedTab == SearchTab.ALL || selectedTab == SearchTab.NOTEBOOKS) && results.notebooks.isNotEmpty()) {
                    item { SectionHeader("Notebooks", results.notebooks.size) }
                    items(results.notebooks) { notebook ->
                        SearchResultItem(
                            icon = Icons.Default.Folder,
                            title = notebook.name,
                            subtitle = "Notebook",
                            onClick = { onNotebookClick(notebook.id) }
                        )
                    }
                }

                if ((selectedTab == SearchTab.ALL || selectedTab == SearchTab.PAGES) && results.pages.isNotEmpty()) {
                    item { SectionHeader("Pages", results.pages.size) }
                    items(results.pages) { page ->
                        val subtitle = remember(page.content, query) {
                            if (page.content.contains(query, ignoreCase = true)) {
                                val index = page.content.indexOf(query, ignoreCase = true)
                                val start = (index - 20).coerceAtLeast(0)
                                val end = (index + 30).coerceAtMost(page.content.length)
                                val prefix = if (start > 0) "..." else ""
                                val suffix = if (end < page.content.length) "..." else ""
                                prefix + page.content.substring(start, end).replace("\n", " ") + suffix
                            } else {
                                page.content.take(50).replace("\n", " ")
                            }
                        }
                        SearchResultItem(
                            icon = Icons.Default.Description,
                            title = page.title,
                            subtitle = subtitle,
                            onClick = { onPageClick(page.id) }
                        )
                    }
                }

                if ((selectedTab == SearchTab.ALL || selectedTab == SearchTab.JOURNAL) && results.journalEntries.isNotEmpty()) {
                    item { SectionHeader("Journal Entries", results.journalEntries.size) }
                    items(results.journalEntries) { entry ->
                        SearchResultItem(
                            icon = Icons.Default.History,
                            title = "Journal Entry",
                            subtitle = entry.content?.take(50) ?: "No content",
                            onClick = { onJournalClick(entry.date) }
                        )
                    }
                }
                
                if (query.length >= 2 && results.notebooks.isEmpty() && results.pages.isEmpty() && results.journalEntries.isEmpty()) {
                    item {
                        Text(
                            "No results found for \"$query\"",
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = NoirTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchFilterChip(
    label: String, 
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) Color.White else NoirSurface,
        contentColor = if (selected) Color.Black else Color.White,
        shape = CircleShape,
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun SectionHeader(title: String, matches: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(2.dp).height(16.dp).background(MaterialTheme.colorScheme.primary))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.weight(1f))
        Text("$matches MATCHES", style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
    }
}

@Composable
fun SearchResultItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(NoirSurface, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
            }
        }
    }
}
