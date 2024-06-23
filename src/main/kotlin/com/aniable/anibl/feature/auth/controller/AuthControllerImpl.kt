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

package com.aniable.anibl.feature.auth.controller

import com.aniable.anibl.Result
import com.aniable.anibl.feature.auth.AuthPayload
import com.aniable.anibl.feature.auth.dto
import com.aniable.anibl.feature.auth.dto.UserDto
import com.aniable.anibl.feature.auth.handle
import com.aniable.anibl.feature.auth.service.AuthServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
@RequestMapping("/api/v1/auth")
class AuthControllerImpl(private val authServiceImpl: AuthServiceImpl) : AuthController {

	@PostMapping("/register")
	override fun register(@RequestBody payload: AuthPayload.UsernamePassword): ResponseEntity<String> {
		when (val response = authServiceImpl.register(payload)) {
			is Result.Failure -> response.error.handle()
			is Result.Success -> return ResponseEntity.ok(response.data.toString())
		}
	}

	@PostMapping("/login")
	override fun login(@RequestBody payload: AuthPayload.UsernamePassword): ResponseEntity<UserDto> {
		when (val response = authServiceImpl.login(payload)) {
			is Result.Failure -> response.error.handle()
			is Result.Success -> {
				val userDto = response.data.dto()
				return ResponseEntity.ok(userDto)
			}
		}
	}
}
