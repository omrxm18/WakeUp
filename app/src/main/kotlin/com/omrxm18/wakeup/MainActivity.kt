package com.omrxm18.wakeup

import android.content.Intent
import android.os.Bundle
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

            updateStatus()
        }

        override fun onResume() {
            super.onResume()
            updateStatus() // re-check after user comes back from Settings
        }

        private fun updateStatus() {
            val enabled = isAccessibilityServiceEnabled(CaffeineService::class.java)
            binding.statusText.text = if (enabled) {
                "Keep-awake service is ON"
            } else {
                "Keep-awake service is OFF"
            }
            binding.enableButton.text = if (enabled) "Open Settings" else "Enable Service"
        }
}
