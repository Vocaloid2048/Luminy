package com.voc2048.luminy.utils.device

import com.voc2048.luminy.BuildKonfig
import kotlinx.serialization.Serializable


@Serializable
data class AppInfo(
    val appProfile: String = "BETA",
    val appVersionName: String = "2.3.9",
    val appVersionCode: Int = 2999,
){
    companion object{
        val AppInfoInstance = AppInfo(
            BuildKonfig.appProfile,
            BuildKonfig.appVersionName, BuildKonfig.appVersionCode)
    }
}