package com.xac.web.gateway.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity

import jakarta.servlet.http.HttpServletRequest

// import com.xac.web.gateway.status.RetStatus
// import com.xac.web.gateway.server.config.AppProperties
import java.util.logging.Logger


// https://www.youtube.com/watch?app=desktop&v=_CLLw3QAuOE
@Controller
// @RequestMapping("/spa")
class SpaForwardController {

    companion object {
        //val log = Logger.getLogger(this::class.java.simpleName)
        val log = Logger.getLogger("route.entry")
    }


    // @GetMapping("/spa/**/{path:[^\\.]*}")
    // Invalid mapping pattern detected:
    // /spa/**/{path:[^\.]*}
    //     ^
    // No more pattern data allowed after {*...} or ** pattern element
	@GetMapping(
		value = arrayOf("/spa/**/{path:[^\\.]*}", "/spa", "/spa/"), 
	)
    fun forward(model: Model, request: HttpServletRequest): String {
        val path = request.getRequestURI()
        println("to forward: ${path}")
        return "forward:/spa/index.html"
    }

    
    // private static final String BASE_PATH = "/var/static/spa";

    // @GetMapping(value = {"/{path:^(?!api).*}", "/"})
    // public ResponseEntity<Resource> serveSpa(@PathVariable(required = false) String path) throws IOException {
    //     if (!StringUtils.hasText(path)) {
    //         path = "index.html";
    //     }

    //     File requestedFile = new File(BASE_PATH, path);
    //     if (!requestedFile.exists() || requestedFile.isDirectory()) {
    //         // fallback to index.html
    //         requestedFile = new File(BASE_PATH, "index.html");
    //         if (!requestedFile.exists()) {
    //             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //         }
    //     }

    //     String contentType = Files.probeContentType(requestedFile.toPath());
    //     if (contentType == null) {
    //         contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    //     }

    //     Resource resource = new FileSystemResource(requestedFile);

    //     return ResponseEntity.ok()
    //             .header(HttpHeaders.CONTENT_TYPE, contentType)
    //             .body(resource);
    // }
}