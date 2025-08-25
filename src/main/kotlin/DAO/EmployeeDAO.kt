package DAO

import model.Employee
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.UUID

interface EmployeeDAO {

    @SqlUpdate("""
        INSERT INTO employees (id, first_name, last_name, password, role, dept, reporting_to)
        VALUES (:id, :firstName, :lastName, :password, :role, :dept, :reportingTo)
    """)
    fun insert(@BindBean employee: Employee): Boolean


    @SqlUpdate("DELETE FROM employees WHERE id = :empId")
    fun delete(@Bind("empId") empId: UUID): Boolean

    @SqlQuery("SELECT id, first_name, last_name, password , role , dept , reporting_to FROM employees WHERE id = :empId")
    fun findById(@Bind("empId") empId: UUID): Employee?

    @SqlQuery("SELECT id, first_name, last_name, password , role , dept , reporting_to FROM employees ORDER BY id")
    fun findAll(): List<Employee>


    @SqlQuery("SELECT count(*) FROM employees WHERE first_name = :firstName AND password = :password LIMIT 1")
    fun validateLogin(@Bind("firstName") firstName: String, @Bind("password") password: String): Boolean
}