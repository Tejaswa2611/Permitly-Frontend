import com.example.permitely.data.models.GetVisitorByIdData
import com.example.permitely.ui.host.Visitor
import com.example.permitely.ui.host.VisitorStatus

/**
     * Convert API VisitorData to UI Visitor model
     */
    fun convertToUiVisitor(visitorData: GetVisitorByIdData): Visitor {
        val visitor = visitorData.visitor
        val latestPass = visitor.passes.firstOrNull() // Get newest pass (first in array)

        return Visitor(
            id = visitor.visitorId.toString(),
            name = visitor.name,
            email = visitor.email,
            phone = visitor.phoneNumber,
            purpose = visitor.purposeOfVisit,
            date = visitor.createdAt.split("T")[0], // Extract date part
            time = visitor.createdAt.split("T")[1].split("Z")[0], // Extract time part
            status = when(visitor.status) {
                "PENDING" -> VisitorStatus.PENDING
                "APPROVED" -> VisitorStatus.APPROVED
                "REJECTED" -> VisitorStatus.REJECTED
                "EXPIRED" -> VisitorStatus.EXPIRED
                else -> VisitorStatus.PENDING
            },
            createdAt = visitor.createdAt,
            // Pass information from latest pass
            hasQRCode = latestPass != null,
            qrCodeUrl = latestPass?.qrCodeData, // This is the actual QR code URL
            passId = latestPass?.passId?.toString(),
            expiryTime = latestPass?.expiryTime
        )
    }

