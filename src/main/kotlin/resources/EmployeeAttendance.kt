package resources

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import model.*
import service.EmployeeManager
import java.time.LocalDate
import java.time.LocalTime

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class EmployeeAttendance(
    private val manager: EmployeeManager = EmployeeManager()
) {
    // -------- Login ----------
    @POST
    @Path("/login")
    fun login(req: LoginRequest): Response =
        if (manager.validateLogin(req))
            Response.ok(mapOf("message" to "Login successful")).build()
        else
            Response.status(Status.UNAUTHORIZED).entity(mapOf("error" to "Invalid Credentials")).build()

    // -------- Employees ----------
    @GET
    @Path("/employees")
    fun getAllEmployees(): List<Employee> = manager.getAllEmployees()

    @GET
    @Path("/employees/{empId}")
    fun getEmployee(@PathParam("empId") empId: String): Response {
        val e = manager.getEmployee(empId)
        return if (e != null) Response.ok(e).build()
        else Response.status(Status.NOT_FOUND)
            .entity(mapOf("error" to "Employee with ID $empId not found")).build()
    }

    @POST
    @Path("/employees")
    fun addEmployee(employee: Employee): Response {
        return if (manager.addEmployee(employee)) {
            Response.status(Status.CREATED)
                .entity(mapOf("message" to "Employee added", "id" to employee.id)).build()
        } else {
            Response.status(Status.BAD_REQUEST)
                .entity(mapOf("error" to manager.errors)).build()
        }
    }

    // -------- Attendance ----------
    @GET
    @Path("/attendance")
    fun listAllAttendance(): List<Attendance> = manager.listAllAttendance()

    @GET
    @Path("/attendance/{empId}")
    fun getAttendance(@PathParam("empId") empId: String): List<Attendance> =
        manager.listAttendanceByEmp(empId)

    @GET
    @Path("/attendance/summary")
    fun getSummary(
        @QueryParam("fromDate") fromDateStr: String,
        @QueryParam("toDate") toDateStr: String
    ): Response {
        val from = LocalDate.parse(fromDateStr)
        val to = LocalDate.parse(toDateStr)
        val summary = manager.workingHoursSummary(from, to)
        return Response.ok(summary).build()
    }

    @POST
    @Path("/attendance/checkIn")
    fun checkIn(a: Attendance): Response {
        if (a.checkOutTime != null)
            return Response.status(Status.BAD_REQUEST)
                .entity(mapOf("error" to "Cannot checkOut while checkIn")).build()

        return if (manager.checkIn(a))
            Response.status(Status.CREATED).entity(mapOf("message" to "checkedIn successfully")).build()
        else
            Response.status(Status.BAD_REQUEST).entity(mapOf("error" to manager.errors)).build()
    }

    @PUT
    @Path("/attendance/checkOut")
    fun checkOut(a: Attendance): Response {
        val ok = manager.checkOut(a.empId, a.checkInDate, a.checkOutTime ?: LocalTime.now())
        return if (ok)
            Response.status(Status.CREATED).entity(mapOf("message" to "checkedOut successfully")).build()
        else
            Response.status(Status.BAD_REQUEST).entity(mapOf("error" to manager.errors)).build()
    }
}
