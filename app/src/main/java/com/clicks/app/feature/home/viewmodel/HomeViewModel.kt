package com.clicks.app.feature.home.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.clicks.app.feature.home.model.NotificationItem

class HomeViewModel : ViewModel() {
    val notifications = mutableStateListOf(
        NotificationItem(1, "Order Delivered", "Your order #ORD-78421 has been successfully delivered.", "2h ago", false),
        NotificationItem(2, "Special Offer", "Flash sale! Enjoy 40% off on all new summer arrivals.", "1d ago", false),
        NotificationItem(3, "Order Dispatched", "Your order #ORD-78390 is out for delivery.", "2d ago", true)
    )

    val unreadNotificationCount: Int
        get() = notifications.count { !it.isRead }

    fun markAllNotificationsRead() {
        notifications.forEach { it.isRead = true }
    }
}
