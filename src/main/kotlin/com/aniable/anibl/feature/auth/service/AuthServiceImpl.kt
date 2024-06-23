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
import com.aniable.anibl.feature.auth.AuthError
import com.aniable.anibl.feature.auth.AuthPayload
import com.aniable.anibl.feature.auth.UserConstraints
import com.aniable.anibl.feature.auth.UserEntity
import com.aniable.anibl.feature.auth.repository.AuthRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthServiceImpl(
	private val authRepository: AuthRepository,
	private val logger: Logger = LoggerFactory.getLogger(AuthService::class.java),
) : AuthService {

	/**
	 * Parameters for the Argon2 password encoder.
	 * These should follow the recommendations set in [RFC 9106](https://www.rfc-editor.org/rfc/rfc9106.html#name-parameter-choice)
	 * (by default, we are using the "SECOND RECOMMENDED" option to save on memory).
	 */
	object Argon2Parameters {

		const val ARGON2_SALT_LENGTH = 32
		const val ARGON2_HASH_LENGTH = 64
		const val ARGON2_PARALLELISM = 4
		const val ARGON2_MEMORY = 65_536
		const val ARGON2_ITERATIONS = 3
	}

	/**
	 * Hash a plaintext password using Argon2. Parameters are defined in [AuthServiceImpl.Argon2Parameters].
	 *
	 * @param plainPassword
	 * @return The entire hashed password. Includes the Argon2 parameters, salt, and hashed password.
	 */
	@Synchronized
	private fun hashPassword(plainPassword: String): String {
		val encoder = Argon2PasswordEncoder(
			Argon2Parameters.ARGON2_SALT_LENGTH,
			Argon2Parameters.ARGON2_HASH_LENGTH,
			Argon2Parameters.ARGON2_PARALLELISM,
			Argon2Parameters.ARGON2_MEMORY,
			Argon2Parameters.ARGON2_ITERATIONS
		)
		val hash = encoder.encode(plainPassword)
		return hash
	}

	/**
	 * Verify that the plain-text [plainPassword] matches the [hashPassword].
	 * This can be used for authorizing a user when they attempt to log in.
	 *
	 * @param plainPassword The plain-text password to match [hashPassword] against.
	 * @param hashPassword The hashed password to match [plainPassword] against.
	 * @return True if the [plainPassword] matches the [hashPassword].
	 */
	@Synchronized
	private fun verifyPassword(plainPassword: String, hashPassword: String): Boolean {
		val encoder = Argon2PasswordEncoder(
			Argon2Parameters.ARGON2_SALT_LENGTH,
			Argon2Parameters.ARGON2_HASH_LENGTH,
			Argon2Parameters.ARGON2_PARALLELISM,
			Argon2Parameters.ARGON2_MEMORY,
			Argon2Parameters.ARGON2_ITERATIONS
		)
		return encoder.matches(plainPassword, hashPassword)
	}

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


	override fun register(payload: AuthPayload.UsernamePassword): Result<UserEntity, AuthError> {
		val authResult = verifyAuthPayload(payload)
		if (authResult is Result.Failure) return authResult

		return try {
			val user = authRepository.save(
				UserEntity(
					username = payload.username.lowercase(),
					passwordHash = hashPassword(payload.password),
					apiKey = generateSecureId()
				)
			)

			logger.info("User registered {}", user.id)
			Result.Success(user)
		} catch (e: Exception) {
			Result.Failure(AuthError.Unknown(e.localizedMessage))
		}
	}

	override fun login(payload: AuthPayload.UsernamePassword): Result<UserEntity, AuthError> {
		val foundUser = authRepository.findByUsername(payload.username.lowercase())
			?: return Result.Failure(AuthError.UserDoesNotExist())

		val foundUserPasswordHash = foundUser.passwordHash
		if (!verifyPassword(payload.password, foundUserPasswordHash)) {
			return Result.Failure(AuthError.InvalidLogin())
		}

		logger.info("User logged in {}", foundUser.id)
		return Result.Success(foundUser)
	}
}
