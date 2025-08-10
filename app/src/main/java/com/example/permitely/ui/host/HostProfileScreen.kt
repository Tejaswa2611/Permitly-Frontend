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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.permitely.ui.common.PermitelyTextField
import com.example.permitely.ui.common.PermitelyAppBar
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
    onLogout: () -> Unit = {},
    viewModel: HostProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Profile state
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Handle error display
    if (uiState.error != null) {
        LaunchedEffect(uiState.error) {
            // Auto-clear error after 3 seconds
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            PermitelyAppBar(
                title = "My Profile",
                onNavigationClick = onNavigateBack,
                actions = {
                    if (uiState.isEditing) {
                        TextButton(
                            onClick = { viewModel.cancelEditing() },
                            enabled = !uiState.isSaving
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = { viewModel.saveProfile() },
                            enabled = !uiState.isSaving,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            if (uiState.isSaving) {
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
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Primary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error display
                if (uiState.error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
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

                // Loading indicator
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
                                text = "Loading profile...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Profile Photo and Basic Info
                ProfilePhotoCard(
                    name = uiState.name,
                    email = uiState.email,
                    role = uiState.role,
                    visitorsCount = uiState.visitorsCount,
                    passesCount = uiState.passesCount,
                    isEditing = uiState.isEditing
                )

                // Editable Profile Information
                EditableProfileCard(
                    name = uiState.name,
                    email = uiState.email,
                    phone = uiState.phoneNumber,
                    createdAt = uiState.createdAt,
                    isEditing = uiState.isEditing,
                    onNameChange = { viewModel.updateName(it) },
                    onEmailChange = { viewModel.updateEmail(it) },
                    onPhoneChange = { viewModel.updatePhoneNumber(it) }
                )

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
    role: String,
    visitorsCount: Int,
    passesCount: Int,
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
                    text = role.uppercase(),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visitors and Passes count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CountCard(
                    label = "Visitors",
                    count = visitorsCount,
                    isEditing = isEditing,
                    modifier = Modifier.weight(1f)
                )

                CountCard(
                    label = "Passes",
                    count = passesCount,
                    isEditing = isEditing,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CountCard(
    label: String,
    count: Int,
    isEditing: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            // Editable field for count when editing
            if (isEditing) {
                Spacer(modifier = Modifier.height(8.dp))

                PermitelyTextField(
                    value = count.toString(),
                    onValueChange = { /* TODO: Handle count change */ },
                    label = "Edit $label",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
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
    createdAt: String,
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

                ProfileInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Member Since",
                    value = createdAt
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
                text = "Are you sure you want to logout? You\'ll need to sign in again to access your account.",
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
