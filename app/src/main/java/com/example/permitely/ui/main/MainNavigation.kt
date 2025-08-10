package com.example.permitely.ui.main

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.permitely.data.storage.TokenStorage
import com.example.permitely.ui.host.*
import com.example.permitely.ui.guard.GuardDashboardScreen

/**
 * Main navigation controller for the authenticated part of the app
 * Handles routing between all main app screens with proper back navigation
 */
@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    userInfo: TokenStorage.UserInfo?,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        // Dashboard screen
        composable("dashboard") {
            when (userInfo?.role?.lowercase()) {
                "host", "", null -> {
                    HostDashboardScreen(
                        onCreateVisitor = {
                            navController.navigate("create_visitor")
                        },
                        onViewAllVisitors = {
                            navController.navigate("visitors_list")
                        },
                        onViewNotifications = {
                            navController.navigate("notifications")
                        },
                        onViewProfile = {
                            navController.navigate("profile")
                        },
                        onLogout = onLogout
                    )
                }
                "admin" -> {
                    // TODO: Implement admin dashboard when available
                    // For now, show host dashboard
                    HostDashboardScreen(
                        onCreateVisitor = {
                            navController.navigate("create_visitor")
                        },
                        onViewAllVisitors = {
                            navController.navigate("visitors_list")
                        },
                        onViewNotifications = {
                            navController.navigate("notifications")
                        },
                        onViewProfile = {
                            navController.navigate("profile")
                        },
                        onLogout = onLogout
                    )
                }
                "guard" -> {
                    GuardDashboardScreen(
                        onScanPass = {
                            navController.navigate("scan_pass")
                        },
                        onCreateVisitor = {
                            navController.navigate("create_visitor")
                        },
                        onViewNotifications = {
                            navController.navigate("notifications")
                        },
                        onViewProfile = {
                            navController.navigate("profile")
                        }
                    )
                }
            }
        }

        // Create visitor screen
        composable("create_visitor") {
            CreateVisitorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVisitorCreated = { visitorData ->
                    try {
                        // Navigate to visitor details using the visitor ID from the created visitor
                        val visitorId = visitorData.visitor.visitorId.toString()
                        navController.navigate("visitor_details/$visitorId")
                    } catch (e: Exception) {
                        println("Error processing visitor data: ${e.message}")
                        e.printStackTrace()
                        // On error, just go back to dashboard
                        navController.popBackStack()
                    }
                }
            )
        }

        // Visitors list screen
        composable("visitors_list") {
            VisitorsListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVisitorClick = { visitor ->
                    // Pass only the visitor ID, not the entire visitor data
                    navController.navigate("visitor_details/${visitor.id}")
                }
            )
        }

        // Visitor details screen - now takes visitor ID as parameter
        composable("visitor_details/{visitorId}") { backStackEntry ->
            val visitorId = backStackEntry.arguments?.getString("visitorId")
            visitorId?.let { id ->
                VisitorDetailsScreen(
                    visitorId = id,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onEditVisitor = { visitorToEdit ->
                        // TODO: Navigate to edit screen when implemented
                        // navController.navigate("edit_visitor/${visitorToEdit.id}")
                    },
                    onDeleteVisitor = { visitorId ->
                        // TODO: Implement visitor deletion logic
                        navController.popBackStack()
                    },
                    onGeneratePass = { visitorId ->
                        // TODO: Implement pass generation logic
                    },
                    onShareQRCode = {
                        // TODO: Implement QR code sharing logic
                    }
                )
            }
        }

        // Notifications screen
        composable("notifications") {
            NotificationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Profile screen
        composable("profile") {
            HostProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = onLogout
            )
        }

        // Scan Pass screen (Guard only)
        composable("scan_pass") {
            com.example.permitely.ui.guard.ScanPassScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEnterManually = {
                    navController.navigate("manual_pass_entry")
                },
                onPassScanned = { passId ->
                    // TODO: Navigate to pass verification result screen
                    // For now, just go back to dashboard
                    navController.popBackStack()
                }
            )
        }

        // Manual Pass Entry screen (Guard only)
        composable("manual_pass_entry") {
            // TODO: Implement manual pass entry screen
            // For now, just go back
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
        }
    }
}

