package idv.xcplay.sqlitebrowser.server.log

// java code
// from https://gist.github.com/michael-pratt/89eb8800be8ad47e79fe9edab8945c69

// import org.slf4j.Logger
// import java.io.IOException
// import java.io.UnsupportedEncodingException
// import java.util.Arrays
// import java.util.Collections
import java.util.Locale
import java.util.function.Consumer
// import java.util.function.Predicate
// import java.util.stream.Stream

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.jmx.export.annotation.ManagedOperation
import org.springframework.jmx.export.annotation.ManagedResource
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component
import jakarta.servlet.annotation.WebFilter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.Cookie;
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.Arrays
import java.util.Collections
//import java.util.List
import java.util.stream.Stream
import java.util.Date

// import com.xac.mpoc.util.Utility
import idv.xcplay.sqlitebrowser.server.log.ExchangeLog


// --> 2024/07/01 03:43:54.501 TID=27 method=GET, path=/rest/version, local=:443, remote=220.132.193.136:53213
//       host => xac-mpoc-gw.xac.com.tw
//       connection => Keep-Alive
//       accept-encoding => gzip
//       user-agent => okhttp/4.10.0
// <-- 2024/07/01 03:43:54.506 TID=27 status=200
// --> 2024/07/01 03:45:02.640 TID=23 method=GET, path=/rest/version, local=:443, remote=125.227.181.17:41872
//       host => xac-mpoc-gw.xac.com.tw
//       connection => Keep-Alive
//       accept-encoding => gzip
//       user-agent => okhttp/4.12.0
// <-- 2024/07/01 03:45:02.647 TID=23 status=200
// --> 2024/07/01 03:50:13.244 TID=27 method=GET, path=/rest/version, local=:443, remote=125.227.181.17:51987
//       host => xac-mpoc-gw.xac.com.tw
//       connection => keep-alive
//       cache-control => max-age=0
//       sec-ch-ua => "Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126"
//       sec-ch-ua-mobile => ?0
//       sec-ch-ua-platform => "Windows"
//       upgrade-insecure-requests => 1
//       user-agent => Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36
//       accept => text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
//       ...
// <-- 2024/07/01 03:50:13.251 TID=27 status=200
// --> 2024/07/01 03:50:34.513 TID=25 method=GET, path=/, local=:443, remote=167.94.138.39:60242
//       host => 35.229.166.187:443
// <-- 2024/07/01 03:50:34.525 TID=25 status=404
// --> 2024/07/01 03:50:42.711 TID=28 method=GET, path=/, local=:443, remote=167.94.138.39:35400
//       host => 35.229.166.187
//       user-agent => Mozilla/5.0 (compatible; CensysInspect/1.1; +https://about.censys.io/)
//       accept => */*
//       accept-encoding => gzip
// <-- 2024/07/01 03:50:42.715 TID=28 status=404

