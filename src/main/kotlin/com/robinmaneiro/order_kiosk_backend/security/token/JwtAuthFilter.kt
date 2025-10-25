package com.robinmaneiro.order_kiosk_backend.security.token

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            if (jwtService.validateAccessToken(authHeader)) {
                val role = jwtService.getRoleFromClaim(authHeader)
                val authorities = when (role.uppercase()) {
                    "USER" -> listOf(SimpleGrantedAuthority("ROLE_USER"))
                    "GUEST" -> listOf(SimpleGrantedAuthority("ROLE_GUEST"))
                    else -> listOf(SimpleGrantedAuthority("ROLE_GUEST"))
                }

                val userId = jwtService.getUserIdFromToken(authHeader)

                val auth = UsernamePasswordAuthenticationToken(userId, null, authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}