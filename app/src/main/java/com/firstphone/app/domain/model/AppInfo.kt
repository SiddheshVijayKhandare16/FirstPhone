package com.firstphone.app.domain.model

data class AppInfo(
    val packageName: String,
    val label: String,
    val isSystemLauncher: Boolean = false
)
