package com.example.permitely.ui.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Visitor Details Screen - Detailed view of specific visitor
 * Shows visitor information, status timeline, QR code, and action buttons
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitorDetailsScreen(
    visitor: Visitor,
    onNavigateBack: () -> Unit = {},
    onEditVisitor: (Visitor) -> Unit = {},
    onDeleteVisitor: (String) -> Unit = {},
    onGeneratePass: (String) -> Unit = {},
    onShareQRCode: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Dialog states
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showQRDialog by remember { mutableStateOf(false) }
    var showGeneratePassDialog by remember { mutableStateOf(false) }
    var isGeneratingPass by remember { mutableStateOf(false) }

    // Pass generation state (simulate if visitor has a pass)
    var hasPass by remember { mutableStateOf(visitor.status == VisitorStatus.APPROVED) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top App Bar
            VisitorDetailsTopBar(
                visitorName = visitor.name,
                onNavigateBack = onNavigateBack
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Visitor Information Card
                VisitorInfoCard(visitor = visitor)

                // Status Timeline Card
                StatusTimelineCard(visitor = visitor)

                // QR Code and Pass Card (if approved)
                if (visitor.status == VisitorStatus.APPROVED) {
                    QRCodePassCard(
                        visitor = visitor,
                        hasPass = hasPass,
                        onShowQR = { showQRDialog = true },
                        onGeneratePass = { showGeneratePassDialog = true }
                    )
                }

                // Action Buttons
                ActionButtonsSection(
                    visitor = visitor,
                    hasPass = hasPass,
                    onEdit = { onEditVisitor(visitor) },
                    onDelete = { showDeleteDialog = true },
                    onGeneratePass = { showGeneratePassDialog = true },
                    onShareQR = { showQRDialog = true }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        DeleteVisitorDialog(
            visitorName = visitor.name,
            onConfirm = {
                onDeleteVisitor(visitor.id)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    // QR Code Dialog
    if (showQRDialog) {
        QRCodeDialog(
            visitor = visitor,
            onDismiss = { showQRDialog = false },
            onShare = {
                onShareQRCode()
                showQRDialog = false
            }
        )
    }

    // Generate Pass Dialog
    if (showGeneratePassDialog) {
        GeneratePassDialog(
            visitor = visitor,
            isLoading = isGeneratingPass,
            onConfirm = {
                scope.launch {
                    isGeneratingPass = true
                    delay(2000) // Simulate API call
                    hasPass = true
                    isGeneratingPass = false
                    showGeneratePassDialog = false
                    onGeneratePass(visitor.id)
                }
            },
            onDismiss = { showGeneratePassDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitorDetailsTopBar(
    visitorName: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Visitor Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = visitorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Background
        )
    )
}

@Composable
private fun VisitorInfoCard(visitor: Visitor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Visitor Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                StatusBadge(status = visitor.status)
            }

            // Profile section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile icon
                Card(
                    modifier = Modifier.size(60.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Name and basic info
                Column {
                    Text(
                        text = visitor.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "ID: ${visitor.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Divider(color = Secondary.copy(alpha = 0.2f))

            // Contact details
            InfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = visitor.email
            )

            InfoRow(
                icon = Icons.Default.Phone,
                label = "Phone",
                value = visitor.phone
            )

            InfoRow(
                icon = Icons.Default.Business,
                label = "Purpose",
                value = visitor.purpose
            )

            InfoRow(
                icon = Icons.Default.DateRange,
                label = "Visit Date",
                value = visitor.date
            )

            InfoRow(
                icon = Icons.Default.Schedule,
                label = "Visit Time",
                value = visitor.time
            )

            InfoRow(
                icon = Icons.Default.Create,
                label = "Created",
                value = visitor.createdAt
            )
        }
    }
}

@Composable
private fun StatusBadge(status: VisitorStatus) {
    val (backgroundColor, textColor) = when (status) {
        VisitorStatus.PENDING -> Secondary.copy(alpha = 0.1f) to Secondary
        VisitorStatus.APPROVED -> Success.copy(alpha = 0.1f) to Success
        VisitorStatus.REJECTED -> Error.copy(alpha = 0.1f) to Error
        VisitorStatus.EXPIRED -> Color.Gray.copy(alpha = 0.1f) to Color.Gray
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = status.displayName.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusTimelineCard(visitor: Visitor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Status Timeline",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            // Timeline items (simulated)
            TimelineItem(
                icon = Icons.Default.Create,
                title = "Request Created",
                subtitle = "Visitor registration submitted",
                time = visitor.createdAt,
                isActive = true
            )

            if (visitor.status != VisitorStatus.PENDING) {
                TimelineItem(
                    icon = when (visitor.status) {
                        VisitorStatus.APPROVED -> Icons.Default.CheckCircle
                        VisitorStatus.REJECTED -> Icons.Default.Cancel
                        VisitorStatus.EXPIRED -> Icons.Default.Schedule
                        else -> Icons.Default.Info
                    },
                    title = when (visitor.status) {
                        VisitorStatus.APPROVED -> "Request Approved"
                        VisitorStatus.REJECTED -> "Request Rejected"
                        VisitorStatus.EXPIRED -> "Visit Expired"
                        else -> "Status Updated"
                    },
                    subtitle = "Status changed by host",
                    time = "Today, 10:30 AM",
                    isActive = true
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    time: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Card(
            modifier = Modifier.size(40.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isActive) Primary.copy(alpha = 0.1f) else Secondary.copy(alpha = 0.1f)
            ),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) Primary else Secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun QRCodePassCard(
    visitor: Visitor,
    hasPass: Boolean,
    onShowQR: () -> Unit,
    onGeneratePass: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Digital Pass",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (visitor.hasQRCode && visitor.qrCodeUrl != null) {
                // Pass details with QR code URL
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Pass Generated Successfully",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Success,
                        fontWeight = FontWeight.Medium
                    )

                    // Pass Information
                    visitor.passId?.let { passId ->
                        InfoRow(
                            icon = Icons.Default.Badge,
                            label = "Pass ID",
                            value = "PASS-$passId"
                        )
                    }

                    visitor.expiryTime?.let { expiryTime ->
                        InfoRow(
                            icon = Icons.Default.Schedule,
                            label = "Valid Until",
                            value = formatDateTime(expiryTime)
                        )
                    }

                    // QR Code URL Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "QR Code",
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "QR Code Access",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // QR Code URL Link
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                shape = RoundedCornerShape(8.dp),
                                onClick = onShowQR
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "QR Code URL:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = visitor.qrCodeUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Text(
                                text = "📧 QR code has been emailed to visitor",
                                style = MaterialTheme.typography.bodySmall,
                                color = Success,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onShowQR,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = OnPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View QR")
                        }

                        OutlinedButton(
                            onClick = { /* Share QR Code URL */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share")
                        }
                    }
                }
            } else if (hasPass) {
                // Legacy pass without QR code URL (fallback)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Pass Generated Successfully",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Success,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Valid until: ${visitor.date} ${visitor.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Text(
                        text = "Pass ID: PASS-${visitor.id.uppercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // QR Code preview placeholder
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    onClick = onShowQR
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = Primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Tap to view",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No pass generated yet. Generate a digital pass for easy entry.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Button(
                    onClick = onGeneratePass,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Success,
                        contentColor = OnSuccess
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Digital Pass")
                }
            }
        }
    }
}

// Helper composable for displaying information rows
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}

// Helper function to format date time
private fun formatDateTime(isoDateTime: String): String {
    return try {
        // Simple formatting - you can enhance this with proper date formatting
        val parts = isoDateTime.split("T")
        if (parts.size >= 2) {
            val date = parts[0]
            val time = parts[1].split("Z")[0].substring(0, 5) // Get HH:mm
            "$date at $time"
        } else {
            isoDateTime
        }
    } catch (e: Exception) {
        isoDateTime
    }
}

// Missing ActionButtonsSection function
@Composable
private fun ActionButtonsSection(
    visitor: Visitor,
    hasPass: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onGeneratePass: () -> Unit,
    onShareQR: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit Button
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                // Delete Button
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Error
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Error)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }

            if (!hasPass && visitor.status == VisitorStatus.APPROVED) {
                Button(
                    onClick = onGeneratePass,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Success,
                        contentColor = OnSuccess
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Digital Pass")
                }
            }
        }
    }
}

// Missing DeleteVisitorDialog function
@Composable
private fun DeleteVisitorDialog(
    visitorName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete Visitor",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = "Are you sure you want to delete $visitorName? This action cannot be undone.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error,
                    contentColor = OnError
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

// Missing QRCodeDialog function
@Composable
private fun QRCodeDialog(
    visitor: Visitor,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "QR Code",
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QR Code",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // QR Code placeholder
                Card(
                    modifier = Modifier.size(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = Primary,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "QR Code for ${visitor.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                visitor.qrCodeUrl?.let { url ->
                    Text(
                        text = "URL: $url",
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onShare,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

// Missing GeneratePassDialog function
@Composable
private fun GeneratePassDialog(
    visitor: Visitor,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        containerColor = Surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Generate Pass",
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Digital Pass",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Generate a digital pass for ${visitor.name}?",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "This will create a QR code that the visitor can use for entry.",
                    color = TextTertiary,
                    style = MaterialTheme.typography.bodySmall
                )

                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Primary
                        )
                        Text(
                            text = "Generating pass...",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Success,
                    contentColor = OnSuccess
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = OnSuccess
                    )
                } else {
                    Text("Generate")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
