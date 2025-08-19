package DAO

import config.Database
import model.Employee
import java.sql.ResultSet

class EmployeeDAO {

    fun insert(employee: Employee): Boolean {
        val sql = "INSERT INTO employees (id, first_name, last_name, password) VALUES (?, ?, ?, ?)"
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, employee.id)
                ps.setString(2, employee.firstName)
                ps.setString(3, employee.lastName)
                ps.setString(4, employee.password)
                return ps.executeUpdate() > 0
            }
        }
    }

    fun delete(empId: String): Boolean {
        val sql = "DELETE FROM employees WHERE id = ?"
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, empId)
                return ps.executeUpdate() > 0
            }
        }
    }

    fun findById(empId: String): Employee? {
        val sql = "SELECT id, first_name, last_name, password FROM employees WHERE id = ?"
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, empId)
                val rs = ps.executeQuery()
                return if (rs.next()) mapRow(rs) else null
            }
        }
    }

    fun findAll(): List<Employee> {
        val sql = "SELECT id, first_name, last_name, password FROM employees ORDER BY id"
        val list = mutableListOf<Employee>()
        Database.getConnection().use { conn ->
            conn.createStatement().use { st ->
                val rs = st.executeQuery(sql)
                while (rs.next()) list += mapRow(rs)
            }
        }
        return list
    }

    fun validateLogin(firstName: String, password: String): Boolean {
        val sql = "SELECT 1 FROM employees WHERE first_name = ? AND password = ? LIMIT 1"
        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, firstName)
                ps.setString(2, password)
                return ps.executeQuery().next()
            }
        }
    }

    private fun mapRow(rs: ResultSet) = Employee(
        id = rs.getString("id"),
        firstName = rs.getString("first_name"),
        lastName = rs.getString("last_name"),
        password = rs.getString("password")
    )
}
