package com.example.permitely.ui.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.permitely.ui.common.PermitelyTextField
import com.example.permitely.ui.common.PermitelySearchAppBar
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Visitors List Screen - View all visitors hosted by this host
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitorsListScreen(
    onNavigateBack: () -> Unit = {},
    onVisitorClick: (Visitor) -> Unit = {},
    viewModel: VisitorsListViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(VisitorFilter.ALL) }
    val scope = rememberCoroutineScope()

    // Observe ViewModel state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Convert API visitors to UI visitors
    val allVisitors = remember(uiState.visitors) {
        uiState.visitors.map { apiVisitor ->
            viewModel.convertToUiVisitor(apiVisitor)
        }
    }

    // Handle filter changes and trigger API calls
    LaunchedEffect(selectedFilter) {
        val statusFilter = when (selectedFilter) {
            VisitorFilter.ALL -> null
            VisitorFilter.PENDING -> "PENDING"
            VisitorFilter.APPROVED -> "APPROVED"
            VisitorFilter.REJECTED -> "REJECTED"
            VisitorFilter.EXPIRED -> "EXPIRED"
        }
        viewModel.filterByStatus(statusFilter)
    }

    // Filter visitors based on search (client-side filtering)
    val filteredVisitors = remember(searchQuery, allVisitors) {
        if (searchQuery.isEmpty()) {
            allVisitors
        } else {
            allVisitors.filter { visitor ->
                visitor.name.contains(searchQuery, ignoreCase = true) ||
                        visitor.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Show error if any
    uiState.errorMessage?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // You can show a snackbar or toast here
            println("Error loading visitors: $errorMessage")
            // Clear error after showing
            viewModel.clearError()
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
            VisitorsListTopBar(
                onNavigateBack = onNavigateBack,
                visitorsCount = filteredVisitors.size
            )

            // Search and Filters
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it }
                )

                // Filter Chips
                FilterChips(
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    visitorCounts = getVisitorCounts(allVisitors)
                )
            }

            // Visitors List
            if (filteredVisitors.isEmpty()) {
                EmptyState(
                    searchQuery = searchQuery,
                    selectedFilter = selectedFilter
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredVisitors) { visitor ->
                        VisitorCard(
                            visitor = visitor,
                            onVisitorClick = { onVisitorClick(visitor) },
                            onApprove = {
                                // TODO: Implement approve logic
                                scope.launch {
                                    // Simulate API call
                                }
                            },
                            onReject = {
                                // TODO: Implement reject logic
                                scope.launch {
                                    // Simulate API call
                                }
                            }
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

@Composable
private fun VisitorsListTopBar(
    onNavigateBack: () -> Unit,
    visitorsCount: Int
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
                    text = "All Visitors",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$visitorsCount visitors found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            IconButton(onClick = { /* TODO: Add sort/filter options */ }) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            PermitelyTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = "Search by name or email...",
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent)
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    selectedFilter: VisitorFilter,
    onFilterChange: (VisitorFilter) -> Unit,
    visitorCounts: Map<VisitorFilter, Int>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(VisitorFilter.values()) { filter ->
            FilterChip(
                filter = filter,
                isSelected = selectedFilter == filter,
                count = visitorCounts[filter] ?: 0,
                onClick = { onFilterChange(filter) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    filter: VisitorFilter,
    isSelected: Boolean,
    count: Int,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Primary else Surface
    val contentColor = if (isSelected) OnPrimary else TextSecondary

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = filter.displayName,
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                if (count > 0) {
                    Surface(
                        color = if (isSelected) OnPrimary.copy(alpha = 0.2f) else Primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = count.toString(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) OnPrimary else Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = backgroundColor,
            selectedLabelColor = contentColor,
            containerColor = backgroundColor,
            labelColor = contentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Primary else BorderLight,
            selectedBorderColor = Primary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitorCard(
    visitor: Visitor,
    onVisitorClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onVisitorClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Visitor Info
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        getStatusColor(visitor.status),
                                        getStatusColor(visitor.status).copy(alpha = 0.7f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = visitor.name.first().toString().uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = OnPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Name and Email
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = visitor.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = visitor.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Status Badge
                StatusBadge(status = visitor.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visitor Details
            VisitorDetailsRow(
                icon = Icons.Default.Phone,
                label = "Phone",
                value = visitor.phone
            )

            VisitorDetailsRow(
                icon = Icons.Default.Description,
                label = "Purpose",
                value = visitor.purpose
            )

            VisitorDetailsRow(
                icon = Icons.Default.Schedule,
                label = "Visit Time",
                value = "${visitor.date} at ${visitor.time}"
            )

            // Action Buttons for Pending Visitors
            if (visitor.status == VisitorStatus.PENDING) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reject Button
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Error,
                            containerColor = Error.copy(alpha = 0.05f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reject",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject")
                    }

                    // Approve Button
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Success,
                            contentColor = OnSuccess
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Approve",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve")
                    }
                }
            }
        }
    }
}

@Composable
private fun VisitorDetailsRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$label: ",
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

@Composable
private fun StatusBadge(status: VisitorStatus) {
    val (backgroundColor, textColor, icon) = when (status) {
        VisitorStatus.PENDING -> Triple(Warning.copy(alpha = 0.1f), Warning, Icons.Default.Schedule)
        VisitorStatus.APPROVED -> Triple(Success.copy(alpha = 0.1f), Success, Icons.Default.CheckCircle)
        VisitorStatus.REJECTED -> Triple(Error.copy(alpha = 0.1f), Error, Icons.Default.Cancel)
        VisitorStatus.EXPIRED -> Triple(TextTertiary.copy(alpha = 0.1f), TextTertiary, Icons.Default.Timer)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clip(RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.name,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyState(
    searchQuery: String,
    selectedFilter: VisitorFilter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.PersonOff,
            contentDescription = "No visitors",
            tint = TextTertiary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (searchQuery.isNotEmpty()) {
                "No visitors found"
            } else {
                "No ${selectedFilter.displayName.lowercase()} visitors"
            },
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = if (searchQuery.isNotEmpty()) {
                "Try adjusting your search terms"
            } else {
                "Create a new visitor appointment to get started"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary
        )
    }
}

// Helper functions and data classes
private fun getStatusColor(status: VisitorStatus): Color {
    return when (status) {
        VisitorStatus.PENDING -> Warning
        VisitorStatus.APPROVED -> Success
        VisitorStatus.REJECTED -> Error
        VisitorStatus.EXPIRED -> TextTertiary
    }
}

private fun getVisitorCounts(visitors: List<Visitor>): Map<VisitorFilter, Int> {
    return mapOf(
        VisitorFilter.ALL to visitors.size,
        VisitorFilter.PENDING to visitors.count { it.status == VisitorStatus.PENDING },
        VisitorFilter.APPROVED to visitors.count { it.status == VisitorStatus.APPROVED },
        VisitorFilter.REJECTED to visitors.count { it.status == VisitorStatus.REJECTED },
        VisitorFilter.EXPIRED to visitors.count { it.status == VisitorStatus.EXPIRED }
    )
}

// Data classes and enums
data class Visitor(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val purpose: String,
    val date: String,
    val time: String,
    val status: VisitorStatus,
    val createdAt: String,
    // QR Code and Pass information
    val hasQRCode: Boolean = false,
    val qrCodeUrl: String? = null,
    val passId: String? = null,
    val expiryTime: String? = null
)

enum class VisitorStatus(val displayName: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    EXPIRED("Expired")
}

enum class VisitorFilter(val displayName: String) {
    ALL("All"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    EXPIRED("Expired")
}

// Dummy data function
private fun getDummyVisitors(): List<Visitor> {
    return listOf(
        Visitor(
            id = "1",
            name = "Alice Johnson",
            email = "alice.johnson@email.com",
            phone = "+1 (555) 123-4567",
            purpose = "Business Meeting",
            date = "Dec 15, 2024",
            time = "2:30 PM",
            status = VisitorStatus.PENDING,
            createdAt = "2024-12-14"
        ),
        Visitor(
            id = "2",
            name = "Bob Wilson",
            email = "bob.wilson@email.com",
            phone = "+1 (555) 234-5678",
            purpose = "Delivery",
            date = "Dec 14, 2024",
            time = "1:45 PM",
            status = VisitorStatus.APPROVED,
            createdAt = "2024-12-13"
        ),
        Visitor(
            id = "3",
            name = "Carol Davis",
            email = "carol.davis@email.com",
            phone = "+1 (555) 345-6789",
            purpose = "Maintenance Work",
            date = "Dec 16, 2024",
            time = "10:00 AM",
            status = VisitorStatus.PENDING,
            createdAt = "2024-12-14"
        ),
        Visitor(
            id = "4",
            name = "David Brown",
            email = "david.brown@email.com",
            phone = "+1 (555) 456-7890",
            purpose = "Guest Visit",
            date = "Dec 12, 2024",
            time = "11:30 AM",
            status = VisitorStatus.REJECTED,
            createdAt = "2024-12-11"
        ),
        Visitor(
            id = "5",
            name = "Emma Taylor",
            email = "emma.taylor@email.com",
            phone = "+1 (555) 567-8901",
            purpose = "Business Consultation",
            date = "Dec 10, 2024",
            time = "3:15 PM",
            status = VisitorStatus.EXPIRED,
            createdAt = "2024-12-09"
        ),
        Visitor(
            id = "6",
            name = "Frank Miller",
            email = "frank.miller@email.com",
            phone = "+1 (555) 678-9012",
            purpose = "Technical Support",
            date = "Dec 17, 2024",
            time = "9:00 AM",
            status = VisitorStatus.APPROVED,
            createdAt = "2024-12-14"
        ),
        Visitor(
            id = "7",
            name = "Grace Lee",
            email = "grace.lee@email.com",
            phone = "+1 (555) 789-0123",
            purpose = "Interview",
            date = "Dec 18, 2024",
            time = "2:00 PM",
            status = VisitorStatus.PENDING,
            createdAt = "2024-12-15"
        ),
        Visitor(
            id = "8",
            name = "Henry Clark",
            email = "henry.clark@email.com",
            phone = "+1 (555) 890-1234",
            purpose = "Package Pickup",
            date = "Dec 13, 2024",
            time = "4:30 PM",
            status = VisitorStatus.APPROVED,
            createdAt = "2024-12-12"
        )
    )
}
