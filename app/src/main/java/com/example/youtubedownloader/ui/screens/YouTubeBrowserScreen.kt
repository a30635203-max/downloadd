package com.example.youtubedownloader.ui.screens

import android.content.Context
import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.youtubedownloader.DownloadService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeBrowserScreen(
    onNavigateToDownloads: () -> Unit,
    onToggleDarkMode: () -> Unit,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showFormatDialog by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YouTube Downloader") },
                actions = {
                    IconButton(onClick = { onToggleDarkMode() }) {
                        Icon(
                            if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                    IconButton(onClick = { onNavigateToDownloads() }) {
                        Icon(Icons.Filled.Download, contentDescription = "Downloads")
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
            // شريط الأدوات السفلي
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(
                        onClick = { webView?.goBack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                    IconButton(
                        onClick = { webView?.goForward() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Forward")
                    }
                    IconButton(
                        onClick = { webView?.reload() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(
                        onClick = { showQualityDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Hd, contentDescription = "Quality")
                    }
                    IconButton(
                        onClick = { showFormatDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.AudioFile, contentDescription = "Format")
                    }
                }
            )
            
            // WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let { currentUrl = it }
                            }
                            
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return false
                            }
                        }
                        loadUrl("https://m.youtube.com")
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    
    // حوار اختيار الجودة
    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("اختر جودة الفيديو") },
            text = {
                Column {
                    listOf("144p", "240p", "360p", "480p", "720p", "1080p", "أفضل جودة").forEach { quality ->
                        ListItem(
                            headlineContent = { Text(quality) },
                            modifier = Modifier.clickable {
                                startDownload(context, currentUrl, "video", quality)
                                showQualityDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
    
    // حوار اختيار الصيغة
    if (showFormatDialog) {
        AlertDialog(
            onDismissRequest = { showFormatDialog = false },
            title = { Text("اختر صيغة التحميل") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("فيديو") },
                        modifier = Modifier.clickable {
                            startDownload(context, currentUrl, "video", "720p")
                            showFormatDialog = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("صوت MP3") },
                        modifier = Modifier.clickable {
                            startDownload(context, currentUrl, "audio", "128k")
                            showFormatDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFormatDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

fun startDownload(context: Context, url: String, format: String, quality: String) {
    val intent = Intent(context, DownloadService::class.java).apply {
        putExtra("url", url)
        putExtra("format", format)
        putExtra("quality", quality)
    }
    context.startService(intent)
}