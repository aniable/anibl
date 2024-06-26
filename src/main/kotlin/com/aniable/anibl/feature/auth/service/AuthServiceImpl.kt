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

package com.aniable.anibl.feature.auth.service

import com.aniable.anibl.Result
import com.aniable.anibl.config.JwtService
import com.aniable.anibl.feature.auth.AuthError
import com.aniable.anibl.feature.auth.AuthPayload
import com.aniable.anibl.feature.auth.dto.UserWithTokenDto
import com.aniable.anibl.feature.auth.entity.UserConstraints
import com.aniable.anibl.feature.auth.entity.UserEntity
import com.aniable.anibl.feature.auth.entity.dto
import com.aniable.anibl.feature.auth.repository.AuthRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthServiceImpl(
	private val authRepository: AuthRepository,
	private val passwordEncoder: PasswordEncoder,
	private val authenticationManager: AuthenticationManager,
	private val jwtService: JwtService,
) : AuthService {

	/**
	 * Generate a secure and unique ID using [UUID.randomUUID] as the base.
	 */
	@Synchronized
	private fun generateSecureId(): String {
		return UUID.randomUUID().toString()
	}

	private fun verifyAuthPayload(payload: AuthPayload.UsernamePassword): Result<Unit, AuthError> {
		if (payload.username.length !in UserConstraints.USERNAME_MIN_LENGTH..UserConstraints.USERNAME_MAX_LENGTH) {
			return Result.Failure(AuthError.InvalidUsernameLength())
		}
		if (payload.password.length < UserConstraints.PASSWORD_MIN_LENGTH) {
			return Result.Failure(AuthError.InvalidPasswordLength())
		}
		return Result.Success(Unit)
	}


	override fun register(payload: AuthPayload.UsernamePassword): Result<UserWithTokenDto, AuthError> {
		val authResult = verifyAuthPayload(payload)
		if (authResult is Result.Failure) return authResult

		return try {
			val user = authRepository.save(
				UserEntity(
					username = payload.username.lowercase(),
					passwordHash = passwordEncoder.encode(payload.password),
					apiKey = generateSecureId()
				)
			)
			val token = jwtService.generateToken(user)
			Result.Success(UserWithTokenDto(user = user.dto(), accessToken = token?.token))
		} catch (e: Exception) {
			Result.Failure(AuthError.Unknown(e.localizedMessage))
		}
	}

	override fun login(payload: AuthPayload.UsernamePassword): Result<UserWithTokenDto, AuthError> {
		try {
			authenticationManager.authenticate(UsernamePasswordAuthenticationToken(payload.username, payload.password))
		} catch (e: Exception) {
			return Result.Failure(AuthError.InvalidLogin(e.localizedMessage))
		}

		val user = authRepository.findByUsername(payload.username.lowercase())
			?: return Result.Failure(AuthError.UserDoesNotExist())
		val token = jwtService.generateToken(user)
		return Result.Success(UserWithTokenDto(user = user.dto(), accessToken = token?.token))
	}
}
