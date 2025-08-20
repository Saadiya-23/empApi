package service

import DAO.AttendanceDAO
import DAO.EmployeeDAO
import model.Attendance
import model.Employee
import model.LoginRequest
import java.time.LocalDate
import java.time.LocalTime

class EmployeeManager(
    private val employeeDAO: EmployeeDAO,
    private val attendanceDAO: AttendanceDAO
) {
    var errors: String = ""
    companion object{
        var nextEmpId:Int=100
    }

    fun addEmployee(employee: Employee): Boolean {
        errors = ""
        if (employee.firstName.isBlank()) errors += "First Name cannot be blank. "
        if (employee.lastName.isBlank()) errors += "Last Name cannot be blank. "
        if (employee.password.isBlank()) errors += "Password cannot be blank. "
        if (errors.isNotBlank()) return false

        if (employee.id.isNullOrBlank()) {
            employee.id = "PQ${nextEmpId++}"
        }
        return employeeDAO.insert(employee)
    }

    fun deleteEmployee(empId: String): Boolean {
        return employeeDAO.delete(empId)
    }

    fun getAllEmployees() = employeeDAO.findAll()

    fun isEmployeeExist(empId:String) = employeeDAO.findById(empId) != null

    fun getEmployee(empId: String) = employeeDAO.findById(empId)

    fun validateLogin(req: LoginRequest) =
        employeeDAO.validateLogin(req.firstName, req.password)




    private fun isFuture(date: LocalDate, time: LocalTime): Boolean {
        val currDate = LocalDate.now()
        val currTime= LocalTime.now()
        return date.isAfter(currDate) || (date == currDate && time.isAfter(currTime))
    }

    fun checkIn(attendance: Attendance): Boolean {
        errors = ""
        val empId = attendance.empId
        if (empId.isNullOrBlank()) errors += "Employee Id missing. "
        val date = attendance.checkInDate ?: LocalDate.now()
        val time = attendance.checkInTime ?: LocalTime.now()

        if (empId != null && !isEmployeeExist(empId))
        errors += "Employee with ID $empId does not exist. "

        if (isFuture(date, time)) errors += "Future date/time not allowed. "
        if (empId != null && attendanceDAO.hasActiveCheckIn(empId, date))
            errors += "Check-in entry already exists for the day. "

        if (errors.isNotBlank()) return false

        attendance.checkInDate = date
        attendance.checkInTime = time
        return attendanceDAO.insertCheckIn(attendance)
    }

    fun checkOut(empId: String?, checkInDate: LocalDate?, checkOutTime: LocalTime?): Boolean {
        errors = ""
        val id = empId ?: run { errors = "Employee Id missing. "; return false }
        if (!isEmployeeExist(empId))
        errors += "Employee with ID $empId does not exist. "
        val date = checkInDate ?: LocalDate.now()
        if (!attendanceDAO.hasActiveCheckIn(id, date)) {
            errors += "No active check-in found for the day. "
        }
        val time = checkOutTime ?: LocalTime.now()
        if (isFuture(date, time)) { errors = "Future date/time not allowed."; return false }
        if (errors.isNotBlank()) return false
        return attendanceDAO.updateCheckOut(id, date, time)
    }

    fun listAllAttendance() = attendanceDAO.listAll()

    fun listAttendanceByEmp(empId: String) = attendanceDAO.listByEmpId(empId)

    fun workingHoursSummary(from: LocalDate, to: LocalDate): Map<String,String> {
        val summaryList = attendanceDAO.workingHoursSummary(from, to)
        return summaryList.associate { it.empId to it.total }
    }
}
