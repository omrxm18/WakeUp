import java.util.Properties
val keystorePropertiesFile = rootProject.file("key.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use { stream ->
        keystoreProperties.load(stream)
    }
}

fun getSecret(key: String, envVar: String): String? {
    return keystoreProperties.getProperty(key) ?: System.getenv(envVar)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

fun gitCommitCount(): Int = try {
    ProcessBuilder("git", "rev-list", "--count", "HEAD")
        .redirectErrorStream(true).start()
        .inputStream.bufferedReader().readText().trim().toIntOrNull() ?: 1
} catch (_: Exception) { 1 }

fun gitLatestTag(): String = try {
    val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
        .redirectErrorStream(true).start()
    val result = process.inputStream.bufferedReader().readText().trim()
    if (process.waitFor() == 0 && result.isNotEmpty()) result.removePrefix("v") else "0.0.0"
} catch (_: Exception) { "0.0.0" }

android {
    namespace = "com.omrxm18.wakeup"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.omrxm18.wakeup"
        minSdk = 24
        targetSdk = 34
        versionCode = gitCommitCount()
        versionName = gitLatestTag()
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    signingConfigs {
        create("release") {
            val storeFilePath = getSecret("storeFile", "KEYSTORE_FILE")
            val storePass = getSecret("storePassword", "KEYSTORE_PASSWORD")
            val alias = getSecret("keyAlias", "KEY_ALIAS")
            val keyPass = getSecret("keyPassword", "KEY_PASSWORD")
            if (!storeFilePath.isNullOrEmpty() && !storePass.isNullOrEmpty() && !alias.isNullOrEmpty() && !keyPass.isNullOrEmpty()) {
                storeFile = rootProject.file(storeFilePath)
                storePassword = storePass
                keyAlias = alias
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning?.storeFile?.exists() == true) {
                signingConfig = releaseSigning
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                          "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.ktx)
}
