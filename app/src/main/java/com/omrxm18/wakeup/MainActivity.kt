package com.omrxm18.wakeup

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.omrxm18.wakeup.databinding.ActivityMainBinding
import com.omrxm18.wakeup.util.CaffeineService
import com.omrxm18.wakeup.util.isAccessibilityServiceEnabled

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enableButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // Optional button for Battery Optimization
        binding.tileInstructions.setOnClickListener {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
            }
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val enabled = isAccessibilityServiceEnabled(CaffeineService::class.java)

        binding.statusText.text = if (enabled) {
            "Step 1 Complete: Accessibility Service is enabled.\n\n" +
            "On Vivo/Oppo devices, please also check:\n" +
            "Settings > Battery > Background Power Management > Wake Up > Don't restrict."
        } else {
            "Step 1: Please enable the Accessibility Service to allow the app to keep your screen awake."
        }

        binding.enableButton.text = if (enabled) "Open Settings" else "Enable Service"
        binding.tileInstructions.alpha = if (enabled) 1.0f else 0.5f
    }
}