// Helper functions to serialize/deserialize visitor data for navigation
private fun createVisitorJson(visitorData: com.example.permitely.data.models.CreateVisitorResponseData): String {
    // Create a simplified JSON string for navigation
    val visitor = visitorData.visitor
    val latestPass = visitor.passes.firstOrNull() // Get newest pass from passes array

    return buildString {
        append("{")
        append("\"id\":\"${visitor.visitorId}\",")
        append("\"name\":\"${visitor.name}\",")
        append("\"email\":\"${visitor.email}\",")
        append("\"phone\":\"${visitor.phoneNumber}\",")
        append("\"purpose\":\"${visitor.purposeOfVisit}\",")
        append("\"date\":\"${visitor.createdAt.split("T")[0]}\",")
        append("\"time\":\"${visitor.createdAt.split("T")[1].split("Z")[0]}\",")
        append("\"status\":\"${visitor.status}\",")
        append("\"createdAt\":\"${visitor.createdAt}\",")
        append("\"hasQRCode\":${latestPass != null},")
        append("\"qrCodeUrl\":\"${latestPass?.qrCodeData ?: ""}\",")
        append("\"passId\":\"${latestPass?.passId ?: ""}\",")
        append("\"expiryTime\":\"${latestPass?.expiryTime ?: ""}\"")
        append("}")
    }
}

private fun createVisitorJsonFromVisitor(visitor: com.example.permitely.ui.host.Visitor): String {
    return buildString {
        append("{")
        append("\"id\":\"${visitor.id}\",")
        append("\"name\":\"${visitor.name}\",")
        append("\"email\":\"${visitor.email}\",")
        append("\"phone\":\"${visitor.phone}\",")
        append("\"purpose\":\"${visitor.purpose}\",")
        append("\"date\":\"${visitor.date}\",")
        append("\"time\":\"${visitor.time}\",")
        append("\"status\":\"${visitor.status}\",")
        append("\"createdAt\":\"${visitor.createdAt}\",")
        append("\"hasQRCode\":${visitor.hasQRCode},")
        append("\"qrCodeUrl\":\"${visitor.qrCodeUrl ?: ""}\",")
        append("\"passId\":\"${visitor.passId ?: ""}\",")
        append("\"expiryTime\":\"${visitor.expiryTime ?: ""}\"")
        append("}")
    }
}

private fun parseVisitorFromJson(json: String): com.example.permitely.ui.host.Visitor? {
    return try {
        // Simple JSON parsing - in production, consider using Gson
        val cleanJson = json.removeSurrounding("{", "}")
        val pairs = cleanJson.split(",").associate { pair ->
            val (key, value) = pair.split(":", limit = 2)
            key.trim('"') to value.trim('"')
        }

        com.example.permitely.ui.host.Visitor(
            id = pairs["id"] ?: "",
            name = pairs["name"] ?: "",
            email = pairs["email"] ?: "",
            phone = pairs["phone"] ?: "",
            purpose = pairs["purpose"] ?: "",
            date = pairs["date"] ?: "",
            time = pairs["time"] ?: "",
            status = when(pairs["status"]) {
                "PENDING" -> com.example.permitely.ui.host.VisitorStatus.PENDING
                "APPROVED" -> com.example.permitely.ui.host.VisitorStatus.APPROVED
                "REJECTED" -> com.example.permitely.ui.host.VisitorStatus.REJECTED
                "EXPIRED" -> com.example.permitely.ui.host.VisitorStatus.EXPIRED
                else -> com.example.permitely.ui.host.VisitorStatus.PENDING
            },
            createdAt = pairs["createdAt"] ?: "",
            hasQRCode = pairs["hasQRCode"]?.toBoolean() ?: false,
            qrCodeUrl = pairs["qrCodeUrl"]?.takeIf { it.isNotEmpty() },
            passId = pairs["passId"]?.takeIf { it.isNotEmpty() },
            expiryTime = pairs["expiryTime"]?.takeIf { it.isNotEmpty() }
        )
    } catch (e: Exception) {
        println("Error parsing visitor JSON: ${e.message}")
        null
    }
}
