

import io.dropwizard.assets.AssetsBundle
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import resources.EmployeeAttendance
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import DAO.AttendanceDAO
import DAO.EmployeeDAO
import service.EmployeeManager


class AttendanceApplication : Application<AttendanceConfiguration>() {
    override fun initialize(bootstrap: Bootstrap<AttendanceConfiguration>) {
        bootstrap.addBundle(AssetsBundle("/assets/", "/app", "index.html"))
    }

    override fun run(configuration: AttendanceConfiguration, environment: Environment) {
        // Step 1: Initialize Jdbi instance from the configuration file
        val jdbi = Jdbi.create(
            configuration.databaseConfig.url,
            configuration.databaseConfig.user,
            configuration.databaseConfig.password
        )
            .installPlugin(KotlinPlugin())
            .installPlugin(KotlinSqlObjectPlugin())
            .installPlugin(PostgresPlugin())

        // Optional: Set timezone for database sessions
        jdbi.open().use { handle ->
            handle.execute("SET TIME ZONE 'Asia/Kolkata'")
        }

        // Step 2: Create DAO instances using JDBI's onDemand factory
        val employeeDAO = jdbi.onDemand(EmployeeDAO::class.java)
        val attendanceDAO = jdbi.onDemand(AttendanceDAO::class.java)

        // Step 3: Instantiate the service with the JDBI DAOs
        val employeeManager = EmployeeManager(employeeDAO, attendanceDAO)

        // Step 4: Register the resource, passing the service instance
        environment.jersey().register(EmployeeAttendance(employeeManager))
    }
}

fun main(args: Array<String>) {
    AttendanceApplication().run(*args)
}