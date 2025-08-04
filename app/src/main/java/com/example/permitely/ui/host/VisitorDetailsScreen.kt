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
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
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

            if (hasPass) {
                // Pass details
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
            }
        }
    }
}

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

            // Primary actions
            if (visitor.status == VisitorStatus.APPROVED) {
                if (hasPass) {
                    ActionButton(
                        icon = Icons.Default.Share,
                        text = "Share QR Code",
                        containerColor = Primary,
                        contentColor = OnPrimary,
                        onClick = onShareQR
                    )
                } else {
                    ActionButton(
                        icon = Icons.Default.QrCode,
                        text = "Generate Pass",
                        containerColor = Success,
                        contentColor = Color.White,
                        onClick = onGeneratePass
                    )
                }
            }

            // Secondary actions
            ActionButton(
                icon = Icons.Default.Edit,
                text = "Edit Details",
                containerColor = Secondary.copy(alpha = 0.1f),
                contentColor = Secondary,
                onClick = onEdit
            )

            ActionButton(
                icon = Icons.Default.Delete,
                text = "Delete Visitor",
                containerColor = Error.copy(alpha = 0.1f),
                contentColor = Error,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Dialog Composables
@Composable
private fun DeleteVisitorDialog(
    visitorName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Error
            )
        },
        title = {
            Text(
                text = "Delete Visitor",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete $visitorName? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun QRCodeDialog(
    visitor: Visitor,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Digital Pass",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // QR Code placeholder
                Card(
                    modifier = Modifier.size(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Code",
                            tint = Primary,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Text(
                    text = visitor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Valid until: ${visitor.date} ${visitor.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            Button(onClick = onShare) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun GeneratePassDialog(
    visitor: Visitor,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = if (isLoading) { {} } else onDismiss,
        icon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Primary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = Primary
                )
            }
        },
        title = {
            Text(
                text = if (isLoading) "Generating Pass..." else "Generate Digital Pass",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (isLoading) {
                    "Please wait while we generate the digital pass for ${visitor.name}."
                } else {
                    "Generate a QR code pass for ${visitor.name}? This will allow them to use the digital pass for entry."
                },
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            if (!isLoading) {
                Button(onClick = onConfirm) {
                    Text("Generate")
                }
            }
        },
        dismissButton = {
            if (!isLoading) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
