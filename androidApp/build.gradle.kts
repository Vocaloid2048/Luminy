import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import properties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)

    id("com.codingfeline.buildkonfig").version("0.24.0")
}

/**
 * tasks to gradle.properties
 */
val properties = Properties()
file("../gradle.properties").inputStream().use { properties.load(it) }

val appVersion: String = SimpleDateFormat("yyyy.MM.dd").format(Date())
val versionCodeFinal = properties.getProperty("APP_VERSION_CODE", "0").toInt() + 1
val isForAppStore = properties.getProperty("IS_FOR_APPSTORE", "true").toBoolean()
val isForPlayStore = properties.getProperty("IS_FOR_PLAYSTORE", "false").toBoolean()
var appProfile: String? = properties.getProperty("APP_PROFILE", "DEV")

if(isForPlayStore) appProfile = "PRODUCTION_GP"

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.voc2048.luminy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.voc2048.luminy"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = versionCodeFinal
        versionName = "1.0"
    }

    flavorDimensions += "version"
    productFlavors{
        properties["APP_PLATFORM"] = "Android"

        create("0dev"){
            versionName = "DEV ${appVersion} (${versionCodeFinal})"
        }
        create("beta"){
            applicationId = "com.voc2048.luminy.beta"
            versionName = "BETA ${appVersion} (${versionCodeFinal})"
        }
        create("production_googleplay"){
            versionName = "GP ${appVersion} (${versionCodeFinal})"
        }
        create("production"){
            applicationId = "com.voc2048.luminy"
            versionName = "${appVersion} (${versionCodeFinal})"
        }

        properties.store(file("../gradle.properties").outputStream(),null)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

fun initGradleProperties(){
    //Write only
    properties["APP_PROFILE"] = appProfile
    properties["APP_VERSION"] = appVersion
    properties["APP_VERSION_CODE"] = versionCodeFinal.toString()
    properties.store(file("../gradle.properties").outputStream(),null)
}