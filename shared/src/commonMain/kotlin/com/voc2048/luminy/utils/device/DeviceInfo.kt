/*
 * Project Luminy (螢光) were
 * Organized & Develop by Vocaloid2048.
 * Copyright © 2026 Vocaloid2048 版權所有
 */

package com.voc2048.luminy.utils.device

import kotlinx.serialization.Serializable


@Serializable
data class DeviceInfo(
    val deviceModel: String = "Unknown",  //DUMMY-A1000
    val deviceOSName: String = "Unknown", //Android
    val deviceOSVersion: String = "Unknown", //34
)