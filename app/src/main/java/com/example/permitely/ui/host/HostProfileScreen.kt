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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.permitely.ui.common.PermitelyTextField
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Host Profile Screen - View/edit host profile
 * Shows profile information, account statistics, settings, and logout option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostProfileScreen(
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Profile state
    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Form fields
    var name by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("john.doe@company.com") }
    var phone by remember { mutableStateOf("+1 (555) 123-4567") }

    // Save profile function
    fun saveProfile() {
        scope.launch {
            isSaving = true
            delay(1500) // Simulate API call
            isSaving = false
            isEditing = false
        }
    }

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
            ProfileTopBar(
                onNavigateBack = onNavigateBack,
                isEditing = isEditing,
                isSaving = isSaving,
                onEdit = { isEditing = true },
                onSave = { saveProfile() },
                onCancel = { isEditing = false }
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Photo and Basic Info
                ProfilePhotoCard(
                    name = name,
                    email = email,
                    isEditing = isEditing
                )

                // Editable Profile Information
                EditableProfileCard(
                    name = name,
                    email = email,
                    phone = phone,
                    isEditing = isEditing,
                    onNameChange = { name = it },
                    onEmailChange = { email = it },
                    onPhoneChange = { phone = it }
                )

                // Account Statistics
                AccountStatisticsCard()

                // Settings Options
                SettingsOptionsCard()

                // Logout Section
                LogoutCard(
                    onLogout = { showLogoutDialog = true }
                )
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(
    onNavigateBack: () -> Unit,
    isEditing: Boolean,
    isSaving: Boolean,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
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

            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Edit/Save/Cancel buttons
            if (isEditing) {
                TextButton(
                    onClick = onCancel,
                    enabled = !isSaving
                ) {
                    Text("Cancel", color = TextSecondary)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onSave,
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = OnPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving...")
                    } else {
                        Text("Save")
                    }
                }
            } else {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePhotoCard(
    name: String,
    email: String,
    isEditing: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo
            Box {
                Card(
                    modifier = Modifier.size(120.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Photo",
                            tint = Primary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // Edit photo button when editing
                if (isEditing) {
                    Card(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.BottomEnd),
                        colors = CardDefaults.cardColors(containerColor = Primary),
                        shape = CircleShape,
                        onClick = { /* TODO: Implement photo picker */ }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = OnPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name and email (read-only here)
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role badge
            Card(
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "HOST",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EditableProfileCard(
    name: String,
    email: String,
    phone: String,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
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
                text = "Personal Information",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            if (isEditing) {
                // Editable fields
                PermitelyTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "Full Name",
                    modifier = Modifier.fillMaxWidth()
                )

                PermitelyTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email Address",
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.fillMaxWidth()
                )

                PermitelyTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Read-only display
                ProfileInfoRow(
                    icon = Icons.Default.Person,
                    label = "Full Name",
                    value = name
                )

                ProfileInfoRow(
                    icon = Icons.Default.Email,
                    label = "Email Address",
                    value = email
                )

                ProfileInfoRow(
                    icon = Icons.Default.Phone,
                    label = "Phone Number",
                    value = phone
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
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
private fun AccountStatisticsCard() {
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
                text = "Account Statistics",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatisticItem(
                    icon = Icons.Default.Group,
                    label = "Total Visitors",
                    value = "127",
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    label = "Approved",
                    value = "98",
                    color = Success,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatisticItem(
                    icon = Icons.Default.Schedule,
                    label = "Pending",
                    value = "15",
                    color = Secondary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.QrCode,
                    label = "Active Passes",
                    value = "23",
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SettingsOptionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Manage notification preferences",
                onClick = { /* TODO: Navigate to notification settings */ }
            )

            SettingsItem(
                icon = Icons.Default.Security,
                title = "Security",
                subtitle = "Change password and security settings",
                onClick = { /* TODO: Navigate to security settings */ }
            )

            SettingsItem(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = "English (US)",
                onClick = { /* TODO: Navigate to language settings */ }
            )

            SettingsItem(
                icon = Icons.Default.HelpOutline,
                title = "Help & Support",
                subtitle = "Get help and contact support",
                onClick = { /* TODO: Navigate to help */ }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(24.dp)
            )

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
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LogoutCard(
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Error
            ),
            elevation = null
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = Error
            )
        },
        title = {
            Text(
                text = "Logout",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to logout? You'll need to sign in again to access your account.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Error)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
