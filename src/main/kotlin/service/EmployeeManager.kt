package service

import DAO.AttendanceDAO
import DAO.EmployeeDAO
import model.Attendance
import model.Employee
import model.LoginRequest
import java.time.LocalDate
import java.time.LocalTime

class EmployeeManager(
    private val employeeDAO: EmployeeDAO = EmployeeDAO(),
    private val attendanceDAO: AttendanceDAO = AttendanceDAO()
) {
    var errors: String = ""

    // ---------------- Employees ----------------
    fun addEmployee(employee: Employee): Boolean {
        errors = ""
        if (employee.firstName.isBlank()) errors += "First Name cannot be blank. "
        if (employee.lastName.isBlank()) errors += "Last Name cannot be blank. "
        if (employee.password.isBlank()) errors += "Password cannot be blank. "
        if (errors.isNotBlank()) return false

        // Simple ID generator: PQ + 4 trailing millis digits (keep your style)
        if (employee.id.isNullOrBlank()) {
            employee.id = "PQ" + System.currentTimeMillis().toString().takeLast(4)
        }
        return employeeDAO.insert(employee)
    }

    fun deleteEmployee(empId: String): Boolean {
        // Attendance rows will be deleted by ON DELETE CASCADE if you set it.
        return employeeDAO.delete(empId)
    }

    fun getAllEmployees() = employeeDAO.findAll()

    fun getEmployee(empId: String) = employeeDAO.findById(empId)

    fun validateLogin(req: LoginRequest) =
        employeeDAO.validateLogin(req.firstName, req.password)

    // ---------------- Attendance ----------------
    private fun isFuture(date: LocalDate, time: LocalTime): Boolean {
        val today = LocalDate.now()
        val now = LocalTime.now()
        return date.isAfter(today) || (date == today && time.isAfter(now))
    }

    fun checkIn(a: Attendance): Boolean {
        errors = ""
        val empId = a.empId
        if (empId.isNullOrBlank()) errors += "Employee Id missing. "
        val date = a.checkInDate ?: LocalDate.now()
        val time = a.checkInTime ?: LocalTime.now()

        if (isFuture(date, time)) errors += "Future date/time not allowed. "
        if (empId != null && attendanceDAO.hasActiveCheckIn(empId, date))
            errors += "Check-in entry already exists for the day. "

        if (errors.isNotBlank()) return false

        a.checkInDate = date
        a.checkInTime = time
        return attendanceDAO.insertCheckIn(a)
    }

    fun checkOut(empId: String?, checkInDate: LocalDate?, checkOutTime: LocalTime?): Boolean {
        errors = ""
        val id = empId ?: run { errors = "Employee Id missing. "; return false }
        val date = checkInDate ?: LocalDate.now()
        val time = checkOutTime ?: LocalTime.now()
        if (isFuture(date, time)) { errors = "Future date/time not allowed."; return false }
        return attendanceDAO.updateCheckOut(id, date, time)
    }

    fun listAllAttendance() = attendanceDAO.listAll()

    fun listAttendanceByEmp(empId: String) = attendanceDAO.listByEmpId(empId)

    fun workingHoursSummary(from: LocalDate, to: LocalDate) =
        attendanceDAO.workingHoursSummary(from, to)
}
