package com.example.permitely.ui.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.permitely.ui.common.PermitelyAppBar
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Notifications Screen - View in-app notifications
 * Shows notification list with read/unread status and mark all as read functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit = {}
) {
    var notifications by remember { mutableStateOf(getDummyNotifications()) }
    var isMarkingAllRead by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Count unread notifications
    val unreadCount = notifications.count { !it.isRead }

    // Mark all as read function
    fun markAllAsRead() {
        scope.launch {
            isMarkingAllRead = true
            delay(1000) // Simulate API call
            notifications = notifications.map { it.copy(isRead = true) }
            isMarkingAllRead = false
        }
    }

    // Mark single notification as read
    fun markAsRead(notificationId: String) {
        notifications = notifications.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else notification
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            NotificationsTopBar(
                onNavigateBack = onNavigateBack,
                unreadCount = unreadCount
            )

            // Content
            if (notifications.isEmpty()) {
                // Empty state
                EmptyNotificationsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mark all as read button (only show if there are unread notifications)
                    if (unreadCount > 0) {
                        item {
                            MarkAllAsReadButton(
                                isLoading = isMarkingAllRead,
                                unreadCount = unreadCount,
                                onClick = { markAllAsRead() }
                            )
                        }
                    }

                    // Notifications list
                    items(notifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = { markAsRead(notification.id) }
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsTopBar(
    onNavigateBack: () -> Unit,
    unreadCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Primary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (unreadCount > 0) "$unreadCount unread notifications" else "All caught up!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (unreadCount > 0) Primary else TextSecondary
                )
            }

            // Notification icon with badge
            if (unreadCount > 0) {
                Box {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )

                    // Unread badge
                    Card(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 8.dp, y = (-8).dp),
                        colors = CardDefaults.cardColors(containerColor = Error),
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkAllAsReadButton(
    isLoading: Boolean,
    unreadCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Primary
            ),
            elevation = null
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Primary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Marking as read...")
            } else {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mark all $unreadCount as read")
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: Notification,
    onMarkAsRead: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Surface else Primary.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 2.dp else 4.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = if (!notification.isRead) onMarkAsRead else { {} }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Notification icon
            Card(
                modifier = Modifier.size(48.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getNotificationIconColor(notification.type).copy(alpha = 0.1f)
                ),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getNotificationIcon(notification.type),
                        contentDescription = null,
                        tint = getNotificationIconColor(notification.type),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Notification content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Header with visitor name and timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.visitorName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = formatTimestamp(notification.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Notification message
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Notification type badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = getNotificationIconColor(notification.type).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = notification.type.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = getNotificationIconColor(notification.type),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Unread indicator
            if (!notification.isRead) {
                Card(
                    modifier = Modifier.size(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary),
                    shape = CircleShape
                ) {}
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.size(120.dp),
            colors = CardDefaults.cardColors(containerColor = Secondary.copy(alpha = 0.1f)),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Notifications",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You're all caught up! New notifications will appear here.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Helper functions
private fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.VISITOR_REQUEST -> Icons.Default.PersonAdd
        NotificationType.VISITOR_APPROVED -> Icons.Default.CheckCircle
        NotificationType.VISITOR_REJECTED -> Icons.Default.Cancel
        NotificationType.VISITOR_ARRIVED -> Icons.Default.Login
        NotificationType.VISITOR_DEPARTED -> Icons.Default.Logout
        NotificationType.PASS_GENERATED -> Icons.Default.QrCode
        NotificationType.PASS_EXPIRED -> Icons.Default.Schedule
        NotificationType.SYSTEM -> Icons.Default.Info
    }
}

private fun getNotificationIconColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.VISITOR_REQUEST -> Secondary
        NotificationType.VISITOR_APPROVED -> Success
        NotificationType.VISITOR_REJECTED -> Error
        NotificationType.VISITOR_ARRIVED -> Primary
        NotificationType.VISITOR_DEPARTED -> Secondary
        NotificationType.PASS_GENERATED -> Primary
        NotificationType.PASS_EXPIRED -> Error
        NotificationType.SYSTEM -> Secondary
    }
}

private fun formatTimestamp(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        timestamp
    }
}

// Data classes
data class Notification(
    val id: String,
    val message: String,
    val timestamp: String,
    val visitorName: String,
    val isRead: Boolean,
    val type: NotificationType
)

enum class NotificationType(val displayName: String) {
    VISITOR_REQUEST("New Request"),
    VISITOR_APPROVED("Approved"),
    VISITOR_REJECTED("Rejected"),
    VISITOR_ARRIVED("Arrived"),
    VISITOR_DEPARTED("Departed"),
    PASS_GENERATED("Pass Generated"),
    PASS_EXPIRED("Pass Expired"),
    SYSTEM("System")
}

// Dummy data function
private fun getDummyNotifications(): List<Notification> {
    return listOf(
        Notification(
            id = "1",
            message = "New visitor request submitted for tomorrow's meeting",
            timestamp = "2024-12-15 14:30:00",
            visitorName = "Alice Johnson",
            isRead = false,
            type = NotificationType.VISITOR_REQUEST
        ),
        Notification(
            id = "2",
            message = "Visitor request has been approved and pass generated",
            timestamp = "2024-12-15 10:15:00",
            visitorName = "Bob Wilson",
            isRead = false,
            type = NotificationType.VISITOR_APPROVED
        ),
        Notification(
            id = "3",
            message = "Visitor has arrived at the main entrance",
            timestamp = "2024-12-15 09:45:00",
            visitorName = "Carol Davis",
            isRead = true,
            type = NotificationType.VISITOR_ARRIVED
        ),
        Notification(
            id = "4",
            message = "Digital pass has been generated successfully",
            timestamp = "2024-12-14 16:20:00",
            visitorName = "David Miller",
            isRead = true,
            type = NotificationType.PASS_GENERATED
        ),
        Notification(
            id = "5",
            message = "Visitor request was rejected due to incomplete information",
            timestamp = "2024-12-14 14:10:00",
            visitorName = "Eva Brown",
            isRead = true,
            type = NotificationType.VISITOR_REJECTED
        ),
        Notification(
            id = "6",
            message = "Visitor has departed from the premises",
            timestamp = "2024-12-14 12:30:00",
            visitorName = "Frank Garcia",
            isRead = true,
            type = NotificationType.VISITOR_DEPARTED
        ),
        Notification(
            id = "7",
            message = "Digital pass has expired. Please generate a new one if needed",
            timestamp = "2024-12-13 18:00:00",
            visitorName = "Grace Lee",
            isRead = true,
            type = NotificationType.PASS_EXPIRED
        ),
        Notification(
            id = "8",
            message = "System maintenance scheduled for tonight from 2 AM to 4 AM",
            timestamp = "2024-12-13 15:45:00",
            visitorName = "System Admin",
            isRead = true,
            type = NotificationType.SYSTEM
        )
    )
}
