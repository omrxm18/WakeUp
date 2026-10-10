package com.omrxm18.wakeup.util

import android.content.Context
import android.provider.Settings
import android.text.TextUtils

fun Context.isAccessibilityServiceEnabled(serviceClass: Class<*>): Boolean {
    val expectedComponentName = "$packageName/${serviceClass.name}"
    val enabledServicesSetting = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
        if (colonSplitter.next().equals(expectedComponentName, ignoreCase = true)) {
            return true
        }
    }
    return false
}
