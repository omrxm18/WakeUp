package com.omrxm18.wakeup.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent

class CaffeineService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())

    private val keepAwake = object : Runnable {
        override fun run() {
            if (isScreenOn() && isToggledOn()) {
                val path = Path().apply { moveTo(1f, 1f) }
                dispatchGesture(
                    GestureDescription.Builder().addStroke(
                        GestureDescription.StrokeDescription(path, 0, 10)
                    ).build(),
                    null,
                    null
                )
            }
            handler.postDelayed(this, 10_000)
        }
    }

    override fun onServiceConnected() {
        handler.post(keepAwake)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        handler.removeCallbacks(keepAwake)
    }

    override fun onDestroy() {
        handler.removeCallbacks(keepAwake)
        super.onDestroy()
    }

    private fun isScreenOn(): Boolean {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return pm.isInteractive
    }

    private fun isToggledOn(): Boolean =
        getSharedPreferences(WakeTileService.PREFS_NAME, MODE_PRIVATE)
        .getBoolean(WakeTileService.KEY_ENABLED, true)
}
