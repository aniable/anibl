/*
 * Anibl
 * Copyright (C) 2024 Aniable LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.aniable.anibl.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Suppress("unused")
@Component
class JwtAuthenticationFilter(
	private val jwtService: JwtService,
	private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

	companion object {

		private const val AUTH_HEADER = "Authorization"
		private const val AUTH_SCHEME = "Bearer\u0020"
	}

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val authHeader = request.getHeader(AUTH_HEADER)
		if (authHeader == null || !authHeader.startsWith(AUTH_SCHEME)) {
			filterChain.doFilter(request, response)
			return
		}

		val authToken = authHeader.substring(AUTH_SCHEME.length)
		val subject = jwtService.extractSubject(authToken)

		if (subject != null && SecurityContextHolder.getContext().authentication == null) {
			val userEntity = userDetailsService.loadUserByUsername(subject) ?: return
			val isTokenValid = jwtService.isValid(authToken, userEntity)

			if (isTokenValid) {
				val usernamePasswordAuthenticationToken =
					UsernamePasswordAuthenticationToken(userEntity, null, userEntity.authorities)
				usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
				SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
			}
		}

		filterChain.doFilter(request, response)
	}
}
