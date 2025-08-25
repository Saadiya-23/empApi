package DTO

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest @JsonCreator constructor(
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("password") val password: String
)