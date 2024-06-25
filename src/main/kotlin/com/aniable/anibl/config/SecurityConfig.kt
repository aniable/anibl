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

import com.aniable.anibl.feature.auth.controller.AuthController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Suppress("unused")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

	@Bean
	fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
		val permittedMatchers = arrayOf("/h2-console/**", "/error/**", AuthController.REQUEST_MATCHER)
		return httpSecurity.csrf { it.disable() }.headers { headers -> headers.frameOptions { it.disable() } }
			.authorizeHttpRequests { it.requestMatchers(*permittedMatchers).permitAll().anyRequest().authenticated() }
			.httpBasic(Customizer.withDefaults()).build()
	}
}
