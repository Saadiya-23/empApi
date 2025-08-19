package DAO

import config.Database
import model.Attendance
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalTime

class AttendanceDAO {

    fun insertCheckIn(a: Attendance): Boolean {
        val sql = """
            INSERT INTO attendances (emp_id, check_in_date, check_in_time)
            VALUES (?, ?, ?)
        """.trimIndent()
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, a.empId!!)
                ps.setObject(2, a.checkInDate)
                ps.setObject(3, a.checkInTime)
                return ps.executeUpdate() > 0
            }
        }
    }

    fun updateCheckOut(empId: String, date: LocalDate, time: LocalTime): Boolean {
        // working_hours = check_out_time - check_in_time (computed by PG)
        val sql = """
            UPDATE attendances
               SET check_out_time = ?,
                   working_hours  = (?::time - check_in_time)
             WHERE emp_id = ?
               AND check_in_date = ?
               AND check_out_time IS NULL
        """.trimIndent()
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, time)
                ps.setObject(2, time)
                ps.setString(3, empId)
                ps.setObject(4, date)
                return ps.executeUpdate() > 0
            }
        }
    }

    fun hasActiveCheckIn(empId: String, date: LocalDate): Boolean {
        val sql = """
            SELECT 1 FROM attendances
             WHERE emp_id = ? AND check_in_date = ? AND check_out_time IS NULL
             LIMIT 1
        """.trimIndent()
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, empId)
                ps.setObject(2, date)
                return ps.executeQuery().next()
            }
        }
    }

    fun listAll(): List<Attendance> {
        val sql = """
            SELECT emp_id, check_in_date, check_in_time, check_out_time
              FROM attendances
             ORDER BY emp_id, check_in_date, check_in_time
        """.trimIndent()
        val out = mutableListOf<Attendance>()
        Database.getConnection().use { conn ->
            conn.createStatement().use { st ->
                val rs = st.executeQuery(sql)
                while (rs.next()) out += mapRow(rs)
            }
        }
        return out
    }

    fun listByEmpId(empId: String): List<Attendance> {
        val sql = """
            SELECT emp_id, check_in_date, check_in_time, check_out_time
              FROM attendances
             WHERE emp_id = ?
             ORDER BY check_in_date, check_in_time
        """.trimIndent()
        val out = mutableListOf<Attendance>()
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, empId)
                val rs = ps.executeQuery()
                while (rs.next()) out += mapRow(rs)
            }
        }
        return out
    }

    fun deleteByEmpId(empId: String): Boolean {
        val sql = "DELETE FROM attendances WHERE emp_id = ?"
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, empId)
                ps.executeUpdate()
                return true
            }
        }
    }

    fun workingHoursSummary(from: LocalDate, to: LocalDate): Map<String, String> {
        // Returns total per-employee as text like "HH hours MM mins"
        val sql = """
            SELECT emp_id,
                   COALESCE(SUM(working_hours), INTERVAL '0') AS total
              FROM attendances
             WHERE check_in_date BETWEEN ? AND ?
               AND working_hours IS NOT NULL
             GROUP BY emp_id
             ORDER BY emp_id
        """.trimIndent()

        val out = linkedMapOf<String, String>()
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setObject(1, from)
                ps.setObject(2, to)
                val rs = ps.executeQuery()
                while (rs.next()) {
                    val interval = rs.getString("total") // e.g., 08:30:00
                    out[rs.getString("emp_id")] = interval
                }
            }
        }
        return out
    }

    private fun mapRow(rs: ResultSet) = Attendance(
        empId = rs.getString("emp_id"),
        checkInDate = rs.getObject("check_in_date", LocalDate::class.java),
        checkInTime = rs.getObject("check_in_time", LocalTime::class.java),
        checkOutTime = rs.getObject("check_out_time", LocalTime::class.java)
    )
}
