package com.example.permitely.ui.guard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.permitely.data.models.DashboardStatsUiState
import com.example.permitely.ui.common.PermitelyProfileAppBar
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Guard Dashboard Screen - Main landing page for guards
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardDashboardScreen(
    onScanPass: () -> Unit = {},
    onCreateVisitor: () -> Unit = {},
    onViewNotifications: () -> Unit = {},
    onViewProfile: () -> Unit = {},
    viewModel: GuardDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Show error snackbar if there's an error
    if (uiState.error != null) {
        LaunchedEffect(uiState.error) {
            delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            PermitelyProfileAppBar(
                title = "Guard Dashboard",
                subtitle = "Manage visitor access",
                userName = uiState.userName,
                userRole = "Guard",
                onProfileClick = onViewProfile,
                onNotificationClick = onViewNotifications,
                notificationCount = 5,
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Loading/Error states
                    item {
                        if (uiState.isLoading) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Primary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Loading dashboard...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        if (uiState.error != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ErrorLight.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = uiState.error ?: "Unknown error",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }

                    // Quick Actions Section (Only 2 cards: Scan QR, Create Visitor)
                    item {
                        QuickActionsSection(
                            onScanPass = onScanPass,
                            onCreateVisitor = onCreateVisitor
                        )
                    }

                    // Stats Cards Section (3 cards: Total, Approved, Pending)
                    item {
                        StatsSection(uiState = uiState)
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    )
}

/**
 * Quick Actions Section - 2x2 grid of large action cards
 */
@Composable
private fun QuickActionsSection(
    onScanPass: () -> Unit,
    onCreateVisitor: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 2x2 Grid for actions
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // First row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionCard(
                    title = "Scan Pass",
                    subtitle = "Verify visitor passes",
                    icon = Icons.Default.QrCodeScanner,
                    gradient = Brush.linearGradient(
                        colors = listOf(Primary, PrimaryLight)
                    ),
                    onClick = onScanPass,
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    title = "Create Visitor",
                    subtitle = "Add new visitor",
                    icon = Icons.Default.PersonAdd,
                    gradient = Brush.linearGradient(
                        colors = listOf(Secondary, SecondaryLight)
                    ),
                    onClick = onCreateVisitor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Stats Section - 3 stats cards showing today's visitor information (vertical layout)
 */
@Composable
private fun StatsSection(uiState: DashboardStatsUiState) {
    Column {
        Text(
            text = "Today's Stats",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 3 stats cards placed vertically (one below another)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                title = "Today's Total Visitors",
                value = uiState.totalVisitors.toString(),
                icon = Icons.Default.People,
                backgroundColor = Primary.copy(alpha = 0.1f),
                iconColor = Primary,
                modifier = Modifier.fillMaxWidth()
            )
            StatsCard(
                title = "Today's Approved Visitors",
                value = uiState.approved.toString(),
                icon = Icons.Default.CheckCircle,
                backgroundColor = Success.copy(alpha = 0.1f),
                iconColor = Success,
                modifier = Modifier.fillMaxWidth()
            )
            StatsCard(
                title = "Today's Pending Visitors",
                value = uiState.pending.toString(),
                icon = Icons.Default.Schedule,
                backgroundColor = Warning.copy(alpha = 0.1f),
                iconColor = Warning,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Stats Card Component - Horizontal card for daily stats
 */
@Composable
private fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Action Card Component - Large card for quick actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
