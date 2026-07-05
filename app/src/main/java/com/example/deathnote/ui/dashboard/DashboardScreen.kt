package com.example.deathnote.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deathnote.domain.model.JournalEntry
import com.example.deathnote.domain.model.Notebook
import com.example.deathnote.ui.journal.JournalViewModel
import com.example.deathnote.ui.notebook.NotebookListScreen
import androidx.compose.material.icons.filled.ColorLens
import com.example.deathnote.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToJournal: (Long) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onCreateNotebook: () -> Unit = {},
    onNotebookClick: (Notebook) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val journalViewModel: JournalViewModel = hiltViewModel()
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val entries by journalViewModel.allEntries.collectAsStateWithLifecycle()
    var showColorDialog by remember { mutableStateOf(false) }

    if (showColorDialog) {
        AlertDialog(
            onDismissRequest = { showColorDialog = false },
            containerColor = NoirSurface,
            title = { Text("Select the Accent", color = Color.White, style = MaterialTheme.typography.labelSmall) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val colors = listOf(
                        ThemeOrange to "Orange",
                        ThemeRed to "Red",
                        ThemeBlue to "Blue",
                        ThemeGreen to "Green",
                        ThemeGrey to "Grey"
                    )
                    colors.forEach { (color, name) ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                                .border(
                                    if (DeathNoteThemeManager.currentAccentColor == color) 2.dp else 0.dp,
                                    Color.White,
                                    CircleShape
                                )
                                .clickable {
                                    DeathNoteThemeManager.currentAccentColor = color
                                    showColorDialog = false
                                }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
                TopAppBar(
                    title = {
                        Text(
                            "DEATHNOTE",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif
                            )
                        )
                    },
                    actions = {
                        IconButton(onClick = { showColorDialog = true }) {
                            Icon(Icons.Default.ColorLens, contentDescription = "Theme", tint = Color.White)
                        }
                        IconButton(onClick = { dashboardViewModel.syncNow() }) {
                            Icon(Icons.Default.Sync, contentDescription = "Sync", tint = Color.White)
                        }
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = {
                            dashboardViewModel.signOut(onSignOut)
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                )
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Black,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        text = { Text("JOURNALING", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        text = { Text("NOTEBOOKS", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    if (pagerState.currentPage == 0) {
                        onNavigateToJournal(System.currentTimeMillis())
                    } else {
                        onCreateNotebook()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
        ) { page ->
            if (page == 0) {
                JournalTab(entries = entries, onDateSelected = onNavigateToJournal)
            } else {
                NotebookListScreen(
                    onNotebookClick = onNotebookClick,
                    onCreateNotebook = onCreateNotebook
                )
            }
        }
    }
}

@Composable
fun JournalTab(entries: List<JournalEntry>, onDateSelected: (Long) -> Unit) {
    var currentCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    
    // Optimize entry lookup by creating a set of normalized dates
    val entryDatesSet = remember(entries) {
        entries.filter { entry ->
            entry.content?.isNotBlank() == true || 
            entry.wokeUpAt?.any { it.isDigit() } == true ||
            entry.felt?.isNotBlank() == true ||
            entry.screenTime?.isNotBlank() == true ||
            entry.sleptAt?.any { it.isDigit() } == true
        }.map { entry ->
            val cal = Calendar.getInstance().apply { timeInMillis = entry.date }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.toSet()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FunctionalCalendar(
            entriesSet = entryDatesSet, 
            onDateSelected = onDateSelected,
            calendar = currentCalendar,
            onCalendarChange = { currentCalendar = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Requirement 7: Monthly overview below calendar
        Text(
            "MONTHLY OVERVIEW", 
            style = MaterialTheme.typography.labelSmall, 
            color = NoirTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val month = currentCalendar.get(Calendar.MONTH)
        val year = currentCalendar.get(Calendar.YEAR)
        
        val today = Calendar.getInstance()
        val isCurrentMonth = today.get(Calendar.MONTH) == month && today.get(Calendar.YEAR) == year
        
        val startDay = if (isCurrentMonth) today.get(Calendar.DAY_OF_MONTH) else daysInMonth
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = startDay,
                key = { it } // Use stable key
            ) { index ->
                val day = startDay - index
                val cardCal = remember(day, month, year) {
                    Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                }
                
                val hasEntry = entryDatesSet.contains(cardCal.timeInMillis)
                
                val dayOfWeek = remember(cardCal) {
                    SimpleDateFormat("EEEE", Locale.getDefault()).format(cardCal.time)
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDateSelected(cardCal.timeInMillis) },
                    colors = CardDefaults.cardColors(containerColor = NoirSurface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%02d", day),
                            style = MaterialTheme.typography.titleLarge,
                            color = if (hasEntry) MaterialTheme.colorScheme.primary else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(dayOfWeek, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                            if (hasEntry) {
                                Text("Entry recorded", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            } else {
                                Text("No entry", style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FunctionalCalendar(
    entriesSet: Set<Long>, 
    onDateSelected: (Long) -> Unit,
    calendar: Calendar,
    onCalendarChange: (Calendar) -> Unit
) {
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = remember(currentMonth, currentYear) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, currentYear)
        cal.set(Calendar.MONTH, currentMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 2 // Adjusting for Monday start
        if (dayOfWeek < 0) dayOfWeek += 7
        dayOfWeek
    }

    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time).uppercase()
    
    var showMonthMenu by remember { mutableStateOf(false) }
    var showYearMenu by remember { mutableStateOf(false) }
    
    val months = listOf(
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    )
    val years = (2020..2090).toList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoirSurface, MaterialTheme.shapes.medium)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val newCal = calendar.clone() as Calendar
                newCal.add(Calendar.MONTH, -1)
                onCalendarChange(newCal)
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color.White)
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Text(
                        monthName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.clickable { showMonthMenu = true }
                    )
                    
                    if (showMonthMenu) {
                        AlertDialog(
                            onDismissRequest = { showMonthMenu = false },
                            containerColor = NoirSurface,
                            title = { Text("Select Month", color = Color.White) },
                            text = {
                                Box(modifier = Modifier.height(300.dp)) {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        contentPadding = PaddingValues(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        itemsIndexed(months) { index, month ->
                                            Surface(
                                                onClick = {
                                                    val newCal = calendar.clone() as Calendar
                                                    newCal.set(Calendar.MONTH, index)
                                                    onCalendarChange(newCal)
                                                    showMonthMenu = false
                                                },
                                                color = if (index == currentMonth) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, if (index == currentMonth) MaterialTheme.colorScheme.primary else NoirSurface)
                                            ) {
                                                Text(
                                                    month,
                                                    modifier = Modifier.padding(12.dp),
                                                    textAlign = TextAlign.Center,
                                                    color = if (index == currentMonth) Color.White else NoirTextSecondary,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showMonthMenu = false }) {
                                    Text("CLOSE", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        )
                    }
                }
                Box {
                    Text(
                        currentYear.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = NoirTextSecondary,
                        modifier = Modifier.clickable { showYearMenu = true }
                    )
                    
                    if (showYearMenu) {
                        AlertDialog(
                            onDismissRequest = { showYearMenu = false },
                            containerColor = NoirSurface,
                            title = { Text("Select Year", color = Color.White) },
                            text = {
                                Box(modifier = Modifier.height(300.dp)) {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        contentPadding = PaddingValues(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(years) { year ->
                                            Surface(
                                                onClick = {
                                                    val newCal = calendar.clone() as Calendar
                                                    newCal.set(Calendar.YEAR, year)
                                                    onCalendarChange(newCal)
                                                    showYearMenu = false
                                                },
                                                color = if (year == currentYear) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, if (year == currentYear) MaterialTheme.colorScheme.primary else NoirSurface)
                                            ) {
                                                Text(
                                                    year.toString(),
                                                    modifier = Modifier.padding(12.dp),
                                                    textAlign = TextAlign.Center,
                                                    color = if (year == currentYear) Color.White else NoirTextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showYearMenu = false }) {
                                    Text("CLOSE", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        )
                    }
                }
            }

            IconButton(onClick = {
                val newCal = calendar.clone() as Calendar
                newCal.add(Calendar.MONTH, 1)
                onCalendarChange(newCal)
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            dayNames.forEach { day ->
                Text(
                    day,
                    style = MaterialTheme.typography.labelSmall,
                    color = NoirTextSecondary,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        var dayCounter = 1
        Column {
            for (row in 0..5) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (col in 0..6) {
                        val currentDayIdx = row * 7 + col
                        if (currentDayIdx < firstDayOfMonth || dayCounter > daysInMonth) {
                            Spacer(modifier = Modifier.size(40.dp))
                        } else {
                            val day = dayCounter
                            val isToday = day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && 
                                          currentMonth == Calendar.getInstance().get(Calendar.MONTH) &&
                                          currentYear == Calendar.getInstance().get(Calendar.YEAR)
                            
                            // Requirement 3: Red color if written, white if blank
                            val dayTimestamp = remember(day, currentMonth, currentYear) {
                                Calendar.getInstance().apply {
                                    set(Calendar.YEAR, currentYear)
                                    set(Calendar.MONTH, currentMonth)
                                    set(Calendar.DAY_OF_MONTH, day)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }.timeInMillis
                            }
                            
                            val hasEntry = entriesSet.contains(dayTimestamp)

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                                        CircleShape
                                    )
                                    .border(
                                        if (isToday) 1.dp else 0.dp,
                                        if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable {
                                        onDateSelected(dayTimestamp)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    day.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (hasEntry) MaterialTheme.colorScheme.primary else Color.White,
                                    fontWeight = if (isToday || hasEntry) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            dayCounter++
                        }
                    }
                }
                if (dayCounter > daysInMonth) break
            }
        }
    }
}
