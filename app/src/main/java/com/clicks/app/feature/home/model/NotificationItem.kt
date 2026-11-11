package com.clicks.app.feature.home.model

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    var isRead: Boolean = false
)
