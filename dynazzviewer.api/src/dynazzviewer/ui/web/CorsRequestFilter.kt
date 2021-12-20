package dynazzviewer.ui.web

import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class CorsRequestFilter(
    private val hosts: List<String>
) : OncePerRequestFilter() {
    val apiEndpoint = "/graphql"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val origin = request.getHeader("Origin")
        if (hosts.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin)
        }
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Headers", "content-type")
        if (apiEndpoint == request.requestURI) {
            filterChain.doFilter(request, CustomHttpServletResponseWrapper(response))
        } else {
            filterChain.doFilter(request, response)
        }
    }

    class CustomHttpServletResponseWrapper(
        response: HttpServletResponse?
    ) : HttpServletResponseWrapper(response) {
        override fun addCookie(cookie: Cookie) {
            if ("JSESSIONID" == cookie.name) {
                super.addHeader("Set-Cookie", getCookieValue(cookie))
            } else {
                super.addCookie(cookie)
            }
        }

        private fun getCookieValue(cookie: Cookie): String {
            val builder = StringBuilder()
            builder.append(cookie.name).append('=').append(cookie.value)
            builder.append(";Path=").append(cookie.path)
            if (cookie.isHttpOnly) {
                builder.append(";HttpOnly")
            }
            if (cookie.secure) {
                builder.append(";Secure")
            }
            builder.append(";SameSite=None")
            return builder.toString()
        }
    }
}
