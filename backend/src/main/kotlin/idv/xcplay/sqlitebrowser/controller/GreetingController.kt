package idv.xcplay.sqlitebrowser.controller

import idv.xcplay.sqlitebrowser.util.AppManifest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest")
public class GreetingController {

	@GetMapping("/version")
	fun version(request: HttpServletRequest): AppVersion {

		val appManifest = AppManifest.instance;
		return AppVersion(appManifest.baseName, appManifest.version)
	}
}

data class AppVersion(
    val serverName: String,
    val serverVersion: String) {
}