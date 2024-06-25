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

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
	@Value("\${security.jwt.secret-key}") private val secretKey: String,
	@Value("\${security.jwt.expiration-time}") private val expiration: Long,
) {

	data class IssuedToken(val token: String, val issuedAt: Date, val expiresAt: Date)

	private fun getSecretSigningKey(): SecretKey {
		val key = Decoders.BASE64.decode(secretKey)
		return Keys.hmacShaKeyFor(key)
	}

	private fun extractAllClaims(token: String): Claims {
		return Jwts.parser().verifyWith(getSecretSigningKey()).build().parseSignedClaims(token).payload;
	}

	private fun <T : Any> extractClaim(token: String, resolver: (Claims) -> T): T {
		val claims = extractAllClaims(token)
		return resolver(claims)
	}

	fun extractSubject(token: String): String? {
		return extractClaim(token, Claims::getSubject)
	}

	fun extractExpiration(token: String): Date? {
		return extractClaim(token, Claims::getExpiration)
	}

	fun generateToken(userDetails: UserDetails): IssuedToken? {
		val currentMs = System.currentTimeMillis()
		val issuedAt = Date(currentMs)
		val expiresAt = Date(currentMs + expiration)
		val token = Jwts.builder().subject(userDetails.username).issuedAt(issuedAt).expiration(expiresAt)
			.signWith(getSecretSigningKey()).compact()
		return IssuedToken(token = token, issuedAt = issuedAt, expiresAt = expiresAt)
	}

	fun isExpired(token: String): Boolean {
		return extractExpiration(token)?.before(Date(System.currentTimeMillis())) ?: false
	}

	fun isValid(token: String, userDetails: UserDetails): Boolean {
		val subject = extractSubject(token)
		return subject.equals(userDetails.username) && !isExpired(token)
	}
}
