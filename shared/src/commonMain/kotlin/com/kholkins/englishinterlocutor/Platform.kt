package com.kholkins.englishinterlocutor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform