package model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Attendance @JsonCreator constructor(
    @JsonProperty("empId")
    var empId: UUID? = null,

    @JsonProperty("checkInDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    var checkInDate: LocalDate? = null,

    @JsonProperty("checkInTime")
    @JsonFormat(pattern = "HH:mm:ss")
    var checkInTime: LocalTime? = null,

    @JsonProperty("checkOutTime")
    @JsonFormat(pattern = "HH:mm:ss")
    var checkOutTime: LocalTime? = null
)
