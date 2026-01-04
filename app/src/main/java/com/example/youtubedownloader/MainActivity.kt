package com.example.youtubedownloader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.youtubedownloader.ui.screens.*
import com.example.youtubedownloader.ui.theme.YouTubeDownloaderTheme
import java.io.File

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // جميع الصلاحيات مُنحت
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // إنشاء المجلدات عند بدء التشغيل
        createAppDirectories()
        
        // طلب الصلاحيات
        requestPermissions()
        
        setContent {
            YouTubeDownloaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    YouTubeDownloaderApp()
                }
            }
        }
    }
    
    private fun createAppDirectories() {
        val baseDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "YouTubeDownloader"
        )
        val videoDir = File(baseDir, "فيديو")
        val audioDir = File(baseDir, "صوت")
        
        if (!videoDir.exists()) videoDir.mkdirs()
        if (!audioDir.exists()) audioDir.mkdirs()
    }
    
    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        
        permissions.add(Manifest.permission.INTERNET)
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }
}

@Composable
fun YouTubeDownloaderApp() {
    val navController = rememberNavController()
    var isDarkMode by remember { mutableStateOf(false) }
    
    YouTubeDownloaderTheme(darkTheme = isDarkMode) {
        NavHost(
            navController = navController,
            startDestination = "browser"
        ) {
            composable("browser") {
                YouTubeBrowserScreen(
                    onNavigateToDownloads = { navController.navigate("downloads") },
                    onToggleDarkMode = { isDarkMode = !isDarkMode },
                    isDarkMode = isDarkMode
                )
            }
            composable("downloads") {
                DownloadsScreen(
                    onBack = { navController.popBackStack() },
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}