package DAO

import model.Attendance
import model.WorkingHoursSummary
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.LocalDate
import java.time.LocalTime

interface AttendanceDAO {

    @SqlUpdate("""
        INSERT INTO attendances (emp_id, check_in_date, check_in_time)
        VALUES (:empId, :checkInDate, :checkInTime)
    """)
    fun insertCheckIn(@BindBean attendance: Attendance): Boolean

    @SqlUpdate("""
        UPDATE attendances
        SET check_out_time = :time,
            working_hours = (:time::time - check_in_time)
        WHERE emp_id = :empId
        AND check_in_date = :date
        AND check_out_time IS NULL
    """)
    fun updateCheckOut(
        @Bind("empId") empId: String,
        @Bind("date") date: LocalDate,
        @Bind("time") time: LocalTime
    ): Boolean

    @SqlQuery("SELECT count(*) FROM attendances WHERE emp_id = :empId AND check_in_date = :date AND check_out_time IS NULL")
    fun hasActiveCheckIn(@Bind("empId") empId: String, @Bind("date") date: LocalDate): Boolean

    @SqlQuery("SELECT emp_id, check_in_date, check_in_time, check_out_time FROM attendances ORDER BY emp_id, check_in_date, check_in_time")
    fun listAll(): List<Attendance>

    @SqlQuery("SELECT emp_id, check_in_date, check_in_time, check_out_time FROM attendances WHERE emp_id = :empId ORDER BY check_in_date, check_in_time")
    fun listByEmpId(@Bind("empId") empId: String): List<Attendance>

    @SqlUpdate("DELETE FROM attendances WHERE emp_id = :empId")
    fun deleteByEmpId(@Bind("empId") empId: String): Boolean

    @SqlQuery("""
        SELECT emp_id, COALESCE(SUM(working_hours), INTERVAL '0') AS total
        FROM attendances
        WHERE check_in_date BETWEEN :from AND :to
        AND working_hours IS NOT NULL
        GROUP BY emp_id
        ORDER BY emp_id
    """)
    fun workingHoursSummary(@Bind("from") from: LocalDate, @Bind("to") to: LocalDate): List<WorkingHoursSummary>
}