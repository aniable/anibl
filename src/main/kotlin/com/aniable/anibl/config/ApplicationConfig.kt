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

@file:Suppress("unused")

package com.aniable.anibl.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfig(private val logger: Logger = LoggerFactory.getLogger(ApplicationConfig::class.java)) {

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
	 * The default password encoder using [Argon2PasswordEncoder] and parameters from [Argon2Parameters].
	 *
	 * To encode a plain-text password, you can use:
	 * ```kotlin
	 * val passwordEncoder: PasswordEncoder // Injected into the class constructor
	 * passwordEncoder.encode("password1234")
	 * ```
	 *
	 * To verify a plain-text password matches a hash, use:
	 * ```kotlin
	 * val passwordEncoder: PasswordEncoder // Injected into the class constructor
	 * val hashedPassword = "" // Obtained from the database
	 * passwordEncoder.matches("password1234", hashedPassword)
	 * ```
	 */
	@Bean
	fun passwordEncoder(): PasswordEncoder {
		logger.info("passwordEncoder {}", Argon2Parameters.toString())
		return Argon2PasswordEncoder(
			Argon2Parameters.ARGON2_SALT_LENGTH,
			Argon2Parameters.ARGON2_HASH_LENGTH,
			Argon2Parameters.ARGON2_PARALLELISM,
			Argon2Parameters.ARGON2_MEMORY,
			Argon2Parameters.ARGON2_ITERATIONS
		)
	}
}
