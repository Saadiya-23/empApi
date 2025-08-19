    package config

    import java.sql.Connection
    import java.sql.DriverManager

    object Database {
        private const val URL = "jdbc:postgresql://localhost:5432/training"
        private const val USER = "postgres"
        private const val PASSWORD = "postgres"

        init { Class.forName("org.postgresql.Driver") }

        fun getConnection(): Connection {
            val connection = DriverManager.getConnection(URL, USER, PASSWORD)
            // Explicitly set timezone for every session
            connection.createStatement().use { stmt ->
                stmt.execute("SET TIME ZONE 'Asia/Kolkata'")
            }
            return connection
        }
    }

    //package config
    ////
    //import java.sql.Connection
    //import java.sql.DriverManager
    //
    //object Database {
    //    // Matches your docker-compose: localhost:5432, db=local_db, user=postgres, pass=postgres
    //    private const val URL = "jdbc:postgresql://localhost:5432/local_db?options=-c%20TimeZone=Asia/Kolkata"
    //
    //
    //    private const val USER = "postgres"
    //    private const val PASSWORD = "postgres"
    //
    //    init { Class.forName("org.postgresql.Driver") }
    //
    //    fun getConnection(): Connection =
    //        DriverManager.getConnection(URL, USER, PASSWORD)
    //}
