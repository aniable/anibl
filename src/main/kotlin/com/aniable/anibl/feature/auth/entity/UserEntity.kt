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

package com.aniable.anibl.feature.auth.entity

import com.aniable.anibl.feature.auth.dto.UserDto
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.*

object UserConstraints {

	const val USERNAME_MIN_LENGTH = 2
	const val USERNAME_MAX_LENGTH = 20
	const val PASSWORD_MIN_LENGTH = 10
}

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
data class UserEntity(
	@Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id") val id: UUID? = null,
	@Column(
		name = "username",
		unique = true,
		nullable = false,
		updatable = false,
		length = UserConstraints.USERNAME_MAX_LENGTH
	) @JvmField val username: String,
	@Column(name = "password_hash", nullable = false) val passwordHash: String,
	@Column(name = "role", nullable = false) @Enumerated(EnumType.STRING) val role: Role = Role.USER,
	@Column(name = "api_key", unique = true, nullable = false, length = 36) val apiKey: String,
	@CreatedDate var createdDate: LocalDateTime? = null,
	@LastModifiedDate var lastModifiedDate: LocalDateTime? = null,
) : UserDetails {

	override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf(role.getAuthority())

	override fun getPassword(): String = passwordHash

	override fun getUsername(): String = username
}

fun UserEntity.dto() = UserDto(
	id = this.id!!,
	username = this.username,
	role = role.name.lowercase(),
	apiKey = this.apiKey,
	createdDate = createdDate,
	lastModifiedDate = lastModifiedDate
)
