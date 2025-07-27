// https://github.com/couchbaselabs/try-cb-kotlin/blob/master/src/main/kotlin/trycb/config/Request.kt (X)
// https://www.baeldung.com/spring-http-logging

package idv.xcplay.sqlitebrowser.server.log

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.filter.CorsFilter

import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Queue
import java.util.LinkedList
import java.util.Date

import java.util.TimeZone
//import java.util.Calendar
import java.text.SimpleDateFormat

//import com.xac.mpoc.util.Utility

// try ??
//  - https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-mvc-3/src/main/java/com/baeldung/filtersinterceptors/LogFilter.java
//  - https://github.com/eugenp/tutorials/blob/master/spring-boot-modules/spring-boot-mvc-3/src/main/java/com/baeldung/filtersinterceptors/LogInterceptor.java


//@ControllerAdvice
@Component
class ExchangeLog {

    // request: ContentCachingRequestWrapper,
    // response: ContentCachingResponseWrapper,

    // @Value("\${app.log.timezone:Asia/Taipei}")
    // val strTimeZone: String = "Asia/Taipei"
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val Log = LoggerFactory.getLogger(javaClass.enclosingClass)
        
        val logDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSX");
    }
    //val logDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSX");

    var recentMax: Int = 0;
    val headerMax = 8;

    // init {
    //     //logDateFormat.setTimeZone(TimeZone.getTimeZone(strTimeZone));
    //     println("First initializer block that prints")
    // }
    // constructor() {
    //     // some code
    //     println("ExchangeLog ctor()")
    //     recentMax = 127
    // }

    data class BodyLog (
        val filterOut: String,
        val filterOpt: Int,
    )

    constructor (
        @Value("\${app.log.timezone:Asia/Taipei}") strTimeZone: String,  // may be from logging.pattern.dateformat
        @Value("\${app.log.recentMax:127}") argRecentMax: Int,
    ){
        recentMax = argRecentMax
        if (argRecentMax < 11) {
            recentMax = 11
        }
        if (argRecentMax > 255) {
            recentMax = 255
        }
        //println("!!! ExchangeLog ctor() strTimeZone=${strTimeZone} recentMax=${recentMax}")
        logDateFormat.setTimeZone(TimeZone.getTimeZone(strTimeZone))
    }

    var recentLog: Queue<String> = LinkedList();

    
    // full record for whitelist
    val fullWhiteList: Array<String> = arrayOf(
        "/mpoc_debug_api/",
        "/mpoc_cots_api/",
        "/rest/",
    )
    val laterBlackList: Array<BodyLog> = arrayOf(
        BodyLog("/mpoc_debug_api/v1/logs", 0),
        BodyLog("/mpoc_debug_api/v1/recent_log", 0),
        BodyLog("/rest/test", 1)
    )

    //val ignoreCase = true // default, for contain() 2nd arg
    val whiteHeaderReqList: Array<String> = arrayOf(
        "host",
        "user-agent",
        "content-type",
        "accept",
    )
    val whiteHeaderRspList: Array<String> = arrayOf(
        "content-type"
    )

    // https://vkuzel.com/log-requests-and-responses-including-body-in-spring-boot ??
    
    /**
     * @param runtimeInfo.body valid when runIdx=1
     * @param runIdx           0, 1
     */
    fun pushRequestRecord(request: ContentCachingRequestWrapper, runtimeInfo: FilterInfo) {

        val path = request.getRequestURI()

        for (white in fullWhiteList) {
            if (path.startsWith(white)) {
                runtimeInfo.headerSkip = false

                // for (black in laterBlackList) {
                //     if (path.startsWith(black)) {
                //         runtimeInfo.bodyPrint = false
                //     }
                // }
                for (cfg in laterBlackList) {
                    if (path.startsWith(cfg.filterOut)) {
                        runtimeInfo.bodyPrint = false
                        if (cfg.filterOpt == 1) {
                            runtimeInfo.bodyPrint = true
                        }
                        runtimeInfo.filterOpt = cfg.filterOpt
                    }
                }

                break
            }
        }

        val curThread = Thread.currentThread()
        //println(Utility.displayTime("tick1 curThread.id=" + curThread.getId()

        var sb: StringBuilder = StringBuilder()

        //val names: java.util.Enumeration<String> = request.getHeaderNames()
        //val list = names.toList()
        // for (name in list) {
        //     //println("   $name")
        //     println("    $name => " + request.getHeader(name))
        // }


        // headers
        //  Accept-encoding => gzip
        //  Connection => Keep-Alive
        //  Host => cht-gw.xac.com.tw
        //  User-agent => cpocAgent
        //  Content-type => application/json; charset=utf-8
        //  Content-length => 433
        

        
        // sb.append("--> ")
        //     .append(getLogTimeFormat() + " TID="+curThread.getId())

        // method=post, path=/cpoc_cots_api/v2/trans/login_service, protocol=HTTP/1.1, local=192.168.41.1:443, remote=125.227.181.17:37512
        sb.append("method="+request.getMethod())
        .append(", path="+path)
        .append(", dataSize="+request.getContentLength())
        // .append(", local="+request.getLocalAddr() + ":" + request.getLocalPort())
        .append(", local=:" + request.getLocalPort())
        .append(", remote="+ maskRemoteAddr(request.getRemoteAddr()) + ":" + request.getRemotePort())
        //.append("\n")
        
        if (!runtimeInfo.headerSkip) {
            val names: java.util.Enumeration<String> = request.getHeaderNames()
            val list = names.toList()
            var ix = 0;
            var meetCount = 0;
            for (name in list) {
                //println("   $name")
                if (ix > headerMax || meetCount >= whiteHeaderReqList.size) {
                    //sb.append("\n      ...")
                    break
                }
                ix += 1;
                if (whiteHeaderReqList.contains(name)) {
                    meetCount += 1;
                    sb.append("\n      " + name + " => " + request.getHeader(name))
                }
            }
        }
        //Log.info(sb.toString());

        //synchronized(ExchangeLog.Companion) {
        synchronized(this) {
            //return recentLog.toString()
            if (recentLog.size > recentMax) {
                recentLog.remove()
            }

            recentLog.add("--> " + getLogTimeFormat() + " TID="+curThread.getId() + " " + sb.toString() + "\n")
            Log.info("--> " + sb.toString())
        }
    }

    /**
     * @param runtimeInfo.body valid when runIdx=0
     * @param runIdx           0, 1
     */
    fun pushRequestBody(request: ContentCachingRequestWrapper, runtimeInfo: FilterInfo) {
        if (runtimeInfo.headerSkip || runtimeInfo.bodyPrint == false) return

        val reqString: String = request.getContentAsString()
        if (reqString.length == 0) return
        val curThread = Thread.currentThread()
        //Log.info("Request(ExchangeLog) body: "+ reqString)
        //println("reqString.length=${reqString.length}")

        synchronized(this) {
            //return recentLog.toString()
            if (recentLog.size > recentMax) {
                recentLog.remove()
            }

            recentLog.add("--> TID="+curThread.getId() + " body=" + reqString + "\n")
            //recentLog.add("--> TID="+curThread.getId() + "\n")
            Log.info("--> " + reqString)
        }
    }

    fun getResponseBody(response: ContentCachingResponseWrapper, info: FilterInfo) {
        if (info.headerSkip == false && info.bodyPrint ) {
            //if (rspContentType != null && rspContentType.startsWith("application/json")) {
                //sb.append("")
                //sb.append(String(response.getContentAsByteArray()));
                //Log.info("Response {}", response.getContentAsByteArray())
                info.body = String(response.getContentAsByteArray())
                //info.body = response.getContentAsString()
            //}
        }
    }

    //fun pushResponseRecord(response: HttpServletResponse) {
    fun pushResponseRecord(response: ContentCachingResponseWrapper, info: FilterInfo) {
        
        val curThread = Thread.currentThread()
        var sb: StringBuilder = StringBuilder()

        val status: Int = response.getStatus()
        // Log.info("response.getContentSize()="+ response.getContentSize()); // no work

        
        // sb.append("<-- ")
        //     .append(getLogTimeFormat() + " TID="+curThread.getId())
        sb.append("status=" + status)
        if (info.body != null) {
            // var game: String = "Jack"
            // sb.append(", dataSize=" + game.length)
            // if (info.bodyAltPrintSize == true) {
            if (info.filterOpt == 1) {
                sb.append(", dataSize=" + info.body!!.length)
            } else {
                sb.append(", body=").append(info.body)
            }
            // }
            
        }

        //println(sb.toString())

        //val names = request.getHeader("Content-Length"); 
        // var names = response.getHeaderNames()
        // val list = names.toList()
        // for (name in list) {
        //     // seem not final step, so can't get some like "content-type"
        //     println("    $name => " + response.getHeader(name))
        // }

        //println("FilterInfo.body=${info.body}")

        

        //synchronized(ExchangeLog.Companion) {
        synchronized(this) {
            //return recentLog.toString()
            if (recentLog.size > recentMax) {
                recentLog.remove()
            }

            recentLog.add("<-- " + getLogTimeFormat() + " TID="+curThread.getId() + " " + sb.toString() + "\n")
            //recentLog.add("<-- " + getLogTimeFormat() + " TID="+curThread.getId() + "\n")
            Log.info("<-- " + sb.toString())
        }
    }
    fun getRecentLog(): String {
        var sb: StringBuilder = StringBuilder()
        //synchronized(ExchangeLog.Companion) {
        synchronized(this) {
            for (record in recentLog) {
                //println("   $name")
                sb.append(record)
            }
            //return recentLog.toString()
        }
        return sb.toString()
    }

    
    fun getLogTimeFormat(): String {
        return logDateFormat.format(Date())
    }
    fun getLogTimeFormat(date: Date): String {
        return logDateFormat.format(date)
    }
}


val regex = "\\.\\d+\\.\\d+\\.".toRegex()
/**
    IPv4 - remote=120.77.151.93 => 120.*.*.93
    IPv6 - remote=0:0:0:0:0:0:0:1
 */
fun maskRemoteAddr(addr: String): String {
    val ret = regex.replace(addr, ".*.*.")
    //println("mask (${addr}) => (${ret})")
    return ret
}


data class FilterInfo(
    var headerSkip: Boolean = true,   // true for simplfied request and no response
    var bodyPrint: Boolean = true,
    var body: String? = null,
    var filterOpt: Int = 0
)
