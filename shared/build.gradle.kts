import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import properties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.gradle.declarative.dsl.schema.FqName.Empty.packageName

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)

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

initGradleProperties()

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    android {
       namespace = "com.voc2048.luminy.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.kotlinx.coroutines.android)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.testRunner)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

buildkonfig {
    packageName = "com.voc2048.luminy"
    //Read only
    defaultConfigs {
        buildConfigField(STRING, "appProfile", appProfile)
        buildConfigField(STRING, "appVersionName", appVersion)
        buildConfigField(INT, "appVersionCode", properties.getProperty("APP_VERSION_CODE"))
    }
}

fun initGradleProperties(){
    //Write only
    properties["APP_PROFILE"] = appProfile
    properties["APP_VERSION"] = appVersion
    properties["APP_VERSION_CODE"] = versionCodeFinal.toString()
    properties.store(file("../gradle.properties").outputStream(),null)
}