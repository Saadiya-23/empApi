package DTO

import java.util.UUID

data class WorkingHoursSummary(
    val empId: UUID,
    val total: String
)