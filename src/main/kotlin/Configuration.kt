
import io.dropwizard.core.Configuration
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

class AttendanceConfiguration : Configuration() {
    @JsonProperty("database")
    @NotNull
    lateinit var databaseConfig: DatabaseConfiguration
}

class DatabaseConfiguration {
    @JsonProperty
    @NotNull
    lateinit var url: String

    @JsonProperty
    @NotNull
    lateinit var user: String

    @JsonProperty
    @NotNull
    lateinit var password: String
}