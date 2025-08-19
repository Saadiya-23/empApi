package model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Employee @JsonCreator constructor(
    @JsonProperty("id", required = false)
    var id: String? = null,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("password")
    val password: String
)
