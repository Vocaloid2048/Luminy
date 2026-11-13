package com.voc2048.luminy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform