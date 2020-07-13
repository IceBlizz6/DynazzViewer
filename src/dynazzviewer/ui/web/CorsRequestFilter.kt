package dynazzviewer.ui.web

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class CorsRequestFilter(
    private val host: String
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        response.setHeader("Access-Control-Allow-Origin", host)
        response.setHeader("Access-Control-Allow-Credentials", "true")
        filterChain.doFilter(request, response)
    }
}
