package com.omrxm18.wakeup.util

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.omrxm18.wakeup.R
import com.omrxm18.wakeup.MainActivity

class WakeTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        if (!isAccessibilityServiceEnabled(CaffeineService::class.java)) {
            // Permission not granted yet — send the user to MainActivity to fix that.
            val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
            return
        }

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val currentlyOn = prefs.getBoolean(KEY_ENABLED, true)
        prefs.edit().putBoolean(KEY_ENABLED, !currentlyOn).apply()
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val serviceGranted = isAccessibilityServiceEnabled(CaffeineService::class.java)
        val toggledOn = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        .getBoolean(KEY_ENABLED, true)

        tile.state = if (serviceGranted && toggledOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = getString(R.string.tile_label)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (!serviceGranted) "Tap to grant access" else if (toggledOn) "On" else "Off"
        }
        tile.updateTile()
    }

    companion object {
        const val PREFS_NAME = "wakeup_prefs"
        const val KEY_ENABLED = "keep_awake_enabled"
    }
}
