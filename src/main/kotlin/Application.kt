
import resources.EmployeeAttendance
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.core.Application
import io.dropwizard.core.Configuration
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment

class AttendanceConfiguration : Configuration()

class AttendanceApplication : Application<AttendanceConfiguration>() {
    override fun initialize(bootstrap: Bootstrap<AttendanceConfiguration>) {
        bootstrap.addBundle(AssetsBundle("/assets/", "/app", "index.html"))
    }

    override fun run(configuration: AttendanceConfiguration, environment: Environment) {
        environment.jersey().register(EmployeeAttendance())
    }
}

fun main(args: Array<String>) {
    AttendanceApplication().run(*args)
}
