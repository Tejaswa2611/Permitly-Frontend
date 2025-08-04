package com.example.permitely.ui.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.permitely.ui.common.PermitelyButton
import com.example.permitely.ui.common.PermitelyTextField
import com.example.permitely.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create Visitor Screen - Host creates new visitor appointment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVisitorScreen(
    onNavigateBack: () -> Unit = {},
    onVisitorCreated: () -> Unit = {}
) {
    var visitorName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var purposeOfVisit by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    // Validation states
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var purposeError by remember { mutableStateOf("") }
    var dateTimeError by remember { mutableStateOf("") }

    // UI states
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Date and Time Picker States
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    // Validation functions
    fun validateForm(): Boolean {
        var isValid = true

        // Clear previous errors
        nameError = ""
        emailError = ""
        phoneError = ""
        purposeError = ""
        dateTimeError = ""

        // Validate name
        if (visitorName.isBlank()) {
            nameError = "Visitor name is required"
            isValid = false
        }

        // Validate email
        if (email.isBlank()) {
            emailError = "Email address is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email address"
            isValid = false
        }

        // Validate phone
        if (phoneNumber.isBlank()) {
            phoneError = "Phone number is required"
            isValid = false
        } else if (phoneNumber.length < 10) {
            phoneError = "Please enter a valid phone number"
            isValid = false
        }

        // Validate purpose
        if (purposeOfVisit.isBlank()) {
            purposeError = "Purpose of visit is required"
            isValid = false
        }

        // Validate date and time
        if (selectedDate.isBlank() || selectedTime.isBlank()) {
            dateTimeError = "Please select visit date and time"
            isValid = false
        }

        return isValid
    }

    // Submit function
    fun submitVisitor() {
        if (validateForm()) {
            scope.launch {
                isLoading = true
                // Simulate API call
                delay(2000)
                isLoading = false
                showSuccessDialog = true
            }
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
            CreateVisitorTopBar(onNavigateBack = onNavigateBack)

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Section
                CreateVisitorHeader()

                // Form Fields
                CreateVisitorForm(
                    visitorName = visitorName,
                    onNameChange = {
                        visitorName = it
                        nameError = ""
                    },
                    nameError = nameError,

                    email = email,
                    onEmailChange = {
                        email = it
                        emailError = ""
                    },
                    emailError = emailError,

                    phoneNumber = phoneNumber,
                    onPhoneChange = {
                        phoneNumber = it
                        phoneError = ""
                    },
                    phoneError = phoneError,

                    purposeOfVisit = purposeOfVisit,
                    onPurposeChange = {
                        purposeOfVisit = it
                        purposeError = ""
                    },
                    purposeError = purposeError,

                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    onDateClick = { showDatePicker = true },
                    onTimeClick = { showTimePicker = true },
                    dateTimeError = dateTimeError
                )

                // Submit Button
                PermitelyButton(
                    text = "Create Visitor Appointment",
                    onClick = { submitVisitor() },
                    loading = isLoading,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Bottom spacing
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            selectedDate = formatter.format(Date(millis))
                            dateTimeError = ""
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        val formattedTime = String.format("%02d:%02d", hour, minute)
                        selectedTime = formattedTime
                        dateTimeError = ""
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                onVisitorCreated()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateVisitorTopBar(onNavigateBack: () -> Unit) {
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
                text = "Create New Visitor",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CreateVisitorHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Primary, Secondary)
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Create Visitor",
                    tint = OnPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "New Visitor Appointment",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fill in the details to create a visitor entry",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun CreateVisitorForm(
    visitorName: String,
    onNameChange: (String) -> Unit,
    nameError: String,

    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String,

    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String,

    purposeOfVisit: String,
    onPurposeChange: (String) -> Unit,
    purposeError: String,

    selectedDate: String,
    selectedTime: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    dateTimeError: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visitor Name
        PermitelyTextField(
            value = visitorName,
            onValueChange = onNameChange,
            label = "Visitor Name *",
            keyboardType = KeyboardType.Text,
            isError = nameError.isNotEmpty(),
            errorMessage = nameError
        )

        // Email Address
        PermitelyTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email Address *",
            keyboardType = KeyboardType.Email,
            isError = emailError.isNotEmpty(),
            errorMessage = emailError
        )

        // Phone Number
        PermitelyTextField(
            value = phoneNumber,
            onValueChange = onPhoneChange,
            label = "Phone Number *",
            keyboardType = KeyboardType.Phone,
            isError = phoneError.isNotEmpty(),
            errorMessage = phoneError
        )

        // Purpose of Visit
        PermitelyTextField(
            value = purposeOfVisit,
            onValueChange = onPurposeChange,
            label = "Purpose of Visit *",
            keyboardType = KeyboardType.Text,
            isError = purposeError.isNotEmpty(),
            errorMessage = purposeError
        )

        // Date and Time Section
        Text(
            text = "Visit Schedule",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date Picker
            DateTimeCard(
                label = "Date",
                value = selectedDate.ifEmpty { "Select Date" },
                icon = Icons.Default.DateRange,
                onClick = onDateClick,
                modifier = Modifier.weight(1f),
                isError = dateTimeError.isNotEmpty() && selectedDate.isEmpty()
            )

            // Time Picker
            DateTimeCard(
                label = "Time",
                value = selectedTime.ifEmpty { "Select Time" },
                icon = Icons.Default.Schedule,
                onClick = onTimeClick,
                modifier = Modifier.weight(1f),
                isError = dateTimeError.isNotEmpty() && selectedTime.isEmpty()
            )
        }

        // Date/Time Error
        if (dateTimeError.isNotEmpty()) {
            Text(
                text = dateTimeError,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun DateTimeCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Error.copy(alpha = 0.05f) else Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        border = if (isError) androidx.compose.foundation.BorderStroke(1.dp, Error) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isError) Error else Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.contains("Select")) TextTertiary else TextPrimary,
                fontWeight = if (value.contains("Select")) FontWeight.Normal else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { content() },
        containerColor = Surface,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary
    )
}

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Success!",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = "Visitor appointment has been created successfully. The visitor will receive a confirmation email with visit details.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            PermitelyButton(
                text = "Done",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}
