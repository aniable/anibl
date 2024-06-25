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

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
@RequestMapping("/api/test")
class TestController {

	@GetMapping("/user")
	fun userTest(): ResponseEntity<String> {
		val securityContext = SecurityContextHolder.getContext().authentication
		val name = securityContext.name
		val authorities = securityContext.authorities

		return ResponseEntity.ok("Authenticated as a USER!\nname=$name\nauthorities=$authorities")
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/admin")
	fun adminTest(): ResponseEntity<String> {
		val securityContext = SecurityContextHolder.getContext().authentication
		val name = securityContext.name
		val authorities = securityContext.authorities

		return ResponseEntity.ok("Authenticated as a ADMIN!\nname=$name\nauthorities=$authorities")
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/manager")
	fun managerTest(): ResponseEntity<String> {
		val securityContext = SecurityContextHolder.getContext().authentication
		val name = securityContext.name
		val authorities = securityContext.authorities

		return ResponseEntity.ok("Authenticated as a MANAGER!\nname=$name\nauthorities=$authorities")
	}
}
