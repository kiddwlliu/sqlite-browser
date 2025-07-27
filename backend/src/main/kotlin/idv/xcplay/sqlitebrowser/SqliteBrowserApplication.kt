package idv.xcplay.sqlitebrowser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SqliteBrowserApplication

fun main(args: Array<String>) {
	runApplication<SqliteBrowserApplication>(*args)
}
