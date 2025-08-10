package com.example.permitely.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.permitely.ui.theme.*

/**
 * Modern App Bar Component for Permitely
 * Follows Material Design 3 guidelines with smooth animations and theme integration
 */

/**
 * Large App Bar - Used for main screens like Dashboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitelyLargeAppBar(
    title: String,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Surface,
            scrolledContainerColor = Surface.copy(alpha = 0.95f),
            titleContentColor = TextPrimary,
            actionIconContentColor = Primary
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier.shadow(
            elevation = 4.dp,
            spotColor = Primary.copy(alpha = 0.1f),
            ambientColor = Primary.copy(alpha = 0.05f)
        )
    )
}

/**
 * Standard App Bar - Used for secondary screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitelyAppBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    backgroundColor: Color = Surface,
    contentColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            onNavigationClick?.let { onClick ->
                IconButton(
                    onClick = onClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigate back",
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            navigationIconContentColor = Primary,
            actionIconContentColor = Primary
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier.shadow(
            elevation = 2.dp,
            spotColor = Primary.copy(alpha = 0.1f),
            ambientColor = Primary.copy(alpha = 0.05f)
        )
    )
}

/**
 * Gradient App Bar - For special screens or enhanced visual appeal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitelyGradientAppBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Primary, Secondary)
                )
            )
            .shadow(
                elevation = 8.dp,
                spotColor = Primary.copy(alpha = 0.2f),
                ambientColor = Primary.copy(alpha = 0.1f)
            ),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Primary, Secondary)
                    )
                )
                .statusBarsPadding()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    onNavigationClick?.let { onClick ->
                        IconButton(
                            onClick = onClick,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = navigationIcon,
                                contentDescription = "Navigate back",
                                tint = OnPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = OnPrimary,
                    navigationIconContentColor = OnPrimary,
                    actionIconContentColor = OnPrimary
                )
            )
        }
    }
}

/**
 * App Bar with Search - For screens that need search functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitelySearchAppBar(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    isSearchActive: Boolean = false,
    onNavigationClick: (() -> Unit)? = null,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isSearchActive,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        PermitelyAppBar(
            title = title,
            onNavigationClick = onNavigationClick,
            actions = {
                IconButton(
                    onClick = { onSearchActiveChange(true) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Primary
                    )
                }
            },
            modifier = modifier
        )
    }

    AnimatedVisibility(
        visible = isSearchActive,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = { /* Handle search submission */ },
            active = isSearchActive,
            onActiveChange = onSearchActiveChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSecondary
                )
            },
            leadingIcon = {
                IconButton(
                    onClick = { onSearchActiveChange(false) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close search",
                        tint = Primary
                    )
                }
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = TextSecondary
                        )
                    }
                }
            },
            colors = SearchBarDefaults.colors(
                containerColor = Surface,
                dividerColor = BorderLight
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Search results content can be added here
        }
    }
}

/**
 * App Bar Action Button - Consistent styling for action buttons
 */
@Composable
fun AppBarActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    badge: String? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) Primary else TextDisabled,
                modifier = Modifier.size(24.dp)
            )
        }

        // Badge for notifications, etc.
        if (badge != null) {
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp),
                containerColor = Warning,
                contentColor = OnWarning
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * App Bar with Profile - For dashboard and main screens
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitelyProfileAppBar(
    title: String,
    subtitle: String? = null,
    userName: String,
    userRole: String,
    onProfileClick: () -> Unit,
    onNotificationClick: (() -> Unit)? = null,
    notificationCount: Int = 0,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            // Notification button
            onNotificationClick?.let { onClick ->
                AppBarActionButton(
                    icon = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    onClick = onClick,
                    badge = if (notificationCount > 0) notificationCount.toString() else null
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Profile section
            TextButton(
                onClick = onProfileClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextPrimary
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = userRole.capitalize(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Primary, Secondary)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = OnPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Surface,
            scrolledContainerColor = Surface.copy(alpha = 0.95f),
            titleContentColor = TextPrimary,
            actionIconContentColor = Primary
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier.shadow(
            elevation = 4.dp,
            spotColor = Primary.copy(alpha = 0.1f),
            ambientColor = Primary.copy(alpha = 0.05f)
        )
    )
}
