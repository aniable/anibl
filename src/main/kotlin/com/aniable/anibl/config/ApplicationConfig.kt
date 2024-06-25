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

import com.aniable.anibl.error.HttpException
import com.aniable.anibl.feature.auth.AuthError
import com.aniable.anibl.feature.auth.entity.Role
import com.aniable.anibl.feature.auth.repository.AuthRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfig(private val authRepository: AuthRepository) {

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
		return Argon2PasswordEncoder(
			Argon2Parameters.ARGON2_SALT_LENGTH,
			Argon2Parameters.ARGON2_HASH_LENGTH,
			Argon2Parameters.ARGON2_PARALLELISM,
			Argon2Parameters.ARGON2_MEMORY,
			Argon2Parameters.ARGON2_ITERATIONS
		)
	}

	@Bean
	fun userDetailsService(): UserDetailsService {
		return UserDetailsService { username: String? ->
			username?.let {
				authRepository.findByUsername(it)
			} ?: throw HttpException.NotFound(AuthError.UserDoesNotExist().message)
		}
	}

	@Bean
	fun authenticationProvider(): AuthenticationProvider {
		val provider = DaoAuthenticationProvider()
		provider.setUserDetailsService(userDetailsService())
		provider.setPasswordEncoder(passwordEncoder())
		return provider
	}

	@Bean
	fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
		return authenticationConfiguration.authenticationManager
	}

	@Bean
	fun roleHierarchy(): RoleHierarchy {
		return RoleHierarchyImpl.withDefaultRolePrefix().role(Role.MANAGER.name).implies(Role.ADMIN.name)
			.role(Role.ADMIN.name).implies(Role.USER.name).build()
	}
}
