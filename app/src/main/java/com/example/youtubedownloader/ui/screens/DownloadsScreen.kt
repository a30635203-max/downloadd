package com.example.youtubedownloader.ui.screens

import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File
import kotlin.collections.mutableStateListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onBack: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("all") } // all, video, audio
    val downloads = remember { mutableStateListOf<DownloadItem>() }
    
    LaunchedEffect(Unit) {
        loadDownloads(downloads)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التحميلات") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleDarkMode() }) {
                        Icon(
                            if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // شريط البحث والتصفية
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("بحث...") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                )
                
                // قائمة التصفية
                var expanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("الكل") },
                            onClick = { filterType = "all"; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("فيديو") },
                            onClick = { filterType = "video"; expanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("صوت") },
                            onClick = { filterType = "audio"; expanded = false }
                        )
                    }
                }
            }
            
            // قائمة الملفات
            val filteredDownloads = downloads.filter { item ->
                (filterType == "all" || item.type == filterType) &&
                (item.name.contains(searchQuery, ignoreCase = true) || searchQuery.isEmpty())
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredDownloads) { item ->
                    DownloadItemCard(item = item)
                }
            }
        }
    }
}

data class DownloadItem(
    val id: String,
    val name: String,
    val type: String, // "video" or "audio"
    val path: String,
    val size: String,
    val date: String
)

@Composable
fun DownloadItemCard(item: DownloadItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "${item.type} • ${item.size} • ${item.date}", 
                     style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { /* فتح الملف */ }) {
                Icon(
                    if (item.type == "audio") Icons.Filled.AudioFile else Icons.Filled.VideoFile,
                    contentDescription = item.type
                )
            }
        }
    }
}

fun loadDownloads(downloads: MutableList<DownloadItem>) {
    val baseDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "YouTubeDownloader"
    )
    
    val videoDir = File(baseDir, "فيديو")
    val audioDir = File(baseDir, "صوت")
    
    videoDir.listFiles()?.forEach { file ->
        downloads.add(DownloadItem(
            id = file.name,
            name = file.nameWithoutExtension,
            type = "video",
            path = file.absolutePath,
            size = "${file.length() / 1024 / 1024} MB",
            date = file.lastModified().toString()
        ))
    }
    
    audioDir.listFiles()?.forEach { file ->
        downloads.add(DownloadItem(
            id = file.name,
            name = file.nameWithoutExtension,
            type = "audio",
            path = file.absolutePath,
            size = "${file.length() / 1024 / 1024} MB",
            date = file.lastModified().toString()
        ))
    }
}