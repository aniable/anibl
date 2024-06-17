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

import com.aniable.anibl.error.HttpException
import com.aniable.anibl.feature.auth.AuthPayload
import com.aniable.anibl.feature.auth.User
import com.aniable.anibl.feature.auth.UserConstraints
import com.aniable.anibl.feature.auth.Users
import com.aniable.anibl.feature.auth.dto.UserDto
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLIntegrityConstraintViolationException
import java.util.*

@Service
@Transactional
class AuthServiceImpl : AuthService {

	/**
	 * Parameters for the Argon2 password encoder.
	 * These should follow the recommendations set in [RFC 9106](https://www.rfc-editor.org/rfc/rfc9106.html#name-parameter-choice)
	 * (by default we are using the "SECOND RECOMMENDED" option to save on memory).
	 */
	object Argon2Parameters {

		const val ARGON2_SALT_LENGTH = 32
		const val ARGON2_HASH_LENGTH = 64
		const val ARGON2_PARALLELISM = 4
		const val ARGON2_MEMORY = 65_536
		const val ARGON2_ITERATIONS = 3
	}

	private fun verifyAuthPayload(payload: AuthPayload.UsernamePassword) {
		if (payload.username.length !in UserConstraints.USERNAME_MIN_LENGTH..UserConstraints.USERNAME_MAX_LENGTH) {
			throw HttpException.BadRequest("Username must be between ${UserConstraints.USERNAME_MIN_LENGTH} and ${UserConstraints.USERNAME_MAX_LENGTH} characters.")
		}

		if (payload.password.length < UserConstraints.PASSWORD_MIN_LENGTH) {
			throw HttpException.BadRequest("Password must be a minimum of ${UserConstraints.PASSWORD_MIN_LENGTH} characters.")
		}
	}

	/**
	 * Generate a secure and unique ID using [UUID.randomUUID] as the base.
	 */
	@Synchronized
	private fun generateSecureId(): String {
		return UUID.randomUUID().toString().replace("-", "")
	}

	/**
	 * Hash a plaintext password using Argon2. Parameters are defined in [AuthServiceImpl.Argon2Parameters].
	 *
	 * @param plainPassword
	 * @return [Pair] The first value of the pair is the salt used, the second value is the hashed password.
	 */
	@Synchronized
	private fun hashPassword(plainPassword: String): Pair<String, String> {
		val encoder = Argon2PasswordEncoder(
			Argon2Parameters.ARGON2_SALT_LENGTH,
			Argon2Parameters.ARGON2_HASH_LENGTH,
			Argon2Parameters.ARGON2_PARALLELISM,
			Argon2Parameters.ARGON2_MEMORY,
			Argon2Parameters.ARGON2_ITERATIONS
		)
		val hash = encoder.encode(plainPassword)
		val salt = hash.split("$").map { it.trim() }.drop(1)[3]
		return Pair(salt, hash)
	}

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

	override fun register(payload: AuthPayload.UsernamePassword): EntityID<UUID> {
		verifyAuthPayload(payload)

		return try {
			val hashedPassword = hashPassword(payload.password)

			Users.insertAndGetId {
				it[username] = payload.username.lowercase()
				it[passwordSalt] = hashedPassword.first
				it[passwordHash] = hashedPassword.second
				it[apiKey] = generateSecureId()
			}
		} catch (e: ExposedSQLException) {
			when (val cause = e.cause) {
				is SQLIntegrityConstraintViolationException -> throw HttpException.Conflict("Username already exists.")
				else -> throw HttpException.InternalServerError(cause?.localizedMessage)
			}
		} catch (e: Exception) {
			throw HttpException.InternalServerError(e.localizedMessage)
		}
	}

	// TODO: Session tokens
	override fun login(payload: AuthPayload.UsernamePassword): UserDto {
		val foundUser = User.find { Users.username eq payload.username.lowercase() }.firstOrNull()
		if (foundUser == null) throw HttpException.NotFound("User not found.")

		val foundUserPasswordHash = foundUser.passwordHash
		if (!verifyPassword(
				payload.password, foundUserPasswordHash
			)
		) throw HttpException.Unauthorized("Incorrect credentials.")

		return UserDto(username = foundUser.username, apiKey = foundUser.apiKey, createdAt = foundUser.createdAt)
	}
}
