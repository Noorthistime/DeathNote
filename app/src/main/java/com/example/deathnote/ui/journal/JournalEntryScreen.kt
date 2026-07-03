package com.example.deathnote.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deathnote.ui.theme.NoirPrimary
import com.example.deathnote.ui.theme.NoirTextPrimary
import com.example.deathnote.ui.theme.NoirTextSecondary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryScreen(
    dateTimestamp: Long = System.currentTimeMillis(),
    onBack: () -> Unit = {}
) {
    val viewModel: JournalViewModel = hiltViewModel()
    val entry by viewModel.currentEntry.collectAsState()
    val focusManager = LocalFocusManager.current

    // Requirement 2: Default value for slept at selected as AM
    var wokeUpAmPm by remember { mutableStateOf("AM") }
    var sleptAtAmPm by remember { mutableStateOf("AM") }

    LaunchedEffect(dateTimestamp) {
        viewModel.loadEntryForDate(dateTimestamp)
    }

    // Initialize AM/PM from entry once loaded
    LaunchedEffect(entry?.id) {
        entry?.wokeUpAt?.let {
            if (it.contains("PM")) wokeUpAmPm = "PM"
            else if (it.contains("AM")) wokeUpAmPm = "AM"
        }
        entry?.sleptAt?.let {
            if (it.contains("PM")) sleptAtAmPm = "PM"
            else if (it.contains("AM")) sleptAtAmPm = "AM"
        }
    }

    // Requirement 1: Auto calculate slept for using previous day's slept at
    LaunchedEffect(entry?.wokeUpAt, wokeUpAmPm) {
        val w = entry?.wokeUpAt ?: ""
        viewModel.calculateSleptFor(dateTimestamp, w, wokeUpAmPm)
    }

    val dateStr = remember(dateTimestamp) {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(dateTimestamp))
    }
    val dayStr = remember(dateTimestamp) {
        SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(dateTimestamp))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DEATHNOTE", style = MaterialTheme.typography.titleLarge, color = NoirPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                // Requirement 5: Remove the button at the top-right two arrows
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(dateStr, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text(dayStr, style = MaterialTheme.typography.bodyLarge, color = NoirPrimary)

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    TimeEditorField(
                        label = "WOKE UP AT",
                        value = (entry?.wokeUpAt ?: "").replace(" AM", "").replace(" PM", ""),
                        onValueChange = { input ->
                            val clean = input.replace(" AM", "").replace(" PM", "")
                            viewModel.updateEntry(wokeUpAt = "$clean $wokeUpAmPm") 
                        },
                        imeAction = ImeAction.Next,
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                    AmPmToggle(
                        selected = wokeUpAmPm, 
                        onSelected = { 
                            wokeUpAmPm = it
                            val currentVal = entry?.wokeUpAt ?: ""
                            val clean = currentVal.replace(" AM", "").replace(" PM", "")
                            if (clean.isNotEmpty()) {
                                viewModel.updateEntry(wokeUpAt = "$clean $it")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                EditorField(
                    label = "SLEPT FOR",
                    value = entry?.sleptFor ?: "",
                    onValueChange = { /* Auto-calculated, but allow override if needed */ viewModel.updateEntry(sleptFor = it) },
                    placeholder = "0:00",
                    modifier = Modifier.weight(1f),
                    imeAction = ImeAction.Next,
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("THE DAY", style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = entry?.content ?: "",
                onValueChange = { viewModel.updateEntry(content = it) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = NoirTextPrimary),
                cursorBrush = SolidColor(NoirPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                decorationBox = { innerTextField ->
                    if (entry?.content.isNullOrEmpty()) {
                        Text("Start writing...", color = NoirTextSecondary)
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = NoirTextSecondary.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(24.dp))

            EditorField(
                label = "FELT",
                value = entry?.felt ?: "",
                onValueChange = { viewModel.updateEntry(felt = it) },
                placeholder = "Balanced, creative...",
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                EditorField(
                    label = "SCREEN TIME",
                    value = entry?.screenTime ?: "",
                    onValueChange = { viewModel.updateEntry(screenTime = it) },
                    placeholder = "2h 15m",
                    modifier = Modifier.weight(1f),
                    imeAction = ImeAction.Next,
                    onNext = { focusManager.moveFocus(FocusDirection.Right) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    TimeEditorField(
                        label = "SLEPT AT",
                        value = (entry?.sleptAt ?: "").replace(" AM", "").replace(" PM", ""),
                        onValueChange = { input ->
                            val clean = input.replace(" AM", "").replace(" PM", "")
                            viewModel.updateEntry(sleptAt = "$clean $sleptAtAmPm") 
                        },
                        imeAction = ImeAction.Done,
                        onNext = { focusManager.clearFocus() }
                    )
                    AmPmToggle(
                        selected = sleptAtAmPm, 
                        onSelected = { 
                            sleptAtAmPm = it
                            val currentVal = entry?.sleptAt ?: ""
                            val clean = currentVal.replace(" AM", "").replace(" PM", "")
                            if (clean.isNotEmpty()) {
                                viewModel.updateEntry(sleptAt = "$clean $it")
                            }
                        }
                    )
                }
            }
        }
    }
}

class TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(4)
        var out = ""
        for (i in digits.indices) {
            out += digits[i]
            if (i == 1 && digits.length > 2) out += ":"
        }

        // If we want the colon to be "pre-written" when empty, we might need a different approach.
        // But the user said "after 2 digits user automatically goes after the separator".
        // This transformation handles that visually.

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

@Composable
fun TimeEditorField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    onNext: () -> Unit = {}
) {
    // value is expected to be HH:MM or similar
    val digits = value.replace(":", "").filter { it.isDigit() }.take(4)
    
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField(
            value = digits,
            onValueChange = { input ->
                val hasSpace = input.contains(" ")
                val digitsOnly = input.filter { it.isDigit() }.take(4)
                
                var newDigits = digitsOnly
                if (hasSpace && digitsOnly.length < 2) {
                    // Pad single digit or empty with 0s when space is pressed
                    newDigits = digitsOnly.padStart(2, '0')
                }
                
                if (newDigits.length <= 4) {
                    var formatted = newDigits
                    if (newDigits.length >= 2) {
                        formatted = newDigits.substring(0, 2) + ":" + newDigits.substring(2)
                    }
                    onValueChange(formatted)
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = NoirTextPrimary),
            cursorBrush = SolidColor(NoirPrimary),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction, 
                keyboardType = KeyboardType.Text // Use Text type to provide the space button
            ),
            keyboardActions = KeyboardActions(onAny = { onNext() }),
            visualTransformation = TimeVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (digits.isEmpty()) {
                        Text("00:00", style = MaterialTheme.typography.bodyLarge, color = NoirTextSecondary.copy(alpha = 0.5f))
                    } else if (digits.length < 2) {
                        // Show placeholder for remaining part
                        Row {
                            innerTextField()
                            Text(":00", style = MaterialTheme.typography.bodyLarge, color = NoirTextSecondary.copy(alpha = 0.5f))
                        }
                    } else if (digits.length == 2) {
                        Row {
                            innerTextField()
                            Text(":00", style = MaterialTheme.typography.bodyLarge, color = NoirTextSecondary.copy(alpha = 0.5f))
                        }
                    } else {
                        innerTextField()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = NoirTextSecondary.copy(alpha = 0.2f))
    }
}

@Composable
fun AmPmToggle(selected: String, onSelected: (String) -> Unit) {
    Row(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            "AM",
            modifier = Modifier
                .clickable { onSelected("AM") }
                .background(if (selected == "AM") NoirPrimary else Color.Transparent)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            color = if (selected == "AM") Color.White else NoirTextSecondary,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "PM",
            modifier = Modifier
                .clickable { onSelected("PM") }
                .background(if (selected == "PM") NoirPrimary else Color.Transparent)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            color = if (selected == "PM") Color.White else NoirTextSecondary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun EditorField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Default,
    onNext: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = NoirTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = NoirTextPrimary),
            cursorBrush = SolidColor(NoirPrimary),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = KeyboardActions(onAny = { onNext() }),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = NoirTextSecondary)
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = NoirTextSecondary.copy(alpha = 0.2f))
    }
}
