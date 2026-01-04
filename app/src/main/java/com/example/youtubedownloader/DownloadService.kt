package com.example.youtubedownloader

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.net.URL
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection

class DownloadService : Service() {
    private val binder = LocalBinder()
    private var notificationManager: NotificationManager? = null
    private val CHANNEL_ID = "download_channel"
    private val NOTIFICATION_ID = 1
    
    private var downloadJob: Job? = null
    
    inner class LocalBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val url = it.getStringExtra("url") ?: return START_NOT_STICKY
            val format = it.getStringExtra("format") ?: "video"
            val quality = it.getStringExtra("quality") ?: "720p"
            
            startDownload(url, format, quality)
        }
        return START_STICKY
    }
    
    private fun startDownload(url: String, format: String, quality: String) {
        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // هنا ستتم إضافة مكتبة لتحميل من اليوتيوب مثل youtube-dl
                // هذا مثال مبسط
                updateNotification("جاري التحميل...", 0)
                
                // محاكاة التحميل
                for (i in 1..100) {
                    delay(100)
                    updateNotification("جاري التحميل...", i)
                }
                
                updateNotification("اكتمل التحميل!", 100)
                
            } catch (e: Exception) {
                updateNotification("فشل التحميل: ${e.message}", 0)
            }
        }
    }
    
    private fun updateNotification(text: String, progress: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("YouTube Downloader")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تنزيل الفيديو",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات تحميل الفيديو"
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    override fun onDestroy() {
        downloadJob?.cancel()
        super.onDestroy()
    }
}