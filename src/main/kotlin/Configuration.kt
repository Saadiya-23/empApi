
import io.dropwizard.core.Configuration
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

class AttendanceConfiguration : Configuration() {
    @JsonProperty("database")
    @NotNull // <-- Changed here
    lateinit var databaseConfig: DatabaseConfiguration
}

class DatabaseConfiguration {
    @JsonProperty
    @NotNull // <-- Changed here
    lateinit var url: String

    @JsonProperty
    @NotNull // <-- Changed here
    lateinit var user: String

    @JsonProperty
    @NotNull // <-- Changed here
    lateinit var password: String
}