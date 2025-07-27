package idv.xcplay.sqlitebrowser.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Paths
import java.sql.DriverManager

@RestController
@RequestMapping("/api/databases")
class DatabaseController {

    @Value("\${db.folder:etc/db}")
    private lateinit var dbFolder: String

    private val validNameRegex = Regex("^[a-zA-Z0-9_]+\\.sqlite\$")
    private val validTableRegex = Regex("^[a-zA-Z0-9_]+\$")

    @GetMapping
    fun listDatabases(): List<String> {
        val folder = File(dbFolder)
        if (!folder.exists() || !folder.isDirectory) return emptyList()

        return folder.listFiles { file ->
            file.isFile && file.name.endsWith(".sqlite") && validNameRegex.matches(file.name)
        }?.map { it.name } ?: emptyList()
    }

    @GetMapping("/{db}/tables")
    fun listTables(@PathVariable db: String): List<String> {
        validateDatabaseName(db)
        val dbPath = Paths.get(dbFolder, db).toAbsolutePath().toString()

        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { conn ->
            val tables = mutableListOf<String>()
            val rs = conn.metaData.getTables(null, null, "%", arrayOf("TABLE"))
            while (rs.next()) {
                val tableName = rs.getString("TABLE_NAME")
                if (validTableRegex.matches(tableName)) {
                    tables.add(tableName)
                }
            }
            return tables
        }
    }

    @GetMapping("/{db}/tables/{table}")
    fun getTableContent(
        @PathVariable db: String,
        @PathVariable table: String,
        @RequestParam(defaultValue = "50") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): List<Map<String, Any?>> {
        validateDatabaseName(db)
        validateTableName(table)

        val dbPath = Paths.get(dbFolder, db).toAbsolutePath().toString()
        val query = "SELECT * FROM \"$table\" LIMIT ? OFFSET ?"

        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { conn ->
            conn.prepareStatement(query).use { stmt ->
                stmt.setInt(1, limit)
                stmt.setInt(2, offset)
                val rs = stmt.executeQuery()
                val meta = rs.metaData
                val columnCount = meta.columnCount
                val results = mutableListOf<Map<String, Any?>>()

                while (rs.next()) {
                    val row = mutableMapOf<String, Any?>()
                    for (i in 1..columnCount) {
                        row[meta.getColumnName(i)] = rs.getObject(i)
                    }
                    results.add(row)
                }
                return results
            }
        }
    }

    @GetMapping("/{db}/tables/{table}/columns")
    fun getTableColumns(
        @PathVariable db: String,
        @PathVariable table: String
    ): List<Map<String, Any?>> {
        validateDatabaseName(db)
        validateTableName(table)

        val dbPath = Paths.get(dbFolder, db).toAbsolutePath().toString()

        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { conn ->
            val query = "PRAGMA table_info(\"$table\")"
            conn.prepareStatement(query).use { stmt ->
                val rs = stmt.executeQuery()
                val columns = mutableListOf<Map<String, Any?>>()
                while (rs.next()) {
                    columns.add(
                        mapOf(
                            "cid" to rs.getInt("cid"),
                            "name" to rs.getString("name"),
                            "type" to rs.getString("type"),
                            "notnull" to (rs.getInt("notnull") == 1),
                            "dflt_value" to rs.getString("dflt_value"),
                            "pk" to (rs.getInt("pk") == 1)
                        )
                    )
                }
                return columns
            }
        }
    }

    private fun validateDatabaseName(name: String) {
        if (!validNameRegex.matches(name)) {
            throw IllegalArgumentException("Invalid database name: $name")
        }
    }

    private fun validateTableName(name: String) {
        if (!validTableRegex.matches(name)) {
            throw IllegalArgumentException("Invalid table name: $name")
        }
    }
}
