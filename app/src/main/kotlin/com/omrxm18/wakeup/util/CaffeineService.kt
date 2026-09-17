package com.omrxm18.wakeup.util

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.omrxm18.wakeup.R

class CaffeineService : AccessibilityService(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var wakeLock: PowerManager.WakeLock? = null
    private val channelId = "caffeine_service_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            "WakeUp:CaffeineService"
        )

        createNotificationChannel()

        getSharedPreferences(WakeTileService.PREFS_NAME, MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateState()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == WakeTileService.KEY_ENABLED) {
            updateState()
        }
    }

    private fun updateState() {
        val enabled = getSharedPreferences(WakeTileService.PREFS_NAME, MODE_PRIVATE)
            .getBoolean(WakeTileService.KEY_ENABLED, true)

        if (enabled) {
            startForegroundService()
            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire()
            }
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        }
    }

    private fun startForegroundService() {
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Wake Up is active")
            .setContentText("Your screen will stay on.")
            .setSmallIcon(R.drawable.ic_tile_wake) // Ensure this exists or use a default
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(notificationId, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Keep Awake Service"
            val descriptionText = "Notifications for the Keep Awake service"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        releaseLock()
    }

    override fun onDestroy() {
        getSharedPreferences(WakeTileService.PREFS_NAME, MODE_PRIVATE)
            .unregisterOnSharedPreferenceChangeListener(this)
        releaseLock()
        super.onDestroy()
    }

    private fun releaseLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }
}
