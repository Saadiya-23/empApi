package model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

enum class Department {
    IT,
    ADMIN,
    CUSTOMER_SERVICE
}

enum class Role {
    DEV,
    QA,
    HR,
    MANAGER,
    SUPPORT
}

data class Employee @JsonCreator constructor(
    @JsonProperty("id", required = false)
    var id: UUID? = null,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("password")
    val password: String,
    @JsonProperty("role")
    val role: Role,
    @JsonProperty("dept")
    val dept: Department,
    @JsonProperty("reportingTo")
    val reportingTo: UUID?
)