//OncePerRequestFilter()
//@ManagedResource
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestCachingFilter", urlPatterns = ["/*"])
class PacketLoggingFilter : OncePerRequestFilter() {

    @Autowired
    lateinit var exchangeLog: ExchangeLog

    private var enabled = true
    @ManagedOperation(description = "Enable logging of HTTP requests and responses")
    fun enable() {
        enabled = true
    }

    @ManagedOperation(description = "Disable logging of HTTP requests and responses")
    fun disable() {
        enabled = false
    }

    private fun printReqCookies(request: HttpServletRequest) {
        if(request.getCookies() != null){
            for ((ix, cookie: Cookie) in request.getCookies().withIndex()) {
            // 	println("the element at $ix is $arg")
                println("cookie[${ix}]=${cookie}")
            }
            // for(cookie: Cookie in request.getCookies()){
                // if(cookie.getName().equals("accessToken")){
                //     token = cookie.getValue();
                // }
            // }
        }
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        printReqCookies(request)
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response)
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain)
        }
    }

    // HttpServletRequest vs ContentCachingRequestWrapper ?
    //  - https://stackoverflow.com/questions/65363908/how-am-i-able-to-read-the-request-body-after-caching-the-httpservletrequest-obje
    @Throws(ServletException::class, IOException::class)
    protected fun doFilterWrapped(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        filterChain: FilterChain
    ) {
        val msg = StringBuilder()
        val curThread = Thread.currentThread()
        var runtimeInfo = FilterInfo()
        try {
            val info = request.getPathInfo()
            //val incomeTime = Date()
            //println(Utility.displayTime("tick1 curThread.id=" + curThread.getId(), incomeTime))
            //beforeRequest(request, response, msg)
            exchangeLog.pushRequestRecord(request, runtimeInfo)
            filterChain.doFilter(request, response)
            exchangeLog.pushRequestBody(request, runtimeInfo)
            // request body is ready after filterChain.doFilter()
        } finally {
            //val returnTime = Date()
            //afterRequest(request, response, msg)
            //println("msg.toString()="+msg.toString())
            // if (log.isInfoEnabled()) {
            //     log.info(msg.toString())
            // }
            //exchangeLog.pushResponseRecord(response, runtimeInfo)
            exchangeLog.getResponseBody(response, runtimeInfo) // body valid before copyBodyToResponse()
            response.copyBodyToResponse() // generate: Content-Type, Content-Language, Content-Length, Date, body
            exchangeLog.pushResponseRecord(response, runtimeInfo)
        }
    }

    protected fun beforeRequest(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper?,
        msg: StringBuilder
    ) {
        //if (enabled && log.isInfoEnabled()) {
            msg.append("\n-- REQUEST --\n")
            logRequestHeader(request, request.getRemoteAddr() + "|>", msg)
        //}
    }

    protected fun afterRequest(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        msg: StringBuilder
    ) {
        //if (enabled && log.isInfoEnabled()) {
            //logRequestBody(request, request.getRemoteAddr() + "|>", msg)
            //msg.append("\n-- RESPONSE --\n")
            logResponse(response, request.getRemoteAddr() + "|<", msg)
        //}
    }

    companion object {
        // private val log: Logger =
        //     LoggerFactory.getLogger(PacketLoggingFilter::class.java)

        // private val VISIBLE_TYPES: List<MediaType> = Arrays.asList(
        //     MediaType.valueOf("text/*"),
        //     MediaType.APPLICATION_FORM_URLENCODED,
        //     MediaType.APPLICATION_JSON,
        //     MediaType.APPLICATION_XML,
        //     MediaType.valueOf("application/*+json"),
        //     MediaType.valueOf("application/*+xml"),
        //     MediaType.MULTIPART_FORM_DATA
        // )

        /**
         * List of HTTP headers whose values should not be logged.
         */
        private val SENSITIVE_HEADERS: List<String> = listOf<String> (
            "authorization",
            "proxy-authorization"
        )

        private fun logRequestHeader(
            request: ContentCachingRequestWrapper,
            prefix: String,
            msg: StringBuilder
        ) {
            val queryString: String? = request.getQueryString()
            if (queryString == null) {
                msg.append(
                    java.lang.String.format(
                        "%s %s %s",
                        prefix,
                        request.getMethod(),
                        request.getRequestURI()
                    )
                ).append("\n")
            } else {
                msg.append(
                    java.lang.String.format(
                        "%s %s %s?%s",
                        prefix,
                        request.getMethod(),
                        request.getRequestURI(),
                        queryString
                    )
                ).append("\n")
            }

            // Collections.list(request.getHeaderNames())
            //     .forEach(Consumer<T> { headerName: T ->
            //         Collections.list(request.getHeaders(headerName))
            //             .forEach(Consumer<T> { headerValue: T? ->
            //                 if (isSensitiveHeader(headerName)) {
            //                     msg.append(
            //                         String.format(
            //                             "%s %s: %s",
            //                             prefix,
            //                             headerName,
            //                             "*******"
            //                         )
            //                     ).append("\n")
            //                 } else {
            //                     msg.append(
            //                         String.format(
            //                             "%s %s: %s",
            //                             prefix,
            //                             headerName,
            //                             headerValue
            //                         )
            //                     ).append("\n")
            //                 }
            //             })
            //     })
            msg.append(prefix).append("\n")
        }

        private fun logRequestBody(
            request: ContentCachingRequestWrapper,
            prefix: String,
            msg: StringBuilder
        ) {
            val content: ByteArray = request.getContentAsByteArray()
            if (content.size > 0) {
                logContent(
                    content,
                    request.getContentType(),
                    request.getCharacterEncoding(),
                    prefix,
                    msg
                )
            }
        }

        private fun logResponse(
            response: ContentCachingResponseWrapper,
            prefix: String,
            msg: StringBuilder
        ) {
            val status: Int = response.getStatus()
            msg.append(
                java.lang.String.format(
                    "%s %s %s",
                    prefix,
                    status,
                    HttpStatus.valueOf(status).getReasonPhrase()
                )
            ).append("\n")
            response.getHeaderNames()
                .forEach { headerName ->
                    response.getHeaders(headerName)
                        .forEach { headerValue ->
                            if (isSensitiveHeader(headerName)) {
                                msg.append(
                                    java.lang.String.format(
                                        "%s %s: %s",
                                        prefix,
                                        headerName,
                                        "*******"
                                    )
                                ).append("\n")
                            } else {
                                msg.append(
                                    java.lang.String.format(
                                        "%s %s: %s",
                                        prefix,
                                        headerName,
                                        headerValue
                                    )
                                ).append("\n")
                            }
                        }
                }
            msg.append(prefix).append("\n")
            val content: ByteArray = response.getContentAsByteArray()
            if (content.size > 0) {
                logContent(
                    content,
                    response.getContentType(),
                    response.getCharacterEncoding(),
                    prefix,
                    msg
                )
            }
        }

        // @Suppress("UNUSED_PARAMETER")
        private fun logContent(
            @Suppress("UNUSED_PARAMETER")content: ByteArray,
            @Suppress("UNUSED_PARAMETER")contentType: String,
            @Suppress("UNUSED_PARAMETER")contentEncoding: String,
            @Suppress("UNUSED_PARAMETER")prefix: String,
            @Suppress("UNUSED_PARAMETER")msg: StringBuilder
        ) {
            //val mediaType: MediaType = MediaType.valueOf(contentType)
            // val visible = VISIBLE_TYPES.stream().anyMatch(
            //     Predicate<MediaType> { visibleType: MediaType -> visibleType.includes(mediaType) })
            // if (visible) {
            //     try {
            //         val contentString = kotlin.String(content, contentEncoding)
            //         Stream.of(
            //             *contentString.split("\r\n|\r|\n".toRegex()).dropLastWhile { it.isEmpty() }
            //                 .toTypedArray()).forEach { line: String? ->
            //             msg.append(prefix).append(" ").append(line).append("\n")
            //         }
            //     } catch (e: UnsupportedEncodingException) {
            //         msg.append(String.format("%s [%d bytes content]", prefix, content.size))
            //             .append("\n")
            //     }
            // } else {
            //     msg.append(String.format("%s [%d bytes content]", prefix, content.size))
            //         .append("\n")
            // }
        }

        /**
         * Determine if a given header name should have its value logged.
         * @param headerName HTTP header name.
         * @return True if the header is sensitive (i.e. its value should **not** be logged).
         */
        private fun isSensitiveHeader(headerName: String): Boolean {
            return SENSITIVE_HEADERS.contains(headerName.lowercase(Locale.getDefault()))
        }

        private fun wrapRequest(request: HttpServletRequest): ContentCachingRequestWrapper {
            return if (request is ContentCachingRequestWrapper) {
                //request as ContentCachingRequestWrapper
                return request
            } else {
                ContentCachingRequestWrapper(request)
            }
        }

        private fun wrapResponse(response: HttpServletResponse): ContentCachingResponseWrapper {
            return if (response is ContentCachingResponseWrapper) {
                //response as ContentCachingResponseWrapper
                return response
            } else {
                ContentCachingResponseWrapper(response)
            }
        }
    }
}